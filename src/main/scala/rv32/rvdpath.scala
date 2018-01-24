//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// 1-stage data path
//=======================================================================
package rvcore

import chisel3._
import chisel3.util._
import rvcommon._

class d2c_io extends Bundle {
    //val pc = Output(UInt(rvspec.xlen.W))
    val inst = Output(UInt(rvspec.xlen.W))
    
    val dexcp = Output(Bool()) //Make control path change pc selection
    //val dcause = Output(UInt(rvcause.X.getWidth.W)) //not pass cause to cpath, direct to csr
}

class dpath_io extends Bundle {
    val imem = new mram_io(rvspec.xlen)
    val dmem = new mram_io(rvspec.xlen)
    val d2c = new d2c_io()
    val c2d = Flipped(new c2d_io())
    
    val ddpath = Flipped(new ddpath_io())

    val dbg_sel_unit = Input(Bool())

    //RegFile debug/test path, temporary used for no LUI/ADDI design
    val rfdbg = Flipped(new mram_io(rvspec.xlen))
}

class rvdpath extends Module {
    
    val io = IO(new dpath_io)
    //DontCare for Initialized Error
    io <> DontCare

    //Conventional RISCV data-flow path

    //#Part1. PC to Instruction Memory
    val pc = Reg(UInt(rvspec.xlen.W))

    val pc_next = Wire(UInt(rvspec.xlen.W))
    val pc_a4 = Wire(UInt(rvspec.xlen.W))
    val pc_br = Wire(UInt(rvspec.xlen.W))
    val pc_jp = Wire(UInt(rvspec.xlen.W))
    val pc_ex = Wire(UInt(rvspec.xlen.W))

    pc_a4 := pc + UInt(4, rvspec.xlen)
    
    pc_next := MuxLookup(
        io.c2d.pc_sel,
        pc_a4,
        Array(
            rvdp.pc_sel.A4 -> pc_a4,
            rvdp.pc_sel.BR -> pc_br,
            rvdp.pc_sel.JP -> pc_jp,
            rvdp.pc_sel.EX -> pc_ex
        )
    )

    //#Part2. Fetch Instruction Memory

    io.imem.req.addr := pc
    io.imem.req.data := 0.U
    io.imem.req.mfunc := mram_op.MF_RD
    io.imem.req.mtype := mram_op.MT_W
    io.imem.req.valid := true.B

    when(io.imem.resp.valid) {
        pc := pc_next
    }

    //Ref: https://github.com/freechipsproject/chisel3/wiki/Muxes-and-Input-Selection
    val inst = Mux(io.imem.resp.valid, io.imem.resp.data, UInt(0x00, rvspec.xlen))
    io.d2c.inst := inst
    
    //Debug, Check again if instruction send to cpath correctly
    //printf("dpath inst: [0x%x]<=(%b)?0x%x:0x00\n", inst, io.imem.resp.valid, io.imem.resp.data)

    //#Part3. Instruction to RegFile
    //Ref: https://github.com/freechipsproject/chisel3/wiki/Cookbook#how-do-i-create-a-vector-of-registers
    val regfile = Reg(Vec(rvspec.xrsz, UInt(rvspec.xlen.W)))

    //Reg Data
    val rs1_addr = inst(rvinst.rs1bh, rvinst.rs1bl)
    val rs2_addr = inst(rvinst.rs2bh, rvinst.rs2bl)
    val rs1_data = Mux((rs1_addr!=0.U), regfile(rs1_addr), UInt(0, rvspec.xlen))
    val rs2_data = Mux((rs2_addr!=0.U), regfile(rs2_addr), UInt(0, rvspec.xlen))
    
    //Imm Data
    val i_sext = Cat(Fill(20,inst(31)), inst(31, 20)).toUInt
    val s_sext = Cat(Fill(20,inst(31)), inst(31, 25), inst(11,7)).toUInt
    val b_sext = Cat(Fill(19,inst(31)), inst(31), inst(7), inst(30,25), inst(11,8), Fill(1,UInt(0,1))).toUInt
    val u_sext = Cat(inst(31, 12), Fill(12,UInt(0,1))).toUInt
    val j_sext = Cat(Fill(11,inst(31)), inst(31), inst(19,12), inst(20), inst(30,21), Fill(1,UInt(0,1))).toUInt
    val z_imm = Cat(Fill(27,UInt(0,1)), inst(19,15))
    
    //Data-path Mux for Op data
    val alu_opd1 = MuxLookup(
        io.c2d.op1_sel, //switch(io.c2d.op1_sel)
        0.U,  //default: 0.U
        Array(
            rvdp.op1_sel.R -> rs1_data,
            rvdp.op1_sel.P -> pc,
            rvdp.op1_sel.I -> z_imm
        )
    )
    val alu_opd2 = MuxLookup(
        io.c2d.op2_sel, //switch(io.c2d.op2_sel)
        0.U,  //default: 0.U
        Array(
            rvdp.op2_sel.R -> rs2_data,
            rvdp.op2_sel.I -> i_sext,
            rvdp.op2_sel.S -> s_sext,
            rvdp.op2_sel.B -> b_sext,
            rvdp.op2_sel.U -> u_sext,
            rvdp.op2_sel.J -> j_sext
        )
    )

    //Branch Conditions
    val bcmp_eq = Wire(Bool())
    val bcmp_lt = Wire(Bool())
    val bcmp_ltu = Wire(Bool())
    
    bcmp_eq := (rs1_data === rs2_data)
    bcmp_lt := (rs1_data.asSInt < rs2_data.asSInt)
    bcmp_ltu := (rs1_data.asUInt < rs2_data.asUInt)

    //#Part4. RegFile to ALU

    val alu_func = io.c2d.alu_func
    //Ref: https://github.com/freechipsproject/chisel3/wiki/Muxes-and-Input-Selection
    val alu_out = MuxLookup(
        alu_func,             //switch(alu_func) 
        UInt(0, rvspec.xlen), //default: UInt(0, xlen=32)
        Array(
            //Use ordering from up to down in SPEC Vol.I Base Instruction Set, Page 54
            rvalu.ADD   -> (alu_opd1 + alu_opd2).asUInt,
            rvalu.SUB   -> (alu_opd1 - alu_opd2).asUInt,
            //Shift left makes internal process length = w(32) + y(max:32) = 64
            //Ref: https://github.com/freechipsproject/chisel3/wiki/Builtin-Operators
            //Statement: z = x << n	w(z) = w(x) + maxNum(n)
            rvalu.SLL   -> (alu_opd1 << alu_opd2(rvinst.shamtsz-1,0)).asUInt,
            rvalu.SLT   -> (alu_opd1.asSInt < alu_opd2.asSInt).asUInt,
            rvalu.SLTU  -> (alu_opd1 < alu_opd2).asUInt,
            rvalu.XOR   -> (alu_opd1 ^ alu_opd2).asUInt,
            rvalu.SRL   -> (alu_opd1 >> alu_opd2(rvinst.shamtsz-1,0)).asUInt,
            rvalu.SRA   -> (alu_opd1.asSInt >> alu_opd2(rvinst.shamtsz-1,0)).asUInt,
            rvalu.OR    -> (alu_opd1 | alu_opd2).asUInt,
            rvalu.AND   -> (alu_opd1 & alu_opd2).asUInt,
            rvalu.CP1   -> alu_opd1.asUInt,
            rvalu.CP2   -> alu_opd2.asUInt
        )
    )(rvspec.xlen-1,0) //Force ALU output fits to datapath width

    //Branch to next pc
    val br_next = alu_out
    pc_br := MuxLookup(
        io.c2d.br_sel,
        pc_a4,
        Array(
            rvdp.br_sel.BEQ -> Mux(bcmp_eq, br_next, pc_a4),
            rvdp.br_sel.BNE -> Mux(!bcmp_eq, br_next, pc_a4),
            rvdp.br_sel.BLT -> Mux(bcmp_lt, br_next, pc_a4),
            rvdp.br_sel.BGE -> Mux(!bcmp_lt, br_next, pc_a4),
            rvdp.br_sel.BLTU -> Mux(bcmp_ltu, br_next, pc_a4),
            rvdp.br_sel.BGEU -> Mux(!bcmp_ltu, br_next, pc_a4)
        )
    )

    //Jump function (JAL/JALR), use ALU to calculate offset with PC
    pc_jp := alu_out

    //#Part5. ALU to Write Back

    //Add a path for CSR data write-back
    val csr_rdata = Wire(UInt(rvspec.xlen.W))

    io.dmem.req.addr := alu_out
    io.dmem.req.data := rs2_data
    //io.dmem.req.valid := true.B
    io.dmem.req.valid := (io.c2d.mem_en === rvdp.mem_en.Y)
    io.dmem.req.mfunc := Mux((io.c2d.mem_wren === rvdp.mem_wren.Y), mram_op.MF_WR, mram_op.MF_RD)

    val mem_type = MuxLookup(
        io.c2d.mem_type,
        0.U,  //default: 0.U
        Array(
            rvdp.mem_type.B -> mram_op.MT_B,
            rvdp.mem_type.H -> mram_op.MT_H,
            rvdp.mem_type.W -> mram_op.MT_W,
            rvdp.mem_type.BU -> mram_op.MT_B,
            rvdp.mem_type.HU -> mram_op.MT_H
        )
    )
    io.dmem.req.mtype := mem_type
    
    val rd_addr = inst(rvinst.rdbh, rvinst.rdbl)
    when(io.c2d.reg_wren === rvdp.reg_wren.Y) {
        switch(io.c2d.wb_sel) {
            is(rvdp.wb_sel.ALU) {
                val rd_data = alu_out
                regfile(rd_addr) := Mux((rd_addr!=0.U), rd_data, UInt(0, rvspec.xlen))
            }
            is(rvdp.wb_sel.MEM) {
                when(io.dmem.resp.valid) {
                    val mem_rbdata = MuxLookup( //Read Back Data
                        io.c2d.mem_type,
                        0.U,  //default: 0.U
                        Array(
                            rvdp.mem_type.B -> Cat(Fill(24,io.dmem.resp.data(7)), io.dmem.resp.data(7,0)),
                            rvdp.mem_type.H -> Cat(Fill(16,io.dmem.resp.data(15)), io.dmem.resp.data(15,0)),
                            rvdp.mem_type.W -> io.dmem.resp.data,
                            rvdp.mem_type.BU -> Cat(Fill(24,UInt(0,1)), io.dmem.resp.data(7,0)),
                            rvdp.mem_type.HU -> Cat(Fill(16,UInt(0,1)), io.dmem.resp.data(15,0))
                        )
                    )
                    val rd_data = mem_rbdata
                    regfile(rd_addr) := Mux((rd_addr!=0.U), rd_data, UInt(0, rvspec.xlen))
                }
            }
            is(rvdp.wb_sel.PC4) {
                val rd_data = pc_a4
                regfile(rd_addr) := Mux((rd_addr!=0.U), rd_data, UInt(0, rvspec.xlen))
            }
            is(rvdp.wb_sel.CSR) {
                val rd_data = csr_rdata
                regfile(rd_addr) := Mux((rd_addr!=0.U), rd_data, UInt(0, rvspec.xlen))
            }
        }
    }

    //CSRs
    val csr = Module(new rvcsr)
    csr.io.addr := inst(csr_def.addrh, csr_def.addrl)
    csr.io.op := io.c2d.csr_cmd
    csr.io.wdata := alu_opd1
    csr_rdata := csr.io.rdata

    csr.io.core_inst := inst
    csr.io.core_pc := pc
    csr.io.core_excp := io.c2d.cexcp
    csr.io.core_cause := io.c2d.ccause
    io.d2c.dexcp := csr.io.csr_excp
    pc_ex := csr.io.csr_evec

    //#Extra. Intuitive RegFile direct access path for Scala/Chisel debug
    io.rfdbg.req.ready := true.B
    io.rfdbg.resp.valid := false.B
    when(io.rfdbg.req.valid) {
        switch(io.rfdbg.req.mfunc) {
            is(mram_op.MF_RD) {
                when(io.rfdbg.req.addr === "hFFFFFFFF".U) { //direct read PC
                    io.rfdbg.resp.valid := true.B
                    io.rfdbg.resp.data := pc
                }
                .otherwise {
                    io.rfdbg.resp.valid := true.B
                    io.rfdbg.resp.data := regfile(io.rfdbg.req.addr(4,0))
                }
            }
            is(mram_op.MF_WR) {
                regfile(io.rfdbg.req.addr(4,0)) := io.rfdbg.req.data
            }
        }
    }

    //rvdm (RV DebugModule)
    io.ddpath.rdata := regfile(io.ddpath.addr)
    when(io.ddpath.valid){
        regfile(io.ddpath.addr) := io.ddpath.wdata
    }  

    //Actually Initialized by reset
    when(reset.toBool()) {
        //pc := UInt(0, rvspec.xlen)
        pc := Mux(io.dbg_sel_unit, UInt(0, rvspec.xlen), rvrst.PC)
        
        /*
        //Disable for core could be configured while still in reset state
        var i = 0
        for( i <- 0 until (rvspec.xrsz-1)) {
            regfile(i) := UInt(i, rvspec.xlen)
        }
        */

        io.imem.req.valid := false.B
        io.dmem.req.valid := false.B

        //Disable for core could be configured while still in reset state
        //io.rfdbg.req.ready := false.B
    }
}
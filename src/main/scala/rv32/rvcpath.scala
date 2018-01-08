//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// 1-stage control path
//=======================================================================
package rvcore

import chisel3._
import chisel3.util._
import rvcommon._

class c2d_io extends Bundle {
    //getWidth.W from "using def" section in ref.
    //ref: https://github.com/ucb-bar/chisel-tutorial/wiki/scripting-hardware-generation
    val alu_func = Output(UInt(rvalu.X.getWidth.W))
    val op2_sel = Output(UInt(rvdp.op2_sel.X.getWidth.W))
    val reg_wren = Output(UInt(rvdp.reg_wren.X.getWidth.W))
    val mem_wren = Output(UInt(rvdp.mem_wren.X.getWidth.W))
    val mem_en = Output(UInt(rvdp.mem_en.X.getWidth.W))
    val wb_sel = Output(UInt(rvdp.wb_sel.X.getWidth.W))
    val mem_type = Output(UInt(rvdp.mem_type.X.getWidth.W))
    val pc_sel = Output(UInt(rvdp.pc_sel.X.getWidth.W))
    val br_sel = Output(UInt(rvdp.br_sel.X.getWidth.W))
    val op1_sel = Output(UInt(rvdp.op1_sel.X.getWidth.W))
    val csr_cmd = Output(UInt(csr_op.X.getWidth.W))

    val cexcp = Output(Bool())
    val ccause = Output(UInt(rvcause.X.getWidth.W))
}

class cpath_io extends Bundle {
    val imem = new mram_io(rvspec.xlen)
    val dmem = new mram_io(rvspec.xlen)
    val d2c = Flipped(new d2c_io())
    val c2d = new c2d_io()
    
    val dcpath = Flipped(new dcpath_io())
}

object is_inst {
    val Y = UInt(1,1)
    val N = UInt(0,1)
}

class rvcpath extends Module {
    val io = IO(new cpath_io)
    //DontCare for Initialized Error
    io <> DontCare

    //Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/rv32_1stage/cpath.scala
    //Ref: https://chisel.eecs.berkeley.edu/2.2.0/manual.html , Sect. 19 Extra Stuff, ListLookup
    //Ref: https://stackoverflow.com/questions/36612741/listlookup-in-chisel , casez
    val ctlsig = ListLookup(    
                                io.d2c.inst,    //switch(io.d2c.inst) or casez(io.d2c.inst) 
                                //Align to below instruction list //default: List(rvalu.X, ....)
                                List(rvalu.X,    rvdp.op2_sel.X, rvdp.reg_wren.X, rvdp.mem_wren.X, rvdp.mem_en.X, rvdp.wb_sel.X,   rvdp.mem_type.X,  rvdp.pc_sel.X,  rvdp.br_sel.X,    rvdp.op1_sel.X, csr_op.X, is_inst.N), 
        Array(
            //Branch instructions, RV32I Base Instruction Set List up to down order
            rvinst.LUI ->       List(rvalu.CP2,  rvdp.op2_sel.U, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.X, csr_op.X, is_inst.Y),
            rvinst.AUIPC ->     List(rvalu.ADD,  rvdp.op2_sel.U, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.P, csr_op.X, is_inst.Y),
            //Branch instructions, RV32I Base Instruction Set List up to down order
            rvinst.JAL ->       List(rvalu.ADD,  rvdp.op2_sel.J, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.PC4, rvdp.mem_type.X,  rvdp.pc_sel.JP, rvdp.br_sel.X,    rvdp.op1_sel.P, csr_op.X, is_inst.Y),
            rvinst.JALR ->      List(rvalu.ADD,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.PC4, rvdp.mem_type.X,  rvdp.pc_sel.JP, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            //Branch instructions, RV32I Base Instruction Set List up to down order
            rvinst.BEQ ->       List(rvalu.ADD,  rvdp.op2_sel.B, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.B,  rvdp.pc_sel.BR, rvdp.br_sel.BEQ,  rvdp.op1_sel.P, csr_op.X, is_inst.Y),
            rvinst.BNE ->       List(rvalu.ADD,  rvdp.op2_sel.B, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.B,  rvdp.pc_sel.BR, rvdp.br_sel.BNE,  rvdp.op1_sel.P, csr_op.X, is_inst.Y),
            rvinst.BLT ->       List(rvalu.ADD,  rvdp.op2_sel.B, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.B,  rvdp.pc_sel.BR, rvdp.br_sel.BLT,  rvdp.op1_sel.P, csr_op.X, is_inst.Y),
            rvinst.BGE ->       List(rvalu.ADD,  rvdp.op2_sel.B, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.B,  rvdp.pc_sel.BR, rvdp.br_sel.BGE,  rvdp.op1_sel.P, csr_op.X, is_inst.Y),
            rvinst.BLTU ->      List(rvalu.ADD,  rvdp.op2_sel.B, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.B,  rvdp.pc_sel.BR, rvdp.br_sel.BLTU, rvdp.op1_sel.P, csr_op.X, is_inst.Y),
            rvinst.BGEU ->      List(rvalu.ADD,  rvdp.op2_sel.B, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.B,  rvdp.pc_sel.BR, rvdp.br_sel.BGEU, rvdp.op1_sel.P, csr_op.X, is_inst.Y),
            //Load/Store instructions, RV32I Base Instruction Set List up to down order
            rvinst.LB ->        List(rvalu.ADD,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.Y, rvdp.wb_sel.MEM, rvdp.mem_type.B,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.LH ->        List(rvalu.ADD,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.Y, rvdp.wb_sel.MEM, rvdp.mem_type.H,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.LW ->        List(rvalu.ADD,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.Y, rvdp.wb_sel.MEM, rvdp.mem_type.W,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.LBU ->       List(rvalu.ADD,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.Y, rvdp.wb_sel.MEM, rvdp.mem_type.BU, rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.LHU ->       List(rvalu.ADD,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.Y, rvdp.wb_sel.MEM, rvdp.mem_type.HU, rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.SB ->        List(rvalu.ADD,  rvdp.op2_sel.S, rvdp.reg_wren.N, rvdp.mem_wren.Y, rvdp.mem_en.Y, rvdp.wb_sel.ALU, rvdp.mem_type.B,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.SH ->        List(rvalu.ADD,  rvdp.op2_sel.S, rvdp.reg_wren.N, rvdp.mem_wren.Y, rvdp.mem_en.Y, rvdp.wb_sel.ALU, rvdp.mem_type.H,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.SW ->        List(rvalu.ADD,  rvdp.op2_sel.S, rvdp.reg_wren.N, rvdp.mem_wren.Y, rvdp.mem_en.Y, rvdp.wb_sel.ALU, rvdp.mem_type.W,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            //For Reg-Imm instructions, RV32I Base Instruction Set List up to down order
            rvinst.ADDI ->      List(rvalu.ADD,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.SLTI ->      List(rvalu.SLT,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.SLTIU ->     List(rvalu.SLTU, rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.XORI ->      List(rvalu.XOR,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.ORI ->       List(rvalu.OR,   rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.ANDI ->      List(rvalu.AND,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.SLLI ->      List(rvalu.SLL,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.SRLI ->      List(rvalu.SRL,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.SRAI ->      List(rvalu.SRA,  rvdp.op2_sel.I, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            //For Reg-Reg instructions, RV32I Base Instruction Set List up to down order
            rvinst.ADD ->       List(rvalu.ADD,  rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.SUB ->       List(rvalu.SUB,  rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            rvinst.SLL ->       List(rvalu.SLL,  rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.SLT ->       List(rvalu.SLT,  rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.SLTU ->      List(rvalu.SLTU, rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.XOR ->       List(rvalu.XOR,  rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.SRL ->       List(rvalu.SRL,  rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.SRA ->       List(rvalu.SRA,  rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.OR ->        List(rvalu.OR,   rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y), 
            rvinst.AND ->       List(rvalu.AND,  rvdp.op2_sel.R, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.ALU, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.X, is_inst.Y),
            //For CSR Read/Write instructions, RV32I Base Instruction Set List up to down order
            rvinst.CSRRW ->     List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.CSR, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.W, is_inst.Y),
            rvinst.CSRRS ->     List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.CSR, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.S, is_inst.Y),
            rvinst.CSRRC ->     List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.CSR, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.R, csr_op.C, is_inst.Y),
            rvinst.CSRRWI ->    List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.CSR, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.I, csr_op.W, is_inst.Y),
            rvinst.CSRRSI ->    List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.CSR, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.I, csr_op.S, is_inst.Y),
            rvinst.CSRRCI ->    List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.Y, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.CSR, rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.I, csr_op.C, is_inst.Y),
            //For CSR Privilege instructions, RV32I Base Instruction Set List up to down order
            rvinst.FENCE  ->    List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.X, csr_op.X, is_inst.Y),
            rvinst.FENCE_I  ->  List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.X, csr_op.X, is_inst.Y),
            rvinst.ECALL  ->    List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.X,  rvdp.pc_sel.EX, rvdp.br_sel.X,    rvdp.op1_sel.X, csr_op.I, is_inst.Y),
            rvinst.EBREAK ->    List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.X,  rvdp.pc_sel.EX, rvdp.br_sel.X,    rvdp.op1_sel.X, csr_op.I, is_inst.Y),
            rvinst.MRET   ->    List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.X,  rvdp.pc_sel.EX, rvdp.br_sel.X,    rvdp.op1_sel.X, csr_op.I, is_inst.Y),
            rvinst.WFI    ->    List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.X,  rvdp.pc_sel.A4, rvdp.br_sel.X,    rvdp.op1_sel.X, csr_op.I, is_inst.Y),
            //For Debug instruction, Debug Spec 0.13, section 4.4.1
            rvinst.DRET   ->    List(rvalu.X  ,  rvdp.op2_sel.X, rvdp.reg_wren.N, rvdp.mem_wren.N, rvdp.mem_en.N, rvdp.wb_sel.X,   rvdp.mem_type.X,  rvdp.pc_sel.EX, rvdp.br_sel.X,    rvdp.op1_sel.X, csr_op.I, is_inst.Y)
        )
    )

    //Debug, verify instruction in cpath
    //printf("cpath inst: 0x%x\n", io.d2c.inst)

    val alu_func :: op2_sel :: reg_wren :: mem_wren :: mem_en :: wb_sel :: mem_type :: pc_sel :: br_sel :: op1_sel :: csr_cmd :: valid_inst :: Nil = ctlsig

    io.c2d.alu_func := alu_func
    io.c2d.op2_sel := op2_sel
    io.c2d.reg_wren := reg_wren
    io.c2d.mem_wren := mem_wren
    io.c2d.mem_en := mem_en
    io.c2d.wb_sel := wb_sel
    io.c2d.mem_type := mem_type
    io.c2d.pc_sel := Mux(io.c2d.cexcp | io.d2c.dexcp, rvdp.pc_sel.EX, pc_sel)
    io.c2d.br_sel := br_sel
    io.c2d.op1_sel := op1_sel
    io.c2d.csr_cmd := csr_cmd

    io.c2d.cexcp := (valid_inst === is_inst.N)
    io.c2d.ccause := Mux(io.c2d.cexcp, rvcause.illegal_inst, 0.U)
}

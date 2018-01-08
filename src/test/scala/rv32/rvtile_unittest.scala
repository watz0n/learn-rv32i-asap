//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// RVTile Unit-test by verilator
//=======================================================================
package rvsim

import chisel3._
import chisel3.util._
import chisel3.iotesters._
import org.scalatest._              
import org.scalatest.exceptions._

import rvcore._
import rvtile._

import rvcommon._

class RVCoreRRPeekPokeTester(dut: rvtile, rs1:(Int,UInt), rs2:(Int,UInt), rd:(Int,UInt), op:UInt) extends PeekPokeTester(dut)  {

    //Selection of debug method after add DebugModule for RV
    poke(dut.io.dbg_sel_unit, true.B)

    //Keep rvcore still in reset before initialized register/memory
    poke(dut.io.rst_core, true.B)

    step(1)

    val (rs1a:Int, rs1d:UInt) = rs1
    val (rs2a:Int, rs2d:UInt) = rs2
    val (rda:Int, rdd:UInt) = rd

    //printf("#RS1[%d]:%08X, RS2[%d]:%08X\n", UInt(rs1a), UInt(rs1d), UInt(rs2a), UInt(rs2d)); 
    println("#RS1[%d]:%08X, RS2[%d]:%08X".format(rs1a, rs1d.litValue, rs2a, rs2d.litValue)); 

    while(peek(dut.io.rfdbg.req.ready) == BigInt(0)) {
        step(1)
    }

    poke(dut.io.rfdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
    poke(dut.io.rfdbg.req.addr, UInt(rs1a&0x3F, rvspec.xlen))
    poke(dut.io.rfdbg.req.data, UInt(rs1d, rvspec.xlen))
    poke(dut.io.rfdbg.req.valid, true.B)
    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)

    poke(dut.io.rfdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
    poke(dut.io.rfdbg.req.addr, UInt(rs2a&0x3F, rvspec.xlen))
    poke(dut.io.rfdbg.req.data, UInt(rs2d, rvspec.xlen))
    poke(dut.io.rfdbg.req.valid, true.B)
    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)     

    //Chisel Concatenate lead to verilator fail, treat as BlackBox fail
    //val inst = Cat(UInt(0,1), op(3), UInt(0,5), UInt(rs2a,6), UInt(rs1a,6), op(2,0), UInt(rda,6), UInt(0x33,7))

    //"b0000000_00010_00001_000_00011_0110011" ADD r1,r2,r3
    //"b0000_0000_0010_0000_1000_0001_1011_0011"
    //val inst = 0x002081B3 //Quick Hack for ADD r1, r2, r3
    
    val opval = op.litValue
    val inst =  (opval&0x08)<<(30-3) | 
                (rs2a&0x3F)<<(20-0) | 
                (rs1a&0x3F)<<(15-0) | 
                (opval&0x07)<<(12-0) |
                (rda&0x3F)<<(7-0) |
                0x33 //Reg-Reg opcode
    
    //printf("#Test Inst: %08X\n", Bits(inst)) //Failed, treat as BlackBox fail
    println("#Test Inst: 0x%08X".format(inst))

/*   
    //Send Instruction by virtual memory respeonse
    poke(dut.io.imem.req.ready, true.B)
    while(peek(dut.io.rfdbg.req.ready) == BigInt(0)) {
        step(1)
    }
    step(1)

    poke(dut.io.imem.resp.valid, true.B)
    poke(dut.io.imem.resp.data, inst)
    step(1)
    poke(dut.io.imem.resp.valid, false.B)
    step(1)
*/

    //Send instructions to Magic RAM
    while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
        step(1)
    }
    poke(dut.io.mrdbg.req.valid, true.B)
    poke(dut.io.mrdbg.req.addr, 0)
    poke(dut.io.mrdbg.req.data, inst)
    poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
    step(1)
    poke(dut.io.mrdbg.req.valid, false.B)
    step(1)

    //Start core
    poke(dut.io.rst_core, false.B)
    step(1)

    poke(dut.io.rfdbg.req.mfunc, mram_op.MF_RD)
    poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
    poke(dut.io.rfdbg.req.addr, UInt(rda&0x3F, rvspec.xlen))
    poke(dut.io.rfdbg.req.data, UInt(0x00, rvspec.xlen))
    poke(dut.io.rfdbg.req.valid, true.B)
    var resp_count = 0;
    while((peek(dut.io.rfdbg.resp.valid) == BigInt(0)) && (resp_count<10)) {
        step(1)
        resp_count += 1
    }
    expect(dut.io.rfdbg.resp.valid, 1)
    expect(dut.io.rfdbg.resp.data, UInt(rdd, rvspec.xlen))

    //printf("#RD[%d], expect: %08X, sim:%08X\n", UInt(rda), UInt(rdd), UInt(peek(dut.io.rfdbg.resp.data)));
    println("#RD[%d], expect: %08X, sim:%08X".format(rda, rdd.litValue, peek(dut.io.rfdbg.resp.data))); 

    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)

}

class RVCoreRIPeekPokeTester(dut: rvtile, rs1:(Int,UInt), imm:UInt, rd:(Int,UInt), op:UInt) extends PeekPokeTester(dut)  {

    //Selection of debug method after add DebugModule for RV
    poke(dut.io.dbg_sel_unit, true.B)

    //Keep rvcore still in reset before initialized register/memory
    poke(dut.io.rst_core, true.B)

    val (rs1a:Int, rs1d:UInt) = rs1
    val (rda:Int, rdd:UInt) = rd

    println("#RS1[%d]:%08X, imm:%08X".format(rs1a, rs1d.litValue, imm.litValue)); 

    while(peek(dut.io.rfdbg.req.ready) == BigInt(0)) {
        step(1)
    }

    poke(dut.io.rfdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
    poke(dut.io.rfdbg.req.addr, UInt(rs1a&0x3F, rvspec.xlen))
    poke(dut.io.rfdbg.req.data, UInt(rs1d, rvspec.xlen))
    poke(dut.io.rfdbg.req.valid, true.B)
    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)  
    
    val opval = op.litValue
    val immval = imm.litValue
    val inst =  (opval&0x08)<<(30-3) |
                (immval&0x3FF)<<(20-0) | 
                (rs1a&0x3F)<<(15-0) | 
                (opval&0x07)<<(12-0) |
                (rda&0x3F)<<(7-0) |
                0x13 //Reg-Imm opcode

    println("#Test Inst: 0x%08X".format(inst))

    //Send instructions to Magic RAM
    while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
        step(1)
    }
    poke(dut.io.mrdbg.req.valid, true.B)
    poke(dut.io.mrdbg.req.addr, 0)
    poke(dut.io.mrdbg.req.data, inst)
    poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
    step(1)
    poke(dut.io.mrdbg.req.valid, false.B)
    step(1)

    //Start core
    poke(dut.io.rst_core, false.B)
    step(1)

    poke(dut.io.rfdbg.req.mfunc, mram_op.MF_RD)
    poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
    poke(dut.io.rfdbg.req.addr, UInt(rda&0x3F, rvspec.xlen))
    poke(dut.io.rfdbg.req.data, UInt(0x00, rvspec.xlen))
    poke(dut.io.rfdbg.req.valid, true.B)
    var resp_count = 0;
    while((peek(dut.io.rfdbg.resp.valid) == BigInt(0)) && (resp_count<10)) {
        step(1)
        resp_count += 1
    }
    expect(dut.io.rfdbg.resp.valid, 1)
    expect(dut.io.rfdbg.resp.data, UInt(rdd, rvspec.xlen))

    //printf("#RD[%d], expect: %08X, sim:%08X\n", UInt(rda), UInt(rdd), UInt(peek(dut.io.rfdbg.resp.data)));
    println("#RD[%d], expect: %08X, sim:%08X".format(rda, rdd.litValue, peek(dut.io.rfdbg.resp.data))); 

    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)

}

object sim_lsop {
  val LB = UInt(0, 4)
  val LH = UInt(1, 4)
  val LW = UInt(2, 4)
  val LBU = UInt(4, 4)
  val LHU = UInt(5, 4)
  val SB = UInt(8, 4)
  val SH = UInt(9, 4)
  val SW = UInt(10, 4)
}

class RVCoreLSPeekPokeTester(dut: rvtile, rs1:(Int,UInt), imm:UInt, rd:(Int, UInt), op:UInt, exp:UInt) extends PeekPokeTester(dut)  {

    //Selection of debug method after add DebugModule for RV
    poke(dut.io.dbg_sel_unit, true.B)

    //Keep rvcore still in reset before initialized register/memory
    poke(dut.io.rst_core, true.B)

    val (rs1a:Int, rs1d:UInt) = rs1
    val (rda:Int, rdd:UInt) = rd

    println("#RS1[%d]:%08X, imm:%08X".format(rs1a, rs1d.litValue, imm.litValue)); 

    while(peek(dut.io.rfdbg.req.ready) == BigInt(0)) {
        step(1)
    }

    poke(dut.io.rfdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
    poke(dut.io.rfdbg.req.addr, UInt(rs1a&0x3F, rvspec.xlen))
    poke(dut.io.rfdbg.req.data, UInt(rs1d, rvspec.xlen))
    poke(dut.io.rfdbg.req.valid, true.B)
    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)  
    
    val opval = op.litValue
    if((opval&0x8) == 0x0) { //Load
        val immval = imm.litValue
        val rs1val = rs1d.litValue

        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        poke(dut.io.mrdbg.req.addr, UInt((rs1val+immval)&0xFFFFFFFC, rvspec.xlen))
        poke(dut.io.mrdbg.req.data, UInt(rdd, rvspec.xlen))
        poke(dut.io.mrdbg.req.valid, true.B)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)  

        //val opval = op.litValue
        val rdval = rdd.litValue
        val inst =  (immval&0x3FF)<<(20-0) | 
                    (rs1a&0x3F)<<(15-0) | 
                    (opval&0x07)<<(12-0) |
                    (rda&0x3F)<<(7-0) |
                    0x03 //Load

        println("#Test Inst: 0x%08X".format(inst))

        //Send instructions to Magic RAM
        while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.mrdbg.req.valid, true.B)
        poke(dut.io.mrdbg.req.addr, 0)
        poke(dut.io.mrdbg.req.data, inst)
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)

        //Start core
        poke(dut.io.rst_core, false.B)
        step(1)

        poke(dut.io.rfdbg.req.mfunc, mram_op.MF_RD)
        poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
        poke(dut.io.rfdbg.req.addr, UInt(rda&0x3F, rvspec.xlen))
        poke(dut.io.rfdbg.req.data, UInt(0x00, rvspec.xlen))
        poke(dut.io.rfdbg.req.valid, true.B)
        var resp_count = 0;
        while((peek(dut.io.rfdbg.resp.valid) == BigInt(0)) && (resp_count<10)) {
            step(1)
            resp_count += 1
        }
        expect(dut.io.rfdbg.resp.valid, 1)
        //expect(dut.io.rfdbg.resp.data, UInt(rdd, rvspec.xlen))
        expect(dut.io.rfdbg.resp.data, UInt(exp, rvspec.xlen))

        println("#RD[%d], expect: %08X, sim:%08X".format(rda, exp.litValue, peek(dut.io.rfdbg.resp.data)))

        step(1)
        poke(dut.io.rfdbg.req.valid, false.B)
        step(1)
    }
    else { //Store
        //Push rs2 data from rd
        poke(dut.io.rfdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
        poke(dut.io.rfdbg.req.addr, UInt(rda&0x3F, rvspec.xlen))
        poke(dut.io.rfdbg.req.data, UInt(rdd, rvspec.xlen))
        poke(dut.io.rfdbg.req.valid, true.B)
        step(1)
        poke(dut.io.rfdbg.req.valid, false.B)
        step(1)  

        //val opval = op.litValue
        val immval = imm.litValue
        val rdval = rdd.litValue
        val inst =  (immval&0x3E0)<<(25-5) | 
                    (rda&0x3F)<<(20-0) | 
                    (rs1a&0x3F)<<(15-0) | 
                    (opval&0x07)<<(12-0) |
                    (immval&0x01F)<<(7-0) |
                    0x23 //Store

        println("#Test Inst: 0x%08X".format(inst))

        //Send instructions to Magic RAM
        while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.mrdbg.req.valid, true.B)
        poke(dut.io.mrdbg.req.addr, 0)
        poke(dut.io.mrdbg.req.data, inst)
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)

        //Start core
        poke(dut.io.rst_core, false.B)
        step(1)

        val rs1val = rs1d.litValue
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_RD)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        poke(dut.io.mrdbg.req.addr, UInt(rs1val+immval, rvspec.xlen))
        poke(dut.io.mrdbg.req.data, UInt(0x00, rvspec.xlen))
        poke(dut.io.mrdbg.req.valid, true.B)
        var resp_count = 0;
        while((peek(dut.io.mrdbg.resp.valid) == BigInt(0)) && (resp_count<10)) {
            step(1)
            resp_count += 1
        }
        expect(dut.io.mrdbg.resp.valid, 1)
        expect(dut.io.mrdbg.resp.data, UInt(exp, rvspec.xlen))

        println("#MRAM[%X], expect: %08X, sim:%08X".format(rs1val+immval, exp.litValue, peek(dut.io.mrdbg.resp.data)))

        step(1)
        poke(dut.io.rfdbg.req.valid, false.B)
        step(1)
    }
}

object sim_juop {
  val LUI = UInt(0, 2)
  val AUIPC = UInt(1, 2)
  val JAL = UInt(2, 2)
  val JALR = UInt(3, 2)
}

class RVCoreJUPeekPokeTester(dut: rvtile, rs1:(Int,UInt), imm:UInt, rd:(Int, UInt), op:UInt, off:UInt) extends PeekPokeTester(dut)  {

    //Selection of debug method after add DebugModule for RV
    poke(dut.io.dbg_sel_unit, true.B)

    //Keep rvcore still in reset before initialized register/memory
    poke(dut.io.rst_core, true.B)

    val (rs1a:Int, rs1d:UInt) = rs1
    val (rda:Int, rdd:UInt) = rd

    println("#RS1[%d]:%08X, imm:%08X".format(rs1a, rs1d.litValue, imm.litValue)); 

    while(peek(dut.io.rfdbg.req.ready) == BigInt(0)) {
        step(1)
    }

    poke(dut.io.rfdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
    poke(dut.io.rfdbg.req.addr, UInt(rs1a&0x3F, rvspec.xlen))
    poke(dut.io.rfdbg.req.data, UInt(rs1d, rvspec.xlen))
    poke(dut.io.rfdbg.req.valid, true.B)
    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)

    val offval = off.litValue
    val pc_off = offval & "hFFFFFFFC".U.litValue

    if(offval != 0) {

        val inst =  (pc_off&0x00000FFF)<<(20-0) |  //Use rs1 and rd ar x0 (zero)
                    0x67 //JALR opcode

        println("#Test Inst: 0x%08X".format(inst))

        //Send instructions to Magic RAM
        while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.mrdbg.req.valid, true.B)
        poke(dut.io.mrdbg.req.addr, 0)
        poke(dut.io.mrdbg.req.data, inst)
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)
    }

    val opval = op.litValue
    if(opval == 0) {
        //LUI
        val immval = imm.litValue
        val inst =  (immval&0xFFFFF000)<<(12-12) | 
                    (rda&0x3F)<<(7-0) |
                    0x37 //LUI opcode
        
        println("#Test Inst: 0x%08X".format(inst))

        //Send instructions to Magic RAM
        while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.mrdbg.req.valid, true.B)
        poke(dut.io.mrdbg.req.addr, pc_off)
        poke(dut.io.mrdbg.req.data, inst)
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)
    }
    else if(opval == 1) {
        //AUIPC
        val immval = imm.litValue
        val inst =  (immval&0xFFFFF000)<<(12-12) | 
                    (rda&0x3F)<<(7-0) |
                    0x17 //AUIPC opcode
        
        println("#Test Inst: 0x%08X".format(inst))

        //Send instructions to Magic RAM
        while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.mrdbg.req.valid, true.B)
        poke(dut.io.mrdbg.req.addr, pc_off)
        poke(dut.io.mrdbg.req.data, inst)
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)
        
    }
    else if(opval == 2) {
        //JAL
        val immval = imm.litValue
        val inst =  (immval&0x100000)<<(31-20) | 
                    (immval&0x0007FE)<<(21-1) | 
                    (immval&0x000800)<<(20-1) | 
                    (immval&0x0FF000)<<(12-12) | 
                    (rda&0x3F)<<(7-0) |
                    0x6F //JAL opcode

        println("#Test Inst: 0x%08X".format(inst))

        //Send instructions to Magic RAM
        while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.mrdbg.req.valid, true.B)
        poke(dut.io.mrdbg.req.addr, pc_off)
        poke(dut.io.mrdbg.req.data, inst)
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)
    }
    else if(opval == 3) {
        //JALR
        val immval = imm.litValue
        val inst =  (immval&0x00000FFF)<<(20-0) | 
                    (rs1a&0x3F)<<(15-0) |
                    (rda&0x3F)<<(7-0) |
                    0x67 //JALR opcode
        
        println("#Test Inst: 0x%08X".format(inst))       

        //Send instructions to Magic RAM
        while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.mrdbg.req.valid, true.B)
        poke(dut.io.mrdbg.req.addr, pc_off)
        poke(dut.io.mrdbg.req.data, inst)
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)
    }

    //Start core
    poke(dut.io.rst_core, false.B)
    step(1)

    if(opval >= 2) {
        poke(dut.io.rfdbg.req.mfunc, mram_op.MF_RD)
        poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
        poke(dut.io.rfdbg.req.addr, UInt("hFFFFFFFF".U, rvspec.xlen)) //Read PC
        poke(dut.io.rfdbg.req.data, UInt(0x00, rvspec.xlen))
        poke(dut.io.rfdbg.req.valid, true.B)
        var resp_count = 0;
        while((peek(dut.io.rfdbg.resp.valid) == BigInt(0)) && (resp_count<10)) {
            step(1)
            resp_count += 1
        }

        if(pc_off != 0) {
          step(1) //Wait rising edge
        }

        expect(dut.io.rfdbg.resp.data, UInt(rdd, rvspec.xlen))

        poke(dut.io.rfdbg.req.mfunc, mram_op.MF_RD)
        poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
        poke(dut.io.rfdbg.req.addr, UInt(rda&0x3F, rvspec.xlen))
        poke(dut.io.rfdbg.req.data, UInt(0x00, rvspec.xlen))
        poke(dut.io.rfdbg.req.valid, true.B)
        resp_count = 0;
        while((peek(dut.io.rfdbg.resp.valid) == BigInt(0)) && (resp_count<10)) {
            step(1)
            resp_count += 1
        }

        expect(dut.io.rfdbg.resp.data, UInt(pc_off+4, rvspec.xlen)) //x[rd] = pc + 4

    } 
    else {

        poke(dut.io.rfdbg.req.mfunc, mram_op.MF_RD)
        poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
        poke(dut.io.rfdbg.req.addr, UInt(rda&0x3F, rvspec.xlen))
        poke(dut.io.rfdbg.req.data, UInt(0x00, rvspec.xlen))
        poke(dut.io.rfdbg.req.valid, true.B)
        var resp_count = 0;
        while((peek(dut.io.rfdbg.resp.valid) == BigInt(0)) && (resp_count<10)) {
            step(1)
            resp_count += 1
        }

        if(pc_off != 0) {
          step(1) //Wait rising edge
        }

        expect(dut.io.rfdbg.resp.data, UInt(rdd, rvspec.xlen))
        println("#RD[%d], expect: %08X, sim:%08X".format(rda, rdd.litValue, peek(dut.io.rfdbg.resp.data)));
    } 

    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)

}

object sim_csrop {
  val CSRRW = UInt(0x01, 3)
  val CSRRS = UInt(0x02, 3)
  val CSRRC = UInt(0x03, 3)
  val CSRRWI = UInt(0x05, 3)
  val CSRRSI = UInt(0x06, 3)
  val CSRRCI = UInt(0x07, 3)
}

class RVCoreCSRPeekPokeTester(dut: rvtile, rs1:(Int,UInt), rs2:(Int,UInt), rd:(Int, UInt), op:UInt, csr:UInt) extends PeekPokeTester(dut)  {

    //Selection of debug method after add DebugModule for RV
    poke(dut.io.dbg_sel_unit, true.B)

    //Keep rvcore still in reset before initialized register/memory
    poke(dut.io.rst_core, true.B)

    val (rs1a:Int, rs1d:UInt) = rs1
    val (rs2a:Int, rs2d:UInt) = rs2
    val (rda:Int, rdd:UInt) = rd
    val csra:UInt = csr

    println("#RS1[%d]:%08X".format(rs1a, rs1d.litValue)); 
    println("#RS2[%d]:%08X".format(rs2a, rs2d.litValue)); 

    while(peek(dut.io.rfdbg.req.ready) == BigInt(0)) {
        step(1)
    }

    val opval = op.litValue
    val csraval = csra.litValue

    poke(dut.io.rfdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
    poke(dut.io.rfdbg.req.addr, UInt(rs1a&0x3F, rvspec.xlen))
    poke(dut.io.rfdbg.req.data, UInt(rs1d, rvspec.xlen))
    poke(dut.io.rfdbg.req.valid, true.B)
    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)

    //CSRRS -> make csr data logic or with x0 to rd
    val setup_inst =  (csraval&0xFFF)<<(20-0) |
                (rs1a&0x1F)<<(15-0) |     //rs1
                (0x01&0x07)<<(12-0) |     //CSRRW
                (0x00&0x1F)<<(7-0) |      //zero
                0x73 //CSR opcode
    
    //Send instructions to Magic RAM
    while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
        step(1)
    }
    poke(dut.io.mrdbg.req.valid, true.B)
    poke(dut.io.mrdbg.req.addr, 0) //Slot0
    poke(dut.io.mrdbg.req.data, setup_inst)
    poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
    step(1)
    poke(dut.io.mrdbg.req.valid, false.B)
    step(1)
    

    if(opval < 4) { //Reg OP

        poke(dut.io.rfdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
        poke(dut.io.rfdbg.req.addr, UInt(rs2a&0x3F, rvspec.xlen))
        poke(dut.io.rfdbg.req.data, UInt(rs2d, rvspec.xlen))
        poke(dut.io.rfdbg.req.valid, true.B)
        step(1)
        poke(dut.io.rfdbg.req.valid, false.B)
        step(1)

        val inst =  (csraval&0xFFF)<<(20-0) |
                    (rs2a&0x1F)<<(15-0) |
                    (opval&0x07)<<(12-0) |
                    (0x00&0x1F)<<(7-0) | //rd is zero
                    0x73 //CSR opcode

        println("#Test Inst: 0x%08X".format(inst))  

        //Send instructions to Magic RAM
        while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.mrdbg.req.valid, true.B)
        poke(dut.io.mrdbg.req.addr, 4) //Slot1
        poke(dut.io.mrdbg.req.data, inst)
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)
    }
    else {  //Imm OP
        val rs2dval = rs2d.litValue
        val inst =  (csraval&0xFFF)<<(20-0) |
                    (rs2dval&0x1F)<<(15-0) |     //Treat rs1d as imm
                    (opval&0x07)<<(12-0) |
                    (0x00&0x1F)<<(7-0) |      //rd is zero
                    0x73 //CSR opcode

        println("#Test Inst: 0x%08X".format(inst))  

        //Send instructions to Magic RAM
        while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
            step(1)
        }
        poke(dut.io.mrdbg.req.valid, true.B)
        poke(dut.io.mrdbg.req.addr, 4) //Slot1
        poke(dut.io.mrdbg.req.data, inst)
        poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
        poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
        step(1)
        poke(dut.io.mrdbg.req.valid, false.B)
        step(1)
    }

    //CSRRS -> make csr data logic or with x0 to rd
    val post_inst =  (csraval&0xFFF)<<(20-0) |
                (0x00&0x1F)<<(15-0) |     //zero
                (0x02&0x07)<<(12-0) |     //CSRRS
                (rda&0x1F)<<(7-0) |
                0x73 //CSR opcode

    //Send instructions to Magic RAM
    while(peek(dut.io.mrdbg.req.ready) == BigInt(0)) {
        step(1)
    }
    poke(dut.io.mrdbg.req.valid, true.B)
    poke(dut.io.mrdbg.req.addr, 8) //Slot2
    poke(dut.io.mrdbg.req.data, post_inst)
    poke(dut.io.mrdbg.req.mfunc, mram_op.MF_WR)
    poke(dut.io.mrdbg.req.mtype, mram_op.MT_W)
    step(1)
    poke(dut.io.mrdbg.req.valid, false.B)
    step(1)

    //Start core
    poke(dut.io.rst_core, false.B)
    step(1)
    
    step(1) //One more step to wait 2nd instruction done
    step(1) //One more step to wait 3rd instruction done

    poke(dut.io.rfdbg.req.mfunc, mram_op.MF_RD)
    poke(dut.io.rfdbg.req.mtype, mram_op.MT_W)
    poke(dut.io.rfdbg.req.addr, UInt(rda&0x3F, rvspec.xlen))
    poke(dut.io.rfdbg.req.data, UInt(0x00, rvspec.xlen))
    poke(dut.io.rfdbg.req.valid, true.B)
    var resp_count = 0;
    while((peek(dut.io.rfdbg.resp.valid) == BigInt(0)) && (resp_count<10)) {
        step(1)
        resp_count += 1
    }
    expect(dut.io.rfdbg.resp.data, UInt(rdd, rvspec.xlen))
    println("#RD[%d], expect: %08X, sim:%08X".format(rda, rdd.litValue, peek(dut.io.rfdbg.resp.data)));

    step(1)
    poke(dut.io.rfdbg.req.valid, false.B)
    step(1)

}

class RVTilePeekPokeSpec extends ChiselFlatSpec with Matchers {
  
  it should "Test1: RVTile should be elaborate normally" in {
    elaborate { 
      new rvtile 
    }
    info("elaborate rvtile done")
  }

  it should "Test2: RVCore Reg-Reg Tester return the correct result" in {
    val manager = new TesterOptionsManager {
      testerOptions = testerOptions.copy(backendName = "verilator")
    }

    var test_count = 0
    //Behavior Check
    //val rvinst_rr_tests = List(
    //((1, UInt(1)), (2, UInt(1)), (3, UInt(2)), rvalu.ADD))
    //Function Test
    val rvinst_rr_tests = List(
    ((1, UInt(1)), (2, UInt(1)), (3, UInt(2)), rvalu.ADD),
    ((1, UInt(21)), (2, UInt(11)), (3, UInt(10)), rvalu.SUB),
    ((1, UInt(0x55)), (2, UInt(1)), (3, UInt(0xAA)), rvalu.SLL),
    ((1, "hFFFFFFFF".U), (2, UInt(0x00000001)), (3, UInt(1)), rvalu.SLT),
    ((1, "hFFFFFFFF".U), (2, UInt(0x00000001)), (3, UInt(0)), rvalu.SLTU),
    ((1, UInt(0x55)), (2, UInt(0xAA)), (3, UInt(0xFF)), rvalu.XOR),
    ((1, UInt(0xAA)), (2, UInt(1)), (3, UInt(0x55)), rvalu.SRL),
    ((1, "hF0000000".U), (2, UInt(1)), (3, "hF8000000".U), rvalu.SRA),
    ((1, UInt(0xAF)), (2, UInt(0x55)), (3, UInt(0xFF)), rvalu.OR),
    ((1, UInt(0xAF)), (2, UInt(0x55)), (3, UInt(0x05)), rvalu.AND))

    rvinst_rr_tests.foreach { listElement => {
      val (rs1:(Int,UInt), rs2:(Int,UInt), rd:(Int,UInt), op:UInt) = listElement
      test_count += 1
      try {
        chisel3.iotesters.Driver.execute(() => new rvtile, manager) {
          dut => new RVCoreRRPeekPokeTester(dut, rs1, rs2, rd, op)
        } should be (true)
      } catch {
        case tfe: TestFailedException => {
          info("Failed on No.%d tests".format(test_count))
          throw tfe
        }
      }
    }}
    info("Passed %d tests".format(test_count))
  }

  it should "Test3: RVCore Reg-Imm Tester return the correct result" in {
    val manager = new TesterOptionsManager {
      testerOptions = testerOptions.copy(backendName = "verilator")
    }

    var test_count = 0
    val rvinst_ri_tests = List(
    ((1, UInt(1)), UInt(1), (3, UInt(2)), rvalu.ADD),
    ((1, "hFFFFFFFF".U), UInt(0x00000001), (3, UInt(1)), rvalu.SLT),
    ((1, "hFFFFFFFF".U), UInt(0x00000001), (3, UInt(0)), rvalu.SLTU),
    ((1, UInt(0x55)), UInt(0xAA), (3, UInt(0xFF)), rvalu.XOR),
    ((1, UInt(0xAF)), UInt(0x55), (3, UInt(0xFF)), rvalu.OR),
    ((1, UInt(0xAF)), UInt(0x55), (3, UInt(0x05)), rvalu.AND),
    ((1, UInt(0x55)), UInt(1), (3, UInt(0xAA)), rvalu.SLL),
    ((1, UInt(0xAA)), UInt(1), (3, UInt(0x55)), rvalu.SRL),
    ((1, "hF0000000".U),UInt(1), (3, "hF8000000".U), rvalu.SRA))

    rvinst_ri_tests.foreach { listElement => {
      val (rs1:(Int,UInt), imm:UInt, rd:(Int,UInt), op:UInt) = listElement
      test_count += 1
      try {
        chisel3.iotesters.Driver.execute(() => new rvtile, manager) {
          dut => new RVCoreRIPeekPokeTester(dut, rs1, imm, rd, op)
        } should be (true)
      } catch {
        case tfe: TestFailedException => {
          info("Failed on No.%d tests".format(test_count))
          throw tfe
        }
      }
    }}
    info("Passed %d tests".format(test_count))
  }

  it should "Test4: RVCore Load/Store Tester return the correct result" in {
    val manager = new TesterOptionsManager {
      testerOptions = testerOptions.copy(backendName = "verilator")
    }

    var test_count = 0
    val rvinst_ls_tests = List(
    ((1, UInt(4)), UInt(4), (3, "h000000AA".U), sim_lsop.LB,  "hFFFFFFAA".U),
    ((1, UInt(4)), UInt(4), (3, "h0000AAAA".U), sim_lsop.LH,  "hFFFFAAAA".U),
    ((1, UInt(4)), UInt(4), (3, "hAAAA5555".U), sim_lsop.LW,  "hAAAA5555".U),
    ((1, UInt(4)), UInt(4), (3, "h000000AA".U), sim_lsop.LBU, "h000000AA".U),
    ((1, UInt(4)), UInt(4), (3, "h0000AAAA".U), sim_lsop.LHU, "h0000AAAA".U),
    ((1, UInt(4)), UInt(4), (3, "hAA55AA55".U), sim_lsop.SB,  "h00000055".U),
    ((1, UInt(4)), UInt(4), (3, "hAA55AA55".U), sim_lsop.SH,  "h0000AA55".U),
    ((1, UInt(4)), UInt(4), (3, "hAA55AA55".U), sim_lsop.SW,  "hAA55AA55".U),
    ((1, UInt(4)), UInt(5), (3, "hDDCCBBAA".U), sim_lsop.LBU, "h000000BB".U),
    ((1, UInt(4)), UInt(6), (3, "hDDCCBBAA".U), sim_lsop.LBU, "h000000CC".U),
    ((1, UInt(4)), UInt(7), (3, "hDDCCBBAA".U), sim_lsop.LBU, "h000000DD".U),
    ((1, UInt(4)), UInt(6), (3, "hDDCCBBAA".U), sim_lsop.LHU, "h0000DDCC".U),
    ((1, UInt(4)), UInt(5), (3, "hDDCCBBAA".U), sim_lsop.SB,  "h0000AA00".U),
    ((1, UInt(4)), UInt(6), (3, "hDDCCBBAA".U), sim_lsop.SB,  "h00AA0000".U),
    ((1, UInt(4)), UInt(7), (3, "hDDCCBBAA".U), sim_lsop.SB,  "hAA000000".U),
    ((1, UInt(4)), UInt(6), (3, "hDDCCBBAA".U), sim_lsop.SH,  "hBBAA0000".U))

    rvinst_ls_tests.foreach { listElement => {
      val (rs1:(Int,UInt), imm:UInt, rd:(Int,UInt), op:UInt, exp:UInt) = listElement
      test_count += 1
      try {
        chisel3.iotesters.Driver.execute(() => new rvtile, manager) {
          dut => new RVCoreLSPeekPokeTester(dut, rs1, imm, rd, op, exp)
        } should be (true)
      } catch {
        case tfe: TestFailedException => {
          info("Failed on No.%d tests".format(test_count))
          throw tfe
        }
      }
    }}
    info("Passed %d tests".format(test_count))
  }

  it should "Test5: RVCore Jump/Upper Tester return the correct result" in {
    val manager = new TesterOptionsManager {
      testerOptions = testerOptions.copy(backendName = "verilator")
    }

    var test_count = 0
    val rvinst_ju_tests = List(
    ((0, UInt(0)), "h87654321".U, (3, "h87654000".U), sim_juop.LUI,  "h0".U),
    ((0, UInt(0)), "h12345678".U, (3, "h12345678".U), sim_juop.AUIPC,  "h678".U),
    ((0, UInt(0)), "h00045674".U, (3, "h00045678".U), sim_juop.JAL,  "h4".U),
    ((1, "h12345000".U), "h00000678".U, (3, "h12345678".U), sim_juop.JALR,  "h4".U))

    rvinst_ju_tests.foreach { listElement => {
      val (rs1:(Int,UInt), imm:UInt, rd:(Int,UInt), op:UInt, exp:UInt) = listElement
      test_count += 1
      try {
        chisel3.iotesters.Driver.execute(() => new rvtile, manager) {
          dut => new RVCoreJUPeekPokeTester(dut, rs1, imm, rd, op, exp)
        } should be (true)
      } catch {
        case tfe: TestFailedException => {
          info("Failed on No.%d tests".format(test_count))
          throw tfe
        }
      }
    }}
    info("Passed %d tests".format(test_count))
  }

  it should "Test6: RVCore CSR Write/Read Tester return the correct result" in {
    val manager = new TesterOptionsManager {
      testerOptions = testerOptions.copy(backendName = "verilator")
    }

    var test_count = 0
    val rvinst_csrwr_tests = List( 
    //mscratch addr is "h340"
    ((1, "h87654321".U), (2, "h12345678".U), (3, "h12345678".U), sim_csrop.CSRRW,  ("h340".U)),
    ((1, "h87654321".U), (2, "hFFFF0000".U), (3, "hFFFF4321".U), sim_csrop.CSRRS,  ("h340".U)),
    ((1, "h87654321".U), (2, "hFFFF0000".U), (3, "h00004321".U), sim_csrop.CSRRC,  ("h340".U)),
    ((1, "h87654321".U), (2, "h12345678".U), (3, "h00000018".U), sim_csrop.CSRRWI,  ("h340".U)),
    ((1, "h87654321".U), (2, "h00000017".U), (3, "h87654337".U), sim_csrop.CSRRSI,  ("h340".U)),
    ((1, "h87654321".U), (2, "h00000017".U), (3, "h87654320".U), sim_csrop.CSRRCI,  ("h340".U)))

    rvinst_csrwr_tests.foreach { listElement => {
      val (rs1:(Int,UInt), rs2:(Int,UInt), rd:(Int,UInt), op:UInt, csr:UInt) = listElement
      test_count += 1
      try {
        chisel3.iotesters.Driver.execute(() => new rvtile, manager) {
          dut => new RVCoreCSRPeekPokeTester(dut, rs1, rs2, rd, op, csr)
        } should be (true)
      } catch {
        case tfe: TestFailedException => {
          info("Failed on No.%d tests".format(test_count))
          throw tfe
        }
      }
    }}
    info("Passed %d tests".format(test_count))
  }

}

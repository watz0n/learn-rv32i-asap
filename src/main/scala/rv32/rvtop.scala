//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// Generate Verilog from FIRRTL entry
//=======================================================================
package rvtop

import chisel3._
import chisel3.util._
//import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import chisel3.iotesters._

import rvcommon._
import rvcore._
import rvtile._
//import ReferenceChipBackend._

//Morphic form Top in riscv-sodor Top.scala
//Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/rv32_1stage/top.scala

class rvtop extends Module 
{
   val io = IO(new Bundle{
      val success = Output(Bool())

      val dbg_sel_unit = Input(Bool())
      val rfdbg = Flipped(new mram_io(rvspec.xlen))
      val mrdbg = Flipped(new mram_io(rvspec.xlen))
      val rst_core = Input(Bool())
    })
    
    val rvtile = Module(new rvtile)
    val dtm = Module(new rvsimdtm).connect(clock, reset.toBool, rvtile.io.dmi, io.success)

    rvtile.io.dbg_sel_unit := io.dbg_sel_unit
    rvtile.io.rfdbg <> io.rfdbg
    rvtile.io.mrdbg <> io.mrdbg
    rvtile.io.rst_core <> io.rst_core
}

//Ref: https://github.com/freechipsproject/chisel3/wiki/Frequently%20Asked%20Questions#get-me-verilog
object elaborate extends App {
  chisel3.Driver.execute(args, () => new rvtop)
}

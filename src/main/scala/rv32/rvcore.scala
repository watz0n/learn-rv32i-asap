//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// Combine control path (cpath) and data path (dpath) into a core
//=======================================================================
package rvcore

import chisel3._
import chisel3.util._
import rvcommon._

class core_io extends Bundle {
    val imem = new mram_io(rvspec.xlen)
    val dmem = new mram_io(rvspec.xlen)
    val rfdbg = Flipped(new mram_io(rvspec.xlen))

    val dcpath = Flipped(new dcpath_io())
    val ddpath = Flipped(new ddpath_io())

    val dbg_sel_unit = Input(Bool()) //Select Debug Function, 0: DMI, dmi.rstcore/ 1:UnitTest,rst_core
}

class rvcore extends Module {
    val io = IO(new core_io)

    val cpath = Module(new rvcpath())
    val dpath = Module(new rvdpath())

    cpath.io.imem <> io.imem
    cpath.io.dmem <> io.dmem

    dpath.io.imem <> io.imem
    dpath.io.dmem <> io.dmem
    dpath.io.rfdbg <> io.rfdbg

    cpath.io.c2d <> dpath.io.c2d
    dpath.io.d2c <> cpath.io.d2c

    //Debug Module 
    cpath.io.dcpath <> io.dcpath
    dpath.io.ddpath <> io.ddpath

    dpath.io.dbg_sel_unit := io.dbg_sel_unit
}
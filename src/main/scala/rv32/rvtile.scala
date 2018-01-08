//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// Combine a core and a magic ram (mram) into a tile
//=======================================================================
package rvtile

import chisel3._
import chisel3.util._
import rvcommon._
import rvcore._

class tile_io extends Bundle {
    val rfdbg = Flipped(new mram_io(rvspec.xlen)) //RegFile DeBuG
    val mrdbg = Flipped(new mram_io(rvspec.xlen)) //MagicRam DeBuG
    val rst_core = Input(Bool()) //ReSeT CORE

    val dbg_sel_unit = Input(Bool()) //Select Debug Function, 0: DMI, dmi.rstcore/ 1:UnitTest,rst_core

    val dmi = Flipped(new dmi_io())
}

class rvtile extends Module {

    val io = IO(new tile_io)

    val dm = Module(new rvdm())
    val core = Module(new rvcore())
    val amem = Module(new mram_async())

    core.io.imem <> amem.io.inst_port
    core.io.dmem <> amem.io.data_port

    io.rfdbg <> core.io.rfdbg
    io.mrdbg <> amem.io.dbg_port

    //Debug Module
    dm.io.dmi <> io.dmi
    core.io.dcpath <> dm.io.dcpath
    core.io.ddpath <> dm.io.ddpath
    amem.io.dm_port <> dm.io.ddmem

    //Keep core in reset state before push all operations to memory
    //Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/rv32_1stage/tile.scala
    val unit_rst = (io.rst_core)&io.dbg_sel_unit
    val dmi_rst = dm.io.rstcore&(!io.dbg_sel_unit)
    core.reset := unit_rst | dmi_rst | reset.toBool
    core.io.dbg_sel_unit := io.dbg_sel_unit

}
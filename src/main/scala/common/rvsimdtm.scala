//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// Debug Transport Module for Simulation
//=======================================================================
package rvcommon

import chisel3._
import chisel3.util._

//Morphic form SimDTM in riscv-sodor csr.scala for native ready-valid interface
//Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/common/csr.scala

class rvsimdtm extends BlackBox {
  val io = IO(new Bundle {
      val clk = Input(Clock())
      val reset = Input(Bool())
      val debug = new dmi_io() //Change to current native ready-valid interface
      val exit = Output(UInt(32.W))
    })

  def connect(tbclk: Clock, tbreset: Bool, dutio: dmi_io, tbsuccess: Bool) = {
    io.clk := tbclk
    io.reset := tbreset
    dutio <> io.debug 

    tbsuccess := io.exit === 1.U
    when (io.exit >= 2.U) {
      printf("*** FAILED *** (exit code = %d)\n", io.exit >> 1.U)
      //stop(1)
    }
  }
}
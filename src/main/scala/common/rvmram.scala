//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// Magic RAM for rvcore, has multiple ports and read data immediately
//=======================================================================
package rvcommon

import chisel3._
import chisel3.util._

//Ref: https://github.com/freechipsproject/chisel3/wiki/Memories
//Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/common/memory.scala

trait mram_op {

    val MF_X = UInt(0,1)
    val MF_RD = UInt(0,1)
    val MF_WR = UInt(1,1)

    val MT_X = UInt(3,2)
    val MT_B = UInt(1,2)
    val MT_H = UInt(2,2)
    val MT_W = UInt(3,2)
}

trait mram_def {
    val mram_io_width = 32
    val mram_base_width = 8
    //val mram_size = 8192 //In slots, currently each slot is 32-bit(Word)
    val mram_size = 0x10000 //For qsort.riscv or rsort.riscv large memory test
}

object mram_op extends mram_op
object mram_def extends mram_def

class mram_req(data_width: Int) extends Bundle {
    val addr = Output(UInt(rvspec.xlen.W))
    val data = Output(UInt(data_width.W))
    //Use pre-defined data width
    //Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/common/memory.scala
    val mfunc = Output(UInt(mram_op.MF_X.getWidth.W)) 
    val mtype = Output(UInt(mram_op.MT_X.getWidth.W))
    val valid = Output(Bool())
    val ready = Input(Bool())

    //Solve cloneType Error!?
    //Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/common/memory.scala
    override def cloneType = { new mram_req(data_width).asInstanceOf[this.type] }
}

class mram_resp(data_width: Int) extends Bundle {
    val data = Output(UInt(data_width.W))
    val valid = Output(Bool())
    
    //Solve cloneType Error!?
    //Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/common/memory.scala
    override def cloneType = { new mram_resp(data_width).asInstanceOf[this.type] }
}

class mram_io(data_width: Int) extends Bundle {
    val req = new mram_req(data_width)
    val resp = Flipped(new mram_resp(data_width))
    
    //Solve cloneType Error!?
    //Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/common/memory.scala
    override def cloneType = { new mram_io(data_width).asInstanceOf[this.type] }
}

//Make a simple asynchronous ram for core functional validation
//Build with Mask ability, ref: https://github.com/freechipsproject/chisel3/wiki/Memories
class mram_async extends Module {
    
    val mask_size:Int = ((mram_def.mram_io_width+(mram_def.mram_base_width-1))/mram_def.mram_base_width)
    //Ref: https://github.com/freechipsproject/chisel3/blob/master/src/main/scala/chisel3/util/Math.scala
    val addr_lo = log2Up((mram_def.mram_io_width)/mram_def.mram_base_width)
    val addr_hi = log2Up(mram_def.mram_size) + addr_lo - 1

    //println("mram_mask_size:%d".format(mask_size)) //Check magic ram mask size should be 4 at RV32I
    //println("addr_hi:%d, addr_lo:%d".format(addr_hi, addr_lo))

    val io = IO(new Bundle {
        val inst_port = Flipped(new mram_io(rvspec.xlen))
        val data_port = Flipped(new mram_io(rvspec.xlen))
        val dbg_port = Flipped(new mram_io(rvspec.xlen))
        val dm_port = Flipped(new mram_io(rvspec.xlen))
    })

    //DontCare for Initialized Error
    io <> DontCare
    
    //Calculate slots in mram_size by slot width is mram_io_width
    val amem = Mem(mram_def.mram_size, Vec(mask_size, UInt(mram_def.mram_base_width.W)))
    //Add higher address memory, for 0x8000000 exception position
    val ahmem = Mem(mram_def.mram_size, Vec(mask_size, UInt(mram_def.mram_base_width.W)))

    //Ref: https://github.com/freechipsproject/chisel3/wiki/Memories#masks
    //val data_out = Wire(Vec(mask_size, UInt(mram_def.mram_base_width.W)))
    //val data_in = Wire(Vec(mask_size, UInt(mram_def.mram_base_width.W)))
    //val mask = Wire(Vec(mask_size, Bool()))

    //inst. port, only read
    io.inst_port.req.ready := true.B
    io.inst_port.resp.valid := false.B
    when(io.inst_port.req.ready) {
        when(io.inst_port.req.valid) {
            switch(io.inst_port.req.mfunc) {
                is(mram_op.MF_RD) {
                    val addr = io.inst_port.req.addr(addr_hi, addr_lo)
                    //io.data_port.resp.data := amem.read(addr).asUInt
                    when(io.inst_port.req.addr(31) === 1.U) {
                        io.inst_port.resp.data := ahmem.read(addr).asUInt
                    }
                    .otherwise {
                        io.inst_port.resp.data := amem.read(addr).asUInt
                    }
                    io.inst_port.resp.valid := true.B
                }
            }
        }
    }

    //data port, read/write
    io.data_port.req.ready := true.B
    io.data_port.resp.valid := false.B
    when(io.data_port.req.ready) {
        when(io.data_port.req.valid) {
            val port = io.data_port
            switch(io.data_port.req.mfunc) {
                is(mram_op.MF_WR) {
                    val addr = port.req.addr(addr_hi, addr_lo)
                    val data = port.req.data
                    val off = port.req.addr(1, 0)
                    val mt = port.req.mtype
                    val hb = port.req.addr(31)

                    switch(mt) {
                        is(mram_op.MT_B) {
                            val dv = Vec(data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0))

                            val dms = MuxLookup(
                                off, 
                                UInt(0x0,mask_size),
                                Array(
                                    UInt(0,2) -> UInt(0x1,mask_size),
                                    UInt(1,2) -> UInt(0x2,mask_size),
                                    UInt(2,2) -> UInt(0x4,mask_size),
                                    UInt(3,2) -> UInt(0x8,mask_size)
                                )
                            )
                            val dm = dms.toBools
                            when(hb === 1.U) {
                                ahmem.write(addr, dv, dm)
                            }
                            .otherwise {
                                amem.write(addr, dv, dm)
                            }
                        }
                        is(mram_op.MT_H) {
                            val dv = Vec(data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*2-1),mram_def.mram_base_width*1),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*2-1),mram_def.mram_base_width*1))

                            val dms = MuxLookup(
                                off(1), 
                                UInt(0x0,mask_size),
                                Array(
                                    UInt(0,1) -> UInt(0x3,mask_size),
                                    UInt(1,1) -> UInt(0xC,mask_size)
                                )
                            )
                            val dm = dms.toBools
                            
                            when(hb === 1.U) {
                                ahmem.write(addr, dv, dm)
                            }
                            .otherwise {
                                amem.write(addr, dv, dm)
                            }
                        }
                        is(mram_op.MT_W) {
                            val dv = Vec(data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*2-1),mram_def.mram_base_width*1),
                                    data((mram_def.mram_base_width*3-1),mram_def.mram_base_width*2),
                                    data((mram_def.mram_base_width*4-1),mram_def.mram_base_width*3))
                            val dms = UInt(0xF, mask_size)
                            val dm = dms.toBools
                            when(hb === 1.U) {
                                ahmem.write(addr, dv, dm)
                            }
                            .otherwise {
                                amem.write(addr, dv, dm)
                            }
                        }
                    }
                }
                is(mram_op.MF_RD) {
                    val addr = port.req.addr(addr_hi, addr_lo)
                    val off = port.req.addr(addr_lo-1, 0)
                    val mt = port.req.mtype
                    val hb = port.req.addr(31)
                    val wbd = port.resp.data
                    val wbv = port.resp.valid

                    val dv = Wire(Vec(mask_size, UInt(mram_def.mram_base_width.W)))
                    when(hb === 1.U) {
                        dv := ahmem.read(addr)
                    }
                    .otherwise {
                        dv := amem.read(addr)
                    }

                    switch(mt) {
                        is(mram_op.MT_B) {
                            val data = MuxLookup(
                                off, 
                                UInt(0x0,mram_def.mram_io_width),
                                Array(
                                    UInt(0,2) -> Cat(Fill(24, UInt(0,1)), dv(0)),
                                    UInt(1,2) -> Cat(Fill(24, UInt(0,1)), dv(1)),
                                    UInt(2,2) -> Cat(Fill(24, UInt(0,1)), dv(2)),
                                    UInt(3,2) -> Cat(Fill(24, UInt(0,1)), dv(3))
                                )
                            )
                            wbd := data.asUInt
                            wbv := true.B
                        }
                        is(mram_op.MT_H) {
                            val data = MuxLookup(
                                off(1), 
                                UInt(0x0,mram_def.mram_io_width),
                                Array(
                                    UInt(0,1) -> Cat(Fill(16, UInt(0,1)), dv(1), dv(0)),
                                    UInt(1,1) -> Cat(Fill(16, UInt(0,1)), dv(3), dv(2))
                                )
                            )
                            wbd := data.asUInt
                            wbv := true.B
                        }
                        is(mram_op.MT_W) {
                            val data = dv
                            wbd := data.asUInt
                            wbv := true.B
                        }
                    }
                }
            }
        }
    }

    //debug port, force read/write
    io.dbg_port.req.ready := true.B
    io.dbg_port.resp.valid := false.B
    //when(io.dbg_port.req.ready) {
        when(io.dbg_port.req.valid) {
            val port = io.dbg_port
            switch(io.dbg_port.req.mfunc) {
                is(mram_op.MF_WR) {
                    val addr = port.req.addr(addr_hi, addr_lo)
                    val data = port.req.data
                    val off = port.req.addr(1, 0)
                    val mt = port.req.mtype
                    val hb = port.req.addr(31)

                    switch(mt) {
                        is(mram_op.MT_B) {
                            val dv = Vec(data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0))

                            val dms = MuxLookup(
                                off, 
                                UInt(0x0,mask_size),
                                Array(
                                    UInt(0,2) -> UInt(0x1,mask_size),
                                    UInt(1,2) -> UInt(0x2,mask_size),
                                    UInt(2,2) -> UInt(0x4,mask_size),
                                    UInt(3,2) -> UInt(0x8,mask_size)
                                )
                            )
                            val dm = dms.toBools
                            when(hb === 1.U) {
                                ahmem.write(addr, dv, dm)
                            }
                            .otherwise {
                                amem.write(addr, dv, dm)
                            }
                        }
                        is(mram_op.MT_H) {
                            val dv = Vec(data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*2-1),mram_def.mram_base_width*1),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*2-1),mram_def.mram_base_width*1))

                            val dms = MuxLookup(
                                off(1), 
                                UInt(0x0,mask_size),
                                Array(
                                    UInt(0,1) -> UInt(0x3,mask_size),
                                    UInt(1,1) -> UInt(0xC,mask_size)
                                )
                            )
                            val dm = dms.toBools
                            
                            when(hb === 1.U) {
                                ahmem.write(addr, dv, dm)
                            }
                            .otherwise {
                                amem.write(addr, dv, dm)
                            }
                        }
                        is(mram_op.MT_W) {
                            val dv = Vec(data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*2-1),mram_def.mram_base_width*1),
                                    data((mram_def.mram_base_width*3-1),mram_def.mram_base_width*2),
                                    data((mram_def.mram_base_width*4-1),mram_def.mram_base_width*3))
                            val dms = UInt(0xF, mask_size)
                            val dm = dms.toBools
                            when(hb === 1.U) {
                                ahmem.write(addr, dv, dm)
                            }
                            .otherwise {
                                amem.write(addr, dv, dm)
                            }
                        }
                    }
                }
                is(mram_op.MF_RD) {
                    val addr = port.req.addr(addr_hi, addr_lo)
                    val off = port.req.addr(addr_lo-1, 0)
                    val mt = port.req.mtype
                    val hb = port.req.addr(31)
                    val wbd = port.resp.data
                    val wbv = port.resp.valid

                    val dv = Wire(Vec(mask_size, UInt(mram_def.mram_base_width.W)))
                    when(hb === 1.U) {
                        dv := ahmem.read(addr)
                    }
                    .otherwise {
                        dv := amem.read(addr)
                    }

                    switch(mt) {
                        is(mram_op.MT_B) {
                            val data = MuxLookup(
                                off, 
                                UInt(0x0,mram_def.mram_io_width),
                                Array(
                                    UInt(0,2) -> Cat(Fill(24, UInt(0,1)), dv(0)),
                                    UInt(1,2) -> Cat(Fill(24, UInt(0,1)), dv(1)),
                                    UInt(2,2) -> Cat(Fill(24, UInt(0,1)), dv(2)),
                                    UInt(3,2) -> Cat(Fill(24, UInt(0,1)), dv(3))
                                )
                            )
                            wbd := data.asUInt
                            wbv := true.B
                        }
                        is(mram_op.MT_H) {
                            val data = MuxLookup(
                                off(1), 
                                UInt(0x0,mram_def.mram_io_width),
                                Array(
                                    UInt(0,1) -> Cat(Fill(16, UInt(0,1)), dv(1), dv(0)),
                                    UInt(1,1) -> Cat(Fill(16, UInt(0,1)), dv(3), dv(2))
                                )
                            )
                            wbd := data.asUInt
                            wbv := true.B
                        }
                        is(mram_op.MT_W) {
                            val data = dv
                            wbd := data.asUInt
                            wbv := true.B
                        }
                    }
                }
            }
        }
    //}

    //debug module port, force read/write
    io.dm_port.req.ready := true.B
    io.dm_port.resp.valid := false.B
    //when(io.dm_port.req.ready) {
        when(io.dm_port.req.valid) {
            val port = io.dm_port
            switch(io.dm_port.req.mfunc) {
                is(mram_op.MF_WR) {
                    val addr = port.req.addr(addr_hi, addr_lo)
                    val data = port.req.data
                    val off = port.req.addr(1, 0)
                    val mt = port.req.mtype
                    val hb = port.req.addr(31)

                    switch(mt) {
                        is(mram_op.MT_B) {
                            val dv = Vec(data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0))

                            val dms = MuxLookup(
                                off, 
                                UInt(0x0,mask_size),
                                Array(
                                    UInt(0,2) -> UInt(0x1,mask_size),
                                    UInt(1,2) -> UInt(0x2,mask_size),
                                    UInt(2,2) -> UInt(0x4,mask_size),
                                    UInt(3,2) -> UInt(0x8,mask_size)
                                )
                            )
                            val dm = dms.toBools
                            when(hb === 1.U) {
                                ahmem.write(addr, dv, dm)
                            }
                            .otherwise {
                                amem.write(addr, dv, dm)
                            }
                        }
                        is(mram_op.MT_H) {
                            val dv = Vec(data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*2-1),mram_def.mram_base_width*1),
                                    data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*2-1),mram_def.mram_base_width*1))

                            val dms = MuxLookup(
                                off(1), 
                                UInt(0x0,mask_size),
                                Array(
                                    UInt(0,1) -> UInt(0x3,mask_size),
                                    UInt(1,1) -> UInt(0xC,mask_size)
                                )
                            )
                            val dm = dms.toBools
                            
                            when(hb === 1.U) {
                                ahmem.write(addr, dv, dm)
                            }
                            .otherwise {
                                amem.write(addr, dv, dm)
                            }
                        }
                        is(mram_op.MT_W) {
                            val dv = Vec(data((mram_def.mram_base_width*1-1),mram_def.mram_base_width*0),
                                    data((mram_def.mram_base_width*2-1),mram_def.mram_base_width*1),
                                    data((mram_def.mram_base_width*3-1),mram_def.mram_base_width*2),
                                    data((mram_def.mram_base_width*4-1),mram_def.mram_base_width*3))
                            val dms = UInt(0xF, mask_size)
                            val dm = dms.toBools
                            when(hb === 1.U) {
                                ahmem.write(addr, dv, dm)
                            }
                            .otherwise {
                                amem.write(addr, dv, dm)
                            }
                        }
                    }
                }
                is(mram_op.MF_RD) {
                    val addr = port.req.addr(addr_hi, addr_lo)
                    val off = port.req.addr(addr_lo-1, 0)
                    val mt = port.req.mtype
                    val hb = port.req.addr(31)
                    val wbd = port.resp.data
                    val wbv = port.resp.valid

                    val dv = Wire(Vec(mask_size, UInt(mram_def.mram_base_width.W)))
                    when(hb === 1.U) {
                        dv := ahmem.read(addr)
                    }
                    .otherwise {
                        dv := amem.read(addr)
                    }

                    switch(mt) {
                        is(mram_op.MT_B) {
                            val data = MuxLookup(
                                off, 
                                UInt(0x0,mram_def.mram_io_width),
                                Array(
                                    UInt(0,2) -> Cat(Fill(24, UInt(0,1)), dv(0)),
                                    UInt(1,2) -> Cat(Fill(24, UInt(0,1)), dv(1)),
                                    UInt(2,2) -> Cat(Fill(24, UInt(0,1)), dv(2)),
                                    UInt(3,2) -> Cat(Fill(24, UInt(0,1)), dv(3))
                                )
                            )
                            wbd := data.asUInt
                            wbv := true.B
                        }
                        is(mram_op.MT_H) {
                            val data = MuxLookup(
                                off(1), 
                                UInt(0x0,mram_def.mram_io_width),
                                Array(
                                    UInt(0,1) -> Cat(Fill(16, UInt(0,1)), dv(1), dv(0)),
                                    UInt(1,1) -> Cat(Fill(16, UInt(0,1)), dv(3), dv(2))
                                )
                            )
                            wbd := data.asUInt
                            wbv := true.B
                        }
                        is(mram_op.MT_W) {
                            val data = dv
                            wbd := data.asUInt
                            wbv := true.B
                        }
                    }
                }
            }
        }
    //}

    //Actually Initialized by reset
    when(reset.toBool()) {
        io.inst_port.req.ready := false.B
        io.inst_port.resp.valid := false.B
        io.inst_port.resp.data := 0.U
        io.data_port.req.ready := false.B
        io.data_port.resp.valid := false.B
        io.data_port.resp.data := 0.U
        io.dbg_port.req.ready := false.B
        io.dbg_port.resp.valid := false.B
        io.dbg_port.resp.data := 0.U
    }
}
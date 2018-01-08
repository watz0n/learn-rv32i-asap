//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// RISCV Debug Module 0.13
//=======================================================================
package rvcommon

import chisel3._
import chisel3.util._

trait dm_addr {
    val data0 = 0x04
    val datasz = (16-4) //0x04-0x0F
    val dmcontrol = 0x10
    val dmstatus = 0x11
    val hartinfo = 0x12
    val hartsum = 0x13
    val hawindowsel = 0x14
    val hawindow = 0x15
    val abstractcs = 0x16
    val command = 0x17
    val abstractauto = 0x18
    val devtreeaddr0 = 0x19
    val progbuf0 = 0x20
    val progbufsz = 16 //0x20-0x2F
    val authdata = 0x30
    val sbcs = 0x38
    val sbaddress0 = 0x39
    val sbaddresssz = 3 //0x39-0x3B
    val sbdata0 = 0x3C
    val sbdatasz = 4 //0x3C-0x3F
}

trait dm_def {
    val dataw = 32
    val addrw = 7
    val progbufsz = 4
    val datasz = 1
    val hartinfo = "h111bc0".U
}

trait dmi_reqop {
    val X = UInt(0, 2)
    val none = UInt(0, 2)
    val read = UInt(1, 2)
    val write = UInt(2, 2)
}

trait dmi_respop {
    val X = UInt(0, 2)
    val success = UInt(0, 2)
    val faliure = UInt(1, 2)
    val hwfaliure = UInt(2, 2)
    val reserved = UInt(3, 2)
}

object dm_addr extends dm_addr
object dm_def extends dm_def
object dmi_reqop extends dmi_reqop
object dmi_respop extends dmi_respop

// Inputs/Outputs

class dmi_req_bits_io extends Bundle {
    val op = Output(UInt(dmi_reqop.X.getWidth.W))
    val addr = Output(UInt(dm_def.addrw.W))
    val data = Output(UInt(dm_def.dataw.W))
}

class dmi_req_io extends Bundle {
    val bits = new dmi_req_bits_io()
    val valid = Output(Bool())
    val ready = Input(Bool())
}

class dmi_resp_bits_io extends Bundle {
    val resp = Output(UInt(dmi_respop.X.getWidth.W))
    val data = Output(UInt(dm_def.dataw.W))
}

class dmi_resp_io extends Bundle {
    val bits = new dmi_resp_bits_io()
    val valid = Output(Bool())
    val ready = Input(Bool())
}

class dmi_io extends Bundle {
    val req = new dmi_req_io()
    val resp = Flipped(new dmi_resp_io())
}

class ddpath_io extends Bundle {
    val addr = Output(UInt(5.W))
    val wdata = Output(UInt(rvspec.xlen.W))
    val valid = Output(Bool())
    val rdata = Input(UInt(rvspec.xlen.W))
    val rstpc = Output(Bool())
}

class dcpath_io extends Bundle {
    val halt = Output(Bool())
}

//Register represents

class debug_io extends Bundle {
    val dmi = Flipped(new dmi_io())
    val ddpath = new ddpath_io()
    val dcpath = new dcpath_io()
    val ddmem = new mram_io(rvspec.xlen)
    val rstcore = Output(Bool())
}

class dmstatusfd extends Bundle {
    val rsrvd1 = UInt(5.W)
    val dmerr = UInt(3.W)
    val rsrvd2 = UInt(1.W)
    val impebreak = Bool()
    val rsrvd3 = UInt(1.W)
    val allhavereset = Bool()
    val anyhavereset = Bool()
    val allresumeack = Bool()
    val allnonexistent = Bool()
    val anynonexistent = Bool()
    val allunavail = Bool()
    val anyunavail = Bool()
    val allrunning = Bool()
    val anyrunning = Bool()
    val allhalted = Bool()
    val anyhalted = Bool()
    val authenticated = Bool()
    val authbusy = Bool()
    val rsrvd4 = UInt(1.W)
    val devtreevalid = Bool()
    val version = UInt(4.W)
}

class dmcontrolfd extends Bundle {
    val haltreq = Bool()
    val resumereq = Bool()
    val hartreset = Bool()
    val ackharvereset = Bool()
    val rsrvd1 = UInt(1.W)
    val hasel = Bool()
    val hartsel = UInt(10.W)
    val rsrvd2 = UInt(14.W)
    val ndmreset = Bool()
    val dmactive = Bool()
}

class hartinfofd extends Bundle {
    val rsrvd1 = UInt(8.W)
    val nscratch = UInt(4.W)
    val rsrvd2 = UInt(3.W)
    val dataaccess = Bool()
    val datasize = UInt(4.W)
    val dataaddr = UInt(12.W)
}

class hawindowselfd extends Bundle {
    val rsrvd1 = UInt(27.W)
    val hawindowsel = UInt(5.W)
}

class abstractcsfd extends Bundle {
    val rsrvd1 = UInt(3.W)
    val progbufsize = UInt(5.W)
    val rsrvd2 = UInt(11.W)
    val busy = Bool()
    val rsrvd3 = UInt(1.W)
    val cmderr = UInt(3.W)
    val rsrvd4 = UInt(3.W)
    val datacount = UInt(5.W)
}

class commandfd extends Bundle {
    val cmdtype = UInt(8.W)
    val rsrvd1 = UInt(1.W)
    val size = UInt(3.W)
    val rsrvd2 = UInt(1.W)
    val postexec = Bool()
    val transfer = Bool()
    val write = Bool()
    val regno = UInt(16.W)
}

class abstractautofd extends Bundle {
    val autoexecprogbuf = UInt(16.W)
    val rsrvd1 = UInt(4.W)
    val autoexecdata = UInt(12.W)
}

class sbcsfd extends Bundle {
    val rsrvd1 = UInt(11.W)
    val sbsingleread = Bool()
    val sbaccess = UInt(3.W)
    val sbautoincrement = Bool()
    val sbautoread = Bool()
    val sberror = UInt(3.W)
    val sbasize = UInt(7.W)
    val sbaccess128 = Bool()
    val sbaccess64 = Bool()
    val sbaccess32 = Bool()
    val sbaccess16 = Bool()
    val sbaccess8 = Bool()
}

class rvdm extends Module {
    val io = IO(new debug_io())
    //DontCare for Initialized Error
    io <> DontCare

    var i=0; //for loop variable

    val reg_dmstatus = Reg(UInt(rvspec.xlen.W))
    val reg_dmcontrol = Reg(UInt(rvspec.xlen.W))
    val reg_sbcs = Reg(UInt(rvspec.xlen.W))
    val reg_abstractcs = Reg(UInt(rvspec.xlen.W))
    val reg_command = Reg(UInt(rvspec.xlen.W))
    val reg_progbuf = Reg(Vec(dm_addr.progbufsz, UInt(rvspec.xlen.W)))
    val reg_data = Reg(Vec(dm_addr.datasz, UInt(rvspec.xlen.W)))

    val reg_sbaddress = Reg(Vec(dm_addr.sbaddresssz, UInt(rvspec.xlen.W)))
    val reg_sbdata = Reg(Vec(dm_addr.sbdatasz, UInt(rvspec.xlen.W)))

    val reg_regrw = Reg(Bool())

    val reg_mrdata_valid = Reg(Bool())
    val reg_mrdata_requested = Reg(Bool())
    val delay_mrdata_valid = Reg(Bool())

    val reg_mwdata_requested = Reg(Bool())

    val reg_rstcore = Reg(Bool())

    val reg_map = collection.mutable.LinkedHashMap[Int,UInt](
        dm_addr.dmcontrol -> reg_dmcontrol,
        dm_addr.dmstatus -> reg_dmstatus,
        dm_addr.hartinfo -> dm_def.hartinfo,
        dm_addr.hartsum -> 0.U,
        dm_addr.hawindowsel -> 0.U,
        dm_addr.hawindow -> 0.U,
        dm_addr.abstractcs -> reg_abstractcs,
        dm_addr.command -> reg_command,
        dm_addr.abstractauto -> 0.U,
        dm_addr.devtreeaddr0 -> 0.U,
        dm_addr.authdata -> 0.U,
        dm_addr.sbcs -> reg_sbcs
    )
    for (i <- (0 to dm_addr.datasz-1)) {
        reg_map += (dm_addr.data0+i) -> reg_data(i)
    }
    for (i <- (0 to dm_addr.progbufsz-1)) {
        reg_map += (dm_addr.progbuf0+i) -> reg_progbuf(i)
    }
    for (i <- (0 to dm_addr.sbaddresssz-1)) {
        reg_map += (dm_addr.sbaddress0+i) -> reg_sbaddress(i)
    }
    for (i <- (0 to dm_addr.sbdatasz-1)) {
        reg_map += (dm_addr.sbdata0+i) -> reg_sbdata(i)
    }

//Default status
    io.ddmem.req.valid := false.B
    io.ddpath.valid := false.B

    io.dmi.req.ready := io.dmi.req.valid
    io.dmi.resp.bits.resp := dmi_respop.success

    val sel_map = reg_map map { case (k,v) => k -> (io.dmi.req.bits.addr === UInt(k, dm_def.addrw)) }
    val mux_arr = (for ((k, v) <- reg_map) yield (UInt(k, dm_def.addrw), v)).toArray
    io.dmi.resp.bits.data := MuxLookup(io.dmi.req.bits.addr, 0.U, mux_arr)

    val dcsr_wdata = io.dmi.req.bits.data

    //val dmstatus = (new dmstatusfd).fromBits(reg_dmstatus)
    //val dmcontrol = (new dmcontrolfd).fromBits(reg_dmcontrol)
    //val sbcs = (new sbcsfd).fromBits(reg_sbcs)
    //val abstractcs = (new abstractcsfd).fromBits(reg_abstractcs)
    //val command = (new commandfd).fromBits(reg_command)
    
    val dmstatus = Wire(new dmstatusfd)
    dmstatus := (new dmstatusfd).fromBits(reg_dmstatus)
    val dmcontrol = Wire(new dmcontrolfd)
    dmcontrol := (new dmcontrolfd).fromBits(reg_dmcontrol)
    val sbcs = Wire(new sbcsfd)
    sbcs := (new sbcsfd).fromBits(reg_sbcs)
    val abstractcs = Wire(new abstractcsfd)
    abstractcs := (new abstractcsfd).fromBits(reg_abstractcs)
    val command = Wire(new commandfd)
    command := (new commandfd).fromBits(reg_command)

    dmstatus.allhalted := dmcontrol.haltreq
    dmstatus.allrunning := dmcontrol.resumereq
    reg_dmstatus := dmstatus.asUInt

    io.dcpath.halt := dmstatus.allhalted && !dmstatus.allrunning


    when(io.dmi.req.bits.op === dmi_reqop.write) {
        when(sel_map(dm_addr.abstractcs) & io.dmi.req.valid) {
            val new_abstractcs = (new abstractcsfd).fromBits(dcsr_wdata)
            abstractcs.cmderr := new_abstractcs.cmderr
            reg_abstractcs := abstractcs.asUInt
        }

        when(sel_map(dm_addr.command)) {
            val new_command = (new commandfd).fromBits(dcsr_wdata)
            when(new_command.size === 2.U) {
                command.postexec := new_command.postexec
                command.regno := new_command.regno
                command.transfer := new_command.transfer
                command.write := new_command.write
                reg_command := command.asUInt
                when(new_command.transfer) {
                    reg_regrw := true.B
                    abstractcs.cmderr := 0.U
                }
                .otherwise {
                    reg_regrw := false.B
                    abstractcs.cmderr := 1.U
                }
                reg_abstractcs := abstractcs.asUInt
            }
            .otherwise {
                abstractcs.cmderr := 2.U
                reg_abstractcs := abstractcs.asUInt
                reg_regrw := false.B
            }
        }
        
        when(sel_map(dm_addr.dmcontrol)) {
            val new_dmcontrol = (new dmcontrolfd).fromBits(dcsr_wdata)
            dmcontrol.haltreq := new_dmcontrol.haltreq
            dmcontrol.resumereq := new_dmcontrol.resumereq
            dmcontrol.hartreset := new_dmcontrol.hartreset
            dmcontrol.ndmreset := new_dmcontrol.ndmreset
            dmcontrol.dmactive := new_dmcontrol.dmactive
            reg_dmcontrol := dmcontrol.asUInt
        }

        when(sel_map(dm_addr.sbcs))  {
            val new_sbcs = (new sbcsfd).fromBits(dcsr_wdata)
            sbcs.sbsingleread := new_sbcs.sbsingleread
            sbcs.sbaccess := new_sbcs.sbaccess
            sbcs.sbautoincrement := new_sbcs.sbautoincrement
            sbcs.sbautoread := new_sbcs.sbautoread
            sbcs.sberror := new_sbcs.sberror
            reg_sbcs := sbcs.asUInt
        }

        for (i <- (0 to dm_addr.sbaddresssz-1)) {
            when(sel_map(dm_addr.sbaddress0+i)) {
                reg_sbaddress(i) := dcsr_wdata
            }
        }

        //Only implement function for sbdata0
        when(sel_map(dm_addr.sbdata0)) {
            reg_sbdata(0) := dcsr_wdata
            io.ddmem.req.mfunc := mram_op.MF_WR
            io.ddmem.req.mtype := mram_op.MT_W
            io.ddmem.req.addr := reg_sbaddress(0)
            io.ddmem.req.data := reg_sbdata(0)
            io.ddmem.req.valid := io.dmi.req.valid
            when(sbcs.sbautoincrement & io.dmi.req.valid) {
                reg_mwdata_requested := true.B
            }
            when(reg_mwdata_requested & io.dmi.req.valid) {
                reg_sbaddress(0) := reg_sbaddress(0) + 4.U
            }
        }

        for (i <- (0 to dm_addr.datasz-1)) {
            when(sel_map(dm_addr.data0+i)) {
                reg_data(i) := dcsr_wdata
            }
        }
    }

    //abstract command, register access
    io.ddpath.valid := false.B
    io.ddpath.addr := (command.regno & 0x1F.U)(4,0) //Only use 5 bits, not follow spec.
    //val cmd_regrw = command.transfer & (abstractcs.cmderr =/= 0.U)
    val cmd_regrw = (command.transfer & reg_regrw)
    when(cmd_regrw) {
        when(command.write) {
            io.ddpath.wdata := reg_data(0)
            io.ddpath.valid := true.B
        }
        .otherwise {
            reg_data(0) := io.ddpath.rdata
        }

        reg_regrw := false.B
        //abstractcs.cmderr := 0.U //Produce combinational loop
        //reg_abstractcs := abstractcs.asUInt
    }

    //memory access

    //reg_mrdata_valid := false.B
    //reg_mrdata_requested := false.B

    when((io.dmi.req.bits.op === dmi_reqop.read&sel_map(dm_addr.sbdata0))|(sbcs.sbautoread & reg_mrdata_requested)) {
        //Only implement function for sbdata0
        reg_sbdata(0) := dcsr_wdata
        io.ddmem.req.mfunc := mram_op.MF_RD
        io.ddmem.req.mtype := mram_op.MT_W
        io.ddmem.req.addr := reg_sbaddress(0)
        io.ddmem.req.valid := io.dmi.req.valid
        when(io.ddmem.resp.valid) {
            reg_sbdata(0) := io.ddmem.resp.data
        }
        reg_mrdata_valid := true.B
        reg_mrdata_requested := true.B

        when(reg_mrdata_valid & io.ddmem.resp.valid) {
            reg_sbdata(0) := io.ddmem.resp.data
            reg_mrdata_valid := true.B
            when(sbcs.sbautoincrement) {
                reg_sbaddress(0) := reg_sbaddress(0) + 4.U
            }
        }
    }

    

    when(!(sel_map(dm_addr.sbdata0))) {
        reg_mrdata_requested := false.B
        reg_mwdata_requested := false.B
    }

    delay_mrdata_valid := io.ddmem.resp.valid //delay valid state when data not immediately ready 
    io.dmi.resp.valid := Mux(reg_mrdata_requested, delay_mrdata_valid, io.dmi.req.valid)
    //io.dmi.resp.valid := io.dmi.req.valid 

    io.rstcore := reg_rstcore

    //Custom Function from Sodor Core
    when((io.dmi.req.bits.addr === "h44".U) && io.dmi.req.valid){
        reg_rstcore := false.B
    }

    //Actually Initialized by reset
    when(reset.toBool()) {

        //reg_dmstatus := 0.U
        reg_dmcontrol := 0.U
        reg_sbcs := 0.U
        //reg_abstractcs := 0.U
        reg_command := 0.U
        reg_progbuf(0) := 0.U
        reg_data(0) := 0.U
        reg_sbaddress(0) := 0.U
        reg_sbdata(0) := 0.U

        io.ddmem.req.valid := false.B
        io.ddpath.valid := false.B

        dmstatus.authenticated := true.B
        dmstatus.version := 2.U
        reg_dmstatus := dmstatus.asUInt
        
        sbcs.sbaccess := 2.U
        sbcs.sbasize := 32.U
        sbcs.sbaccess32 := true.B
        reg_sbcs := sbcs.asUInt

        abstractcs.datacount := dm_addr.datasz.U
        abstractcs.progbufsize := dm_addr.progbufsz.U
        reg_abstractcs := abstractcs.asUInt

        reg_mrdata_valid := false.B
        reg_mrdata_requested  := false.B
        delay_mrdata_valid := false.B

        reg_mwdata_requested := false.B
        
        io.dmi.resp.bits.resp := dmi_respop.hwfaliure

        reg_regrw := false.B
        reg_rstcore := true.B
    }
}
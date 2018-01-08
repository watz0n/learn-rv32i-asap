//=======================================================================
// RISCV, A Simple As Possible Core
// Watson Huang
// Jan 2, 2018
// 
// RISCV Privilege CSR functions 1.10, M-Mode and Debug CSR
//=======================================================================
package rvcommon

import chisel3._
import chisel3.util._

trait csr_def {
    val addrsz = 12
    val timesz = 64
    val addrh = 31
    val addrl = 20
}

trait csr_op {
    val X = UInt(0, 3) //Not in Write/Set/Clear or Instruction mode
    val W = UInt(1, 3)
    val S = UInt(2, 3)
    val C = UInt(3, 3)
    val I = UInt(4, 3)
}

trait mcsr_addr {
    val mvendorid = 0xF11
    val marchid = 0xF12
    val mimpid = 0xF13
    val mhartid = 0xF14

    val mstatus = 0x300
    val misa = 0x301
    val medeleg = 0x302
    val mideleg = 0x303
    val mie = 0x304
    val mtvec = 0x305
    val mcounteren = 0x306

    val mscratch = 0x340
    val mepc = 0x341
    val mcause = 0x342
    val mtval = 0x343
    val mip = 0x344

    //Not implement PMP yet

    //Performance counter
    val mcycle = 0xB00
    val minstret = 0xB02
    val mhpmcounter_start = 0xB03
    val mhpmcounter_number = (31-3+1)
    val mcycleh = 0xB80
    val minstreth = 0xB82
    val mhpmcounterh_start = 0xB83
    //val mhpmcounterh_number = (31-3+1)
    //Event counter
    val mhpmevent_start = 0x323
    val mhpmevent_number = (31-3+1)

    //Reserved region, but should be here by user-level CSR
    val mtime = 0xB01
    val mtimeh = 0xB81
    val mtimecmp = 0xB20 //after mhpmcounter31
    val mtimecmph = 0xBA0 //after mhpmcounter31h
}

trait dcsr_addr {
    val dcsr = 0x7B0
    val dpc = 0x7B1
    val dscratch0 = 0x7B2
    val dscratch1 = 0x7B3
}

object csr_def  extends csr_def
object csr_op   extends csr_op
object csr_addr extends mcsr_addr with
                        dcsr_addr

//mstatus FielD
//Ref: https://github.com/freechipsproject/chisel3/wiki/Cookbook#how-do-i-create-a-bundle-from-a-uint
//From reference, lower fields (up-to-down, down is lower than up) represent lower bits group
class mstatusfd extends Bundle { 
    val SD = Bool()
    val rsrvd1 = UInt((30-23+1).W)
    val TSR = Bool()
    val TW = Bool()
    val TVM = Bool()
    val MXR = Bool()
    val SUM = Bool()
    val MPRV = Bool()
    val XS = UInt(2.W)
    val FS = UInt(2.W)
    val MPP = UInt(2.W)
    val rsrvd2 = UInt(2.W)
    val SPP = Bool()
    val MPIE = Bool()
    val rsrvd3 = UInt(1.W)
    val SPIE = Bool()
    val UPIE = Bool()
    val MIE = Bool()
    val rsrvd4 = UInt(1.W)
    val SIE = Bool()
    val UIE = Bool()
}

class mipfd extends Bundle {
    val rsrvd1 = UInt((30-12+1).W)
    val MEIP = Bool()
    val rsrvd2 = UInt(1.W)
    val SEIP = Bool()
    val UEIP = Bool()
    val MTIP = Bool()
    val rsrvd3 = UInt(1.W)
    val STIP = Bool()
    val UTIP = Bool()
    val MSIP = Bool()
    val rsrvd4 = UInt(1.W)
    val SSIP = Bool()
    val USIP = Bool()
}

class miefd extends Bundle {
    val rsrvd1 = UInt((30-12+1).W)
    val MEIE = Bool()
    val rsrvd2 = UInt(1.W)
    val SEIE = Bool()
    val UEIE = Bool()
    val MTIE = Bool()
    val rsrvd3 = UInt(1.W)
    val STIE = Bool()
    val UTIE = Bool()
    val MSIE = Bool()
    val rsrvd4 = UInt(1.W)
    val SSIE = Bool()
    val USIE = Bool()
}

class mcounterenfd extends Bundle {
    val HPM = Vec(31-3+1, Bool())
    val IR = Bool()
    val TM = Bool()
    val CY = Bool()
}

class dcsrfd extends Bundle {
    val xdebugver = UInt(4.W)
    val rsrvd1 = UInt(12.W)
    val ebreakm = Bool()
    val ebreakh = Bool()
    val ebreaks = Bool()
    val ebreaku = Bool()
    val rsrvd2 = UInt(1.W)
    val stopcount = Bool()
    val stoptime = Bool()
    val cause = UInt(3.W)
    val rsrvd3 = UInt(3.W)
    val step = Bool()
    val prv = UInt(2.W)
}

class csr_io extends Bundle {
    // Read/Write Interface
    val addr = Input(UInt(csr_def.addrsz.W))
    val wdata = Input(UInt(rvspec.xlen.W))
    val rdata = Output(UInt(rvspec.xlen.W))
    val op = Input(UInt(csr_op.X.getWidth.W))

    val core_inst = Input(UInt(rvspec.xlen.W))
    val core_pc = Input(UInt(rvspec.xlen.W))
    val core_excp = Input(Bool())
    val core_cause = Input(UInt(rvcause.X.getWidth.W))
    val csr_excp = Output(Bool())
    val csr_evec = Output(UInt(rvspec.xlen.W))
}

class rvcsr extends Module {
    
    var i = 0; //var to for loop count
    val io = IO(new csr_io)
    //DontCare for Initialized Error
    io <> DontCare

    val reg_priv = Reg(UInt(rvpriv.X.getWidth.W))

    val reg_mstatus = Reg(UInt(rvspec.xlen.W))
    val reg_mtvec = Reg(UInt(rvspec.xlen.W))
    val reg_medeleg = Reg(UInt(rvspec.xlen.W))
    val reg_mideleg = Reg(UInt(rvspec.xlen.W))
    val reg_mip = Reg(UInt(rvspec.xlen.W))
    val reg_mie = Reg(UInt(rvspec.xlen.W))
    val reg_mtime = Reg(UInt(csr_def.timesz.W)) //64b
    //val reg_mtimecmp = Reg(UInt(rvspec.xlen.W)) //cmp data address
    val reg_mtimecmp = Reg(UInt(csr_def.timesz.W)) //64b, change to buildin
    val reg_mcycle = Reg(UInt(csr_def.timesz.W)) //64b
    val reg_minstret = Reg(UInt(csr_def.timesz.W)) //64b
    //Ref: https://github.com/freechipsproject/chisel3/wiki/Cookbook#how-do-i-create-a-vector-of-registers
    val reg_mhpmcounters = Reg(Vec(csr_addr.mhpmcounter_number, UInt(csr_def.timesz.W))) //64b
    val reg_mhpmevents = Reg(Vec(csr_addr.mhpmevent_number, UInt(rvspec.xlen.W)))

    val reg_mcounteren = Reg(UInt(rvspec.xlen.W))
    val reg_mscratch = Reg(UInt(rvspec.xlen.W))
    val reg_mepc = Reg(UInt(rvspec.xlen.W))
    val reg_mcause = Reg(UInt(rvspec.xlen.W))
    val reg_mtval = Reg(UInt(rvspec.xlen.W))

    val reg_debug = Reg(Bool())

    val reg_dcsr = Reg(UInt(rvspec.xlen.W))
    val reg_dpc = Reg(UInt(rvspec.xlen.W))
    val reg_dscratch0 = Reg(UInt(rvspec.xlen.W))
    val reg_dscratch1 = Reg(UInt(rvspec.xlen.W))

//Pre-defined values
    val mvendorid = rvspec.vendorid.U
    val marchid = rvspec.archid.U
    val mimpid = rvspec.impid.U
    val mhartid = rvspec.hartid.U

    //Calculate Extension bit from String
    //Get Byte in string, Ref: https://alvinalexander.com/scala/how-to-process-characters-in-string-map-for-yield-foreach
    //Process each element, Ref: https://alvinalexander.com/scala/scala-for-loop-yield-examples-yield-tutorial
    //Reduce collection, Ref: https://alvinalexander.com/scala/how-to-walk-scala-collections-reduceleft-foldright-cookbook
    val mext = (for (c <- rvspec.misa.getBytes) yield 0x01<<(c-'A')).reduceLeft(_|_) 
    val mxl = "h40000000".U //From Volume II spec. Table 3.1, @xlen=32
    val misa = mxl|UInt(mext, rvspec.xlen)

//CSR Register address to register/value mapping    
    //Using map function to simplify fetch data by address
    //Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/common/csr.scala
    val reg_map = collection.mutable.LinkedHashMap[Int,UInt](
        csr_addr.mvendorid -> mvendorid,
        csr_addr.marchid -> marchid,
        csr_addr.mimpid -> mimpid,
        csr_addr.mhartid -> mhartid,

        csr_addr.mstatus -> reg_mstatus,
        csr_addr.misa -> misa,
        csr_addr.medeleg -> reg_medeleg,
        csr_addr.mideleg -> reg_mideleg,
        csr_addr.mie -> reg_mie,
        csr_addr.mtvec -> reg_mtvec,
        csr_addr.mcounteren -> reg_mcounteren,

        csr_addr.mscratch -> reg_mscratch,
        csr_addr.mepc -> reg_mepc,
        csr_addr.mcause -> reg_mcause,
        csr_addr.mtval -> reg_mtval,
        csr_addr.mip -> reg_mip,

        csr_addr.mcycle -> reg_mcycle(31,0),
        csr_addr.minstret -> reg_minstret(63, 32),
        csr_addr.mcycleh -> reg_mcycle(31, 0),
        csr_addr.minstreth -> reg_minstret(63,32),
        
        csr_addr.mtime -> reg_mtime(31,0),
        csr_addr.mtimecmp -> reg_mtimecmp(63, 32),
        csr_addr.mtimeh -> reg_mtime(63, 32),
        csr_addr.mtimecmph -> reg_mtimecmp(63, 32),

        csr_addr.dcsr -> reg_dcsr,
        csr_addr.dpc -> reg_dpc,
        csr_addr.dscratch0 -> reg_dscratch0,
        csr_addr.dscratch1 -> reg_dscratch1

    )
    
    //Use map += syntax to add sequential CSRs
    //Ref: https://stackoverflow.com/questions/3993613/what-is-the-syntax-for-adding-an-element-to-a-scala-collection-mutable-map
    for (i <- 0 to (csr_addr.mhpmcounter_number-1)) {
        reg_map += (csr_addr.mhpmcounter_start+i) -> reg_mhpmcounters(i)(31,0)
        reg_map += (csr_addr.mhpmcounterh_start+i) -> reg_mhpmcounters(i)(63,32)
    }
    for (i <- 0 to (csr_addr.mhpmevent_number-1)) {
        reg_map += (csr_addr.mhpmevent_start+i) -> reg_mhpmevents(i)
    }

//Increase Counters
    val counteren = (new mcounterenfd).fromBits(reg_mcounteren)

    when(counteren.CY) { //mCYcle
        reg_mcycle := reg_mcycle + 1.U
    }

    when(counteren.TM) { //mTiMe
        reg_mtime := reg_mtime + 1.U
    }

    when(counteren.IR) { //mInstRet
        reg_minstret := reg_minstret + 1.U
    }

    for (i <- 0 to (csr_addr.mhpmcounter_number-1)) {
        when(counteren.HPM(i)) {
            reg_mhpmcounters(i) := reg_mhpmcounters(i) + 1.U
        }
    }

// Privilege status
    val priv = Wire(UInt(rvpriv.X.getWidth.W))
    priv := reg_priv
    
    //val mstatus = (new mstatusfd).fromBits(reg_mstatus)
    //val mie = (new miefd).fromBits(reg_mie)
    //val mip = (new mipfd).fromBits(reg_mip)
    //val dcsr = (new dcsrfd).fromBits(reg_dcsr)
    
    val mstatus = Wire(new mstatusfd)
    mstatus := (new mstatusfd).fromBits(reg_mstatus)
    val mie = Wire(new miefd)
    mie := (new miefd).fromBits(reg_mie)
    val mip = Wire(new mipfd)
    mip := (new mipfd).fromBits(reg_mip)
    val dcsr = Wire(new dcsrfd)
    dcsr := (new dcsrfd).fromBits(reg_dcsr)

    val csr_priv = io.addr(9, 8)
    val legal_priv = (priv >= csr_priv)
    val priv_inst = (io.op === csr_op.I)
    val priv_inst_type = io.addr(2,0)
    val inst_ecall = (legal_priv&priv_inst)&(priv_inst_type === UInt(0,3))
    val inst_ebreak = (legal_priv&priv_inst)&(priv_inst_type === UInt(1,3))
    val inst_ret = (legal_priv&priv_inst)&(priv_inst_type === UInt(2,3))
    //val inst_wfi = (legal_priv&priv_inst)&(priv_inst_type === UInt(5,3))
    
    io.csr_excp := inst_ecall | inst_ebreak | inst_ret

    // Timer interrupt
    when (reg_mtime >= reg_mtimecmp) {
        mip.MTIP := true.B //Note: not work now
        reg_mip := mip.asUInt
    }

    //MRET
    when (inst_ret & (csr_priv === rvpriv.M) & (io.addr(10) === 0.U)  ) {
        mstatus.MIE := mstatus.MPIE
        mstatus.MPIE := true.B
        reg_priv := mstatus.MPP
        io.csr_evec := reg_mepc
        reg_mstatus := mstatus.asUInt
    }

    //ECALL 
    when(inst_ecall){
        reg_mcause := rvcause.mecall
        io.csr_evec := "h80000004".U
    }

    //EBREAK
    when(inst_ebreak){
        reg_mcause := rvcause.breakpoint
        io.csr_evec := "h80000004".U
        when(dcsr.ebreakm) {
            reg_debug := true.B //Note: not work now
        }
    }

    //DRET
    when (inst_ret & (csr_priv === rvpriv.M) & (io.addr(10) === 1.U) ) {
        reg_debug := false.B
        reg_priv := dcsr.prv
        io.csr_evec := reg_dpc
    }

//Exceptions
    when (io.core_excp) {
        switch(io.core_cause) {
            is(rvcause.illegal_inst) {
                reg_mcause := io.core_cause
                reg_mepc := io.core_pc
                io.csr_evec := "h80000004".U
            }
        }
    }

// CSR Registers Read/Writer
    //Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/common/csr.scala , Line258
    val sel_map = reg_map map { case (k,v) => k -> (io.addr === UInt(k, csr_def.addrsz)) }

    val read_only = io.addr(11)&io.addr(10)
    val csr_inst = (io.op === csr_op.I)
    val csr_wen = (!read_only)&(!csr_inst)&legal_priv

    //Ref: https://github.com/ucb-bar/riscv-sodor/blob/master/src/common/csr.scala , Line 328
    //use MuxLookup adhere, not Mux1H
    val mux_arr = (for ((k, v) <- reg_map) yield (UInt(k, csr_def.addrsz), v)).toArray
    io.rdata := Mux(legal_priv, MuxLookup(io.addr, 0.U, mux_arr), 0.U)
    
    val csr_wdata = Wire(UInt(rvspec.xlen.W))
    csr_wdata := (Mux((io.op =/= csr_op.W), io.rdata, 0.U) | Mux((io.op =/= csr_op.C), io.wdata, 0.U)) & (~Mux((io.op === csr_op.C), io.wdata, 0.U))
    when(csr_wen) {

        when(sel_map(csr_addr.mstatus)) {
            val new_mstatus = (new mstatusfd).fromBits(csr_wdata)
            mstatus.MIE := new_mstatus.MIE
            mstatus.MPIE := new_mstatus.MPIE
            reg_mstatus := mstatus.asUInt
        }

        when(sel_map(csr_addr.mie)) {
            val new_mie = (new miefd).fromBits(csr_wdata)
            //mie.MEIE := new_mie.MEIE //External interrupt, implement with PLIC, 
            mie.MTIE := new_mie.MTIE //Time interrupt
            mie.MSIE := new_mie.MSIE //Software interrupt
            reg_mie := mie.asUInt
        }

        when(sel_map(csr_addr.mtvec)) {
            reg_mtvec := Cat(csr_wdata(31,2), Fill(2,UInt(0,1))) //Force to Direct mode
        }

        when(sel_map(csr_addr.mcounteren)) {
            reg_mcounteren := csr_wdata
        }

        when(sel_map(csr_addr.mscratch)) {
            reg_mscratch := csr_wdata
        }

        when(sel_map(csr_addr.mepc)) {
            reg_mepc := Cat(csr_wdata(31,2), Fill(2,UInt(0,1))) //Align to 4byte address segment
        }

        when(sel_map(csr_addr.mcause)) {
            reg_mcause := csr_wdata
        }

        when(sel_map(csr_addr.mtval)) {
            reg_mtval := csr_wdata
        }

        when(sel_map(csr_addr.mip)) {
            val new_mip = (new mipfd).fromBits(csr_wdata)
            //mip.MEIP := new_mip.MEIP //External pending, implement with PLIC, 
            mip.MTIP := new_mip.MTIP //Time pending
            mip.MSIP := new_mip.MSIP //Software pending
            reg_mip := mip.asUInt
        }

        when(sel_map(csr_addr.mcycle)) {
            //reg_mcycle(31,0) := csr_wdata //produce error, could not reassign read-only UInt
            reg_mcycle := Cat(reg_mcycle(63, 32), csr_wdata)
        }

        when(sel_map(csr_addr.minstret)) {
            //reg_minstret(31,0) := csr_wdata //produce error, could not reassign read-only UInt
            reg_minstret := Cat(reg_minstret(63, 32), csr_wdata)
        }
        
        when(sel_map(csr_addr.mcycleh)) {
            //reg_mcycle(63,32) := csr_wdata //produce error, could not reassign read-only UInt
            reg_mcycle := Cat(csr_wdata, reg_mcycle(31, 0))
        }

        when(sel_map(csr_addr.minstreth)) {
            //reg_minstret(63,32) := csr_wdata //produce error, could not reassign read-only UInt
            reg_minstret := Cat(csr_wdata, reg_minstret(31, 0))
        }

        for (i <- 0 to (csr_addr.mhpmcounter_number-1)) {
            when(sel_map(csr_addr.mhpmcounter_start+i)) {
                //reg_mhpmcounters(i)(31,0) := csr_wdata //produce error, could not reassign read-only UInt
                reg_mhpmcounters(i) := Cat(reg_mhpmcounters(i)(63,32), csr_wdata)
            }
        }

        for (i <- 0 to (csr_addr.mhpmcounter_number-1)) {
            when(sel_map(csr_addr.mhpmcounterh_start+i)) {
                //reg_mhpmcounters(i)(63,32) := csr_wdata //produce error, could not reassign read-only UInt
                reg_mhpmcounters(i) := Cat(csr_wdata, reg_mhpmcounters(i)(31,0))
            }
        }

        for (i <- 0 to (csr_addr.mhpmevent_number-1)) {
            when(sel_map(csr_addr.mhpmevent_start+i)) {
                reg_mhpmevents(i) := csr_wdata
            }
        }

        when(sel_map(csr_addr.mtime)) {
            //reg_mtime(31,0) := csr_wdata //produce error, could not reassign read-only UInt
            reg_mtime := Cat(reg_mtime(63,32), csr_wdata)
        }

        when(sel_map(csr_addr.mtimecmp)) {
            //reg_mtimecmp(31,0) := csr_wdata //produce error, could not reassign read-only UInt
            reg_mtimecmp := Cat(reg_mtimecmp(63,32), csr_wdata)
        }
        
        when(sel_map(csr_addr.mtimeh)) {
            //reg_mtime(63,32) := csr_wdata //produce error, could not reassign read-only UInt
            reg_mtime := Cat(csr_wdata, reg_mtime(31,0))
        }

        when(sel_map(csr_addr.mtimecmph)) {
            //reg_mtimecmp(63,32) := csr_wdata //produce error, could not reassign read-only UInt
            reg_mtimecmp := Cat(csr_wdata, reg_mtimecmp(31,0))
        }
        
        when(sel_map(csr_addr.medeleg)) {
            reg_medeleg := csr_wdata
        }

        when(sel_map(csr_addr.mideleg)) {
            reg_mideleg := csr_wdata
        }

        when(sel_map(csr_addr.dcsr)) {
            val new_dcsr = (new dcsrfd).fromBits(csr_wdata)
            dcsr.step := new_dcsr.step
            dcsr.ebreakm := new_dcsr.ebreakm
            reg_dcsr := dcsr.asUInt
        }

        when(sel_map(csr_addr.dpc)) {
            reg_dpc := csr_wdata
        }

        when(sel_map(csr_addr.dscratch0)) {
            reg_dscratch0 := csr_wdata
        }

        when(sel_map(csr_addr.dscratch1)) {
            reg_dscratch1 := csr_wdata
        }
    }

    //Actually Initialized by reset
    when(reset.toBool()) {
        
        reg_priv := rvpriv.M 

        //reg_mstatus := 0.U
        //reconfigure data in fields
        mstatus := (new mstatusfd).fromBits(0.U)
        mstatus.MIE := false.B
        mstatus.MPRV := false.B
        mstatus.MPP := rvpriv.M
        reg_mstatus := mstatus.asUInt

        //reg_mtvec := 0.U
        reg_mtvec := rvrst.MTVEC
        reg_medeleg := 0.U
        reg_mideleg := 0.U
        reg_mip := 0.U
        reg_mie := 0.U
        reg_mtime := 0.U
        reg_mtimecmp := 0.U
        reg_mcycle := 0.U
        reg_minstret := 0.U

        //Reset counters
        for(i <- 0 until csr_addr.mhpmcounter_number) {
            reg_mhpmcounters(i) := 0.U
        }
        for(i <- 0 until csr_addr.mhpmevent_number) {
            reg_mhpmevents(i) := 0.U
        }

        reg_mcounteren := 0.U
        reg_mscratch := 0.U
        reg_mepc := 0.U
        reg_mcause := 0.U
        reg_mtval := 0.U

        reg_debug := false.B

        //reg_dcsr := 0.U
        dcsr := (new dcsrfd).fromBits(0.U)
        dcsr.xdebugver := UInt(4,4)
        dcsr.prv := rvpriv.M
        reg_dcsr := dcsr.asUInt
        reg_dpc := 0.U
        reg_dscratch0 := 0.U
        reg_dscratch1 := 0.U
    }
}

// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Primary design header
//
// This header should be included by all source files instantiating the design.
// The class here is then constructed to instantiate the design.
// See the Verilator manual for examples.

#ifndef _Vrvtop_H_
#define _Vrvtop_H_

#include "verilated_heavy.h"
#include "Vrvtop__Dpi.h"

class Vrvtop__Syms;
class Vrvtop___024unit;

//----------

VL_MODULE(Vrvtop) {
  public:
    // CELLS
    // Public to allow access to /*verilator_public*/ items;
    // otherwise the application code can consider these internals.
    Vrvtop___024unit*  	__PVT____024unit;
    
    // PORTS
    // The application code writes and reads these signals to
    // propagate new values into/out from the Verilated model.
    VL_IN8(clock,0,0);
    VL_IN8(reset,0,0);
    VL_OUT8(io_success,0,0);
    VL_IN8(io_dbg_sel_unit,0,0);
    VL_IN8(io_rfdbg_req_mfunc,0,0);
    VL_IN8(io_rfdbg_req_mtype,1,0);
    VL_IN8(io_rfdbg_req_valid,0,0);
    VL_OUT8(io_rfdbg_req_ready,0,0);
    VL_OUT8(io_rfdbg_resp_valid,0,0);
    VL_IN8(io_mrdbg_req_mfunc,0,0);
    VL_IN8(io_mrdbg_req_mtype,1,0);
    VL_IN8(io_mrdbg_req_valid,0,0);
    VL_OUT8(io_mrdbg_req_ready,0,0);
    VL_OUT8(io_mrdbg_resp_valid,0,0);
    VL_IN8(io_rst_core,0,0);
    //char	__VpadToAlign15[1];
    VL_IN(io_rfdbg_req_addr,31,0);
    VL_IN(io_rfdbg_req_data,31,0);
    VL_OUT(io_rfdbg_resp_data,31,0);
    VL_IN(io_mrdbg_req_addr,31,0);
    VL_IN(io_mrdbg_req_data,31,0);
    VL_OUT(io_mrdbg_resp_data,31,0);
    
    // LOCAL SIGNALS
    // Internals; generally not touched by application code
    VL_SIG8(rvtop__DOT__rvtile__DOT___T_93,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__reg_regrw,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__reg_mrdata_valid,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__reg_mrdata_requested,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__delay_mrdata_valid,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__reg_mwdata_requested,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__reg_rstcore,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__dmcontrol_haltreq,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__dmcontrol_resumereq,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT___T_472,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__command_write,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__command_transfer,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__sbcs_sbautoincrement,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__sbcs_sbautoread,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT___T_587,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT___T_589,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT__cmd_regrw,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT___T_602,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT___T_605,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT___T_614,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT___T_617,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT___GEN_129,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__dm__DOT___GEN_130,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__alu_func,3,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__ctlsig_1,2,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__ctlsig_2,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT___T_502,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__ctlsig_3,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__ctlsig_5,1,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__ctlsig_6,2,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__br_sel,2,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__ctlsig_9,1,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__csr_cmd,2,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT__ctlsig_11,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__cpath__DOT___T_910,1,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__bcmp_eq,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__bcmp_lt,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__bcmp_ltu,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__mem_type,1,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_655,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_656,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_priv,1,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__legal_priv,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_396,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__inst_ecall,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__inst_ebreak,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__inst_ret,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_407,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__csr_wen,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___GEN_32,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_422,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_426,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___GEN_35,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__mstatus_MPIE,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___GEN_34,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__mstatus_MPP,1,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_456,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__dcsr_prv,1,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_0___05FT_136_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_0___05FT_346_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_0___05FT_620_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_0___05FT_894_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_1___05FT_136_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_1___05FT_346_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_1___05FT_620_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_1___05FT_894_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_2___05FT_136_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_2___05FT_346_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_2___05FT_620_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_2___05FT_894_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_3___05FT_136_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_3___05FT_346_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_3___05FT_620_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_3___05FT_894_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_0___05FT_121_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_0___05FT_334_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_0___05FT_608_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_0___05FT_882_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_1___05FT_121_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_1___05FT_334_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_1___05FT_608_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_1___05FT_882_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_2___05FT_121_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_2___05FT_334_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_2___05FT_608_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_2___05FT_882_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_3___05FT_121_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_3___05FT_334_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_3___05FT_608_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_3___05FT_882_data,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___T_188,3,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___T_242,3,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_229,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_230,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_231,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_232,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_364,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_372,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_388,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_399,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_410,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_421,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_434,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_442,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___T_462,3,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___T_516,3,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_651,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_652,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_653,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_654,7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_718,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_726,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_742,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_753,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_764,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_775,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_788,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_796,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_1072,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_1080,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_1142,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_1150,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_1161,0,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_1165,0,0);
    VL_SIG8(rvtop__DOT__rvsimdtm__DOT__r_reset,0,0);
    VL_SIG8(rvtop__DOT__rvsimdtm__DOT_____05Fdebug_req_ready,0,0);
    VL_SIG8(rvtop__DOT__rvsimdtm__DOT_____05Fdebug_req_valid,0,0);
    //char	__VpadToAlign171[1];
    VL_SIG16(rvtop__DOT__rvtile__DOT__dm__DOT__command_regno,15,0);
    //char	__VpadToAlign174[2];
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_dmstatus,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_dmcontrol,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_sbcs,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_abstractcs,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_command,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_progbuf_0,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_0,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_1,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_2,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_3,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_4,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_5,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_6,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_7,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_8,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_9,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_10,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_data_11,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_sbaddress_0,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_sbaddress_1,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_sbaddress_2,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT__reg_sbdata_0,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT___T_358,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT___T_467,29,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT___T_492,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT___T_514,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT___T_558,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT___T_586,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT___GEN_32,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__dm__DOT___T_592,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__pc,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_0,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_1,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_2,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_3,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_4,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_5,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_6,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_7,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_8,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_9,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_10,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_11,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_12,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_13,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_14,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_15,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_16,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_17,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_18,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_19,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_20,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_21,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_22,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_23,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_24,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_25,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_26,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_27,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_28,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_29,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_30,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__regfile_31,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__pc_a4,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__inst,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__rs1_data,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__alu_opd1,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__rs2_data,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__alu_opd2,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___T_323,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___T_332,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___T_339,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___T_368,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___T_374,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___T_380,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_362,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_363,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_364,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_365,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_366,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_367,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_368,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_369,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_370,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_371,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_372,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_373,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_374,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_375,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_376,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_377,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_378,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_379,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_380,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_381,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_382,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_383,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_384,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_385,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_386,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_387,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_388,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_389,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_390,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_391,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_392,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___GEN_393,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mstatus,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mtvec,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_medeleg,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mideleg,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mip,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mie,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_0,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_1,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_2,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_3,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_4,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_5,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_6,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_7,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_8,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_9,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_10,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_11,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_12,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_13,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_14,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_15,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_16,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_17,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_18,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_19,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_20,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_21,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_22,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_23,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_24,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_25,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_26,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_27,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmevents_28,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mcounteren,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mscratch,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mepc,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mcause,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mtval,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_dcsr,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_dpc,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_dscratch0,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_dscratch1,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__csr_wdata,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_420,30,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_447,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___GEN_47,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1044,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1133,30,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1339,31,0);
    VL_SIG(rvtop__DOT__rvtile__DOT__amem__DOT___GEN_1166,31,0);
    VL_SIG(rvtop__DOT__rvsimdtm__DOT_____05Fdebug_req_bits_addr,31,0);
    VL_SIG(rvtop__DOT__rvsimdtm__DOT_____05Fdebug_req_bits_op,31,0);
    VL_SIG(rvtop__DOT__rvsimdtm__DOT_____05Fdebug_req_bits_data,31,0);
    VL_SIG(rvtop__DOT__rvsimdtm__DOT_____05Fexit,31,0);
    //char	__VpadToAlign828[4];
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT___T_278,62,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mtime,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mtimecmp,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mcycle,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_minstret,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_0,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_1,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_2,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_3,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_4,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_5,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_6,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_7,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_8,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_9,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_10,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_11,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_12,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_13,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_14,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_15,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_16,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_17,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_18,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_19,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_20,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_21,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_22,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_23,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_24,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_25,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_26,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_27,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT__reg_mhpmcounters_28,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_217,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_220,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_223,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_226,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_229,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_232,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_235,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_238,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_241,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_244,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_247,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_250,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_253,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_256,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_259,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_262,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_265,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_268,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_271,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_274,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_277,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_280,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_283,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_286,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_289,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_292,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_295,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_298,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_301,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_304,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_307,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_310,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1179,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1181,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1183,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1185,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1187,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1189,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1191,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1193,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1195,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1197,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1199,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1201,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1203,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1205,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1207,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1209,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1211,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1213,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1215,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1217,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1219,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1221,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1223,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1225,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1227,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1229,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1231,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1233,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1235,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1237,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1239,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1241,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1243,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1245,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1247,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1249,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1251,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1253,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1255,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1257,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1259,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1261,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1263,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1265,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1267,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1269,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1271,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1273,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1275,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1277,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1279,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1281,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1283,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1285,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1287,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1289,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1291,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1293,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1295,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1297,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1299,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1301,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1303,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1305,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1307,63,0);
    VL_SIG64(rvtop__DOT__rvtile__DOT__core__DOT__dpath__DOT__csr__DOT___T_1309,63,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_0[65536],7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_1[65536],7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_2[65536],7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__amem_3[65536],7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_0[65536],7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_1[65536],7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_2[65536],7,0);
    VL_SIG8(rvtop__DOT__rvtile__DOT__amem__DOT__ahmem_3[65536],7,0);
    
    // LOCAL VARIABLES
    // Internals; generally not touched by application code
    VL_SIG8(__Vclklast__TOP__clock,0,0);
    //char	__VpadToAlign526181[3];
    
    // INTERNAL VARIABLES
    // Internals; generally not touched by application code
    //char	__VpadToAlign526188[4];
    Vrvtop__Syms*	__VlSymsp;		// Symbol table
    
    // PARAMETERS
    // Parameters marked /*verilator public*/ for use by application code
    
    // CONSTRUCTORS
  private:
    Vrvtop& operator= (const Vrvtop&);	///< Copying not allowed
    Vrvtop(const Vrvtop&);	///< Copying not allowed
  public:
    /// Construct the model; called by application code
    /// The special name  may be used to make a wrapper with a
    /// single model invisible WRT DPI scope names.
    Vrvtop(const char* name="TOP");
    /// Destroy the model; called (often implicitly) by application code
    ~Vrvtop();
    
    // USER METHODS
    
    // API METHODS
    /// Evaluate the model.  Application must call when inputs change.
    void eval();
    /// Simulation complete, run final blocks.  Application must call on completion.
    void final();
    
    // INTERNAL METHODS
  private:
    static void _eval_initial_loop(Vrvtop__Syms* __restrict vlSymsp);
  public:
    void __Vconfigure(Vrvtop__Syms* symsp, bool first);
  private:
    static QData	_change_request(Vrvtop__Syms* __restrict vlSymsp);
  public:
    static void	_combo__TOP__11(Vrvtop__Syms* __restrict vlSymsp);
    static void	_combo__TOP__13(Vrvtop__Syms* __restrict vlSymsp);
    static void	_combo__TOP__15(Vrvtop__Syms* __restrict vlSymsp);
    static void	_combo__TOP__17(Vrvtop__Syms* __restrict vlSymsp);
    static void	_combo__TOP__2(Vrvtop__Syms* __restrict vlSymsp);
    static void	_combo__TOP__6(Vrvtop__Syms* __restrict vlSymsp);
    static void	_combo__TOP__9(Vrvtop__Syms* __restrict vlSymsp);
  private:
    void	_configure_coverage(Vrvtop__Syms* __restrict vlSymsp, bool first);
    void	_ctor_var_reset();
  public:
    static void	_eval(Vrvtop__Syms* __restrict vlSymsp);
    static void	_eval_initial(Vrvtop__Syms* __restrict vlSymsp);
    static void	_eval_settle(Vrvtop__Syms* __restrict vlSymsp);
    static void	_initial__TOP__1(Vrvtop__Syms* __restrict vlSymsp);
    static void	_sequent__TOP__4(Vrvtop__Syms* __restrict vlSymsp);
    static void	_sequent__TOP__7(Vrvtop__Syms* __restrict vlSymsp);
    static void	_settle__TOP__10(Vrvtop__Syms* __restrict vlSymsp);
    static void	_settle__TOP__12(Vrvtop__Syms* __restrict vlSymsp);
    static void	_settle__TOP__14(Vrvtop__Syms* __restrict vlSymsp);
    static void	_settle__TOP__16(Vrvtop__Syms* __restrict vlSymsp);
    static void	_settle__TOP__18(Vrvtop__Syms* __restrict vlSymsp);
    static void	_settle__TOP__5(Vrvtop__Syms* __restrict vlSymsp);
    static void	_settle__TOP__8(Vrvtop__Syms* __restrict vlSymsp);
} VL_ATTR_ALIGNED(128);

#endif  /*guard*/

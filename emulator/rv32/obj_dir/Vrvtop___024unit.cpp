// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vrvtop.h for the primary calling header

#include "Vrvtop___024unit.h"  // For This
#include "Vrvtop__Syms.h"

#include "verilated_dpi.h"

//--------------------
// STATIC VARIABLES


//--------------------

VL_CTOR_IMP(Vrvtop___024unit) {
    // Reset internal values
    // Reset structure values
    _ctor_var_reset();
}

void Vrvtop___024unit::__Vconfigure(Vrvtop__Syms* vlSymsp, bool first) {
    if (0 && first) {}  // Prevent unused
    this->__VlSymsp = vlSymsp;
}

Vrvtop___024unit::~Vrvtop___024unit() {
}

//--------------------
// Internal Methods

VL_INLINE_OPT void Vrvtop___024unit::__Vdpiimwrap_debug_tick_TOP____024unit(CData& debug_req_valid, CData debug_req_ready, IData& debug_req_bits_addr, IData& debug_req_bits_op, IData& debug_req_bits_data, CData debug_resp_valid, CData& debug_resp_ready, IData debug_resp_bits_resp, IData debug_resp_bits_data, IData& debug_tick__Vfuncrtn) {
    VL_DEBUG_IF(VL_PRINTF("        Vrvtop___024unit::__Vdpiimwrap_debug_tick_TOP____024unit\n"); );
    // Body
    unsigned char debug_req_valid__Vcvt;
    unsigned char debug_req_ready__Vcvt;
    debug_req_ready__Vcvt = debug_req_ready;
    int debug_req_bits_addr__Vcvt;
    int debug_req_bits_op__Vcvt;
    int debug_req_bits_data__Vcvt;
    unsigned char debug_resp_valid__Vcvt;
    debug_resp_valid__Vcvt = debug_resp_valid;
    unsigned char debug_resp_ready__Vcvt;
    int debug_resp_bits_resp__Vcvt;
    debug_resp_bits_resp__Vcvt = debug_resp_bits_resp;
    int debug_resp_bits_data__Vcvt;
    debug_resp_bits_data__Vcvt = debug_resp_bits_data;
    int debug_tick__Vfuncrtn__Vcvt = debug_tick(&debug_req_valid__Vcvt, debug_req_ready__Vcvt, &debug_req_bits_addr__Vcvt, &debug_req_bits_op__Vcvt, &debug_req_bits_data__Vcvt, debug_resp_valid__Vcvt, &debug_resp_ready__Vcvt, debug_resp_bits_resp__Vcvt, debug_resp_bits_data__Vcvt);
    debug_req_valid = (1U & debug_req_valid__Vcvt);
    debug_req_bits_addr = debug_req_bits_addr__Vcvt;
    debug_req_bits_op = debug_req_bits_op__Vcvt;
    debug_req_bits_data = debug_req_bits_data__Vcvt;
    debug_resp_ready = (1U & debug_resp_ready__Vcvt);
    debug_tick__Vfuncrtn = debug_tick__Vfuncrtn__Vcvt;
}

void Vrvtop___024unit::_ctor_var_reset() {
    VL_DEBUG_IF(VL_PRINTF("        Vrvtop___024unit::_ctor_var_reset\n"); );
}

void Vrvtop___024unit::_configure_coverage(Vrvtop__Syms* __restrict vlSymsp, bool first) {
    VL_DEBUG_IF(VL_PRINTF("        Vrvtop___024unit::_configure_coverage\n"); );
    // Body
    if (0 && vlSymsp && first) {} // Prevent unused
}

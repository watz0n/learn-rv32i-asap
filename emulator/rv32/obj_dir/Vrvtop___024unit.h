// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design internal header
// See Vrvtop.h for the primary calling header

#ifndef _Vrvtop___024unit_H_
#define _Vrvtop___024unit_H_

#include "verilated_heavy.h"
#include "Vrvtop__Dpi.h"

class Vrvtop__Syms;

//----------

VL_MODULE(Vrvtop___024unit) {
  public:
    // CELLS
    
    // PORTS
    
    // LOCAL SIGNALS
    
    // LOCAL VARIABLES
    
    // INTERNAL VARIABLES
  private:
    //char	__VpadToAlign12[4];
    Vrvtop__Syms*	__VlSymsp;		// Symbol table
  public:
    
    // PARAMETERS
    
    // CONSTRUCTORS
  private:
    Vrvtop___024unit& operator= (const Vrvtop___024unit&);	///< Copying not allowed
    Vrvtop___024unit(const Vrvtop___024unit&);	///< Copying not allowed
  public:
    Vrvtop___024unit(const char* name="TOP");
    ~Vrvtop___024unit();
    
    // USER METHODS
    
    // API METHODS
    
    // INTERNAL METHODS
    void __Vconfigure(Vrvtop__Syms* symsp, bool first);
    void	__Vdpiimwrap_debug_tick_TOP____024unit(CData& debug_req_valid, CData debug_req_ready, IData& debug_req_bits_addr, IData& debug_req_bits_op, IData& debug_req_bits_data, CData debug_resp_valid, CData& debug_resp_ready, IData debug_resp_bits_resp, IData debug_resp_bits_data, IData& debug_tick__Vfuncrtn);
  private:
    void	_configure_coverage(Vrvtop__Syms* __restrict vlSymsp, bool first);
    void	_ctor_var_reset();
} VL_ATTR_ALIGNED(128);

#endif  /*guard*/

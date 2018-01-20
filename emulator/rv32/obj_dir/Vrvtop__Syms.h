// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Symbol table internal header
//
// Internal details; most calling programs do not need this header

#ifndef _Vrvtop__Syms_H_
#define _Vrvtop__Syms_H_

#include "verilated_heavy.h"

// INCLUDE MODULE CLASSES
#include "Vrvtop.h"
#include "Vrvtop___024unit.h"

// DPI TYPES for DPI Export callbacks (Internal use)

// SYMS CLASS
class Vrvtop__Syms : public VerilatedSyms {
  public:
    
    // LOCAL STATE
    const char* __Vm_namep;
    bool	__Vm_activity;		///< Used by trace routines to determine change occurred
    bool	__Vm_didInit;
    //char	__VpadToAlign10[6];
    
    // SUBCELL STATE
    Vrvtop*                        TOPp;
    Vrvtop___024unit               TOP____024unit;
    
    // COVERAGE
    
    // SCOPE NAMES
    
    // CREATORS
    Vrvtop__Syms(Vrvtop* topp, const char* namep);
    ~Vrvtop__Syms() {};
    
    // METHODS
    inline const char* name() { return __Vm_namep; }
    inline bool getClearActivity() { bool r=__Vm_activity; __Vm_activity=false; return r;}
    
} VL_ATTR_ALIGNED(64);

#endif  /*guard*/

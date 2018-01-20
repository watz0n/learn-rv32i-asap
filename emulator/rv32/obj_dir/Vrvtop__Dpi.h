// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Prototypes for DPI import and export functions.
//
// Verilator includes this file in all generated .cpp files that use DPI functions.
// Manually include this file where DPI .c import functions are declared to insure
// the C functions match the expectations of the DPI imports.

#include "svdpi.h"

#ifdef __cplusplus
extern "C" {
#endif
    
    
    // DPI IMPORTS
    // DPI Import at /home/wats0n/projects/learn-rv32i-asap/vsrc/rvsimdtm.v:8
    extern int debug_tick (unsigned char* debug_req_valid, unsigned char debug_req_ready, int* debug_req_bits_addr, int* debug_req_bits_op, int* debug_req_bits_data, unsigned char debug_resp_valid, unsigned char* debug_resp_ready, int debug_resp_bits_resp, int debug_resp_bits_data);
    
#ifdef __cplusplus
}
#endif

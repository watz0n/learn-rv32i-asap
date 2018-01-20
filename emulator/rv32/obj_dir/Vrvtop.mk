# Verilated -*- Makefile -*-
# DESCRIPTION: Verilator output: Makefile for building Verilated archive or executable
#
# Execute this makefile from the object directory:
#    make -f Vrvtop.mk

default: /home/wats0n/projects/learn-rv32i-asap/emulator/rv32/emulator

### Constants...
# Perl executable (from $PERL)
PERL = perl
# Path to Verilator kit (from $VERILATOR_ROOT)
VERILATOR_ROOT = /home/wats0n/projects/verilator
# Path to SystemPerl kit top (from $SYSTEMPERL)
SYSTEMPERL = 
# Path to SystemPerl kit includes (from $SYSTEMPERL_INCLUDE)
SYSTEMPERL_INCLUDE = 
# SystemC include directory with systemc.h (from $SYSTEMC_INCLUDE)
SYSTEMC_INCLUDE ?= 
# SystemC library directory with libsystemc.a (from $SYSTEMC_LIBDIR)
SYSTEMC_LIBDIR ?= 

### Switches...
# SystemPerl output mode?  0/1 (from --sp)
VM_SP = 0
# SystemC output mode?  0/1 (from --sc)
VM_SC = 0
# SystemPerl or SystemC output mode?  0/1 (from --sp/--sc)
VM_SP_OR_SC = 0
# Deprecated
VM_PCLI = 1
# Deprecated: SystemC architecture to find link library path (from $SYSTEMC_ARCH)
VM_SC_TARGET_ARCH = linux

### Vars...
# Design prefix (from --prefix)
VM_PREFIX = Vrvtop
# Module prefix (from --prefix)
VM_MODPREFIX = Vrvtop
# User CFLAGS (from -CFLAGS on Verilator command line)
VM_USER_CFLAGS = \
	 -O1 -std=c++11  -g -I/home/wats0n/projects/learn-rv32i-asap/emulator/common -I/usr/local/include -I/home/wats0n/projects/verilator/include/ \

# User LDLIBS (from -LDFLAGS on Verilator command line)
VM_USER_LDLIBS = \
	 -L/usr/local/lib -Wl,-rpath,/usr/local/lib -L. -lpthread \
	/home/wats0n/projects/learn-rv32i-asap/riscv-fesvr/build/libfesvr.so \
	/home/wats0n/projects/learn-rv32i-asap/emulator/rv32/simdtm.o \

# User .cpp files (from .cpp's on Verilator command line)
VM_USER_CLASSES = \
	emulator \

# User .cpp directories (from .cpp's on Verilator command line)
VM_USER_DIR = \
	/home/wats0n/projects/learn-rv32i-asap/emulator/common \


### Default rules...
# Include list of all generated classes
include Vrvtop_classes.mk
# Include global rules
include $(VERILATOR_ROOT)/include/verilated.mk

### Executable rules... (from --exe)
VPATH += $(VM_USER_DIR)

emulator.o: /home/wats0n/projects/learn-rv32i-asap/emulator/common/emulator.cpp
	$(CXX) $(CXXFLAGS) $(CPPFLAGS) $(OPT_FAST) -c -o $@ $<

### Link rules... (from --exe)
/home/wats0n/projects/learn-rv32i-asap/emulator/rv32/emulator: $(VK_USER_OBJS) $(VK_GLOBAL_OBJS) $(VM_PREFIX)__ALL.a
	$(LINK) $(LDFLAGS) $^ $(LOADLIBES) $(LDLIBS) -o $@ $(LIBS) $(SC_LIBS) 2>&1 | c++filt


# Verilated -*- Makefile -*-

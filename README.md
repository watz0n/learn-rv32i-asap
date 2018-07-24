A Simple As Possible RISCV-32I Implementation by Chisel3
===

This project is a toy-project to build RISCV-32I by Chisel3 from scratch, and verify the functionality by Berkeley front-end server `riscv-fesvr`. The concept is coming from [RISCV Sodor Project](https://github.com/librecores/riscv-sodor).

Current implementation has these features:

* Simple 1-stage architecture
* [RISCV Spec. Volumn I: User-Level ISA RV32I v2.0](https://content.riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf)
* [RISCV Spec. Volumn II: Privileged ISA v1.10, M-Mode and Debug CSR](https://content.riscv.org/wp-content/uploads/2017/05/riscv-privileged-v1.10.pdf) 
* [Sifive RISCV External Debug Support v0.13 for riscv-fesvr](https://static.dev.sifive.com/riscv-debug-spec-0.13.f7f3277.pdf) with non-standard features
* Unit-Test for simple instructions sanity check
* Magic RAM form Chisel3 Mem() module
* Pass Sodor Project pre-compiled `riscv-tests` and `riscv-bmarks`

This project start from RV32I Base Instruction Set at Volumn I page 104 and the lecture [Berkeley CS152 FA16, L3: From CISC to RISC](http://www-inst.eecs.berkeley.edu/~cs152/fa16/lectures/L03-CISCRISC.pdf), from Reg-Reg at page 10, until JALR at page 17. Then reference the [Sodor Project](https://github.com/librecores/riscv-sodor) and privileged specification to implement CSR and SimDTM to link `riscv-fesvr` for running pre-compiled test-bench. Current [system diagram](https://github.com/watz0n/learn-rv32i-asap/blob/master/doc/Chisel3-RV32I-ASAP-Overview.png) and [data-path diagram](https://github.com/watz0n/learn-rv32i-asap/blob/master/doc/Chisel3-RV32I-ASAP-DataPath.png) are under `./doc` directory. If data-path diagram is too complicat to find wire name, you can use [draw.io](https://www.draw.io/) service to open .XML file and search the keyword.

The memory system is implemented by Chisel3 Mem() module, it has lower memory region start from `0x00000000` for unit-test, and higher memory region start from `0x80000000` for `riscv-fesvr` entry point. Each memory region has `0x10000` bytes capacity. But the memory can't be allocated too much. If it's over 1MBs(`0x100000`), Verilator emulator would crash from OS memory barrier. An I think the solution would like [Sodor Project](https://github.com/librecores/riscv-sodor) to declare a memory in Verilog from BlackBox.

If you wish to have more fundamental learning material, like how to use Chisel3, please reference [learn-chisel3-gcd](https://github.com/watz0n/learn-chisel3-gcd). If you feel current project is messy and need a clean start point, please reference [learn-rv32i-unittest-alu](https://github.com/watz0n/learn-rv32i-unittest-alu).

Adhere, we are focus how to build RV32I emulator and use unit-test in this repo. If you are interested to know how I implement this project from scratch and handle the Chisel3 error in detail, please reference [my development notes](https://watz0n.github.io/blog/en-post/2018/01/10/learn-rv32i-series-en.html). But it would be under-construction before the documents for learn-chisel3-gcd and learn-rv32i-unittest-alu are ready. 

Setup Chisel3 Build Environment
===

We need two software to use Chisel3: `sbt` and `Verilator 3.906`.
The official project has comprehensive installation guide, or you can reference my progress. By the way, My build system is Ubuntu 16.04 under Windows10 via Bash on Windows, if you like setup same environment, please reference [my old win10 setup-up process](https://github.com/wats0n/install-chisel-win10).

Follow the official Chisel3 installation guide
---
Following the Linux Installation Guide from [Chisel3 project page](https://github.com/freechipsproject/chisel3), 
and it's better to update Verilator to v3.906 from [Sodor project page](https://github.com/librecores/riscv-sodor#building-the-processor-emulators), my environment chosen installation from .tgz package.

My Chisel3 installation progress
---
Here is a simplified progress to setup `sbt` environment:
```bash
#install sbt form chisel3 projcet
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
sudo apt-get update
#Install necessary packages for verilator and chisel3
sudo apt-get install git make autoconf g++ flex bison default-jdk sbt
```
Next step is Verilator installation progress. I usually install Verilator under `~/work/verilator` directory:
```bash
cd ~/
mkdir work
cd work
# reference form riscv-sodor
wget https://www.veripool.org/ftp/verilator-3.906.tgz
tar -xzf verilator-3.906.tgz
mv verilator-3.906 verilator
cd verilator
unset VERILATOR_ROOT
./configure
make
export VERILATOR_ROOT=$PWD
export PATH=$PATH:$VERILATOR_ROOT/bin
```

Get the repo
===
As Sodor procedure, because we need official `riscv-fesvr` to handle Debug Transport Module simulation.
```bash
git clone --recursive https://github.com/watz0n/learn-rv32i-asap
cd learn-rv32i-asap
```

Directory structure in repo.
===
* .\doc\ : System Overview and Data-Path Diagram
* .\emulator\ : Emulator main code, and DTM simulator for riscv-fesver
* .\install\ : Pre-compiled tests and benchmarks from Sodor Project
* .\project\ : Project settings and compiled class directory
* .\riscv-fesvr\ : Use RISCV Front-End SerVeR source as submodule
* .\src\main\scala\ : Chisel3 circuit codes
    * .\src\main\scala\common : Pre-defined constants, Memory, CSRs codes
    * .\src\main\scala\rv32: RISCV-32I Core
* .\src\test\scala\ : Chisel3 test-bench codes
    * .\src\main\test\rv32 : RV32I Instruction Unit-Test

Setup submodule riscv-fesvr
---
Then, use `riscv-fesvr` build code snippet from [Sodor project page](https://github.com/librecores/riscv-sodor#building-the-processor-emulators):
```bash
cd riscv-fesvr
mkdir build; cd build
../configure --prefix=/usr/local
make install 
```

Build RISCV-32I Emulator
---

Build rv32i emulator is identical to [Sodor project page](https://github.com/librecores/riscv-sodor#building-the-processor-emulators).
```bash
./configure --with-riscv=/usr/local
make
```

Or if you want to refresh `./configure` from configure.ac, it's fine with following steps.
```bash
autoconf
./configure --with-riscv=/usr/local
make
```
Makefile operations for Emulator and Unit-Test
===

Almost the same with [Sodor Project](https://github.com/librecores/riscv-sodor#running-the-risc-v-tests), but I removed statistics function and added unit-test option. 

*Running Sodor RISC-V Tests*
```bash
make run-emulator
```

*Generate Sodor RISC-V Tests Waveform(.vcd)*
```bash
make run-emulator-debug
```

*Clear Emulator and test results*
```bash
make clean
```

If you need simple unit-test for your new instruction, you can reference the Chisel code in `./src/test/scala/rv32/rvtile_unittest.scala`, and build your custom test-bench.

*New: Running Unit-Test in rvtile_unittest.scala*
```bash
make unit-test
```

Because we use Chisel3 test function would generate large meta-data like `./test_run_dir`, I've write a script to clean it:

*New: Clear Unit-Test meta-data and results*
```bash
make clean-unit
```

There is a more strong cleaner, not only clean meta-data, but also clean compiled Chisel3 class data:

*New: Clear All generated data include cached Scala/Chisel code*
```bash
make clean-deep
```

Debug by Value Change Dump (VCD) File
===

Use make unit-test
---
Under `./test_run_dir` directory, there are tester directories.
For example:
```bash
# Execute
make unit-test
#...
# Verilog code would be
./test_run_dir/rvsim.RVTilePeekPokeSpec308285776/rvtile.v
# Verilog VCD File would be
./test_run_dir/rvsim.RVTilePeekPokeSpec308285776/rvtile.vcd
```
* rvsim : unit-test package name
* RVTilePeekPokeSpec : unit-test class name
* 308285776 : random seed for Verilator
* rvtile : the Module class name in unit-test

All test use same directory, result would be overwrite by next test. But it would stop on error for our debug.

Use run-emulator-debug
---
Under `emulator/rv32/output` directory.
For example:
```bash
# Execute
make run-emulator-debug
#...
# Verilog code would be
./emulator/rv32/generated-src/rvtop.v
# From test code: install/riscv-tests/rv32ui-p-simple
# Verilog VCD File would be
./emulator/rv32/output/rv32ui-p-simple.vcd
```

Learning Materials
===

Below is my short list to know how to build RISCV-32I from Chisel, hope it would help your on your walkthrough.

University Courses (Online Data)
---
* [Berkeley CS61C SP16](http://inst.eecs.berkeley.edu/~cs61c/sp16/) : Prerequisite course of CS152, worth to study the basic ideas from old RISC architecture. The [CS61C FA17](http://inst.eecs.berkeley.edu/~cs61c/fa17/) use new RISCV textbook.
* [Berkeley CS150 FA13](http://www-inst.eecs.berkeley.edu/~cs150/fa13/) : 
This laboratory course has essential concept for ValidIO/Decoupled mechanism.
* [Berkeley CS152 FA16](http://www-inst.eecs.berkeley.edu/~cs152/fa16/): RISCV in real class, suggest reading all lectures to build your database in mind before implementation.

Online Courses
---
* [MITx 6.004x Series](https://www.edx.org/course/computation-structures-part-1-digital-mitx-6-004-1x-0) : This course enlighten me to Digital Design world, and apply BETA core design experience in this design. If you want to look this course without edX account, here is the [official website](http://computationstructures.org/).
* [MITx 6.005x Series](https://www.edx.org/course/software-construction-java-mitx-6-005-1x) : Understand how to use Java language, and apply Java Exception assignment experience for Chisel3 unit-test framework, scalatest.

Books
---
* [The RISC-V Reader: An Open Architecture Atlas](http://riscvbook.com/) : Great book to understand Privileged Spec. CSR behaviors.
* [Programming in Scala, First Edition](https://www.artima.com/pins1ed/) : Comprehensive Scala introduction book, strong recommendation to read before implementation.

Wiki
---
* [Chisel3 Official Wiki](https://github.com/freechipsproject/chisel3/wiki) : 
Lots of Chisel3 use cases and examples.
* [Chisel Learning Journey](https://github.com/librecores/riscv-sodor/wiki/Chisel-Learning-Journey) : Great content about Sodor implementation since 2017/12 update, especially the [Overview page](https://github.com/librecores/riscv-sodor/wiki/overview) has crucial information about DM module non-standard command 0x44(reset the core).

Projects
---
* [GitHub learn-chisel3-gcd](https://github.com/watz0n/learn-chisel3-gcd) : My Chisel3 learning experience, focus on how to link HDL (Verilog/VHDL) experience with Chisel3 design pattern, and test Chisel3 test-bench fesibility.
* [GitHub learn-rv32i-unittest-alu](https://github.com/watz0n/learn-rv32i-unittest-alu) : The beginning of this projcet, represent the first step for building RISCV Reg-Reg and Reg-Imm datapath form scratch.


TODO List
===
* Document for chisel3-gcd, chisel3-rv32i-unittest-alu
* 5-stage pipeline


Done List
===
* Synchronous Memory : [learn-rv32i-arty project](https://github.com/watz0n/learn-rv32i-arty)
* FPGA synthesis : [learn-rv32i-arty project](https://github.com/watz0n/learn-rv32i-arty)
* Connect fesvr to JTAG Interface on FPGA : [learn-rv32i-arty project](https://github.com/watz0n/learn-rv32i-arty)


FAQs
===
*Hey! You have some typo or something wrong! Where are you?*

If you have any questions, corrections, or other feedback, you can email me or open an issue.

* E-Mail:   watz0n.tw@gmail.com
* Blog:     https://blog.watz0n.tech
* Backup:   https://watz0n.github.io

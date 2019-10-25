package multicpu

import java.io.File

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import chisel3._


import scala.util.Random
import Math.{pow, abs}


// class CPUUnitTester(c: CPU) extends PeekPokeTester(c) {
//     val RandGen = new Random()
//     var mem = new Array[Int](128)
//     var mem_data_prev = 0;

//     def printStatus() {
//         println("====== Registers ======")
//         for (n <- 0 until 5) {
//             poke(c.io.debug.ra_c, n)
//             println("$" + n.toString + "=" + peek(c.io.debug.rd_c).toInt )
//         }
//         println("======= States ========")
//         println("ir mdr aluout pc state " + peek(c.io.debug.ir).toInt + " "
//                 + peek(c.io.debug.mdr).toInt + " "+ peek(c.io.debug.aluout).toInt + " "
//                 + peek(c.io.debug.pc).toInt + " " + peek(c.io.ctrl_dbg.cur_state).toInt )
//         println("R I J L S EQ NE "+ peek(c.io.ctrl_dbg.isRType).toInt + " "
//                                   + peek(c.io.ctrl_dbg.isIType).toInt + " "
//                                   + peek(c.io.ctrl_dbg.isJType).toInt + " "
//                                   + peek(c.io.ctrl_dbg.isLW).toInt    + " "
//                                   + peek(c.io.ctrl_dbg.isSW).toInt    + " "
//                                   + peek(c.io.ctrl_dbg.isBEQ).toInt   + " "
//                                   + peek(c.io.ctrl_dbg.isBNE).toInt   + " "  )
//     }

//     def processMemory() {   // NOTICE: Memory is a little behind..
//         var mem_addr  = peek(c.io.mem.mem_addr).toInt
//         //var mem_rdata = peek(c.io.mem.mem_rdata)
//         var mem_wdata = peek(c.io.mem.mem_wdata).toInt
//         var mem_wen   = peek(c.io.mem.mem_wen).toInt
//         if (mem_wen == 1) {
//             mem(mem_addr >> 2) = mem_wdata.toInt;
//         }
//         println("Maddr: " + mem_addr.toString + " En: " + mem_wen + "rdata: " + mem_data_prev)

//         poke(c.io.mem.mem_rdata, mem_data_prev)
//         mem_data_prev = mem(mem_addr >> 2)
//     }

//     def initMemory() {
//         mem(0) = 0x20010001; // addi $1, $0, 1
//         mem(1) = 0x20210001; // addi $1, $1, 1
//         mem(2) = 0x00210820; // add $1, $1, $1
//         //mem(3) = 0x08000003; // J 0x12 (index = 3)
//         mem(3) = 0x08000002; // J 0x8 (index = 2)
//         mem(4) = 0x08000003; // add $1, $1, $1
//         mem(5) = 0x08000003; // add $1, $1, $1
//         mem(6) = 0x08000003; // add $1, $1, $1
//         mem(7) = 0x08000003; // add $1, $1, $1
//         mem(8) = 0x08000003; // add $1, $1, $1
//     }

//     initMemory()
//     println(mem(0).toString)
//     for (i <- 0 until 32) {
//         println("Clk " + i.toString)
//         processMemory()
//         printStatus()
//         step(1)

//     }

// }


class CPUUnitTester(c: CPU) extends PeekPokeTester(c) {
    val RandGen = new Random()

    def printStatus() {
        println("====== Registers ======")
        for (n <- 0 until 5) {
            poke(c.io.debug.ra_c, n)
            println("$" + n.toString + "=" + peek(c.io.debug.rd_c).toInt )
        }
        println("======= States ========")
        println("ir mdr aluout pc state " + peek(c.io.debug.ir).toInt + " "
                + peek(c.io.debug.mdr).toInt + " "+ peek(c.io.debug.aluout).toInt + " "
                + peek(c.io.debug.pc).toInt + " " + peek(c.io.ctrl_dbg.cur_state).toInt )
        println("R I J L S EQ NE "+ peek(c.io.ctrl_dbg.isRType).toInt + " "
                                  + peek(c.io.ctrl_dbg.isIType).toInt + " "
                                  + peek(c.io.ctrl_dbg.isJType).toInt + " "
                                  + peek(c.io.ctrl_dbg.isLW).toInt    + " "
                                  + peek(c.io.ctrl_dbg.isSW).toInt    + " "
                                  + peek(c.io.ctrl_dbg.isBEQ).toInt   + " "
                                  + peek(c.io.ctrl_dbg.isBNE).toInt   + " "  )
    }

    poke(c.io.single, 1);
    for (i <- 0 until 32) {
        println("Clk " + i.toString)
        printStatus()
        if (i == 10)
          poke(c.io.single, 0)
        step(1)
    }

}

class CPUTester extends ChiselFlatSpec {
  behavior of "CPU"
  backends foreach {backend =>
    it should s"perform correct math operation on dynamic operand in $backend" in {
      Driver(() => new CPU(), backend)((c) => new CPUUnitTester(c)) should be (true)
    }
  }
}

object CPUGen extends App {
  chisel3.Driver.execute(args, () => new CPU())
}

object CPUTest extends App {
  iotesters.Driver.execute(args, () => new CPU()) {
    c => new CPUUnitTester(c)
  }
}
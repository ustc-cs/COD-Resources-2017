// See LICENSE.txt for license details.
package multicpu

import java.io.File

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import chisel3._

import scala.util.Random
import Math.{pow, abs}

//github.com/freechipsproject/chisel-tester
class ALUUnitTester(c: ALU) extends PeekPokeTester(c) {
  val RandGen = new Random()

  for (n <- 0 until 64) {
    val a      = BigInt(abs(RandGen.nextLong() % (pow(2, 32).toLong - 1) ))
    val b      = BigInt(abs(RandGen.nextLong() % (pow(2, 32).toLong - 1) ))
    val opcode = BigInt(abs(RandGen.nextLong() % 16))
  // for (a <- 0 until 16) {
    // for (b <- 0 until 16) {
      // for (opcode <- 0 until 4) {
        var output = BigInt(0)
        if (opcode == ALUSel.ALU_ADD.toInt) {
          output = (a+b) & BigInt((Math.pow(2,32) - 1).toLong)
        } else if (opcode == ALUSel.ALU_SUB.toInt) {
          output = (a-b) & BigInt((Math.pow(2,32) - 1).toLong)
        } else if (opcode == ALUSel.ALU_SLT.toInt) {
          output = a.toInt < b.toInt
        } else if (opcode == ALUSel.ALU_SLTU.toInt) {
          output = a < b
        } else if (opcode == ALUSel.ALU_AND.toInt) {
          output = a & b
        } else if (opcode == ALUSel.ALU_OR.toInt) {
          output = a | b
        } else if (opcode == ALUSel.ALU_XOR.toInt) {
          output = a ^ b
        } else if (opcode == ALUSel.ALU_COPY_A.toInt) {
          output = a
        } else if (opcode == ALUSel.ALU_NOR.toInt) {
          output = (~(a.toInt | b.toInt))  // Problem ! TBC
        } else {
          output = b
        }
        poke(c.io.A, a)
        poke(c.io.B, b)
        poke(c.io.opcode, opcode)
        step(1)
        println("A = "+ a.toString + "; B = "+ b.toString + "; opcode = " + opcode.toString)
        expect(c.io.out, output)
  }
      // }}}
}

class ALUTester extends ChiselFlatSpec {
  behavior of "ALU"
  backends foreach {backend =>
    it should s"perform correct math operation on dynamic operand in $backend" in {
      Driver(() => new ALU(32), backend)((c) => new ALUUnitTester(c)) should be (true)
    }
  }

    /**
    * By default verilator backend produces vcd file, and firrtl and treadle backends do not.
    * Following examples show you how to turn on vcd for firrtl and treadle and how to turn it off for verilator
    */

  // "running with --generate-vcd-output on" should "create a vcd file from your test" in {
  //   iotesters.Driver.execute(
  //     Array("--generate-vcd-output", "on", "--target-dir", "test_run_dir/vcd", "--top-name", "alu"),
  //     () => new ALU(32)
  //   ) {

  //     c => new ALUUnitTester(c)
  //   } should be(true)

  //   new File("test_run_dir/vcd/alu.vcd").exists should be (true)
  // }

}

object ALUGen extends App {
  chisel3.Driver.execute(args, () => new ALU(32))
}

object ALUTest extends App {
  iotesters.Driver.execute(args, () => new ALU(32)) {
    c => new ALUUnitTester(c)
  }
}
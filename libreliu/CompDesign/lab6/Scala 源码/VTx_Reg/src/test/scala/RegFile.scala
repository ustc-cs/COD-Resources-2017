// See LICENSE.txt for license details.
package multicpu

import java.io.File

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import chisel3._

import scala.util.Random
import Math.{pow, abs}



//github.com/freechipsproject/chisel-tester
class RegFileUnitTester(c: RegFile) extends PeekPokeTester(c) {
    val RandGen = new Random()

    def setNum(n: Int, x: BigInt) {
        poke(c.io.wa, n)
        poke(c.io.wd, x)
        poke(c.io.RegWrite, true)
        step(1)
        poke(c.io.RegWrite, false)
    }

    def checkNum(n: Int, x: BigInt, which: Int) {
        if (which == 0) {
            poke(c.io.ra_a, n)
            if (n == 0)
                expect(c.io.rd_a, 0)
            else
                expect(c.io.rd_a, x)
        } else {
            poke(c.io.ra_b, n)
            if (n == 0)
                expect(c.io.rd_b, 0)
            else
                expect(c.io.rd_b, x)
        }
    }

    for (n <- 0 until 32) {
        checkNum(n, BigInt(0), abs(RandGen.nextLong() % 2))
    }

    for (n <- 0 until 64) {
        val a      = BigInt(abs(RandGen.nextLong() % (pow(2, 32).toLong - 1) ))
        val n: Int = abs(RandGen.nextLong() % 32)
        val which  = abs(RandGen.nextLong() % 2)

        setNum(n, a)
        checkNum(n, a, which)
    }
  

}

class RegFileTester extends ChiselFlatSpec {
  behavior of "RegFile"
  backends foreach {backend =>
    it should s"perform correct math operation on dynamic operand in $backend" in {
      Driver(() => new RegFile(), backend)((c) => new RegFileUnitTester(c)) should be (true)
    }
  }
}

object RegFileGen extends App {
  chisel3.Driver.execute(args, () => new RegFile())
}

object RegFileTest extends App {
  iotesters.Driver.execute(args, () => new RegFile()) {
    c => new RegFileUnitTester(c)
  }
}
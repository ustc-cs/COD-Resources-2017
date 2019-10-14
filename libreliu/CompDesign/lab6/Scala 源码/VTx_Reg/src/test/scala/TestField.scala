package testfield

import java.io.File

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import chisel3._


import scala.util.Random
import Math.{pow, abs}

object TestFieldGen extends App {
  chisel3.Driver.execute(args, () => new TestField())
}

class TestFieldUnitTester(c: TestField) extends PeekPokeTester(c) {
    step(1)
    poke(c.io.signal, 1)
    step(5)
    poke(c.io.signal, 0)
    step(50)
    poke(c.io.signal, 1)
    step(1)
    poke(c.io.signal, 0)
    step(1)
    poke(c.io.signal, 1)
    step(50)
    poke(c.io.signal, 0)
    step(20)
}

object TestFieldTest extends App {
  iotesters.Driver.execute(args, () => new TestField()) {
    c => new TestFieldUnitTester(c)
  }
}
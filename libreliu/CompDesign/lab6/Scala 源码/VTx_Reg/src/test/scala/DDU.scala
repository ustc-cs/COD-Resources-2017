package ddu

import java.io.File

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import chisel3._


import scala.util.Random
import Math.{pow, abs}

object DDUGen extends App {
  chisel3.Driver.execute(args, () => new DDU())
}

class DDUUnitTester(c: DDU) extends PeekPokeTester(c) {
  // poke(c.io.cont, 1)
  // step(10)
  // poke(c.io.cont, 0)
  // step(20)
  // poke(c.io.step, 1)
  // step(2)
  // poke(c.io.cont, 1)
  // step(50)
  poke(c.io.cont, 1)
  step(400)
}

object DDUTest extends App {
  iotesters.Driver.execute(args, () => new DDU()) {
    c => new DDUUnitTester(c)
  }
}
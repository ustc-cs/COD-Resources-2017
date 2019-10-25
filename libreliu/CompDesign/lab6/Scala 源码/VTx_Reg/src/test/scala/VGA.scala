package vga

import java.io.File

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import chisel3._

import scala.util.Random
import Math.{pow, abs}

object VGAGen extends App {
  chisel3.Driver.execute(args, () => new VGA())
}

class VGAUnitTester(c: VGA) extends PeekPokeTester(c) {

  step(800000)
}

object VGATest extends App {
  iotesters.Driver.execute(args, () => new VGA()) {
    c => new VGAUnitTester(c)
  }
}
package vga

import chisel3._
import chisel3.util._


class VGACounter(val n: Int) {
  require(n >= 0)
  val value = if (n > 1) RegInit(0.U(log2Ceil(n).W)) else 0.U

  /** Increment the counter, returning whether the counter currently is at the
    * maximum and will wrap. The incremented value is registered and will be
    * visible on the next cycle.
    */
  def inc(): Bool = {
    if (n > 1) {
      val wrap = value === (n-1).asUInt
      value := value + 1.U
      if (!isPow2(n)) {
        when (wrap) { value := 0.U }
      }
      wrap
    } else {
      true.B
    }
  }

  def clear(clearTo: Int): Bool = {
    if (n > 1) {
      val wrap = value === (n-1).asUInt
      value := clearTo.U
      wrap
    } else {
      true.B
    }
  }
}

object VGACounter
{
  /** Instantiate a [[Counter! counter]] with the specified number of counts.
    */
  def apply(n: Int): VGACounter = new VGACounter(n)

  def apply(cond: Bool, clear: Bool, n: Int, clearTo: Int): (UInt, Bool) = {
    val c = new VGACounter(n)
    var wrap: Bool = null
    when (clear) {  wrap = c.clear(clearTo)  }
    .elsewhen (cond) { wrap = c.inc() }
    (c.value, cond && wrap)
  }
}
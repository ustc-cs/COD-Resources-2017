
package multicpu

import chisel3._
import chisel3.util._

object ALUSel {
  val ALU_ADD    = 0.U(4.W)
  val ALU_SUB    = 1.U(4.W)
  val ALU_AND    = 2.U(4.W)
  val ALU_OR     = 3.U(4.W)
  val ALU_XOR    = 4.U(4.W)
  val ALU_SLT    = 5.U(4.W)
  val ALU_SLL    = 6.U(4.W)
  val ALU_SLTU   = 7.U(4.W)
  val ALU_SRL    = 8.U(4.W)
  val ALU_SRA    = 9.U(4.W)
  val ALU_COPY_A = 10.U(4.W)
  val ALU_COPY_B = 11.U(4.W)
  val ALU_NOR    = 12.U(4.W)
  val ALU_XXX    = 15.U(4.W)
}

class ALU(n: Int) extends Module {
    val io = IO(new Bundle {
        val A = Input(UInt(n.W))
        val B = Input(UInt(n.W))
        val opcode = Input(UInt(4.W))
        val out = Output(UInt(n.W))
        val flagZero = Output(Bool())
    })

    io.out := MuxLookup(io.opcode, io.B, Seq(
      ALUSel.ALU_ADD  -> (io.A + io.B),
      ALUSel.ALU_SUB  -> (io.A - io.B),
      //ALUSel.ALU_SRA  -> (io.A.asSInt >> io.B).asUInt,
      //ALUSel.ALU_SRL  -> (io.A >> io.B),
      ALUSel.ALU_SLL  -> (io.A << io.B(4,0)),   // !Notice!
      ALUSel.ALU_SLT  -> (io.A.asSInt < io.B.asSInt),
      ALUSel.ALU_SLTU -> (io.A < io.B),
      ALUSel.ALU_AND  -> (io.A & io.B),
      ALUSel.ALU_OR   -> (io.A | io.B),
      ALUSel.ALU_XOR  -> (io.A ^ io.B),
      ALUSel.ALU_NOR  -> ~(io.A | io.B),
      ALUSel.ALU_COPY_A -> io.A))

    io.flagZero := (io.out === 0.U)
}
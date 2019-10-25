
package multicpu

import chisel3._
import chisel3.util._

class RegFile extends Module {
    val io = IO(new Bundle {
        val ra_a = Input(UInt(5.W))
        val ra_b = Input(UInt(5.W))
        val wa = Input(UInt(5.W))
        val wd = Input(UInt(32.W))
        val rd_a = Output(UInt(32.W))
        val rd_b = Output(UInt(32.W))
        val RegWrite = Input(Bool())

        // Port for debugging
        val ra_c = Input(UInt(5.W))
        val rd_c = Output(UInt(32.W))
    })
    val regs = RegInit( VecInit( Seq.fill(32)(0.U(32.W)) ) )
    for (i <- 0 until 32) {
        when (io.wa === i.U(5.W) && io.RegWrite) {
            regs(i) := io.wd
        }
        // when (io.ra_a === i.U(5.W)) {  // Will produce error!
        //     io.rd_a := regs(i) 
        // }
        // when (io.ra_b === i.U(5.W)) {
        //     io.rd_b := regs(i)
        // }
    }
    val raZero = (io.ra_a === 0.U)
    val rbZero = (io.ra_b === 0.U)
    val rcZero = (io.ra_c === 0.U)
    io.rd_a := Mux(raZero, 0.U, regs(io.ra_a))
    io.rd_b := Mux(rbZero, 0.U, regs(io.ra_b))
    io.rd_c := Mux(rcZero, 0.U, regs(io.ra_c))
}
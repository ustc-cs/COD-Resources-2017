package multicpu

import chisel3._
import chisel3.util._

// Datapath perspective
class MemPort extends Bundle {
    val mem_rdata = Input(UInt(32.W))
    val mem_addr = Output(UInt(32.W))
    val mem_wdata = Output(UInt(32.W))
    val mem_wen   = Output(Bool())

    // Extra reading port for debugging
    val mem_addr2 = Output(UInt(32.W))
    val mem_rdata2 = Input(UInt(32.W))
}

object COEReader {
    def read(s: String) : Array[UInt] = {
        val lines = scala.io.Source.fromFile("src/test/resources/xxd_c4_util_final_bin.txt").getLines()
        
        var arr = Array[UInt]()

        for (l <- lines) {
            // TODO: Strip leading blank chars
            val num = java.lang.Long.parseLong(l, 16)
            println("COEReader: got " + num.toString)
            arr = scala.Array.concat(arr, Array(num.U(32.W)))
        }
        arr
    }
}

    // // a synchronous-read, synchronous-write memory (like in FPGAs).
    // val mem = SyncReadMem(1024, UInt(32.W))
    // // Create one write port and one read port.
    // mem.write(io.mem_addr, wdata)
    // dataOut := mem.read(addr, enable)

class Mem(n: Int) extends Module {
    val io = IO(Flipped(new MemPort))

    // val data_init = Array(
    //     0x20010001.U(32.W),
    //     0x20210001.U(32.W),
    //     0x00210820.U(32.W),
    //     0x08000002.U(32.W),
    //     0x08000003.U(32.W),
    //     0x08000003.U(32.W),
    //     0x08000003.U(32.W),
    //     0x08000003.U(32.W)
    // ).toSeq
    val data_init = COEReader.read("inst_rom.coe")
    if (n < data_init.length) {
        throw new IllegalArgumentException
    }

    val memory = RegInit( VecInit( Seq.concat(data_init, Seq.fill(n - data_init.length)(0.U(32.W)) ) ) )
    // for (p <- 0 until n) {
    //     when (io.mem_wen && (io.mem_addr >> 2) === p.U) {
    //         memory(p) := io.mem_wdata
    //     }
    // }
    when (io.mem_wen) {
        memory(io.mem_addr >> 2) := io.mem_wdata
    }

    // Mem here must be async, or buggy
    io.mem_rdata := memory(io.mem_addr >> 2)
    //io.mem_rdata := RegNext(memory((io.mem_addr >> 2)))

    // For debugging
    io.mem_rdata2 := memory(io.mem_addr2 >> 2)
}


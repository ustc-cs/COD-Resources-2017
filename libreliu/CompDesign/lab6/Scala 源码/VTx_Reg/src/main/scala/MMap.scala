package multicpu

import chisel3._
import chisel3.util._

import vga._

// // Datapath perspective
// class MemPort extends Bundle {
//     val mem_rdata = Input(UInt(32.W))
//     val mem_addr = Output(UInt(32.W))
//     val mem_wdata = Output(UInt(32.W))
//     val mem_wen   = Output(Bool())

//     // Extra reading port for debugging
//     val mem_addr2 = Output(UInt(32.W))
//     val mem_rdata2 = Input(UInt(32.W))
// }

class MemCellPort extends Bundle {
    val mem_rdata = Input(UInt(32.W))
    val mem_wdata = Output(UInt(32.W))
    val mem_wen   = Output(Bool())
}

class VRamPort extends Bundle {
    val mem_addr  = Output(UInt(32.W))
    val mem_wdata = Output(UInt(16.W))
    val mem_wen   = Output(Bool())
}
// 0 ~ 3FF(1023) : Prog Mem, 1024 bytes (as 128 Words)
// 3000(12288) ~ 4F40(20288) : Disp Mem, 80*25 words (but upper 2 byte is always zero)
// FF00(65280) : UART Mem
class MMap extends Module {
    val io = IO(new Bundle {
        // MMap -> Datapath
        val mmap_port = Flipped(new MemPort)
        // Mem -> MMap
        val mem_port  = new MemPort
        // Uart -> MMap
        val uart_port = new MemCellPort
        // VRam -> MMap
        val vram_port = new VRamPort
    })

    val addr_in_mem = ((io.mmap_port.mem_addr >= 0.U) && (io.mmap_port.mem_addr < 1024.U))
    val addr_in_uart = (io.mmap_port.mem_addr === 65280.U)
    val addr_in_vram = (io.mmap_port.mem_addr >= 12288.U) && (io.mmap_port.mem_addr < 20289.U)

    io.mmap_port.mem_rdata := MuxCase(0.U, Seq(
        addr_in_mem  -> io.mem_port.mem_rdata,
        addr_in_uart -> io.uart_port.mem_rdata,
        addr_in_vram -> 0.U
    ))

    //io.mem_port.mem_rdata
    io.mem_port.mem_addr  := io.mmap_port.mem_addr
    io.mem_port.mem_wdata := io.mmap_port.mem_wdata
    io.mem_port.mem_wen   := (io.mmap_port.mem_wen && addr_in_mem)
    io.mem_port.mem_addr2 := io.mmap_port.mem_addr2

    io.mmap_port.mem_rdata2 := io.mem_port.mem_rdata2

    //io.uart_port.mem_rdata
    io.uart_port.mem_wdata := io.mmap_port.mem_wdata
    io.uart_port.mem_wen   := (io.mmap_port.mem_wen && addr_in_uart)

    io.vram_port.mem_addr := (io.mmap_port.mem_addr - 12288.U) >> 2
    io.vram_port.mem_wdata := io.mmap_port.mem_wdata(15,0)
    io.vram_port.mem_wen   := (io.mmap_port.mem_wen && addr_in_vram)
}


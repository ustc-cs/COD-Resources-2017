package ddu

import multicpu._

import chisel3._
import chisel3.util._

object Config {
    val delay_1ms = 5000  // 1ms under 5MHz freq
    val delay_1us = 5     // 1us under 5MHz freq
}

class SegDisp extends Bundle {
    val seg = Output(UInt(7.W))
    val dp  = Output(Bool())
    val an  = Output(UInt(8.W))
}

class DDU extends Module {
    val io = IO(new Bundle {
        val cont = Input(Bool())
        val step = Input(Bool())
        val mem  = Input(Bool())
        val inc  = Input(Bool())
        val dec  = Input(Bool())
        val disp = new SegDisp
        val led  = Output(UInt(16.W))

        val vram_port = new Bundle {
            val rdata = Input(UInt(16.W))
            val raddr = Output(UInt(16.W))

            val wdata = Output(UInt(16.W))
            val waddr = Output(UInt(32.W))
            val wen   = Output(Bool())
        }
        val uart_port = new MemCellPort
        val vga_sig = new vga.VGASig
    })

    val cpu = Module(new CPU())
    val mmap = Module(new MMap())

    val main_mem = Module(new multicpu.Mem(256))

    val vga_inst = Module(new vga.VGA)

    cpu.io.mem_port <> mmap.io.mmap_port
    mmap.io.mem_port <> main_mem.io
    mmap.io.uart_port <> io.uart_port

    vga_inst.io.sig <> io.vga_sig
    
    vga_inst.io.vram_rdata  := io.vram_port.rdata 
    io.vram_port.raddr := vga_inst.io.vram_raddr

    io.vram_port.wdata := mmap.io.vram_port.mem_wdata
    io.vram_port.waddr := mmap.io.vram_port.mem_addr
    io.vram_port.wen   := mmap.io.vram_port.mem_wen


    // --------- Legacy DDU Code -----------

    def toBin(b: BitPat): UInt = BitPat.bitPatToUInt(b)
    def Decode(n: UInt) = MuxLookup(n, toBin(BitPat("b1000000")), Seq(
        0.U -> toBin(BitPat("b1000000")),
        1.U -> toBin(BitPat("b1111001")),
        2.U -> toBin(BitPat("b0100100")),
        3.U -> toBin(BitPat("b0110000")),
        4.U -> toBin(BitPat("b0011001")),
        5.U -> toBin(BitPat("b0010010")),
        6.U -> toBin(BitPat("b0000010")),
        7.U -> toBin(BitPat("b1111000")),
        8.U -> toBin(BitPat("b0000000")),
        9.U -> toBin(BitPat("b0010000")),
        10.U -> toBin(BitPat("b0001000")),
        11.U -> toBin(BitPat("b0000011")),
        12.U -> toBin(BitPat("b1000110")),
        13.U -> toBin(BitPat("b0100001")),
        14.U -> toBin(BitPat("b0000110")),
        15.U -> toBin(BitPat("b0001110"))
    ))

    def counter(max: UInt) = {
        val x = RegInit(0.U(max.getWidth.W))
        x := Mux(x === max, 0.U, x + 1.U)
        x
    }

    def counter(max: UInt, carry: Bool) = {
        val x = RegInit(0.U(max.getWidth.W))
        x := Mux(carry, Mux(x === max, 0.U, x + 1.U), x)
        x
    }

    def anti_jitter(sig: Bool, n: Int) : Bool = {
        val x = RegNext(sig)
        val rising_edge = sig && !x 
        val cnt = RegInit(0.U(n.U.getWidth.W))

        val idle :: count :: Nil = Enum(2)
        val cur_state = RegInit(idle)
        val res = RegInit(false.B)
        when (cur_state === idle) {
            when ( rising_edge ) {
                cur_state := count
                cnt       := n.U
                res       := true.B
            }
        } 
        .elsewhen (cur_state === count) {
            res := false.B
            when (cnt === 0.U) {
                cur_state := idle
            } .otherwise {
                cnt       := cnt - 1.U
            }
        } .otherwise {
            res := false.B
            cur_state     := idle
        }
        res
    }
    //---- Display -----
    // Sweep Generator
    val step  = counter(7.U, counter((Config.delay_1ms * 1).U) === 0.U)  // 0 ~ 7, with freq divider
    io.disp.an := ~(1.U(7.W) << step)

    // data to be displayed
    val disp_data = Wire(UInt(32.W))
    io.disp.seg := Decode(MuxLookup(step, 0.U, Seq(
        0.U -> disp_data(3,0),
        1.U -> disp_data(7,4),
        2.U -> disp_data(11,8),
        3.U -> disp_data(15,12),
        4.U -> disp_data(19,16),
        5.U -> disp_data(23,20),
        6.U -> disp_data(27,24),
        7.U -> disp_data(31,28)
    )))
    // Dot
    io.disp.dp := false.B
    //----- data ------
    disp_data := Mux(io.mem, cpu.io.debug.mr_d, cpu.io.debug.rd_c)
    val disp_addr = RegInit(0.U(32.W))
    when (anti_jitter(io.inc, (Config.delay_1ms * 500)))  {
        disp_addr := disp_addr + 1.U
    } .elsewhen (anti_jitter(io.dec, (Config.delay_1ms * 500))) {
        disp_addr := disp_addr - 1.U
    }

    cpu.io.debug.mr_a := disp_addr
    cpu.io.debug.ra_c := disp_addr(4,0)
    //---- single step control ---
    val pressed = anti_jitter(io.step, (Config.delay_1ms * 500))  // will lasts for 1 cycle while pressed
    // io.cont = 1 => single = 0
    // io.cont = 0 => when pressed, single = 0, else single = 1
    cpu.io.single := !(pressed || io.cont)

    //--- led ---
    // upper 8 bits for RF/(MEM ADDR >> 2)
    // lower 8 bits for PC addr >> 2

    io.led := Cat(  Mux(io.mem, disp_addr(9,0) >> 2, disp_addr(4,0))  , (cpu.io.debug.pc(9,0) >> 2)(7,0) )
}
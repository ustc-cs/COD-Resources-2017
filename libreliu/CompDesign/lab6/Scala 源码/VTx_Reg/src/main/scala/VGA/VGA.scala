package vga

import chisel3._
import chisel3.util._

import scala.collection.mutable.ListBuffer

// CGROM data
object CGROMData {
  val raw_data = Array(
    Array(0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00),/*" ",0*/
    Array(0x00,0x00,0x18,0x3C,0x3C,0x3C,0x18,0x18,0x18,0x00,0x18,0x18,0x00,0x00,0x00,0x00),/*"!",1*/
    Array(0x00,0x66,0x66,0x66,0x24,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00),/*""",2*/
    Array(0x00,0x00,0x00,0x36,0x36,0x7F,0x36,0x36,0x36,0x7F,0x36,0x36,0x00,0x00,0x00,0x00),/*"#",3*/
    Array(0x18,0x18,0x3E,0x63,0x43,0x03,0x3E,0x60,0x60,0x61,0x63,0x3E,0x18,0x18,0x00,0x00),/*"$",4*/
    Array(0x00,0x00,0x00,0x00,0x43,0x63,0x30,0x18,0x0C,0x06,0x63,0x61,0x00,0x00,0x00,0x00),/*"%",5*/
    Array(0x00,0x00,0x1C,0x36,0x36,0x1C,0x6E,0x3B,0x33,0x33,0x33,0x6E,0x00,0x00,0x00,0x00),/*"&",6*/
    Array(0x00,0x0C,0x0C,0x0C,0x04,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00),/*"'",7*/
    Array(0x00,0x00,0x30,0x18,0x0C,0x0C,0x0C,0x0C,0x0C,0x0C,0x18,0x30,0x00,0x00,0x00,0x00),/*"(",8*/
    Array(0x00,0x00,0x0C,0x18,0x30,0x30,0x30,0x30,0x30,0x30,0x18,0x0C,0x00,0x00,0x00,0x00),/*")",9*/
    Array(0x00,0x00,0x00,0x00,0x00,0x66,0x3C,0xFF,0x3C,0x66,0x00,0x00,0x00,0x00,0x00,0x00),/*"*",10*/
    Array(0x00,0x00,0x00,0x00,0x00,0x18,0x18,0x7E,0x18,0x18,0x00,0x00,0x00,0x00,0x00,0x00),/*"+",11*/
    Array(0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x18,0x18,0x18,0x0C,0x00,0x00,0x00),/*",",12*/
    Array(0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x7F,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00),/*"-",13*/
    Array(0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x18,0x18,0x00,0x00,0x00,0x00),/*".",14*/
    Array(0x00,0x00,0x00,0x00,0x40,0x60,0x30,0x18,0x0C,0x06,0x03,0x01,0x00,0x00,0x00,0x00),/*"/",15*/
    Array(0x00,0x00,0x1C,0x36,0x63,0x63,0x6B,0x6B,0x63,0x63,0x36,0x1C,0x00,0x00,0x00,0x00),/*"0",16*/
    Array(0x00,0x00,0x18,0x1C,0x1E,0x18,0x18,0x18,0x18,0x18,0x18,0x7E,0x00,0x00,0x00,0x00),/*"1",17*/
    Array(0x00,0x00,0x3E,0x63,0x60,0x30,0x18,0x0C,0x06,0x03,0x63,0x7F,0x00,0x00,0x00,0x00),/*"2",18*/
    Array(0x00,0x00,0x3E,0x63,0x60,0x60,0x3C,0x60,0x60,0x60,0x63,0x3E,0x00,0x00,0x00,0x00),/*"3",19*/
    Array(0x00,0x00,0x30,0x38,0x3C,0x36,0x33,0x7F,0x30,0x30,0x30,0x78,0x00,0x00,0x00,0x00),/*"4",20*/
    Array(0x00,0x00,0x7F,0x03,0x03,0x03,0x3F,0x60,0x60,0x60,0x63,0x3E,0x00,0x00,0x00,0x00),/*"5",21*/
    Array(0x00,0x00,0x1C,0x06,0x03,0x03,0x3F,0x63,0x63,0x63,0x63,0x3E,0x00,0x00,0x00,0x00),/*"6",22*/
    Array(0x00,0x00,0x7F,0x63,0x60,0x60,0x30,0x18,0x0C,0x0C,0x0C,0x0C,0x00,0x00,0x00,0x00),/*"7",23*/
    Array(0x00,0x00,0x3E,0x63,0x63,0x63,0x3E,0x63,0x63,0x63,0x63,0x3E,0x00,0x00,0x00,0x00),/*"8",24*/
    Array(0x00,0x00,0x3E,0x63,0x63,0x63,0x7E,0x60,0x60,0x60,0x30,0x1E,0x00,0x00,0x00,0x00),/*"9",25*/
    Array(0x00,0x00,0x00,0x00,0x18,0x18,0x00,0x00,0x00,0x18,0x18,0x00,0x00,0x00,0x00,0x00),/*":",26*/
    Array(0x00,0x00,0x00,0x00,0x18,0x18,0x00,0x00,0x00,0x18,0x18,0x0C,0x00,0x00,0x00,0x00),/*";",27*/
    Array(0x00,0x00,0x00,0x60,0x30,0x18,0x0C,0x06,0x0C,0x18,0x30,0x60,0x00,0x00,0x00,0x00),/*"<",28*/
    Array(0x00,0x00,0x00,0x00,0x00,0x7E,0x00,0x00,0x7E,0x00,0x00,0x00,0x00,0x00,0x00,0x00),/*"=",29*/
    Array(0x00,0x00,0x00,0x06,0x0C,0x18,0x30,0x60,0x30,0x18,0x0C,0x06,0x00,0x00,0x00,0x00),/*">",30*/
    Array(0x00,0x00,0x3E,0x63,0x63,0x30,0x18,0x18,0x18,0x00,0x18,0x18,0x00,0x00,0x00,0x00),/*"?",31*/
    Array(0x00,0x00,0x00,0x3E,0x63,0x63,0x7B,0x7B,0x7B,0x3B,0x03,0x3E,0x00,0x00,0x00,0x00),/*"@",32*/
    Array(0x00,0x00,0x08,0x1C,0x36,0x63,0x63,0x7F,0x63,0x63,0x63,0x63,0x00,0x00,0x00,0x00),/*"A",33*/
    Array(0x00,0x00,0x3F,0x66,0x66,0x66,0x3E,0x66,0x66,0x66,0x66,0x3F,0x00,0x00,0x00,0x00),/*"B",34*/
    Array(0x00,0x00,0x3C,0x66,0x43,0x03,0x03,0x03,0x03,0x43,0x66,0x3C,0x00,0x00,0x00,0x00),/*"C",35*/
    Array(0x00,0x00,0x1F,0x36,0x66,0x66,0x66,0x66,0x66,0x66,0x36,0x1F,0x00,0x00,0x00,0x00),/*"D",36*/
    Array(0x00,0x00,0x7F,0x66,0x46,0x16,0x1E,0x16,0x06,0x46,0x66,0x7F,0x00,0x00,0x00,0x00),/*"E",37*/
    Array(0x00,0x00,0x7F,0x66,0x46,0x16,0x1E,0x16,0x06,0x06,0x06,0x0F,0x00,0x00,0x00,0x00),/*"F",38*/
    Array(0x00,0x00,0x3C,0x66,0x43,0x03,0x03,0x7B,0x63,0x63,0x66,0x5C,0x00,0x00,0x00,0x00),/*"G",39*/
    Array(0x00,0x00,0x63,0x63,0x63,0x63,0x7F,0x63,0x63,0x63,0x63,0x63,0x00,0x00,0x00,0x00),/*"H",40*/
    Array(0x00,0x00,0x3C,0x18,0x18,0x18,0x18,0x18,0x18,0x18,0x18,0x3C,0x00,0x00,0x00,0x00),/*"I",41*/
    Array(0x00,0x00,0x78,0x30,0x30,0x30,0x30,0x30,0x33,0x33,0x33,0x1E,0x00,0x00,0x00,0x00),/*"J",42*/
    Array(0x00,0x00,0x67,0x66,0x66,0x36,0x1E,0x1E,0x36,0x66,0x66,0x67,0x00,0x00,0x00,0x00),/*"K",43*/
    Array(0x00,0x00,0x0F,0x06,0x06,0x06,0x06,0x06,0x06,0x46,0x66,0x7F,0x00,0x00,0x00,0x00),/*"L",44*/
    Array(0x00,0x00,0x63,0x77,0x7F,0x7F,0x6B,0x63,0x63,0x63,0x63,0x63,0x00,0x00,0x00,0x00),/*"M",45*/
    Array(0x00,0x00,0x63,0x67,0x6F,0x7F,0x7B,0x73,0x63,0x63,0x63,0x63,0x00,0x00,0x00,0x00),/*"N",46*/
    Array(0x00,0x00,0x3E,0x63,0x63,0x63,0x63,0x63,0x63,0x63,0x63,0x3E,0x00,0x00,0x00,0x00),/*"O",47*/
    Array(0x00,0x00,0x3F,0x66,0x66,0x66,0x3E,0x06,0x06,0x06,0x06,0x0F,0x00,0x00,0x00,0x00),/*"P",48*/
    Array(0x00,0x00,0x3E,0x63,0x63,0x63,0x63,0x63,0x63,0x6B,0x7B,0x3E,0x30,0x70,0x00,0x00),/*"Q",49*/
    Array(0x00,0x00,0x3F,0x66,0x66,0x66,0x3E,0x36,0x66,0x66,0x66,0x67,0x00,0x00,0x00,0x00),/*"R",50*/
    Array(0x00,0x00,0x3E,0x63,0x63,0x06,0x1C,0x30,0x60,0x63,0x63,0x3E,0x00,0x00,0x00,0x00),/*"S",51*/
    Array(0x00,0x00,0x7E,0x7E,0x5A,0x18,0x18,0x18,0x18,0x18,0x18,0x3C,0x00,0x00,0x00,0x00),/*"T",52*/
    Array(0x00,0x00,0x63,0x63,0x63,0x63,0x63,0x63,0x63,0x63,0x63,0x3E,0x00,0x00,0x00,0x00),/*"U",53*/
    Array(0x00,0x00,0x63,0x63,0x63,0x63,0x63,0x63,0x63,0x36,0x1C,0x08,0x00,0x00,0x00,0x00),/*"V",54*/
    Array(0x00,0x00,0x63,0x63,0x63,0x63,0x6B,0x6B,0x6B,0x7F,0x77,0x36,0x00,0x00,0x00,0x00),/*"W",55*/
    Array(0x00,0x00,0x63,0x63,0x36,0x3E,0x1C,0x1C,0x3E,0x36,0x63,0x63,0x00,0x00,0x00,0x00),/*"X",56*/
    Array(0x00,0x00,0x66,0x66,0x66,0x66,0x3C,0x18,0x18,0x18,0x18,0x3C,0x00,0x00,0x00,0x00),/*"Y",57*/
    Array(0x00,0x00,0x7F,0x63,0x61,0x30,0x18,0x0C,0x06,0x43,0x63,0x7F,0x00,0x00,0x00,0x00),/*"Z",58*/
    Array(0x00,0x00,0x3C,0x0C,0x0C,0x0C,0x0C,0x0C,0x0C,0x0C,0x0C,0x3C,0x00,0x00,0x00,0x00),/*"[",59*/
    Array(0x00,0x00,0x00,0x01,0x03,0x07,0x0E,0x1C,0x38,0x70,0x60,0x40,0x00,0x00,0x00,0x00),/*"\",60*/
    Array(0x00,0x00,0x3C,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x3C,0x00,0x00,0x00,0x00),/*"]",61*/
    Array(0x08,0x1C,0x36,0x63,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00),/*"^",62*/
    Array(0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xFF,0x00,0x00),/*"_",63*/
    Array(0x0C,0x0C,0x18,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00),/*"`",64*/
    Array(0x00,0x00,0x00,0x00,0x00,0x1E,0x30,0x3E,0x33,0x33,0x33,0x6E,0x00,0x00,0x00,0x00),/*"a",65*/
    Array(0x00,0x00,0x07,0x06,0x06,0x1E,0x36,0x66,0x66,0x66,0x66,0x3E,0x00,0x00,0x00,0x00),/*"b",66*/
    Array(0x00,0x00,0x00,0x00,0x00,0x3E,0x63,0x03,0x03,0x03,0x63,0x3E,0x00,0x00,0x00,0x00),/*"c",67*/
    Array(0x00,0x00,0x38,0x30,0x30,0x3C,0x36,0x33,0x33,0x33,0x33,0x6E,0x00,0x00,0x00,0x00),/*"d",68*/
    Array(0x00,0x00,0x00,0x00,0x00,0x3E,0x63,0x7F,0x03,0x03,0x63,0x3E,0x00,0x00,0x00,0x00),/*"e",69*/
    Array(0x00,0x00,0x1C,0x36,0x26,0x06,0x0F,0x06,0x06,0x06,0x06,0x0F,0x00,0x00,0x00,0x00),/*"f",70*/
    Array(0x00,0x00,0x00,0x00,0x00,0x6E,0x33,0x33,0x33,0x33,0x33,0x3E,0x30,0x33,0x1E,0x00),/*"g",71*/
    Array(0x00,0x00,0x07,0x06,0x06,0x36,0x6E,0x66,0x66,0x66,0x66,0x67,0x00,0x00,0x00,0x00),/*"h",72*/
    Array(0x00,0x00,0x18,0x18,0x00,0x1C,0x18,0x18,0x18,0x18,0x18,0x3C,0x00,0x00,0x00,0x00),/*"i",73*/
    Array(0x00,0x00,0x60,0x60,0x00,0x70,0x60,0x60,0x60,0x60,0x60,0x60,0x66,0x66,0x3C,0x00),/*"j",74*/
    Array(0x00,0x00,0x07,0x06,0x06,0x66,0x36,0x1E,0x1E,0x36,0x66,0x67,0x00,0x00,0x00,0x00),/*"k",75*/
    Array(0x00,0x00,0x1C,0x18,0x18,0x18,0x18,0x18,0x18,0x18,0x18,0x3C,0x00,0x00,0x00,0x00),/*"l",76*/
    Array(0x00,0x00,0x00,0x00,0x00,0x37,0x7F,0x6B,0x6B,0x6B,0x6B,0x63,0x00,0x00,0x00,0x00),/*"m",77*/
    Array(0x00,0x00,0x00,0x00,0x00,0x3B,0x66,0x66,0x66,0x66,0x66,0x66,0x00,0x00,0x00,0x00),/*"n",78*/
    Array(0x00,0x00,0x00,0x00,0x00,0x3E,0x63,0x63,0x63,0x63,0x63,0x3E,0x00,0x00,0x00,0x00),/*"o",79*/
    Array(0x00,0x00,0x00,0x00,0x00,0x3B,0x66,0x66,0x66,0x66,0x66,0x3E,0x06,0x06,0x0F,0x00),/*"p",80*/
    Array(0x00,0x00,0x00,0x00,0x00,0x6E,0x33,0x33,0x33,0x33,0x33,0x3E,0x30,0x30,0x78,0x00),/*"q",81*/
    Array(0x00,0x00,0x00,0x00,0x00,0x3B,0x6E,0x66,0x06,0x06,0x06,0x0F,0x00,0x00,0x00,0x00),/*"r",82*/
    Array(0x00,0x00,0x00,0x00,0x00,0x3E,0x63,0x06,0x1C,0x30,0x63,0x3E,0x00,0x00,0x00,0x00),/*"s",83*/
    Array(0x00,0x00,0x08,0x0C,0x0C,0x3F,0x0C,0x0C,0x0C,0x0C,0x6C,0x38,0x00,0x00,0x00,0x00),/*"t",84*/
    Array(0x00,0x00,0x00,0x00,0x00,0x33,0x33,0x33,0x33,0x33,0x33,0x6E,0x00,0x00,0x00,0x00),/*"u",85*/
    Array(0x00,0x00,0x00,0x00,0x00,0x66,0x66,0x66,0x66,0x66,0x3C,0x18,0x00,0x00,0x00,0x00),/*"v",86*/
    Array(0x00,0x00,0x00,0x00,0x00,0x63,0x63,0x6B,0x6B,0x6B,0x7F,0x36,0x00,0x00,0x00,0x00),/*"w",87*/
    Array(0x00,0x00,0x00,0x00,0x00,0x63,0x36,0x1C,0x1C,0x1C,0x36,0x63,0x00,0x00,0x00,0x00),/*"x",88*/
    Array(0x00,0x00,0x00,0x00,0x00,0x63,0x63,0x63,0x63,0x63,0x63,0x7E,0x60,0x30,0x1F,0x00),/*"y",89*/
    Array(0x00,0x00,0x00,0x00,0x00,0x7F,0x33,0x18,0x0C,0x06,0x63,0x7F,0x00,0x00,0x00,0x00),/*"z",90*/
    Array(0x00,0x00,0x70,0x18,0x18,0x18,0x0E,0x18,0x18,0x18,0x18,0x70,0x00,0x00,0x00,0x00),/*"{",91*/
    Array(0x00,0x00,0x18,0x18,0x18,0x18,0x00,0x18,0x18,0x18,0x18,0x18,0x00,0x00,0x00,0x00),/*"|",92*/
    Array(0x00,0x00,0x0E,0x18,0x18,0x18,0x70,0x18,0x18,0x18,0x18,0x0E,0x00,0x00,0x00,0x00),/*"}",93*/
    Array(0x00,0x00,0x6E,0x3B,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00) /*"~",94*/
  )

  val data = {
    var li = new ListBuffer[UInt]()
    for (i <- raw_data) {
      for (j <- i) {
        li += j.U
      }
    }
    li.toSeq
  }
}

// Conform to VGA Textmode Spec
// |     Attribute       |           Character          |
// | 15 |  14 13 12      |    11 10 9 8    |  7 ... 0   |
// Blink Background color Foreground color | Code point |
class VRam extends Module {
  val io = IO(new Bundle {
    val rdata = Output(UInt(16.W))
    val raddr = Input(UInt(16.W))

    val wdata = Input(UInt(16.W))
    val waddr = Input(UInt(32.W))
    val wen   = Input(Bool())
  })
  val init_chars = (32 to 126).map(i => Cat(0.U(1.W), 0.U(3.W), 15.U(4.W),i.U(8.W)) ).toSeq
  val memory = RegInit( VecInit( Seq.concat( init_chars, Seq.fill(80 * 25 - (init_chars.length))(50.U(16.W)) ) ))

  for (p <- 0 until (80*25)) {
    when (io.wen && (io.waddr) === p.U) {
        memory(p) := io.wdata
    }

    // VRam primitive to simulate behavior of Block mem
    //io.mem_rdata := memory(io.raddr)
    io.rdata := RegNext(memory((io.raddr)))
  }
}

// Operate entirely at VGACore Freq.
// VGA Textmode : 80x25, 720x400p85
// Text: 9*16 (720 / 80 = 9; 400 / 25 = 16)
class VGA extends Module {
  val io = IO(new Bundle {
    val sig = new VGASig
    val vram_raddr = Output(UInt(32.W))
    val vram_rdata = Input(UInt(16.W))
  })

  val core = Module(new VGACore)
  val cgrom = Module(new CGROM)

  core.io.sig <> io.sig
  // Row: 0 ~ 399
  // Col: 0 ~ 719
  // To synchronize with frame start; will reset to 1 once start asserts
  val start = (core.io.row === 0.U) && (core.io.col === 0.U)

  // 0 ~ 8, 9 total
  val (col_ctick, col_ctick_carry) = VGACounter(core.io.ready, start, 9, 1)
  // 0 ~ 79, 80 total
  val (col_tick, col_tick_carry) = VGACounter(col_ctick_carry, start, 80, 0)

  // 0 ~ 15 (character), only +1 as in valid paint region
  val (row_ctick, row_ctick_carry) = VGACounter(col_tick_carry, start, 16, 0)
  // 0 ~ 24
  val (row_tick, row_tick_carry) = VGACounter(row_ctick_carry, start, 25, 0)

  val bg_color = MuxLookup(io.vram_rdata(14,12), 0.U(12.W), Seq(
    0.U -> Cat(0.U(4.W), 0.U(4.W), 0.U(4.W)),         // Black
    1.U -> Cat(0.U(4.W), 0.U(4.W), 12.U(4.W)),        // Blue
    2.U -> Cat(0.U(4.W), 12.U(4.W), 0.U(4.W)),        // Green
    3.U -> Cat(0.U(4.W), 12.U(4.W), 12.U(4.W)),       // Cyan
    4.U -> Cat(12.U(4.W), 0.U(4.W), 0.U(4.W)),        // Red
    5.U -> Cat(12.U(4.W), 0.U(4.W), 12.U(4.W)),       // Magenta
    6.U -> Cat(12.U(4.W), 6.U(4.W), 0.U(4.W)),        // Brown
    7.U -> Cat(12.U(4.W), 12.U(4.W), 12.U(4.W)),       // Gray
    15.U ->  Cat(15.U(4.W), 15.U(4.W), 15.U(4.W))       // White
  ))

  val fg_color = MuxLookup(io.vram_rdata(11,8), 0.U(12.W), Seq(
    0.U -> Cat(0.U(4.W), 0.U(4.W), 0.U(4.W)),         // Black
    1.U -> Cat(0.U(4.W), 0.U(4.W), 12.U(4.W)),        // Blue
    2.U -> Cat(0.U(4.W), 12.U(4.W), 0.U(4.W)),        // Green
    3.U -> Cat(0.U(4.W), 12.U(4.W), 12.U(4.W)),       // Cyan
    4.U -> Cat(12.U(4.W), 0.U(4.W), 0.U(4.W)),        // Red
    5.U -> Cat(12.U(4.W), 0.U(4.W), 12.U(4.W)),       // Magenta
    6.U -> Cat(12.U(4.W), 6.U(4.W), 0.U(4.W)),        // Brown
    7.U -> Cat(12.U(4.W), 12.U(4.W), 12.U(4.W)),       // Gray
    15.U ->  Cat(15.U(4.W), 15.U(4.W), 15.U(4.W))       // White
  ))

  // Look for the next char *if necessary*
  // io.vram_raddr := Mux((row_tick * 80.U + col_tick + 1.U === 2000.U), 0.U, (row_tick * 80.U + col_tick + 1.U))
  // io.vram_raddr := MuxCase(0.U, Seq(
  //   // Between frame: look for the next
  //   (core.io.col.asSInt < 0.S) -> (row_tick * 80.U + col_tick),
  //   // not ready to disp next char: use current
  //   (col_ctick =/= 8.U) -> ()
  //   // Started
  //   ((core.io.col.asSInt >= 0.S) && (row_tick * 80.U + col_tick + 1.U =/= 2000.U)) -> (row_tick * 80.U + col_tick + 1.U),
  //   // Wrapped
  //   ((core.io.col.asSInt >= 0.S) && (row_tick * 80.U + col_tick + 1.U === 2000.U)) -> 0.U
  // ))
  val frame_bp = (core.io.row === 0.U) && (core.io.col.asSInt < 0.S)
  val reach_the_end = (row_tick * 80.U + col_tick + 1.U === 2000.U)

  val this_char_addr = (row_tick * 80.U + col_tick)
  val next_char_addr = Wire(UInt(32.W))
  when (col_tick === 79.U) {
    when (row_ctick === 15.U) {  // We need next row
      next_char_addr := Mux((row_tick + 1.U) * 80.U === 2000.U, 0.U, (row_tick + 1.U) * 80.U)
    } .otherwise {
      next_char_addr := row_tick * 80.U
    }
  } .otherwise {
    next_char_addr := Mux(reach_the_end, 0.U, row_tick * 80.U + col_tick + 1.U)
  }
  
  io.vram_raddr := Mux((col_ctick =/= 8.U && !frame_bp), this_char_addr, MuxCase(0.U, Seq(
    // Between frame: look for the next
    ((frame_bp) || (reach_the_end))  -> (0.U),
    (!reach_the_end) -> next_char_addr
  )))
  // CGRom is Async
  cgrom.io.addr := io.vram_rdata(7,0)
  cgrom.io.row  := row_ctick

  core.io.color := Mux(col_ctick === 8.U, bg_color, Mux(cgrom.io.data(col_ctick), fg_color, bg_color) )
}

// 8*16 CGROM, get by row
/* There are modes with a character box width of 9 dots
 * (e.g. the default 80×25 mode), however the 9th column 
 * is used for spacing between characters, so the content
 * cannot be changed. It is always blank, and drawn with 
 * the current background colour.
 */
// https://github.com/freechipsproject/chisel3/wiki/BlackBoxes


// class CGROM extends Module {
//   val io = IO(new Bundle {
//     // addr = 0 ~ 255
//     val addr = Input(UInt(8.W))
//     // row = 0 ~ 15
//     val row  = Input(UInt(4.W))
//     val data = Output(UInt(8.W))
//   })
//   val rom = RegInit(VecInit(CGROMData.data))
//   io.data := MuxCase(0.U, Array(
//     (io.addr < 32.U)                    -> 0.U,
//     (io.addr >= 32.U && io.addr <= 127.U) -> rom( (io.addr - 32.U) * 16.U + io.row )
//   ))
// }

import chisel3.experimental._

class CGROMBlackbox extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle{
    val clock = Input(Clock())
    // addr = 0 ~ 255
    val addr = Input(UInt(8.W))
    // row = 0 ~ 15
    val row  = Input(UInt(4.W))
    val data = Output(UInt(8.W))
  })
  // Won't work, since Chisel 2 here..
  // addClock(Driver.implicitClock) // will now add a "clk" here~
  // renameClock(Chisel.Driver.implicitClock, "clock")
  // renameReset("reset")
  setResource("/BlackBoxCGROM.v")
}

// Won't work
// Shim because Blackbox io is slightly different than normal Chisel Modules
class CGROM extends Module {
  val io = IO(new Bundle{
    // val clock = Input(Clock())
    // val reset = Input(Bool())
    // addr = 0 ~ 255
    val addr = Input(UInt(8.W))
    // row = 0 ~ 15
    val row  = Input(UInt(4.W))
    val data = Output(UInt(8.W))
  })
  val impl = Module(new CGROMBlackbox)
  impl.io.clock := clock
  impl.io.addr := io.addr
  impl.io.row := io.row
  io.data := impl.io.data
}
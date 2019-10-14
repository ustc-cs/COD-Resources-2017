// Thanks to iBug for its VGA Sources.

package vga

import chisel3._
import chisel3.util._

/* hd: Horizontal Visible Area
 * hf: Horizontal Front Porch
 * hs: Horizontal Sync Pulse
 * hb: Horizontal Back Porch
 * vd: Vertical Visible Area
 * vf: Vertical Front Porch
 * vs: Vertical Sync Pulse
 * vb: Vertical Back Porch
 */

object VGAConfig {
  val config = Map(
    //                    |  hd  |  hf  |  hs  |  hb  |  vd   |  vf  |  vs  |  vb  |
    "640x480@60"   -> List(  640 ,  16  ,   96 ,   48 ,  480  ,  10  ,   2  ,  31  ),
    // Not widely supported, at 85Hz
    "720x400@85"   -> List(  720 ,  36  ,   72 ,  108 ,  400  ,  1   ,   3  ,  42  ),
    "720x400@70"   -> List(  720 ,  15  ,  108 ,   51 ,  400  ,  11  ,   2  ,  32  ),
    "800x600@60"   -> List(  800 ,  40  ,  128 ,   88 ,  600  ,  1   ,   4  ,  23  ),
    "800x600@72"   -> List(  800 ,  56  ,  120 ,   64 ,  600  ,  37  ,   6  ,  23  ),
    "1024x768@60"  -> List( 1024 ,  24  ,  136 ,  160 ,  768  ,  3   ,   6  ,  29  )
  )
  val refresh_freq = Map(
    "640x480@60"   -> 25175000,   // 25.175 MHz Pixel Freq
    "720x400@85"   -> 35500000,   // 35.500 MHz Pixel Freq
    "720x400@70"   -> 28322000,
    "800x600@60"   -> 40000000,   // 40.000 MHz Pixel Freq
    "800x600@72"   -> 50000000,   // 50.000 MHz Pixel Freq
    "1024x768@60"  -> 65000000    // 65.000 MHz Pixel Freq
  )

  val mode_selected = "720x400@70"
}

/*

module dcu
# (parameter ROW_WIDTH = 32, // 800*600*12(4:4:4) bpp
   parameter COL_WIDTH = 32,
   parameter WIDTH = 800,
   parameter HEIGHT = 600,
   parameter H_BP = 64,
   parameter H_FP = 56,
   parameter H_SPW = 120,
   parameter V_BP = 23,   //Vertical Back Porch
   parameter V_FP = 37,   //Vertical Front Porch
   parameter V_SPW = 6    //Vertical Sync Pulse Width
)(
    input pclk,
    input rst,
    output [ROW_WIDTH - 1: 0] row,
    output [ROW_WIDTH - 1: 0] col,
    output ready,      // if ready, row and col info are valid.
    input [11:0] color, // BBBB GGGG RRRR
    output [3:0] r,
    output [3:0] g,
    output [3:0] b,
    output hsync,
    output vsync
    );
// [V_SPW -> V_BP -> [H_SPW -> H_BP -> WIDTH -> H_FP] * HEIGHT -> V_FP] per frame

    reg [31:0] h_tick;  //could be smaller, but for convenience
    reg [31:0] v_tick;  //as well
    
    always @ (posedge pclk) begin
        if (rst) begin
            h_tick <= 0;
            v_tick <= 0;
        end else begin
            if (h_tick >= H_SPW + H_BP + WIDTH + H_FP - 1) begin   //h_tick [0, H_SPW + H_BP + WIDTH + H_FP - 1]
                h_tick <= 0;
                if (v_tick >= V_SPW + HEIGHT + V_BP + V_FP - 1)    //v_tick [0, V_SPW + HEIGHT + V_BP + V_FP - 1]
                    v_tick <= 0;
                else
                    v_tick <= v_tick + 1;
            end else
                h_tick <= h_tick + 1;
        end
    end
    
    assign ready = (h_tick >= H_SPW + H_BP) && (h_tick <= H_SPW + H_BP + WIDTH)
                     && (v_tick >= V_SPW + V_BP) && (v_tick <= V_SPW + V_BP + HEIGHT);
    
    assign r = ready ? color[3:0] : 0;  //During FP/BP it's okay to send garbage data, but *don't generate extra latches*
    assign g = ready ? color[7:4] : 0;
    assign b = ready ? color[11:8] : 0;
    
    // Pixel Control
    assign col = h_tick - H_SPW - H_BP;
    assign row = v_tick - V_SPW - V_BP;
    
    //Sync Control
    assign hsync = h_tick < H_SPW ? 0 : 1;
    assign vsync = v_tick < V_SPW ? 0 : 1;
    
endmodule
*/

class VGASig extends Bundle {
  val r = Output(UInt(4.W))
  val g = Output(UInt(4.W))
  val b = Output(UInt(4.W))
  val hsync = Output(Bool())
  val vsync = Output(Bool())
}

class VGACore extends Module {
  val io = IO(new Bundle {
    val row = Output(UInt(32.W))
    val col = Output(UInt(32.W))
    val ready = Output(Bool())
    // Indicate that in next cycle
    // ready will assert
    val pre_ready = Output(Bool())
    val color = Input(UInt(12.W))
    val sig = new VGASig()
  })

  val cfg_list = VGAConfig.config(VGAConfig.mode_selected)
  val hd = cfg_list(0).U(32.W) // Must have this, or compr will be buggy
  val hf = cfg_list(1).U(32.W) // for h_tick >= hs + hb it'll do 
  val hs = cfg_list(2).U(32.W)
  val hb = cfg_list(3).U(32.W)
  val vd = cfg_list(4).U(32.W)
  val vf = cfg_list(5).U(32.W)
  val vs = cfg_list(6).U(32.W)
  val vb = cfg_list(7).U(32.W)

  val h_tick = RegInit(0.U(32.W))
  val v_tick = RegInit(0.U(32.W))

  val h_max = hs + hb + hd + hf
  val v_max = vs + vb + vd + vf

  // Layered counter
  when (h_tick >= h_max - 1.U) {
    h_tick := 0.U
    when (v_tick >= v_max - 1.U) {
      v_tick := 0.U
    } .otherwise {
      v_tick := v_tick + 1.U
    }
  } .otherwise {
    h_tick := h_tick + 1.U
  }
  // 0 ~ hs+hb-1 = total hs+hb cycles
  io.ready := (h_tick >= hs + hb) && (h_tick < hs + hb + hd) && (v_tick >= vs + vb) && (v_tick < vs + vb + vd)
  io.pre_ready := (h_tick >= hs + hb - 1.U) && (h_tick < hs + hb + hd - 1.U) && (v_tick >= vs + vb - 1.U) && (v_tick < vs + vb + vd - 1.U)
  io.sig.r := Mux(io.ready, io.color(3,0), 0.U)
  io.sig.g := Mux(io.ready, io.color(7,4), 0.U)
  io.sig.b := Mux(io.ready, io.color(11,8), 0.U)

  io.col := h_tick - hs - hb // BUG!!
  io.row := v_tick - vs - vb

  io.sig.hsync := h_tick >= hs
  io.sig.vsync := v_tick >= vs
}
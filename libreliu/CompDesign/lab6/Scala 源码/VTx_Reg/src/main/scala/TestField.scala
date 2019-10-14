package testfield

import chisel3._
import chisel3.util._

class TestField extends Module {
    val io = IO(new Bundle {
        val signal = Input(Bool())
        val pressed = Output(Bool())
    })
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
    
    io.pressed := anti_jitter(io.signal, 10)
}

 
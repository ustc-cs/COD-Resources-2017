package multicpu

import chisel3._
import chisel3.util._

// CPU without mem
class CPU extends Module {
    val io = IO(new Bundle {
        val debug = new DebugPort
        val ctrl_dbg = Flipped(new CtrlDbg)
        val mem_port = new MemPort
        val single = Input(Bool())
    })

    val datapath = Module(new Datapath)
    io.debug <> datapath.io.debug

    val ctrl = Module(new Control)
    ctrl.io.base <> datapath.io.ctrl
    io.ctrl_dbg <> ctrl.io.ctrl_dbg
    ctrl.io.SingleStep := io.single

    io.mem_port <> datapath.io.mem

    // Data dispatcher
}
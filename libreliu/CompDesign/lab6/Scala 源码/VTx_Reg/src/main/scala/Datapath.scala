package multicpu

import chisel3._
import chisel3.util._

class DebugPort extends Bundle {
    val ir = Output(UInt(32.W))
    val mdr = Output(UInt(32.W))
    val aluout = Output(UInt(32.W))
    val pc = Output(UInt(32.W))
    val ra_c = Input(UInt(5.W))
    val rd_c = Output(UInt(32.W))

    val mr_a = Input(UInt(32.W))
    val mr_d = Output(UInt(32.W))
}

class DatapathIO extends Bundle {
    val ctrl = new CtrlSigBase
    val mem = new MemPort
    val debug = new DebugPort
}

class Datapath extends Module {
    val io = IO(new DatapathIO)
    val alu = Module(new ALU(32))
    val regFile = Module(new RegFile)
    // moving mem out of Datapath for simu & synthesizing
    // val mem = Module(new Mem)

    // IR
    val ir = RegInit(Instr.Pseudo_NOP)
    when (io.ctrl.IRWrite) {
        ir := io.mem.mem_rdata
    }
    io.ctrl.Inst := ir

    // MDR
    val mdr = RegNext(io.mem.mem_rdata)

    // ALUOut
    val aluout = RegNext(alu.io.out)

    // PC
    val pc = RegInit(0.U(32.W))
    when ( (io.ctrl.PCWriteBEQ && alu.io.flagZero)    ||
           (io.ctrl.PCWriteBNE && (!alu.io.flagZero)) ||
            io.ctrl.PCWrite ) {
        pc := MuxLookup(io.ctrl.PCSource, 0.U(32.W), Seq(
            CtrlSigPC.ALU     -> alu.io.out,
            CtrlSigPC.ALUOut  -> aluout,
            CtrlSigPC.J       -> Cat(pc(31,28), (ir(25,0) << 2)),
            CtrlSigPC.RegOutA -> regFile.io.rd_a
        ))
    }

    // regFile
    regFile.io.ra_a := ir(25,21)
    regFile.io.ra_b := ir(20,16)
    //regFile.io.wa   := Mux(io.ctrl.RegDst, ir(15,11), ir(20,16))
    regFile.io.wa   := MuxLookup(io.ctrl.RegDst, 0.U(5.W), Seq(
        CtrlSigRegDst.Rt  ->  ir(20,16),
        CtrlSigRegDst.Rd  ->  ir(15,11),
        CtrlSigRegDst.ra  ->  31.U(5.W)
    ))
    regFile.io.wd   := MuxLookup(io.ctrl.MemtoReg, 0.U(32.W), Seq(
        CtrlSigMemtoReg.ALUOut     -> aluout,
        CtrlSigMemtoReg.MDR        -> mdr,
        CtrlSigMemtoReg.PC         -> pc,
        CtrlSigMemtoReg.ImmShift16 -> Cat(ir(15,0), 0.U(16.W))
    ))  // **use aluout(delayed alu.io)**
    //regFile.io.rd_a := 
    //regFile.io.rd_b := 
    regFile.io.RegWrite := io.ctrl.RegWrite

    def SignExt(n: UInt, m: Int) = {
        val p = Wire(SInt(m.W))
        p := n.asSInt
        p.asUInt
    }
    // ALU
    // alu.io.A := Mux(io.ctrl.ALUSrcA, regFile.io.rd_a, pc) // deprecated 
    alu.io.A := MuxLookup(io.ctrl.ALUSrcA, 0.U, Seq(
            CtrlSigALUA.PC   -> pc,
            CtrlSigALUA.RegA -> regFile.io.rd_a,
            CtrlSigALUA.RegB -> regFile.io.rd_b
    ))
    alu.io.B := MuxLookup(io.ctrl.ALUSrcB, 0.U, Seq(
        CtrlSigALUB.RegB   -> regFile.io.rd_b,
        CtrlSigALUB.Const4 -> 4.U(32.W),
        CtrlSigALUB.EImm  -> SignExt(ir(15,0), 32),  // it works?
        CtrlSigALUB.EImm2 -> SignExt((ir(15,0) << 2), 32),
        CtrlSigALUB.ZImm  -> ir(15,0),
        CtrlSigALUB.shamt -> ir(10,6)
    ))
    alu.io.opcode := io.ctrl.ALUOp
    // alu.io.out
    // alu.io.flagZero

    // Mem
    io.mem.mem_addr  := Mux(io.ctrl.IorD, aluout, pc)
    io.mem.mem_wdata := regFile.io.rd_b
    io.mem.mem_wen   := io.ctrl.MemWrite

    // Debug
    io.debug.ir      := ir
    io.debug.mdr     := mdr
    io.debug.aluout  := aluout
    io.debug.pc      := pc
    regFile.io.ra_c  := io.debug.ra_c
    io.debug.rd_c    := regFile.io.rd_c
    io.mem.mem_addr2 := io.debug.mr_a 
    io.debug.mr_d    := io.mem.mem_rdata2
}
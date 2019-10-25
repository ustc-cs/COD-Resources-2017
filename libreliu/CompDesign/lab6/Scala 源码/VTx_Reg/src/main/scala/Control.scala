/* Changelog: 
 * - 05/31/2019: To adapt for Sync BRAM, add 1 cycle delay to sw and lw 
 */

package multicpu

import chisel3._
import chisel3.util._

class CtrlDbg extends Bundle {
    val cur_state = Input(UInt(4.W))
    val isRType   = Input(Bool())
    val isIType   = Input(Bool())
    val isJType   = Input(Bool())
    val isLW      = Input(Bool())
    val isSW      = Input(Bool())
    val isBEQ     = Input(Bool())
    val isBNE     = Input(Bool())
}

//-- Datapath perspective --
class CtrlSigBase extends Bundle {
    val Inst = Output(UInt(32.W))
    val IRWrite = Input(Bool())
    // RegDst = 1: Write to rd (add, etc)
    //          0: Write to rt (addi, lw, etc)
    val RegDst = Input(UInt(2.W))
    val RegWrite = Input(Bool())
    val ALUSrcA = Input(UInt(2.W))
    val ALUSrcB = Input(UInt(3.W))
    val ALUOp   = Input(UInt(4.W))
    val PCSource = Input(UInt(2.W))
    val PCWriteBEQ = Input(Bool())
    val PCWriteBNE = Input(Bool())
    val PCWrite = Input(Bool())
    // IorD = 1: Address = aluout
    val IorD = Input(Bool())
    val MemRead = Input(Bool())
    val MemWrite = Input(Bool())
    val MemtoReg = Input(UInt(2.W))
}

//-- Datapath perspective --
class CtrlSigIO extends Bundle {
    val base = (new CtrlSigBase)

    // Single Step
    val SingleStep = Output(Bool())
    // Displayed sigs
    val ctrl_dbg = (new CtrlDbg)
}

object CtrlSigALUB {   // ALUSrcB
    val RegB    = 0.U(3.W) // So called "rt"
    val Const4  = 1.U(3.W)
    val EImm   = 2.U(3.W)  // Sign-extended immediate
    val EImm2  = 3.U(3.W)  // EAddr << 2
    val ZImm   = 4.U(3.W)  // Zero-extended immediate
    val shamt  = 5.U(3.W)  // use ir(10,6) (shamt).zeroext as ALUSrcB
}

object CtrlSigALUA {   // ALUSrcA
    val PC       = 0.U(2.W) // from pc
    val RegA     = 1.U(2.W) // ir(25,21) (rs)
    val RegB     = 2.U(2.W) // ir(20,16) rt  
}

object CtrlSigPC {     // PCSource
    // Directly comes from ALU
    val ALU  = 0.U(2.W)

    // J, 26bit addr
    // PC := Cat(PC(31,28),instr_index, 0.U(2.W))                
    val J  = 2.U(2.W)
    
    // BNE && BEQ, which needs jump on ALU_Flags,
    // and previous calculated ALU result
    //     (PC + 4) + sign_extend(offset << 2)
    //     Therefore PC comes from ALUOut
    val ALUOut = 1.U(2.W) // Previous ALU Result(1 cyc. earlier)

    // From Reg Read Port A (ir(25,21), rs)
    val RegOutA = 3.U(2.W)
}

object CtrlSigRegDst {  // Control RegFile Write Address
    val Rt = 0.U(2.W)   // ir(20,16)
    val Rd = 1.U(2.W)   // ir(15,11)
    val ra = 2.U(2.W)   // Write to $ra (31)
}

object CtrlSigMemtoReg {  // Control RegFile Write Data
    val ALUOut     = 0.U(2.W)
    val MDR        = 1.U(2.W)
    val PC         = 2.U(2.W) // PC+4, addr of the next instr
    val ImmShift16 = 3.U(2.W) // r_wd <- Cat(Imm, 0.U(16.W))
}
//------------------------

/** Creates a cascade of n Muxs to search for a key value.
  *
  * @example {{{
  * MuxLookupB(idx, default,
  *     Array(BitPat("b001") -> a, BitPat("b0??") -> b))
  * }}}
  */

object MuxLookupB {
  /** @param key a key to search for
    * @param default a default value if nothing is found
    * @param mapping a sequence to search of keys and values
    * @return the value found or the default if not
    */
  def apply[S <: UInt, T <: Data, D <: BitPat] (key: S, default: T, mapping: Seq[(BitPat, T)]): T = {
    var res = default
    for ((k, v) <- mapping.reverse)
      res = Mux(k === key, v, res)
    res
  }
}

import Instr._

class Control extends Module {
    val io = IO(Flipped(new CtrlSigIO))

/*     val isIType = ((io.Inst === ADDI) || 
                   (io.Inst === ANDI) ||
                   (io.Inst === ORI ) ||
                   (io.Inst === XORI) ||
                   (io.Inst === SLTI))
Have to be separated. Why?
*/ 

    val isRType = Wire(Bool())
    val isIType = Wire(Bool())
    val isJType = Wire(Bool())
    val isLW    = Wire(Bool())
    val isSW    = Wire(Bool())
    val isBEQ   = Wire(Bool())
    val isBNE   = Wire(Bool())
    val isLUI   = Wire(Bool())
    val isSLL   = Wire(Bool())

    // isRType := Lookup(io.Inst, false.B, Seq(
    //     ADD -> true.B,
    //     AND -> true.B,
    //     SUB -> true.B,
    //     OR  -> true.B,
    //     NOR -> true.B,
    //     XOR -> true.B,
    //     SLT -> true.B
    //     ))

    
    isRType := (   (io.base.Inst === ADD) || 
                   (io.base.Inst === ADDU)||
                   (io.base.Inst === AND) ||
                   (io.base.Inst === SUB) ||
                   (io.base.Inst === OR ) ||
                   (io.base.Inst === NOR) ||
                   (io.base.Inst === XOR) ||
                   (io.base.Inst === SLT))
    isIType := ((io.base.Inst === ADDI) || 
                   (io.base.Inst === ANDI) ||
                   (io.base.Inst === ORI ) ||
                   (io.base.Inst === XORI) ||
                   (io.base.Inst === SLTI) ||
                   (io.base.Inst === ADDIU) ||
                   (io.base.Inst === SLTIU))
    isJType := ((io.base.Inst === J) || (io.base.Inst === JAL) || (io.base.Inst === JR)) 
    isLW    := (io.base.Inst === LW)
    isSW    := (io.base.Inst === SW)
    isLUI   := (io.base.Inst === LUI)
    isBEQ   := (io.base.Inst === BEQ)
    isBNE   := (io.base.Inst === BNE)
    isSLL   := (io.base.Inst === SLL)

    def make_default () {
        io.base.IRWrite      := false.B
        io.base.RegDst       := 0.U
        io.base.RegWrite     := false.B
        io.base.ALUSrcA      := CtrlSigALUA.PC
        io.base.ALUSrcB      := 0.U
        io.base.ALUOp        := 0.U
        io.base.PCSource     := false.B
        io.base.PCWriteBEQ   := false.B 
        io.base.PCWriteBNE   := false.B
        io.base.PCWrite      := false.B
        io.base.IorD         := false.B
        io.base.MemRead      := false.B
        io.base.MemWrite     := false.B
        io.base.MemtoReg     := 0.U   
    }

    val inst_fetch :: inst_decode :: lwsw_mem_addr_comp :: lw_mem_access :: lw_mem_access_wait :: sw_mem_access :: sw_mem_access_wait :: lw_writeback :: r_exec :: r_writeback :: lui_writeback ::i_exec :: i_writeback :: b_exec :: j_exec :: s_exec :: illegal :: delay_start :: rest :: Nil = Enum(19)

    val cur_state = RegInit(delay_start)
    make_default()  // **Important, else you'll suffer errors on unconnected wires
                    // act like the "default" clause in Verilog "case"
    switch (cur_state) {

        is (inst_fetch) {     // IR <= mem[pc]; PC <= PC + 4

            io.base.MemRead   := true.B
            io.base.ALUSrcA   := CtrlSigALUA.PC // ALU_A <= PC
            io.base.IorD      := false.B // mem_addr <= PC
            io.base.IRWrite   := true.B
            io.base.ALUSrcB   := CtrlSigALUB.Const4 // ALU_B <= 4
            io.base.ALUOp     := ALUSel.ALU_ADD
            io.base.PCWrite   := Mux(io.SingleStep, false.B, true.B)   // Enable PC Unconditional Writing
            io.base.PCSource  := CtrlSigPC.ALU

            //cur_state := inst_decode
            cur_state    := Mux(io.SingleStep, rest, inst_decode)
        }
        is (inst_decode) {    // ALUOut <= PC + Sign-Ext(Imm << 2)

            io.base.ALUSrcA   := CtrlSigALUA.PC  // ALU_A <= regFile Port A
            io.base.ALUSrcB   := CtrlSigALUB.EImm2
            io.base.ALUOp     := ALUSel.ALU_ADD

            cur_state := MuxCase(illegal, Seq(
                isRType -> r_exec,
                isIType -> i_exec,
                isJType -> j_exec,
                isLW    -> lwsw_mem_addr_comp,
                isSW    -> lwsw_mem_addr_comp,
                isBEQ   -> b_exec,
                isBNE   -> b_exec,
                isSLL   -> s_exec,
                isLUI   -> lui_writeback
            ))
        }
        is (lwsw_mem_addr_comp) {

            io.base.ALUSrcA   := CtrlSigALUA.RegA  // ALU_A <= regFile Port A (rs)
            io.base.ALUSrcB   := CtrlSigALUB.EImm
            io.base.ALUOp     := ALUSel.ALU_ADD
            
            cur_state := MuxCase(illegal, Array(
                isLW    -> lw_mem_access_wait,
                isSW    -> sw_mem_access_wait
            ))
        }
        is (lw_mem_access_wait) { // Calc multiple times, therefore ALUOut can 1 cyc longer
            io.base.ALUSrcA   := CtrlSigALUA.RegA  // ALU_A <= regFile Port A (rs)
            io.base.ALUSrcB   := CtrlSigALUB.EImm
            io.base.ALUOp     := ALUSel.ALU_ADD

            io.base.MemRead   := true.B
            io.base.IorD      := true.B  // (gather address from aluout)

            cur_state := lw_mem_access
        }
        is (sw_mem_access_wait) { // Calc multiple times, therefore ALUOut can 1 cyc longer
            
            io.base.ALUSrcA   := CtrlSigALUA.RegA  // ALU_A <= regFile Port A (rs)
            io.base.ALUSrcB   := CtrlSigALUB.EImm
            io.base.ALUOp     := ALUSel.ALU_ADD

            io.base.MemRead   := true.B
            io.base.IorD      := true.B  // (gather address from aluout)

            cur_state := sw_mem_access
        }
        is (lw_mem_access) {

            io.base.MemRead   := true.B
            io.base.IorD      := true.B  // (gather address from aluout)

            cur_state := lw_writeback
        }
        is (sw_mem_access) {

            io.base.MemWrite  := true.B
            io.base.IorD      := true.B

            cur_state := inst_fetch
        }
        is (lw_writeback) {

            io.base.RegDst    := CtrlSigRegDst.Rt // regFile Write addr <= ir(20,16) (rt)
            io.base.RegWrite  := true.B
            io.base.MemtoReg  := CtrlSigMemtoReg.MDR  // use mdr(delayed mem_rdata)

            cur_state := inst_fetch
        }
        is (r_exec) {

            io.base.ALUSrcA   := CtrlSigALUA.RegA
            io.base.ALUSrcB   := CtrlSigALUB.RegB
            io.base.ALUOp     := MuxLookupB(io.base.Inst, ALUSel.ALU_XXX, Seq(
                ADD     -> ALUSel.ALU_ADD,
                ADDU    -> ALUSel.ALU_ADD,
                AND     -> ALUSel.ALU_AND,
                SUB     -> ALUSel.ALU_SUB,
                OR      -> ALUSel.ALU_OR ,
                NOR     -> ALUSel.ALU_NOR,
                XOR     -> ALUSel.ALU_XOR,
                SLT     -> ALUSel.ALU_SLT
            ))

            cur_state := r_writeback
        }
        is (r_writeback) {

            io.base.RegDst    := CtrlSigRegDst.Rd
            io.base.RegWrite  := true.B
            io.base.MemtoReg  := CtrlSigMemtoReg.ALUOut // use alu_out

            cur_state := inst_fetch
        }
        is (lui_writeback) {
            io.base.RegDst    := CtrlSigRegDst.Rt
            io.base.RegWrite  := true.B
            io.base.MemtoReg  := CtrlSigMemtoReg.ImmShift16 // use alu_out

            cur_state := inst_fetch
        }
        is (s_exec) {
            io.base.ALUSrcA   := CtrlSigALUA.RegB
            io.base.ALUSrcB   := CtrlSigALUB.shamt
            io.base.ALUOp     := MuxLookupB(io.base.Inst, ALUSel.ALU_XXX, Seq(
                SLL     -> ALUSel.ALU_SLL
            ))

            cur_state := r_writeback // write back are the common one
        }

        is (i_exec) {

            io.base.ALUSrcA   := CtrlSigALUA.RegA  // ALU_A <= regFile Port A (rs)
            io.base.ALUSrcB   := MuxLookupB(io.base.Inst, CtrlSigALUB.EImm, Seq(
                ADDI     -> CtrlSigALUB.EImm,
                ADDIU    -> CtrlSigALUB.EImm,
                ANDI     -> CtrlSigALUB.ZImm,
                ORI      -> CtrlSigALUB.ZImm,
                XORI     -> CtrlSigALUB.ZImm,
                SLTI     -> CtrlSigALUB.EImm,
                SLTIU    -> CtrlSigALUB.EImm
            ))
            io.base.ALUOp     := MuxLookupB(io.base.Inst, ALUSel.ALU_XXX, Seq(
                ADDI     -> ALUSel.ALU_ADD,
                ADDIU    -> ALUSel.ALU_ADD,
                ANDI     -> ALUSel.ALU_AND,
                ORI      -> ALUSel.ALU_OR,
                XORI     -> ALUSel.ALU_XOR,
                SLTI     -> ALUSel.ALU_SLT,
                SLTIU    -> ALUSel.ALU_SLTU
            ))

            cur_state := i_writeback
        }
        is (i_writeback) {

            io.base.RegDst   := CtrlSigRegDst.Rt // regFile Write addr <= ir(20,16) (rt)
            io.base.RegWrite  := true.B
            io.base.MemtoReg  := CtrlSigMemtoReg.ALUOut // use alu_out

            cur_state := inst_fetch
        }
        is (b_exec) {

            io.base.ALUSrcA    := CtrlSigALUA.RegA // (rs)
            io.base.ALUSrcB    := CtrlSigALUB.RegB
            io.base.ALUOp      := ALUSel.ALU_SUB
            io.base.PCWriteBEQ := isBEQ
            io.base.PCWriteBNE := isBNE
            io.base.PCSource   := CtrlSigPC.ALUOut

            cur_state := inst_fetch
        }
        is (j_exec) {
            // Need to see J/JAL
            // For JAL, extra writing to $ra(31) (write pc) is required
            io.base.PCWrite   := true.B
            //io.base.PCSource  := CtrlSigPC.J
            io.base.PCSource  := MuxLookupB(io.base.Inst, CtrlSigPC.J, Seq(
                JAL      -> CtrlSigPC.J,
                J        -> CtrlSigPC.J,
                JR       -> CtrlSigPC.RegOutA // ir(25,21)
            ))
            
            io.base.RegWrite  := MuxLookupB(io.base.Inst, false.B, Seq(
                JAL      -> true.B,
                J        -> false.B,
                JR       -> false.B
            ))
            io.base.RegDst    := MuxLookupB(io.base.Inst, 0.U, Seq(
                JAL      -> CtrlSigRegDst.ra, // Write to $ra
                J        -> 0.U,              // Default
                JR       -> 0.U               // Default
            ))

            io.base.MemtoReg  := MuxLookupB(io.base.Inst, 0.U, Seq(
                JAL      -> CtrlSigMemtoReg.PC, // Write PC(+4)
                J        -> 0.U,                // Default
                JR       -> 0.U                 // Default
            ))

            cur_state := inst_fetch
        }

        is (illegal) {

            cur_state := illegal
        }

        is (delay_start) {
            cur_state := inst_fetch
        }
        
        // halting the processor, return to inst_decode if single=0
        // to make this happen, keep pc + 4 available at any time
        // without actually changing pc
        is (rest) {
            io.base.MemRead   := true.B
            io.base.ALUSrcA   := CtrlSigALUA.PC // ALU_A <= PC
            io.base.IorD      := false.B // mem_addr <= PC
            io.base.IRWrite   := true.B
            io.base.ALUSrcB   := CtrlSigALUB.Const4 // ALU_B <= 4
            io.base.ALUOp     := ALUSel.ALU_ADD
            // Enable PC Unconditional Writing if we're leaving SingleStep mode
            io.base.PCWrite   := Mux(io.SingleStep, false.B, true.B) 
            io.base.PCSource  := CtrlSigPC.ALU

            cur_state := Mux(io.SingleStep, rest, inst_decode)
        }


    }

    //Debug
    io.ctrl_dbg.cur_state := cur_state
    io.ctrl_dbg.isRType   := isRType
    io.ctrl_dbg.isIType   := isIType
    io.ctrl_dbg.isJType   := isJType
    io.ctrl_dbg.isLW      := isLW   
    io.ctrl_dbg.isSW      := isSW   
    io.ctrl_dbg.isBEQ     := isBEQ  
    io.ctrl_dbg.isBNE     := isBNE  

}
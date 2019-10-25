;buildInfoPackage: chisel3, version: 3.1.7, scalaVersion: 2.11.12, sbtVersion: 1.1.1, builtAtString: 2019-03-20 22:15:13.399, builtAtMillis: 1553120113399
circuit ALU : 
  module ALU : 
    input clock : Clock
    input reset : UInt<1>
    output io : {flip A : UInt<32>, flip B : UInt<32>, flip opcode : UInt<4>, out : UInt<32>, flagZero : UInt<1>}
    
    node _T_28 = add(io.A, io.B) @[ALU.scala 33:32]
    node _T_29 = tail(_T_28, 1) @[ALU.scala 33:32]
    node _T_30 = sub(io.A, io.B) @[ALU.scala 34:32]
    node _T_31 = asUInt(_T_30) @[ALU.scala 34:32]
    node _T_32 = tail(_T_31, 1) @[ALU.scala 34:32]
    node _T_33 = asSInt(io.A) @[ALU.scala 38:32]
    node _T_34 = asSInt(io.B) @[ALU.scala 38:46]
    node _T_35 = lt(_T_33, _T_34) @[ALU.scala 38:39]
    node _T_36 = lt(io.A, io.B) @[ALU.scala 39:32]
    node _T_37 = and(io.A, io.B) @[ALU.scala 40:32]
    node _T_38 = or(io.A, io.B) @[ALU.scala 41:32]
    node _T_39 = xor(io.A, io.B) @[ALU.scala 42:32]
    node _T_40 = eq(UInt<4>("h0a"), io.opcode) @[Mux.scala 46:19]
    node _T_41 = mux(_T_40, io.A, io.B) @[Mux.scala 46:16]
    node _T_42 = eq(UInt<4>("h04"), io.opcode) @[Mux.scala 46:19]
    node _T_43 = mux(_T_42, _T_39, _T_41) @[Mux.scala 46:16]
    node _T_44 = eq(UInt<4>("h03"), io.opcode) @[Mux.scala 46:19]
    node _T_45 = mux(_T_44, _T_38, _T_43) @[Mux.scala 46:16]
    node _T_46 = eq(UInt<4>("h02"), io.opcode) @[Mux.scala 46:19]
    node _T_47 = mux(_T_46, _T_37, _T_45) @[Mux.scala 46:16]
    node _T_48 = eq(UInt<4>("h07"), io.opcode) @[Mux.scala 46:19]
    node _T_49 = mux(_T_48, _T_36, _T_47) @[Mux.scala 46:16]
    node _T_50 = eq(UInt<4>("h05"), io.opcode) @[Mux.scala 46:19]
    node _T_51 = mux(_T_50, _T_35, _T_49) @[Mux.scala 46:16]
    node _T_52 = eq(UInt<4>("h01"), io.opcode) @[Mux.scala 46:19]
    node _T_53 = mux(_T_52, _T_32, _T_51) @[Mux.scala 46:16]
    node _T_54 = eq(UInt<4>("h00"), io.opcode) @[Mux.scala 46:19]
    node _T_55 = mux(_T_54, _T_29, _T_53) @[Mux.scala 46:16]
    io.out <= _T_55 @[ALU.scala 32:12]
    node _T_57 = eq(io.out, UInt<1>("h00")) @[ALU.scala 45:28]
    io.flagZero <= _T_57 @[ALU.scala 45:17]
    

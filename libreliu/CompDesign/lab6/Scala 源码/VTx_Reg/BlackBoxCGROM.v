module CGROMBlackbox (
    input [7:0] data,
    input [3:0] row,
    input [7:0] addr,
    input clock
  );

  wire [7:0] data_out;
  cgrom_dist your_instance_name (
    .a((addr - 8'd32) * 8'd16 + row),      // input wire [10 : 0] a
    .spo(data_out)  // output wire [7 : 0] spo
  );
  assign data = ((addr < 8'd32) || (addr > 8'd127)) ? 8'd0 : data_out;

endmodule
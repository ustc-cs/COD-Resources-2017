module CGROMBlackbox (
    input [7:0] data,
    input [3:0] row,
    input [7:0] addr,
    input clock
  );

  wire [7:0] data_out;
  cgrom_blk your_instance_name (
    .clka(clock),                             // input wire clka
    .addra( (addr - 8'd32) * 8'd16 + row ),  // input wire [10 : 0] addra
    .douta(data_out)                             // output wire [7 : 0] douta
  );

  assign data = ((addr < 8'd32) || (addr > 8'd127)) ? 8'd0 : data_out;

endmodule
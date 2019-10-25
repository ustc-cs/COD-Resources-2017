`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2019/05/30 15:14:06
// Design Name: 
// Module Name: top_tb
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////


module top_tb();

reg clk;

   parameter PERIOD = 5;
   always begin
      clk = 1'b0;
      #(PERIOD/2) clk = 1'b1;
      #(PERIOD/2);
   end
				
reg rst;
initial begin
    rst = 1'b1;
    #20;
    rst = 1'b0;
end

//    always begin
        
    
//    end

top ddu (.clk_in1(clk), .reset(rst), .io_cont(1), .io_step(0), .io_mem(1), .io_inc(0), .io_dec(0));
endmodule

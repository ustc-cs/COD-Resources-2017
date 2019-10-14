`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2019/05/12 23:14:09
// Design Name: 
// Module Name: top
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


module top(
input         clk_in1, // @[:@2341.4]
input         reset, // @[:@2342.4]
input         io_cont, // @[:@2343.4]
input         io_step, // @[:@2343.4]
input         io_mem, // @[:@2343.4]
input         io_inc, // @[:@2343.4]
input         io_dec, // @[:@2343.4]
output [6:0]  io_disp_seg, // @[:@2343.4]
output        io_disp_dp, // @[:@2343.4]
output [7:0]  io_disp_an, // @[:@2343.4]
output [15:0] io_led, // @[:@2343.4]
input         i_Rx_Serial,
output [3:0]  io_vga_sig_r, // @[:@4279.4]
output [3:0]  io_vga_sig_g, // @[:@4279.4]
output [3:0]  io_vga_sig_b, // @[:@4279.4]
output        io_vga_sig_hsync, // @[:@4279.4]
output        io_vga_sig_vsync // @[:@4279.4]
    );
    
   wire clock;
   
   wire io_vram_port_wen;
   wire [11:0] io_vram_port_waddr;
   wire [15:0] io_vram_port_wdata;
   wire [11:0] io_vram_port_raddr;
   wire [15:0] io_vram_port_rdata;
   wire [31:0] io_uart_port_mem_rdata;
   wire [31:0] io_uart_port_mem_wdata;
   wire io_uart_port_mem_wen;
   
   clk_wiz_0 my_clk (
      .clk_out1(clock),
      .clk_in1(clk_in1)
   );
      
    wire i_Rx_Next;
    wire o_Rx_DV;
    wire [7:0] o_Rx_Byte;
    uart_rx uart_rx_inst (
     .i_Clock(clock),            // Connect to memory clock for better performance
     .i_Rx_Serial(i_Rx_Serial),
     .i_Rx_Next(i_Rx_Next),          // Start to detect for the next if high
     .o_Rx_DV(o_Rx_DV),            // Assert when data ready
     .o_Rx_Byte(o_Rx_Byte)
    );

   UART uart_inst(
    .mem_wen(io_uart_port_mem_wen),
    .mem_clk(clock),
    .rst(reset),
    .mem_wdata(io_uart_port_mem_wdata),
    .mem_rdata(io_uart_port_mem_rdata),
    
    // UART Lines
    .i_Rx_Next(i_Rx_Next),
    .o_Rx_DV(o_Rx_DV),
    .o_Rx_Byte(o_Rx_Byte)
   
   );

   DDU my_ddu(
     .clock(clock), // @[:@2341.4]
     .reset(reset), // @[:@2342.4]
     .io_cont(io_cont), // @[:@2343.4]
     .io_step(io_step), // @[:@2343.4]
     .io_mem(io_mem), // @[:@2343.4]
     .io_inc(io_inc), // @[:@2343.4]
     .io_dec(io_dec), // @[:@2343.4]
     .io_disp_seg(io_disp_seg), // @[:@2343.4]
     .io_disp_dp(io_disp_dp), // @[:@2343.4]
     .io_disp_an(io_disp_an), // @[:@2343.4]
     .io_led(io_led), // @[:@2343.4]
     .io_vram_port_rdata(io_vram_port_rdata), // @[:@4279.4]
     .io_vram_port_raddr(io_vram_port_raddr), // @[:@4279.4]
     .io_vram_port_wdata(io_vram_port_wdata), // @[:@4279.4]
     .io_vram_port_waddr(io_vram_port_waddr), // @[:@4279.4]
     .io_vram_port_wen(io_vram_port_wen), // @[:@4279.4]
     .io_uart_port_mem_rdata(io_uart_port_mem_rdata), // @[:@4279.4]
     .io_uart_port_mem_wdata(io_uart_port_mem_wdata), // @[:@4279.4]
     .io_uart_port_mem_wen(io_uart_port_mem_wen), // @[:@4279.4]
     .io_vga_sig_r(io_vga_sig_r), // @[:@4279.4]
     .io_vga_sig_g(io_vga_sig_g), // @[:@4279.4]
     .io_vga_sig_b(io_vga_sig_b), // @[:@4279.4]
     .io_vga_sig_hsync(io_vga_sig_hsync), // @[:@4279.4]
     .io_vga_sig_vsync(io_vga_sig_vsync) // @[:@4279.4]

    );
      
    blk_mem_gen_0 your_instance_name (
      .clka(clock),    // input wire clka
      .ena(1'b1),      // input wire ena
      .wea(io_vram_port_wen),      // input wire [0 : 0] wea
      .addra(io_vram_port_waddr),  // input wire [11 : 0] addra
      .dina(io_vram_port_wdata),    // input wire [15 : 0] dina
      .clkb(clock),    // input wire clkb
      .addrb(io_vram_port_raddr),  // input wire [11 : 0] addrb
      .doutb(io_vram_port_rdata)  // output wire [15 : 0] doutb
    );


endmodule

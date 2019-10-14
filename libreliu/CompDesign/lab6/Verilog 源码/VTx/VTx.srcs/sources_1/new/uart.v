`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2019/05/25 15:24:11
// Design Name: 
// Module Name: uart
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


module uart_rx 
  #(parameter CLKS_PER_BIT =5900)
  (
   input        i_Clock,            // Connect to memory clock for better performance
   input        i_Rx_Serial,
   input        i_Rx_Next,          // Start to detect for the next if high
   output       o_Rx_DV,            // Assert when data ready
   output [7:0] o_Rx_Byte
   );
    
  parameter s_IDLE           = 3'b000;
  parameter s_RX_START_BIT   = 3'b001;
  parameter s_RX_DATA_BITS   = 3'b010;
  parameter s_RX_STOP_BIT    = 3'b011;
  parameter s_WAIT_FOR_NEXT  = 3'b100;
  parameter s_CLEANUP        = 3'b101;
   
  reg           r_Rx_Data_R = 1'b1;
  reg           r_Rx_Data   = 1'b1;
   
  reg [31:0]    r_Clock_Count = 0;
  reg [2:0]     r_Bit_Index   = 0; //8 bits total
  reg [7:0]     r_Rx_Byte     = 0; //bits read
  reg           r_Rx_DV       = 0;
  reg [2:0]     r_SM_Main     = 0;
   
  // Purpose: Double-register the incoming data.
  // This allows it to be used in the UART RX Clock Domain.
  // (It removes problems caused by metastability)
  always @(posedge i_Clock)
    begin
      r_Rx_Data_R <= i_Rx_Serial;
      r_Rx_Data   <= r_Rx_Data_R;
    end
   
   
  // Purpose: Control RX state machine
  always @(posedge i_Clock)
    begin
       
      case (r_SM_Main)
        s_IDLE :
          begin
            r_Rx_DV       <= 1'b0;
            r_Clock_Count <= 0;
            r_Bit_Index   <= 0;
             
            if (r_Rx_Data == 1'b0)          // Start bit detected and 
              r_SM_Main <= s_RX_START_BIT;
            else
              r_SM_Main <= s_IDLE;
          end
         
        // Check middle of start bit to make sure it's still low
        s_RX_START_BIT :
          begin
            if (r_Clock_Count == (CLKS_PER_BIT-1)/2)
              begin
                if (r_Rx_Data == 1'b0)
                  begin
                    r_Clock_Count <= 0;  // reset counter, found the middle
                    r_SM_Main     <= s_RX_DATA_BITS;
                  end
                else
                  r_SM_Main <= s_IDLE;
              end
            else
              begin
                r_Clock_Count <= r_Clock_Count + 1;
                r_SM_Main     <= s_RX_START_BIT;
              end
          end // case: s_RX_START_BIT
         
         
        // Wait CLKS_PER_BIT-1 clock cycles to sample serial data
        s_RX_DATA_BITS :
          begin
            if (r_Clock_Count < CLKS_PER_BIT-1)
              begin
                r_Clock_Count <= r_Clock_Count + 1;
                r_SM_Main     <= s_RX_DATA_BITS;
              end
            else
              begin
                r_Clock_Count          <= 0;
                r_Rx_Byte[r_Bit_Index] <= r_Rx_Data;
                 
                // Check if we have received all bits
                if (r_Bit_Index < 7)
                  begin
                    r_Bit_Index <= r_Bit_Index + 1;
                    r_SM_Main   <= s_RX_DATA_BITS;
                  end
                else
                  begin
                    r_Bit_Index <= 0;
                    r_SM_Main   <= s_RX_STOP_BIT;
                  end
              end
          end // case: s_RX_DATA_BITS
     
     
        // Receive Stop bit.  Stop bit = 1
        s_RX_STOP_BIT :
          begin
            // Wait CLKS_PER_BIT-1 clock cycles for Stop bit to finish
            // Can't call our CPU earlier, since our CPU runs damn too fast
            
            if (r_Clock_Count < CLKS_PER_BIT-1)
              begin
                r_Clock_Count <= r_Clock_Count + 1;
                r_SM_Main     <= s_RX_STOP_BIT;
              end
            else
              begin
                r_Clock_Count <= 0;
                r_SM_Main     <= s_WAIT_FOR_NEXT;
              end
          end // case: s_RX_STOP_BIT
          
        s_WAIT_FOR_NEXT:
          begin
            r_Rx_DV           <= 1'b1;
            if (i_Rx_Next)
              begin
                // Okay, we can go to CLEANUP
                r_SM_Main     <= s_CLEANUP;
              end
            else
              begin
                r_SM_Main     <= s_WAIT_FOR_NEXT;
              end
          end
        
         
        // Stay here 1 clock
        s_CLEANUP :
          begin
            r_SM_Main <= s_IDLE;
            r_Rx_DV   <= 1'b0;
          end
         
         
        default :
          r_SM_Main <= s_IDLE;
         
      endcase
    end   
   
  assign o_Rx_DV   = r_Rx_DV;
  assign o_Rx_Byte = r_Rx_Byte;
   
endmodule // uart_rx

// +----------------------------------------------+
// |  31   |  30   | 29 ...  8   |    7  ... 0    |
// | READY | VALID | don't care  |  Serial data   |
// +----------------------------------------------+

// Usage: When Data is valid, bit 30 == 1
//        Write bit 31 to 1 (1 cycle is okay) to read next
//        (then bit 31 will automatically set to 0)

module UART (
    input mem_wen,
    input mem_clk,
    input rst,
    input [31:0] mem_wdata,
    output [31:0] mem_rdata,
    
    // UART Lines
    output i_Rx_Next,
    input o_Rx_DV,
    input [7:0] o_Rx_Byte

);
    reg ready_bit;
    reg ready_bit_prev;
    assign mem_rdata[31]    =   ready_bit; // Ready bit
    assign mem_rdata[30]    =   o_Rx_DV;   // Valid bit
    assign mem_rdata[7:0]   =   o_Rx_Byte;
    assign mem_rdata[29:8]  =   23'b0;

    assign i_Rx_Next = ready_bit;
    
    always @ (posedge mem_clk) 
      begin
        if (rst) begin
            ready_bit <= 1'b0;
        end else begin
            if (mem_wen) begin
                ready_bit <= mem_wdata[31];
            end else if (~ready_bit_prev && ready_bit) begin
                ready_bit <= 1'b0;
            end
        end
      end
    
    always @ (posedge mem_clk)
      begin
        if (rst) begin
          ready_bit_prev <= 1'b0;
        end else begin
          ready_bit_prev <= ready_bit;
        end
      end

endmodule
    
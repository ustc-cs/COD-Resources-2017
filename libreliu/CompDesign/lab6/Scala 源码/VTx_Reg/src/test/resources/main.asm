.text

_start:
# $s0 是光标行数 [0,23)  $s1 是光标列数[0,79)
add  $s0, $zero, $zero
add  $s1, $zero, $zero

addi   $s2, $zero, 24   # 24
addi   $s3, $zero, 80   # 80

addi   $s4, $zero, 10   # \n 的 ascii 码

input_loop:
jal  get_char   # 获得输入  储存在 $v0
# 读到 \n 或者 行满 都需要换行
addi $s1, $s1, 1

input_if:  # 读到 \n
bne  $v0, $s4, input_else_if
jal  new_line
j    input_loop

input_else_if:   # 列数已满
addi $t0, $s1, 1
bne  $t0, $s3, input_rest
jal  new_line

input_rest:
# 保存到 vram[$s0][$s1]   即 vram[$s0*col_num+$s1]
add  $a0, $s0, $zero
add  $a1, $s3, $zero
add  $s5, $v0, $zero

jal  multply

lw   $t0, vram_start
add  $t0, $t0, $v0
add  $t0, $t0, $s1
sw   $s5, 0($t0)

j    input_loop



# 乘法函数
multply:
add  $t0, $zero, $zero
add  $v0, $zero, $zero

mult_loop:
beq	 $t0, $a1, mult_end
add  $v0, $v0, $a0
addi $t0, $t0, 1

mult_end:
jr   $ra	



# 换行函数
new_line:
addi $s0, $s0, 1  # 行数 ++

new_line_if:
bne  $s0, $s2, new_line_else
    # 已满则需要移动已有的 vram
    jal  mv_vram
    addi $s0, $s2, -1   # 行数 = raw_num-1
    add  $s1, $zero, $zero  # 列数=0
    j    new_line_end

new_line_else:
# 行数未满直接换行
add  $s1, $zero, $zero

new_line_end:
jr   $ra



# 屏幕填满时移动行函数
mv_vram:
    # 视为连续内存空间进行移动
    # [col_num,row_num*col_num) 移动到  [0,row_num*col_num-col_num）

    lw   $t4, vram_start

    add  $t0, $zero, $t4   # 指向 vram 开始

    lw   $t1, col_num      # 指向起始移动位置的指针
    add  $t1, $t1, $t4

    lw   $t2, total_num    # 指向结尾(不包括)的指针
    add  $t2, $t2, $t4

mv_vram_loop:
beq  $t1, $t2, mv_vram_end
lw   $t3, 0($t1)
sw   $t3, 0($t0)
addi $t0, $t0, 1
addi $t1, $t1, 1
j    mv_vram_loop

mv_vram_end:
jr   $ra




# 读字符函数
get_char:
lw	 $t0, uart_addr

uart_valid_check:
lw	 $v0, 0($t0)
# 检查valid 位
lw   $t1, valid_mask
and  $t1, $t1, $v0
beq  $t1, $zero, uart_valid_check

# 设置 ready 位为 1
lw   $t1, ready_word
sw   $t1, 0($t0)

# 截取低 8 位
lw   $t1, char_mask
and  $v0, $v0, $t1

# 返回
jr   $ra


.data

vram_start: .word   0x0000DFFC   # VRAM 起始地址
row_num:    .word   24
col_num:    .word   80
total_num:  .word   1920

ascii_start: .word   32
ascii_end:   .word   127
enter_ascii: .word   10

uart_addr:  .word	0x0000DFF8   # uart 的地址
valid_mask: .word   0x40000000   # valid 在 30 位
char_mask:  .word   0x000000FF   # 截取 ascii 有效位
ready_word: .word   0x80000000   # 离开后设置 ready 位为 1
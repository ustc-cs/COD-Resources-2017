.text

_start:
# Set stack frame
addi $sp, $zero, 500

# $s0 是光标行数 [0,23)  $s1 是光标列数[0,79)
add  $s0, $zero, $zero
add  $s1, $zero, $zero

addi   $s2, $zero, 24   # 24
addi   $s3, $zero, 80   # 80
addi   $s4, $zero, 10   # \n 的 ascii 码

main_loop:
	addiu	$sp,$sp,-40
	sw	$31,36($sp)
	sw	$fp,32($sp)
	move	$fp,$sp
	li	$4,72			# 0x48
	jal	putchar
	nop

	li	$4,101			# 0x65
	.option	pic0
	jal	putchar
	nop

	li	$4,108			# 0x6c
	jal	putchar
	nop

	li	$4,108			# 0x6c
	jal	putchar
	nop

	li	$4,111			# 0x6f
	jal	putchar
	nop

$L9:
	jal	getchar
	nop

	sw	$2,24($fp)
	lw	$4,24($fp)
	jal	putchar
	nop

	b	$L9
	nop

getchar:
	addiu	$sp,$sp,-32
	sw	$fp,28($sp)
	move	$fp,$sp
	li	$2,65535			# 0xffff
	sw	$2,8($fp)
	li	$2,1073741824			# 0x40000000
	sw	$2,12($fp)
	li	$2,255			# 0xff
	sw	$2,16($fp)
	b	$L5
$L6:
	nop
$L5:
	lw	$2,8($fp)
	#nop
	lw	$2,0($2)
	lw	$3,12($fp)
	#nop
	sltu	$3,$3,1
	andi	$3,$3,0x00ff
	and	$2,$2,$3
	bne	$2,$0,$L6
	lw	$2,8($fp)
	#nop
	lw	$3,0($2)
	lw	$2,16($fp)
	#nop
	and	$2,$3,$2
	move	$sp,$fp
	lw	$fp,28($sp)
	addiu	$sp,$sp,32
	jr	$31


# Put char in $s7 (with attr) to current cursor position, and
# move cursor 1 position ahead
putchar:
	addiu	$sp,$sp,-8
	sw	$fp,4($sp)
    # copy sp to fp
	move	$fp,$sp
	sw	$4,8($fp)
	lw	$3, 0
	lw	$2,%lo(cursor_h)($2)
	nop
	move	$4,$2
	move	$2,$4
	sll	$2,$2,2
	addu	$2,$2,$4
	sll	$2,$2,4
	move	$4,$2
	lui	$2, 0
	lw	$2,%lo(cursor_v)($2)
	nop
	addu	$2,$4,$2
	sll	$2,$2,2
	addu	$2,$3,$2
	lw	$3,8($fp)
	nop
	sw	$3,0($2)
	lui	$2, 0
	lw	$2,%lo(cursor_h)($2)
	nop
	addiu	$3,$2,1
	lui	$2, 0
	sw	$3,%lo(cursor_h)($2)
	lui	$2, 0
	lw	$3,%lo(cursor_h)($2)
	li	$2,80			# 0x50
	bne	$3,$2,$L3
	nop

	lui	$2, 0
	sw	$0,%lo(cursor_h)($2)
	lui	$2, 0
	lw	$2,%lo(cursor_v)($2)
	nop
	addiu	$3,$2,1
	lui	$2, 0
	sw	$3,%lo(cursor_v)($2)
	lui	$2, 0
	lw	$3,%lo(cursor_v)($2)
	li	$2,25			# 0x19
	bne	$3,$2,$L3
	nop

	lui	$2, 0
	sw	$0,%lo(cursor_v)($2)
$L3:
	nop
	move	$sp,$fp
	lw	$fp,4($sp)
	addiu	$sp,$sp,8
	jr	$31
	nop


vram_offset: 	.word	12288
cursor_h:       .word   0
cursor_v:       .word   0
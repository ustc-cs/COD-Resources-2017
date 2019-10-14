	.file	1 "util.c"
	.section .mdebug.abi32
	.previous
	.nan	legacy
	.module	fp=32
	.module	nooddspreg
	.abicalls
	.text
	.globl	cursor_h
	.section	.bss,"aw",@nobits
	.align	2
	.type	cursor_h, @object
	.size	cursor_h, 4
cursor_h:
	.space	4
	.globl	cursor_v
	.align	2
	.type	cursor_v, @object
	.size	cursor_v, 4
cursor_v:
	.space	4
	.text
	.align	2
	.globl	main_loop
	.set	nomips16
	.set	nomicromips
	.ent	main_loop
	.type	main_loop, @function
main_loop:
	.frame	$fp,40,$31		# vars= 8, regs= 2/0, args= 16, gp= 8
	.mask	0xc0000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.set	nomacro
	addiu	$sp,$sp,-40
	sw	$31,36($sp)
	sw	$fp,32($sp)
	move	$fp,$sp
	lui	$2,%hi(cursor_h)
	sw	$0,%lo(cursor_h)($2)
	lui	$2,%hi(cursor_v)
	sw	$0,%lo(cursor_v)($2)
	li	$4,3902			# 0xf3e
	.option	pic0
	jal	putchar
	nop

	.option	pic2
$L11:
	.option	pic0
	jal	getchar
	nop

	.option	pic2
	sw	$2,28($fp)
	lw	$3,28($fp)
	li	$2,10			# 0xa
	beq	$3,$2,$L2
	nop

	lw	$3,28($fp)
	li	$2,13			# 0xd
	bne	$3,$2,$L3
	nop

$L2:
	lui	$2,%hi(cursor_h)
	sw	$0,%lo(cursor_h)($2)
	lui	$2,%hi(cursor_v)
	lw	$2,%lo(cursor_v)($2)
	nop
	addiu	$3,$2,1
	lui	$2,%hi(cursor_v)
	sw	$3,%lo(cursor_v)($2)
	.option	pic0
	b	$L4
	nop

	.option	pic2
$L3:
	lw	$3,28($fp)
	li	$2,8			# 0x8
	bne	$3,$2,$L5
	nop

	lui	$2,%hi(cursor_h)
	lw	$2,%lo(cursor_h)($2)
	nop
	bne	$2,$0,$L6
	nop

	lui	$2,%hi(cursor_h)
	li	$3,24			# 0x18
	sw	$3,%lo(cursor_h)($2)
	lui	$2,%hi(cursor_v)
	lw	$2,%lo(cursor_v)($2)
	nop
	addiu	$3,$2,-1
	lui	$2,%hi(cursor_v)
	sw	$3,%lo(cursor_v)($2)
	li	$4,3872			# 0xf20
	.option	pic0
	jal	putchar
	nop

	.option	pic2
	lui	$2,%hi(cursor_h)
	li	$3,24			# 0x18
	sw	$3,%lo(cursor_h)($2)
	lui	$2,%hi(cursor_v)
	lw	$2,%lo(cursor_v)($2)
	nop
	addiu	$3,$2,-1
	lui	$2,%hi(cursor_v)
	sw	$3,%lo(cursor_v)($2)
	.option	pic0
	b	$L11
	nop

	.option	pic2
$L6:
	lui	$2,%hi(cursor_h)
	lw	$2,%lo(cursor_h)($2)
	nop
	addiu	$3,$2,-1
	lui	$2,%hi(cursor_h)
	sw	$3,%lo(cursor_h)($2)
	li	$4,3872			# 0xf20
	.option	pic0
	jal	putchar
	nop

	.option	pic2
	lui	$2,%hi(cursor_h)
	lw	$2,%lo(cursor_h)($2)
	nop
	addiu	$3,$2,-1
	lui	$2,%hi(cursor_h)
	sw	$3,%lo(cursor_h)($2)
	.option	pic0
	b	$L11
	nop

	.option	pic2
$L5:
	lw	$3,28($fp)
	li	$2,27			# 0x1b
	bne	$3,$2,$L8
	nop

	sw	$0,24($fp)
	.option	pic0
	b	$L9
	nop

	.option	pic2
$L10:
	li	$4,3872			# 0xf20
	.option	pic0
	jal	putchar
	nop

	.option	pic2
	lw	$2,24($fp)
	nop
	addiu	$2,$2,1
	sw	$2,24($fp)
$L9:
	lw	$2,24($fp)
	nop
	slt	$2,$2,4000
	bne	$2,$0,$L10
	nop

	lui	$2,%hi(cursor_h)
	sw	$0,%lo(cursor_h)($2)
	lui	$2,%hi(cursor_v)
	sw	$0,%lo(cursor_v)($2)
	.option	pic0
	b	$L11
	nop

	.option	pic2
$L8:
	lw	$2,28($fp)
	nop
	ori	$2,$2,0xf00
	move	$4,$2
	.option	pic0
	jal	putchar
	nop

	.option	pic2
$L4:
	.option	pic0
	b	$L11
	nop

	.option	pic2
	.set	macro
	.set	reorder
	.end	main_loop
	.size	main_loop, .-main_loop
	.align	2
	.globl	putchar
	.set	nomips16
	.set	nomicromips
	.ent	putchar
	.type	putchar, @function
putchar:
	.frame	$fp,24,$31		# vars= 8, regs= 1/0, args= 0, gp= 8
	.mask	0x40000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.set	nomacro
	addiu	$sp,$sp,-24
	sw	$fp,20($sp)
	move	$fp,$sp
	sw	$4,24($fp)
	li	$2,12288			# 0x3000
	sw	$2,8($fp)
	lui	$2,%hi(cursor_v)
	lw	$3,%lo(cursor_v)($2)
	nop
	move	$2,$3
	sll	$2,$2,2
	addu	$2,$2,$3
	sll	$2,$2,4
	move	$3,$2
	lui	$2,%hi(cursor_h)
	lw	$2,%lo(cursor_h)($2)
	nop
	addu	$2,$3,$2
	sll	$2,$2,2
	lw	$3,8($fp)
	nop
	addu	$2,$3,$2
	lw	$3,24($fp)
	nop
	sw	$3,0($2)
	lui	$2,%hi(cursor_h)
	lw	$2,%lo(cursor_h)($2)
	nop
	addiu	$3,$2,1
	lui	$2,%hi(cursor_h)
	sw	$3,%lo(cursor_h)($2)
	lui	$2,%hi(cursor_h)
	lw	$3,%lo(cursor_h)($2)
	li	$2,80			# 0x50
	bne	$3,$2,$L14
	nop

	lui	$2,%hi(cursor_h)
	sw	$0,%lo(cursor_h)($2)
	lui	$2,%hi(cursor_v)
	lw	$2,%lo(cursor_v)($2)
	nop
	addiu	$3,$2,1
	lui	$2,%hi(cursor_v)
	sw	$3,%lo(cursor_v)($2)
	lui	$2,%hi(cursor_v)
	lw	$3,%lo(cursor_v)($2)
	li	$2,25			# 0x19
	bne	$3,$2,$L14
	nop

	lui	$2,%hi(cursor_v)
	sw	$0,%lo(cursor_v)($2)
$L14:
	nop
	move	$sp,$fp
	lw	$fp,20($sp)
	addiu	$sp,$sp,24
	jr	$31
	nop

	.set	macro
	.set	reorder
	.end	putchar
	.size	putchar, .-putchar
	.align	2
	.globl	getchar
	.set	nomips16
	.set	nomicromips
	.ent	getchar
	.type	getchar, @function
getchar:
	.frame	$fp,32,$31		# vars= 16, regs= 1/0, args= 0, gp= 8
	.mask	0x40000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.set	nomacro
	addiu	$sp,$sp,-32
	sw	$fp,28($sp)
	move	$fp,$sp
	li	$2,65280			# 0xff00
	sw	$2,8($fp)
	li	$2,1073741824			# 0x40000000
	sw	$2,12($fp)
	li	$2,255			# 0xff
	sw	$2,16($fp)
	nop
$L16:
	lw	$2,8($fp)
	nop
	lw	$3,0($2)
	lw	$2,12($fp)
	nop
	and	$2,$3,$2
	beq	$2,$0,$L16
	nop

	lw	$2,8($fp)
	nop
	lw	$2,0($2)
	lw	$3,16($fp)
	nop
	and	$2,$3,$2
	sw	$2,20($fp)
	lw	$2,8($fp)
	li	$3,-2147483648			# 0xffffffff80000000
	sw	$3,0($2)
	lw	$2,20($fp)
	move	$sp,$fp
	lw	$fp,28($sp)
	addiu	$sp,$sp,32
	jr	$31
	nop

	.set	macro
	.set	reorder
	.end	getchar
	.size	getchar, .-getchar
	.ident	"GCC: (GNU) 8.3.0"


util_final.elf:     file format elf32-tradbigmips


Disassembly of section .text:

00000000 <_start>:
   0:	201d03fc 	addi	sp,zero,1020
	...

00000010 <main_loop>:
  10:	27bdffd8 	addiu	sp,sp,-40
  14:	afbf0024 	sw	ra,36(sp)
  18:	afbe0020 	sw	s8,32(sp)
  1c:	03a0f025 	move	s8,sp
  20:	3c020000 	lui	v0,0x0
  24:	ac400350 	sw	zero,848(v0)
  28:	3c020000 	lui	v0,0x0
  2c:	ac400354 	sw	zero,852(v0)
  30:	24040f3e 	li	a0,3902
  34:	0c000076 	jal	1d8 <putchar>
  38:	00000000 	nop
  3c:	0c0000af 	jal	2bc <getchar>
  40:	00000000 	nop
  44:	afc2001c 	sw	v0,28(s8)
  48:	8fc3001c 	lw	v1,28(s8)
  4c:	2402000a 	li	v0,10
  50:	10620005 	beq	v1,v0,68 <main_loop+0x58>
  54:	00000000 	nop
  58:	8fc3001c 	lw	v1,28(s8)
  5c:	2402000d 	li	v0,13
  60:	1462000b 	bne	v1,v0,90 <main_loop+0x80>
  64:	00000000 	nop
  68:	3c020000 	lui	v0,0x0
  6c:	ac400350 	sw	zero,848(v0)
  70:	3c020000 	lui	v0,0x0
  74:	8c420354 	lw	v0,852(v0)
  78:	00000000 	nop
  7c:	24430001 	addiu	v1,v0,1
  80:	3c020000 	lui	v0,0x0
  84:	ac430354 	sw	v1,852(v0)
  88:	10000051 	b	1d0 <main_loop+0x1c0>
  8c:	00000000 	nop
  90:	8fc3001c 	lw	v1,28(s8)
  94:	24020008 	li	v0,8
  98:	1462002e 	bne	v1,v0,154 <main_loop+0x144>
  9c:	00000000 	nop
  a0:	3c020000 	lui	v0,0x0
  a4:	8c420350 	lw	v0,848(v0)
  a8:	00000000 	nop
  ac:	14400018 	bnez	v0,110 <main_loop+0x100>
  b0:	00000000 	nop
  b4:	3c020000 	lui	v0,0x0
  b8:	24030018 	li	v1,24
  bc:	ac430350 	sw	v1,848(v0)
  c0:	3c020000 	lui	v0,0x0
  c4:	8c420354 	lw	v0,852(v0)
  c8:	00000000 	nop
  cc:	2443ffff 	addiu	v1,v0,-1
  d0:	3c020000 	lui	v0,0x0
  d4:	ac430354 	sw	v1,852(v0)
  d8:	24040f20 	li	a0,3872
  dc:	0c000076 	jal	1d8 <putchar>
  e0:	00000000 	nop
  e4:	3c020000 	lui	v0,0x0
  e8:	24030018 	li	v1,24
  ec:	ac430350 	sw	v1,848(v0)
  f0:	3c020000 	lui	v0,0x0
  f4:	8c420354 	lw	v0,852(v0)
  f8:	00000000 	nop
  fc:	2443ffff 	addiu	v1,v0,-1
 100:	3c020000 	lui	v0,0x0
 104:	ac430354 	sw	v1,852(v0)
 108:	1000ffcc 	b	3c <main_loop+0x2c>
 10c:	00000000 	nop
 110:	3c020000 	lui	v0,0x0
 114:	8c420350 	lw	v0,848(v0)
 118:	00000000 	nop
 11c:	2443ffff 	addiu	v1,v0,-1
 120:	3c020000 	lui	v0,0x0
 124:	ac430350 	sw	v1,848(v0)
 128:	24040f20 	li	a0,3872
 12c:	0c000076 	jal	1d8 <putchar>
 130:	00000000 	nop
 134:	3c020000 	lui	v0,0x0
 138:	8c420350 	lw	v0,848(v0)
 13c:	00000000 	nop
 140:	2443ffff 	addiu	v1,v0,-1
 144:	3c020000 	lui	v0,0x0
 148:	ac430350 	sw	v1,848(v0)
 14c:	1000ffbb 	b	3c <main_loop+0x2c>
 150:	00000000 	nop
 154:	8fc3001c 	lw	v1,28(s8)
 158:	2402001b 	li	v0,27
 15c:	14620016 	bne	v1,v0,1b8 <main_loop+0x1a8>
 160:	00000000 	nop
 164:	afc00018 	sw	zero,24(s8)
 168:	10000008 	b	18c <main_loop+0x17c>
 16c:	00000000 	nop
 170:	24040f20 	li	a0,3872
 174:	0c000076 	jal	1d8 <putchar>
 178:	00000000 	nop
 17c:	8fc20018 	lw	v0,24(s8)
 180:	00000000 	nop
 184:	24420001 	addiu	v0,v0,1
 188:	afc20018 	sw	v0,24(s8)
 18c:	8fc20018 	lw	v0,24(s8)
 190:	00000000 	nop
 194:	28420fa0 	slti	v0,v0,4000
 198:	1440fff5 	bnez	v0,170 <main_loop+0x160>
 19c:	00000000 	nop
 1a0:	3c020000 	lui	v0,0x0
 1a4:	ac400350 	sw	zero,848(v0)
 1a8:	3c020000 	lui	v0,0x0
 1ac:	ac400354 	sw	zero,852(v0)
 1b0:	1000ffa2 	b	3c <main_loop+0x2c>
 1b4:	00000000 	nop
 1b8:	8fc2001c 	lw	v0,28(s8)
 1bc:	00000000 	nop
 1c0:	34420f00 	ori	v0,v0,0xf00
 1c4:	00402025 	move	a0,v0
 1c8:	0c000076 	jal	1d8 <putchar>
 1cc:	00000000 	nop
 1d0:	1000ff9a 	b	3c <main_loop+0x2c>
 1d4:	00000000 	nop

000001d8 <putchar>:
 1d8:	27bdffe8 	addiu	sp,sp,-24
 1dc:	afbe0014 	sw	s8,20(sp)
 1e0:	03a0f025 	move	s8,sp
 1e4:	afc40018 	sw	a0,24(s8)
 1e8:	24023000 	li	v0,12288
 1ec:	afc20008 	sw	v0,8(s8)
 1f0:	3c020000 	lui	v0,0x0
 1f4:	8c430354 	lw	v1,852(v0)
 1f8:	00000000 	nop
 1fc:	00601025 	move	v0,v1
 200:	00021080 	sll	v0,v0,0x2
 204:	00431021 	addu	v0,v0,v1
 208:	00021100 	sll	v0,v0,0x4
 20c:	00401825 	move	v1,v0
 210:	3c020000 	lui	v0,0x0
 214:	8c420350 	lw	v0,848(v0)
 218:	00000000 	nop
 21c:	00621021 	addu	v0,v1,v0
 220:	00021080 	sll	v0,v0,0x2
 224:	8fc30008 	lw	v1,8(s8)
 228:	00000000 	nop
 22c:	00621021 	addu	v0,v1,v0
 230:	8fc30018 	lw	v1,24(s8)
 234:	00000000 	nop
 238:	ac430000 	sw	v1,0(v0)
 23c:	3c020000 	lui	v0,0x0
 240:	8c420350 	lw	v0,848(v0)
 244:	00000000 	nop
 248:	24430001 	addiu	v1,v0,1
 24c:	3c020000 	lui	v0,0x0
 250:	ac430350 	sw	v1,848(v0)
 254:	3c020000 	lui	v0,0x0
 258:	8c430350 	lw	v1,848(v0)
 25c:	24020050 	li	v0,80
 260:	14620010 	bne	v1,v0,2a4 <putchar+0xcc>
 264:	00000000 	nop
 268:	3c020000 	lui	v0,0x0
 26c:	ac400350 	sw	zero,848(v0)
 270:	3c020000 	lui	v0,0x0
 274:	8c420354 	lw	v0,852(v0)
 278:	00000000 	nop
 27c:	24430001 	addiu	v1,v0,1
 280:	3c020000 	lui	v0,0x0
 284:	ac430354 	sw	v1,852(v0)
 288:	3c020000 	lui	v0,0x0
 28c:	8c430354 	lw	v1,852(v0)
 290:	24020019 	li	v0,25
 294:	14620003 	bne	v1,v0,2a4 <putchar+0xcc>
 298:	00000000 	nop
 29c:	3c020000 	lui	v0,0x0
 2a0:	ac400354 	sw	zero,852(v0)
 2a4:	00000000 	nop
 2a8:	03c0e825 	move	sp,s8
 2ac:	8fbe0014 	lw	s8,20(sp)
 2b0:	27bd0018 	addiu	sp,sp,24
 2b4:	03e00008 	jr	ra
 2b8:	00000000 	nop

000002bc <getchar>:
 2bc:	27bdffe0 	addiu	sp,sp,-32
 2c0:	afbe001c 	sw	s8,28(sp)
 2c4:	03a0f025 	move	s8,sp
 2c8:	3402ff00 	li	v0,0xff00
 2cc:	afc20008 	sw	v0,8(s8)
 2d0:	3c024000 	lui	v0,0x4000
 2d4:	afc2000c 	sw	v0,12(s8)
 2d8:	240200ff 	li	v0,255
 2dc:	afc20010 	sw	v0,16(s8)
 2e0:	00000000 	nop
 2e4:	8fc20008 	lw	v0,8(s8)
 2e8:	00000000 	nop
 2ec:	8c430000 	lw	v1,0(v0)
 2f0:	8fc2000c 	lw	v0,12(s8)
 2f4:	00000000 	nop
 2f8:	00621024 	and	v0,v1,v0
 2fc:	1040fff9 	beqz	v0,2e4 <getchar+0x28>
 300:	00000000 	nop
 304:	8fc20008 	lw	v0,8(s8)
 308:	00000000 	nop
 30c:	8c420000 	lw	v0,0(v0)
 310:	8fc30010 	lw	v1,16(s8)
 314:	00000000 	nop
 318:	00621024 	and	v0,v1,v0
 31c:	afc20014 	sw	v0,20(s8)
 320:	8fc20008 	lw	v0,8(s8)
 324:	3c038000 	lui	v1,0x8000
 328:	ac430000 	sw	v1,0(v0)
 32c:	8fc20014 	lw	v0,20(s8)
 330:	03c0e825 	move	sp,s8
 334:	8fbe001c 	lw	s8,28(sp)
 338:	27bd0020 	addiu	sp,sp,32
 33c:	03e00008 	jr	ra
 340:	00000000 	nop
	...

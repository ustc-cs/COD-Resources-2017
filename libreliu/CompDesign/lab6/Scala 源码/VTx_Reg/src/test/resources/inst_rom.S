# 本文档存储器以字节编址
# 本文档存储器以字节编址
# 本文档存储器以字节编址
j _start

.data
    .word 0,8,1,6,0xfffffff8,1,3,5,0   #编译成机器码时便器器会在前面多加个0，所以后面lw指令地址会多加4

_start:    
		addi $t0,$0,3       #t0=3
        addi $t1,$0,5   	#t1=5
		addi $t2,$0,1       #t2=1

        add  $s0,$t1,$t0  #s0=t1+t0=8  测试add指令 正确继续执行
        lw   $s1,12($0)  
        bne  $s1,$s0,_fail#不正确跳到fail 

        and  $s0,$t1,$t0  #s0=t1&t0=1  测试and指令 正确继续执行
        lw   $s1,16($0)  
        bne  $s1,$s0,_fail
 
        xor  $s0,$t1,$t0  #s0=t1^t0=6  测试xor指令 正确继续执行
        lw   $s1,20($0)  
        bne  $s1,$s0,_fail

        nor  $s0,$t1,$t0  #s0=t1 nor t0=0xfffffff8
        lw   $s1,24($0)  
        bne  $s1,$s0,_fail

        slt  $s0,$t0,$t1  #s0=1
        lw   $s1,28($0)  
        bne  $s1,$s0,_fail

        andi $s0,$t0,7  #s0=3
        lw   $s1,32($0)  
        bne  $s1,$s0,_fail

        ori  $s0,$t1,4  #s0=5
        lw   $s1,36($0)  
        bne  $s1,$s0,_fail
        
        sw   $t1,40($0)
        lw   $s1,40($0)
        beq  $t1,$s1,_sucess
                 
_fail:  
		sw   $0,8($0) #失败通过看存储器地址0x08里值，若为0则测试不通过，最初地址0x08里值为0
        j    _fail

_sucess: 
	sw   $t2,8($0)    #全部测试通过，存储器地址0x08里值为1
	j   _sucess       

					  #判断测试通过的条件是最后存储器地址0x08里值为1，说明全部通过测试
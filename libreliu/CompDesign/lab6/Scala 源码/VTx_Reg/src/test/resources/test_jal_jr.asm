_start:
    jal sub_prog
    addi $s0, $zero, 3  # $s0 = 3

sub_prog:
    addi $s1, $zero, 4  # $s1 = 4
    jr $ra

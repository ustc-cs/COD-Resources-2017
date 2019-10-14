#!/bin/bash

mipsel-linux-gnu-gcc -nostdlib -S util.c -o util.c.S -ffreestanding -static -fno-delayed-branch -Wa,-O0  -EB
mipsel-linux-gnu-gcc -nostdlib -c util.c -o util.o -ffreestanding -static -fno-delayed-branch -Wa,-O0  -EB
mipsel-linux-gnu-as start.S -o start.o -EB
mipsel-linux-gnu-ld -nostdlib --script linker.ld start.o util.o -o util_final.elf -EB
mipsel-linux-gnu-objcopy  -O binary util_final.elf util_final.bin --set-start=_start
xxd -c 4 -p util_final.bin > xxd_c4_util_final_bin.txt
mipsel-linux-gnu-objdump -d util_final.elf > util_final.elf.objdump.d
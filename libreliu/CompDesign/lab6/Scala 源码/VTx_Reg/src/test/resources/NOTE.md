# .bss 自动填0
Sections:
Idx Name          Size      VMA       LMA       File off  Algn
  0 .text         000001f0  00000000  00000000  00010000  2**4
                  CONTENTS, ALLOC, LOAD, READONLY, CODE
  1 .bss          00000010  000001f0  000001f0  000101f0  2**4
                  ALLOC
（因为内存初始化完了就是0）
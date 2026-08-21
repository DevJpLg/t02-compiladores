; Assembly ficticio gerado pelo compilador da linguagem da disciplina
; Fonte de origem: correto_1.txt

        LOADI  R0, 0
        STORE  i, R0
        ; comando while: inicio do laco
L0:
        LOAD   R1, i
        LOADI  R2, 10
        CMPLT  R3, R1, R2
        JMPF   R3, L1
        ; comando if: avaliacao da condicao
        LOAD   R4, i
        LOADI  R5, 3
        CMPEQ  R6, R4, R5
        JMPF   R6, L2
        LOADI  R7, 1
        WRITE  R7
        JMP    L3
L2:
        LOAD   R8, i
        LOADI  R9, 3
        CMPEQ  R10, R8, R9
        NOT    R11, R10
        WRITE  R11
L3:
        LOAD   R12, i
        LOADI  R13, 1
        ADD    R14, R12, R13
        STORE  i, R14
        JMP    L0
L1:
        HALT

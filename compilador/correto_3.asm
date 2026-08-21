; Assembly ficticio gerado pelo compilador da linguagem da disciplina
; Fonte de origem: correto_3.txt

        LOADI  R0, 0
        STORE  tentativas, R0
        LOADI  R1, 0.0
        STORE  nota, R1
        ; comando while: inicio do laco
L0:
        LOAD   R2, tentativas
        LOADI  R3, 3
        CMPLT  R4, R2, R3
        JMPF   R4, L1
        READ   nota
        ; comando if: avaliacao da condicao
        LOAD   R5, nota
        LOADI  R6, 6.0
        CMPGE  R7, R5, R6
        LOAD   R8, tentativas
        LOADI  R9, 2
        CMPNE  R10, R8, R9
        AND    R11, R7, R10
        JMPF   R11, L2
        LOADI  R12, 1
        WRITE  R12
        JMP    L3
L2:
        LOADI  R13, 0
        WRITE  R13
L3:
        LOAD   R14, tentativas
        LOADI  R15, 1
        ADD    R16, R14, R15
        STORE  tentativas, R16
        JMP    L0
L1:
        HALT

; Assembly ficticio gerado pelo compilador da linguagem da disciplina
; Fonte de origem: correto_2.txt

        LOADI  R0, 100
        CVTIR  R1, R0
        STORE  inicial, R1
        LOAD   R2, inicial
        STORE  montante, R2
        ; comando for: inicializacao
        LOADI  R3, 1
        STORE  mes, R3
        ; comando for: teste da condicao
L0:
        LOAD   R4, mes
        LOADI  R5, 11
        CMPLT  R6, R4, R5
        JMPF   R6, L1
        LOAD   R10, montante
        LOADI  R11, 1.1
        MUL    R12, R10, R11
        STORE  montante, R12
        ; comando for: incremento
        LOAD   R7, mes
        LOADI  R8, 1
        ADD    R9, R7, R8
        STORE  mes, R9
        JMP    L0
L1:
        LOAD   R13, montante
        WRITE  R13
        HALT

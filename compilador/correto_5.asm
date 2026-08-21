; Assembly ficticio gerado pelo compilador da linguagem da disciplina
; Fonte de origem: correto_5.txt

        LOADI  R0, 250
        NEG    R1, R0
        STORE  saldo, R1
        LOADI  R2, 0.5
        NEG    R3, R2
        STORE  taxa, R3
        ; comando if: avaliacao da condicao
        LOAD   R4, saldo
        LOADI  R5, 0
        CMPLT  R6, R4, R5
        JMPF   R6, L0
        LOADI  R7, 100
        STORE  saldo_1, R7
        LOAD   R8, saldo_1
        WRITE  R8
L0:
        LOAD   R9, saldo
        WRITE  R9
        LOAD   R10, saldo
        NEG    R11, R10
        WRITE  R11
        LOAD   R12, taxa
        LOADI  R13, 2.0
        NEG    R14, R13
        MUL    R15, R12, R14
        WRITE  R15
        HALT

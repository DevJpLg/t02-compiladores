; Assembly ficticio gerado pelo compilador da linguagem da disciplina
; Fonte de origem: correto_4.txt

        READ   matricula
        LOADI  R0, 8.5
        STORE  media, R0
        LOADI  R1, 'A'
        STORE  letra, R1
        LOADI  R2, "Compiladores"
        STORE  disciplina, R2
        LOAD   R3, media
        LOADI  R4, 1.0
        SUB    R5, R3, R4
        LOADI  R6, 2.0
        DIV    R7, R5, R6
        STORE  ajuste, R7
        ; comando if: avaliacao da condicao
        LOAD   R8, media
        LOADI  R9, 7.0
        CMPGT  R10, R8, R9
        LOAD   R11, ajuste
        LOADI  R12, 3.0
        CMPLE  R13, R11, R12
        OR     R14, R10, R13
        JMPF   R14, L0
        LOADI  R15, 1
        STORE  aprovado_1, R15
        LOAD   R16, aprovado_1
        WRITE  R16
L0:
        LOAD   R17, matricula
        WRITE  R17
        LOAD   R18, media
        WRITE  R18
        LOAD   R19, ajuste
        WRITE  R19
        LOAD   R20, letra
        WRITE  R20
        LOAD   R21, disciplina
        WRITE  R21
        HALT

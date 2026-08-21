package compilador;

import java_cup.runtime.Symbol;

/* ============================================================
   LangZ - ESPECIFICACAO JFLEX (Analisador Lexico)
   Trabalho P2 - Compiladores

   Mesma especificacao entregue na A1, com uma unica mudanca
   exigida pela analise semantica: o token NUM foi separado em
   NUM_INT e NUM_REAL. E essa separacao que permite detectar a
   atribuicao de um valor real a uma variavel do tipo inteiro.
   ============================================================ */

%%

%class Lexer
%public
%unicode
%cup
%line
%column

%{
    /* Quantidade de caracteres invalidos encontrados no fonte.

       O contador existe para que um erro lexico REPROVE a compilacao.
       Antes dele a mensagem era apenas impressa e o caractere invalido
       era descartado: se a sequencia de tokens restante ainda fosse
       valida, o compilador seguia para as fases seguintes e anunciava
       "nenhum erro lexico", contradizendo a propria mensagem. */
    private int errosLexicos = 0;

    /** Quantos erros lexicos foram encontrados ate aqui. */
    public int errosLexicos() {
        return errosLexicos;
    }

    /* Token sem valor associado. A LINHA vai no campo "left" e a
       COLUNA no campo "right" do Symbol (ja com base 1), porque o
       analisador semantico usa os dois para localizar o erro. */
    private Symbol tk(int tipo) {
        return new Symbol(tipo, yyline + 1, yycolumn + 1);
    }

    /* Token que carrega um valor (identificador, numero, literal...). */
    private Symbol tk(int tipo, Object valor) {
        return new Symbol(tipo, yyline + 1, yycolumn + 1, valor);
    }
%}

/* ---------- MACROS ---------- */

Letra     = [a-zA-Z_]
Digito    = [0-9]
Espaco    = [ \t\r\n\f]+

Id        = {Letra}({Letra}|{Digito})*

NumInt    = {Digito}+
NumReal   = {Digito}+"."{Digito}+

LitChr    = "'"[^'\n]"'"
LitStr    = \"[^\"]*\"

Coment    = "/*" ~"*/"

%%

/* ---------- RUIDO ---------- */
{Espaco}   { }
{Coment}   { }

/* ---------- PALAVRAS RESERVADAS ---------- */
"if"       { return tk(sym.IF);     }
"else"     { return tk(sym.ELSE);   }
"while"    { return tk(sym.WHILE);  }
"for"      { return tk(sym.FOR);    }
"printf"   { return tk(sym.PRINTF); }
"scanf"    { return tk(sym.SCANF);  }
"true"     { return tk(sym.TRUE);   }
"false"    { return tk(sym.FALSE);  }

/* ---------- TIPOS ---------- */
"int"      { return tk(sym.T_INT);  }
"real"     { return tk(sym.T_REAL); }
"chr"      { return tk(sym.T_CHR);  }
"str"      { return tk(sym.T_STR);  }

/* ---------- OPERADORES DE DOIS CARACTERES ---------- */
"=="       { return tk(sym.EQ);  }
"!="       { return tk(sym.NEQ); }
"<="       { return tk(sym.LEQ); }
">="       { return tk(sym.GEQ); }
"&&"       { return tk(sym.AND); }
"||"       { return tk(sym.OR);  }

/* ---------- OPERADORES DE UM CARACTERE ---------- */
"!"        { return tk(sym.NOT);   }
"+"        { return tk(sym.PLUS);  }
"-"        { return tk(sym.MINUS); }
"*"        { return tk(sym.TIMES); }
"/"        { return tk(sym.DIV);   }
"="        { return tk(sym.ATTR);  }
"<"        { return tk(sym.LT);    }
">"        { return tk(sym.GT);    }

/* ---------- DELIMITADORES ---------- */
"("        { return tk(sym.LPAR);   }
")"        { return tk(sym.RPAR);   }
"{"        { return tk(sym.LBRACE); }
"}"        { return tk(sym.RBRACE); }
";"        { return tk(sym.SEMI);   }

/* ---------- LITERAIS E IDENTIFICADORES ---------- */
{LitChr}   { return tk(sym.LIT_CHR, yytext()); }
{LitStr}   { return tk(sym.LIT_STR, yytext()); }
{Id}       { return tk(sym.ID, yytext()); }
{NumReal}  { return tk(sym.NUM_REAL, Double.valueOf(yytext()));  }
{NumInt}   { return tk(sym.NUM_INT,  Integer.valueOf(yytext())); }

/* ---------- ERRO LEXICO ---------- */
[^]        { errosLexicos++;
             System.err.println("Erro Lexico [linha " + (yyline + 1)
                                + ", col " + (yycolumn + 1)
                                + "]: caractere invalido '" + yytext() + "'"); }

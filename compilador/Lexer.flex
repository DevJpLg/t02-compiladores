package compilador;

import java_cup.runtime.Symbol;

%%

%class Lexer
%public
%unicode
%cup
%line
%column
%{
    private int errosLexicos = 0;
    public int errosLexicos() {
        return errosLexicos;
    }
    private Symbol tk(int tipo) {
        return new Symbol(tipo, yyline + 1, yycolumn + 1);
    }
    private Symbol tk(int tipo, Object valor) {
        return new Symbol(tipo, yyline + 1, yycolumn + 1, valor);
    }
%}
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

{Espaco}   { }
{Coment}   { }

"if"       { return tk(sym.IF);     }
"else"     { return tk(sym.ELSE);   }
"while"    { return tk(sym.WHILE);  }
"for"      { return tk(sym.FOR);    }
"printf"   { return tk(sym.PRINTF); }
"scanf"    { return tk(sym.SCANF);  }
"true"     { return tk(sym.TRUE);   }
"false"    { return tk(sym.FALSE);  }

"int"      { return tk(sym.T_INT);  }
"real"     { return tk(sym.T_REAL); }
"chr"      { return tk(sym.T_CHR);  }
"str"      { return tk(sym.T_STR);  }

"=="       { return tk(sym.EQ);  }
"!="       { return tk(sym.NEQ); }
"<="       { return tk(sym.LEQ); }
">="       { return tk(sym.GEQ); }
"&&"       { return tk(sym.AND); }
"||"       { return tk(sym.OR);  }
"!"        { return tk(sym.NOT);   }
"+"        { return tk(sym.PLUS);  }
"-"        { return tk(sym.MINUS); }
"*"        { return tk(sym.TIMES); }
"/"        { return tk(sym.DIV);   }
"="        { return tk(sym.ATTR);  }
"<"        { return tk(sym.LT);    }
">"        { return tk(sym.GT);    }
"("        { return tk(sym.LPAR);   }
")"        { return tk(sym.RPAR);   }
"{"        { return tk(sym.LBRACE); }
"}"        { return tk(sym.RBRACE); }
";"        { return tk(sym.SEMI);   }
{LitChr}   { return tk(sym.LIT_CHR, yytext()); }
{LitStr}   { return tk(sym.LIT_STR, yytext()); }
{Id}       { return tk(sym.ID, yytext()); }
{NumReal}  { return tk(sym.NUM_REAL, Double.valueOf(yytext()));  }
{NumInt}   { return tk(sym.NUM_INT,  Integer.valueOf(yytext())); }
[^]        { errosLexicos++;
             System.err.println("Erro Lexico [linha " + (yyline + 1)
                                + ", col " + (yycolumn + 1)
                                + "]: caractere invalido '" + yytext() + "'"); }

# Gramática BNF — Linguagem LangZ

A linguagem **LangZ** é uma linguagem fictícia de uso didático, com suporte a
tipos primitivos, estruturas de controle e operações de entrada e saída.

> **Mudanças da P2:**
>
> 1. O token `<num>` da A1 foi separado em `<num_int>` e `<num_real>`. É essa
>    separação que permite ao analisador semântico detectar a atribuição de um
>    valor real a uma variável do tipo inteiro.
> 2. Foi acrescentado o **menos unário** (`-<expr>`), que não existia na A1.
>    Sem ele, escrever `x = -1;` era erro de sintaxe.
>
> O restante da gramática é idêntico ao da A1.

---

## Estrutura Geral

```
<programa> ::= <bloco>
```

Um programa LangZ é formado por um único bloco delimitado por chaves.

---

## Blocos e Comandos

```
<bloco> ::= "{" <lista_cmds> "}"
           | "{" "}"

<lista_cmds> ::= <cmd> <lista_cmds>
               | <cmd>

<cmd> ::= <decl>
        | <atrib>
        | <se_senao>
        | <enquanto>
        | <para>
        | <escrever>
        | <ler>
        | <bloco>
```

O primeiro bloco do programa é o escopo **global**. Cada bloco interno abre um
escopo **local**, descartado ao encontrar o `}`.

---

## Declaração e Atribuição

```
<decl>  ::= <tipo> <id> ";"

<tipo>  ::= "int" | "real" | "chr" | "str"

<atrib> ::= <id> "=" <expr> ";"
```

---

## Estruturas de Controle

```
<se_senao>  ::= "if" "(" <expr> ")" <bloco> <opt_senao>

<opt_senao> ::= "else" <bloco>
              | ε

<enquanto>  ::= "while" "(" <expr> ")" <bloco>

<para>      ::= "for" "(" <atrib> <expr> ";" <atrib_inc> ")" <bloco>

<atrib_inc> ::= <id> "=" <expr>
```

---

## Entrada e Saída

```
<escrever> ::= "printf" "(" <expr> ")" ";"

<ler>      ::= "scanf"  "(" <id>   ")" ";"
```

---

## Expressões

```
<expr> ::= <expr> <op_arit> <expr>
         | <expr> <op_rel>  <expr>
         | <expr> <op_log>  <expr>
         | "-" <expr>          /* menos unario */
         | "!" <expr>
         | <termo>

<termo> ::= <id>
           | <num_int>
           | <num_real>
           | <lit_chr>
           | <lit_str>
           | "true"
           | "false"
           | "(" <expr> ")"
```

---

## Operadores

```
<op_arit> ::= "+" | "-" | "*" | "/"

<op_rel>  ::= "==" | "!=" | "<" | ">" | "<=" | ">="

<op_log>  ::= "&&" | "||"
```

Precedência, da menor para a maior: `||` → `&&` → `==` `!=` → `<` `>` `<=` `>=`
→ `+` `-` → `*` `/` → `!` e `-` unário (ambos no topo, associativos à direita).
O `else` é associado ao `if` mais próximo.

---

## Elementos Léxicos

```
<id>       ::= letra ( letra | digito )*
<num_int>  ::= digito+
<num_real> ::= digito+ "." digito+
<lit_chr>  ::= "'" caractere "'"
<lit_str>  ::= '"' { caractere } '"'
<letra>    ::= [a-zA-Z_]
<digito>   ::= [0-9]
```

---

## Regras Semânticas (acrescentadas na P2)

Além dos quatro tipos declaráveis (`int`, `real`, `chr`, `str`), existe o tipo
interno **`logico`**, produzido por `true`, `false`, pelas comparações e pelos
operadores lógicos. Ele não pode ser declarado nem armazenado em variável: serve
apenas como condição de `if`, `while` e `for`.

| Situação | Regra |
|---|---|
| `int = int`, `real = real`, `chr = chr`, `str = str` | válido |
| `real = int` | válido, com conversão implícita (`CVTIR`) |
| `int = real` e demais combinações | **erro semântico** |
| `+` `-` `*` `/` | apenas `int` e `real`; `int op int` → `int`, qualquer `real` → `real` |
| `<` `>` `<=` `>=` | apenas operandos numéricos → `logico` |
| `==` `!=` | operandos numéricos ou do mesmo tipo → `logico` |
| `&&` `\|\|` | os dois operandos precisam ser `logico` → `logico` |
| `!` | operando precisa ser `logico` → `logico` |
| `-` unário | operando precisa ser `int` ou `real` → mesmo tipo do operando |
| condição de `if` / `while` / `for` | precisa resultar em `logico` |
| identificador | precisa estar declarado e visível no escopo atual |
| declaração | não pode repetir o mesmo nome no mesmo escopo |

---

## Nomes das variáveis no código gerado

Uma variável **global** (nível 0) é endereçada no Assembly pelo próprio nome.
Uma variável **local** recebe o nível do bloco como sufixo — `aprovado` declarado
dentro de um `if` vira `aprovado_1`. Isso evita que uma variável local que
**sombreia** uma global de mesmo nome acabe gravando no mesmo endereço:

```
{ int x;          →  STORE x,   R0     /* global  */
  x = 1;
  { real x;       →  STORE x_1, R1     /* local, endereco distinto */
    x = 2.5; } }
```

A tabela de símbolos mostra esse nome na coluna final, quando ele difere do nome
declarado (`-> aprovado_1`).

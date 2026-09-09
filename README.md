# Compilador da linguagem LangZ

**Trabalho da P2 — Compiladores**
Grupo: **João Pedro**, **Breno** e **Gabriel**

Compilador completo para a **LangZ**, uma linguagem fictícia criada na disciplina.
Partindo do analisador léxico (JFlex) e sintático (CUP) entregues na A1, esta etapa
acrescenta a **análise semântica** e a **geração de código Assembly fictício**.

As quatro fases rodam em sequência sobre cada arquivo-fonte:

```
léxica  ->  sintática  ->  semântica  ->  geração de código
 JFlex        CUP        AnalisadorSemântico   GeradorDeCodigo
                         + TabelaDeSimbolos      -> arquivo .asm
```

A tradução é **dirigida pela sintaxe**: as verificações semânticas e a emissão de
instruções acontecem durante a própria redução das produções do CUP, sem uma passagem
extra sobre a árvore.

---

## ✅ Mapa dos requisitos

### 1) Ações semânticas (pedido: pelo menos 3 erros)

Foram implementadas **6 verificações**, cobrindo 4 dos 5 exemplos do enunciado:

| # | Erro semântico verificado | Item | Onde está |
|---|---|---|---|
| 1 | Variável utilizada sem ter sido declarada | (a) | `AnalisadorSemantico.usar()` |
| 2 | Incompatibilidade de tipos na atribuição | (b) | `AnalisadorSemantico.verificarAtribuicao()` |
| 3 | Escopo: local usada fora do bloco onde foi declarada | (c) | `TabelaDeSimbolos.buscar()` (pilha de ambientes) |
| 4 | Operadores aritméticos, relacionais e lógicos com operandos inválidos | (d) | `verificarAritmetica()`, `verificarComparacao()`, `verificarLogica()`, `verificarNegativo()`, `verificarNegacao()` |
| 5 | Declaração repetida no mesmo escopo | extra | `AnalisadorSemantico.declarar()` |
| 6 | Condição de `if`/`while`/`for` que não resulta em valor lógico | extra | `AnalisadorSemantico.verificarCondicao()` |

> O item (e), correspondência entre parâmetros atuais e formais, não se aplica:
> a gramática da LangZ não possui declaração de funções.

**Classes exigidas pelo enunciado:**

- Analisador Semântico → [`compilador/AnalisadorSemantico.java`](compilador/AnalisadorSemantico.java)
- Tabela de Símbolos → [`compilador/TabelaDeSimbolos.java`](compilador/TabelaDeSimbolos.java)

### 2) Geração de código Assembly (pedido: 2 comandos)

Foram implementados **6 comandos**:

| Comando | Instruções geradas |
|---|---|
| `printf` | `WRITE` |
| `scanf` | `READ` |
| `if` / `else` | `JMPF`, `JMP` e rótulos |
| `while` | rótulo de início, `JMPF`, `JMP` |
| `for` | inicialização, teste, corpo e incremento reordenados |
| atribuição e expressões | `LOAD`, `LOADI`, `STORE`, `ADD`, `SUB`, `MUL`, `DIV`, `NEG`, `NOT`, `CVTIR`, `CMP*`, `AND`, `OR` |

**Classe exigida pelo enunciado:**

- Geração de Código → [`compilador/GeradorDeCodigo.java`](compilador/GeradorDeCodigo.java)

### Arquivos-fonte de teste

| Requisito | Entregue | Arquivos |
|---|---|---|
| 03 códigos-fontes corretos | **5** | `correto_1.txt` a `correto_5.txt` |
| 03 códigos-fontes com erros semânticos | **3** | `erro_semantico_1.txt` a `erro_semantico_3.txt` |

Cada fonte com erro traz, em comentário na própria linha, qual erro é esperado ali.
Os quatro arquivos `erro_bloco.txt`, `erro_expressao.txt`, `erro_lexico.txt` e
`erro_sintatico.txt` são da **A1** e testam as fases léxica e sintática; foram mantidos
como referência e não fazem parte dos três fontes pedidos neste trabalho.

---

## ⚠️ Pré-requisitos

É preciso ter um **JDK** (não apenas o JRE) instalado e no `PATH` do sistema:

```cmd
javac -version
java -version
```

Se `javac` não for reconhecido, use o caminho completo do JDK instalado, por
exemplo `"C:\Program Files\Java\jdk-XX\bin\javac.exe"`.

Os comandos abaixo usam `;` como separador de classpath (Windows). No Linux/macOS,
troque `;` por `:` e as barras `\` por `/`.

O `cup.jar` e o `jflex.jar` já acompanham o repositório — não é preciso baixar nada.

---

## ▶️ Como rodar

### Passo 1 — Limpar arquivos gerados anteriormente

```cmd
del /q compilador\*.class compilador\Lexer.java compilador\parser.java compilador\sym.java
```

_(Se for a primeira vez, pode aparecer "não foi possível encontrar" — ignorar e continuar.)_

### Passo 2 — Gerar o analisador sintático (CUP → `parser.java` e `sym.java`)

```cmd
java -jar cup.jar -destdir compilador -parser parser -symbols sym compilador\Parser.cup
```

✅ Esperado: `0 errors and 0 warnings`, `0 conflicts detected` e
`Code written to "parser.java", and "sym.java"`.

### Passo 3 — Gerar o analisador léxico (JFlex → `Lexer.java`)

```cmd
java -jar jflex.jar compilador\Lexer.flex
```

✅ Esperado: `Writing code to "compilador\Lexer.java"`.

### Passo 4 — Compilar todos os arquivos Java

```cmd
javac -encoding UTF-8 -cp ".;cup.jar" compilador\*.java
```

✅ Esperado: sem erros. Avisos de `deprecated API` são normais.

### Passo 5 — Executar o compilador

```cmd
java -cp ".;cup.jar" compilador.Compilador
```

✅ Esperado: para cada fonte, a **tabela de símbolos**; nos fontes corretos, o
**Assembly gerado** e a gravação do arquivo `.asm`; nos fontes com erro, a
**lista de erros semânticos** com linha e coluna. No final:
`RESUMO: 5 fonte(s) compilado(s), 3 fonte(s) com erro.`

### Passo 6 (opcional) — Compilar um fonte específico

```cmd
java -cp ".;cup.jar" compilador.Compilador compilador\correto_2.txt
```

---

## 📁 Estrutura do projeto

```
t02-compiladores/
├─ compilador/
│  ├─ Lexer.flex               especificação JFlex  (analisador léxico)
│  ├─ Parser.cup               especificação CUP    (gramática + ações semânticas)
│  │
│  ├─ AnalisadorSemantico.java  verificações semânticas          [requisito 1]
│  ├─ TabelaDeSimbolos.java     pilha de escopos e símbolos    [requisito 1]
│  ├─ GeradorDeCodigo.java      tradução para Assembly         [requisito 2]
│  ├─ AtributosExpressao.java   liga a semântica à geração de código
│  ├─ Compilador.java           classe principal (main)
│  │
│  ├─ Lexer.java               gerado pelo JFlex
│  ├─ parser.java, sym.java    gerados pelo CUP
│  │
│  ├─ correto_[1-5].txt        fontes corretos
│  ├─ correto_[1-5].asm        Assembly gerado a partir deles
│  ├─ erro_semantico_[1-3].txt fontes com erros semânticos
│  └─ erro_*.txt               fontes com erros léxicos/sintáticos (da A1)
│
├─ bnf.md                      gramática BNF da LangZ
├─ cup.jar, jflex.jar          geradores usados na build
└─ README.md
```

---

## 🧮 O Assembly fictício

Máquina de registradores (`R0`, `R1`, ...) e rótulos (`L0`, `L1`, ...):

| Instrução | Efeito |
|---|---|
| `LOADI Rd, valor` | carrega uma constante no registrador |
| `LOAD  Rd, var` | carrega o conteúdo de uma variável |
| `STORE var, Rs` | grava o registrador na variável |
| `ADD` / `SUB` / `MUL` / `DIV  Rd, Rs, Rt` | operações aritméticas |
| `NEG   Rd, Rs` | troca o sinal (menos unário) |
| `NOT   Rd, Rs` | negação lógica |
| `CVTIR Rd, Rs` | converte inteiro em real (promoção de tipo) |
| `CMPEQ` / `CMPNE` / `CMPLT` / `CMPGT` / `CMPLE` / `CMPGE` | comparações (resultado 0 ou 1) |
| `AND` / `OR   Rd, Rs, Rt` | operadores lógicos |
| `JMPF  Rs, L` | desvia para `L` se `Rs` for falso |
| `JMP   L` | desvio incondicional |
| `READ  var` | leitura de dado (`scanf`) |
| `WRITE Rs` | escrita de dado (`printf`) |
| `HALT` | fim do programa |

### Dois detalhes de implementação

**Endereçamento de variáveis locais.** Cada bloco `{ ... }` aberto recebe um
identificador único, usado como sufixo no rótulo da variável (`saldo_1`). Sem isso,
uma local que sombreia uma global de mesmo nome geraria o mesmo `STORE`/`LOAD` e as
duas dividiriam o mesmo endereço. O sufixo é o identificador do bloco, e não o nível
de aninhamento, porque dois blocos **irmãos** estão no mesmo nível.

**Reordenação do `for`.** No texto o incremento aparece antes do corpo do laço, mas
precisa ser executado depois dele. O gerador captura as instruções do incremento em
um buffer temporário e as reemite na posição correta.

---

## 🗣️ Divisão da apresentação

| Integrante | Parte |
|---|---|
| | Análise léxica e sintática — `Lexer.flex`, `Parser.cup` e as mudanças da A1 para a P2 |
| | Análise semântica — `AnalisadorSemantico`, `TabelaDeSimbolos` e os três fontes com erro |
| | Geração de código — `GeradorDeCodigo`, `AtributosExpressao` e a reordenação do `for` |

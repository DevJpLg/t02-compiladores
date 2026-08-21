# Verificação do Trabalho P2 — Compiladores (LangZ)

**Entrega:** 09/09/2026 · **Apresentação:** 10/09/2026
**Última verificação:** 21/08/2026 — build limpo do zero (CUP → JFlex → `javac`),
os 12 fontes da pasta mais 11 fontes adversariais escritos para forçar casos-limite.

> **Situação: conforme.** Todos os itens do enunciado estão cumpridos, e os três
> defeitos encontrados na primeira revisão foram corrigidos e reverificados.

---

## 1. Checklist do enunciado

| # | Exigência | Situação |
|---|---|---|
| 1 | Ações semânticas para **pelo menos 3** erros semânticos | ✅ **6 verificações** |
| 1 | Classe Java para o **Analisador Semântico** | ✅ `AnalisadorSemantico.java` |
| 1 | Classe Java para a **Tabela de Símbolos** | ✅ `TabelaDeSimbolos.java` |
| 2 | Geração de Assembly fictício para **2 comandos** | ✅ **5 comandos**: `if/else`, `while`, `for`, `printf`, `scanf` |
| 2 | Classe Java para a **Geração de Código** | ✅ `GeradorDeCodigo.java` |
| a | Todos os arquivos (JFlex, CUP, classes Java, etc.) | ✅ |
| b | **03 códigos-fonte corretos** | ✅ **5** (`correto_1..5.txt`) |
| c | **03 códigos-fonte com erros semânticos** | ✅ **3** (`erro_semantico_1..3.txt`) |
| — | Apresentação individual por integrante | ✅ divisão definida no README |

### Cobertura dos exemplos de erro semântico do professor

| Item do enunciado | Coberto? | Onde é demonstrado |
|---|---|---|
| (a) variável usada mas não declarada | ✅ | `erro_semantico_1.txt` (`b`, `c`) |
| (b) compatibilidade de valores com tipos | ✅ | `erro_semantico_2.txt` (`real` → `int`, `int` → `str`) |
| (c) escopo de variáveis (locais e globais) | ✅ | `erro_semantico_3.txt` (`y` fora do bloco) |
| (d) erros em operadores e operandos | ✅ | `erro_semantico_2/3.txt` (`str + int`, `int && logico`, `int == logico`) |
| (e) parâmetros atuais × formais | ➖ **N/A** | LangZ não tem funções/procedimentos |

**Resposta pronta para o item (e), se ele perguntar na apresentação:** *"a gramática
da LangZ, herdada da A1, não possui declaração de funções nem chamadas com
parâmetros, então esse erro não existe na linguagem. O enunciado pede 'pelo menos
três' e apresenta a lista como 'alguns exemplos'; no lugar do (e) implementamos
redeclaração no mesmo escopo e checagem da condição de `if`/`while`/`for`."*

---

## 2. Correções aplicadas nesta rodada

### ✅ Erro léxico agora reprova o fonte

**Antes:** a regra de erro do JFlex apenas *imprimia* a mensagem. Se o caractere
inválido caísse numa posição em que descartá-lo ainda deixava a sequência de
tokens válida, o compilador imprimia `Erro Lexico` e, logo abaixo, afirmava
`nenhum erro lexico, sintatico ou semantico`, gerava o `.asm` e contava o fonte
como compilado.

**Depois:** o `Lexer` conta os caracteres inválidos (`errosLexicos()`) e o
`Compilador` consulta esse contador antes de liberar a geração de código.

```
Erro Lexico [linha 4, col 5]: caractere invalido '@'
  RESULTADO: 1 erro(s) lexico(s) encontrado(s) - mensagens acima.
  Geracao de codigo cancelada para este fonte.
```

*Arquivos:* `Lexer.flex`, `Compilador.java`.

### ✅ Erros semânticos saem ordenados por posição

**Antes:** dentro de um `for`, o erro do lado direito da atribuição era registrado
antes do erro do lado esquerdo (coluna 28 aparecia antes da coluna 24), porque a
ação semântica da atribuição só roda depois que a expressão reduz.

**Depois:** cada erro guarda linha e coluna e a lista é ordenada antes de ser
exibida.

*Arquivo:* `AnalisadorSemantico.java` (classe interna `Erro implements Comparable`).

### ✅ Sombreamento gera endereços distintos no Assembly

**Antes:** uma variável local com o mesmo nome de uma global gerava o mesmo
`STORE x` — as duas dividiam o mesmo endereço e a global era sobrescrita.

**Depois:** cada símbolo carrega o nome que a geração de código usa. Global mantém
o próprio nome; local recebe o nível do bloco como sufixo:

```
  saldo        int      linha 3    nivel 0
  saldo        int      linha 10   nivel 1   -> saldo_1

          STORE  saldo,   R1     ; global
          STORE  saldo_1, R7     ; local, endereco distinto
```

*Arquivos:* `TabelaDeSimbolos.java` (campo `rotulo`), `AnalisadorSemantico.java`
(`rotuloDe`), `Parser.cup` (`LOAD` / `STORE` / `READ`).

### ✅ Menos unário acrescentado à linguagem

**Antes:** `x = -1;` era **erro de sintaxe** — o `-` só existia como operador
binário na gramática da A1. Um risco real numa demonstração ao vivo.

**Depois:** produção `expr ::= "-" expr` com a mesma precedência (máxima) do `!`,
verificação semântica (`-str` e `-logico` são erro) e a instrução `NEG Rd, Rs`.

*Arquivos:* `Parser.cup`, `AnalisadorSemantico.java` (`verificarNegativo`),
`GeradorDeCodigo.java`, `bnf.md`.

### ✅ Novo fonte de teste `correto_5.txt`

Cobre exatamente o que a rodada acrescentou: menos unário sobre literal e sobre
variável, e sombreamento de variável local. Sem ele, as duas correções acima
ficariam sem teste na entrega.

### ✅ Documentação atualizada

`bnf.md` (menos unário, precedência, regra semântica, esquema de nomes no código
gerado) e `README.md` (instrução `NEG`, ordenação dos erros, contagem de erros
léxicos, item (e) do enunciado, novo fonte, resumo esperado).

---

## 3. O que foi verificado e está correto

- **Build limpo:** CUP `0 errors, 0 warnings, 0 conflicts detected`,
  `0 terminals declared but not used`, `0 productions never reduced`.
  JFlex e `javac` sem erros. O único aviso (`deprecated API`) vem do construtor
  gerado pelo CUP e é inofensivo.
- **Saída esperada:** `RESUMO: 5 fonte(s) compilado(s), 3 fonte(s) com erro.`
- **Tabela de símbolos:** pilha de ambientes correta — busca do escopo interno
  para o externo, descarte no `}`, nível correto, sombreamento aceito.
- **Sem cascata de erros:** o tipo `indefinido` corta a propagação; um `b` não
  declarado gera um erro por uso, não uma avalanche.
- **Linha e coluna conferidas** caractere a caractere nos três `erro_semantico_*`.
- **Geração de código nos casos difíceis:**
  - `if/else` aninhado dentro de `if/else` — pilha de rótulos balanceada;
  - `for` dentro de `for` — o buffer de captura do incremento não se embaralha;
  - `while` e `for` reavaliam a condição a cada volta;
  - o incremento do `for` é reemitido **depois** do corpo, na posição certa.
- **Geração de código cancelada** sempre que há erro léxico ou semântico.
- **Erros léxicos e sintáticos** dos 4 fontes extras da A1 continuam detectados
  com linha e coluna.
- Nenhum `.txt` tem BOM (evita falso "caractere inválido" no Windows).

---

## 4. Limitações conhecidas (nada a corrigir — só para saber responder)

- **`logico` não pode ser armazenado.** `int flag; flag = true;` é erro semântico:
  `logico` é um tipo interno de expressão, não um tipo declarável. É intencional e
  está documentado no `bnf.md`.
- **Corpo de `if`/`while`/`for` sempre exige chaves** (`if (x > 0) printf(x);` é
  erro de sintaxe). Herdado da A1.
- **Sem verificação de uso antes da inicialização** — o enunciado não pede.
- **Registradores nunca são reaproveitados** (`R0..R21` em `correto_4`). Normal num
  gerador didático sem alocação de registradores; bom item de "trabalho futuro".
- **Sem curto-circuito em `&&` / `||`** — os dois lados são sempre avaliados.
  Também é trabalho futuro clássico.

---

## 5. Antes de enviar (até 09/09)

1. Rodar os Passos 1 a 5 do `README.md` numa máquina limpa, do zero.
2. Enviar a pasta completa para `brenoter@gmail.com`: `Lexer.flex`, `Parser.cup`,
   as 5 classes `.java`, `cup.jar`, `jflex.jar`, `java_cup/`, os 12 `.txt`, os
   `.asm` gerados, `README.md` e `bnf.md`.
3. Os `.class` são recriados pelo Passo 4 — se preferir, remova-os do `.zip` e
   avise no e-mail, para evitar incompatibilidade de versão de JDK na máquina dele.
4. Compactar em `.zip` (o Gmail bloqueia `.jar` solto; dentro do `.zip` costuma
   passar — se não passar, mandar por link do Drive).

# 🖥️ Trabalho P2 - Compiladores - Grupo: João Pedro, Breno e Gabriel

## ▶️ Como Rodar
---

### Passo 1 — Limpar arquivos gerados anteriormente

```cmd
del /q compilador\*.class compilador\Lexer.java compilador\parser.java compilador\sym.java
```

_(Se for a primeira vez, pode aparecer "não foi possível encontrar" — ignorar e continuar.)_

---

### Passo 2 — Gerar o analisador sintático (CUP → `parser.java` e `sym.java`)

```cmd
java -jar cup.jar -destdir compilador -parser parser -symbols sym compilador\Parser.cup
```

✅ Esperado: mensagem `0 errors and 0 warnings`, `0 conflicts detected` e `Code written to "parser.java", and "sym.java"`.

---

### Passo 3 — Gerar o analisador léxico (JFlex → `Lexer.java`)

```cmd
java -jar jflex.jar compilador\Lexer.flex
```

✅ Esperado: mensagem `Writing code to "compilador\Lexer.java"`.

---

### Passo 4 — Compilar todos os arquivos Java

```cmd
"C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -encoding UTF-8 -cp ".;cup.jar" compilador\*.java
```

✅ Esperado: sem erros. Avisos de `deprecated API` são normais — não são erros.

---

### Passo 5 — Executar o compilador

```cmd
"C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp ".;cup.jar" compilador.Compilador
```

✅ Esperado: para cada fonte, a **tabela de símbolos**; nos fontes corretos, o
**Assembly gerado** e a gravação do arquivo `.asm`; nos fontes com erro, a
**lista de erros semânticos** com linha e coluna. No final:
`RESUMO: 5 fonte(s) compilado(s), 3 fonte(s) com erro.`

---

### Passo 6 (opcional) — Compilar um fonte específico

```cmd
"C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp ".;cup.jar" compilador.Compilador compilador\correto_2.txt
```
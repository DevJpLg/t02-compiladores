package compilador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ANALISADOR SEMANTICO
 *
 * Concentra as acoes semanticas chamadas pelas producoes do CUP.
 * Ele nao percorre uma arvore pronta: as verificacoes acontecem
 * durante a propria reducao das regras (traducao dirigida pela sintaxe).
 *
 * Erros verificados:
 *   (1) uso de variavel nao declarada;
 *   (2) declaracao repetida no mesmo escopo;
 *   (3) incompatibilidade de tipos na atribuicao (ex.: real -> int);
 *   (4) operadores aritmeticos, relacionais ou logicos aplicados a
 *       operandos invalidos;
 *   (5) escopo: variavel local usada fora do bloco onde foi declarada;
 *   (6) condicao de if/while/for que nao resulta em valor logico.
 *
 * Os erros sao guardados com a linha e a coluna em que ocorreram e
 * devolvidos ORDENADOS por posicao no fonte, porque a ordem em que as
 * producoes reduzem nem sempre e a ordem em que o programador escreveu
 * (no comando for, por exemplo, o lado direito da atribuicao reduz antes
 * do lado esquerdo).
 */
public class AnalisadorSemantico {

    /** Tipo devolvido quando a verificacao falhou; evita erros em cascata. */
    public static final String INDEFINIDO = "indefinido";
    /** Tipo interno produzido pelas comparacoes, pelos operadores logicos
        e pelas constantes true/false. */
    public static final String LOGICO = "logico";

    /**
     * Um erro semantico com a sua posicao no fonte, para que a lista final
     * possa ser ordenada por linha e coluna antes de ser exibida.
     */
    private static class Erro implements Comparable<Erro> {
        final int linha;
        final int coluna;
        final String descricao;

        Erro(int linha, int coluna, String descricao) {
            this.linha     = linha;
            this.coluna    = coluna;
            this.descricao = descricao;
        }

        public int compareTo(Erro outro) {
            if (linha != outro.linha) {
                return (linha < outro.linha) ? -1 : 1;
            }
            if (coluna != outro.coluna) {
                return (coluna < outro.coluna) ? -1 : 1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "Erro Semantico [linha " + linha + ", coluna " + coluna + "]: " + descricao;
        }
    }

    private final TabelaDeSimbolos tabela = new TabelaDeSimbolos();
    private final List<Erro> erros = new ArrayList<Erro>();
    private final List<String> avisos = new ArrayList<String>();

    /* ---------------- controle de escopo ---------------- */

    /** Producao do bloco: "{" abre um novo ambiente de nomes. */
    public void abrirEscopo() {
        tabela.abrirEscopo();
    }

    /** Producao do bloco: "}" descarta as variaveis locais do bloco. */
    public void fecharEscopo() {
        tabela.fecharEscopo();
    }

    /* ---------------- declaracao e uso ---------------- */

    /**
     * Acao da producao "tipo IDENT ;".
     * Erro (2): o mesmo nome ja foi declarado neste escopo.
     */
    public void declarar(String nome, String tipo, int linha, int coluna) {
        if (!tabela.inserir(nome, tipo, linha)) {
            TabelaDeSimbolos.Simbolo anterior = tabela.buscar(nome);
            registrarErro(linha, coluna, "identificador '" + nome
                    + "' ja foi declarado neste escopo (linha " + anterior.linha + ").");
        }
    }

    /**
     * Acao usada sempre que um identificador aparece em uma expressao,
     * em uma atribuicao ou dentro de input(...).
     * Erros (1) e (5): o nome nao existe ou nao esta visivel neste escopo.
     *
     * @return o tipo declarado do identificador, ou INDEFINIDO.
     */
    public String usar(String nome, int linha, int coluna) {
        TabelaDeSimbolos.Simbolo s = tabela.buscar(nome);
        if (s == null) {
            registrarErro(linha, coluna, "variavel '" + nome
                    + "' utilizada sem declaracao visivel neste escopo.");
            return INDEFINIDO;
        }
        return s.tipo;
    }

    /**
     * Nome que o GERADOR DE CODIGO deve usar para enderecar o identificador.
     * Uma global mantem o proprio nome; uma local recebe o nivel do bloco
     * como sufixo (ex.: "aprovado_1"), de modo que uma variavel local que
     * sombreia uma global nao acabe gravando no mesmo endereco.
     */
    public String rotuloDe(String nome) {
        TabelaDeSimbolos.Simbolo s = tabela.buscar(nome);
        return (s == null) ? nome : s.rotulo;
    }

    /* ---------------- compatibilidade de tipos ---------------- */

    /**
     * Acao da producao de atribuicao "IDENT = expressao ;".
     * Erro (3): o tipo do lado direito nao cabe na variavel do lado esquerdo.
     * Unica conversao aceita: int -> real (promocao implicita).
     */
    public void verificarAtribuicao(String tipoDestino, String tipoOrigem, String nome,
                                    int linha, int coluna) {
        if (indefinido(tipoDestino) || indefinido(tipoOrigem)) {
            return; // erro anterior ja foi relatado
        }
        if (tipoDestino.equals(tipoOrigem)) {
            return;
        }
        if (exigeConversao(tipoDestino, tipoOrigem)) {
            avisos.add("Aviso na linha " + linha + ": conversao implicita de int para real em '"
                    + nome + "'.");
            return;
        }
        registrarErro(linha, coluna, "tipos incompativeis: a variavel '" + nome
                + "' e do tipo '" + tipoDestino + "' e recebeu um valor do tipo '"
                + tipoOrigem + "'.");
    }

    /** Indica se a atribuicao precisa da instrucao de conversao int -> real. */
    public boolean exigeConversao(String tipoDestino, String tipoOrigem) {
        return "real".equals(tipoDestino) && "int".equals(tipoOrigem);
    }

    /* ---------------- operadores e operandos ---------------- */

    /**
     * Acao das producoes aritmeticas (+, -, *, /).
     * Erro (4): so numeros podem participar dessas operacoes.
     * @return "int" quando os dois operandos sao inteiros, "real" quando
     *         ha promocao, ou INDEFINIDO quando a operacao e invalida.
     */
    public String verificarAritmetica(String operador, String tipoEsq, String tipoDir,
                                      int linha, int coluna) {
        if (indefinido(tipoEsq) || indefinido(tipoDir)) {
            return INDEFINIDO;
        }
        if (numerico(tipoEsq) && numerico(tipoDir)) {
            return (tipoEsq.equals("real") || tipoDir.equals("real")) ? "real" : "int";
        }
        registrarErro(linha, coluna, "o operador '" + operador
                + "' nao pode ser aplicado aos operandos '" + tipoEsq + "' e '" + tipoDir + "'.");
        return INDEFINIDO;
    }

    /**
     * Acao das producoes de comparacao (==, !=, <, >, <=, >=).
     * Erro (4): os relacionais de ordem exigem numeros; "==" e "!=" exigem
     * operandos numericos ou do mesmo tipo.
     * @return LOGICO quando a comparacao e valida.
     */
    public String verificarComparacao(String operador, String tipoEsq, String tipoDir,
                                      int linha, int coluna) {
        if (indefinido(tipoEsq) || indefinido(tipoDir)) {
            return INDEFINIDO;
        }
        boolean valida;
        if (operador.equals("==") || operador.equals("!=")) {
            valida = (numerico(tipoEsq) && numerico(tipoDir)) || tipoEsq.equals(tipoDir);
        } else {
            valida = numerico(tipoEsq) && numerico(tipoDir);
        }
        if (valida) {
            return LOGICO;
        }
        registrarErro(linha, coluna, "comparacao invalida: '" + operador
                + "' nao aceita os operandos '" + tipoEsq + "' e '" + tipoDir + "'.");
        return INDEFINIDO;
    }

    /**
     * Acao das producoes dos operadores logicos (&& e ||).
     * Erro (4): os dois operandos precisam ser expressoes logicas.
     * @return LOGICO quando a operacao e valida.
     */
    public String verificarLogica(String operador, String tipoEsq, String tipoDir,
                                  int linha, int coluna) {
        if (indefinido(tipoEsq) || indefinido(tipoDir)) {
            return INDEFINIDO;
        }
        if (LOGICO.equals(tipoEsq) && LOGICO.equals(tipoDir)) {
            return LOGICO;
        }
        registrarErro(linha, coluna, "o operador logico '" + operador
                + "' exige dois operandos logicos, mas recebeu '" + tipoEsq
                + "' e '" + tipoDir + "'.");
        return INDEFINIDO;
    }

    /**
     * Acao da producao do MENOS UNARIO (-expr).
     * Erro (4): so faz sentido trocar o sinal de um numero.
     */
    public String verificarNegativo(String tipo, int linha, int coluna) {
        if (indefinido(tipo)) {
            return INDEFINIDO;
        }
        if (numerico(tipo)) {
            return tipo;
        }
        registrarErro(linha, coluna, "o operador '-' unario exige um operando numerico, "
                + "mas recebeu '" + tipo + "'.");
        return INDEFINIDO;
    }

    /**
     * Acao da producao de negacao (!expr).
     * Erro (4): so faz sentido negar uma expressao logica.
     */
    public String verificarNegacao(String tipo, int linha, int coluna) {
        if (indefinido(tipo)) {
            return INDEFINIDO;
        }
        if (LOGICO.equals(tipo)) {
            return LOGICO;
        }
        registrarErro(linha, coluna, "o operador '!' exige um operando logico, mas recebeu '"
                + tipo + "'.");
        return INDEFINIDO;
    }

    /**
     * Acao dos comandos if, while e for.
     * Erro (6): a condicao precisa ser uma expressao logica.
     */
    public void verificarCondicao(String tipo, String comando, int linha, int coluna) {
        if (indefinido(tipo)) {
            return;
        }
        if (!LOGICO.equals(tipo)) {
            registrarErro(linha, coluna, "a condicao do comando '" + comando
                    + "' deve ser uma expressao logica, mas resultou em '" + tipo + "'.");
        }
    }

    /* ---------------- utilitarios ---------------- */

    private boolean numerico(String tipo) {
        return "int".equals(tipo) || "real".equals(tipo);
    }

    private boolean indefinido(String tipo) {
        return tipo == null || INDEFINIDO.equals(tipo);
    }

    private void registrarErro(int linha, int coluna, String descricao) {
        erros.add(new Erro(linha, coluna, descricao));
    }

    public boolean possuiErros() {
        return !erros.isEmpty();
    }

    /** Erros formatados e ORDENADOS por linha e coluna. */
    public List<String> erros() {
        List<Erro> ordenados = new ArrayList<Erro>(erros);
        Collections.sort(ordenados);
        List<String> texto = new ArrayList<String>();
        for (Erro e : ordenados) {
            texto.add(e.toString());
        }
        return texto;
    }

    public List<String> avisos() {
        return avisos;
    }

    public TabelaDeSimbolos tabela() {
        return tabela;
    }
}

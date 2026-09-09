package compilador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalisadorSemantico {
    public static final String INDEFINIDO = "indefinido";
    public static final String LOGICO = "logico";
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
    public void abrirEscopo() {
        tabela.abrirEscopo();
    }
    public void fecharEscopo() {
        tabela.fecharEscopo();
    }

    public void declarar(String nome, String tipo, int linha, int coluna) {
        if (!tabela.inserir(nome, tipo, linha)) {
            TabelaDeSimbolos.Simbolo anterior = tabela.buscar(nome);
            registrarErro(linha, coluna, "identificador '" + nome
                    + "' ja foi declarado neste escopo (linha " + anterior.linha + ").");
        }
    }
    public String usar(String nome, int linha, int coluna) {
        TabelaDeSimbolos.Simbolo s = tabela.buscar(nome);
        if (s == null) {
            registrarErro(linha, coluna, "variavel '" + nome
                    + "' utilizada sem declaracao visivel neste escopo.");
            return INDEFINIDO;
        }
        return s.tipo;
    }
    public String rotuloDe(String nome) {
        TabelaDeSimbolos.Simbolo s = tabela.buscar(nome);
        return (s == null) ? nome : s.rotulo;
    }
    public void verificarAtribuicao(String tipoDestino, String tipoOrigem, String nome,
                                    int linha, int coluna) {
        if (indefinido(tipoDestino) || indefinido(tipoOrigem)) {
            return;
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
    public boolean exigeConversao(String tipoDestino, String tipoOrigem) {
        return "real".equals(tipoDestino) && "int".equals(tipoOrigem);
    }

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
    public void verificarCondicao(String tipo, String comando, int linha, int coluna) {
        if (indefinido(tipo)) {
            return;
        }
        if (!LOGICO.equals(tipo)) {
            registrarErro(linha, coluna, "a condicao do comando '" + comando
                    + "' deve ser uma expressao logica, mas resultou em '" + tipo + "'.");
        }
    }

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

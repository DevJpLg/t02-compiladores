package compilador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TABELA DE SIMBOLOS
 *
 * Estrutura de apoio do analisador semantico. Guarda todo identificador
 * declarado no programa junto com o seu tipo, a linha onde apareceu e o
 * nivel de aninhamento (0 = escopo global do programa).
 *
 * A tabela e organizada como uma PILHA DE AMBIENTES: cada bloco "{ ... }"
 * empilha um novo mapa. O primeiro bloco do programa e o escopo GLOBAL
 * (nivel 0) e os blocos internos sao escopos LOCAIS (nivel 1, 2 ...):
 *   - a busca sobe do escopo mais interno para o mais externo;
 *   - ao fechar o bloco, o mapa do topo e descartado e as variaveis
 *     daquele bloco deixam de existir.
 */
public class TabelaDeSimbolos {

    /** Registro de um identificador declarado. */
    public static class Simbolo {
        public final String nome;
        public final String tipo;
        public final int linha;
        public final int nivel;

        /**
         * Nome usado pelo GERADOR DE CODIGO para enderecar a variavel.
         *
         * Uma variavel global mantem o proprio nome; uma variavel local
         * recebe o nivel do bloco como sufixo (ex.: "aprovado_1"). Sem
         * isso, uma local que sombreia uma global de mesmo nome geraria
         * o mesmo STORE/LOAD e as duas dividiriam o mesmo endereco.
         */
        public final String rotulo;

        public Simbolo(String nome, String tipo, int linha, int nivel) {
            this.nome   = nome;
            this.tipo   = tipo;
            this.linha  = linha;
            this.nivel  = nivel;
            this.rotulo = (nivel == 0) ? nome : nome + "_" + nivel;
        }

        @Override
        public String toString() {
            String linhaTabela =
                String.format("%-12s %-8s linha %-4d nivel %d", nome, tipo, linha, nivel);
            return nome.equals(rotulo) ? linhaTabela : linhaTabela + "   -> " + rotulo;
        }
    }

    /** Pilha de ambientes: a posicao 0 e o escopo global do programa. */
    private final List<Map<String, Simbolo>> ambientes = new ArrayList<Map<String, Simbolo>>();

    /** Copia de todos os simbolos ja declarados, usada apenas para relatorio. */
    private final List<Simbolo> historico = new ArrayList<Simbolo>();

    /** Chamado quando o parser reconhece a abertura de um bloco "{". */
    public void abrirEscopo() {
        ambientes.add(new LinkedHashMap<String, Simbolo>());
    }

    /** Chamado quando o parser reconhece o fechamento de um bloco "}". */
    public void fecharEscopo() {
        if (!ambientes.isEmpty()) {
            ambientes.remove(ambientes.size() - 1);
        }
    }

    /** Nivel do escopo corrente (0 = global). */
    public int nivelAtual() {
        return ambientes.size() - 1;
    }

    /**
     * Insere um identificador no escopo corrente.
     * @return false quando o nome JA existe neste mesmo escopo (redeclaracao).
     */
    public boolean inserir(String nome, String tipo, int linha) {
        if (ambientes.isEmpty()) {
            abrirEscopo();
        }
        Map<String, Simbolo> escopo = ambientes.get(ambientes.size() - 1);
        if (escopo.containsKey(nome)) {
            return false;
        }
        Simbolo s = new Simbolo(nome, tipo, linha, nivelAtual());
        escopo.put(nome, s);
        historico.add(s);
        return true;
    }

    /**
     * Procura um identificador do escopo mais interno para o mais externo.
     * @return o simbolo encontrado ou null caso ele nao esteja visivel aqui.
     */
    public Simbolo buscar(String nome) {
        for (int i = ambientes.size() - 1; i >= 0; i--) {
            Simbolo s = ambientes.get(i).get(nome);
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    /** Todos os simbolos declarados durante a compilacao (para o relatorio final). */
    public List<Simbolo> historico() {
        return historico;
    }

    /** Impressao da tabela, util na apresentacao do trabalho. */
    public void imprimir() {
        System.out.println("  TABELA DE SIMBOLOS");
        System.out.println("  ------------------------------------------------");
        if (historico.isEmpty()) {
            System.out.println("  (nenhum identificador declarado)");
            return;
        }
        for (Simbolo s : historico) {
            System.out.println("  " + s);
        }
    }
}

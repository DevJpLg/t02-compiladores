package compilador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TabelaDeSimbolos {
    public static class Simbolo {
        public final String nome;
        public final String tipo;
        public final int linha;
        public final int nivel;
        public final int escopo;
        public final String rotulo;
        public Simbolo(String nome, String tipo, int linha, int nivel, int escopo) {
            this.nome   = nome;
            this.tipo   = tipo;
            this.linha  = linha;
            this.nivel  = nivel;
            this.escopo = escopo;
            this.rotulo = (escopo == 0) ? nome : nome + "_" + escopo;
        }
        @Override
        public String toString() {
            String linhaTabela =
                String.format("%-12s %-8s linha %-4d nivel %d", nome, tipo, linha, nivel);
            return nome.equals(rotulo) ? linhaTabela : linhaTabela + "   -> " + rotulo;
        }
    }

    private final List<Map<String, Simbolo>> ambientes = new ArrayList<Map<String, Simbolo>>();
    private final List<Integer> identificadores = new ArrayList<Integer>();
    private int contadorEscopos = 0;
    private final List<Simbolo> historico = new ArrayList<Simbolo>();
    public void abrirEscopo() {
        ambientes.add(new LinkedHashMap<String, Simbolo>());
        identificadores.add(contadorEscopos++);
    }
    public void fecharEscopo() {
        if (!ambientes.isEmpty()) {
            ambientes.remove(ambientes.size() - 1);
            identificadores.remove(identificadores.size() - 1);
        }
    }
    public int nivelAtual() {
        return ambientes.size() - 1;
    }

    public int escopoAtual() {
        return identificadores.isEmpty() ? 0 : identificadores.get(identificadores.size() - 1);
    }

    public boolean inserir(String nome, String tipo, int linha) {
        if (ambientes.isEmpty()) {
            abrirEscopo();
        }
        Map<String, Simbolo> escopo = ambientes.get(ambientes.size() - 1);
        if (escopo.containsKey(nome)) {
            return false;
        }
        Simbolo s = new Simbolo(nome, tipo, linha, nivelAtual(), escopoAtual());
        escopo.put(nome, s);
        historico.add(s);
        return true;
    }
    public Simbolo buscar(String nome) {
        for (int i = ambientes.size() - 1; i >= 0; i--) {
            Simbolo s = ambientes.get(i).get(nome);
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    public List<Simbolo> historico() {
        return historico;
    }

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

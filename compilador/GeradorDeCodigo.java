package compilador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeradorDeCodigo {
    private final List<String> programa = new ArrayList<String>();
    private List<String> buffer = null;
    private int contadorRegistradores = 0;
    private int contadorRotulos = 0;
    public String novoRegistrador() {
        return "R" + (contadorRegistradores++);
    }
    public String novoRotulo() {
        return "L" + (contadorRotulos++);
    }

    public void emitir(String mnemonico, String operandos) {
        acrescentar(String.format("        %-6s %s", mnemonico, operandos));
    }

    public void emitir(String mnemonico) {
        acrescentar("        " + mnemonico);
    }

    public void emitirComentario(String texto) {
        acrescentar("        ; " + texto);
    }

    public void emitirRotulo(String rotulo) {
        acrescentar(rotulo + ":");
    }

    public void emitirTrecho(List<String> trecho) {
        for (String linha : trecho) {
            acrescentar(linha);
        }
    }
    private void acrescentar(String linha) {
        if (buffer != null) {
            buffer.add(linha);
        } else {
            programa.add(linha);
        }
    }

    public void iniciarCaptura() {
        buffer = new ArrayList<String>();
    }

    public List<String> encerrarCaptura() {
        List<String> capturado = (buffer == null) ? new ArrayList<String>() : buffer;
        buffer = null;
        return capturado;
    }

    public List<String> programa() {
        return programa;
    }
    public void imprimir() {
        for (String linha : programa) {
            System.out.println("  " + linha);
        }
    }
    public void salvar(String caminho, String fonte) throws IOException {
        BufferedWriter escritor = new BufferedWriter(new FileWriter(caminho));
        try {
            escritor.write("; Assembly ficticio gerado pelo compilador da linguagem da disciplina");
            escritor.newLine();
            escritor.write("; Fonte de origem: " + fonte);
            escritor.newLine();
            escritor.newLine();
            for (String linha : programa) {
                escritor.write(linha);
                escritor.newLine();
            }
        } finally {
            escritor.close();
        }
    }
}

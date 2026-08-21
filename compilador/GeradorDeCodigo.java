package compilador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GERADOR DE CODIGO
 *
 * Traduz os comandos da linguagem ficticia para um Assembly ficticio de
 * registradores (R0, R1, R2 ...) e rotulos (L0, L1, L2 ...).
 *
 * Conjunto de instrucoes utilizado:
 *   LOADI  Rd, valor      carrega uma constante no registrador
 *   LOAD   Rd, var        carrega o conteudo de uma variavel
 *   STORE  var, Rs        grava o registrador na variavel
 *   ADD/SUB/MUL/DIV  Rd, Rs, Rt      operacoes aritmeticas
 *   NEG    Rd, Rs         troca o sinal (menos unario)
 *   CVTIR  Rd, Rs         converte inteiro em real (promocao de tipo)
 *   CMPEQ/CMPLT/CMPGT Rd, Rs, Rt     comparacoes (resultado 0 ou 1)
 *   JMPF   Rs, L          desvia para L se Rs for falso
 *   JMP    L              desvio incondicional
 *   READ   var            leitura de dado (comando input)
 *   WRITE  Rs             escrita de dado (comando output)
 *   HALT                  fim do programa
 *
 * O gerador tambem sabe "capturar" instrucoes em um buffer temporario.
 * Isso e necessario no comando for: o incremento aparece no texto antes
 * do corpo do laco, mas precisa ser executado depois dele.
 */
public class GeradorDeCodigo {

    private final List<String> programa = new ArrayList<String>();

    /** Buffer ativo durante uma captura (null quando nao ha captura). */
    private List<String> buffer = null;

    private int contadorRegistradores = 0;
    private int contadorRotulos = 0;

    /* ---------------- nomes novos ---------------- */

    /** Devolve um registrador ainda nao utilizado: R0, R1, R2 ... */
    public String novoRegistrador() {
        return "R" + (contadorRegistradores++);
    }

    /** Devolve um rotulo ainda nao utilizado: L0, L1, L2 ... */
    public String novoRotulo() {
        return "L" + (contadorRotulos++);
    }

    /* ---------------- emissao ---------------- */

    /** Acrescenta uma instrucao completa (mnemonico + operandos). */
    public void emitir(String mnemonico, String operandos) {
        acrescentar(String.format("        %-6s %s", mnemonico, operandos));
    }

    /** Acrescenta uma instrucao sem operandos, como HALT. */
    public void emitir(String mnemonico) {
        acrescentar("        " + mnemonico);
    }

    /** Acrescenta um comentario ao codigo gerado. */
    public void emitirComentario(String texto) {
        acrescentar("        ; " + texto);
    }

    /** Marca um rotulo de desvio na coluna zero. */
    public void emitirRotulo(String rotulo) {
        acrescentar(rotulo + ":");
    }

    /** Insere de uma vez um trecho guardado anteriormente. */
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

    /* ---------------- captura (usada pelo for) ---------------- */

    /** A partir daqui as instrucoes vao para um buffer, e nao para o programa. */
    public void iniciarCaptura() {
        buffer = new ArrayList<String>();
    }

    /** Encerra a captura e devolve as instrucoes guardadas. */
    public List<String> encerrarCaptura() {
        List<String> capturado = (buffer == null) ? new ArrayList<String>() : buffer;
        buffer = null;
        return capturado;
    }

    /* ---------------- saida ---------------- */

    public List<String> programa() {
        return programa;
    }

    public void imprimir() {
        for (String linha : programa) {
            System.out.println("  " + linha);
        }
    }

    /** Grava o Assembly ficticio em disco (arquivo .asm). */
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

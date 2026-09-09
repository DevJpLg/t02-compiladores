package compilador;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class Compilador {
    private static final String[] FONTES_DE_TESTE = {
        "correto_1.txt",
        "correto_2.txt",
        "correto_3.txt",
        "correto_4.txt",
        "correto_5.txt",
        "erro_semantico_1.txt",
        "erro_semantico_2.txt",
        "erro_semantico_3.txt"
    };

    public static void main(String[] args) {
        String[] alvos = (args.length > 0) ? args : FONTES_DE_TESTE;
        System.out.println("=== Linguagem LangZ - Compilador ===");
        System.out.println("Fases: lexica -> sintatica -> semantica -> geracao de codigo");
        int compilados = 0;
        int rejeitados = 0;
        for (String alvo : alvos) {
            File fonte = localizar(alvo);
            System.out.println();
            System.out.println("================================================================");
            if (fonte == null) {
                System.out.println("ARQUIVO NAO ENCONTRADO: " + alvo);
                rejeitados++;
                continue;
            }
            System.out.println("FONTE: " + fonte.getName());
            System.out.println("================================================================");
            if (compilar(fonte)) {
                compilados++;
            } else {
                rejeitados++;
            }
        }
        System.out.println();
        System.out.println("================================================================");
        System.out.println("RESUMO: " + compilados + " fonte(s) compilado(s), "
                           + rejeitados + " fonte(s) com erro.");
        System.out.println("================================================================");
    }
    private static File localizar(String nome) {
        File direto = new File(nome);
        if (direto.isFile()) {
            return direto;
        }
        File naPasta = new File("compilador" + File.separator + nome);
        if (naPasta.isFile()) {
            return naPasta;
        }
        return null;
    }
    private static boolean compilar(File fonte) {
        parser sintatico;
        Lexer lexico;
        try {
            Reader leitor = new InputStreamReader(new FileInputStream(fonte), "UTF-8");
            lexico = new Lexer(leitor);
            sintatico = new parser(lexico);
            sintatico.parse();
            leitor.close();
        } catch (Exception e) {
            System.out.println("Compilacao interrompida: " + e.getMessage());
            return false;
        }
        AnalisadorSemantico semantico = sintatico.semantico;
        int errosLexicos = lexico.errosLexicos();
        System.out.println();
        semantico.tabela().imprimir();
        if (!semantico.avisos().isEmpty()) {
            System.out.println();
            for (String aviso : semantico.avisos()) {
                System.out.println("  " + aviso);
            }
        }
        List<String> erros = semantico.erros();
        if (errosLexicos > 0 || semantico.possuiErros() || sintatico.erroSintatico) {
            System.out.println();
            if (errosLexicos > 0) {
                System.out.println("  RESULTADO: " + errosLexicos
                                   + " erro(s) lexico(s) encontrado(s) - mensagens acima.");
            }
            if (semantico.possuiErros() || errosLexicos == 0) {
                System.out.println("  RESULTADO: " + erros.size() + " erro(s) semantico(s) encontrado(s).");
                System.out.println("  ------------------------------------------------");
                for (String erro : erros) {
                    System.out.println("  " + erro);
                }
            }
            System.out.println();
            System.out.println("  Geracao de codigo cancelada para este fonte.");
            return false;
        }
        System.out.println();
        System.out.println("  RESULTADO: nenhum erro lexico, sintatico ou semantico.");
        System.out.println();
        System.out.println("  ASSEMBLY FICTICIO GERADO");
        System.out.println("  ------------------------------------------------");
        sintatico.gerador.imprimir();
        String destino = fonte.getPath().replaceAll("\\.txt$", "") + ".asm";
        try {
            sintatico.gerador.salvar(destino, fonte.getName());
            System.out.println();
            System.out.println("  Codigo salvo em: " + destino);
        } catch (Exception e) {
            System.out.println("  Falha ao gravar o arquivo .asm: " + e.getMessage());
            return false;
        }
        return true;
    }
}

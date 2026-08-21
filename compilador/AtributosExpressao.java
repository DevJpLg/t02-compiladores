package compilador;

/**
 * ATRIBUTOS DE UMA EXPRESSAO
 *
 * Objeto que sobe pela arvore sintatica durante a reducao das producoes
 * de expressao. Ele carrega, ao mesmo tempo:
 *   - o TIPO da expressao, consumido pelo analisador semantico;
 *   - o REGISTRADOR onde o gerador de codigo deixou o resultado.
 *
 * E a peca que liga a analise semantica a geracao de codigo.
 */
public class AtributosExpressao {

    /** "int", "real", "chr", "str", "logico" ou "indefinido". */
    public final String tipo;

    /** Registrador do Assembly ficticio que guarda o valor (ex.: "R3"). */
    public final String registrador;

    public AtributosExpressao(String tipo, String registrador) {
        this.tipo = tipo;
        this.registrador = registrador;
    }

    @Override
    public String toString() {
        return tipo + "@" + registrador;
    }
}

package compilador;

public class AtributosExpressao {
    public final String tipo;
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

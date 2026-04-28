package Exception;

public class LimitePrestamosExcedidoException extends BibliotecaException {
    public LimitePrestamosExcedidoException(String nombreSocio, int limite) {
        super("El socio " + nombreSocio + " ha excedido su límite de " + limite + " libros.");
    }
}
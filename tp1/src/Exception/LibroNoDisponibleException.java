package Exception;

public class LibroNoDisponibleException extends BibliotecaException {
    public LibroNoDisponibleException(String isbn) {
        super("El libro con ISBN " + isbn + " no está disponible para préstamo.");
    }
}
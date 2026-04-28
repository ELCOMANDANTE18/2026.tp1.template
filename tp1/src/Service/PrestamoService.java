package Service;

import Model.Socio;
import Model.Libro;

public interface PrestamoService {
    void realizarPrestamo(String isbn, String dni) throws Exception; // Luego usaremos excepciones personalizadas
    void devolverLibro(String isbn, String dni);
}


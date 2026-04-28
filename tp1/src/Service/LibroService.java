package Service;

import Model.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroService {
    void registrarLibro(Libro libro);
    Optional<Libro> buscarPorIsbn(String isbn);
    List<Libro> buscarPorCriterio(String criterio); // Para título, autor o categoría
}


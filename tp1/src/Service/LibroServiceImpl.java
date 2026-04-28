package Service;

import Model.Libro;
import Repository.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LibroServiceImpl implements LibroService {
    private final Repository<Libro, String> libroRepo;

    // Inyección por constructor (Dependency Inversion)
    public LibroServiceImpl(Repository<Libro, String> libroRepo) {
        this.libroRepo = libroRepo;
    }

    @Override
    public void registrarLibro(Libro libro) {
        libroRepo.guardar(libro);
    }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libroRepo.buscarPorId(isbn);
    }

    @Override
    public List<Libro> buscarPorCriterio(String criterio) {
        String query = criterio.toLowerCase();
        return libroRepo.buscarTodos().stream()
                .filter(l -> l.titulo().toLowerCase().contains(query) ||
                        l.autor().toLowerCase().contains(query) ||
                        l.categoria().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }
}
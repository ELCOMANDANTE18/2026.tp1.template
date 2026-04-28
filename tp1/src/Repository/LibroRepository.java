package Repository; // Asegúrate de que coincida con la carpeta

import Repository.Repository;
import model.Libro; // Verifica que el paquete de Libro sea 'model'
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroRepository implements Repository<Libro, String> {
    private final List<Libro> libros = new ArrayList<>();

    @Override
    public void guardar(Libro libro) {
        libros.add(libro);
    }

    @Override
    public Optional<Libro> buscarPorId(String isbn) { // Aquí iba el error del tipo 'T'
        return libros.stream()
                .filter(l -> l.isbn().equals(isbn))
                .findFirst();
    }

    @Override
    public List<Libro> buscarTodos() {
        return new ArrayList<>(libros);
    }
}
package Service;

import Model.*;
import Repository.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PrestamoServiceImpl implements PrestamoService {
    private final Repository<Libro, String> libroRepo;
    private final Repository<Socio, String> socioRepo;

    // Simulación de base de datos de préstamos activos: DNI -> Lista de ISBNs
    private final Map<String, List<String>> prestamosActivos = new HashMap<>();

    public PrestamoServiceImpl(Repository<Libro, String> libroRepo, Repository<Socio, String> socioRepo) {
        this.libroRepo = libroRepo;
        this.socioRepo = socioRepo;
    }

    @Override
    public void realizarPrestamo(String isbn, String dni) throws Exception {
        // 1. Buscar socio y libro usando Optional
        Socio socio = socioRepo.buscarPorId(dni)
                .orElseThrow(() -> new Exception("Socio no encontrado"));

        Libro libro = libroRepo.buscarPorId(isbn)
                .orElseThrow(() -> new Exception("Libro no encontrado"));

        // 2. Validar límite según TipoSocio (Lógica del Issue #06)
        List<String> librosDelSocio = prestamosActivos.getOrDefault(dni, new ArrayList<>());

        if (librosDelSocio.size() >= socio.tipo().getLimitePrestamos()) {
            throw new Exception("El socio " + socio.nombre() + " ha superado su límite de " +
                    socio.tipo().getLimitePrestamos() + " libros.");
        }

        // 3. Registrar préstamo
        librosDelSocio.add(isbn);
        prestamosActivos.put(dni, librosDelSocio);
        System.out.println("✅ Préstamo registrado: " + libro.titulo() + " para " + socio.nombre());
    }

    @Override
    public void devolverLibro(String isbn, String dni) {
        if (prestamosActivos.containsKey(dni)) {
            prestamosActivos.get(dni).remove(isbn);
            System.out.println("✅ Devolución exitosa del ISBN: " + isbn);
        }
    }
}
package Service;

import Model.*;
import Repository.Repository;
import Exception.LibroNoDisponibleException;
import Exception.LimitePrestamosExcedidoException;
import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PrestamoServiceImpl implements PrestamoService {
    private final Repository<Libro, String> libroRepo;
    private final Repository<Socio, String> socioRepo;

    // Almacenamos quién tiene qué libro (DNI -> Lista de ISBNs)
    private final Map<String, List<String>> prestamosActivos = new HashMap<>();
    // Almacenamos libros que ya están prestados para que no se dupliquen
    private final Set<String> librosPrestados = new HashSet<>();


    public PrestamoServiceImpl(Repository<Libro, String> libroRepo, Repository<Socio, String> socioRepo) {
        this.libroRepo = libroRepo;
        this.socioRepo = socioRepo;
    }

    private final Map<String, LocalDate> fechasVencimiento = new HashMap<>();

    @Override
    public void realizarPrestamo(String isbn, String dni) throws Exception {
        Socio socio = socioRepo.buscarPorId(dni)
                .orElseThrow(() -> new Exception("Socio no encontrado"));

        Libro libro = libroRepo.buscarPorId(isbn)
                .orElseThrow(() -> new Exception("Libro no encontrado"));

        // 1. Verificar si el libro ya está prestado (Gestión de disponibilidad)
        if (librosPrestados.contains(isbn)) {
            throw new LibroNoDisponibleException(isbn);
        }

        // 2. Validar límite del socio
        List<String> susLibros = prestamosActivos.getOrDefault(dni, new ArrayList<>());
        if (susLibros.size() >= socio.tipo().getLimitePrestamos()) {
            throw new LimitePrestamosExcedidoException(socio.nombre(), socio.tipo().getLimitePrestamos());
        }

        fechasVencimiento.put(isbn, LocalDate.now().plusDays(7));

        // 3. TRANSACCIÓN: Registrar en ambos mapas
        susLibros.add(isbn);
        prestamosActivos.put(dni, susLibros);
        librosPrestados.add(isbn); // Marcamos el libro como no disponible

        System.out.println("✅ Transacción exitosa: " + socio.nombre() + " retiró " + libro.titulo());
    }

    @Override
    public void devolverLibro(String isbn, String dni) {
        if (prestamosActivos.containsKey(dni) && prestamosActivos.get(dni).contains(isbn)) {
            LocalDate fechaVencimiento = fechasVencimiento.get(isbn);
            LocalDate fechaHoy = LocalDate.now();

            // Cálculo de días de retraso usando ChronoUnit
            long diasRetraso = ChronoUnit.DAYS.between(fechaVencimiento, fechaHoy);

            if (diasRetraso > 0) {
                System.out.println("⚠️ ATENCIÓN: Devolución con " + diasRetraso + " días de retraso.");
            } else {
                System.out.println("✅ Devolución a tiempo. ¡Gracias!");
            }

            // Limpieza de registros
            prestamosActivos.get(dni).remove(isbn);
            librosPrestados.remove(isbn);
            fechasVencimiento.remove(isbn);    }
    }
}
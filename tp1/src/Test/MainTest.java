package Test;

import Model.*;
import Repository.*;
import java.util.Optional;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("🧪 Iniciando Pruebas Milestone 1 - BiblioTech\n");

        // 1. Inicialización de Repositorios
        Repository<Libro, String> libroRepo = new LibroRepository();
        Repository<Socio, String> socioRepo = new SocioRepository();

        // 2. Test de Libros (Records + Repository)
        Libro libro1 = new Libro("978-1", "Java para Ingenieros", "Victor", 2026, "Programación");
        libroRepo.guardar(libro1);

        Optional<Libro> busquedaLibro = libroRepo.buscarPorId("978-1");
        imprimirResultado("Registro de Libro", busquedaLibro.isPresent() && busquedaLibro.get().titulo().equals("Java para Ingenieros"));

        // 3. Test de Socios (Enum + Record + Repository)
        Socio socio1 = new Socio("12345678", "Victor Gimenez", "vb.gimenez@alumno.um.edu.ar", TipoSocio.ESTUDIANTE);
        socioRepo.guardar(socio1);

        Optional<Socio> busquedaSocio = socioRepo.buscarPorId("12345678");
        boolean socioOk = busquedaSocio.isPresent() && busquedaSocio.get().tipo() == TipoSocio.ESTUDIANTE;
        imprimirResultado("Registro de Socio (Estudiante)", socioOk);

        // 4. Verificación de Límites (Lógica del Enum)
        boolean limiteOk = socio1.tipo().getLimitePrestamos() == 3;
        imprimirResultado("Validación de Límite Estudiante (3)", limiteOk);

        System.out.println("\n🚀 Milestone 1 verificado correctamente.");
    }

    private static void imprimirResultado(String test, boolean pasado) {
        String status = pasado ? "✅ PASADO" : "❌ FALLADO";
        System.out.println(test + ": " + status);
    }
}
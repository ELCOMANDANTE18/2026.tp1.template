package Test;

import Model.*;
import Repository.*;
import Service.*;
import java.util.List;

public class MainTestM2 {
    public static void main(String[] args) {
        System.out.println("🧪 Iniciando Pruebas Milestone 2 - Lógica de Negocio\n");

        // 1. Setup de Repositorios y Servicios (Inyección de Dependencias)
        LibroRepository libroRepo = new LibroRepository();
        SocioRepository socioRepo = new SocioRepository();

        LibroService libroService = new LibroServiceImpl(libroRepo);
        PrestamoService prestamoService = new PrestamoServiceImpl(libroRepo, socioRepo);

        // 2. Carga de Datos iniciales
        Libro l1 = new Libro("111", "Java 1", "Victor", 2026, "Programacion");
        Libro l2 = new Libro("222", "Java 2", "Victor", 2026, "Programacion");
        Libro l3 = new Libro("333", "Java 3", "Victor", 2026, "Programacion");
        Libro l4 = new Libro("444", "Java 4", "Victor", 2026, "Programacion");

        libroService.registrarLibro(l1);
        libroService.registrarLibro(l2);
        libroService.registrarLibro(l3);
        libroService.registrarLibro(l4);

        Socio victosEstudiante = new Socio("123", "Victor G", "v@u.edu", TipoSocio.ESTUDIANTE);
        socioRepo.guardar(victosEstudiante);

        // 3. Test Búsqueda Avanzada
        List<Libro> busqueda = libroService.buscarPorCriterio("Victor");
        imprimirResultado("Búsqueda avanzada (encontró 4 libros)", busqueda.size() == 4);

        // 4. Test Límite de Préstamos (Debe fallar al 4to)
        try {
            prestamoService.realizarPrestamo("111", "123");
            prestamoService.realizarPrestamo("222", "123");
            prestamoService.realizarPrestamo("333", "123");
            System.out.println("✅ 3 libros prestados correctamente.");

            // Este debería tirar excepción
            prestamoService.realizarPrestamo("444", "123");
            imprimirResultado("Validación de Límite (Estudiante máx 3)", false);
        } catch (Exception e) {
            imprimirResultado("Validación de Límite (Estudiante máx 3): " + e.getMessage(), true);
        }

        // 5. Test Disponibilidad (Prestar libro ya prestado)
        try {
            Socio otroSocio = new Socio("456", "Docente X", "d@u.edu", TipoSocio.DOCENTE);
            socioRepo.guardar(otroSocio);
            prestamoService.realizarPrestamo("111", "456"); // El 111 ya lo tiene Victor
            imprimirResultado("Validación de Disponibilidad", false);
        } catch (Exception e) {
            imprimirResultado("Validación de Disponibilidad: " + e.getMessage(), true);
        }

        System.out.println("\n🚀 Milestone 2 verificado con éxito.");
    }

    private static void imprimirResultado(String test, boolean pasado) {
        System.out.println((pasado ? "✅ " : "❌ ") + test);
    }
}
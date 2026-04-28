package main;

import Model.*;
import Repository.*;
import Service.*;
import Exception.BibliotecaException;
import java.util.Scanner;
import java.util.List; // Importante para manejar la lista de resultados

public class Main {
    public static void main(String[] args) {
        // 1. Inicialización de la infraestructura (Inyección de Dependencias)
        LibroRepository libroRepo = new LibroRepository();
        SocioRepository socioRepo = new SocioRepository();

        LibroService libroService = new LibroServiceImpl(libroRepo);
        PrestamoService prestamoService = new PrestamoServiceImpl(libroRepo, socioRepo);

        // 2. Carga inicial de datos para la demo
        precargarDatos(libroService, socioRepo);

        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        System.out.println("📚 BIENVENIDO A BIBLIOTECH - SISTEMA DE GESTIÓN");

        while (!salir) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Buscar Libros");
            System.out.println("2. Realizar Préstamo");
            System.out.println("3. Devolver Libro");
            System.out.println("4. Ver Catálogo Completo");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            try {
                switch (opcion) {
                    case "1" -> {
                        System.out.println("\n🔍 --- BÚSQUEDA DE LIBROS ---");
                        System.out.print("Ingrese título, autor, codigo o categoría: ");
                        String criterio = scanner.nextLine();
                        List<Libro> resultados = libroService.buscarPorCriterio(criterio);

                        if (resultados.isEmpty()) {
                            System.out.println("❌ No se encontraron libros con: " + criterio);
                        } else {
                            System.out.println("✅ Libros encontrados:");
                            resultados.forEach(l -> System.out.println("    > " + l));
                        }
                    }
                    case "2" -> {
                        System.out.println("\n📖 --- REGISTRAR PRÉSTAMO ---");
                        System.out.print("DNI del Socio: ");
                        String dni = scanner.nextLine();
                        System.out.print("ISBN del Libro: ");
                        String isbn = scanner.nextLine();
                        prestamoService.realizarPrestamo(isbn, dni);
                    }
                    case "3" -> {
                        System.out.println("\n🔄 --- DEVOLUCIÓN ---");
                        System.out.print("DNI del Socio: ");
                        String dni = scanner.nextLine();
                        System.out.print("ISBN del Libro: ");
                        String isbn = scanner.nextLine();
                        prestamoService.devolverLibro(isbn, dni);
                    }
                    case "4" -> {
                        System.out.println("\n📚 --- CATÁLOGO COMPLETO ---");
                        // Usamos el service para listar todo enviando un criterio vacío
                        libroService.buscarPorCriterio("").forEach(l -> System.out.println("    • " + l));
                    }
                    case "5" -> salir = true;
                    default -> System.out.println("❌ Opción no válida, intente de nuevo.");
                }
            } catch (BibliotecaException e) {
                System.out.println("\n⚠️  ERROR DE NEGOCIO: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n🆘 ERROR INESPERADO: " + e.getMessage());
            }
        }
        System.out.println("👋 Gracias por usar BiblioTech. ¡Hasta luego!");
    }

    private static void precargarDatos(LibroService ls, SocioRepository sr) {
        ls.registrarLibro(new Libro("101", "Java Moderno", "Victor", 2026, "Programacion"));
        ls.registrarLibro(new Libro("102", "Clean Code", "Robert Martin", 2008, "Software"));
        sr.guardar(new Socio("123", "Victor Gimenez", "v@alumno.um.edu.ar", TipoSocio.ESTUDIANTE));
        sr.guardar(new Socio("456", "Profe Java", "p@um.edu.ar", TipoSocio.DOCENTE));
    }
}
package Model;

public record Ebook(String isbn,
                    String titulo,
                    String autor,
                    String categoria,
                    String formato, // ej: PDF, EPUB
                    double tamañoArchivo) implements Recurso {
}

package model;

public record LIbro(String isbn,
                    String titulo,
                    String autor,
                    int anio,
                    String categoria) implements Recurso{
    
}

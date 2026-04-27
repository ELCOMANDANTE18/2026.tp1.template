package model;

public record Socio(
        String dni,
        String nombre,
        String email,
        TipoSocio tipo
) {
    // El record ya genera los métodos dni(), nombre(), etc. automáticamente
}
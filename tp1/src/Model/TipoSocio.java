package Model;

public enum TipoSocio {
    ESTUDIANTE(3),
    DOCENTE(5);

    private final int limitePrestamos;

    TipoSocio(int limitePrestamos) {
        this.limitePrestamos = limitePrestamos;
    }

    public int getLimitePrestamos() {
        return limitePrestamos;
    }
}
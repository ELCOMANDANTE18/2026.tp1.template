package Repository;

import Model.Socio;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SocioRepository implements Repository<Socio, String> {
    // Usamos un Map para que la búsqueda por DNI sea más eficiente
    private final Map<String, Socio> socios = new HashMap<>();

    @Override
    public void guardar(Socio socio) {
        socios.put(socio.dni(), socio);
    }

    @Override
    public Optional<Socio> buscarPorId(String dni) {
        return Optional.ofNullable(socios.get(dni));
    }

    @Override
    public List<Socio> buscarTodos() {
        return new ArrayList<>(socios.values());
    }
}
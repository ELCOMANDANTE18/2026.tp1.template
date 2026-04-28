#  Documentación de BiblioTech

**Alumno:** Victor Benjamin Gimenez | **Legajo:** 61174
**Materia:** Programación 2 — Ingeniería en Informática
**Fecha de entrega:** 28 de Abril de 2026

---

## 1. Introducción

**BiblioTech** es un sistema de gestión bibliotecaria desarrollado en Java como trabajo práctico universitario. Su objetivo es digitalizar y automatizar los procesos clave de una biblioteca: el registro de recursos (libros físicos y e-books), la administración de socios (estudiantes y docentes), y el ciclo completo de préstamo y devolución.

El sistema fue diseñado priorizando tres pilares:

- **Mantenibilidad**: arquitectura en capas con responsabilidades claramente separadas.
- **Extensibilidad**: uso de interfaces y polimorfismo que permiten agregar nuevos tipos de recursos o repositorios sin modificar la lógica existente.
- **Robustez**: gestión de errores mediante una jerarquía de excepciones de negocio propia.

---

## 2. Arquitectura del Sistema

El proyecto sigue una **arquitectura en capas** (Layered Architecture), donde cada capa tiene una única responsabilidad y solo depende de la capa inmediatamente inferior a través de abstracciones (interfaces).

```
tp1/src/
├── Model/              # Capa de Dominio: entidades del negocio
│   ├── Recurso.java         (Interfaz base para recursos)
│   ├── Libro.java           (Record: libro físico)
│   ├── Ebook.java           (Record: libro digital)
│   ├── Socio.java           (Record: usuario de la biblioteca)
│   └── TipoSocio.java       (Enum: ESTUDIANTE / DOCENTE con límites)
│
├── Repository/         # Capa de Persistencia: acceso a datos (in-memory)
│   ├── Repository.java      (Interfaz genérica Repository<T, ID>)
│   ├── LibroRepository.java (Implementación para Libro, indexada por ISBN)
│   └── SocioRepository.java (Implementación para Socio, indexada por DNI)
│
├── Service/            # Capa de Negocio: lógica de aplicación
│   ├── LibroService.java        (Interfaz de operaciones sobre libros)
│   ├── LibroServiceImpl.java    (Implementación: registro y búsqueda)
│   ├── PrestamoService.java     (Interfaz de préstamos y devoluciones)
│   └── PrestamoServiceImpl.java (Implementación: transacciones, fechas, límites)
│
├── Exception/          # Capa de Errores: jerarquía de excepciones de negocio
│   ├── BibliotecaException.java           (Excepción base checked)
│   ├── LibroNoDisponibleException.java    (Libro ya prestado)
│   └── LimitePrestamosExcedidoException.java (Socio alcanzó su cupo)
│
└── main/
    └── Main.java       # Punto de entrada: CLI interactiva + orquestación de DI
```

---

## 3. Diagrama de Entidades e Interacciones

```
                    ┌─────────────┐
                    │   Main.java │  ← Orquestador (CLI + DI)
                    └──────┬──────┘
                           │ instancia y conecta
              ┌────────────┴────────────┐
              ▼                         ▼
   ┌─────────────────────┐   ┌─────────────────────────┐
   │   LibroServiceImpl  │   │   PrestamoServiceImpl   │
   │  implements         │   │  implements             │
   │  LibroService       │   │  PrestamoService        │
   └──────────┬──────────┘   └────────┬────────┬───────┘
              │ usa                   │ usa    │ usa
              ▼                       ▼        ▼
   ┌──────────────────┐  ┌──────────────┐  ┌───────────────┐
   │ LibroRepository  │  │LibroRepository│  │SocioRepository│
   │ Repository<      │  │(misma instancia│  │Repository<    │
   │  Libro, String>  │  │ compartida)   │  │ Socio, String>│
   └────────┬─────────┘  └──────────────┘  └───────┬───────┘
            │ almacena                               │ almacena
            ▼                                       ▼
   ┌─────────────────┐                   ┌────────────────┐
   │  Libro (record) │                   │  Socio (record)│
   │  Ebook (record) │                   │  TipoSocio(enum│
   │  implements     │                   └────────────────┘
   │  Recurso        │
   └─────────────────┘

   Excepciones lanzadas por PrestamoServiceImpl:
   BibliotecaException
       ├── LibroNoDisponibleException
       └── LimitePrestamosExcedidoException
```

**Flujo de un préstamo:**

```
Usuario ingresa ISBN + DNI (CLI)
        │
        ▼
PrestamoServiceImpl.realizarPrestamo()
    ├── socioRepo.buscarPorId(dni)      → Optional<Socio>
    ├── libroRepo.buscarPorId(isbn)     → Optional<Libro>
    ├── ¿libro en librosPrestados?      → lanza LibroNoDisponibleException
    ├── ¿sobrePasó límite del socio?    → lanza LimitePrestamosExcedidoException
    └── registra en prestamosActivos + librosPrestados + fechasVencimiento
```

---

## 4. Decisiones de Diseño

### 4.1 Uso de `record` para las entidades del dominio

Las entidades `Libro`, `Ebook` y `Socio` se implementaron como **Records de Java** (introducidos en Java 16). Esta decisión se tomó porque:

- Son **datos inmutables**: una vez creado un libro, su ISBN y título no cambian. Los Records garantizan inmutabilidad por defecto.
- **Eliminan boilerplate**: el compilador genera automáticamente constructor, `getters`, `equals()`, `hashCode()` y `toString()`, reduciendo el código repetitivo.
- **Semántica clara**: al ver `record Libro(...)`, cualquier lector entiende inmediatamente que es un contenedor de datos sin estado mutable.

```java
// Un record reemplaza ~40 líneas de clase clásica con validación manual
public record Libro(String isbn, String titulo, String autor, int anio, String categoria)
    implements Recurso {}
```

### 4.2 Interfaz `Recurso` y polimorfismo

Se definió la interfaz `Recurso` para unificar `Libro` y `Ebook` bajo un tipo común. Esto permite que, en el futuro, el sistema pueda operar sobre cualquier tipo de recurso (`Revista`, `Audiobook`) sin modificar la lógica de los servicios: solo se agrega un nuevo `record` que implemente `Recurso`.

### 4.3 Repositorio Genérico `Repository<T, ID>`

En lugar de crear una interfaz de repositorio por cada entidad, se diseñó una **interfaz genérica única**:

```java
public interface Repository<T, ID> {
    void guardar(T entidad);
    Optional<T> buscarPorId(ID id);
    List<T> buscarTodos();
}
```

Esto aplica el principio **DRY** (Don't Repeat Yourself) y permite que los servicios dependan de la abstracción genérica en lugar de implementaciones concretas.

### 4.4 `Optional` en lugar de `null`

Todos los métodos de búsqueda retornan `Optional<T>` en lugar de `null`. Esto obliga al caller a manejar explícitamente el caso de ausencia de datos, eliminando la posibilidad de `NullPointerException` silenciosos.

```java
// Sin Optional: riesgo de NPE si el libro no existe
Libro libro = libroRepo.buscarPorId(isbn);  // podría ser null

// Con Optional: el compilador fuerza el manejo del caso vacío
Libro libro = libroRepo.buscarPorId(isbn)
    .orElseThrow(() -> new Exception("Libro no encontrado"));
```

### 4.5 `HashMap` en `SocioRepository` vs `ArrayList` en `LibroRepository`

- `SocioRepository` usa un `HashMap<String, Socio>` porque las búsquedas de socios siempre son por DNI (clave única), logrando O(1) en lugar de O(n).
- `LibroRepository` usa un `ArrayList<Libro>` porque se necesita tanto búsqueda por ISBN como iteración para filtrado por múltiples criterios (título, autor, categoría).

---



La instanciación y el "cableado" de dependencias ocurre exclusivamente en `Main.java`:

```java
LibroRepository libroRepo = new LibroRepository();       // concreto
SocioRepository socioRepo = new SocioRepository();

LibroService libroService = new LibroServiceImpl(libroRepo);        // inyectado
PrestamoService prestamoService = new PrestamoServiceImpl(libroRepo, socioRepo);
```

---

## 5. Manejo de Errores: Jerarquía de Excepciones

Se diseñó una jerarquía de **excepciones checked** propia para representar los errores del dominio de negocio con semántica precisa:

```
Exception  (Java estándar)
    └── BibliotecaException              ← Base de todos los errores del sistema
            ├── LibroNoDisponibleException       ← Libro ya está en préstamo
            └── LimitePrestamosExcedidoException ← Socio alcanzó su cupo máximo
```

**Por qué `checked` (extienden `Exception` y no `RuntimeException`):**
Al ser `checked`, el compilador obliga a que cualquier método que las lance las declare en su firma (`throws`) y que el caller las capture. Esto hace que los errores de negocio sean explícitos y no puedan ignorarse silenciosamente.

**Cómo se capturan en el CLI:**

```java
try {
    prestamoService.realizarPrestamo(isbn, dni);
} catch (BibliotecaException e) {
    System.out.println("ERROR DE NEGOCIO: " + e.getMessage());
} catch (Exception e) {
    System.out.println("ERROR INESPERADO: " + e.getMessage());
}
```

Se usa el polimorfismo de la jerarquía: capturar `BibliotecaException` atrapa tanto `LibroNoDisponibleException` como `LimitePrestamosExcedidoException`, evitando duplicar bloques `catch`.

---

## 6. Instrucciones de Ejecución

### Requisitos previos
- **JDK 17** o superior (para soporte de Records).
- Terminal (Linux/macOS) o Command Prompt/PowerShell (Windows).

### Compilación desde la terminal

Desde la raíz del proyecto (carpeta `tp1/`):

```bash
# 1. Moverse al directorio fuente
cd tp1/src

# 2. Compilar todos los paquetes indicando el directorio de salida
javac -d ../out $(find . -name "*.java")
```

### Ejecución

```bash
# 3. Ejecutar desde el directorio de salida
cd ../out
java main.Main
```

### Flujo de demostración

El sistema precarga automáticamente datos de prueba al iniciar:

| Dato | Valor |
|------|-------|
| Libro 1 | ISBN: `101`, "Java Moderno" — Programacion |
| Libro 2 | ISBN: `102`, "Clean Code" — Software |
| Socio 1 | DNI: `123`, Victor Gimenez (ESTUDIANTE, límite: 3) |
| Socio 2 | DNI: `456`, Profe Java (DOCENTE, límite: 5) |

**Ejemplo de sesión:**
```
📚 BIENVENIDO A BIBLIOTECH - SISTEMA DE GESTIÓN

--- MENÚ PRINCIPAL ---
1. Buscar Libros
2. Realizar Préstamo
3. Devolver Libro
4. Ver Catálogo Completo
5. Salir
Seleccione una opción: 2

📖 --- REGISTRAR PRÉSTAMO ---
DNI del Socio: 123
ISBN del Libro: 101
✅ Transacción exitosa: Victor Gimenez retiró Java Moderno
```

---

## 7. Flujo de Trabajo (Metodología de Desarrollo)

El proyecto siguió un flujo profesional basado en **Issues y ramas de Git**:

| Milestone | Issues resueltos | Rama |
|-----------|-----------------|------|
| **M1: Arquitectura Base** | ISSUE 01–04: Modelos, Repositorios | `feature/modelos`, `feature/repositorios` |
| **M2: Lógica de Negocio** | ISSUE 05–08: Servicios, Búsqueda, Transacciones | `feature/servicio-busqueda`, `feature/logica-prestamos` |
| **M3: Excepciones + CLI** | ISSUE 09–11: Excepciones, Main, DI | `feature/excepciones-personalizadas`, `feature/cli-interface` |



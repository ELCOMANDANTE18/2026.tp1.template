Debido a los problemas que tiene github puse los issues en un archivo markdown


# Milestone 1: Arquitectura Base y Modelos

### ISSUE 01: Definir Jerarquía de Recurso:

    Crear la interfaz Recurso y los record para Libro y Ebook. ESTADO []

### ISSUE 02: Modelar Socio y Categorías
    
    Implementar record Socio y enums para tipos de socio (Estudiante/Docente). ESTADO []

### ISSUE 03: Interfaz Repository Genérica

    Crear Repository<T, ID> con métodos CRUD básicos usando Optional. ESTADO []

### ISSUE 04: Implementación InMemoryRepository 
    
    Crear clases concretas que gestionen los datos en colecciones (List o Map). ESTADO [] 


# Milestone 2: Lógica de Negocio (Servicios)

### ISSUE 05: Servicio de Búsqueda Avanzada

    Lógica para filtrar libros por ISBN, autor o categoría sin devolver null. ESTADO []

### ISSUE 06: Validación de Límites de Préstamo

    Lógica para verificar si un Estudiante (máx 3) o Docente (máx 5) puede retirar libros. ESTADO []

### ISSUE 07: Gestión de Transacciones

    Registrar el préstamo y actualizar la disponibilidad del recurso. ESTADO []

### ISSUE 08: Lógica de Devoluciones

    Calcular días de retraso y liberar el ejemplar. ESTADO []

# Milestone 3: Excepciones y Punto de Entrada

### ISSUE 09: Jerarquía de BibliotecaException

    Crear excepciones personalizadas como LibroNoDisponibleException. ESTADO []

### ISSUE 10: Orquestador Main (CLI)

    Implementar el menú interactivo para el usuario final por consola. ESTADO []

### ISSUE 11: Inyección de Dependencias

    Asegurar que los servicios reciban sus repositorios por constructor. ESTADO []


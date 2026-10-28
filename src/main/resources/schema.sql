-- Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

-- Categorías
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE
);

-- Tareas
CREATE TABLE IF NOT EXISTS tareas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion VARCHAR(1000),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    prioridad VARCHAR(10) NOT NULL DEFAULT 'MEDIA',
    fecha_limite DATE,
    categoria_id INT,
    responsable_id INT,
    creador_id INT NOT NULL,
    CONSTRAINT chk_estado CHECK (estado IN ('PENDIENTE', 'EN_PROGRESO', 'TERMINADA')),
    CONSTRAINT chk_prioridad CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA')),
    CONSTRAINT fk_tarea_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    CONSTRAINT fk_tarea_responsable FOREIGN KEY (responsable_id) REFERENCES usuarios(id),
    CONSTRAINT fk_tarea_creador FOREIGN KEY (creador_id) REFERENCES usuarios(id)
);

-- Categorías iniciales
INSERT INTO categorias (nombre) SELECT 'Desarrollo' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Desarrollo');
INSERT INTO categorias (nombre) SELECT 'Pruebas' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Pruebas');
INSERT INTO categorias (nombre) SELECT 'Documentación' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Documentación');
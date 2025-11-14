CREATE DATABASE IF NOT EXISTS ROY;
USE ROY;
show databases;

-- ============================================
-- TABLA: USUARIO
-- ============================================
CREATE TABLE USUARIO (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nombre_us VARCHAR(50) NOT NULL,
    apellido_us VARCHAR(50) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    domicilio VARCHAR(150) NOT NULL,
    fecha_registro DATE NOT NULL,
    PRIMARY KEY (id_usuario)
);

-- ============================================
-- TABLA: RESENA
-- ============================================
CREATE TABLE RESENA (
    id_resena INT NOT NULL AUTO_INCREMENT,
    id_us_autor INT NOT NULL,
    id_us_receptor INT NOT NULL,
    calificacion INT NOT NULL,
    comentario TEXT NULL,
    fecha_resena DATE NOT NULL,
    PRIMARY KEY (id_resena),
    FOREIGN KEY (id_us_autor) REFERENCES USUARIO(id_usuario),
    FOREIGN KEY (id_us_receptor) REFERENCES USUARIO(id_usuario)
);

-- ============================================
-- TABLA: OBJETO
-- ============================================
CREATE TABLE OBJETO (
    id_objeto INT NOT NULL AUTO_INCREMENT,
    id_us_arrendador INT NOT NULL,
    nombre_objeto VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    descripcion TEXT NOT NULL,
    PRIMARY KEY (id_objeto),
    FOREIGN KEY (id_us_arrendador) REFERENCES USUARIO(id_usuario)
);

-- ============================================
-- TABLA: SOLICITUD_RENTA
-- ============================================
CREATE TABLE SOLICITUD_RENTA (
    id_solicitud INT NOT NULL AUTO_INCREMENT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    id_objeto INT NOT NULL,
    id_us_arrendador INT NOT NULL,
    id_us_arrendatario INT NOT NULL,
    PRIMARY KEY (id_solicitud),
    FOREIGN KEY (id_objeto) REFERENCES OBJETO(id_objeto),
    FOREIGN KEY (id_us_arrendador) REFERENCES USUARIO(id_usuario),
    FOREIGN KEY (id_us_arrendatario) REFERENCES USUARIO(id_usuario)
);

-- ============================================
-- TABLA: TRANSACCION
-- ============================================
CREATE TABLE TRANSACCION (
    id_transaccion INT NOT NULL AUTO_INCREMENT,
    monto_total DECIMAL(10,2) NOT NULL,
    fecha_pago DATE NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    estatus_pago VARCHAR(20) NOT NULL,
    id_solicitud INT NOT NULL,
    PRIMARY KEY (id_transaccion),
    FOREIGN KEY (id_solicitud) REFERENCES SOLICITUD_RENTA(id_solicitud)
);

-- ============================================
-- TABLA: CUENTABANCARIA
-- ============================================
CREATE TABLE CUENTABANCARIA (
    id_cuenta INT NOT NULL AUTO_INCREMENT,
    banco VARCHAR(50) NOT NULL,
    numero_tarjeta VARCHAR(20) NOT NULL,
    id_usuario INT NOT NULL,
    PRIMARY KEY (id_cuenta),
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);

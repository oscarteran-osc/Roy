USE ROY;
SET FOREIGN_KEY_CHECKS = 0;
-- Usuarios realistas (MX)
INSERT INTO USUARIO
(nombre_us, apellido_us, correo, telefono, contrasena, domicilio, fecha_registro, zona, foto_url)
VALUES
('Sofía','Ramírez','sofia.ramirez@roy.mx','5510000001','$2a$10$dummyhash','Av. Insurgentes Sur 123, Benito Juárez, CDMX','2025-11-05','CDMX - Benito Juárez','https://res.cloudinary.com/dcwinxdt7/image/upload/v1768014306/roy/profile/ny2go4vhc1fuiidbdb6w.png'),
('Diego','Hernández','diego.hernandez@roy.mx','5510000002','$2a$10$dummyhash','Calz. de Tlalpan 850, Coyoacán, CDMX','2025-11-20','CDMX - Coyoacán',NULL),
('Valeria','Gómez','valeria.gomez@roy.mx','5510000003','$2a$10$dummyhash','Eje Central 45, Cuauhtémoc, CDMX','2025-12-02','CDMX - Cuauhtémoc',NULL),
('Jorge','López','jorge.lopez@roy.mx','5510000004','$2a$10$dummyhash','Av. Universidad 300, Benito Juárez, CDMX','2025-12-10','CDMX - Benito Juárez',NULL),
('Mariana','Cruz','mariana.cruz@roy.mx','5510000005','$2a$10$dummyhash','Reforma 120, Cuauhtémoc, CDMX','2025-12-15','CDMX - Cuauhtémoc',NULL),

('Fernanda','Martínez','fernanda.martinez@roy.mx','5520000001','$2a$10$dummyhash','Av. Central 10, Ecatepec, EdoMex','2025-10-12','EDOMEX - Ecatepec',NULL),
('Luis','Santos','luis.santos@roy.mx','5520000002','$2a$10$dummyhash','Av. López Portillo 550, Coacalco, EdoMex','2025-10-25','EDOMEX - Coacalco',NULL),
('Regina','Flores','regina.flores@roy.mx','5520000003','$2a$10$dummyhash','Periférico 200, Naucalpan, EdoMex','2025-11-01','EDOMEX - Naucalpan',NULL),

('Andrea','Vargas','andrea.vargas@roy.mx','5530000001','$2a$10$dummyhash','Av. Patria 500, Zapopan, Jalisco','2025-09-18','GDL - Zapopan',NULL),
('Carlos','Navarro','carlos.navarro@roy.mx','5530000002','$2a$10$dummyhash','Av. Vallarta 980, Guadalajara, Jalisco','2025-10-03','GDL - Guadalajara',NULL),

('Paola','Ríos','paola.rios@roy.mx','5540000001','$2a$10$dummyhash','Av. Constitución 200, Monterrey, NL','2025-09-28','MTY - Monterrey',NULL),
('Emilio','Ortega','emilio.ortega@roy.mx','5540000002','$2a$10$dummyhash','Av. Garza Sada 1500, Monterrey, NL','2025-10-08','MTY - Monterrey',NULL),

('Camila','Morales','camila.morales@roy.mx','5550000001','$2a$10$dummyhash','Blvd. Atlixco 120, Puebla, Puebla','2025-11-08','PUE - Puebla',NULL),
('Iván','Castillo','ivan.castillo@roy.mx','5550000002','$2a$10$dummyhash','Av. Juárez 77, Puebla, Puebla','2025-11-14','PUE - Puebla',NULL),

('Daniela','Salazar','daniela.salazar@roy.mx','5560000001','$2a$10$dummyhash','Av. Yucatán 33, Mérida, Yucatán','2025-12-01','MID - Mérida',NULL);

-- Objetos: 120 (repartidos entre arrendadores 1..15)
-- Usaremos imagen_url como principal y también la tabla IMAGEN_OBJETO con 1-3 imgs.

INSERT INTO OBJETO
(id_us_arrendador, nombre_objeto, precio, estado, categoria, descripcion, imagen_url)
VALUES
(1,'Taladro inalámbrico 20V con brocas',180.00,'disponible','Herramientas','Taladro inalámbrico ideal para casa. Incluye cargador y juego de brocas básico.','https://picsum.photos/seed/roy1/900/700'),
(2,'Escalera de aluminio 5 peldaños',120.00,'disponible','Hogar','Escalera ligera y estable, perfecta para limpieza y mantenimiento.','https://picsum.photos/seed/roy2/900/700'),
(3,'Hidrolavadora compacta',250.00,'disponible','Herramientas','Hidrolavadora para coche/patio. Boquillas y manguera incluidas.','https://picsum.photos/seed/roy3/900/700'),
(4,'Proyector Full HD (cine en casa)',320.00,'disponible','Electrónica','Proyector con HDMI, ideal para pelis y presentaciones.','https://picsum.photos/seed/roy4/900/700'),
(5,'Consola retro con 2 controles',150.00,'disponible','Electrónica','Consola retro con juegos preinstalados, perfecta para reunión.','https://picsum.photos/seed/roy5/900/700'),
(6,'Carpa para 4 personas',220.00,'disponible','Camping','Carpa impermeable, fácil de armar. Incluye estacas y bolsa.','https://picsum.photos/seed/roy6/900/700'),
(7,'Silla gamer ergonómica',200.00,'disponible','Hogar','Silla cómoda para estudiar/jugar, reclinable y ajustable.','https://picsum.photos/seed/roy7/900/700'),
(8,'Bocina Bluetooth potente',160.00,'disponible','Electrónica','Bocina con buen bass, batería larga. Ideal para fiesta pequeña.','https://picsum.photos/seed/roy8/900/700'),
(9,'Cámara instantánea tipo Polaroid',230.00,'disponible','Fotografía','Cámara instantánea para eventos. Incluye 1 paquete de película.','https://picsum.photos/seed/roy9/900/700'),
(10,'Kit de luces LED para fotos/video',190.00,'disponible','Fotografía','Aro de luz + tripié, perfecto para reels, fotos y tareas.','https://picsum.photos/seed/roy10/900/700');

-- Genera el resto rápido duplicando patrones (para que no pegues 120 a mano):
-- 110 objetos extra con datos variados usando un INSERT SELECT (truco)
INSERT INTO OBJETO (id_us_arrendador, nombre_objeto, precio, estado, categoria, descripcion, imagen_url)
SELECT
  (1 + (n % 15)) AS id_us_arrendador,
  CONCAT(
    CASE (n % 12)
      WHEN 0 THEN 'Bicicleta urbana rodada 26'
      WHEN 1 THEN 'Patín eléctrico'
      WHEN 2 THEN 'GoPro/Action cam con accesorios'
      WHEN 3 THEN 'Nintendo Switch para reunión'
      WHEN 4 THEN 'Microondas compacto'
      WHEN 5 THEN 'Aspiradora potente'
      WHEN 6 THEN 'Lavadora a presión'
      WHEN 7 THEN 'Kit de herramienta (caja completa)'
      WHEN 8 THEN 'Maleta de viaje grande'
      WHEN 9 THEN 'Silla plegable + mesa camping'
      WHEN 10 THEN 'Router WiFi / repetidor'
      ELSE 'Impresora multifuncional'
    END,
    ' #', n
  ) AS nombre_objeto,
  (80 + (n % 20) * 15) AS precio,
  'disponible' AS estado,
  CASE (n % 8)
    WHEN 0 THEN 'Electrónica'
    WHEN 1 THEN 'Hogar'
    WHEN 2 THEN 'Herramientas'
    WHEN 3 THEN 'Fotografía'
    WHEN 4 THEN 'Camping'
    WHEN 5 THEN 'Deportes'
    WHEN 6 THEN 'Eventos'
    ELSE 'Oficina'
  END AS categoria,
  CONCAT('Renta por día. Entrego limpio y funcionando. Se requiere identificación al recoger. Artículo: ', n) AS descripcion,
  CONCAT('https://picsum.photos/seed/roy_obj_', n, '/900/700') AS imagen_url
FROM (
  SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
  UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
  UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
  UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
  UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25
  UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30
  UNION ALL SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35
  UNION ALL SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40
  UNION ALL SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45
  UNION ALL SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48 UNION ALL SELECT 49 UNION ALL SELECT 50
  UNION ALL SELECT 51 UNION ALL SELECT 52 UNION ALL SELECT 53 UNION ALL SELECT 54 UNION ALL SELECT 55
  UNION ALL SELECT 56 UNION ALL SELECT 57 UNION ALL SELECT 58 UNION ALL SELECT 59 UNION ALL SELECT 60
  UNION ALL SELECT 61 UNION ALL SELECT 62 UNION ALL SELECT 63 UNION ALL SELECT 64 UNION ALL SELECT 65
  UNION ALL SELECT 66 UNION ALL SELECT 67 UNION ALL SELECT 68 UNION ALL SELECT 69 UNION ALL SELECT 70
  UNION ALL SELECT 71 UNION ALL SELECT 72 UNION ALL SELECT 73 UNION ALL SELECT 74 UNION ALL SELECT 75
  UNION ALL SELECT 76 UNION ALL SELECT 77 UNION ALL SELECT 78 UNION ALL SELECT 79 UNION ALL SELECT 80
  UNION ALL SELECT 81 UNION ALL SELECT 82 UNION ALL SELECT 83 UNION ALL SELECT 84 UNION ALL SELECT 85
  UNION ALL SELECT 86 UNION ALL SELECT 87 UNION ALL SELECT 88 UNION ALL SELECT 89 UNION ALL SELECT 90
  UNION ALL SELECT 91 UNION ALL SELECT 92 UNION ALL SELECT 93 UNION ALL SELECT 94 UNION ALL SELECT 95
  UNION ALL SELECT 96 UNION ALL SELECT 97 UNION ALL SELECT 98 UNION ALL SELECT 99 UNION ALL SELECT 100
  UNION ALL SELECT 101 UNION ALL SELECT 102 UNION ALL SELECT 103 UNION ALL SELECT 104 UNION ALL SELECT 105
  UNION ALL SELECT 106 UNION ALL SELECT 107 UNION ALL SELECT 108 UNION ALL SELECT 109 UNION ALL SELECT 110
) t;

-- 1 imagen principal por cada objeto
INSERT INTO IMAGEN_OBJETO (id_objeto, url_imagen, es_principal)
SELECT id_objeto, imagen_url, TRUE
FROM OBJETO;

-- 2 imágenes extra por cada objeto (no principal)
INSERT INTO IMAGEN_OBJETO (id_objeto, url_imagen, es_principal)
SELECT id_objeto, CONCAT('https://picsum.photos/seed/roy_extra1_', id_objeto, '/900/700'), FALSE
FROM OBJETO;

INSERT INTO IMAGEN_OBJETO (id_objeto, url_imagen, es_principal)
SELECT id_objeto, CONCAT('https://picsum.photos/seed/roy_extra2_', id_objeto, '/900/700'), FALSE
FROM OBJETO;

SET SQL_SAFE_UPDATES = 0;

UPDATE usuario
SET foto_url = CONCAT('https://i.pravatar.cc/300?img=', (id_usuario % 70) + 1)
WHERE foto_url IS NULL OR foto_url = '';

SELECT id_usuario, correo, zona, foto_url
FROM usuario
ORDER BY id_usuario;

INSERT INTO objeto (id_us_arrendador, nombre_objeto, precio, estado, categoria, descripcion, imagen_url)
VALUES
(1, 'Taladro inalámbrico Bosch 18V', 120.00, 'bueno', 'Herramientas', 'Taladro con batería, ideal para casa. Incluye brocas básicas.', 'https://loremflickr.com/640/480/drill?lock=201'),
(2, 'Escalera plegable 4 pasos', 80.00, 'bueno', 'Hogar', 'Escalera estable, ligera, perfecta para foco/limpieza.', 'https://loremflickr.com/640/480/ladder?lock=202'),
(3, 'Cámara Canon Rebel T6', 250.00, 'muy bueno', 'Electrónica', 'Cámara DSLR con lente 18-55. Ideal para eventos.', 'https://loremflickr.com/640/480/camera?lock=203'),
(4, 'Proyector portátil 1080p', 220.00, 'bueno', 'Electrónica', 'Proyector para pelis/presentaciones, incluye HDMI.', 'https://loremflickr.com/640/480/projector?lock=204'),
(5, 'Bocina JBL Charge', 90.00, 'bueno', 'Electrónica', 'Bocina potente, batería dura bastante, bluetooth.', 'https://loremflickr.com/640/480/speaker?lock=205'),
(6, 'Bicicleta rodada 26', 160.00, 'regular', 'Deportes', 'Bici para paseos, frenos al 80%, llantas bien.', 'https://loremflickr.com/640/480/bicycle?lock=206'),
(7, 'Patines en línea talla 24', 70.00, 'muy bueno', 'Deportes', 'Patines con ajuste, protecciones opcionales.', 'https://loremflickr.com/640/480/skates?lock=207'),
(8, 'Carpa para 4 personas', 130.00, 'bueno', 'Aventura', 'Carpa impermeable, incluye varillas y bolsa.', 'https://loremflickr.com/640/480/tent?lock=208'),
(9, 'Hielera grande 45L', 60.00, 'bueno', 'Aventura', 'Hielera para picnic/fiesta, mantiene frío bien.', 'https://loremflickr.com/640/480/cooler?lock=209'),
(10,'Kit de luces LED para foto', 95.00, 'muy bueno', 'Fotografía', 'Aro de luz + tripié, ideal para contenido.', 'https://loremflickr.com/640/480/ringlight?lock=210'),

(11,'PlayStation 4 Slim', 280.00, 'bueno', 'Consolas', 'PS4 con 1 control. Juegos aparte.', 'https://loremflickr.com/640/480/ps4?lock=211'),
(12,'Nintendo Switch', 300.00, 'muy bueno', 'Consolas', 'Switch con dock y joycons, lista para jugar.', 'https://loremflickr.com/640/480/nintendo?lock=212'),
(13,'Control Xbox', 45.00, 'bueno', 'Consolas', 'Control en buen estado, sin drift.', 'https://loremflickr.com/640/480/gamecontroller?lock=213'),
(14,'Laptop Lenovo i5 8GB', 350.00, 'bueno', 'Computo', 'Laptop para clases/oficina, SSD rápido.', 'https://loremflickr.com/640/480/laptop?lock=214'),
(15,'iPad 9a gen', 260.00, 'muy bueno', 'Computo', 'iPad para apuntes, incluye funda.', 'https://loremflickr.com/640/480/ipad?lock=215'),
(16,'Microondas 20L', 75.00, 'regular', 'Hogar', 'Calienta bien, detalles estéticos.', 'https://loremflickr.com/640/480/microwave?lock=216'),
(1, 'Licuadora Oster', 55.00, 'bueno', 'Cocina', 'Licuadora con vaso de vidrio, funciona al 100%.', 'https://loremflickr.com/640/480/blender?lock=217'),
(2, 'Freidora de aire', 85.00, 'muy bueno', 'Cocina', 'Airfryer 4L, fácil de usar, súper práctica.', 'https://loremflickr.com/640/480/airfryer?lock=218'),
(3, 'Silla gamer', 90.00, 'regular', 'Muebles', 'Silla cómoda, detalles en el vinipiel.', 'https://loremflickr.com/640/480/gamingchair?lock=219'),
(4, 'Mesa plegable', 45.00, 'bueno', 'Muebles', 'Mesa fácil de guardar, ideal para eventos.', 'https://loremflickr.com/640/480/table?lock=220'),

(5, 'Andamio pequeño', 140.00, 'bueno', 'Herramientas', 'Andamio para pintura, seguro y estable.', 'https://loremflickr.com/640/480/scaffold?lock=221'),
(6, 'Hidrolavadora', 160.00, 'muy bueno', 'Herramientas', 'Hidrolavadora para coche/patio. Incluye manguera.', 'https://loremflickr.com/640/480/pressurewasher?lock=222'),
(7, 'Parlante para fiesta', 150.00, 'bueno', 'Audio', 'Bocina grande con micrófono, suena fuerte.', 'https://loremflickr.com/640/480/party-speaker?lock=223'),
(8, 'Micrófono condenser', 60.00, 'muy bueno', 'Audio', 'Mic para grabar voz, incluye base y cable.', 'https://loremflickr.com/640/480/microphone?lock=224'),
(9, 'Guitarra acústica', 90.00, 'regular', 'Música', 'Guitarra básica, cuerdas nuevas, funda incluida.', 'https://loremflickr.com/640/480/guitar?lock=225'),
(10,'Teclado musical', 120.00, 'bueno', 'Música', 'Teclado con soporte, ideal para practicar.', 'https://loremflickr.com/640/480/keyboard?lock=226'),
(11,'Maleta grande de viaje', 50.00, 'bueno', 'Viaje', 'Maleta 28”, ruedas funcionales.', 'https://loremflickr.com/640/480/suitcase?lock=227'),
(12,'GoPro Hero', 190.00, 'muy bueno', 'Fotografía', 'GoPro con carcasa y accesorios básicos.', 'https://loremflickr.com/640/480/gopro?lock=228'),
(13,'Impresora HP', 80.00, 'regular', 'Computo', 'Impresora con wifi, imprime bien, tinta aparte.', 'https://loremflickr.com/640/480/printer?lock=229'),
(14,'Router WiFi', 35.00, 'bueno', 'Computo', 'Router básico para casa, buena señal.', 'https://loremflickr.com/640/480/router?lock=230'),

(15,'Bafle + tripié', 180.00, 'bueno', 'Eventos', 'Bafle para fiestas, incluye tripié.', 'https://loremflickr.com/640/480/speaker?lock=231'),
(16,'Sillas plegables (x6)', 110.00, 'muy bueno', 'Eventos', 'Paquete de 6 sillas, perfectas para reunión.', 'https://loremflickr.com/640/480/chair?lock=232'),
(1, 'Mesa de dulces (setup)', 200.00, 'bueno', 'Eventos', 'Decoración básica para mesa de dulces.', 'https://loremflickr.com/640/480/party?lock=233'),
(2, 'Inflable pequeño para niños', 260.00, 'bueno', 'Eventos', 'Inflable compacto, divertido para fiestas.', 'https://loremflickr.com/640/480/bouncycastle?lock=234'),
(3, 'Barra de pesas', 75.00, 'bueno', 'Deportes', 'Barra con discos básicos para entrenar.', 'https://loremflickr.com/640/480/weights?lock=235'),
(4, 'Caminadora', 300.00, 'regular', 'Deportes', 'Caminadora funciona, algo ruidosa.', 'https://loremflickr.com/640/480/treadmill?lock=236'),
(5, 'Set de camping (sillas+mesa)', 140.00, 'muy bueno', 'Aventura', 'Set plegable para camping, muy cómodo.', 'https://loremflickr.com/640/480/camping?lock=237'),
(6, 'Kayak inflable', 280.00, 'bueno', 'Aventura', 'Kayak inflable con remos, para lago.', 'https://loremflickr.com/640/480/kayak?lock=238'),
(7, 'Dron básico', 210.00, 'bueno', 'Electrónica', 'Dron para principiantes, cámara simple.', 'https://loremflickr.com/640/480/drone?lock=239'),
(8, 'Tripié profesional', 55.00, 'muy bueno', 'Fotografía', 'Tripié alto, estable, para cámara/cel.', 'https://loremflickr.com/640/480/tripod?lock=240');


INSERT INTO imagen_objeto (id_objeto, url_imagen, es_principal)
SELECT id_objeto, imagen_url, TRUE
FROM objeto
WHERE imagen_url IS NOT NULL;

INSERT INTO imagen_objeto (id_objeto, url_imagen, es_principal)
SELECT id_objeto, CONCAT('https://loremflickr.com/640/480/', LOWER(REPLACE(categoria,' ','-')), '?lock=', id_objeto + 500), FALSE
FROM objeto;

INSERT INTO imagen_objeto (id_objeto, url_imagen, es_principal)
SELECT id_objeto, CONCAT('https://loremflickr.com/640/480/', LOWER(REPLACE(estado,' ','-')), '?lock=', id_objeto + 800), FALSE
FROM objeto;
SET FOREIGN_KEY_CHECKS = 1;
UPDATE objeto
SET estado = 'Disponible';

UPDATE objeto SET categoria = 'Herramientas' WHERE categoria IN ('Herramientas');
UPDATE objeto SET categoria = 'Eventos' WHERE categoria IN ('Eventos');
UPDATE objeto SET categoria = 'Tecnología' WHERE categoria IN ('Electrónica','Computo','Consolas','Audio');
UPDATE objeto SET categoria = 'Transporte' WHERE categoria IN ('Viaje');
UPDATE objeto SET categoria = 'Hogar y Muebles' WHERE categoria IN ('Hogar','Muebles','Cocina');
UPDATE objeto SET categoria = 'Deportes y Aire Libre' WHERE categoria IN ('Deportes','Aventura');
UPDATE objeto SET categoria = 'Electrodomésticos' WHERE categoria IN ('Electrodomésticos');
UPDATE objeto SET categoria = 'Ropa y Accesorios' WHERE categoria IN ('Ropa');
UPDATE objeto SET categoria = 'Juegos y Entretenimiento' WHERE categoria IN ('Juegos','Entretenimiento');
UPDATE objeto SET categoria = 'Mascotas' WHERE categoria IN ('Mascotas');

SELECT categoria, COUNT(*) FROM objeto GROUP BY categoria;

UPDATE objeto SET categoria = 'Deportes y Aire Libre'
WHERE categoria = 'Camping';

UPDATE objeto SET categoria = 'Tecnología'
WHERE categoria IN ('Fotografía', 'Oficina');

UPDATE objeto SET categoria = 'Juegos y Entretenimiento'
WHERE categoria = 'Música';

select * from usuario;

UPDATE USUARIO
SET contrasena = '$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm';
ALTER TABLE usuario 
MODIFY domicilio VARCHAR(255) NULL;



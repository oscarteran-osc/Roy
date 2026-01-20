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
(1,'Taladro inalámbrico 20V con brocas',180.00,'Disponible','Herramientas','Taladro inalámbrico ideal para casa. Incluye cargador y juego de brocas básico.','https://images.unsplash.com/photo-1504148455328-c376907d081c?w=640'),
(2,'Escalera de aluminio 5 peldaños',120.00,'Disponible','Hogar','Escalera ligera y estable, perfecta para limpieza y mantenimiento.','https://images.unsplash.com/photo-1581858147666-503937a5ad74?w=640'),
(3,'Hidrolavadora compacta',250.00,'No Disponible','Herramientas','Hidrolavadora para coche/patio. Boquillas y manguera incluidas.','https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=640'),
(4,'Proyector Full HD (cine en casa)',320.00,'Disponible','Electrónica','Proyector con HDMI, ideal para pelis y presentaciones.','https://images.unsplash.com/photo-1593784991095-a205069470b6?w=640'),
(5,'Consola retro con 2 controles',150.00,'Disponible','Electrónica','Consola retro con juegos preinstalados, perfecta para reunión.','https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=640'),
(6,'Carpa para 4 personas',220.00,'No Disponible','Camping','Carpa impermeable, fácil de armar. Incluye estacas y bolsa.','https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=640'),
(7,'Silla gamer ergonómica',200.00,'Disponible','Hogar','Silla cómoda para estudiar/jugar, reclinable y ajustable.','https://images.unsplash.com/photo-1564089876584-7f0210f0f5f1?w=640'),
(8,'Bocina Bluetooth potente',160.00,'Disponible','Electrónica','Bocina con buen bass, batería larga. Ideal para fiesta pequeña.','https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=640'),
(9,'Cámara instantánea tipo Polaroid',230.00,'No Disponible','Fotografía','Cámara instantánea para eventos. Incluye 1 paquete de película.','https://images.unsplash.com/photo-1526139334526-f591a54b477c?w=640'),
(10,'Kit de luces LED para fotos/video',190.00,'Disponible','Fotografía','Aro de luz + tripié, perfecto para reels, fotos y tareas.','https://images.unsplash.com/photo-1550985616-10810253b84d?w=640');

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
(1,'Taladro inalámbrico 20V con brocas',180.00,'Disponible','Herramientas','Taladro inalámbrico ideal para casa. Incluye cargador y juego de brocas básico.','https://images.unsplash.com/photo-1504148455328-c376907d081c?w=640'),
(2,'Escalera de aluminio 5 peldaños',120.00,'Disponible','Hogar','Escalera ligera y estable, perfecta para limpieza y mantenimiento.','https://imgur.com/7eSMWSa'),
(3,'Hidrolavadora compacta',250.00,'No Disponible','Herramientas','Hidrolavadora para coche/patio. Boquillas y manguera incluidas.','https://i.pinimg.com/736x/79/27/82/792782e4b1456309c4049488155bdb4c.jpg'),
(4,'Proyector Full HD (cine en casa)',320.00,'Disponible','Electrónica','Proyector con HDMI, ideal para pelis y presentaciones.','https://imgur.com/0DBmcLz'),
(5,'Consola retro con 2 controles',150.00,'Disponible','Electrónica','Consola retro con juegos preinstalados, perfecta para reunión.','https://i.pinimg.com/736x/0a/4c/bd/0a4cbd5fc654335f10a0893724d4c71a.jpg'),
(6,'Carpa para 4 personas',220.00,'No Disponible','Camping','Carpa impermeable, fácil de armar. Incluye estacas y bolsa.','https://imgur.com/eSAyPQM'),
(7,'Silla gamer ergonómica',200.00,'Disponible','Hogar','Silla cómoda para estudiar/jugar, reclinable y ajustable.','https://i.pinimg.com/736x/1c/b6/bf/1cb6bfc2b3142fa94e47563d170f8db7.jpg'),
(8,'Bocina Bluetooth potente',160.00,'Disponible','Electrónica','Bocina con buen bass, batería larga. Ideal para fiesta pequeña.','https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=640'),
(9,'Cámara instantánea tipo Polaroid',230.00,'No Disponible','Fotografía','Cámara instantánea para eventos. Incluye 1 paquete de película.','https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=640'),
(10,'Kit de luces LED para fotos/video',190.00,'Disponible','Fotografía','Aro de luz + tripié, perfecto para reels, fotos y tareas.','https://i.pinimg.com/1200x/2a/99/00/2a99001ea817cec852a895607ddb935c.jpg'),

(11,'PlayStation 4 Slim', 280.00, 'Disponible', 'Consolas', 'PS4 con 1 control. Juegos aparte.', 'https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?w=640'),
(12,'Nintendo Switch', 300.00, 'No Disponible', 'Consolas', 'Switch con dock y joycons, lista para jugar.', 'https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=640'),
(13,'Control Xbox', 45.00, 'Disponible', 'Consolas', 'Control en buen estado, sin drift.', 'https://images.unsplash.com/photo-1600080972464-8e5f35f63d08?w=640'),
(14,'Laptop Lenovo i5 8GB', 350.00, 'Disponible', 'Computo', 'Laptop para clases/oficina, SSD rápido.', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=640'),
(15,'iPad 9a gen', 260.00, 'No Disponible', 'Computo', 'iPad para apuntes, incluye funda.', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=640'),
(16,'Microondas 20L', 75.00, 'Disponible', 'Hogar', 'Calienta bien, detalles estéticos.', 'https://images.unsplash.com/photo-1585659722983-3a675dabf23d?w=640'),
(1, 'Licuadora Oster', 55.00, 'Disponible', 'Cocina', 'Licuadora con vaso de vidrio, funciona al 100%.', 'https://images.unsplash.com/photo-1585515320310-259814833e62?w=640'),
(2, 'Freidora de aire', 85.00, 'No Disponible', 'Cocina', 'Airfryer 4L, fácil de usar, súper práctica.', 'https://i.pinimg.com/1200x/31/02/af/3102af1800aa457d54a34ccba4fc722c.jpg'),
(3, 'Silla gamer', 90.00, 'Disponible', 'Hogar y Muebles', 'Silla cómoda, detalles en el vinipiel.', 'https://i.pinimg.com/736x/a1/ad/a2/a1ada249832ad718abb0934cf618620b.jpg'),
(4, 'Mesa plegable', 45.00, 'Disponible', 'Hogar y Muebles', 'Mesa fácil de guardar, ideal para eventos.', 'https://i.pinimg.com/736x/49/98/27/499827e5eef8b611dd3abab2a096e383.jpg'),

(5, 'Andamio pequeño', 140.00, 'No Disponible', 'Herramientas', 'Andamio para pintura, seguro y estable.', 'https://i.pinimg.com/736x/84/f3/01/84f301a46ed89695c037158df1b6d536.jpg'),
(6, 'Hidrolavadora', 160.00, 'Disponible', 'Herramientas', 'Hidrolavadora para coche/patio. Incluye manguera.', 'https://i.pinimg.com/736x/29/70/5a/29705a8522e4abf052ee0ed7032074c1.jpg'),
(7, 'Parlante para fiesta', 150.00, 'Disponible', 'Audio', 'Bocina grande con micrófono, suena fuerte.', 'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=640'),
(8, 'Micrófono condenser', 60.00, 'No Disponible', 'Audio', 'Mic para grabar voz, incluye base y cable.', 'https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=640'),
(9, 'Guitarra acústica', 90.00, 'Disponible', 'Música', 'Guitarra básica, cuerdas nuevas, funda incluida.', 'https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=640'),
(10,'Teclado musical', 120.00, 'Disponible', 'Música', 'Teclado con soporte, ideal para practicar.', 'https://i.pinimg.com/736x/94/e4/a0/94e4a0734b870a171c2c0bd019b2a39f.jpg'),
(11,'Maleta grande de viaje', 50.00, 'No Disponible', 'Viaje', 'Maleta 28”, ruedas funcionales.', 'https://images.unsplash.com/photo-1565026057447-bc90a3dceb87?w=640'),
(12,'GoPro Hero', 190.00, 'Disponible', 'Fotografía', 'GoPro con carcasa y accesorios básicos.', 'https://i.pinimg.com/736x/d8/cd/6c/d8cd6cd77097fabfba482b8ddccd4473.jpg'),
(13,'Impresora HP', 80.00, 'No Disponible', 'Computo', 'Impresora con wifi, imprime bien, tinta aparte.', 'https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?w=640'),
(14,'Router WiFi', 35.00, 'Disponible', 'Computo', 'Router básico para casa, buena señal.', 'https://images.unsplash.com/photo-1606904825846-647eb07f5be2?w=640'),

(15,'Bafle + tripié', 180.00, 'Disponible', 'Eventos', 'Bafle para fiestas, incluye tripié.', 'https://i.pinimg.com/736x/fc/37/df/fc37df26da446996f65239c259d92c21.jpg'),
(16,'Sillas plegables (x6)', 110.00, 'No Disponible', 'Eventos', 'Paquete de 6 sillas, perfectas para reunión.', 'https://images.unsplash.com/photo-1503602642458-232111445657?w=640'),
(1, 'Mesa de dulces (setup)', 200.00, 'Disponible', 'Eventos', 'Decoración básica para mesa de dulces.', 'https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?w=640'),
(2, 'Inflable pequeño para niños', 260.00, 'Disponible', 'Eventos', 'Inflable compacto, divertido para fiestas.', 'https://i.pinimg.com/1200x/2b/6a/69/2b6a69d8293c577b6e2980cf451747df.jpg'),
(3, 'Barra de pesas', 75.00, 'No Disponible', 'Deportes', 'Barra con discos básicos para entrenar.', 'https://i.pinimg.com/736x/b8/12/7a/b8127aae0a09cea9adf1b03555429dbb.jpg'),
(4, 'Caminadora', 300.00, 'Disponible', 'Deportes', 'Caminadora funciona, algo ruidosa.', 'https://i.pinimg.com/736x/56/6c/43/566c43be98f2108925aeb1d6774d84c8.jpg'),
(5, 'Set de camping (sillas+mesa)', 140.00, 'Disponible', 'Aventura', 'Set plegable para camping, muy cómodo.', 'https://i.pinimg.com/1200x/60/66/1b/60661bdb55a9fdded7fb3f0dfad91f33.jpg'),
(6, 'Kayak inflable', 280.00, 'No Disponible', 'Aventura', 'Kayak inflable con remos, para lago.', 'https://i.pinimg.com/1200x/a2/61/57/a261575e99cd7e0d568bdb018f998cba.jpg'),
(7, 'Dron básico', 210.00, 'Disponible', 'Electrónica', 'Dron para principiantes, cámara simple.', 'https://images.unsplash.com/photo-1473968512647-3e447244af8f?w=640'),
(8, 'Tripié profesional', 55.00, 'Disponible', 'Fotografía', 'Tripié alto, estable, para cámara/cel.', 'https://i.pinimg.com/1200x/20/f0/a0/20f0a04dc527016bcd31cb9369f87eaa.jpg');


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


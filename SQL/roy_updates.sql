USE ROY;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- PASO 1: MODIFICAR ESTRUCTURA DE TABLA RESENA
-- =====================================================

-- Agregar columna id_objeto a la tabla resena
ALTER TABLE resena 
ADD COLUMN id_objeto INT NULL AFTER id_us_receptor,
ADD CONSTRAINT resena_ibfk_3 FOREIGN KEY (id_objeto) REFERENCES objeto(id_objeto);

-- =====================================================
-- PASO 2: LIMPIAR DATOS EXISTENTES
-- =====================================================

-- Tablas que dependen de otras (hijas) primero:
TRUNCATE TABLE FAVORITO;
TRUNCATE TABLE MENSAJE;
TRUNCATE TABLE NOTIFICACION;
TRUNCATE TABLE REPORTE;
TRUNCATE TABLE IMAGEN_OBJETO;
TRUNCATE TABLE TRANSACCION;
TRUNCATE TABLE SOLICITUD_RENTA;
TRUNCATE TABLE RESENA;
TRUNCATE TABLE CUENTABANCARIA;
-- Tablas base (padres) al final:
TRUNCATE TABLE OBJETO;
TRUNCATE TABLE USUARIO;

SET FOREIGN_KEY_CHECKS = 1;
-- =====================================================
-- PASO 3: INSERTAR USUARIOS
-- =====================================================

INSERT INTO USUARIO
(nombre_us, apellido_us, correo, telefono, contrasena, domicilio, fecha_registro, zona, foto_url)
VALUES
('Sofía','Ramírez','sofia.ramirez@roy.mx','5510000001','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Insurgentes Sur 123, Benito Juárez, CDMX','2025-11-05','CDMX - Benito Juárez','https://res.cloudinary.com/dcwinxdt7/image/upload/v1768014306/roy/profile/ny2go4vhc1fuiidbdb6w.png'),
('Diego','Hernández','diego.hernandez@roy.mx','5510000002','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Calz. de Tlalpan 850, Coyoacán, CDMX','2025-11-20','CDMX - Coyoacán','https://i.pravatar.cc/300?img=2'),
('Valeria','Gómez','valeria.gomez@roy.mx','5510000003','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Eje Central 45, Cuauhtémoc, CDMX','2025-12-02','CDMX - Cuauhtémoc','https://i.pravatar.cc/300?img=3'),
('Jorge','López','jorge.lopez@roy.mx','5510000004','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Universidad 300, Benito Juárez, CDMX','2025-12-10','CDMX - Benito Juárez','https://i.pravatar.cc/300?img=4'),
('Mariana','Cruz','mariana.cruz@roy.mx','5510000005','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Reforma 120, Cuauhtémoc, CDMX','2025-12-15','CDMX - Cuauhtémoc','https://i.pravatar.cc/300?img=5'),
('Fernanda','Martínez','fernanda.martinez@roy.mx','5520000001','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Central 10, Ecatepec, EdoMex','2025-10-12','EDOMEX - Ecatepec','https://i.pravatar.cc/300?img=6'),
('Luis','Santos','luis.santos@roy.mx','5520000002','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. López Portillo 550, Coacalco, EdoMex','2025-10-25','EDOMEX - Coacalco','https://i.pravatar.cc/300?img=7'),
('Regina','Flores','regina.flores@roy.mx','5520000003','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Periférico 200, Naucalpan, EdoMex','2025-11-01','EDOMEX - Naucalpan','https://i.pravatar.cc/300?img=8'),
('Andrea','Vargas','andrea.vargas@roy.mx','5530000001','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Patria 500, Zapopan, Jalisco','2025-09-18','GDL - Zapopan','https://i.pravatar.cc/300?img=9'),
('Carlos','Navarro','carlos.navarro@roy.mx','5530000002','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Vallarta 980, Guadalajara, Jalisco','2025-10-03','GDL - Guadalajara','https://i.pravatar.cc/300?img=10'),
('Paola','Ríos','paola.rios@roy.mx','5540000001','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Constitución 200, Monterrey, NL','2025-09-28','MTY - Monterrey','https://i.pravatar.cc/300?img=11'),
('Emilio','Ortega','emilio.ortega@roy.mx','5540000002','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Garza Sada 1500, Monterrey, NL','2025-10-08','MTY - Monterrey','https://i.pravatar.cc/300?img=12'),
('Camila','Morales','camila.morales@roy.mx','5550000001','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Blvd. Atlixco 120, Puebla, Puebla','2025-11-08','PUE - Puebla','https://i.pravatar.cc/300?img=13'),
('Iván','Castillo','ivan.castillo@roy.mx','5550000002','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Juárez 77, Puebla, Puebla','2025-11-14','PUE - Puebla','https://i.pravatar.cc/300?img=14'),
('Daniela','Salazar','daniela.salazar@roy.mx','5560000001','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Yucatán 33, Mérida, Yucatán','2025-12-01','MID - Mérida','https://i.pravatar.cc/300?img=15'),
('Roberto','Silva','roberto.silva@roy.mx','5510000006','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Revolución 456, Coyoacán, CDMX','2025-11-12','CDMX - Coyoacán','https://i.pravatar.cc/300?img=16'),
('Ana','Torres','ana.torres@roy.mx','5510000007','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Chapultepec 789, Miguel Hidalgo, CDMX','2025-10-28','CDMX - Miguel Hidalgo','https://i.pravatar.cc/300?img=17'),
('Miguel','Ruiz','miguel.ruiz@roy.mx','5510000008','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Calz. Ermita 234, Iztapalapa, CDMX','2025-12-05','CDMX - Iztapalapa','https://i.pravatar.cc/300?img=18'),
('Laura','Mendoza','laura.mendoza@roy.mx','5520000004','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Gustavo Baz 890, Tlalnepantla, EdoMex','2025-11-18','EDOMEX - Tlalnepantla','https://i.pravatar.cc/300?img=19'),
('Pedro','García','pedro.garcia@roy.mx','5530000003','$2a$10$o87TzCRbjKGTOCUM0yJKcOa7yhDkKu3bxCAIRdEgM5R6uEw..3sKm','Av. Américas 567, Guadalajara, Jalisco','2025-10-15','GDL - Guadalajara','https://i.pravatar.cc/300?img=20');

-- =====================================================
-- PASO 4: INSERTAR OBJETOS CON ARRENDADORES ASIGNADOS
-- =====================================================

INSERT INTO OBJETO
(id_us_arrendador, nombre_objeto, precio, estado, categoria, descripcion, imagen_url)
VALUES
-- Usuario 1 - Sofía
(1,'Taladro inalámbrico 20V con brocas',180.00,'Disponible','Herramientas','Taladro inalámbrico ideal para casa. Incluye cargador y juego de brocas básico.','https://images.unsplash.com/photo-1504148455328-c376907d081c?w=640'),
(1,'Licuadora Oster',55.00,'Disponible','Electrodomésticos','Licuadora con vaso de vidrio, funciona al 100%.','https://images.unsplash.com/photo-1585515320310-259814833e62?w=640'),
-- Usuario 2 - Diego
(2,'Escalera de aluminio 5 peldaños',120.00,'Disponible','Herramientas','Escalera ligera y estable, perfecta para limpieza y mantenimiento.','https://imgur.com/7eSMWSa'),
(2,'Freidora de aire',85.00,'Disponible','Electrodomésticos','Airfryer 4L, fácil de usar, súper práctica.','https://i.pinimg.com/1200x/31/02/af/3102af1800aa457d54a34ccba4fc722c.jpg'),
(2,'Mesa de dulces (setup)',200.00,'Disponible','Eventos','Decoración básica para mesa de dulces.','https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?w=640'),
-- Usuario 3 - Valeria
(3,'Hidrolavadora compacta',250.00,'Disponible','Herramientas','Hidrolavadora para coche/patio. Boquillas y manguera incluidas.','https://i.pinimg.com/736x/79/27/82/792782e4b1456309c4049488155bdb4c.jpg'),
(3,'Silla gamer',90.00,'Disponible','Hogar y Muebles','Silla cómoda, detalles en el vinipiel.','https://i.pinimg.com/736x/a1/ad/a2/a1ada249832ad718abb0934cf618620b.jpg'),
(3,'Barra de pesas',75.00,'Disponible','Deportes y Aire Libre','Barra con discos básicos para entrenar.','https://i.pinimg.com/736x/b8/12/7a/b8127aae0a09cea9adf1b03555429dbb.jpg'),
-- Usuario 4 - Jorge
(4,'Proyector Full HD (cine en casa)',320.00,'Disponible','Tecnología','Proyector con HDMI, ideal para pelis y presentaciones.','https://imgur.com/0DBmcLz'),
(4,'Mesa plegable',45.00,'Disponible','Hogar y Muebles','Mesa fácil de guardar, ideal para eventos.','https://i.pinimg.com/736x/49/98/27/499827e5eef8b611dd3abab2a096e383.jpg'),
(4,'Caminadora',300.00,'Disponible','Deportes y Aire Libre','Caminadora funciona, algo ruidosa.','https://i.pinimg.com/736x/56/6c/43/566c43be98f2108925aeb1d6774d84c8.jpg'),
-- Usuario 5 - Mariana
(5,'Consola retro con 2 controles',150.00,'Disponible','Juegos y Entretenimiento','Consola retro con juegos preinstalados, perfecta para reunión.','https://i.pinimg.com/736x/0a/4c/bd/0a4cbd5fc654335f10a0893724d4c71a.jpg'),
(5,'Andamio pequeño',140.00,'Disponible','Herramientas','Andamio para pintura, seguro y estable.','https://i.pinimg.com/736x/84/f3/01/84f301a46ed89695c037158df1b6d536.jpg'),
(5,'Set de camping (sillas+mesa)',140.00,'Disponible','Deportes y Aire Libre','Set plegable para camping, muy cómodo.','https://i.pinimg.com/1200x/60/66/1b/60661bdb55a9fdded7fb3f0dfad91f33.jpg'),
-- Usuario 6 - Fernanda
(6,'Carpa para 4 personas',220.00,'Disponible','Deportes y Aire Libre','Carpa impermeable, fácil de armar. Incluye estacas y bolsa.','https://imgur.com/eSAyPQM'),
(6,'Hidrolavadora',160.00,'Disponible','Herramientas','Hidrolavadora para coche/patio. Incluye manguera.','https://i.pinimg.com/736x/29/70/5a/29705a8522e4abf052ee0ed7032074c1.jpg'),
(6,'Kayak inflable',280.00,'Disponible','Deportes y Aire Libre','Kayak inflable con remos, para lago.','https://i.pinimg.com/1200x/a2/61/57/a261575e99cd7e0d568bdb018f998cba.jpg'),
-- Usuario 7 - Luis
(7,'Silla gamer ergonómica',200.00,'Disponible','Hogar y Muebles','Silla cómoda para estudiar/jugar, reclinable y ajustable.','https://i.pinimg.com/736x/1c/b6/bf/1cb6bfc2b3142fa94e47563d170f8db7.jpg'),
(7,'Parlante para fiesta',150.00,'Disponible','Tecnología','Bocina grande con micrófono, suena fuerte.','https://images.unsplash.com/photo-1545454675-3531b543be5d?w=640'),
(7,'Dron básico',210.00,'Disponible','Tecnología','Dron para principiantes, cámara simple.','https://images.unsplash.com/photo-1473968512647-3e447244af8f?w=640'),
-- Usuario 8 - Regina
(8,'Bocina Bluetooth potente',160.00,'Disponible','Tecnología','Bocina con buen bass, batería larga. Ideal para fiesta pequeña.','https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=640'),
(8,'Micrófono condenser',60.00,'Disponible','Tecnología','Mic para grabar voz, incluye base y cable.','https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=640'),
(8,'Tripié profesional',55.00,'Disponible','Tecnología','Tripié alto, estable, para cámara/cel.','https://i.pinimg.com/1200x/20/f0/a0/20f0a04dc527016bcd31cb9369f87eaa.jpg'),
-- Usuario 9 - Andrea
(9,'Cámara instantánea tipo Polaroid',230.00,'Disponible','Tecnología','Cámara instantánea para eventos. Incluye 1 paquete de película.','https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=640'),
(9,'Guitarra acústica',90.00,'Disponible','Juegos y Entretenimiento','Guitarra básica, cuerdas nuevas, funda incluida.','https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=640'),
(9,'Inflable pequeño para niños',260.00,'Disponible','Eventos','Inflable compacto, divertido para fiestas.','https://i.pinimg.com/1200x/2b/6a/69/2b6a69d8293c577b6e2980cf451747df.jpg'),
-- Usuario 10 - Carlos
(10,'Kit de luces LED para fotos/video',190.00,'Disponible','Tecnología','Aro de luz + tripié, perfecto para reels, fotos y tareas.','https://i.pinimg.com/1200x/2a/99/00/2a99001ea817cec852a895607ddb935c.jpg'),
(10,'Teclado musical',120.00,'Disponible','Juegos y Entretenimiento','Teclado con soporte, ideal para practicar.','https://i.pinimg.com/736x/94/e4/a0/94e4a0734b870a171c2c0bd019b2a39f.jpg'),
(10,'GoPro Hero',190.00,'Disponible','Tecnología','GoPro con carcasa y accesorios básicos.','https://i.pinimg.com/736x/d8/cd/6c/d8cd6cd77097fabfba482b8ddccd4473.jpg'),
-- Usuario 11 - Paola
(11,'PlayStation 4 Slim',280.00,'Disponible','Juegos y Entretenimiento','PS4 con 1 control. Juegos aparte.','https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?w=640'),
(11,'Maleta grande de viaje',50.00,'Disponible','Transporte','Maleta 28", ruedas funcionales.','https://images.unsplash.com/photo-1565026057447-bc90a3dceb87?w=640'),
(11,'Bafle + tripié',180.00,'Disponible','Eventos','Bafle para fiestas, incluye tripié.','https://i.pinimg.com/736x/fc/37/df/fc37df26da446996f65239c259d92c21.jpg'),
-- Usuario 12 - Emilio
(12,'Nintendo Switch',300.00,'Disponible','Juegos y Entretenimiento','Switch con dock y joycons, lista para jugar.','https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=640'),
(12,'Impresora HP',80.00,'Disponible','Tecnología','Impresora con wifi, imprime bien, tinta aparte.','https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?w=640'),
(12,'Sillas plegables (x6)',110.00,'Disponible','Eventos','Paquete de 6 sillas, perfectas para reunión.','https://images.unsplash.com/photo-1503602642458-232111445657?w=640'),
-- Usuario 13 - Camila
(13,'Control Xbox',45.00,'Disponible','Juegos y Entretenimiento','Control en buen estado, sin drift.','https://images.unsplash.com/photo-1600080972464-8e5f35f63d08?w=640'),
(13,'Router WiFi',35.00,'Disponible','Tecnología','Router básico para casa, buena señal.','https://images.unsplash.com/photo-1606904825846-647eb07f5be2?w=640'),
-- Usuario 14 - Iván
(14,'Laptop Lenovo i5 8GB',350.00,'Disponible','Tecnología','Laptop para clases/oficina, SSD rápido.','https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=640'),
-- Usuario 15 - Daniela
(15,'iPad 9a gen',260.00,'Disponible','Tecnología','iPad para apuntes, incluye funda.','https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=640'),
-- Usuario 16 - Roberto
(16,'Microondas 20L',75.00,'Disponible','Electrodomésticos','Calienta bien, detalles estéticos.','https://images.unsplash.com/photo-1585659722983-3a675dabf23d?w=640');

-- =====================================================
-- PASO 5: INSERTAR IMÁGENES DE OBJETOS
-- =====================================================

-- Imagen 1: La imagen principal del objeto (es_principal = TRUE)
INSERT INTO imagen_objeto (id_objeto, url_imagen, es_principal)
SELECT id_objeto, imagen_url, TRUE
FROM objeto
WHERE imagen_url IS NOT NULL;

-- Imagen 2: Primera imagen adicional (usando Unsplash con variación)
INSERT INTO imagen_objeto (id_objeto, url_imagen, es_principal)
SELECT 
    id_objeto,
    CASE 
        WHEN categoria = 'Herramientas' THEN CONCAT('https://images.unsplash.com/photo-1581858147666-503937a5ad74?w=640&q=', id_objeto)
        WHEN categoria = 'Tecnología' THEN CONCAT('https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=640&q=', id_objeto)
        WHEN categoria = 'Electrodomésticos' THEN CONCAT('https://images.unsplash.com/photo-1585659722983-3a675dabf23d?w=640&q=', id_objeto)
        WHEN categoria = 'Hogar y Muebles' THEN CONCAT('https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=640&q=', id_objeto)
        WHEN categoria = 'Deportes y Aire Libre' THEN CONCAT('https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=640&q=', id_objeto)
        WHEN categoria = 'Juegos y Entretenimiento' THEN CONCAT('https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=640&q=', id_objeto)
        WHEN categoria = 'Eventos' THEN CONCAT('https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?w=640&q=', id_objeto)
        WHEN categoria = 'Transporte' THEN CONCAT('https://images.unsplash.com/photo-1565026057447-bc90a3dceb87?w=640&q=', id_objeto)
        ELSE CONCAT('https://picsum.photos/seed/obj_', id_objeto, '_1/640/480')
    END AS url_imagen,
    FALSE AS es_principal
FROM objeto;

INSERT INTO imagen_objeto (id_objeto, url_imagen, es_principal)
SELECT 
    id_objeto,
    CONCAT('https://loremflickr.com/640/480/', 
           LOWER(REPLACE(categoria, ' ', '-')), 
           '?lock=', id_objeto * 2) AS url_imagen,
    FALSE AS es_principal
FROM objeto;


-- =====================================================
-- PASO 6: INSERTAR RESEÑAS (2-3 POR OBJETO)
-- =====================================================

-- Plantilla de comentarios variados para reseñas
INSERT INTO RESENA (id_us_autor, id_us_receptor, id_objeto, calificacion, comentario, fecha_resena)
VALUES
-- Objeto 1: Taladro (Usuario 1)
(2, 1, 1, 5, 'Excelente herramienta, llegó en perfectas condiciones. El dueño muy amable y puntual en la entrega.', '2026-01-15'),
(3, 1, 1, 5, 'Funciona de maravilla, la batería dura bastante. Totalmente recomendado para trabajos en casa.', '2026-01-18'),
(4, 1, 1, 4, 'Buen taladro, cumple su función. Le daría 5 estrellas si incluyera más brocas variadas.', '2026-01-20'),

-- Objeto 2: Licuadora (Usuario 1)
(5, 1, 2, 5, 'Licua perfecto, muy potente. La usé para hacer smoothies toda la semana, súper práctica.', '2026-01-16'),
(6, 1, 2, 4, 'Buena licuadora, funciona bien aunque es un poco ruidosa. El vaso de vidrio es un plus.', '2026-01-19'),

-- Objeto 3: Escalera (Usuario 2)
(7, 2, 3, 5, 'Escalera muy estable y ligera. Perfecta para cambiar focos y limpiar ventanas. Recomendada.', '2026-01-14'),
(8, 2, 3, 5, 'Justo lo que necesitaba. El dueño muy responsable, la entregó limpia y en buen estado.', '2026-01-17'),
(9, 2, 3, 4, 'Cumple su función, está en buen estado. Solo le falta un escalón superior más ancho.', '2026-01-21'),

-- Objeto 4: Freidora de aire (Usuario 2)
(10, 2, 4, 5, '¡Me encantó! Cocina súper rápido y sin aceite. La voy a rentar de nuevo seguro.', '2026-01-15'),
(11, 2, 4, 5, 'Excelente para hacer comida saludable. Fácil de usar y limpiar. Muy recomendable.', '2026-01-18'),

-- Objeto 5: Mesa de dulces (Usuario 2)
(12, 2, 5, 5, 'Hermosa decoración, le dio un toque especial a la fiesta de mi hija. Todo impecable.', '2026-01-13'),
(13, 2, 5, 4, 'Bonita decoración, aunque esperaba más accesorios. Aún así se ve bien en las fotos.', '2026-01-16'),
(14, 2, 5, 5, 'Perfecta para eventos infantiles. El montaje es sencillo y queda muy elegante.', '2026-01-19'),

-- Objeto 6: Hidrolavadora (Usuario 3)
(15, 3, 6, 5, 'Limpió mi coche como nuevo. Potente y fácil de manejar. Gran servicio del arrendador.', '2026-01-14'),
(16, 3, 6, 4, 'Buena presión, me sirvió para lavar el patio. Solo que la manguera es algo corta.', '2026-01-17'),

-- Objeto 7: Silla gamer (Usuario 3)
(17, 3, 7, 4, 'Cómoda para trabajar todo el día. El vinipiel tiene algunos detalles pero nada grave.', '2026-01-15'),
(18, 3, 7, 5, 'Súper cómoda y ajustable. Ideal para largas sesiones de estudio. La recomiendo mucho.', '2026-01-18'),
(19, 3, 7, 4, 'Buena silla, reclinable y con apoyo lumbar. Un poco usada pero funcional.', '2026-01-20'),

-- Objeto 8: Barra de pesas (Usuario 3)
(20, 3, 8, 5, 'Perfecta para entrenar en casa. Los discos tienen buen peso y la barra está sólida.', '2026-01-16'),
(1, 3, 8, 4, 'Buen equipo para empezar. Me hubiera gustado que incluyera más discos de peso variable.', '2026-01-19'),

-- Objeto 9: Proyector (Usuario 4)
(2, 4, 9, 5, 'Calidad de imagen excelente. Vimos películas toda la noche, una experiencia increíble.', '2026-01-14'),
(3, 4, 9, 5, 'Proyector potente y fácil de configurar. Perfecto para presentaciones y cine en casa.', '2026-01-17'),
(4, 4, 9, 5, 'Nítido y brillante incluso con algo de luz. Muy satisfecho con la renta.', '2026-01-20'),

-- Objeto 10: Mesa plegable (Usuario 4)
(5, 4, 10, 4, 'Práctica y fácil de guardar. Soporta buen peso, ideal para reuniones pequeñas.', '2026-01-15'),
(6, 4, 10, 5, 'Mesa resistente y estable. La usé para un evento y funcionó perfecto. Muy recomendable.', '2026-01-18'),

-- Objeto 11: Caminadora (Usuario 4)
(7, 4, 11, 4, 'Funciona bien para cardio en casa. Es algo ruidosa como mencionó el dueño pero cumple.', '2026-01-16'),
(8, 4, 11, 3, 'Hace su trabajo pero el ruido es molesto. Recomendaría usarla en área aislada.', '2026-01-19'),

-- Objeto 12: Consola retro (Usuario 5)
(9, 5, 12, 5, '¡Nostalgia pura! Los juegos traen recuerdos. Perfecta para reunión con amigos.', '2026-01-14'),
(10, 5, 12, 5, 'Diversión garantizada. Funcionan todos los juegos y controles responden bien.', '2026-01-17'),
(11, 5, 12, 4, 'Entretenida, aunque algunos juegos son muy viejos. Los controles están OK.', '2026-01-20'),

-- Objeto 13: Andamio (Usuario 5)
(12, 5, 13, 5, 'Muy estable y seguro. Lo usé para pintar toda la sala sin problemas.', '2026-01-15'),
(13, 5, 13, 4, 'Cumple su función, resistente y fácil de armar. Solo le falta una plataforma más grande.', '2026-01-18'),

-- Objeto 14: Set de camping (Usuario 5)
(14, 5, 14, 5, 'Excelente para acampar. Las sillas son cómodas y la mesa es práctica. Todo compacto.', '2026-01-16'),
(15, 5, 14, 5, 'Perfecto para día de campo. Fácil de transportar y armar. Muy satisfecho.', '2026-01-19'),

-- Objeto 15: Carpa (Usuario 6)
(16, 6, 15, 5, 'Carpa espaciosa e impermeable. Aguantó una lluvia ligera sin problemas.', '2026-01-14'),
(17, 6, 15, 4, 'Buena carpa, fácil de armar. Un poco pesada para cargar pero muy resistente.', '2026-01-17'),
(18, 6, 15, 5, 'Cómoda para 4 personas. Incluye todo lo necesario para acampar. Recomendada.', '2026-01-20'),

-- Objeto 16: Hidrolavadora (Usuario 6)
(19, 6, 16, 5, 'Excelente presión, limpió el patio a profundidad. Muy fácil de usar.', '2026-01-15'),
(20, 6, 16, 4, 'Buena máquina, limpia bien. La manguera podría ser más larga pero funciona.', '2026-01-18'),

-- Objeto 17: Kayak inflable (Usuario 6)
(1, 6, 17, 5, 'Aventura increíble en el lago. Estable y fácil de remar. ¡Una experiencia genial!', '2026-01-16'),
(2, 6, 17, 5, 'Resistente y cómodo. Los remos incluidos son de buena calidad. Totalmente recomendado.', '2026-01-19'),

-- Objeto 18: Silla gamer ergonómica (Usuario 7)
(3, 7, 18, 5, 'La silla más cómoda que he probado. Perfecta para estudiar largas horas.', '2026-01-14'),
(4, 7, 18, 5, 'Excelente apoyo lumbar y muy ajustable. Vale totalmente la pena rentarla.', '2026-01-17'),
(5, 7, 18, 4, 'Cómoda y funcional. El reclinable es genial, solo tiene detalles estéticos mínimos.', '2026-01-20'),

-- Objeto 19: Parlante para fiesta (Usuario 7)
(6, 7, 19, 5, '¡Suena increíble! El micrófono funcionó perfecto para karaoke. Fiesta exitosa.', '2026-01-15'),
(7, 7, 19, 5, 'Potencia espectacular, llenó todo el jardín de música. Muy recomendable.', '2026-01-18'),

-- Objeto 20: Dron básico (Usuario 7)
(8, 7, 20, 4, 'Divertido para principiantes. La cámara graba aceptable. Buena experiencia.', '2026-01-16'),
(9, 7, 20, 4, 'Fácil de volar, estable en el aire. La batería podría durar más pero está bien.', '2026-01-19'),
(10, 7, 20, 5, 'Perfecto para aprender a volar drones. Controles simples y cámara decente.', '2026-01-21'),

-- Objeto 21: Bocina Bluetooth (Usuario 8)
(11, 8, 21, 5, 'El bass es impresionante. La batería duró toda la tarde. Excelente bocina.', '2026-01-14'),
(12, 8, 21, 4, 'Buen sonido y portátil. Un poco pesada pero la calidad lo compensa.', '2026-01-17'),

-- Objeto 22: Micrófono condenser (Usuario 8)
(13, 8, 22, 5, 'Calidad de audio profesional. Perfecto para grabar podcast. Incluye todo necesario.', '2026-01-15'),
(14, 8, 22, 5, 'Captura la voz con claridad. Fácil de configurar. Muy satisfecho.', '2026-01-18'),

-- Objeto 23: Tripié profesional (Usuario 8)
(15, 8, 23, 5, 'Estable y resistente. Ajustable a cualquier altura. Ideal para fotografía.', '2026-01-16'),
(16, 8, 23, 4, 'Buen tripié, aguanta bien el peso de la cámara. Un poco pesado para transportar.', '2026-01-19'),
(17, 8, 23, 5, 'Profesional y versátil. Funcionó perfecto para grabar videos. Recomendado.', '2026-01-21'),

-- Objeto 24: Cámara Polaroid (Usuario 9)
(18, 9, 24, 5, '¡Fotos instantáneas hermosas! Fue el hit de la fiesta. Muy nostálgica.', '2026-01-14'),
(19, 9, 24, 4, 'Divertida para eventos. Las fotos salen bien, aunque el paquete de película es limitado.', '2026-01-17'),

-- Objeto 25: Guitarra acústica (Usuario 9)
(20, 9, 25, 5, 'Suena muy bien para ser guitarra de renta. Las cuerdas nuevas se sienten genial.', '2026-01-15'),
(1, 9, 25, 5, 'Perfecta para practicar. Bien afinada y cómoda de tocar. Incluye funda resistente.', '2026-01-18'),
(2, 9, 25, 4, 'Buena guitarra para principiantes. El sonido es aceptable, nada profesional pero cumple.', '2026-01-20'),

-- Objeto 26: Inflable para niños (Usuario 9)
(3, 9, 26, 5, 'Los niños se divirtieron muchísimo. Fácil de inflar y muy seguro. Excelente compra.', '2026-01-16'),
(4, 9, 26, 5, 'Inflable resistente y divertido. Hizo que la fiesta fuera inolvidable.', '2026-01-19'),

-- Objeto 27: Kit de luces LED (Usuario 10)
(5, 10, 27, 5, 'Iluminación perfecta para fotos y videos. El aro de luz es súper útil. Recomendado.', '2026-01-14'),
(6, 10, 27, 5, 'Calidad profesional. Mis fotos mejoraron notablemente. El tripié es estable.', '2026-01-17'),

-- Objeto 28: Teclado musical (Usuario 10)
(7, 10, 28, 4, 'Buen teclado para aprender. El soporte es cómodo. Algunas teclas suenan ligeramente diferente.', '2026-01-15'),
(8, 10, 28, 5, 'Ideal para practicar en casa. Todas las funciones trabajan bien. Muy contento.', '2026-01-18'),
(9, 10, 28, 4, 'Funcional y práctico. Los sonidos son aceptables, perfecto para principiantes.', '2026-01-20'),

-- Objeto 29: GoPro Hero (Usuario 10)
(10, 10, 29, 5, 'Videos de excelente calidad. Los accesorios incluidos son muy útiles. Gran experiencia.', '2026-01-16'),
(11, 10, 29, 5, 'Perfecta para aventuras extremas. Resistente al agua y fácil de usar. Recomendadísima.', '2026-01-19'),

-- Objeto 30: PlayStation 4 (Usuario 11)
(12, 11, 30, 5, 'Funciona perfecto. Pasamos horas jugando en familia. El control responde excelente.', '2026-01-14'),
(13, 11, 30, 4, 'Buena consola, aunque solo trae un control. Los juegos corren sin problemas.', '2026-01-17'),
(14, 11, 30, 5, 'PS4 en excelente estado. Sin lag ni problemas. Totalmente recomendable.', '2026-01-20'),

-- Objeto 31: Maleta de viaje (Usuario 11)
(15, 11, 31, 5, 'Maleta espaciosa y resistente. Las ruedas funcionan perfecto. Ideal para viajes largos.', '2026-01-15'),
(16, 11, 31, 4, 'Cumple su función, cabe bastante. Tiene algunas marcas de uso pero está bien.', '2026-01-18'),

-- Objeto 32: Bafle + tripié (Usuario 11)
(17, 11, 32, 5, 'Sonido potente y claro. El tripié es estable. Perfecto para eventos medianos.', '2026-01-16'),
(18, 11, 32, 5, 'Excelente equipo de audio. Fácil de transportar y configurar. Muy profesional.', '2026-01-19'),

-- Objeto 33: Nintendo Switch (Usuario 12)
(19, 12, 33, 5, '¡Nos encantó! Juegos divertidos para toda la familia. Los joycons funcionan perfecto.', '2026-01-14'),
(20, 12, 33, 5, 'Excelente consola. El dock funciona bien con TV. Muy entretenida.', '2026-01-17'),
(1, 12, 33, 4, 'Divertida y portátil. La batería podría durar más pero en general muy buena.', '2026-01-20'),

-- Objeto 34: Impresora HP (Usuario 12)
(2, 12, 34, 4, 'Imprime bien, wifi funciona correctamente. Hay que comprar tinta aparte como indica.', '2026-01-15'),
(3, 12, 34, 5, 'Perfecta para trabajos escolares. Rápida y eficiente. Muy recomendable.', '2026-01-18'),

-- Objeto 35: Sillas plegables (Usuario 12)
(4, 12, 35, 5, 'Las 6 sillas están en buen estado. Cómodas y fáciles de guardar. Perfectas para evento.', '2026-01-16'),
(5, 12, 35, 4, 'Prácticas y resistentes. Una tiene pequeño detalle pero todas funcionan bien.', '2026-01-19'),

-- Objeto 36: Control Xbox (Usuario 13)
(6, 13, 36, 5, 'Control en perfecto estado. Sin drift, todos los botones responden bien.', '2026-01-14'),
(7, 13, 36, 4, 'Buen control, funciona correctamente. Batería incluida. Muy útil.', '2026-01-17'),
(8, 13, 36, 5, 'Como nuevo, respuesta excelente. Perfecto para jugar con amigos.', '2026-01-20'),

-- Objeto 37: Router WiFi (Usuario 13)
(9, 13, 37, 4, 'Buena señal en toda la casa. Configuración sencilla. Cumple su propósito.', '2026-01-15'),
(10, 13, 37, 5, 'Router confiable y rápido. Sin cortes de conexión. Muy satisfecho.', '2026-01-18'),

-- Objeto 38: Laptop Lenovo (Usuario 14)
(11, 14, 38, 5, 'Laptop rápida para trabajo. El SSD hace la diferencia. Perfecta para clases online.', '2026-01-14'),
(12, 14, 38, 5, 'Excelente rendimiento. Corre programas sin problemas. Batería dura bastante.', '2026-01-17'),

-- Objeto 39: iPad 9a gen (Usuario 15)
(13, 15, 39, 5, 'Perfect para tomar apuntes digitales. La funda protege bien. Muy útil.', '2026-01-15'),
(14, 15, 39, 4, 'Buen iPad, pantalla clara. La batería podría durar más pero funciona genial.', '2026-01-18'),
(15, 15, 39, 5, 'Ideal para estudiantes. Rápido y fluido. La funda es de buena calidad.', '2026-01-20'),

-- Objeto 40: Microondas (Usuario 16)
(16, 16, 40, 4, 'Calienta bien y rápido. Los detalles estéticos no afectan su funcionamiento.', '2026-01-16'),
(17, 16, 40, 5, 'Funcional y práctico. Perfecto para calentar comida. Muy recomendable.', '2026-01-19');

-- =====================================================
-- PASO 7: VERIFICACIÓN Y ESTADÍSTICAS
-- =====================================================

SELECT 
    o.id_objeto,
    o.nombre_objeto,
    COUNT(i.id_imagen) AS total_imagenes,
    SUM(CASE WHEN i.es_principal = 1 THEN 1 ELSE 0 END) AS imagenes_principales,
    SUM(CASE WHEN i.es_principal = 0 THEN 1 ELSE 0 END) AS imagenes_adicionales
FROM objeto o
LEFT JOIN imagen_objeto i ON o.id_objeto = i.id_objeto
GROUP BY o.id_objeto, o.nombre_objeto
ORDER BY o.id_objeto
LIMIT 10;

-- Ver reseñas por objeto
SELECT 
    o.nombre_objeto,
    COUNT(r.id_resena) as total_resenas,
    AVG(r.calificacion) as promedio_calificacion
FROM objeto o
LEFT JOIN resena r ON o.id_objeto = r.id_objeto
GROUP BY o.id_objeto, o.nombre_objeto
HAVING COUNT(r.id_resena) > 0
ORDER BY promedio_calificacion DESC;


-- Estadísticas generales
SELECT 
    'Total Usuarios' as metrica,
    COUNT(*) as valor
FROM usuario
UNION ALL
SELECT 
    'Total Objetos',
    COUNT(*)
FROM objeto
UNION ALL
SELECT 
    'Total Reseñas',
    COUNT(*)
FROM resena
UNION ALL
SELECT 
    'Promedio Reseñas por Objeto',
    ROUND(COUNT(*) / (SELECT COUNT(*) FROM objeto), 2)
FROM resena;

SET FOREIGN_KEY_CHECKS = 1;
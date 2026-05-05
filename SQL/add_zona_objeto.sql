-- ✅ Agregar columna zona a la tabla objeto
-- Ejecutar este script en tu base de datos MySQL

ALTER TABLE objeto
ADD COLUMN IF NOT EXISTS zona VARCHAR(120) NULL AFTER imagen_url;

-- Verificar que se agregó correctamente
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'objeto' AND COLUMN_NAME = 'zona';

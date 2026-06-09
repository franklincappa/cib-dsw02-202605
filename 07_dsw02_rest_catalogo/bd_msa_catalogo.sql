CREATE DATABASE db_msa_catalogo;
GO

USE db_msa_catalogo;
GO

-- Tabla categorias

CREATE TABLE categorias (
    id              BIGINT        IDENTITY(1,1) PRIMARY KEY,
    nombre          NVARCHAR(100) NOT NULL,
    descripcion     NVARCHAR(500) NULL,
    activo          BIT           NOT NULL DEFAULT 1,
    fecha_creacion  DATETIME2     NOT NULL DEFAULT GETDATE()
);
GO

-- Tabla marcas

CREATE TABLE marcas (
    id              BIGINT        IDENTITY(1,1) PRIMARY KEY,
    nombre          NVARCHAR(100) NOT NULL,
    descripcion     NVARCHAR(500) NULL,
    pais_origen     NVARCHAR(100) NULL,
    activo          BIT           NOT NULL DEFAULT 1,
    fecha_creacion  DATETIME2     NOT NULL DEFAULT GETDATE()
);
GO

-- Tabla productos

CREATE TABLE productos (
    id                  BIGINT        IDENTITY(1,1) PRIMARY KEY,
    categoria_id        BIGINT        NULL,
    marca_id            BIGINT        NULL,
    codigo_sku          NVARCHAR(50)  NOT NULL UNIQUE,
    nombre              NVARCHAR(100) NOT NULL,
    descripcion         NVARCHAR(500) NULL,
    precio              DECIMAL(10,2) NOT NULL,
    activo              BIT           NOT NULL DEFAULT 1,
    fecha_creacion      DATETIME2     NOT NULL DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2     NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_productos_categorias FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    CONSTRAINT FK_productos_marcas     FOREIGN KEY (marca_id)     REFERENCES marcas(id)
);
GO


-- Tabla inventario (1 a 1 con producto)

CREATE TABLE inventario (
    id                  BIGINT    IDENTITY(1,1) PRIMARY KEY,
    producto_id         BIGINT    NOT NULL UNIQUE,
    stock_actual        INT       NOT NULL DEFAULT 0,
    stock_minimo        INT       NOT NULL DEFAULT 0,
    stock_maximo        INT       NOT NULL DEFAULT 9999,
    fecha_actualizacion DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_inventario_productos FOREIGN KEY (producto_id) REFERENCES productos(id)
);
GO

-- Datos

INSERT INTO categorias (nombre, descripcion) VALUES
    ('Laptops y Computadoras', 'Equipos de cómputo portátiles y de escritorio'),
    ('Periféricos',            'Mouse, teclados, webcams y accesorios'),
    ('Monitores',              'Pantallas y monitores para computadora'),
    ('Audio',                  'Auriculares, parlantes y accesorios de sonido'),
    ('Almacenamiento',         'SSD, HDD y memorias RAM'),
    ('Accesorios',             'Hubs, soportes y otros accesorios');
GO

INSERT INTO marcas (nombre, descripcion, pais_origen) VALUES
    ('Dell',      'Fabricante de equipos de cómputo',         'Estados Unidos'),
    ('Logitech',  'Periféricos y accesorios para computadora', 'Suiza'),
    ('HyperX',    'Periféricos gaming de alto rendimiento',    'Estados Unidos'),
    ('LG',        'Electrónica de consumo y monitores',        'Corea del Sur'),
    ('Sony',      'Electrónica y audio de consumo',            'Japón'),
    ('Samsung',   'Electrónica y almacenamiento',              'Corea del Sur'),
    ('Corsair',   'Hardware para entusiastas y gaming',        'Estados Unidos');
GO

INSERT INTO productos (categoria_id, marca_id, codigo_sku, nombre, descripcion, precio, activo) VALUES
    (1, 1, 'LAP-DELL-001', 'Laptop Dell Inspiron 15',    'Intel i5, 8GB RAM, 256GB SSD',            2500.00, 1),
    (2, 2, 'MOU-LOGI-001', 'Mouse Logitech MX Master 3', 'Mouse inalámbrico ergonómico',             180.00, 1),
    (2, 3, 'TEC-HYPX-001', 'Teclado HyperX Alloy',       'Teclado mecánico RGB, switches rojos',    350.00, 1),
    (3, 4, 'MON-LG24-001', 'Monitor LG 24MK600',         'IPS 24", Full HD, 75Hz',                  850.00, 1),
    (4, 5, 'AUR-SONY-001', 'Auriculares Sony WH-1000XM5','Cancelación de ruido activa',             750.00, 1),
    (2, 2, 'WEB-LOGI-001', 'Webcam Logitech C920',        'Full HD 1080p, micrófono estéreo',        320.00, 1),
    (5, 6, 'SSD-SAMS-001', 'SSD Samsung 970 EVO 1TB',    'NVMe M.2, lectura 3500MB/s',              450.00, 1),
    (5, 7, 'RAM-CORS-001', 'RAM Corsair Vengeance 16GB',  'DDR4 3200MHz, kit 2x8GB',                280.00, 1),
    (6, 2, 'HUB-LOGI-001', 'Hub USB-C Logitech 7 en 1',  'HDMI 4K, USB 3.0, SD, carga 100W',       150.00, 1),
    (6, 1, 'SOP-DELL-001', 'Laptop Stand Dell',           'Soporte aluminio ajustable 10-17"',        90.00, 0);
GO

-- Inventario (1 por producto)

INSERT INTO inventario (producto_id, stock_actual, stock_minimo, stock_maximo) VALUES
    (1, 15, 5,  50),
    (2, 40, 10, 100),
    (3, 25, 5,  60),
    (4, 10, 3,  30),
    (5,  8, 2,  20),
    (6, 20, 5,  50),
    (7, 30, 10, 80),
    (8, 18, 5,  50),
    (9,  5, 2,  30),
    (10, 3, 0,  20);
GO

-- Listado de productos
SELECT p.codigo_sku, p.nombre, c.nombre AS categoria,
       m.nombre AS marca, i.stock_actual, i.stock_minimo
FROM productos p
LEFT JOIN categorias c  ON p.categoria_id = c.id
LEFT JOIN marcas m      ON p.marca_id     = m.id
LEFT JOIN inventario i  ON i.producto_id  = p.id;
GO


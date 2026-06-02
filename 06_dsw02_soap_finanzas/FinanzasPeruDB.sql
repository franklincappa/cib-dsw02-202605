-- ============================================================
--  FinanzasPeruDB - Script de Base de Datos
--  Motor: SQL Server 2019+
--  Sistema: Gestion Financiera - FinanzasPeru S.A.
-- ============================================================

USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'FinanzasPeruDB')
    CREATE DATABASE FinanzasPeruDB;
GO

USE FinanzasPeruDB;
GO

IF OBJECT_ID('TIPOS_CLIENTE', 'U') IS NOT NULL DROP TABLE TIPOS_CLIENTE;
GO
CREATE TABLE TIPOS_CLIENTE (
    id_tipo_cliente   INT           IDENTITY(1,1) PRIMARY KEY,
    codigo            VARCHAR(10)   NOT NULL UNIQUE,
    descripcion       VARCHAR(60)   NOT NULL,
    limite_credito    DECIMAL(15,2) DEFAULT 0.00,
    tasa_preferencial BIT           DEFAULT 0,
    activo            BIT           DEFAULT 1
);
GO

IF OBJECT_ID('CLIENTES', 'U') IS NOT NULL DROP TABLE CLIENTES;
GO
CREATE TABLE CLIENTES (
    id_cliente        BIGINT        IDENTITY(1,1) PRIMARY KEY,
    tipo_documento    VARCHAR(3)    NOT NULL CHECK (tipo_documento IN ('DNI','RUC','CE')),
    nro_documento     VARCHAR(15)   NOT NULL UNIQUE,
    nombres           VARCHAR(100)  NOT NULL,
    apellido_paterno  VARCHAR(60)   NOT NULL,
    apellido_materno  VARCHAR(60),
    fecha_nacimiento  DATE,
    direccion         VARCHAR(200)  NOT NULL,
    telefono          VARCHAR(15)   NOT NULL,
    email             VARCHAR(100)  NOT NULL UNIQUE,
    tipo_cliente      VARCHAR(20)   NOT NULL DEFAULT 'NATURAL'
                                    CHECK (tipo_cliente IN ('NATURAL','JURIDICO')),
    estado            VARCHAR(10)   NOT NULL DEFAULT 'ACTIVO'
                                    CHECK (estado IN ('ACTIVO','INACTIVO','BLOQUEADO')),
    fecha_registro    DATETIME      NOT NULL DEFAULT GETDATE(),
    id_tipo_cliente_ref INT         REFERENCES TIPOS_CLIENTE(id_tipo_cliente)
);
GO


IF OBJECT_ID('PRODUCTOS_FINANCIEROS', 'U') IS NOT NULL DROP TABLE PRODUCTOS_FINANCIEROS;
GO
CREATE TABLE PRODUCTOS_FINANCIEROS (
    id_producto       INT           IDENTITY(1,1) PRIMARY KEY,
    codigo            VARCHAR(10)   NOT NULL UNIQUE,
    nombre            VARCHAR(80)   NOT NULL,
    tipo_producto     VARCHAR(30)   NOT NULL
                                    CHECK (tipo_producto IN ('CUENTA_AHORRO','CUENTA_CORRIENTE',
                                           'PRESTAMO_PERSONAL','PRESTAMO_HIPOTECARIO',
                                           'TARJETA_CREDITO','DEPOSITO_PLAZO')),
    tasa_interes      DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    moneda            CHAR(3)       NOT NULL DEFAULT 'PEN' CHECK (moneda IN ('PEN','USD')),
    monto_minimo      DECIMAL(15,2) DEFAULT 0.00,
    activo            BIT           DEFAULT 1,
    descripcion       VARCHAR(255)
);
GO


IF OBJECT_ID('CUENTAS', 'U') IS NOT NULL DROP TABLE CUENTAS;
GO
CREATE TABLE CUENTAS (
    id_cuenta         BIGINT        IDENTITY(1,1) PRIMARY KEY,
    nro_cuenta        VARCHAR(20)   NOT NULL UNIQUE,
    id_cliente        BIGINT        NOT NULL REFERENCES CLIENTES(id_cliente),
    id_producto       INT           NOT NULL REFERENCES PRODUCTOS_FINANCIEROS(id_producto),
    saldo             DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    moneda            CHAR(3)       NOT NULL DEFAULT 'PEN',
    estado            VARCHAR(15)   NOT NULL DEFAULT 'ACTIVA'
                                    CHECK (estado IN ('ACTIVA','INACTIVA','BLOQUEADA','CERRADA')),
    fecha_apertura    DATE          NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    fecha_cierre      DATE
);
GO


IF OBJECT_ID('TRANSACCIONES', 'U') IS NOT NULL DROP TABLE TRANSACCIONES;
GO
CREATE TABLE TRANSACCIONES (
    id_transaccion    BIGINT        IDENTITY(1,1) PRIMARY KEY,
    id_cuenta         BIGINT        NOT NULL REFERENCES CUENTAS(id_cuenta),
    tipo_operacion    VARCHAR(25)   NOT NULL
                                    CHECK (tipo_operacion IN ('DEPOSITO','RETIRO',
                                           'TRANSFERENCIA_ENTRADA','TRANSFERENCIA_SALIDA',
                                           'PAGO_SERVICIO','COBRO_COMISION')),
    monto             DECIMAL(15,2) NOT NULL,
    saldo_anterior    DECIMAL(15,2) NOT NULL,
    saldo_posterior   DECIMAL(15,2) NOT NULL,
    descripcion       VARCHAR(200),
    canal             VARCHAR(20)   DEFAULT 'WEB'
                                    CHECK (canal IN ('WEB','APP','VENTANILLA','ATM')),
    fecha_operacion   DATETIME      NOT NULL DEFAULT GETDATE(),
    nro_referencia    VARCHAR(30)   UNIQUE
);
GO


IF OBJECT_ID('PRESTAMOS', 'U') IS NOT NULL DROP TABLE PRESTAMOS;
GO
CREATE TABLE PRESTAMOS (
    id_prestamo       BIGINT        IDENTITY(1,1) PRIMARY KEY,
    id_cliente        BIGINT        NOT NULL REFERENCES CLIENTES(id_cliente),
    id_producto       INT           NOT NULL REFERENCES PRODUCTOS_FINANCIEROS(id_producto),
    monto_aprobado    DECIMAL(15,2) NOT NULL,
    saldo_pendiente   DECIMAL(15,2) NOT NULL,
    tasa_interes      DECIMAL(5,2)  NOT NULL,
    plazo_meses       INT           NOT NULL,
    cuota_mensual     DECIMAL(15,2) NOT NULL,
    fecha_desembolso  DATE          NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    fecha_vencimiento DATE          NOT NULL,
    estado            VARCHAR(15)   NOT NULL DEFAULT 'VIGENTE'
                                    CHECK (estado IN ('VIGENTE','CANCELADO','EN_MORA','REFINANCIADO')),
    moneda            CHAR(3)       NOT NULL DEFAULT 'PEN'
);
GO




INSERT INTO TIPOS_CLIENTE (codigo, descripcion, limite_credito, tasa_preferencial) VALUES
    ('VIP',    'Cliente VIP Premium',          500000.00, 1),
    ('PLATA',  'Cliente Segmento Plata',        150000.00, 1),
    ('BRONCE', 'Cliente Segmento Bronce',        50000.00, 0),
    ('PYME',   'Pequeña y Mediana Empresa',     200000.00, 1),
    ('CORP',   'Corporativo',                  1000000.00, 1);
GO


INSERT INTO PRODUCTOS_FINANCIEROS (codigo, nombre, tipo_producto, tasa_interes, moneda, monto_minimo, descripcion) VALUES
    ('AH001', 'Cuenta Ahorro Clasica',         'CUENTA_AHORRO',         2.50, 'PEN', 50.00,    'Cuenta de ahorro con tasa anual 2.5%'),
    ('AH002', 'Cuenta Ahorro Dolar',           'CUENTA_AHORRO',         1.80, 'USD', 50.00,    'Cuenta de ahorro en dolares'),
    ('CC001', 'Cuenta Corriente Empresarial',  'CUENTA_CORRIENTE',      0.00, 'PEN', 500.00,   'Cuenta corriente para empresas'),
    ('PP001', 'Prestamo Personal Express',     'PRESTAMO_PERSONAL',    18.50, 'PEN', 1000.00,  'Credito personal hasta 12 meses'),
    ('PH001', 'Credito Hipotecario',           'PRESTAMO_HIPOTECARIO',  9.25, 'PEN', 50000.00, 'Credito para vivienda hasta 20 años'),
    ('TC001', 'Tarjeta de Credito Gold',       'TARJETA_CREDITO',      28.00, 'PEN', 0.00,     'Tarjeta con linea hasta S/ 20,000'),
    ('DP001', 'Deposito a Plazo Fijo 90 dias', 'DEPOSITO_PLAZO',        5.50, 'PEN', 1000.00,  'Inversion a 90 dias con tasa garantizada');
GO

INSERT INTO CLIENTES (tipo_documento, nro_documento, nombres, apellido_paterno, apellido_materno,
                      fecha_nacimiento, direccion, telefono, email, tipo_cliente, estado, id_tipo_cliente_ref) VALUES
    ('DNI','70123456','Carlos Andres',    'Quispe',    'Mamani',  '1985-03-12','Av. Arequipa 1250, Miraflores, Lima',   '987654321','carlos.quispe@email.com',   'NATURAL',  'ACTIVO',   1),
    ('DNI','72345678','Maria Elena',      'Torres',    'Huanca',  '1990-07-22','Jr. Lampa 456, Cercado de Lima',         '976543210','maria.torres@email.com',    'NATURAL',  'ACTIVO',   2),
    ('DNI','68901234','Jorge Luis',       'Ramirez',   'Chávez',  '1978-11-05','Av. La Marina 890, San Miguel, Lima',    '965432109','jorge.ramirez@email.com',   'NATURAL',  'ACTIVO',   2),
    ('DNI','75432198','Ana Lucia',        'Flores',    'Paredes', '1995-02-18','Calle Los Pinos 234, Surco, Lima',       '954321098','ana.flores@email.com',      'NATURAL',  'ACTIVO',   3),
    ('DNI','63210987','Pedro Antonio',    'Gutierrez', 'Soto',    '1972-09-30','Av. Universitaria 567, Los Olivos',      '943210987','pedro.gutierrez@email.com', 'NATURAL',  'ACTIVO',   3),
    ('RUC','20501234567','',              'Inversiones',' Andinas S.A.C.', NULL,'Av. Javier Prado Este 3360, San Isidro','01-4567890','contacto@inversionesandinas.pe','JURIDICO','ACTIVO',4),
    ('RUC','20609876543','',              'Tech',      'Solutions Peru S.R.L.',NULL,'Jr. Camaná 789, Cercado de Lima',   '01-3456789','info@techsolutionsperu.com','JURIDICO', 'ACTIVO',   4),
    ('DNI','71098765','Lucia Fernanda',   'Mendoza',   'Cruz',    '1988-06-14','Av. Petit Thouars 1890, Lince',         '932109876','lucia.mendoza@email.com',   'NATURAL',  'ACTIVO',   1),
    ('CE','002345678','John Michael',     'Smith',     NULL,       '1983-04-25','Av. Salaverry 3450, Jesus Maria',       '921098765','john.smith@email.com',      'NATURAL',  'ACTIVO',   1),
    ('DNI','69876543','Roberto Carlos',   'Vargas',    'Leon',    '1965-12-08','Jr. Ancash 1230, Rimac, Lima',          '910987654','roberto.vargas@email.com',  'NATURAL',  'BLOQUEADO',3);
GO


INSERT INTO CUENTAS (nro_cuenta, id_cliente, id_producto, saldo, moneda, estado, fecha_apertura) VALUES
    ('001-123456-00-01', 1, 1,  15420.50, 'PEN', 'ACTIVA',  '2020-03-15'),
    ('001-123456-00-02', 1, 2,   3200.00, 'USD', 'ACTIVA',  '2021-06-10'),
    ('001-234567-00-01', 2, 1,   8750.00, 'PEN', 'ACTIVA',  '2019-11-20'),
    ('001-345678-00-01', 3, 1,  42000.00, 'PEN', 'ACTIVA',  '2018-05-08'),
    ('001-456789-00-01', 4, 1,   1200.00, 'PEN', 'ACTIVA',  '2022-01-14'),
    ('002-567890-00-01', 6, 3, 250000.00, 'PEN', 'ACTIVA',  '2017-09-01'),
    ('002-678901-00-01', 7, 3,  98000.00, 'PEN', 'ACTIVA',  '2019-04-22'),
    ('001-789012-00-01', 8, 1,  56780.90, 'PEN', 'ACTIVA',  '2016-08-30'),
    ('001-890123-00-01', 9, 2,  12500.00, 'USD', 'ACTIVA',  '2020-12-05'),
    ('001-901234-00-01',10, 1,    250.00, 'PEN', 'BLOQUEADA','2021-03-18');
GO


INSERT INTO PRESTAMOS (id_cliente, id_producto, monto_aprobado, saldo_pendiente,
                       tasa_interes, plazo_meses, cuota_mensual,
                       fecha_desembolso, fecha_vencimiento, estado) VALUES
    (1, 4,  25000.00, 18500.00, 18.50, 24,  1312.00, '2023-01-10', '2025-01-10', 'VIGENTE'),
    (2, 4,  10000.00,  6200.00, 18.50, 12,   920.00, '2023-06-01', '2024-06-01', 'VIGENTE'),
    (3, 5, 180000.00,165000.00,  9.25,240,  1650.00, '2022-03-15', '2042-03-15', 'VIGENTE'),
    (8, 5, 320000.00,298000.00,  9.25,240,  2940.00, '2021-07-20', '2041-07-20', 'VIGENTE'),
    (5, 4,   8000.00,  8000.00, 18.50, 12,   736.00, '2024-01-05', '2025-01-05', 'EN_MORA');
GO


INSERT INTO TRANSACCIONES (id_cuenta, tipo_operacion, monto, saldo_anterior, saldo_posterior, descripcion, canal, nro_referencia) VALUES
    (1,'DEPOSITO',        5000.00, 10420.50, 15420.50,'Deposito en ventanilla','VENTANILLA','TXN-2024-001001'),
    (1,'RETIRO',          2000.00, 15420.50, 13420.50,'Retiro cajero automatico','ATM',      'TXN-2024-001002'),
    (1,'DEPOSITO',        4000.00, 13420.50, 17420.50,'Transferencia recibida', 'WEB',       'TXN-2024-001003'),
    (3,'DEPOSITO',        3000.00,  5750.00,  8750.00,'Deposito nomina',       'WEB',        'TXN-2024-002001'),
    (3,'PAGO_SERVICIO',    450.00,  8750.00,  8300.00,'Pago luz y agua',       'APP',        'TXN-2024-002002'),
    (4,'TRANSFERENCIA_ENTRADA',8000.00,34000.00,42000.00,'Trans. recibida',   'WEB',         'TXN-2024-003001'),
    (6,'DEPOSITO',       50000.00,200000.00,250000.00,'Deposito corporativo',  'VENTANILLA', 'TXN-2024-004001'),
    (8,'COBRO_COMISION',     25.00, 56805.90, 56780.90,'Mantenimiento cuenta', 'WEB',        'TXN-2024-005001');
GO

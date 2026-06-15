create database Proyecto_PuntoyComa;
use Proyecto_PuntoyComa;

create table empleados (
Cod_empleado int auto_increment,
Id_empleado varchar(30) UNIQUE,
nss VARCHAR(20) UNIQUE NOT NULL,
fecha_nacimiento DATE NOT NULL,
Nombre VARCHAR(50),
Email VARCHAR(50),
Telefono varchar(15),
estado VARCHAR(20) DEFAULT 'Activo',
Cargo varchar(30),
tipo_contrato VARCHAR(30),
fecha_antiguedad DATE DEFAULT (CURRENT_DATE),
fecha_despido DATE,
constraint PK_empleados primary key (Cod_empleado),
CONSTRAINT UQ_Empleado UNIQUE (Id_empleado),
CONSTRAINT CHK_Estado_Empleado CHECK (estado IN ('Activo', 'Baja Enfermedad', 'Excedencia', 'Inactivo')),
CONSTRAINT CHK_Fecha_Coherente CHECK (fecha_despido IS NULL OR fecha_despido >= fecha_antiguedad)
);

-- Empleados Iniciales
-- Empleado 1: Administrador
INSERT INTO empleados (Id_empleado, nss, fecha_nacimiento, Nombre, Email, Telefono, estado, Cargo, tipo_contrato, fecha_antiguedad) VALUES ('EMP-001', 'NSS-111111111', '1985-04-12', 'Carlos Mendoza', 'carlos.mendoza@empresa.com', '600111222', 'Activo', 'Administrador', 'Indefinido', '2020-01-15');
-- Empleado 2: Encargado Compras
INSERT INTO empleados (Id_empleado, nss, fecha_nacimiento, Nombre, Email, Telefono, estado, Cargo, tipo_contrato, fecha_antiguedad) VALUES ('EMP-002', 'NSS-222222222', '1990-08-23', 'Ana Gómez', 'ana.gomez@empresa.com', '600333444', 'Activo', 'Encargado Compras', 'Indefinido', '2022-03-01');
-- Empleado 3: Encargado Reservas
INSERT INTO empleados (Id_empleado, nss, fecha_nacimiento, Nombre, Email, Telefono, estado, Cargo, tipo_contrato, fecha_antiguedad) VALUES ('EMP-003', 'NSS-333333333', '1995-11-05', 'Luis Martínez', 'luis.martinez@empresa.com', '600555666', 'Activo', 'Encargado Reservas', 'Temporal', '2023-06-10');


create table Usuarios (
id_user int auto_increment,
nom_user varchar(30) unique,
contrasenya varchar(20),
empleado int unique,
constraint PK_USUARIOS primary key (id_user),
constraint FK_Usuarios_empleados foreign key (empleado) references empleados(Cod_empleado));

-- Usuario para el Administrador
INSERT INTO Usuarios (nom_user, contrasenya, empleado) VALUES ('Admin', '1234', 1);
-- Usuario para el Encargado de Compras
INSERT INTO Usuarios (nom_user, contrasenya, empleado) VALUES ('Compras', '1234', 2);
-- Usuario para el Encargado de Reservas
INSERT INTO Usuarios (nom_user, contrasenya, empleado) VALUES ('Reservas', '1234', 3);

CREATE TABLE Salarios (
    id_salario SERIAL,
    Cod_empleado INT,
    mes_periodo VARCHAR(15) NOT NULL,
    salario_base DECIMAL(10,2) NOT NULL,
    incentivos_productividad DECIMAL(10,2) DEFAULT 0,
    comisiones DECIMAL(10,2) DEFAULT 0,

    CONSTRAINT PK_Salarios PRIMARY KEY (id_salario),
    
    CONSTRAINT FK_Salarios_Empleado FOREIGN KEY (Cod_empleado) 
	REFERENCES Empleados(Cod_empleado) ON DELETE CASCADE,
    CONSTRAINT CHK_Salario_Minimo CHECK (salario_base > 0),
    CONSTRAINT CHK_Variables_No_Negativas CHECK (incentivos_productividad >= 0 AND comisiones >= 0)
);

INSERT INTO Salarios (Cod_empleado, mes_periodo, salario_base, incentivos_productividad, comisiones)
VALUES 
(1, 'Enero 2026', 2500.00, 300.00, 0.00),   -- Administrador
(2, 'Enero 2026', 1800.00, 150.00, 50.00),  -- Encargado Compras
(3, 'Enero 2026', 1600.00, 100.00, 120.00); -- Encargado Reservas

CREATE TABLE Registro_Jornada (
    id_registro SERIAL,
    Cod_empleado INT,
    fecha DATE DEFAULT (CURRENT_DATE),
    hora_entrada TIME DEFAULT (CURRENT_TIME),
    hora_salida TIME,
    actividad_realizada TEXT,
    evaluacion_desempeño INT,
    CONSTRAINT PK_Registro_Jornada PRIMARY KEY (id_registro),
    CONSTRAINT FK_Jornada_Empleado FOREIGN KEY (Cod_empleado) 
        REFERENCES Empleados(Cod_empleado) ON DELETE CASCADE,
    CONSTRAINT CHK_Horas_Ordenadas CHECK (hora_salida IS NULL OR hora_salida > hora_entrada),
    CONSTRAINT CHK_Rango_Evaluacion CHECK (evaluacion_desempeño BETWEEN 1 AND 10)
);

-- Registros de jornada.
INSERT INTO Registro_Jornada (Cod_empleado, fecha, hora_entrada, hora_salida, actividad_realizada, evaluacion_desempeño) VALUES 
(1, '2026-06-01', '08:00:00', '16:00:00', 'Revisión de cuentas anuales y gestión de altas.', 9),
(2, '2026-06-01', '09:00:00', '17:00:00', 'Negociación con proveedores de suministros.', 8),
(3, '2026-06-01', '08:30:00', '16:30:00', 'Gestión de reservas overbooking y atención al cliente.', 7);

create table Proveedores (
Cod_proveedor int auto_increment,
Id_proveedor varchar(30),
Telefono varchar(15),
Nombre varchar(30),
Email varchar(50),
estado VARCHAR(20) DEFAULT 'Activo',
CONSTRAINT PK_Proveedores primary key (Cod_proveedor),
CONSTRAINT UQ_Proveedores UNIQUE (Id_proveedor),
CONSTRAINT CHK_Estado_Proveedores CHECK (estado IN ('Activo', 'Inactivo'))
);

-- Proveedores Iniciales
INSERT INTO Proveedores (Id_proveedor, Telefono, Nombre, Email, estado) VALUES 
('PROV-001', '911223344', 'Distribuciones Globales S.L.', 'contacto@distglobal.com', 'Activo'),
('PROV-002', '933445566', 'Tecnología y Suministros Norte', 'ventas@tecnonorte.com', 'Activo');

create table Productos (
ID_producto int auto_increment,
Nombre varchar (30),
Stock int,
Stock_minimo int,
Descripcion varchar(100),
Proveedor int NOT NULL,
Precio_unidad double,
estado VARCHAR(20) DEFAULT 'Activo',
CONSTRAINT PK_Productos primary key (ID_producto),
CONSTRAINT FK_Producto_Proveedor foreign key (Proveedor) references proveedores (Cod_proveedor) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT CHK_Estado_Producto CHECK (estado IN ('Activo', 'Inactivo'))
);

-- Productos Iniciales
INSERT INTO Productos (Nombre, Stock, Stock_minimo, Descripcion, Proveedor, Precio_unidad, estado) VALUES 
('Papel de Impresora A4', 100, 20, 'Paquete de 500 hojas 80g', 1, 4.50, 'Activo'),
('Ratón Óptico USB', 50, 10, 'Ratón básico para oficina', 2, 8.99, 'Activo');

create table Orden_Compra (
No_orden int auto_increment,
Fecha date DEFAULT (current_date),
Direccion varchar(50),
Telefono varchar(15),
Precio_total double,
Empleado int NOT NULL,
Proveedor int NOT NULL,
Estado varchar(15) DEFAULT 'Pendiente',
CONSTRAINT PK_Orden_compra PRIMARY KEY (No_orden),
CONSTRAINT FK_Orden_empleado FOREIGN KEY (Empleado) references empleados(Cod_empleado) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT FK_Orden_proveedor FOREIGN KEY (Proveedor) references proveedores(Cod_proveedor) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT CHK_Estado_Pedido CHECK (estado IN ('Pendiente', 'Confirmado', 'En preparación', 'Enviado', 'Entregado', 'Cancelado', 'En incidencia', 'Devuelto')));

-- Ordenes de compra
INSERT INTO Orden_Compra (Fecha, Direccion, Telefono, Precio_total, Empleado, Proveedor, Estado) VALUES 
('2026-06-02', 'Calle Mayor 45, Planta 1', '600333444', 45.00, 2, 1, 'Confirmado'),
('2026-06-03', 'Calle Mayor 45, Planta 1', '600333444', 89.90, 2, 2, 'Pendiente');


create table Lineas_Compra (
No_compra int NOT NULL,
No_linea int NOT NULL,
Cantidad int,
Producto int,
CONSTRAINT PK_Lineas_Compra primary key (No_linea, No_compra),
CONSTRAINT FK_Lineas_Orden foreign key (No_compra) references Orden_Compra(No_orden) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT FK_Linea_Producto foreign key (Producto) references Productos(ID_producto) ON DELETE SET NULL ON UPDATE CASCADE) ;

-- Lineas de compra para las ordenes
INSERT INTO Lineas_Compra (No_compra, No_linea, Cantidad, Producto) VALUES (1, 1, 10, 1);
INSERT INTO Lineas_Compra (No_compra, No_linea, Cantidad, Producto) VALUES (2, 1, 10, 2);

-- 1. Tabla CLIENTES
CREATE TABLE clientes (
    cod INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    identificador VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(20),
    estado VARCHAR(20) DEFAULT 'Activo',
    CONSTRAINT PK_Clientes PRIMARY KEY (cod),
    CONSTRAINT UQ_Clientes_DNI UNIQUE (identificador)
);

INSERT INTO clientes (nombre, identificador, email, telefono) VALUES
('Carlos Martínez López',   '12345678A', 'carlos.martinez@gmail.com', '611223344'),
('Laura Sánchez Ruiz',      '87654321B', 'laura.sanchez@hotmail.com', '622334455'),
('Miguel Fernández Torres', '11223344C', 'miguel.fernandez@yahoo.es', '633445566');

-- 2. Tabla ALOJAMIENTOS
CREATE TABLE alojamientos (
    cod INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    iva DECIMAL(4,2) NOT NULL,
    capacidad INT NOT NULL,
    tipo_alojamiento VARCHAR(50) NOT NULL,
    estado VARCHAR(30) DEFAULT 'Disponible',
    CONSTRAINT PK_Alojamientos PRIMARY KEY (cod)
);

INSERT INTO alojamientos (nombre, precio_base, iva, capacidad, tipo_alojamiento, estado) VALUES
('Suite Mediterránea', 180.00, 0.1, 2, 'Suite',      'Disponible'),
('Habitación Jardín',   95.00, 0.1, 2, 'Habitación', 'Disponible'),
('Villa del Bosque',   320.00, 0.1, 6, 'Villa',      'Disponible');

-- 3. Tabla ACTIVIDADES
CREATE TABLE actividades (
    cod INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    iva DECIMAL(4,2) NOT NULL,
    capacidad INT NOT NULL,
    hora_inicio TIME,
    hora_fin TIME,
    estado VARCHAR(30) DEFAULT 'Disponible',
    CONSTRAINT PK_Actividades PRIMARY KEY (cod)
);

INSERT INTO actividades (nombre, precio_base, iva, capacidad, hora_inicio, hora_fin, estado) VALUES
('Montar a Caballo',        35.00, 0.21, 10, '09:00:00', '10:30:00', 'Disponible'),
('Montar a Caballo',        35.00, 0.21, 10, '12:00:00', '13:30:00', 'Disponible'),
('Montar a Caballo',        35.00, 0.21, 10, '17:00:00', '18:30:00', 'Disponible'),
('Yoga al Aire Libre',      20.00, 0.21, 15, '08:00:00', '09:00:00', 'Disponible'),
('Yoga al Aire Libre',      20.00, 0.21, 15, '11:00:00', '12:00:00', 'Disponible'),
('Yoga al Aire Libre',      20.00, 0.21, 15, '18:00:00', '19:00:00', 'Disponible'),
('Senderismo por la Sierra',25.00, 0.21, 20, '07:30:00', '11:00:00', 'Disponible'),
('Senderismo por la Sierra',25.00, 0.21, 20, '10:00:00', '13:30:00', 'Disponible'),
('Senderismo por la Sierra',25.00, 0.21, 20, '16:00:00', '19:30:00', 'Disponible');

-- 4. Tabla SALAS_EVENTO
CREATE TABLE salas_evento (
    cod INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    iva DECIMAL(4,2) NOT NULL,
    capacidad INT NOT NULL,
    hora_inicio TIME,
    hora_fin TIME,
    estado VARCHAR(30) DEFAULT 'Disponible',
    CONSTRAINT PK_SalasEvento PRIMARY KEY (cod)
);
INSERT INTO salas_evento (nombre, precio_base, iva, capacidad, hora_inicio, hora_fin, estado) VALUES
('Salón Real',           500.00, 0.21, 100, '09:00:00', '13:00:00', 'Disponible'),
('Salón Real',           500.00, 0.21, 100, '14:00:00', '18:00:00', 'Disponible'),
('Salón Real',           500.00, 0.21, 100, '19:00:00', '23:00:00', 'Disponible'),
('Sala Jardín Botánico', 300.00, 0.21,  50, '09:00:00', '12:00:00', 'Disponible'),
('Sala Jardín Botánico', 300.00, 0.21,  50, '13:00:00', '16:00:00', 'Disponible'),
('Sala Jardín Botánico', 300.00, 0.21,  50, '17:00:00', '20:00:00', 'Disponible'),
('Terraza Panorámica',   400.00, 0.21,  75, '10:00:00', '13:00:00', 'Disponible'),
('Terraza Panorámica',   400.00, 0.21,  75, '15:00:00', '18:00:00', 'Disponible'),
('Terraza Panorámica',   400.00, 0.21,  75, '20:00:00', '23:00:00', 'Disponible');

-- 5. Tabla RESERVAS
CREATE TABLE reservas (
    cod INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(30) DEFAULT 'Alta',
    tipo_recurso ENUM('ALOJAMIENTO', 'ACTIVIDAD', 'SALA') NOT NULL,
    CONSTRAINT FK_Reservas_Clientes FOREIGN KEY (id_cliente) REFERENCES clientes(cod)
);

-- Puente para Alojamientos
CREATE TABLE reserva_alojamiento (
    cod_reserva INT PRIMARY KEY,
    id_alojamiento INT NOT NULL,
    CONSTRAINT FK_RA_Reserva FOREIGN KEY (cod_reserva) REFERENCES reservas(cod) ON DELETE CASCADE,
    CONSTRAINT FK_RA_Alojamiento FOREIGN KEY (id_alojamiento) REFERENCES alojamientos(cod)
);
INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES
(1, '2025-07-01', '2025-07-05', 'Alta', 'ALOJAMIENTO');
INSERT INTO reserva_alojamiento (cod_reserva, id_alojamiento) VALUES (1, 1);

INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES
(2, '2025-07-03', '2025-07-07', 'Alta', 'ALOJAMIENTO');
INSERT INTO reserva_alojamiento (cod_reserva, id_alojamiento) VALUES (2, 2);

INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES
(3, '2025-07-10', '2025-07-15', 'Alta', 'ALOJAMIENTO');
INSERT INTO reserva_alojamiento (cod_reserva, id_alojamiento) VALUES (3, 3);

-- Puente para Actividades
CREATE TABLE reserva_actividad (
    cod_reserva INT PRIMARY KEY,
    id_actividad INT NOT NULL,
    CONSTRAINT FK_RACT_Reserva FOREIGN KEY (cod_reserva) REFERENCES reservas(cod) ON DELETE CASCADE,
    CONSTRAINT FK_RACT_Actividad FOREIGN KEY (id_actividad) REFERENCES actividades(cod)
);

INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES
(1, '2025-07-02', '2025-07-02', 'Alta', 'ACTIVIDAD');
INSERT INTO reserva_actividad (cod_reserva, id_actividad) VALUES (4, 1);

INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES
(2, '2025-07-04', '2025-07-04', 'Alta', 'ACTIVIDAD');
INSERT INTO reserva_actividad (cod_reserva, id_actividad) VALUES (5, 4);

INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES
(3, '2025-07-11', '2025-07-11', 'Alta', 'ACTIVIDAD');
INSERT INTO reserva_actividad (cod_reserva, id_actividad) VALUES (6, 7);

-- Puente para Salas
CREATE TABLE reserva_sala (
    cod_reserva INT PRIMARY KEY,
    id_sala INT NOT NULL,
    CONSTRAINT FK_RSALA_Reserva FOREIGN KEY (cod_reserva) REFERENCES reservas(cod) ON DELETE CASCADE,
    CONSTRAINT FK_RSALA_Sala FOREIGN KEY (id_sala) REFERENCES salas_evento(cod)
);

INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES
(1, '2025-07-03', '2025-07-03', 'Alta', 'SALA');
INSERT INTO reserva_sala (cod_reserva, id_sala) VALUES (7, 1);

INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES
(2, '2025-07-05', '2025-07-05', 'alta', 'SALA');
INSERT INTO reserva_sala (cod_reserva, id_sala) VALUES (8, 4);

INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES
(3, '2025-07-12', '2025-07-12', 'Alta', 'SALA');
INSERT INTO reserva_sala (cod_reserva, id_sala) VALUES (9, 7);

CREATE TABLE UBICACION (
    id_ubicacion INT PRIMARY KEY,
    nombre VARCHAR(100),
    tipo_edificio VARCHAR(50)
);

CREATE TABLE INCIDENCIA (
    id_incidencia INT PRIMARY KEY,
    fecha_reporte DATE,
    descripcion TEXT,
    estado VARCHAR(30),
    prioridad VARCHAR(20),
    fecha_cierre DATE,
    id_ubicacion INT,
    cliente int,
    empleado int,
    FOREIGN KEY (id_ubicacion) REFERENCES UBICACION(id_ubicacion),
    FOREIGN KEY (cliente) REFERENCES CLIENTES(cod),
    FOREIGN KEY (empleado) REFERENCES EMPLEADOS(Cod_empleado)
);

CREATE TABLE TAREA (
    num_tarea INT PRIMARY KEY,
    nombre VARCHAR(100),
    tipo_tarea VARCHAR(50),
    descripcion TEXT,
    dificultad VARCHAR(20),
    estado VARCHAR(30),
    prioridad VARCHAR(20),
    id_incidencia INT,
    empleado int,
    FOREIGN KEY (id_incidencia) REFERENCES INCIDENCIA(id_incidencia),
    FOREIGN KEY (empleado) REFERENCES EMPLEADOS(Cod_empleado)
);

CREATE TABLE REVISION_PERIODICA (
    id_revision INT PRIMARY KEY,
    fecha_realizacion DATE,
    resultado VARCHAR(100),
    id_ubicacion INT,
    empleado int,
    FOREIGN KEY (id_ubicacion) REFERENCES UBICACION(id_ubicacion),
    FOREIGN KEY (empleado) REFERENCES EMPLEADOS(Cod_empleado)
);

-- Subsistema proveedores y compras

-- 1. CONSULTA SENCILLA: Salario medio de los empleados
select AVG(salario_base) from salarios;

-- 2. CONSULTA CON AGREGACIÓN: ver cuántos productos vende un proveedor
select p.nombre, count(prod.ID_producto) as "Total de productos que vende" FROM proveedores p, productos prod WHERE p.Cod_proveedor = prod.Proveedor GROUP BY p.nombre;

-- 3. CONSULTA CON SUBCONSULTA: ver productos del proveedor Tecnología y Suministros Norte.
Select id_producto, nombre from productos where proveedor = (select cod_proveedor from proveedores where nombre like 'Tecnología y Suministros Norte');

-- 4. UNA CONSULTA CON MULTITABLA: relacionar las ordenes de compra con sus respectivas lineas
select orden.no_orden, linea.no_linea, prod.nombre, linea.cantidad from orden_compra orden JOIN lineas_compra linea ON orden.no_orden = linea.No_compra JOIN productos prod ON prod.ID_producto = linea.Producto;

-- 5. Un INSERT y un UPDATE y un DELETE con una SELECT incluida:
-- Dar de alta un usuario en base a un empleado: INSERT
INSERT INTO Usuarios (nom_user, contrasenya, empleado) select 'LauraG' AS nom_user, 'Temporal2026' AS contrasenya, Cod_empleado from empleados where Nombre = 'Laura Gómez';
select * from usuarios;

-- Subida de precios de un proveedor: UPDATE
update Productos set Precio_unidad = Precio_unidad * 1.10 where Proveedor = ( select Cod_proveedor from Proveedores WHERE Nombre = 'Tecnología y Suministros Norte');
select * from productos;

-- Eliminar pedidos en esatdo eliminado o incidencia: DELETE
DELETE FROM Lineas_Compra WHERE No_compra IN (SELECT No_orden FROM Orden_Compra WHERE Estado IN ('En incidencia', 'Cancelado'));

-- Subsistema reservas y actividades

-- 1. CONSULTA SIMPLE
 
SELECT nombre, precio_base, iva, ROUND(precio_base * (1 + iva), 2) AS "Precio Total" FROM alojamientos  WHERE estado = 'Disponible';
 
-- 2. CONSULTA CON AGREGACIÓN
 
SELECT tipo_recurso, count(*) from reservas group by tipo_recurso having count(*)>1;
 
-- 3. CONSULTA CON SUBCONSULTA
 
SELECT nombre, tipo_alojamiento, capacidad, precio_base, estado FROM alojamientos WHERE precio_base > (SELECT AVG(precio_base) FROM alojamientos) ORDER BY precio_base DESC;
 

-- 4. CONSULTA MULTITABLA
 
Select c.nombre, count(*) as "Reservas totales" from reservas r JOIN clientes c on r.id_cliente=c.cod group by r.id_cliente;
 
-- 5a. INSERT con SELECT
 
-- Primero insertamos el nuevo cliente
INSERT INTO clientes (nombre, identificador, email, telefono) VALUES ('Ana Torres Vega', '99887766Z', 'ana.torres@gmail.com', '644556677');

INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso)VALUES ((SELECT cod FROM clientes WHERE identificador = '99887766Z'), '2025-08-01', '2025-08-01', 'Alta', 'ACTIVIDAD');
 
INSERT INTO reserva_actividad (cod_reserva, id_actividad)VALUES ((SELECT MAX(cod) FROM reservas), (SELECT cod FROM actividades WHERE precio_base = (SELECT MIN(precio_base) FROM actividades) LIMIT 1));

-- 5b. UPDATE con SELECT

UPDATE clientes SET estado = 'Inactivo' WHERE cod IN (SELECT id_cliente FROM reservas GROUP BY id_cliente HAVING MAX(fecha_fin) < '2025-07-31') AND cod > 0;
UPDATE clientes SET estado = 'Activo' WHERE cod IN (SELECT id_cliente FROM reservas GROUP BY id_cliente HAVING MAX(fecha_fin) < '2025-07-31') AND cod > 0;

-- 5c. DELETE con SELECT

UPDATE clientes SET estado = 'Inactivo' WHERE cod = 4;
DELETE FROM reservas WHERE id_cliente IN (SELECT cod FROM clientes WHERE estado <> 'Activo');
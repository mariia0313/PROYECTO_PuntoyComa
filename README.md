
# Sistema de Gestión Hotel **Punto y Coma**
![Logo Punto y Coma](./logo.enc)

---

## Índice

1. [Descripción General](#descripcin-general)
2. [Integrantes del Grupo](#integrantes-del-grupo)
3. [Organización en GitHub](#organizacin-en-github)
4. [Tecnologías Utilizadas](#tecnologas-utilizadas)
5. [Subsistemas del Proyecto](#subsistemas-del-proyecto)
   * [Gestión de Personal](#gestin-de-personal)
   * [Gestión de Reservas y Actividades](#subsistema-de-gestin-de-reservas-y-actividades).
   * [Subsistema de Mantenimiento](#subsistema-de-mantenimiento)
   * [Compras a Proveedores](#subsistema-de-compras-a-proveedores)
6. [Diagrama Entidad-Relación](#diagrama-entidad-relacin)
7. [Ejemplos de Código](#ejemplos-de-cdigo)
8. [Estado del Proyecto](#estado-del-proyecto)

---

## Descripción General

Este readme explica y muestra lo más importante del proyecto Punto Y Coma, que trata de un sistema integral para la gestión de un hotel-resort que permite administrar reservas de alojamientos y espacios, gestionar eventos, actividades para clientes, así como la gestión interna de mantenimiento y compras a proveedores. El proyecto se compone de una **aplicación Java en modo consola** con conexión a **MySQL** y una **web informativa** desarrollada con **HTML, CSS y JavaScript**. Dado que el código aún no está finiquitado, no podemos incluir toda la explicación del desarrollo completo del sistema.

> [!NOTA]
> Este proyecto se desarrolla bajo metodología **SCRUM** con entregas incrementales e iterativas.

> [!IMPORTANTE]
> El sistema cuenta con autenticación de usuarios por roles: **Administrador**, **Encargado de Compras** y **Encargado de Reservas**. Los usuarios cuentan con contraseña, sin embargo; no está encriptada ya que no se ha visto en este curso.

---

## Integrantes del Grupo

| Nombre | Rol |
|--------|-----|
| **María Herrero** | Release Manager |
| **David Catalán** | QC Manager (Quality Control) |
| **Aaron García** | Developer |

---

## Organización en GitHub

Repositorio oficial: [https://github.com/mariia0313/PROYECTO_PuntoyComa.git](https://github.com/mariia0313/PROYECTO_PuntoyComa.git)

### Estructura de Ramas

| Rama | Propósito |
|------|-----------|
| `main` | Código estable de producción |
| `development` | Integración de funcionalidades terminadas |
| `feature/*` | Ramas temporales para cada User Story |

### Flujo de Trabajo

1. Los desarrolladores crean una rama `feature/nombre-feature` a partir de `development`.
2. Al finalizar, abren una **Pull Request** hacia `development`.
3. Un compañero revisa el código y realiza el **merge**, garantizando revisión cruzada.
4. Las ramas mergeadas se eliminan.

### Problemas Resueltos

> Durante el desarrollo se produjo un merge accidental en `main`. La Release Manager ejecutó un **revert** para restaurar la rama principal y posteriormente la funcionalidad se integró correctamente en `development`.

> Los conflictos entre archivos los resuelve la persona que subió el archivo, luego hace el merge y un compañero valida la PR.

---

## Tecnologías Utilizadas

| Tecnología | Propósito |
|------------|-----------|
| Java |Lógica de negocio (CLI) |
| MySQL Community Server | Persistencia de datos |
| HTML5 + CSS3 + JavaScript | Web informativa |
| Git + GitHub | Control de versiones y organización del proyecto|
| NetBeans / VS Code | Entorno de desarrollo |
| Maven | Tests unitarios |

## Subsistemas del Proyecto

### Gestión de Personal

**Tablas implicadas:** `empleados`, `usuarios`, `salarios`, `registro_jornada`

Gestiona la informacion de los trabajadores del hotel. La tabla `empleados` almacena datos personales, cargo y estado laboral. Cada empleado tiene un `usuario` asociado para la autenticacion en el sistema. Los `salarios` registran el sueldo base, incentivos y comisiones por periodo, mientras que `registro_jornada` controla la entrada, salida y evaluacion del desempeno diario.
> No es un subsistema como tal indicado en el enunciado del proyecto, pero es un apartado necesario para poder llevarlo a cabo. De esta parte se ha encargado María Herrero.

### Subsistema de Gestión de Reservas y Actividades

**Tablas implicadas:** `clientes`, `alojamientos`, `actividades`, `salas_evento`, `reservas`, `reserva_alojamiento`, `reserva_actividad`, `reserva_sala`

Administra las reservas de alojamiento y espacios. Los `clientes` pueden reservar distintos tipos de recurso (`alojamientos`, `actividades` o `salas_evento`). La tabla `reservas` actua como tabla principal con un campo `tipo_recurso` (ENUM), y tres tablas puente (`reserva_alojamiento`, `reserva_actividad`, `reserva_sala`) almacenan la relacion especifica con cada recurso.

**Tablas implicadas:** `actividades`, `reserva_actividad`, `clientes`

Gestiona las actividades ludicas del hotel. La tabla `actividades` almacena el nombre, precio, capacidad, horarios y estado de cada actividad. Los clientes alojados pueden reservar actividades a traves de la tabla puente `reserva_actividad`, que vincula la reserva con la actividad concreta. Se incluye control de aforo y exportacion de registros.
> Decidimos juntar el subsistema de reservas y el de actividades dado que un integrante no iba a poder realizar el proyecto. De esta parte se ha encargado David Catalán

### Subsistema de Mantenimiento

**Tablas implicadas:** `ubicacion`, `incidencia`, `tarea`, `revision_periodica`, `clientes`, `empleados`

Coordina las incidencias reportadas por personal o clientes. Las `incidencias` se asocian a una `ubicacion` del hotel y pueden ser reportadas por `clientes` o `empleados`. A partir de cada incidencia se generan `tareas` asignadas al personal de mantenimiento. Ademas, se programan `revision_periodica` para mantenimiento preventivo de las instalaciones.
> De esta parte se ha encargado Aaron García.

### Subsistema de Compras a Proveedores

**Tablas implicadas:** `proveedores`, `productos`, `orden_compra`, `lineas_compra`, `empleados`

Gestiona proveedores y productos de compras. Los `proveedores` suministran `productos` que tienen control de stock minimo. Las `ordenes_compra` son creadas por `empleados` y detallan los productos solicitados a traves de `lineas_compra`. El sistema permite seguimiento del estado del pedido (Pendiente, Confirmado, Enviado, etc.).
> De esta parte se ha encargado María Herrero.

---
## Diagrama Entidad-Relación
![Diagrama Entidad-Relación](./Diagrama_Entidad_Relación.png)
---
Aquí tenemos, sacado de todo lo creado mediante código SQL gracias a una función de SQL Workbench, el diagrama entidad relacion para que se pueda entender las conexiones entre unos subsistemas y otros.

## Ejemplos de Código

### Conexion a Base de Datos

```java
    /**
     * Establece el enlace con la base de datos local 'proyecto_puntoycoma'.
     * @return Connection objeto de conexión.
     * @throws Exception si falla la carga del driver o las credenciales.
     */
    public Connection abrirConexion()throws Exception{
        Connection con = null;
        
    try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_puntoycoma?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "root", "yA20mnVB");
            System.out.println("Conexión exitosa");
            
        } catch (SQLException e) {
            System.out.println("Código de error SQL: " + e.getErrorCode());
            System.out.println("Estado SQL: " + e.getSQLState());
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    
    return con;
    
}
```

### Consulta de Empleados

```java

    /**
     * Recupera y muestra por consola el listado completo de empleados almacenados 
     * con todos sus detalles laborales y personales.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public void mostrarEmpleados(Connection con) throws SQLException {
        Statement stmt = null;
        String query = "SELECT * from empleados";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int codigo = rs.getInt("Cod_empleado");
                System.out.println("Código de empleado: " + codigo);
                String dni = rs.getString("Id_empleado");
                System.out.println("DNI: " + dni);
                String telef = rs.getString("Telefono");
                System.out.println("Telefono: " + telef);
                String nombre = rs.getString("Nombre");
                System.out.println("Nombre de empleado: " + nombre);
                String email = rs.getString("Email");
                System.out.println("Email de proveedor: " + email);
                String nuss = rs.getString("nss");
                System.out.println("NUSS: " + nuss);
                String contrato = rs.getString("tipo_contrato");
                System.out.println("Tipo de contrato: " + contrato);
                java.sql.Date fNac = rs.getDate("fecha_nacimiento");
                System.out.println("Fecha de nacimiento: " + fNac);
                java.sql.Date fAnt = rs.getDate("fecha_antiguedad");
                System.out.println("Fecha de antiguedad: " + fAnt);
                java.sql.Date fDesp = rs.getDate("fecha_despido");
                String despidoInfo = (fDesp == null) ? "En activo" : fDesp.toString();
                System.out.println("Fecha de despido: " + despidoInfo);
                String estado = rs.getString("Estado");
                System.out.println("Estado del empleado: " + estado);
                System.out.println("**********************************");
            }
        } catch (SQLException e) {
            printSQLException(e);
        } finally {
            stmt.close();

        }
    }
```

### Insercion de una Reserva

```java
/**
     * Crea una nueva reserva solicitando los datos por consola.
     * Valida que el recurso no esté reservado en las fechas indicadas mediante una consulta SQL de solapamiento.
     * Si la reserva es válida la inserta en BD y genera la factura .txt localmente.
     * @param leer Scanner para la entrada de datos.
     * @param con  Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante la operación.
     */
    public static void crearReserva(Scanner leer, Connection con) throws SQLException {
        boolean valido = true;
        Cliente cliente = null;
        TipoReserva recurso = null;
        int codRecurso;
        java.sql.Date fechaInicio = null;
        java.sql.Date fechaFin = null;

        ClienteDAO.mostrarClientes(con);
        System.out.println("Introduzca el codigo del cliente:");
        int codCliente = leer.nextInt();
        leer.nextLine();

        if (!ClienteDAO.existeCliente(con, codCliente)) {
            System.out.println("Cliente no encontrado. Cree primero el cliente.");
            valido = false;
        } else {
            cliente = ClienteDAO.obtenerClientePorId(con, codCliente);
        }

        // Seleccionar tipo de recurso
        if (valido) {
            System.out.println("Tipo de reserva:");
            System.out.println("1. Alojamiento");
            System.out.println("2. Actividad");
            System.out.println("3. Sala de Evento");
            int opcion = leer.nextInt();
            leer.nextLine();

            switch (opcion) {
                case 1:
                    TipoReservaDAO.mostrarAlojamientos(con);
                    System.out.println("Introduzca el codigo del alojamiento:");
                    codRecurso = leer.nextInt();
                    recurso = TipoReservaDAO.obtenerAlojamientoPorId(con, codRecurso);
                    break;
                case 2:
                    TipoReservaDAO.mostrarActividades(con);
                    System.out.println("Introduzca el codigo de la actividad:");
                    codRecurso = leer.nextInt();
                    recurso = TipoReservaDAO.obtenerActividadPorId(con, codRecurso);
                    break;
                case 3:
                    TipoReservaDAO.mostrarSalasEvento(con);
                    System.out.println("Introduzca el codigo de la sala de evento:");
                    codRecurso = leer.nextInt();
                    recurso = TipoReservaDAO.obtenerSalaEventoPorId(con, codRecurso);
                    break;
                default:
                    System.out.println("Opcion no valida");
                    valido = false;
                    break;
            }
            if (recurso == null && valido) {
                System.out.println("Recurso no encontrado en la base de datos.");
                valido = false;
            }
        }

        if (valido) {
            System.out.println("Introduzca el año de inicio:");
            int anioInicio = leer.nextInt();
            System.out.println("Introduzca el mes de inicio (1-12):");
            int mesInicio = leer.nextInt();
            System.out.println("Introduzca el dia de inicio");
            int diaInicio = leer.nextInt();
            fechaInicio = new java.sql.Date(anioInicio - 1900, mesInicio - 1, diaInicio);

            if (recurso instanceof Alojamiento) {
                System.out.println("Introduzca el año de fin:");
                int anioFin = leer.nextInt();
                System.out.println("Introduzca el mes de fin (1-12):");
                int mesFin = leer.nextInt();
                System.out.println("Introduzca el dia de fin:");
                int diaFin = leer.nextInt();
                fechaFin = new java.sql.Date(anioFin - 1900, mesFin - 1, diaFin);

                if (!fechaFin.after(fechaInicio)) {
                    System.out.println("La fecha de fin debe ser posterior a la fecha de inicio.");
                    valido = false;
                }
            } else {
                fechaFin = fechaInicio;
            }
        }

        String tipo;
        if(recurso instanceof Alojamiento) tipo ="ALOJAMIENTO";
        else if(recurso instanceof Actividad) tipo ="ACTIVIDAD";
        else tipo ="SALA";

        if (valido && haySolapamiento(con, tipo, recurso.getCod(), fechaInicio, fechaFin)) {
            System.out.println("ERROR: El recurso ya esta reservado en esas fechas.");
            valido = false;
        }

        if (valido) {
            Reserva reserva = new Reserva(0, cliente, recurso, fechaInicio, fechaFin, tipo);
            double precioTotal = reserva.calcularPrecioTotal();
            Statement stmt = null;
            ResultSet rs = null;
            con.setAutoCommit(false);
            int codReserva =0;
            try {
                // 1. Insertar en reservas
                String sqlRes = "INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES (?, ?, ?, 'Alta', ?)";
                PreparedStatement ps = con.prepareStatement(sqlRes, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, cliente.getCod());
                ps.setDate(2, fechaInicio);
                ps.setDate(3, fechaFin);
                ps.setString(4, tipo);
                ps.executeUpdate();
                
                rs = ps.getGeneratedKeys();
                if (rs.next()) codReserva = rs.getInt(1);

                // 2. Insertar en la tabla puente
                String tablaPuente = (recurso instanceof Alojamiento) ? "reserva_alojamiento" : (recurso instanceof Actividad) ? "reserva_actividad" : "reserva_sala";
                String colPuente = (recurso instanceof Alojamiento) ? "id_alojamiento" : (recurso instanceof Actividad) ? "id_actividad" : "id_sala";
                String sqlPuente = "INSERT INTO " + tablaPuente + " (cod_reserva, " + colPuente + ") VALUES (?, ?)";
                
                PreparedStatement psPuente = con.prepareStatement(sqlPuente);
                psPuente.setInt(1, codReserva);
                psPuente.setInt(2, recurso.getCod());
                psPuente.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                System.err.println("Error de base de datos en la operación:");
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            }
            Reserva reservaFinal = new Reserva(codReserva, cliente, recurso, fechaInicio, fechaFin, tipo);
            generarFactura(reservaFinal);
        }
    }
```

---

## Estado del Proyecto

> El proyecto se encuentra actualmente en **fase de desarrollo e integracion** en la rama `development` Se prevee que para el día 12, se haga la primera versión al Main del github.

| Hito | Estado |
|------|--------|
| Diseño de base de datos | Completado |
| Gestión de Personal | Completado |
| Subsistema de Compras | Completado |
| Subsistema de Reservas y Actividades | En desarrollo |
| Subsistema de Mantenimiento | Pendiente |
| Web informativa | En desarrollo |
| Testing y QC | Realizado |
| Release v1.0.0 | Pendiente |

---

**Proyecto Intermodular 1º DAW - IES Font de Sant Lluís**

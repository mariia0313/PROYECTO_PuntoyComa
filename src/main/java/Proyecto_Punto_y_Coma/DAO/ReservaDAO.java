package Proyecto_Punto_y_Coma.DAO;

import Proyecto_Punto_y_Coma.ENTIDAD.Actividad;
import Proyecto_Punto_y_Coma.ENTIDAD.Alojamiento;
import Proyecto_Punto_y_Coma.ENTIDAD.Cliente2;
import Proyecto_Punto_y_Coma.ENTIDAD.Reserva;
import Proyecto_Punto_y_Coma.ENTIDAD.SalaEvento;
import Proyecto_Punto_y_Coma.ENTIDAD.TipoReserva;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Scanner;

/**
 * Clase DAO para la gestión de Reservas.
 * Operaciones en base de datos a excepción de la generación de facturas, la crea un fichero de texto.
 *
 * @author David Catalán Aragó
 */
public class ReservaDAO {

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
        Cliente2 cliente = null;
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

    /**
     * Comprueba si existe solapamiento de fechas para un recurso concreto.
     * Dos reservas se solapan si: fechaInicio menor que otraFechaFin
     * Y fechaFin mayor que otraFechaInicio.
     * @param con         Conexión activa.
     * @param idRecurso Tipo del recurso ('ALOJAMIENTO','ACTIVIDAD','SALA_EVENTO').
     * @param codRecurso  Código del recurso.
     * @param fechaInicio Fecha de inicio propuesta.
     * @param fechaFin    Fecha de fin propuesta.
     * @return true si hay solapamiento, false si el recurso está libre.
     */
    private static boolean haySolapamiento(Connection con, String tipoRecurso, int idRecurso, java.sql.Date inicio, java.sql.Date fin) throws SQLException {
        // Corregido: Buscamos por id_recurso y eliminamos tipo_recurso que ya no existe en la tabla
        String tablaPuente = tipoRecurso.equals("ALOJAMIENTO") ? "reserva_alojamiento" : tipoRecurso.equals("ACTIVIDAD") ? "reserva_actividad" : "reserva_sala";
        String colPuente = tipoRecurso.equals("ALOJAMIENTO") ? "id_alojamiento" : tipoRecurso.equals("ACTIVIDAD") ? "id_actividad" : "id_sala";

        String query = "SELECT COUNT(*) FROM reservas r " + "JOIN " + tablaPuente + " p ON r.cod = p.cod_reserva " + "WHERE p." + colPuente + " = ? AND " + "((r.fecha_inicio <= ? AND r.fecha_fin >= ?) OR (r.fecha_inicio >= ? AND r.fecha_inicio < ?)) AND r.estado = 'Alta'";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(query);
            stmt.setInt(1, idRecurso);
            stmt.setDate(2, fin);
            stmt.setDate(3, inicio);
            stmt.setDate(4, inicio);
            stmt.setDate(5, fin);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar solapamiento: ");
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        return false;
    }

    /**
     * Recupera y muestra por consola todas las reservas almacenadas en la BD haciendo un JOIN con la tabla clientes para mostrar el nombre del cliente.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public static void mostrarReservas(Connection con) throws SQLException {
        String query = "SELECT r.*, c.nombre AS nombre_cliente, c.dni, " +
                   "ra.id_alojamiento, ract.id_actividad, rs.id_sala " +
                   "FROM reservas r " +
                   "JOIN clientes c ON r.id_cliente = c.cod " +
                   "LEFT JOIN reserva_alojamiento ra ON r.cod = ra.cod_reserva " +
                   "LEFT JOIN reserva_actividad ract ON r.cod = ract.cod_reserva " +
                   "LEFT JOIN reserva_sala rs ON r.cod = rs.cod_reserva";

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int codReserva = rs.getInt("cod");
                java.sql.Date fInicio = rs.getDate("fecha_inicio");
                java.sql.Date fFin = rs.getDate("fecha_fin");
                Cliente2 cliente = new Cliente2(rs.getInt("id_cliente"), rs.getString("nombre_cliente"), rs.getString("dni"), null, null);

                int idRecurso = 0;
                String tipoNombre = "";
                TipoReserva recurso = null;

                int idAloj = rs.getInt("id_alojamiento");
                if (!rs.wasNull()) {
                    idRecurso = idAloj;
                    recurso = TipoReservaDAO.obtenerAlojamientoPorId(con, idRecurso);
                    tipoNombre = "Alojamiento";
                } else {
                    int idAct = rs.getInt("id_actividad");
                    if (!rs.wasNull()) {
                        idRecurso = idAct;
                        recurso = TipoReservaDAO.obtenerActividadPorId(con, idRecurso);
                        tipoNombre = "Actividad";
                    } else {
                        int idSal = rs.getInt("id_sala");
                        if (!rs.wasNull()) {
                            idRecurso = idSal;
                            recurso = TipoReservaDAO.obtenerSalaEventoPorId(con, idRecurso);
                            tipoNombre = "Sala de Evento";
                        }
                    }
                }

                double precioTotal = 0;
                if (recurso != null) {
                    Reserva temporal = new Reserva(codReserva, cliente, recurso, fInicio, fFin, tipoNombre);
                    precioTotal = temporal.calcularPrecioTotal();
                }

                System.out.println("**********************************");
                System.out.println("Codigo Reserva: " + codReserva);
                System.out.println("Cliente:        " + rs.getString("nombre_cliente") + " (DNI: " + rs.getString("dni") + ")");
                System.out.println("Tipo Recurso:   " + tipoNombre + " (Cod: " + idRecurso + ")");
                System.out.println("Fecha Inicio:   " + fInicio);
                System.out.println("Fecha Fin:      " + fFin);
                System.out.println("Estado:         " + rs.getString("estado"));
                System.out.println("Precio Total:   " + precioTotal + " EUR");
            }
            System.out.println("**********************************");
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Muestra todas las reservas de un cliente concreto filtrando por cod_cliente.
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada del código de cliente.
     * @throws SQLException Si hay errores en la consulta.
     */
    public static void mostrarReservasPorCliente(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo del cliente");
        int codCliente = leer.nextInt();
        leer.nextLine();

        if (!ClienteDAO.existeCliente(con, codCliente)) {
            System.out.println("Cliente no encontrado.");
        } else {
            String query = "SELECT r.*, c.nombre AS nombre_cliente, c.dni, " +
                       "ra.id_alojamiento, ract.id_actividad, rs.id_sala " +
                       "FROM reservas r " +
                       "JOIN clientes c ON r.id_cliente = c.cod " +
                       "LEFT JOIN reserva_alojamiento ra ON r.cod = ra.cod_reserva " +
                       "LEFT JOIN reserva_actividad ract ON r.cod = ract.cod_reserva " +
                       "LEFT JOIN reserva_sala rs ON r.cod = rs.cod_reserva " +
                       "WHERE r.id_cliente = ?";

            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                stmt = con.prepareStatement(query);
                stmt.setInt(1, codCliente);
                rs = stmt.executeQuery();

                boolean hayReservas = false;
                while (rs.next()) {
                    hayReservas = true;
                    int codReserva = rs.getInt("cod");

                    Cliente2 cliente = new Cliente2(rs.getInt("id_cliente"), rs.getString("nombre_cliente"), rs.getString("dni"), null, null);

                    int idRecurso = 0;
                    String tipoNombre = "";
                    TipoReserva recurso = null;

                    int idAloj = rs.getInt("id_alojamiento");
                    if (!rs.wasNull()) {
                        idRecurso = idAloj;
                        recurso = TipoReservaDAO.obtenerAlojamientoPorId(con, idRecurso);
                        tipoNombre = "Alojamiento";
                    } else {
                        int idAct = rs.getInt("id_actividad");
                        if (!rs.wasNull()) {
                            idRecurso = idAct;
                            recurso = TipoReservaDAO.obtenerActividadPorId(con, idRecurso);
                            tipoNombre = "Actividad";
                        } else {
                            int idSal = rs.getInt("id_sala");
                            if (!rs.wasNull()) {
                                idRecurso = idSal;
                                recurso = TipoReservaDAO.obtenerSalaEventoPorId(con, idRecurso);
                                tipoNombre = "Sala de Evento";
                            }
                        }
                    }

                    double precioTotal = 0;
                    if (recurso != null) {
                        Reserva temporal = new Reserva(codReserva, cliente, recurso, rs.getDate("fecha_inicio"), rs.getDate("fecha_fin"), tipoNombre);
                        precioTotal = temporal.calcularPrecioTotal();
                    }

                    System.out.println("**********************************");
                    System.out.println("Codigo Reserva: " + codReserva);
                    System.out.println("Cliente:        " + rs.getString("nombre_cliente") + " (DNI: " + rs.getString("dni") + ")");
                    System.out.println("Tipo Recurso:   " + tipoNombre + " (Cod: " + idRecurso + ")");
                    System.out.println("Fecha Inicio:   " + rs.getDate("fecha_inicio"));
                    System.out.println("Fecha Fin:      " + rs.getDate("fecha_fin"));
                    System.out.println("Precio Total:   " + precioTotal + " EUR");
                }

                if (!hayReservas) {
                    System.out.println("Este cliente no tiene reservas.");
                }
                System.out.println("**********************************");
            } catch (SQLException e) {
                System.err.println("Error de base de datos en la operación:");
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            }
        }
    }

    /**
     * Recupera una reserva completa de la base de datos a partir de su código identificador.
     * Realiza una unión (JOIN) con clientes y las tablas puente para obtener todos los datos necesarios.
     * @param con Conexión activa a la base de datos.
     * @param cod Código de la reserva a buscar.
     * @return Objeto Reserva con todos sus datos cargados o null si no existe.
     * @throws SQLException Si ocurre un error durante la consulta.
     */
    public static Reserva obtenerReservaPorCod(Connection con, int cod) throws SQLException {
        String query = "SELECT r.*, c.nombre AS nombre_cliente, c.dni, " +
                    "ra.id_alojamiento, ract.id_actividad, rs.id_sala " +
                    "FROM reservas r " +
                    "JOIN clientes c ON r.id_cliente = c.cod " +
                    "LEFT JOIN reserva_alojamiento ra ON r.cod = ra.cod_reserva " +
                    "LEFT JOIN reserva_actividad ract ON r.cod = ract.cod_reserva " +
                    "LEFT JOIN reserva_sala rs ON r.cod = rs.cod_reserva " +
                    "WHERE r.cod = ?";
        
        Reserva reservaEncontrada = null;
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, cod);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente2 cliente = new Cliente2(rs.getInt("id_cliente"), rs.getString("nombre_cliente"), rs.getString("dni"), null, null);
                    
                    TipoReserva recurso = null;
                    
                    int idAloj = rs.getInt("id_alojamiento");
                    if (!rs.wasNull()) recurso = TipoReservaDAO.obtenerAlojamientoPorId(con, idAloj);
                    
                    int idAct = rs.getInt("id_actividad");
                    if (!rs.wasNull()) recurso = TipoReservaDAO.obtenerActividadPorId(con, idAct);
                    
                    int idSala = rs.getInt("id_sala");
                    if (!rs.wasNull()) recurso = TipoReservaDAO.obtenerSalaEventoPorId(con, idSala);
                    
                    reservaEncontrada = new Reserva(cod, cliente, recurso, rs.getDate("fecha_inicio"), rs.getDate("fecha_fin"), rs.getString("tipo_recurso"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservaEncontrada;
    }

    /**
     * Modifica las fechas de una reserva existente mediante un menú por consola.
     * Realiza la actualización de forma simple mediante una sentencia SQL UPDATE tradicional.
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de datos.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void modificarReserva(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo de la reserva a modificar");
        int cod = leer.nextInt();
        leer.nextLine();
    
        if (!existeReserva(con, cod)) {
            System.out.println("Reserva no encontrada.");
        } else {
            Reserva res = ReservaDAO.obtenerReservaPorCod(con, cod);

            int opcion = 0;
            PreparedStatement ps = null;
            do {
                System.out.println("Elija que modificar");
                System.out.println("1. Fecha Inicio");
                System.out.println("2. Fecha Fin");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                leer.nextLine();
                
                if (opcion == 1 || opcion == 2) {
                    System.out.println("Introduzca el año:");
                    int anio = leer.nextInt();
                    System.out.println("Introduzca el mes (1-12):");
                    int mes = leer.nextInt();
                    System.out.println("Introduzca el dia:");
                    int dia = leer.nextInt();
                    leer.nextLine();

                    java.sql.Date nuevaFecha = new java.sql.Date(anio - 1900, mes - 1, dia);
                    java.sql.Date inicioValidar = (opcion == 1) ? nuevaFecha : res.getFechaInicio();
                    java.sql.Date finValidar = (opcion == 2) ? nuevaFecha : res.getFechaFin();
                    
                    if (haySolapamiento(con, res.getTipoRecurso(), res.getTipoReserva().getCod(), inicioValidar, finValidar)){
                        System.out.println("Error: El recurso ya está reservado en esas fechas.");
                    } else {
                        String campo = (opcion == 1) ? "fecha_inicio" : "fecha_fin";
                        String sql = "UPDATE reservas SET " + campo + " = ? WHERE cod = ?";
                        try {
                            ps = con.prepareStatement(sql);
                            ps.setDate(1, nuevaFecha);
                            ps.setInt(2, cod);

                            int filas = ps.executeUpdate();
                            if (filas > 0) {
                                System.out.println("Campo actualizado correctamente");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            if (ps != null) ps.close();
                        }
                    }
                }
            } while (opcion != 0);
        }
    }

    /**
     * Cancela una reserva existente cambiando su estado a 'Baja'.
     * Realiza la actualización de forma simple mediante una sentencia SQL UPDATE tradicional.
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada del código de reserva.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void cancelarReserva(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo de la reserva a cancelar");
        int cod = leer.nextInt();
        leer.nextLine();
    
        if (!existeReserva(con, cod)) {
            System.out.println("Reserva no encontrada.");
        } else {
            String sql = "UPDATE reservas SET estado = 'Baja' WHERE cod = ?";
            PreparedStatement ps = null;
            try {
                ps = con.prepareStatement(sql);
                ps.setInt(1, cod);
                
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    System.out.println("Reserva cancelada correctamente.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null) {
                    ps.close();
                }
            }
        }
    }

    /**
     * Verifica si existe una reserva con el código dado.
     * @param con    Conexión activa a la base de datos.
     * @param codigo Código de la reserva.
     * @return true si existe, false en caso contrario.
     */
    public static boolean existeReserva(Connection con, int codigo) {
        boolean existe = false;
        PreparedStatement ps;
        String sql = "SELECT COUNT(*) FROM reservas WHERE cod = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    existe = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        }
        return existe;
    }

    /**
     * Genera una factura detallada en un fichero de texto plano (.txt) con todos los datos de la reserva.
     * Crea automáticamente el directorio 'Facturas' si no existe y guarda el archivo allí bajo el formato 'factura_CODRESERVA.txt'.
     * @param reserva Objeto Reserva a partir del cual se extraen los datos del cliente, 
     * el recurso reservado, las fechas y los importes económicos.
    */
    public static void generarFactura(Reserva reserva) {
        String nombreFichero = "factura_" + reserva.getCod() + ".txt";
        File dir = new File("Facturas");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File f = new File(dir, nombreFichero);

        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(f, false); // false = sobreescribir
            pw = new PrintWriter(fw);

            pw.println("============================================");
            pw.println("            FACTURA DE RESERVA              ");
            pw.println("============================================");
            pw.println("Codigo Reserva: " + reserva.getCod());
            pw.println("Fecha Emision:  " + new java.sql.Date(System.currentTimeMillis()));
            pw.println("--------------------------------------------");
            pw.println("CLIENTE");
            pw.println("Nombre:    " + reserva.getCliente().getNombre());
            pw.println("DNI:       " + reserva.getCliente().getDni());
            pw.println("Email:     " + reserva.getCliente().getEmail());
            pw.println("Telefono:  " + reserva.getCliente().getTelefono());
            pw.println("--------------------------------------------");
            pw.println("RECURSO RESERVADO");

            TipoReserva tr = reserva.getTipoReserva();
            if (tr instanceof Alojamiento) {
                Alojamiento a = (Alojamiento) tr;
                pw.println("Tipo:          Alojamiento");
                pw.println("Nombre:        " + a.getNombre());
                pw.println("Tipo Alojamiento:    " + a.getTipoAlojamiento());
                pw.println("Capacidad:     " + a.getCapacidad());
            } else if (tr instanceof Actividad) {
                Actividad act = (Actividad) tr;
                pw.println("Tipo:          Actividad");
                pw.println("Nombre:        " + act.getNombre());
                pw.println("Horario:       " + act.getHoraInicio() + " - " + act.getHoraFin());
                pw.println("Estado:        " + act.getEstado());
            } else if (tr instanceof SalaEvento) {
                SalaEvento s = (SalaEvento) tr;
                pw.println("Tipo:          Sala de Evento");
                pw.println("Nombre:        " + s.getNombre());
                pw.println("Capacidad:     " + s.getCapacidad());
                pw.println("Horario:       " + s.getHoraInicio() + " - " + s.getHoraFin());
            }

            pw.println("--------------------------------------------");
            pw.println("FECHAS");
            pw.println("Fecha Inicio:  " + reserva.getFechaInicio());
            pw.println("Fecha Fin:     " + reserva.getFechaFin());
            pw.println("--------------------------------------------");
            pw.println("PRECIOS");
            pw.println("Precio Base:   " + tr.getPrecioBase() + " €");
            pw.println("IVA (" + (tr.getIva() * 100) + "%): " + tr.precioIVA() + " €");
            pw.println("TOTAL:         " + reserva.calcularPrecioTotal() + " €");
            pw.println("============================================");
            pw.println("        Gracias por su reserva");
            pw.println("============================================");

            System.out.println("Factura generada correctamente: " + f.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error al generar la factura: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Menú principal de gestión de reservas con las opciones requeridas.
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de opciones.
     * @throws SQLException Si ocurre un error en las operaciones llamadas.
     */
    public static void menu(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("===== GESTION DE RESERVAS =====");
            System.out.println("1. Ver todas las reservas");
            System.out.println("2. Crear reserva");
            System.out.println("3. Modificar reserva");
            System.out.println("4. Cancelar reserva");
            System.out.println("5. Ver reservas por cliente");
            System.out.println("0. Salir");
            opcion = leer.nextInt();
            leer.nextLine();

            switch (opcion) {
                case 1:
                    mostrarReservas(con);
                    break;
                case 2: 
                    crearReserva(leer, con);
                    break;
                case 3: 
                    modificarReserva(con, leer);
                    break;
                case 4: 
                    cancelarReserva(con, leer);
                    break;
                case 5: 
                    mostrarReservasPorCliente(con, leer);
                    break;
                case 0: 
                    System.out.println("Saliendo...");
                    break;
                default: 
                    System.out.println("Opcion no valida"); 
                    break;
            }
        } while (opcion != 0);
    }
}
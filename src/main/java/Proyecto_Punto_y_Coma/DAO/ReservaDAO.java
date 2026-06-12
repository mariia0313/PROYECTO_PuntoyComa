package Proyecto_Punto_y_Coma.DAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import Proyecto_Punto_y_Coma.ENTIDAD.Actividad;
import Proyecto_Punto_y_Coma.ENTIDAD.Alojamiento;
import Proyecto_Punto_y_Coma.ENTIDAD.Cliente;
import Proyecto_Punto_y_Coma.ENTIDAD.Reserva;
import Proyecto_Punto_y_Coma.ENTIDAD.SalaEvento;
import Proyecto_Punto_y_Coma.ENTIDAD.TipoReserva;

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
        Cliente cliente = null;
        TipoReserva recurso = null;
        int codRecurso;
        java.sql.Date fechaInicio = null;
        java.sql.Date fechaFin = null;

        ClienteDAO.mostrarClientes(con);
        System.out.println("Introduzca el codigo del cliente:");
        int codCliente = ConexionBD.leerEntero(leer);

        if (!ClienteDAO.existeCliente(con, codCliente)) {
            System.out.println("Cliente no encontrado. Cree primero el cliente.");
            valido = false;
        } else {
            cliente = ClienteDAO.obtenerClientePorId(con, codCliente);
        }

        if (valido) {
            System.out.println("Tipo de reserva:");
            System.out.println("1. Alojamiento");
            System.out.println("2. Actividad");
            System.out.println("3. Sala de Evento");
            int opcion = ConexionBD.leerEntero(leer);

            switch (opcion) {
                case 1:
                    TipoReservaDAO.mostrarAlojamientos(con);
                    System.out.println("Introduzca el codigo del alojamiento:");
                    codRecurso = ConexionBD.leerEntero(leer);
                    recurso = TipoReservaDAO.obtenerAlojamientoPorId(con, codRecurso);
                    break;
                case 2:
                    TipoReservaDAO.mostrarActividades(con);
                    System.out.println("Introduzca el codigo de la actividad:");
                    codRecurso = ConexionBD.leerEntero(leer);
                    recurso = TipoReservaDAO.obtenerActividadPorId(con, codRecurso);
                    break;
                case 3:
                    TipoReservaDAO.mostrarSalasEvento(con);
                    System.out.println("Introduzca el codigo de la sala de evento:");
                    codRecurso = ConexionBD.leerEntero(leer);
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
            do{
                System.out.println("Introduzca la fecha de inicio (YYYY-MM-DD)");
                String fechaI = leer.nextLine();
                try {
                    fechaInicio = java.sql.Date.valueOf(fechaI);
                } catch (IllegalArgumentException e) {
                    System.out.println("Formato incorrecto. Use YYYY-MM-DD");
                }
            }while(fechaInicio == null);

            if (recurso instanceof Alojamiento) {
                do{
                    System.out.println("Introduzca la fecha de fin (YYYY-MM-DD)");
                    String fechaF = leer.nextLine();

                    try {
                        fechaFin = java.sql.Date.valueOf(fechaF);
                        if (!fechaFin.after(fechaInicio)) {
                            System.out.println("La fecha de fin debe ser posterior a la fecha de inicio.");
                            fechaFin = null;
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Formato incorrecto. Use YYYY-MM-DD");
                    }
                }while(fechaFin == null);
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
            Statement stmt = null;
            ResultSet rs = null;
            con.setAutoCommit(false);
            int codReserva =0;
            try {
                // 1. Insertar en reservas
                String sqlRes = "INSERT INTO reservas (id_cliente, fecha_inicio, fecha_fin, estado, tipo_recurso) VALUES (?, ?, ?, 'Alta', ?)";
                PreparedStatement ps = con.prepareStatement(sqlRes, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, cliente.getCodigo());
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
     * Comprueba si existe solapamiento de fechas para un recurso concreto, permitiendo excluir una reserva existente (Usado en modificaciones).
     * Dos reservas se solapan si: fechaInicio menor que otraFechaFin
     * Y fechaFin mayor que otraFechaInicio.
     * @param con             Conexión activa.
     * @param tipoRecurso     Tipo del recurso ('ALOJAMIENTO','ACTIVIDAD','SALA_EVENTO').
     * @param idRecurso       Código del recurso.
     * @param fechaInicio     Fecha de inicio propuesta.
     * @param fechaFin        Fecha de fin propuesta.
     * @param cod      código de la reserva a ignorar en la comprobación (pasar -1 si no aplica).
     * @return true si hay solapamiento, false si el recurso está libre.
     */
    private static boolean haySolapamiento(Connection con, String tipoRecurso, int idRecurso, java.sql.Date inicio, java.sql.Date fin, int cod) throws SQLException {
        String tablaPuente = tipoRecurso.equals("ALOJAMIENTO") ? "reserva_alojamiento" : tipoRecurso.equals("ACTIVIDAD") ? "reserva_actividad" : "reserva_sala";
        String colPuente = tipoRecurso.equals("ALOJAMIENTO") ? "id_alojamiento" : tipoRecurso.equals("ACTIVIDAD") ? "id_actividad" : "id_sala";

        String query = "SELECT COUNT(*) FROM reservas r " + "JOIN " + tablaPuente + 
        " p ON r.cod = p.cod_reserva " + "WHERE p." + colPuente + " = ? AND r.cod != ? AND " + 
        "((r.fecha_inicio <= ? AND r.fecha_fin >= ?) OR (r.fecha_inicio >= ? AND r.fecha_inicio < ?)) " + "AND r.estado = 'Alta'";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(query);
            stmt.setInt(1, idRecurso);
            stmt.setInt(2, cod);
            stmt.setDate(3, fin);
            stmt.setDate(4, inicio);
            stmt.setDate(5, inicio);
            stmt.setDate(6, fin);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
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
        String query = "SELECT r.*, c.nombre AS nombre_cliente, c.identificador, ra.id_alojamiento, ract.id_actividad, rs.id_sala" + 
        " FROM reservas r JOIN clientes c ON r.id_cliente = c.cod LEFT JOIN reserva_alojamiento ra ON r.cod = ra.cod_reserva" + 
        "LEFT JOIN reserva_actividad ract ON r.cod = ract.cod_reserva " +
        "LEFT JOIN reserva_sala rs ON r.cod = rs.cod_reserva ORDER BY r.cod ASC";

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int codReserva = rs.getInt("cod");
                java.sql.Date fInicio = rs.getDate("fecha_inicio");
                java.sql.Date fFin = rs.getDate("fecha_fin");
                Cliente cliente = new Cliente(rs.getInt("id_cliente"), rs.getString("nombre_cliente"), rs.getString("identificador"), null, null, "Activo");

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
                System.out.println("Cliente:        " + rs.getString("nombre_cliente") + " (Identificador: " + rs.getString("identificador") + ")");
                System.out.println("Tipo Recurso:   " + tipoNombre + " (Cod: " + idRecurso + ")");
                System.out.println("Fecha Inicio:   " + fInicio);
                System.out.println("Fecha Fin:      " + fFin);
                System.out.println("Precio Total:   " + precioTotal + " EUR");
                System.out.println("Estado:         " + rs.getString("estado"));
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
        int codCliente = ConexionBD.leerEntero(leer);

        if (!ClienteDAO.existeCliente(con, codCliente)) {
            System.out.println("Cliente no encontrado.");
        } else {
            String query = "SELECT r.*, c.nombre AS nombre_cliente, c.identificador, " +
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

                    Cliente cliente = new Cliente(rs.getInt("id_cliente"), rs.getString("nombre_cliente"), rs.getString("identificador"), null, null, "Activo");

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
                    System.out.println("Cliente:        " + rs.getString("nombre_cliente") + " (Identificador: " + rs.getString("identificador") + ")");
                    System.out.println("Tipo Recurso:   " + tipoNombre + " (Cod: " + idRecurso + ")");
                    System.out.println("Fecha Inicio:   " + rs.getDate("fecha_inicio"));
                    System.out.println("Fecha Fin:      " + rs.getDate("fecha_fin"));
                    System.out.println("Precio Total:   " + precioTotal + " EUR");
                    System.out.println("Estado:   " + rs.getString("estado"));
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
        String query = "SELECT r.*, c.nombre AS nombre_cliente, c.identificador, ra.id_alojamiento, ract.id_actividad, rs.id_sala FROM reservas r " +
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
                    Cliente cliente = new Cliente(rs.getInt("id_cliente"), rs.getString("nombre_cliente"), rs.getString("identificador"), null, null, "Activo");
                    
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
        int cod = ConexionBD.leerEntero(leer);
    
        if (!existeReserva(con, cod)) {
            System.out.println("Reserva no encontrada.");
        } else {
            Reserva res = ReservaDAO.obtenerReservaPorCod(con, cod);

            if (res.getTipoRecurso().equals("ALOJAMIENTO")) {
                int opcion = 0;
                PreparedStatement ps = null;
                do {
                    System.out.println("Elija que modificar");
                    System.out.println("1. Fecha Inicio");
                    System.out.println("2. Fecha Fin");
                    System.out.println("0. Salir");
                    opcion = ConexionBD.leerEntero(leer);

                    java.sql.Date nuevaFecha = null;
                    boolean valido = true;
                    
                    if (opcion == 1 || opcion == 2) {
                        do {
                            System.out.println("Introduzca la nueva fecha (YYYY-MM-DD)");
                            String nFecha = leer.nextLine();
                            try {
                                nuevaFecha = java.sql.Date.valueOf(nFecha);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Formato incorrecto. Use YYYY-MM-DD (ej: 1990-05-15)");
                            }
                        } while (nuevaFecha == null);

                        java.sql.Date inicioValidar = (opcion == 1) ? nuevaFecha : res.getFechaInicio();
                        java.sql.Date finValidar = (opcion == 2) ? nuevaFecha : res.getFechaFin();

                        if (finValidar.before(inicioValidar)) {
                            System.out.println("Error: La fecha de fin no puede ser anterior a la de inicio.");
                            valido = false;
                        }
                        
                        if (haySolapamiento(con, res.getTipoRecurso(), res.getTipoReserva().getCod(), inicioValidar, finValidar, cod)) {
                            System.out.println("Error: El recurso ya está reservado en esas fechas.");
                            valido = false;
                        }

                        if (valido) {
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
            } else if (res.getTipoRecurso().equals("ACTIVIDAD") || res.getTipoRecurso().equals("SALA")) {
                int opcion = 0;
                PreparedStatement ps = null;
                do {
                    System.out.println("Quieres modificar la fecha?");
                    System.out.println("1. Fecha");
                    System.out.println("0. Salir");
                    opcion = ConexionBD.leerEntero(leer);

                    java.sql.Date nuevaFecha = null;
                    boolean valido = true;
                    
                    if (opcion == 1) {
                        do {
                            System.out.println("Introduzca la nueva fecha (YYYY-MM-DD)");
                            String nFecha = leer.nextLine();
                            try {
                                nuevaFecha = java.sql.Date.valueOf(nFecha);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Formato incorrecto. Use YYYY-MM-DD (ej: 1990-05-15)");
                            }
                        } while (nuevaFecha == null);
                        
                        if (haySolapamiento(con, res.getTipoRecurso(), res.getTipoReserva().getCod(), nuevaFecha, nuevaFecha, cod)) {
                            System.out.println("Error: El recurso ya está reservado en esas fechas.");
                            valido = false;
                        }

                        if (valido) {
                            String sql = "UPDATE reservas SET fecha_inicio = ?, fecha_fin = ?  WHERE cod = ?";
                            try {
                                ps = con.prepareStatement(sql);
                                ps.setDate(1, nuevaFecha);
                                ps.setDate(2, nuevaFecha);
                                ps.setInt(3, cod);

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
            } else {
                System.out.println("Tipo de recurso no reconocido, no se pueden modificar las fechas.");
            }
        }
    }

    /**
     * Cambia el estado de una reserva de 'Alta' a 'Baja' en la base de datos.
     * @param con  Conexión activa a la base de datos MySQL.
     * @param leer Scanner para la lectura del código por consola.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void cancelarReserva(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el código de la reserva a cancelar:");
        int cod = ConexionBD.leerEntero(leer);

        if (!existeReserva(con, cod)) {
            System.out.println("Reserva no encontrada.");
        } else {
            PreparedStatement ps = null;
            try {
                ps = con.prepareStatement("UPDATE reservas SET estado = 'Baja' WHERE cod = ?");
                ps.setInt(1, cod);
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    System.out.println("Reserva dada de baja correctamente.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (ps != null) ps.close();
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
        String cod = String.valueOf(reserva.getCod());
        String nombreCliente = reserva.getCliente().getNombre();
        String idCliente = reserva.getCliente().getIdentificador();
        String email = reserva.getCliente().getEmail();
        String telf = reserva.getCliente().getTelefono();
        
        TipoReserva tr = reserva.getTipoReserva();
        String tipo = tr.getClass().getSimpleName();
        double total = reserva.calcularPrecioTotal();
        
        File dir = new File("Facturas");
        if (!dir.exists()) dir.mkdirs();
        File f = new File(dir, "factura_reserva_" + cod + ".html");

        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(f, false); // false = sobreescribir
            pw = new PrintWriter(fw);

            pw.println("<html><head><meta charset='UTF-8'>");
            pw.println("<style>");
            pw.println("body { font-family: Arial; margin: 30px; color: #333; }");
            pw.println("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
            pw.println("th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }");
            pw.println("th { background-color: #f8f8f8; }");
            pw.println(".total { font-size: 1.2em; font-weight: bold; color: #d32f2f; }");
            pw.println("</style></head><body>");

            pw.println("<h1>Factura #" + cod + "</h1>");
            pw.println("<p>Fecha: " + new java.sql.Date(System.currentTimeMillis()) + "</p>");
            
            pw.println("<h3>Cliente</h3>");
            pw.println("<p>" + nombreCliente + " (" + idCliente + ")<br>Email: " + email + "<br>Tel: " + telf + "</p>");

            pw.println("<h3>Detalle</h3>");
            pw.println("<table>");
            pw.println("<tr><th>Concepto</th><th>Información</th></tr>");
            pw.println("<tr><td>Tipo</td><td>" + tipo + "</td></tr>");
            pw.println("<tr><td>Fechas</td><td>" + reserva.getFechaInicio() + " al " + reserva.getFechaFin() + "</td></tr>");
            pw.println("</table>");

            pw.println("<p class='total'>TOTAL A PAGAR: " + String.format("%.2f", total) + " €</p>");
            
            pw.println("</body></html>");

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
            opcion = ConexionBD.leerEntero(leer);

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
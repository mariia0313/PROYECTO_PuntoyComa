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
        String tipoRecurso = "";
        int codRecurso;
        Date fechaInicio = null;
        Date fechaFin = null;

        ClienteDAO.mostrarClientes(con);
        System.out.println("Introduzca el codigo del cliente:");
        int codCliente = leer.nextInt();
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
            switch (opcion) {
                case 1:
                    TipoReservaDAO.mostrarAlojamientos(con);
                    System.out.println("Introduzca el codigo del alojamiento:");
                    codRecurso = leer.nextInt();
                    recurso = TipoReservaDAO.obtenerAlojamientoPorId(con, codRecurso);
                    tipoRecurso = "ALOJAMIENTO";
                    break;
                case 2:
                    TipoReservaDAO.mostrarActividades(con);
                    System.out.println("Introduzca el codigo de la actividad:");
                    codRecurso = leer.nextInt();
                    recurso = TipoReservaDAO.obtenerActividadPorId(con, codRecurso);
                    tipoRecurso = "ACTIVIDAD";
                    break;
                case 3:
                    TipoReservaDAO.mostrarSalasEvento(con);
                    System.out.println("Introduzca el codigo de la sala de evento:");
                    codRecurso = leer.nextInt();
                    recurso = TipoReservaDAO.obtenerSalaEventoPorId(con, codRecurso);
                    tipoRecurso = "SALA_EVENTO";
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
            fechaInicio = new Date(anioInicio - 1900, mesInicio - 1, diaInicio);

            System.out.println("Introduzca el anio de fin:");
            int anioFin = leer.nextInt();
            System.out.println("Introduzca el mes de fin (1-12):");
            int mesFin = leer.nextInt();
            System.out.println("Introduzca el dia de fin:");
            int diaFin = leer.nextInt();
            fechaFin = new Date(anioFin - 1900, mesFin - 1, diaFin);

            if (!fechaFin.after(fechaInicio)) {
                System.out.println("La fecha de fin debe ser posterior a la fecha de inicio.");
                valido = false;
            }
        }

        if (valido && haySolapamiento(con, tipoRecurso, recurso.getCod(), fechaInicio, fechaFin)) {
            System.out.println("ERROR: El recurso ya esta reservado en esas fechas.");
            valido = false;
        }

        if (valido) {
            Reserva reserva = new Reserva(0, cliente, recurso, fechaInicio, fechaFin);
            double precioTotal = reserva.calcularPrecioTotal();
            Statement stmt = null;
            ResultSet rs = null;
            int codReserva = 0;
            try {
                stmt = con.createStatement();
                stmt.executeUpdate("INSERT INTO reservas (cod_cliente, tipo_recurso, cod_recurso, fecha_inicio, fecha_fin, estado, precio_total) VALUES (" + codCliente + ", '" + tipoRecurso + "', " + recurso.getCod() + ", '" + fechaInicio + "', '" + fechaFin + "', 'Alta', " + precioTotal + ")");
                rs = stmt.executeQuery("SELECT cod_reserva FROM reservas ORDER BY cod_reserva DESC LIMIT 1");
                if (rs.next()) {
                    codReserva = rs.getInt("cod_reserva");
                    System.out.println("Reserva creada con codigo: " + codReserva);
                }
            } catch (SQLException e) {
                System.err.println("Error de base de datos en la operación:");
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            }
            Reserva reservaFinal = new Reserva(codReserva, cliente, recurso, fechaInicio, fechaFin);
            generarFactura(reservaFinal);
        }
    }

    /**
     * Comprueba si existe solapamiento de fechas para un recurso concreto.
     * Dos reservas se solapan si: fechaInicio menor que otraFechaFin
     * Y fechaFin mayor que otraFechaInicio.
     * @param con         Conexión activa.
     * @param tipoRecurso Tipo del recurso ('ALOJAMIENTO','ACTIVIDAD','SALA_EVENTO').
     * @param codRecurso  Código del recurso.
     * @param fechaInicio Fecha de inicio propuesta.
     * @param fechaFin    Fecha de fin propuesta.
     * @return true si hay solapamiento, false si el recurso está libre.
     */
    public static boolean haySolapamiento(Connection con, String tipoRecurso, int codRecurso, Date fechaInicio, Date fechaFin) {
        Statement stmt = null;
        ResultSet rs = null;
        boolean solapa = false;
        try {
            stmt = con.createStatement();
            String query = "SELECT cod_reserva FROM reservas" + " WHERE tipo_recurso = '" + tipoRecurso + "'" + " AND cod_recurso = " + codRecurso + " AND estado = 'Alta'" + " AND fecha_inicio < '" + fechaFin + "'" + " AND fecha_fin > '"    + fechaInicio + "'";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                solapa = true;
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return solapa;
    }

    /**
     * Recupera y muestra por consola todas las reservas almacenadas en la BD haciendo un JOIN con la tabla clientes para mostrar el nombre del cliente.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public static void mostrarReservas(Connection con) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT r.*, c.nombre AS nombre_cliente, c.dni FROM reservas r JOIN clientes c ON r.cod_cliente = c.cod_cliente";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("**********************************");
                System.out.println("Codigo Reserva: " + rs.getInt("cod_reserva"));
                System.out.println("Cliente:        " + rs.getString("nombre_cliente") + " (DNI: " + rs.getString("dni") + ")");
                System.out.println("Tipo Recurso:   " + rs.getString("tipo_recurso") + " (Cod: " + rs.getInt("cod_recurso") + ")");
                System.out.println("Fecha Inicio:   " + rs.getDate("fecha_inicio"));
                System.out.println("Fecha Fin:      " + rs.getDate("fecha_fin"));
                System.out.println("Estado:         " + rs.getString("estado"));
                System.out.println("Precio Total:   " + rs.getDouble("precio_total") + " EUR");
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

        if (!ClienteDAO.existeCliente(con, codCliente)) {
            System.out.println("Cliente no encontrado.");
        } else {
            Statement stmt = null;
            ResultSet rs = null;
            String query = "SELECT r.*, c.nombre AS nombre_cliente FROM reservas r JOIN clientes c ON r.cod_cliente = c.cod_cliente WHERE r.cod_cliente = " + codCliente;
            try {
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);
                boolean hayReservas = false;
                while (rs.next()) {
                    hayReservas = true;
                    System.out.println("**********************************");
                    System.out.println("Codigo Reserva: " + rs.getInt("cod_reserva"));
                    System.out.println("Cliente:        " + rs.getString("nombre_cliente"));
                    System.out.println("Tipo Recurso:   " + rs.getString("tipo_recurso") + " (Cod: " + rs.getInt("cod_recurso") + ")");
                    System.out.println("Fecha Inicio:   " + rs.getDate("fecha_inicio"));
                    System.out.println("Fecha Fin:      " + rs.getDate("fecha_fin"));
                    System.out.println("Estado:         " + rs.getString("estado"));
                    System.out.println("Precio Total:   " + rs.getDouble("precio_total") + " EUR");
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
     * Modifica las fechas de una reserva existente mediante un menú por consola.
     * Realiza la actualización de forma simple mediante una sentencia SQL UPDATE tradicional.
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de datos.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void modificarReserva(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo de la reserva a modificar");
        int cod = leer.nextInt();
    
        if (!existeReserva(con, cod)) {
            System.out.println("Reserva no encontrada.");
        } else {
            int opcion = 0;
            PreparedStatement ps = null;
            do {
                System.out.println("Elija que modificar");
                System.out.println("1. Fecha Inicio");
                System.out.println("2. Fecha Fin");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                
                if (opcion != 0) {
                    String campo = (opcion == 1) ? "fecha_inicio" : "fecha_fin";
                    System.out.println("Introduzca el año:");
                    int anio = leer.nextInt();
                    System.out.println("Introduzca el mes (1-12):");
                    int mes = leer.nextInt();
                    System.out.println("Introduzca el dia:");
                    int dia = leer.nextInt();

                    Date nuevaFecha = new Date(anio - 1900, mes - 1, dia);
                    
                    String sql = "UPDATE reservas SET " + campo + " = ? WHERE cod_reserva = ?";
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
                        if (ps != null) {
                            ps.close();
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
    
        if (!existeReserva(con, cod)) {
            System.out.println("Reserva no encontrada.");
        } else {
            String sql = "UPDATE reservas SET estado = 'Baja' WHERE cod_reserva = ?";
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
        Statement stmt = null;
        ResultSet rs = null;
        boolean existe = false;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT cod_reserva FROM reservas");
            while (rs.next()) {
                if(rs.getInt("cod_reserva") == codigo) {
                    existe = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
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
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            File dir = new File("Facturas");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File f = new File(dir, nombreFichero);
            fw = new FileWriter(f, false); // false = sobreescribir
            pw = new PrintWriter(fw);

            pw.println("============================================");
            pw.println("            FACTURA DE RESERVA              ");
            pw.println("============================================");
            pw.println("Codigo Reserva: " + reserva.getCod());
            pw.println("Fecha Emision:  " + new java.util.Date());
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
    public void menu(Connection con, Scanner leer) throws SQLException {
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
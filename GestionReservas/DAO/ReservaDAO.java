package GestionReservas.DAO;

import HotelReservas.ENTIDAD.Actividad;
import HotelReservas.ENTIDAD.Alojamiento;
import HotelReservas.ENTIDAD.Cliente;
import HotelReservas.ENTIDAD.Reserva;
import HotelReservas.ENTIDAD.SalaEvento;
import HotelReservas.ENTIDAD.TipoReserva;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;
import java.util.Scanner;

/**
 * Operaciones y gestión de reservas.
 * Incluye:
 *   - Validación de solapamiento de fechas mediante BD.
 *   - Generación de factura en fichero .txt mediante FileWriter.
 *   - Almacenamiento de objetos Reserva en fichero mediante ObjectOutputStream / ObjectInputStream.
 *
 * Tabla esperada en BD:
 * CREATE TABLE reservas (
 *     cod_reserva   INT AUTO_INCREMENT PRIMARY KEY,
 *     cod_cliente   INT         NOT NULL,
 *     tipo_recurso  VARCHAR(20) NOT NULL,
 *     cod_recurso   INT         NOT NULL,
 *     fecha_inicio  DATE        NOT NULL,
 *     fecha_fin     DATE        NOT NULL,
 *     estado        VARCHAR(20) NOT NULL DEFAULT 'Alta',
 *     precio_total  DOUBLE      NOT NULL,
 *     FOREIGN KEY (cod_cliente) REFERENCES clientes(cod_cliente)
 * );
 *
 * @author David Catalán Aragó
 */
public class ReservaDAO {

    /**
     * Almacena un objeto Reserva en el fichero 'reservas.obj'.
     * Utiliza MiObjectOutputStream para poder añadir objetos sin corromper la cabecera del stream.
     * @param reserva Objeto Reserva a serializar.
     */
    public static void guardarReservaEnFichero(Reserva reserva) {
        FileOutputStream fs = null;
        MiObjectOutputStream oos = null;
        try {
            fs  = new FileOutputStream("reservas.obj", true);
            oos = new MiObjectOutputStream(fs);
            oos.writeObject(reserva);
            System.out.println("Reserva guardada en fichero correctamente");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) { oos.close(); }
                if (fs  != null) { fs.close();  }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Lee y muestra por consola todas las reservas almacenadas en el fichero 'reservas.obj' mediante ObjectInputStream.
     */
    public static void mostrarReservasDeFichero() {
        File f = null;
        FileInputStream fe = null;
        ObjectInputStream ois = null;
        try {
            f = new File("reservas.obj");
            if (f.exists()) {
                fe  = new FileInputStream(f);
                ois = new ObjectInputStream(fe);
                System.out.println("===== RESERVAS EN FICHERO =====");
                while (true) {
                    Reserva r = (Reserva) ois.readObject();
                    System.out.println(r.toString());
                }
            } else {
                System.out.println("El fichero de reservas no existe todavía");
            }
        } catch (EOFException eof) {
            System.out.println("Fin de fichero de reservas");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) { ois.close(); }
                if (fe  != null) { fe.close();  }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Genera una factura en fichero de texto plano .txt con los datos de la reserva.
     * El fichero se guarda como 'factura_CODRESERVA.txt' en el directorio de trabajo.
     * @param reserva Reserva para la que se genera la factura.
     */
    public static void generarFactura(Reserva reserva) {
        String nombreFichero = "factura_" + reserva.getCod() + ".txt";
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            File f = new File(nombreFichero);
            fw = new FileWriter(f, false); // false = sobreescribir
            pw = new PrintWriter(fw);

            pw.println("============================================");
            pw.println("           FACTURA DE RESERVA              ");
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
            pw.println("       Gracias por su reserva");
            pw.println("============================================");

            if (fw != null) {
                fw.close();
            }
            System.out.println("Factura generada correctamente: " + nombreFichero);

        } catch (IOException e) {
            System.out.println("Error al generar la factura: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea una nueva reserva solicitando los datos por consola.
     * Valida que el recurso no esté reservado en las fechas indicadas mediante una consulta SQL de solapamiento.
     * Si la reserva es válida la inserta en BD, genera la factura .txt y serializa el objeto en el fichero binario reservas.obj.
     * @param leer Scanner para la entrada de datos.
     * @param con  Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante la operación.
     */
    public static void crearReserva(Scanner leer, Connection con) throws SQLException {
        boolean valido = true;
        Cliente cliente = null;
        TipoReserva recurso = null;
        String tipoRecurso = "";
        Date fechaInicio = null;
        Date fechaFin = null;

        // Seleccionar cliente
        System.out.println("Introduzca el codigo del cliente");
        int codCliente = Integer.parseInt(leer.nextLine());
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
            int tipoOpcion = Integer.parseInt(leer.nextLine());
            switch (tipoOpcion) {
                case 1:
                    TipoReservaDAO.mostrarAlojamientos(con);
                    System.out.println("Introduzca el codigo del alojamiento");
                    recurso = TipoReservaDAO.obtenerAlojamientoPorId(con, Integer.parseInt(leer.nextLine()));
                    tipoRecurso = "ALOJAMIENTO";
                    break;
                case 2:
                    TipoReservaDAO.mostrarActividades(con);
                    System.out.println("Introduzca el codigo de la actividad");
                    recurso = TipoReservaDAO.obtenerActividadPorId(con, Integer.parseInt(leer.nextLine()));
                    tipoRecurso = "ACTIVIDAD";
                    break;
                case 3:
                    TipoReservaDAO.mostrarSalasEvento(con);
                    System.out.println("Introduzca el codigo de la sala de evento");
                    recurso = TipoReservaDAO.obtenerSalaEventoPorId(con, Integer.parseInt(leer.nextLine()));
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

        // Fechas
        if (valido) {
            System.out.println("Introduzca el anio de inicio");
            int anioInicio = Integer.parseInt(leer.nextLine());
            System.out.println("Introduzca el mes de inicio (1-12)");
            int mesInicio = Integer.parseInt(leer.nextLine());
            System.out.println("Introduzca el dia de inicio");
            int diaInicio = Integer.parseInt(leer.nextLine());
            Calendar calInicio = Calendar.getInstance();
            calInicio.set(anioInicio, mesInicio - 1, diaInicio);
            fechaInicio = calInicio.getTime();

            System.out.println("Introduzca el anio de fin");
            int anioFin = Integer.parseInt(leer.nextLine());
            System.out.println("Introduzca el mes de fin (1-12)");
            int mesFin = Integer.parseInt(leer.nextLine());
            System.out.println("Introduzca el dia de fin");
            int diaFin = Integer.parseInt(leer.nextLine());
            Calendar calFin = Calendar.getInstance();
            calFin.set(anioFin, mesFin - 1, diaFin);
            fechaFin = calFin.getTime();

            if (!fechaFin.after(fechaInicio)) {
                System.out.println("La fecha de fin debe ser posterior a la fecha de inicio.");
                valido = false;
            }
        }

        // Validar solapamiento
        if (valido && haySolapamiento(con, tipoRecurso, recurso.getCod(), fechaInicio, fechaFin)) {
            System.out.println("ERROR: El recurso ya esta reservado en esas fechas.");
            valido = false;
        }

        // Insertar en BD, generar factura y guardar en fichero
        if (valido) {
            double precioTotal = new Reserva(0, cliente, recurso, fechaInicio, fechaFin).calcularPrecioTotal();
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
                ConexionBD.printSQLException(e);
            } finally {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            }
            Reserva reservaFinal = new Reserva(codReserva, cliente, recurso, fechaInicio, fechaFin);
            generarFactura(reservaFinal);
            guardarReservaEnFichero(reservaFinal);
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
        boolean solapa = false;
        try {
            stmt = con.createStatement();
            String query = "SELECT cod_reserva FROM reservas" + " WHERE tipo_recurso = '" + tipoRecurso + "'" + " AND cod_recurso = " + codRecurso + " AND estado = 'Alta'" + " AND fecha_inicio < '" + fechaFin + "'" + " AND fecha_fin > '"    + fechaInicio + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                solapa = true;
            }
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
        } finally {
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
        String query = "SELECT r.*, c.nombre AS nombre_cliente, c.dni " + "FROM reservas r JOIN clientes c ON r.cod_cliente = c.cod_cliente";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
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
            ConexionBD.printSQLException(e);
        } finally {
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
        int codCliente = Integer.parseInt(leer.nextLine());

        if (!ClienteDAO.existeCliente(con, codCliente)) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        Statement stmt = null;
        String query = "SELECT r.*, c.nombre AS nombre_cliente FROM reservas r "
                + "JOIN clientes c ON r.cod_cliente = c.cod_cliente "
                + "WHERE r.cod_cliente = " + codCliente;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            boolean hayReservas = false;
            while (rs.next()) {
                hayReservas = true;
                System.out.println("**********************************");
                System.out.println("Codigo Reserva: " + rs.getInt("cod_reserva"));
                System.out.println("Cliente:        " + rs.getString("nombre_cliente"));
                System.out.println("Tipo Recurso:   " + rs.getString("tipo_recurso")
                        + " (Cod: " + rs.getInt("cod_recurso") + ")");
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
            ConexionBD.printSQLException(e);
        } finally {
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Modifica los datos de una reserva existente mediante un menú por consola.
     * Utiliza ResultSet TYPE_SCROLL_SENSITIVE y CONCUR_UPDATABLE (unidad 8).
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de datos.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void modificarReserva(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo de la reserva a modificar");
        int cod = Integer.parseInt(leer.nextLine());
    
        if (!existeReserva(con, cod)) {
            System.out.println("Reserva no encontrada.");
        } else {
            int opcion = 0;
            Statement stmt = null;
            do {
                System.out.println("Elija que modificar");
                System.out.println("1. Fecha Inicio");
                System.out.println("2. Fecha Fin");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                if (opcion != 0) {
                    String campo = (opcion == 1) ? "fecha_inicio" : "fecha_fin";
                    System.out.println("Introduzca la nueva fecha (YYYY-MM-DD)");
                    String nuevaFecha = leer.nextLine();
                    try {
                        stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery("SELECT * FROM reservas WHERE cod_reserva = " + cod);
                        while (rs.next()) { rs.updateString(campo, nuevaFecha); rs.updateRow(); System.out.println("Campo actualizado correctamente"); }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        if (stmt != null) stmt.close();
                    }
                }
            } while (opcion != 0);
        }
    }

    /**
     * Cancela una reserva existente cambiando su estado a 'Baja'.
     * Utiliza ResultSet TYPE_SCROLL_SENSITIVE y CONCUR_UPDATABLE (unidad 8).
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada del código de reserva.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void cancelarReserva(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo de la reserva a cancelar");
        int cod = Integer.parseInt(leer.nextLine());
    
        if (!existeReserva(con, cod)) {
            System.out.println("Reserva no encontrada.");
        } else {
            Statement stmt = null;
            try {
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery("SELECT * FROM reservas WHERE cod_reserva = " + cod);
                while (rs.next()) {
                    if (rs.getString("estado").equalsIgnoreCase("Baja")) {
                        System.out.println("La reserva ya estaba cancelada.");
                    } else {
                        rs.updateString("estado", "Baja");
                        rs.updateRow();
                        System.out.println("Reserva cancelada correctamente.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (stmt != null) stmt.close();
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
        boolean existe = false;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT cod_reserva FROM reservas");
            while (rs.next()) {
                if(rs.getInt("cod_reserva") == codigo) {
                    existe = true;
                }
            }
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return existe;
    }

    /**
     * Menú principal de gestión de reservas con las 5 opciones requeridas.
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
            System.out.println("6. Ver reservas guardadas en fichero");
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
                case 6: 
                    mostrarReservasDeFichero();
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

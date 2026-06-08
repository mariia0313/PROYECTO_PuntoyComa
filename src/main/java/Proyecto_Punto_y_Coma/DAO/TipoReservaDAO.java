package Proyecto_Punto_y_Coma.DAO;

import Proyecto_Punto_y_Coma.ENTIDAD.Actividad;
import Proyecto_Punto_y_Coma.ENTIDAD.Alojamiento;
import Proyecto_Punto_y_Coma.ENTIDAD.SalaEvento;
import java.sql.*;
import java.util.Scanner;

/**
 * Operaciones para los recursos reservables del hotel:
 * Alojamiento, Actividad y SalaEvento.
 * @author David Catalán Aragó
 */
public class TipoReservaDAO {


    /**
     * Registra un nuevo alojamiento en la base de datos solicitando los datos por consola.
     * @param leer Scanner para la entrada de datos.
     * @param con  Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante el INSERT.
     */
    public static void crearAlojamiento(Scanner leer, Connection con) throws SQLException {
        System.out.println("Introduzca el nombre del alojamiento");
        String nombre = leer.nextLine();

        System.out.println("Introduzca el precio base por noche");
        double precioBase = leer.nextDouble();

        System.out.println("Introduzca el IVA (ej: 0.21 para 21%)");
        double iva = leer.nextDouble();

        System.out.println("Introduzca la capacidad de personas");
        int capacidad = leer.nextInt();
        leer.nextLine();

        System.out.println("Introduzca el tipo de alojamiento (ej: Habitacion Doble, Suite)");
        String tipoAlojamiento = leer.nextLine();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO alojamientos (nombre, precio_base, iva, capacidad, tipo_alojamiento, estado) VALUES ('" + nombre + "', " + precioBase + ", " + iva + ", " + capacidad + ", '" + tipoAlojamiento + "', 'Disponible')");
            rs = stmt.executeQuery("SELECT cod FROM alojamientos WHERE nombre = '" + nombre + "' ORDER BY cod DESC LIMIT 1");
              if (rs.next()) System.out.println("Alojamiento creado con codigo: " + rs.getInt("cod"));
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Recupera y muestra por consola todos los alojamientos almacenados.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public static void mostrarAlojamientos(Connection con) throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM alojamientos");
            while (rs.next()) {
                System.out.println("**********************************");
                System.out.println("Codigo: " + rs.getInt("cod"));
                System.out.println("Nombre: " + rs.getString("nombre"));
                System.out.println("Precio base/noche: " + rs.getDouble("precio_base"));
                System.out.println("IVA: " + (rs.getDouble("iva") * 100) + "%");
                System.out.println("Capacidad: " + rs.getInt("capacidad"));
                System.out.println("Tipo: " + rs.getString("tipo_alojamiento"));
                System.out.println("Estado: " + rs.getString("estado"));
            }
            System.out.println("**********************************");
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Modifica un campo de un alojamiento existente mediante un menu por consola.
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la navegación y entrada de nuevos datos.
     * @throws SQLException Si ocurre un error al actualizar el registro.
     */
    public static void modificarAlojamiento(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo del alojamiento a modificar");
        int cod = leer.nextInt();
        if (!existeRecurso(con, "alojamientos", "cod", cod)) {
            System.out.println("Alojamiento no encontrado en la base de datos");
        } else{
            int opcion = 0;
            PreparedStatement ps = null;
            do {
                System.out.println("Elija que modificar");
                System.out.println("1. Nombre");
                System.out.println("2. Precio base");
                System.out.println("3. IVA");
                System.out.println("4. Capacidad");
                System.out.println("5. Tipo alojamiento");
                System.out.println("6. Estado");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                leer.nextLine();

                String campo = "";
                switch (opcion) {
                    case 1:
                        campo = "nombre";
                        break;
                    case 2:
                        campo = "precio_base";
                        break;
                    case 3:
                        campo = "iva";
                        break;
                    case 4:
                        campo = "capacidad";
                        break;
                    case 5:
                        campo = "tipo_alojamiento";
                        break;
                    case 6:
                        campo = "estado";
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        break;
                    default: 
                    System.out.println("Opcion no valida");
                    break;
                }
                if (opcion > 0 && opcion <= 6) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    String nuevo = leer.nextLine();
                    String sql = "UPDATE alojamientos SET " + campo + " = ? WHERE cod = ?";
                    try {
                        ps = con.prepareStatement(sql);
                        switch (opcion) {
                            case 2:
                            case 3:
                                ps.setDouble(1, Double.parseDouble(nuevo));
                                break;
                            case 4:
                                ps.setInt(1, Integer.parseInt(nuevo));
                                break;
                            default:
                                ps.setString(1, nuevo);
                                break;
                        }
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
     * Recupera un Alojamiento de la BD por su código.
     * @param con    Conexión activa a la base de datos.
     * @param codigo Código del alojamiento.
     * @return Objeto Alojamiento o null si no se encuentra.
     * @throws SQLException Si hay un error en la consulta.
     */
    public static Alojamiento obtenerAlojamientoPorId(Connection con, int codigo) throws SQLException {
        Alojamiento alojamiento = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM alojamientos WHERE cod = " + codigo);
            if (rs.next()) alojamiento = new Alojamiento(rs.getInt("cod"), rs.getString("nombre"), rs.getDouble("precio_base"), rs.getDouble("iva"), rs.getInt("capacidad"), rs.getString("tipo_alojamiento"), rs.getString("estado"));
            else System.out.println("Aviso: No se encontró alojamiento con código " + codigo);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        return alojamiento;
    }

    /**
     * Elimina un alojamiento de la base de datos a partir de su código identificador.
     * Realiza una baja directa utilizando un PreparedStatement de tipo DELETE.
     * Si el recurso está referenciado en otra tabla, captura la excepción para evitar que el programa se detenga.
     * @param con  Conexión activa a la base de datos MySQL.
     * @param leer Scanner para la lectura del código por consola.
     * @throws SQLException Si ocurre un error inesperado en la base de datos.
     */
    public static void eliminarAlojamiento(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el código del alojamiento a eliminar:");
        int cod = leer.nextInt();

        String sql = "DELETE FROM alojamientos WHERE cod = ?";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, cod);
            int filas = ps.executeUpdate();
            if (filas > 0){
                System.out.println("Alojamiento eliminado correctamente.");
            }else{
                System.out.println("Alojamiento no encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("No se puede eliminar: el recurso está en uso en alguna reserva.");
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Registra una nueva actividad en la base de datos solicitando los datos por consola.
     * @param leer Scanner para la entrada de datos.
     * @param con  Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante el INSERT.
     */
    public static void crearActividad(Scanner leer, Connection con) throws SQLException {
        System.out.println("Introduzca el nombre de la actividad");
        String nombre = leer.nextLine();

        System.out.println("Introduzca el precio base");
        double precioBase = leer.nextDouble();

        System.out.println("Introduzca el IVA (ej: 0.21 para 21%)");
        double iva = leer.nextDouble();

        System.out.println("Introduzca la capacidad maxima de participantes");
        int capacidad = leer.nextInt();
        leer.nextLine();

        System.out.println("Introduzca la hora de inicio (ej: 10:00)");
        String horaInicio = leer.nextLine();

        System.out.println("Introduzca la hora de fin (ej: 12:00)");
        String horaFin = leer.nextLine();

        System.out.println("Introduzca el estado (Disponible / Completa / Cancelada)");
        String estado = leer.nextLine();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO actividades (nombre, precio_base, iva, capacidad, hora_inicio, hora_fin, estado) VALUES ('" + nombre + "', " + precioBase + ", " + iva + ", " + capacidad + ", '" + horaInicio + "', '" + horaFin + "', '" + estado + "')");
            rs = stmt.executeQuery("SELECT cod FROM actividades WHERE nombre = '" + nombre + "' ORDER BY cod DESC LIMIT 1");
            if (rs.next()) System.out.println("Actividad creada con codigo: " + rs.getInt("cod"));
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Recupera y muestra por consola todas las actividades almacenadas.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public static void mostrarActividades(Connection con) throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM actividades");
            while (rs.next()) {
                System.out.println("**********************************");
                System.out.println("Codigo: " + rs.getInt("cod"));
                System.out.println("Nombre: " + rs.getString("nombre"));
                System.out.println("Precio base: " + rs.getDouble("precio_base"));
                System.out.println("IVA: " + (rs.getDouble("iva") * 100) + "%");
                System.out.println("Capacidad: " + rs.getInt("capacidad"));
                System.out.println("Hora inicio: " + rs.getString("hora_inicio"));
                System.out.println("Hora fin: " + rs.getString("hora_fin"));
                System.out.println("Estado: " + rs.getString("estado"));
            }
            System.out.println("**********************************");
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Modifica las características de una actividad existente mediante un menú por consola.
     * Realiza la actualización de forma simple mediante una sentencia SQL UPDATE tradicional.
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de datos.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void modificarActividad(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo de la actividad a modificar");
        int cod = leer.nextInt();
        
        if (!existeRecurso(con, "actividades", "cod", cod)) {
            System.out.println("Actividad no encontrada en la base de datos");
        } else {
            int opcion = 0;
            PreparedStatement ps = null;
            do {
                System.out.println("Elija que modificar");
                System.out.println("1. Nombre");
                System.out.println("2. Precio base");
                System.out.println("3. IVA");
                System.out.println("4. Capacidad");
                System.out.println("5. Hora inicio");
                System.out.println("6. Hora fin");
                System.out.println("7. Estado");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                leer.nextLine();
                
                String campo = "";
                switch (opcion) {
                    case 1:
                        campo = "nombre";
                        break;
                    case 2:
                        campo = "precio_base";
                        break;
                    case 3:
                        campo = "iva";
                        break;
                    case 4:
                        campo = "capacidad";
                        break;
                    case 5: 
                        campo = "hora_inicio";
                        break;
                    case 6:
                        campo = "hora_fin";
                        break;
                    case 7:
                        campo = "estado";
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opcion no valida");
                        break;
                }
                
                if (opcion > 0 && opcion <= 7) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    String nuevo = leer.nextLine();
                    
                    String sql = "UPDATE actividades SET " + campo + " = ? WHERE cod = ?";
                    try {
                        ps = con.prepareStatement(sql);
                        switch (opcion) {
                            case 2:
                            case 3:
                                ps.setDouble(1, Double.parseDouble(nuevo));
                                break;
                            case 4:
                                ps.setInt(1, Integer.parseInt(nuevo));
                                break;
                            default:
                                ps.setString(1, nuevo);
                                break;
                        }
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
     * Recupera una Actividad de la BD por su código.
     * @param con    Conexión activa a la base de datos.
     * @param codigo Código de la actividad.
     * @return Objeto Actividad o null si no se encuentra.
     * @throws SQLException Si hay un error en la consulta.
     */
    public static Actividad obtenerActividadPorId(Connection con, int codigo) throws SQLException {
        Actividad actividad = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM actividades WHERE cod = " + codigo);
            if (rs.next()) actividad = new Actividad(rs.getInt("cod"), rs.getString("nombre"), rs.getDouble("precio_base"), rs.getDouble("iva"), rs.getInt("capacidad"), rs.getObject("hora_inicio", java.time.LocalTime.class), rs.getObject("hora_fin", java.time.LocalTime.class), rs.getString("estado"));
            else System.out.println("Aviso: No se encontró alojamiento con código " + codigo);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        return actividad;
    }

    /**
     * Elimina una actividad de la base de datos a partir de su código identificador.
     * Realiza una baja directa utilizando un PreparedStatement de tipo DELETE.
     * Si el recurso está referenciado en otra tabla, captura la excepción para evitar que el programa se detenga.
     * @param con  Conexión activa a la base de datos MySQL.
     * @param leer Scanner para la lectura del código por consola.
     * @throws SQLException Si ocurre un error inesperado en la base de datos.
     */
    public static void eliminarActividad(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el código de la actividad a eliminar:");
        int cod = leer.nextInt();
        String sql = "DELETE FROM actividades WHERE cod = ?";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, cod);
            int filas = ps.executeUpdate();
            if (filas > 0){
                System.out.println("Actividad eliminada correctamente.");
            } else{
                System.out.println("Actividad no encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("No se puede eliminar: el recurso está en uso en alguna reserva.");
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Registra una nueva sala de evento en la base de datos solicitando los datos por consola.
     * @param leer Scanner para la entrada de datos.
     * @param con  Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante el INSERT.
     */
    public static void crearSalaEvento(Scanner leer, Connection con) throws SQLException {
        System.out.println("Introduzca el nombre de la sala");
        String nombre = leer.nextLine();

        System.out.println("Introduzca el precio base por hora");
        double precioBase = leer.nextDouble();

        System.out.println("Introduzca el IVA (ej: 0.21 para 21%)");
        double iva = leer.nextDouble();

        System.out.println("Introduzca la capacidad maxima de personas");
        int capacidad = leer.nextInt();
        leer.nextLine();

        System.out.println("Introduzca la hora de inicio disponible (ej: 09:00)");
        String horaInicio = leer.nextLine();

        System.out.println("Introduzca la hora de fin disponible (ej: 22:00)");
        String horaFin = leer.nextLine();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO salas_evento (nombre, precio_base, iva, capacidad, hora_inicio, hora_fin, estado) VALUES ('" + nombre + "', " + precioBase + ", " + iva + ", " + capacidad + ", '" + horaInicio + "', '" + horaFin + "', 'Disponible')");
            rs = stmt.executeQuery("SELECT cod FROM salas_evento WHERE nombre = '" + nombre + "' ORDER BY cod DESC LIMIT 1");
            if (rs.next()) System.out.println("Sala de evento creada con codigo: " + rs.getInt("cod"));
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Recupera y muestra por consola todas las salas de evento almacenadas.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public static void mostrarSalasEvento(Connection con) throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM salas_evento");
            while (rs.next()) {
                System.out.println("**********************************");
                System.out.println("Codigo: " + rs.getInt("cod"));
                System.out.println("Nombre sala: " + rs.getString("nombre"));
                System.out.println("Precio base/hora: "+ rs.getDouble("precio_base"));
                System.out.println("IVA: " + (rs.getDouble("iva") * 100) + "%");
                System.out.println("Capacidad: " + rs.getInt("capacidad"));
                System.out.println("Hora inicio: " + rs.getString("hora_inicio"));
                System.out.println("Hora fin: " + rs.getString("hora_fin"));
                System.out.println("Estado: " + rs.getString("estado"));
            }
            System.out.println("**********************************");
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Modifica las características de una sala de eventos existente mediante un menú por consola.
     * Realiza la actualización de forma simple mediante una sentencia SQL UPDATE tradicional.
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de datos.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void modificarSalaEvento(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo de la sala a modificar");
        int cod = leer.nextInt();
        
        if (!existeRecurso(con, "salas_evento", "cod", cod)) {
            System.out.println("Sala de evento no encontrada en la base de datos");
        } else {
            int opcion = 0;
            PreparedStatement ps = null;
            do {
                System.out.println("Elija que modificar");
                System.out.println("1. Nombre");
                System.out.println("2. Precio base");
                System.out.println("3. IVA");
                System.out.println("4. Capacidad");
                System.out.println("5. Hora inicio");
                System.out.println("6. Hora fin");
                System.out.println("7. Estado");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                leer.nextLine();
                
                String campo = "";
                switch (opcion) {
                    case 1:
                        campo = "nombre";
                        break;
                    case 2:
                        campo = "precio_base";
                        break;
                    case 3:
                        campo = "iva";
                        break;
                    case 4:
                        campo = "capacidad";
                        break;
                    case 5:
                        campo = "hora_inicio";
                        break;
                    case 6:
                        campo = "hora_fin";
                        break;
                    case 7:
                        campo = "estado";
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opcion no valida");
                        break;
                }
                
                if (opcion > 0 && opcion <= 7) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    String nuevo = leer.nextLine();
                    
                    String sql = "UPDATE salas_evento SET " + campo + " = ? WHERE cod = ?";
                    try {
                        ps = con.prepareStatement(sql);
                        switch (opcion) {
                            case 2:
                            case 3:
                                ps.setDouble(1, Double.parseDouble(nuevo));
                                break;
                            case 4:
                                ps.setInt(1, Integer.parseInt(nuevo));
                                break;
                            default:
                                ps.setString(1, nuevo);
                                break;
                        }
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
     * Recupera una SalaEvento de la BD por su código.
     * @param con    Conexión activa a la base de datos.
     * @param codigo Código de la sala.
     * @return Objeto SalaEvento o null si no se encuentra.
     * @throws SQLException Si hay un error en la consulta.
     */
    public static SalaEvento obtenerSalaEventoPorId(Connection con, int codigo) throws SQLException {
        SalaEvento sala = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM salas_evento WHERE cod = " + codigo);
            if (rs.next()) sala = new SalaEvento(rs.getInt("cod"), rs.getString("nombre"), rs.getDouble("precio_base"), rs.getDouble("iva"), rs.getInt("capacidad"), rs.getObject("hora_inicio", java.time.LocalTime.class), rs.getObject("hora_fin", java.time.LocalTime.class), rs.getString("estado"));
            else System.out.println("Aviso: No se encontró alojamiento con código " + codigo);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        return sala;
    }

    /**
     * Elimina una sala evento de la base de datos a partir de su código identificador.
     * Realiza una baja directa utilizando un PreparedStatement de tipo DELETE.
     * Si el recurso está referenciado en otra tabla, captura la excepción para evitar que el programa se detenga.
     * @param con  Conexión activa a la base de datos MySQL.
     * @param leer Scanner para la lectura del código por consola.
     * @throws SQLException Si ocurre un error inesperado en la base de datos.
     */
    public static void eliminarSalaEvento(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el código de la sala a eliminar:");
        int cod = leer.nextInt();
        String sql = "DELETE FROM salas_evento WHERE cod = ?";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, cod);
            int filas = ps.executeUpdate();
            if (filas > 0){
                System.out.println("Sala eliminada correctamente.");
            }else{
                System.out.println("Sala no encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("No se puede eliminar: el recurso está en uso en alguna reserva.");
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Verifica si existe un registro con el código dado en la tabla y columna indicadas.
     * @param con     Conexión activa a la base de datos.
     * @param tabla   Nombre de la tabla a consultar.
     * @param columna Nombre de la columna clave.
     * @param codigo  Código del recurso a buscar.
     * @return true si el registro existe, false en caso contrario.
     */
    public static boolean existeRecurso(Connection con, String tabla, String columna, int codigo) {
        Statement stmt = null;
        boolean existe = false;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + columna + " FROM " + tabla + " WHERE " + columna + " = " + codigo);
            if (rs.next()) {
                existe = true;
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        }
        return existe;
    }

    /**
     * Menú de gestión de tipos de reserva (recursos del hotel).
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de opciones.
     * @throws SQLException Si ocurre un error en las operaciones llamadas.
    */
    public void menu(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("===== GESTIÓN DE RECURSOS =====");
            System.out.println("1. Alojamientos");
            System.out.println("2. Actividades");
            System.out.println("3. Salas de Evento");
            System.out.println("0. Volver al menú principal");
            opcion = leer.nextInt();
            leer.nextLine();

            switch (opcion) {
                case 1: menuAlojamiento(con, leer); break;
                case 2: menuActividad(con, leer); break;
                case 3: menuSalaEvento(con, leer); break;
                case 0: break;
                default: System.out.println("Opcion no valida"); break;
            }
        } while (opcion != 0);
    }

    /**
     * Gestiona el submenú interactivo para las operaciones de los Alojamientos.
     * Permite usar las acciones de inserción, edición, lectura y borrado.
     * @param con  Conexión activa a la base de datos MySQL.
     * @param leer Scanner para la captura de datos y opciones.
     * @throws SQLException Si ocurre un error en las consultas de alojamiento ejecutadas.
    */
    private void menuAlojamiento(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("--- ALOJAMIENTOS ---");
            System.out.println("1. Anadir alojamiento");
            System.out.println("2. Modificar alojamiento");
            System.out.println("3. Ver alojamientos");
            System.out.println("4. Eliminar alojamiento");
            System.out.println("0. Volver");
            opcion = leer.nextInt();
            leer.nextLine();

            switch (opcion) {
                case 1: crearAlojamiento(leer, con); break;
                case 2: modificarAlojamiento(con, leer); break;
                case 3: mostrarAlojamientos(con); break;
                case 4: eliminarAlojamiento(con, leer); break;
                case 0: break;
                default: System.out.println("Opcion no valida"); break;
            }
        } while (opcion != 0);
    }

    /**
     * Gestiona el submenú interactivo para las operaciones de las Actividades.
     * Permite usar las acciones de inserción, edición, lectura y borrado.
     * @param con  Conexión activa a la base de datos MySQL.
     * @param leer Scanner para la captura de datos y opciones.
     * @throws SQLException Si ocurre un error en las consultas de alojamiento ejecutadas.
    */
    private void menuActividad(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("--- ACTIVIDADES ---");
            System.out.println("1. Anadir actividad");
            System.out.println("2. Modificar actividad");
            System.out.println("3. Ver actividades");
            System.out.println("4. Eliminar actividad");
            System.out.println("0. Volver");
            opcion = leer.nextInt();
            leer.nextLine();

            switch (opcion) {
                case 1: crearActividad(leer, con); break;
                case 2: modificarActividad(con, leer); break;
                case 3: mostrarActividades(con); break;
                case 4: eliminarActividad(con, leer); break;
                case 0: break;
                default: System.out.println("Opcion no valida"); break;
            }
        } while (opcion != 0);
    }

    /**
     * Gestiona el submenú interactivo para las operaciones de las Salas de Eventos.
     * Permite usar las acciones de inserción, edición, lectura y borrado.
     * @param con  Conexión activa a la base de datos MySQL.
     * @param leer Scanner para la captura de datos y opciones.
     * @throws SQLException Si ocurre un error en las consultas de alojamiento ejecutadas.
    */
    private void menuSalaEvento(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("--- SALAS DE EVENTO ---");
            System.out.println("1. Anadir sala de evento");
            System.out.println("2. Modificar sala de evento");
            System.out.println("3. Ver salas de evento");
            System.out.println("4. Eliminar sala de evento");
            System.out.println("0. Volver");
            opcion = leer.nextInt();
            leer.nextLine();
            
            switch (opcion) {
                case 1: crearSalaEvento(leer, con); break;
                case 2: modificarSalaEvento(con, leer); break;
                case 3: mostrarSalasEvento(con); break;
                case 4: eliminarSalaEvento(con, leer); break;
                case 0: break;
                default: System.out.println("Opcion no valida"); break;
            }
        } while (opcion != 0);
    }
}

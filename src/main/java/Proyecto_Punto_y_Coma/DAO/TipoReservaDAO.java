package Proyecto_Punto_y_Coma.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Scanner;

import Proyecto_Punto_y_Coma.ENTIDAD.Actividad;
import Proyecto_Punto_y_Coma.ENTIDAD.Alojamiento;
import Proyecto_Punto_y_Coma.ENTIDAD.SalaEvento;

/**
 * Operaciones para los recursos reservables del hotel:
 * Alojamiento, Actividad y SalaEvento.
 * @author David Catalán Aragó
 */
public class TipoReservaDAO {


    public static double leerIVA(Scanner leer){
        double iva=0;
        boolean valido = false;
        while (!valido) {
            System.out.println("Introduzca el IVA entre 0 y 1(ej: 0.21 para 21%)");
            double entrada = ConexionBD.leerDouble(leer);
            if (entrada >= 0 && entrada <= 1) {
                iva = entrada;
                valido = true;
            } else {
                System.out.println("IVA no válido. Debe estar entre 0 y 1. Por ejemplo 0.21 para 21%");
            }
        }
        return iva;
    }

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
        double precioBase = ConexionBD.leerDouble(leer);

        double iva = leerIVA(leer);

        System.out.println("Introduzca la capacidad de personas");
        int capacidad = ConexionBD.leerEntero(leer);

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
        int cod = ConexionBD.leerEntero(leer);
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
                opcion = ConexionBD.leerEntero(leer);
                
                String nuevo = "";
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
                if (opcion == 1 || opcion == 5) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    nuevo = leer.nextLine();
                }else if(opcion == 2){
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    nuevo = String.valueOf(ConexionBD.leerEntero(leer));

                }else if(opcion == 3) {
                    nuevo = String.valueOf(leerIVA(leer));
                    
                } else if (opcion == 4) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    nuevo = String.valueOf(ConexionBD.leerEntero(leer));
                } else if (opcion == 6) {
                    int opcion2;
                    do{
                        System.out.println("Elija el nuevo estado");
                        System.out.println("1. Disponible");
                        System.out.println("2. No Disponible");
                        opcion2 = ConexionBD.leerEntero(leer);
                        switch (opcion2) {
                            case 1:
                                nuevo = "Disponible";
                                break;
                            case 2:
                                nuevo = "No Disponible";
                                break;
                            default:
                                System.out.println("Opcion no valida");
                                break;
                        }
                    } while (nuevo.isEmpty());
                }
                if (opcion > 0 && opcion < 7) {
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
     * Registra una nueva actividad en la base de datos solicitando los datos por consola.
     * @param leer Scanner para la entrada de datos.
     * @param con  Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante el INSERT.
     */
    public static void crearActividad(Scanner leer, Connection con) throws SQLException {
        Time horaInicio = null;
        Time horaFin = null;

        System.out.println("Introduzca el nombre de la actividad");
        String nombre = leer.nextLine();

        System.out.println("Introduzca el precio base");
        double precioBase = ConexionBD.leerDouble(leer);

        System.out.println("Introduzca el IVA (ej: 0.21 para 21%)");
        double iva = leerIVA(leer);

        System.out.println("Introduzca la capacidad maxima de participantes");
        int capacidad = ConexionBD.leerEntero(leer);

        do {
            System.out.println("Introduzca la hora de inicio (ej: 09:00):");
            String entrada = leer.nextLine();
            try {
                if (entrada.length() == 5) entrada += ":00";
                horaInicio = Time.valueOf(entrada);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Formato inválido. Use el formato HH:mm");
            }
        } while (horaInicio == null);

        do {
            System.out.println("Introduzca la hora de fin (ej: 22:00):");
            String entrada = leer.nextLine();
            try {
                if (entrada.length() == 5) entrada += ":00";
                Time hFin = Time.valueOf(entrada);
                
                if (hFin.after(horaInicio)) {
                    horaFin = hFin;
                } else {
                    System.out.println("Error: La hora de fin debe ser posterior a la de inicio.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Formato inválido. Use el formato HH:mm");
            }
        } while (horaFin == null);

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO actividades (nombre, precio_base, iva, capacidad, hora_inicio, hora_fin) VALUES ('" + nombre + "', " + precioBase + ", " + iva + ", " + capacidad + ", '" + horaInicio + "', '" + horaFin + "')");
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
        int cod = ConexionBD.leerEntero(leer);
        
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
                opcion = ConexionBD.leerEntero(leer);
                
                String nuevo = "";
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
                
                if (opcion == 1) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    nuevo = leer.nextLine();
                    
                } else if(opcion == 2) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    nuevo = String.valueOf(ConexionBD.leerEntero(leer));
                } else if(opcion == 3) {
                    nuevo = String.valueOf(leerIVA(leer));
                    
                } else if (opcion == 4) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    nuevo = String.valueOf(ConexionBD.leerEntero(leer));
                } else if (opcion == 5){
                    Time hora=null;
                    do {
                        System.out.println("Introduzca la hora de inicio nueva (ej: 09:00):");
                        String entrada = leer.nextLine();
                        try {
                            if (entrada.length() == 5) entrada += ":00";
                            hora = Time.valueOf(entrada);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: Formato inválido. Use el formato HH:mm");
                        }
                    } while (hora == null);
                    nuevo = hora.toString();
                } else if (opcion == 6) {
                    Time horaFin = null;
                    Time horaInicio = null;

                    String sqlBusqueda = "SELECT hora_inicio FROM actividades WHERE cod = ?";
                    try (PreparedStatement psBusqueda = con.prepareStatement(sqlBusqueda)) {
                        psBusqueda.setInt(1, cod);
                        try (ResultSet rs = psBusqueda.executeQuery()) {
                            if (rs.next()) {
                                horaInicio = rs.getTime("hora_inicio");
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Error al recuperar la hora de inicio: " + e.getMessage());
                    }

                    if (horaInicio != null) {
                        do {
                            System.out.println("La actividad comienza a las: " + horaInicio);
                            System.out.println("Introduzca la nueva hora de fin (ej: 22:00):");
                            String entrada = leer.nextLine();
                            try {
                                if (entrada.length() == 5) entrada += ":00";
                                Time hora = Time.valueOf(entrada);

                                if (hora.after(horaInicio)) {
                                    horaFin = hora;
                                } else {
                                    System.out.println("Error: La hora de fin debe ser posterior a la de inicio (" + horaInicio + ").");
                                }
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: Formato inválido. Use el formato HH:mm");
                            }
                        } while (horaFin == null);
                        nuevo = horaFin.toString();
                    } else {
                        System.out.println("Error: No se pudo encontrar la hora de inicio para esta actividad.");
                    }
                }else if (opcion == 7) {
                    int opcion2;
                    do{
                        System.out.println("Elija el nuevo estado");
                        System.out.println("1. Disponible");
                        System.out.println("2. No Disponible");
                        opcion2 = ConexionBD.leerEntero(leer);
                        switch (opcion2) {
                            case 1:
                                nuevo = "Disponible";
                                break;
                            case 2:
                                nuevo = "No Disponible";
                                break;
                            default:
                                System.out.println("Opcion no valida");
                                break;
                        }
                    } while (nuevo.isEmpty());
                }
                if(opcion > 0 && opcion < 8){
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
                            case 5:
                            case 6:
                                ps.setTime(1, Time.valueOf(nuevo));
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
            if (rs.next()) actividad = new Actividad(rs.getInt("cod"), rs.getString("nombre"), rs.getDouble("precio_base"), rs.getDouble("iva"), rs.getInt("capacidad"), rs.getTime("hora_inicio"), rs.getTime("hora_fin"), rs.getString("estado"));
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
     * Registra una nueva sala de evento en la base de datos solicitando los datos por consola.
     * @param leer Scanner para la entrada de datos.
     * @param con  Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante el INSERT.
     */
    public static void crearSalaEvento(Scanner leer, Connection con) throws SQLException {
        Time horaInicio = null;
        Time horaFin = null;

        System.out.println("Introduzca el nombre de la sala");
        String nombre = leer.nextLine();

        System.out.println("Introduzca el precio base por hora");
        double precioBase = ConexionBD.leerDouble(leer);

        System.out.println("Introduzca el IVA (ej: 0.21 para 21%)");
        double iva = leerIVA(leer);

        System.out.println("Introduzca la capacidad maxima de personas");
        int capacidad = ConexionBD.leerEntero(leer);

        do {
            System.out.println("Introduzca la hora de inicio (ej: 09:00):");
            String entrada = leer.nextLine();
            try {
                if (entrada.length() == 5) entrada += ":00";
                horaInicio = Time.valueOf(entrada);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Formato inválido. Use el formato HH:mm");
            }
        } while (horaInicio == null);

        do {
            System.out.println("Introduzca la hora de fin (ej: 22:00):");
            String entrada = leer.nextLine();
            try {
                if (entrada.length() == 5) entrada += ":00";
                Time hFin = Time.valueOf(entrada);
                
                if (hFin.after(horaInicio)) {
                    horaFin = hFin;
                } else {
                    System.out.println("Error: La hora de fin debe ser posterior a la de inicio.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Formato inválido. Use el formato HH:mm");
            }
        } while (horaFin == null);

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
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de datos.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void modificarSalaEvento(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el codigo de la sala a modificar");
        int cod = ConexionBD.leerEntero(leer);
        
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
                opcion = ConexionBD.leerEntero(leer);
                
                String nuevo = "";
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
                
               if (opcion == 1) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    nuevo = leer.nextLine();
                    
                } else if(opcion == 2) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    nuevo = String.valueOf(ConexionBD.leerEntero(leer));

                } else if(opcion == 3) {
                    nuevo = String.valueOf(leerIVA(leer));
                    
                } else if (opcion == 4) {
                    System.out.println("Introduzca el nuevo valor para " + campo);
                    nuevo = String.valueOf(ConexionBD.leerEntero(leer));

                } else if (opcion == 5) {
                    Time hora=null;
                    do {
                        System.out.println("Introduzca la hora de inicio nueva (ej: 09:00):");
                        String entrada = leer.nextLine();
                        try {
                            if (entrada.length() == 5) entrada += ":00";
                            hora = Time.valueOf(entrada);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: Formato inválido. Use el formato HH:mm");
                        }
                    } while (hora == null);
                    nuevo = hora.toString();
                } else if (opcion == 6) {
                    Time horaFin = null;
                    Time horaInicio = null;

                    String sqlBusqueda = "SELECT hora_inicio FROM salas_evento WHERE cod = ?";
                    try (PreparedStatement psBusqueda = con.prepareStatement(sqlBusqueda)) {
                        psBusqueda.setInt(1, cod);
                        try (ResultSet rs = psBusqueda.executeQuery()) {
                            if (rs.next()) {
                                horaInicio = rs.getTime("hora_inicio");
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Error al recuperar la hora de inicio: " + e.getMessage());
                    }

                    if (horaInicio != null) {
                        do {
                            System.out.println("La actividad comienza a las: " + horaInicio);
                            System.out.println("Introduzca la nueva hora de fin (ej: 22:00):");
                            String entrada = leer.nextLine();
                            try {
                                if (entrada.length() == 5) entrada += ":00";
                                Time hora = Time.valueOf(entrada);

                                if (hora.after(horaInicio)) {
                                    horaFin = hora;
                                } else {
                                    System.out.println("Error: La hora de fin debe ser posterior a la de inicio (" + horaInicio + ").");
                                }
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: Formato inválido. Use el formato HH:mm");
                            }
                        } while (horaFin == null);
                        nuevo = horaFin.toString();
                    } else {
                        System.out.println("Error: No se pudo encontrar la hora de inicio para esta actividad.");
                    }
                } else if (opcion == 7) {
                    int opcion2;
                    do{
                        System.out.println("Elija el nuevo estado");
                        System.out.println("1. Disponible");
                        System.out.println("2. No Disponible");
                        opcion2 = ConexionBD.leerEntero(leer);
                        switch (opcion2) {
                            case 1:
                                nuevo = "Disponible";
                                break;
                            case 2:
                                nuevo = "No Disponible";
                                break;
                            default:
                                System.out.println("Opcion no valida");
                                break;
                        }
                    } while (nuevo.isEmpty());
                }
                if(opcion > 0 && opcion < 8){
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
                            case 5:
                            case 6:
                                ps.setTime(1, Time.valueOf(nuevo));
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
            if (rs.next()) sala = new SalaEvento(rs.getInt("cod"), rs.getString("nombre"), rs.getDouble("precio_base"), rs.getDouble("iva"), rs.getInt("capacidad"), rs.getTime("hora_inicio"), rs.getTime("hora_fin"), rs.getString("estado"));
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
            opcion = ConexionBD.leerEntero(leer);

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
            System.out.println("1. Añadir alojamiento");
            System.out.println("2. Modificar alojamiento");
            System.out.println("3. Ver alojamientos");
            System.out.println("0. Volver");
            opcion = ConexionBD.leerEntero(leer);

            switch (opcion) {
                case 1: crearAlojamiento(leer, con); break;
                case 2: modificarAlojamiento(con, leer); break;
                case 3: mostrarAlojamientos(con); break;
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
            System.out.println("1. Añadir actividad");
            System.out.println("2. Modificar actividad");
            System.out.println("3. Ver actividades");
            System.out.println("0. Volver");
            opcion = ConexionBD.leerEntero(leer);

            switch (opcion) {
                case 1: crearActividad(leer, con); break;
                case 2: modificarActividad(con, leer); break;
                case 3: mostrarActividades(con); break;
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
            System.out.println("1. Añadir sala de evento");
            System.out.println("2. Modificar sala de evento");
            System.out.println("3. Ver salas de evento");
            System.out.println("0. Volver");
            opcion = ConexionBD.leerEntero(leer);

            switch (opcion) {
                case 1: crearSalaEvento(leer, con); break;
                case 2: modificarSalaEvento(con, leer); break;
                case 3: mostrarSalasEvento(con); break;
                case 0: break;
                default: System.out.println("Opcion no valida"); break;
            }
        } while (opcion != 0);
    }
}

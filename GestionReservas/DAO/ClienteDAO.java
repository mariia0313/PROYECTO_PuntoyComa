package GestionReservas.DAO;

import HotelReservas.ENTIDAD.Cliente;
import java.sql.*;
import java.util.Scanner;

/**
 * Operaciones y gestión de datos para la entidad Cliente.
 *
 * Tabla esperada en BD:
 * CREATE TABLE clientes (
 *     cod_cliente  INT AUTO_INCREMENT PRIMARY KEY,
 *     nombre       VARCHAR(100) NOT NULL,
 *     dni          VARCHAR(20)  NOT NULL UNIQUE,
 *     email        VARCHAR(100),
 *     telefono     VARCHAR(20)
 * );
 *
 * @author David Catalán Aragó
 */
public class ClienteDAO {

    /**
     * Registra un nuevo cliente en la base de datos solicitando los datos por consola.
     * @param leer Scanner para la entrada de datos.
     * @param con  Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante el INSERT.
     */
    public static void crearCliente(Scanner leer, Connection con) throws SQLException {
        System.out.println("Introduzca el nombre del cliente");
        String nombre = leer.nextLine();
        System.out.println("Introduzca el DNI del cliente");
        String dni = leer.nextLine();
        System.out.println("Introduzca el email del cliente");
        String email = leer.nextLine();
        System.out.println("Introduzca el teléfono del cliente");
        String telefono = leer.nextLine();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            String insert = "INSERT INTO clientes (nombre, dni, email, telefono) VALUES ('"+ nombre + "', '" + dni + "', '" + email + "', '" + telefono + "')";
            stmt.executeUpdate(insert);

            rs = stmt.executeQuery("SELECT cod_cliente FROM clientes WHERE dni = '" + dni + "'");
            if (rs.next()) {
                System.out.println("Cliente creado con código: " + rs.getInt("cod_cliente"));
            }
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Recupera y muestra por consola todos los clientes almacenados.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public static void mostrarClientes(Connection con) throws SQLException {
        Statement stmt = null;
        String query = "SELECT * FROM clientes";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("**********************************");
                System.out.println("Código: " + rs.getInt("cod_cliente"));
                System.out.println("Nombre: " + rs.getString("nombre"));
                System.out.println("DNI: " + rs.getString("dni"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Teléfono: " + rs.getString("telefono"));
            }
            System.out.println("**********************************");
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
        } finally {
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Verifica si existe un cliente con el código dado.
     * @param con    Conexión activa a la base de datos.
     * @param codigo Código del cliente a buscar.
     * @return true si existe, false en caso contrario.
     */
    public static boolean existeCliente(Connection con, int codigo) {
        Statement stmt = null;
        boolean existe = false;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT cod_cliente FROM clientes");
            while (rs.next()) {
                if (rs.getInt("cod_cliente") == codigo) {
                    existe = true;
                }
            }
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
        }
        return existe;
    }

    /**
     * Recupera un cliente de la base de datos por su código.
     * @param con Conexión activa a la base de datos.
     * @param codigo Código del cliente a recuperar.
     * @return Objeto Cliente con sus datos o null si no se encuentra.
     * @throws SQLException Si hay un error en la consulta.
     */
    public static Cliente obtenerClientePorId(Connection con, int codigo) throws SQLException {
        Cliente cliente = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(
                    "SELECT * FROM clientes WHERE cod_cliente = " + codigo);
            if (rs.next()) {
                int cod = rs.getInt("cod_cliente");
                String nombre = rs.getString("nombre");
                String dni = rs.getString("dni");
                String email = rs.getString("email");
                String telefono = rs.getString("telefono");
                cliente = new Cliente(cod, nombre, dni, email, telefono);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        return cliente;
    }

    /**
     * Menú de gestión de clientes.
     * @param con  Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de opciones.
     * @throws SQLException Si ocurre un error en las operaciones llamadas.
     */
    public void menu(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("===== GESTIÓN DE CLIENTES =====");
            System.out.println("1. Añadir cliente");
            System.out.println("2. Ver clientes");
            System.out.println("0. Salir");
            opcion = leer.nextInt();
            switch (opcion) {
                case 1: crearCliente(leer, con); break;
                case 2: mostrarClientes(con);    break;
                case 0: System.out.println("Saliendo..."); break;
                default: System.out.println("Opción no válida"); break;
            }
        } while (opcion != 0);
    }
}

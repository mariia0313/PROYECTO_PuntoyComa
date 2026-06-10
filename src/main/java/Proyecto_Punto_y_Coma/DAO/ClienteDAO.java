package Proyecto_Punto_y_Coma.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import Proyecto_Punto_y_Coma.ENTIDAD.Cliente;

/**
 * Operaciones y gestión de datos para la entidad Cliente.
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
        System.out.println("Introduzca el DNI/NIE del cliente");
        String identificador = leer.nextLine();
        System.out.println("Introduzca el email del cliente");
        String email = leer.nextLine();
        System.out.println("Introduzca el teléfono del cliente");
        String telefono = leer.nextLine();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String insert = "INSERT INTO clientes (nombre, identificador, email, telefono) VALUES (?, ?, ?, ?)";
            ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nombre);
            ps.setString(2, identificador);
            ps.setString(3, email);
            ps.setString(4, telefono);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Cliente creado con código: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos en la operación:");
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
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
                System.out.println("Identificador: " + rs.getString("identificador"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Teléfono: " + rs.getString("telefono"));
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
            ResultSet rs = stmt.executeQuery("SELECT cod FROM clientes WHERE cod = " + codigo);
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
            rs = stmt.executeQuery("SELECT * FROM clientes WHERE cod = " + codigo);
            if (rs.next()) {
                int cod = rs.getInt("cod_cliente");
                String nombre = rs.getString("nombre");
                String identificador = rs.getString("identificador");
                String email = rs.getString("email");
                String telefono = rs.getString("telefono");
                String estado = rs.getString("estado");
                cliente = new Cliente(cod, nombre, identificador, email, telefono, estado);
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
     * Cambia el estado de un cliente a 'Inactivo' en la base de datos.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para la entrada del código.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public static void eliminarCliente(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el código del cliente a eliminar:");
        int cod = leer.nextInt();
        leer.nextLine();
    
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE clientes SET estado = 'Inactivo' WHERE cod_cliente = ?");
            ps.setInt(1, cod);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Cliente desactivado correctamente.");
            } else {
                System.out.println("Cliente no encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Menú de gestión de clientes.
     */
    public void menu(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("===== GESTIÓN DE CLIENTES =====");
            System.out.println("1. Añadir cliente");
            System.out.println("2. Ver clientes");
            System.out.println("3. Eliminar cliente");
            System.out.println("0. Salir");
            opcion = leer.nextInt();
            leer.nextLine();
                        
            switch (opcion) {
                case 1: 
                    crearCliente(leer, con); 
                    break;
                case 2: 
                    mostrarClientes(con);    
                    break;
                case 3:
                    eliminarCliente(con, leer);
                    break;
                case 0: 
                    System.out.println("Saliendo..."); 
                    break;
                default: 
                    System.out.println("Opción no válida"); 
                    break;
            }
        } while (opcion != 0);
    }
}

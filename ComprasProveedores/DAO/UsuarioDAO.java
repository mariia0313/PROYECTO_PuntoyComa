package ComprasProveedores.DAO;

import ComprasProveedores.ENTIDADES.Empleado;
import ComprasProveedores.ENTIDADES.Usuario;
import java.util.Scanner;
import java.sql.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Clase de acceso a datos (DAO) para la gestión de usuarios del sistema.
 * Se encarga del registro de credenciales y la validación de acceso (login).
 * @author María Herrero Rodríguez
 */
public class UsuarioDAO {

    private static void printSQLException(SQLException e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    /**
     * Registra un nuevo usuario en el sistema vinculado a un empleado
     * existente. Incluye validación de doble contraseña y verifica que el
     * empleado no tenga ya un usuario activo.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para capturar el código de empleado, nombre de
     * usuario y contraseñas.
     * @throws SQLException Si ocurre un error en la inserción o en la
     * recuperación del ID generado.
     */
    public void crearUsuario(Connection con, Scanner leer) throws SQLException{
        EmpleadosDAO empDAO = new EmpleadosDAO();
        
        System.out.println("Introduzca su codigo de empleado");
        int cod = leer.nextInt();
        if (empDAO.existeEmpleado(con, cod) == false){
                System.out.println("No existe empleado con ese código");
            } else {
            leer.nextLine();
            if (existeUsuario(con, cod)) {
                System.out.println("Ya existe un usuario asociado a ese empleado");
                } else {
                    System.out.println("Introduzca el nombre de usuario");
                    String nombre = leer.nextLine();
                    System.out.println("Introduzca la contraseña");
                    String contra = leer.nextLine();
                    System.out.println("Vuelva a introducir su contraseña para confirmar");
                    String contra2 = leer.nextLine();
                    while (!contra.equals(contra2)) {
                        System.out.println("Las contrasenñas no coinciden. Intétalo de nuevo");
                        System.out.println("Introduzca la contraseña");
                        contra = leer.nextLine();
                        System.out.println("Vuelva a introducir su contraseña para confirmar");
                        contra2 = leer.nextLine();
                    }

                    if (contra.equals(contra2)) {
                        Usuario usuario = new Usuario(nombre, contra, cod);

                        ResultSet rs = null;
                        Statement stmt = null;
                        int idUsuario = 0;
                        try {
                            stmt = con.createStatement();
                            String insert = "INSERT INTO usuarios (nom_user, contrasenya, empleado) VALUES ('" + nombre + "', '" + contra + "', '" + cod + "')";
                            stmt.executeUpdate(insert);
                            String query = "SELECT id_user FROM usuarios WHERE empleado = '" + cod + "'";
                            rs = stmt.executeQuery(query);
                            if (rs.next()) {
                                idUsuario = rs.getInt("id_user");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            stmt.close();
                        }

                        usuario.setId(idUsuario);
                    }
        }
        
        }

        
    }
    
    /**
     * Verifica si un empleado ya dispone de un usuario registrado en la base de datos.
     * @param con Conexión activa a la base de datos.
     * @param codigo Identificador numérico del empleado.
     * @return true si el empleado ya tiene un usuario asociado, false en caso contrario.
     */
    public static boolean existeUsuario(Connection con, int codigo) {
        Statement stmt = null;
        String query = "SELECT empleado FROM usuarios";
        boolean existe = false;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int cod = rs.getInt("empleado");
                if (cod == codigo) {
                    existe = true;
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return existe;
    }
    
    /**
     * Gestiona el proceso de autenticación en el sistema.
     * Compara las credenciales introducidas con la base de datos y, si son correctas, 
     * recupera el objeto Empleado asociado a esa cuenta.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para introducir nombre de usuario y contraseña.
     * @return El objeto {@link Empleado} que ha iniciado sesión, o null si la autenticación falla.
     */
    public Empleado inicioSesion(Connection con, Scanner leer) {
            EmpleadosDAO empDAO = new EmpleadosDAO();
            Empleado empleado = null;
            System.out.println("Introduzca su nombre de usuario");
            String nombre = leer.nextLine();
            System.out.println("Introduzca su contraseña");
            String contra = leer.nextLine();
            Statement stmt = null;
            
        String query = "SELECT contrasenya FROM usuarios WHERE nom_user LIKE '" + nombre + "'";
        boolean coincide = false;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String contra2 = rs.getString("contrasenya");
                if (contra.equals(contra2)) {
                    coincide = true;
                    
                    stmt = null;
                    query = "SELECT empleado from usuarios where nom_user like '" + nombre + "'";
                    try {
                        stmt = con.createStatement();
                        rs = stmt.executeQuery(query);
                        while (rs.next()) {
                           int cod = rs.getInt("empleado");
                           empleado = empDAO.obtenerEmpleadoPorId(con, cod);
                        }
                    } catch (SQLException e) {
                        printSQLException(e);
                    }
                    
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        
        if (!coincide) {
            System.out.println("Credenciales incorrectas. Vuelva a intentarlo o cree un usuario");
        }
         
        return empleado;
    }
    
    

}

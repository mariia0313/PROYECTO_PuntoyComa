package Proyecto_Punto_y_Coma.DAO;

import Proyecto_Punto_y_Coma.ENTIDAD.Empleado;
import Proyecto_Punto_y_Coma.ENTIDAD.Usuario;
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
        throw new UnsupportedOperationException("Error de Base de Datos: " + e.getMessage()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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

                        String insert = "INSERT INTO usuarios (nom_user, contrasenya, empleado) VALUES (?, ?, ?)";
                        String query = "SELECT id_user FROM usuarios WHERE empleado = ?";
                        int idUsuario = 0;
                        try (PreparedStatement pstmtInsert = con.prepareStatement(insert); PreparedStatement pstmtQuery = con.prepareStatement(query)) {
                            pstmtInsert.setString(1, nombre);
                            pstmtInsert.setString(2, contra);
                            pstmtInsert.setInt(3, cod);
                            pstmtInsert.executeUpdate();

                            pstmtQuery.setInt(1, cod);
                            try (ResultSet rs = pstmtQuery.executeQuery()) {
                                if (rs.next()) {
                                    idUsuario = rs.getInt("id_user");
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
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
        boolean existe = false;
        String query = "SELECT 1 FROM usuarios WHERE empleado = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    existe = true;
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            
        String query = "SELECT id_user, contrasenya, empleado FROM usuarios WHERE nom_user = ?";
        boolean coincide = false;
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String contra2 = rs.getString("contrasenya");
                    if (contra.equals(contra2)) {
                        coincide = true;
                        int cod = rs.getInt("empleado");
                        empleado = empDAO.obtenerEmpleadoPorId(con, cod);
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
    
    /**
     * Proporciona una interfaz para modificar las credenciales de un usuario.
     * Permite cambiar el nombre de usuario o actualizar la contraseña con
     * validación.
     *
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de datos.
     * @throws SQLException Si ocurre un error al actualizar la base de datos.
     */
    public void modificarUsuario(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el código del empleado cuyo usuario desea modificar:");
        int cod = leer.nextInt();
        leer.nextLine();

        if (!existeUsuario(con, cod)) {
            System.out.println("Este empleado no tiene un usuario asociado.");
            return;
        }

        System.out.println("¿Qué desea modificar?");
        System.out.println("1. Nombre de usuario");
        System.out.println("2. Contraseña");
        System.out.print("Elija una opción: ");
        int opcion = ConexionBD.leerEntero(leer);

        try {
            String query = "SELECT * FROM usuarios WHERE empleado = ?";
            PreparedStatement pstmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstmt.setInt(1, cod);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if (opcion == 1) {
                    System.out.println("Introduzca el nuevo nombre de usuario:");
                    String nuevoNombre = leer.nextLine();
                    rs.updateString("nom_user", nuevoNombre);
                    rs.updateRow();
                    System.out.println("Nombre de usuario actualizado.");
                } else if (opcion == 2) {
                    System.out.println("Introduzca la nueva contraseña:");
                    String contra = leer.nextLine();
                    System.out.println("Confirme la nueva contraseña:");
                    String contra2 = leer.nextLine();

                    if (contra.equals(contra2)) {
                        rs.updateString("contrasenya", contra);
                        rs.updateRow();
                        System.out.println("Contraseña actualizada con éxito.");
                    } else {
                        System.out.println("Las contraseñas no coinciden. Operación cancelada.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Elimina el usuario asociado a un empleado de la base de datos.
     * En este caso, elimino directamente en vez de cambiar el estado porque el usuario no tiene ningún funcionamiento mas allá de acceder a unas opciones u otras
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para capturar el código de empleado.
     * @throws SQLException Si ocurre un error durante el borrado.
     */
    public void eliminarUsuario(Connection con, Scanner leer) throws SQLException {
        System.out.println("Introduzca el código del empleado cuyo usuario desea eliminar:");
        int cod = leer.nextInt();
        leer.nextLine();

        if (!existeUsuario(con, cod)) {
            System.out.println("No existe un usuario para ese empleado.");
            return;
        } else {
            System.out.println("¿Está seguro de que desea eliminar el acceso de este usuario? (S/N)");
            String confirmar = leer.nextLine();

            if (confirmar.equalsIgnoreCase("S")) {
                String delete = "DELETE FROM usuarios WHERE empleado = ?";
                try (PreparedStatement pstmt = con.prepareStatement(delete)) {
                    pstmt.setInt(1, cod);
                    int filas = pstmt.executeUpdate();
                    if (filas > 0) {
                        System.out.println("Usuario eliminado correctamente.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        }
    }

    /**
     * Muestra el menú de gestión de accesos y usuarios. Permite crear,
     * modificar, eliminar y gestionar el inicio de sesión.
     *
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de opciones.
     * @throws SQLException Si ocurre un error en las consultas SQL.
     */
    public void menu(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE ACCESOS Y USUARIOS ---");
            System.out.println("1. Crear nuevo usuario");
            System.out.println("2. Modificar usuario existente");
            System.out.println("3. Eliminar acceso de usuario");
            System.out.println("4. Mostrar usuarios en el sistema");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elija una opción: ");

            opcion = ConexionBD.leerEntero(leer);

            switch (opcion) {
                case 1:
                    crearUsuario(con, leer);
                    break;
                case 2:
                    modificarUsuario(con, leer);
                    break;
                case 3:
                    eliminarUsuario(con, leer);
                    break;
                case 4:
                    mostrarUsuarios(con);
                    break;
                case 0:
                    System.out.println("Regresando...");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } while (opcion != 0);
    }
    
    /**
     * Recupera y muestra por pantalla una lista simplificada de todos los
     * usuarios registrados en la base de datos. Muestra el ID de usuario, el
     * nombre de acceso (login) y el código del empleado vinculado.
     * * @param con Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public void mostrarUsuarios(Connection con) throws SQLException {
        String query = "SELECT id_user, nom_user, empleado FROM usuarios";

        System.out.println("\n--- LISTADO DE USUARIOS ---");

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id_user");
                String login = rs.getString("nom_user");
                int codEmp = rs.getInt("empleado");

                System.out.println("ID: " + id + " | Login: " + login + " | Cód. Empleado: " + codEmp);
            }
            System.out.println("---------------------------\n");

        } catch (SQLException e) {
            System.out.println("Error al mostrar los usuarios: " + e.getMessage());
        }
    }
}

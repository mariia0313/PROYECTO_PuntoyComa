/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComprasProveedores.DAO;
import ComprasProveedores.ENTIDAD.Proveedor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class ProveedorDAO {
    // CREACIÓN DE PROVEEDOR
    public static void crearProveedor(Scanner leer, Connection con) throws SQLException {

        System.out.println("Introduzca el CIF del nuevo proveedor");
        String cif = leer.nextLine();
        System.out.println("Introduzca el nombre del nuevo proveedor");
        String nombre = leer.nextLine();
        System.out.println("Introduzca el email del nuevo proveedor");
        String email = leer.nextLine();
        System.out.println("Introduzca el teléfono del nuevo proveedor");
        String telf = leer.nextLine();

        Proveedor proveedor = new Proveedor(cif, nombre, email, telf);
        ResultSet rs = null;
        Statement stmt = null;
        int idProveedor = 0;
        try {
            stmt = con.createStatement();
            String insert = "INSERT INTO proveedores (Id_proveedor, telefono, nombre, email) VALUES ('" + cif + "', '" + telf + "', '" + nombre + "', '" + email + "')";
            stmt.executeUpdate(insert);
            String query = "SELECT cod_proveedor FROM proveedores WHERE Id_proveedor = '" + cif + "'";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                idProveedor = rs.getInt("cod_proveedor");
                System.out.println("Se ha generado el ID: " + idProveedor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }

        proveedor.setCodigo(idProveedor);
    }

    // VER PROVEEDORES CREADOS EN LA BASE DE DATOS
    public static void mostrarProveedores(Connection con) throws SQLException {
        Statement stmt = null;
        String query = "SELECT * from proveedores";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int codigo = rs.getInt("Cod_proveedor");
                System.out.println("Código de proveedor: " + codigo);
                String cif = rs.getString("Id_proveedor");
                System.out.println("CIF: " + cif);
                String telef = rs.getString("Telefono");
                System.out.println("Telefono: " + telef);
                String nombre = rs.getString("Nombre");
                System.out.println("Nombre de proveedor: " + nombre);
                String email = rs.getString("Email");
                System.out.println("Email de proveedor: " + email);
                Boolean estado1 = rs.getBoolean("Estado");
                String estado = "Inactivo";
                if (estado1 == true) {
                    estado = "Activo";
                }
                System.out.println("Estado del proveedor: " + estado);
                System.out.println("**********************************");
            }
        } catch (SQLException e) {
            printSQLException(e);
        } finally {
            stmt.close();

        }
    }

    // BUSCAR POR CODIGO PROVEEDOR EXISTE 
    public static boolean existeProveedor(Connection con, int codigo) {
        Statement stmt = null;
        String query = "SELECT Cod_proveedor FROM proveedores";
        boolean existe = false;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int cod = rs.getInt("Cod_proveedor");
                if (cod == codigo) {
                    existe = true;
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return existe;
    }

    // MODIFICAR PROVEEDOR
    public static void modificarProveedor(Connection con, Scanner leer) throws SQLException {
        int opcion = 0;
        int cod = 0;
        Statement stmt = null;
        String nuevo = "";
        String campo = "";
        System.out.println("Inserte el código del proveedor a modificar");
        cod = leer.nextInt();
        if (existeProveedor(con, cod) == true) {
            // MENÚ PATA ELEGIR QUÉ MODIFICAR
            do {
                System.out.println("Elija qué modificar");
                System.out.println("1. CIF");
                System.out.println("2. Nombre");
                System.out.println("3. Telefono");
                System.out.println("4. Email");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                leer.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.println("Inserte el nuevo CIF");
                        nuevo = leer.nextLine();
                        campo = "Id_proveedor";
                        break;
                    case 2:
                        System.out.println("Inserte el nuevo nombre");
                        nuevo = leer.nextLine();
                        campo = "Nombre";
                        break;
                    case 3:
                        System.out.println("Inserte el nuevo telefono");
                        nuevo = leer.nextLine();
                        campo = "Telefono";
                        break;
                    case 4:
                        System.out.println("Inserte el nuevo email");
                        nuevo = leer.nextLine();
                        campo = "Email";
                        break;

                    case 0:
                        System.out.println("Saliendo...");
                        break;

                    default:
                        System.out.println("Opción no válida");
                        break;
                }

                try {
                    stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    String query = "SELECT * from proveedores where cod_proveedor = " + cod;
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        rs.updateString(campo, nuevo);
                        rs.updateRow();
                        System.out.println("Campo actualizado correctamente");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    stmt.close();
                }

            } while (opcion != 0);
        } else {

            System.out.println("El proveedor no ha sido encontrado en la base de datos");
        }

    }

    // ELIMINAR (DESACTIVAR) PROVEEDOR
    public static void desactivarProveedor(Connection con, Scanner leer) throws SQLException {
        Statement stmt = null;
        System.out.println("Introduzca código del proveedor que desee desactivar");
        int cod = leer.nextInt();
        try {
            stmt = con.createStatement();
            String update = "UPDATE proveedores set estado=false";
            stmt.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }

    }

    private static void printSQLException(SQLException e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

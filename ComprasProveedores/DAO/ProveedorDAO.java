/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComprasProveedores.DAO;

import ComprasProveedores.ENTIDAD.Producto;
import ComprasProveedores.ENTIDAD.Proveedor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import ComprasProveedores.ENTIDADES.Producto;
import ComprasProveedores.ENTIDADES.Proveedor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Clase de acceso a datos (DAO) para la gestión de proveedores.
 * Permite realizar operaciones CRUD y sincronización de objetos con la base de datos.
 * @author María Herrero Rodríguez
 */
public class ProveedorDAO {
    
    /**
     * Registra un nuevo proveedor en la base de datos y muestra el ID autogenerado.
     * @param leer Scanner para la entrada de datos (CIF, nombre, email, teléfono).
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante la inserción o consulta.
     */
    public static void crearProveedor(Scanner leer, Connection con) throws SQLException {

        System.out.println("Introduzca el CIF del nuevo proveedor");
        String cif = leer.nextLine();
        System.out.println("Introduzca el nombre del nuevo proveedor");
        String nombre = leer.nextLine();
        System.out.println("Introduzca el email del nuevo proveedor");
        String email = leer.nextLine();
        System.out.println("Introduzca el teléfono del nuevo proveedor");
        String telf = leer.nextLine();

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
    }

    /**
     * Recupera y lista por consola todos los proveedores almacenados en el sistema.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la ejecución de la consulta SELECT.
     */
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
                String estado = rs.getString("Estado");
                System.out.println("Estado del proveedor: " + estado);
                System.out.println("**********************************");
            }
        } catch (SQLException e) {
            printSQLException(e);
        } finally {
            stmt.close();

        }
    }

    /**
     * Comprueba si un proveedor existe en la base de datos mediante su código interno.
     * @param con Conexión activa a la base de datos.
     * @param codigo Identificador numérico del proveedor.
     * @return true si el proveedor existe, false en caso contrario.
     */ 
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

    /**
     * Permite la modificación dinámica de los atributos de un proveedor.
     * Utiliza un menú interactivo y ResultSets actualizables para aplicar los cambios.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para seleccionar el campo y el nuevo valor.
     * @throws SQLException Si falla la actualización en la fila seleccionada.
     */
    public static void modificarProveedor(Connection con, Scanner leer) throws SQLException {
        int opcion = 0;
        int cod = 0;
        Statement stmt = null;
        String nuevo = "";
        String campo = "";
        System.out.println("Inserte el código del proveedor a modificar");
        cod = leer.nextInt();
        if (existeProveedor(con, cod) == true) {
            do {
                System.out.println("Elija qué modificar");
                System.out.println("1. CIF");
                System.out.println("2. Nombre");
                System.out.println("3. Telefono");
                System.out.println("4. Email");
                System.out.println("5. Modificar estado");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                leer.nextLine();

                switch (opcion) {
                    case 1:
                        campo = "Id_proveedor";
                        break;
                    case 2:
                        campo = "Nombre";
                        break;
                    case 3:
                        campo = "Telefono";
                        break;
                    case 4:
                        campo = "Email";
                        break;
                    case 5:
                        campo = "Estado";
                        break;
                        
                    case 0:
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                        break;
                }

                System.out.println("Introduzca el nuevo valor para " + campo + ":");
                nuevo = leer.nextLine();                
                
                if (opcion != 0){
                    try {
                        stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        String query = "SELECT * FROM proveedores WHERE cod_proveedor = " + cod;
                        ResultSet rs = stmt.executeQuery(query);

                        if (rs.next()) {
                            rs.updateString(campo, nuevo);
                            rs.updateRow();
                            System.out.println("Campo '" + campo + "' actualizado correctamente");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error al actualizar la base de datos: " + e.getMessage());
                    } finally {
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                }

            } while (opcion != 0);
        } else {

            System.out.println("El producto no ha sido encontrado en la base de datos");
        }

    }
    
    /**
     * Gestiona el menú principal de operaciones exclusivas para proveedores.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para la navegación por opciones.
     * @throws SQLException Si ocurre un error en los métodos invocados.
     */
    public void menu(Connection con, Scanner leer) throws SQLException, IOException {
        int opcion;
        do {
            System.out.println("      SISTEMA DE GESTIÓN DE PROVEEDORES    ");
            System.out.println("1. Añadir proveedor");
            System.out.println("2. Modificar proveedor");
            System.out.println("3. Ver proveedores");
            System.out.println("4. Generar informe de los proveedores y sus productos");
            System.out.println("0. Salir");
            opcion = leer.nextInt();
            leer.nextLine();
            switch (opcion) {
                case 1:
                    crearProveedor(leer, con);
                    break;

                case 2:
                    modificarProveedor(con, leer);
                    break;

                case 3:
                    mostrarProveedores(con);
                    break;
                    
                case 4:
                    crearInformeProveedores(con);
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
   
    /**
     * Realiza una carga completa de proveedores y sus productos asociados.
     * Sincroniza la relación uno-a-muchos (Proveedor-Producto) en una lista de objetos.
     * @param con Conexión activa a la base de datos.
     * @return ArrayList de objetos Proveedor, cada uno con su lista de productos cargada.
     */
    public ArrayList<Proveedor> rellenarProductosProveedores(Connection con) {
        String query = "SELECT * from proveedores";

        Statement stmt = null;
        Statement stmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ArrayList<Proveedor> proveedores = new ArrayList<Proveedor>();

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                int cod = rs.getInt("Cod_proveedor");
                String id = rs.getString("Id_proveedor");
                String telf = rs.getString("Telefono");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                String estado = rs.getString("Estado");

                Proveedor proveedor = new Proveedor(cod, id, nombre, email, telf, estado);
                proveedores.add(proveedor);

                //CARGA DE PRODUCTOS
                String query2 = "SELECT * from productos where proveedor = " + cod;
                stmt2 = con.createStatement();
                rs2 = stmt2.executeQuery(query2);

                while (rs2.next()) {
                    int idP = rs2.getInt("ID_producto");
                    String nombreP = rs2.getString("Nombre");
                    int stock = rs2.getInt("Stock");
                    int stockm = rs2.getInt("Stock_minimo");
                    String desc = rs2.getString("Descripcion");
                    double precio = rs2.getDouble("Precio_unidad");
                    String estadoP = rs2.getString("Estado");

                    Producto producto = new Producto(idP, nombreP, desc, stock, stockm, estadoP, cod, precio);
                    proveedor.addProducto(producto);
                }

                if (rs2 != null) {
                    rs2.close();
                }
                if (stmt2 != null) {
                    stmt2.close();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return proveedores;
    }
    
    /**
     * Consulta los proveedores del ArrayList, los añade a la lista y genera el
     * archivo "Informe_Proveedores.html".
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si falla la consulta o el cierre del Statement.
     */
    public void crearInformeProveedores(Connection con) throws SQLException, IOException {
        ArrayList<Proveedor> proveedores = rellenarProductosProveedores(con);
        File f = new File("Informe_Proveedores.html");
        FileWriter fw = null;

        try {
            fw = new FileWriter(f);
            
            fw.write("<html><head><meta charset='UTF-8'><title>Informe Proveedores</title></head><body>");
            fw.write("<h1>Informe de Proveedores y Productos</h1>");

            fw.write("<table border='1' cellpadding='10' cellspacing='0' style='width:100%;'>");
            fw.write("<tr style='background-color: #eee;'><th>Datos del Proveedor</th><th>Catálogo de Productos</th></tr>");

            for (Proveedor p : proveedores) {
                fw.write("<tr>");

                fw.write("<td valign='top'>" + p.toString() + "</td>");

                fw.write("<td>");
                ArrayList<Producto> productos = p.getProductos();

                if (productos != null && !productos.isEmpty()) {
                    fw.write("<ul>");
                    for (Producto prod : productos) {
                        fw.write("<li>" + prod.toString() + "</li>");
                    }
                    fw.write("</ul>");
                } else {
                    fw.write("<i>No hay productos registrados para este proveedor.</i>");
                }

                fw.write("</td>");
                fw.write("</tr>");
            }

            fw.write("</table></body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fw.close();
        }
    }


    private static void printSQLException(SQLException e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

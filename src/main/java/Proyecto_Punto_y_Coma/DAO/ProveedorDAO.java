/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Proyecto_Punto_y_Coma.DAO;

import Proyecto_Punto_y_Coma.ENTIDAD.Producto;
import Proyecto_Punto_y_Coma.ENTIDAD.Proveedor;
import java.io.*;
import java.sql.*;
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

        String insert = "INSERT INTO proveedores (Id_proveedor, telefono, nombre, email) VALUES (?, ?, ?, ?)";
        String query = "SELECT cod_proveedor FROM proveedores WHERE Id_proveedor = ?";

        try (PreparedStatement pstmtInsert = con.prepareStatement(insert); PreparedStatement pstmtQuery = con.prepareStatement(query)) {

            pstmtInsert.setString(1, cif);
            pstmtInsert.setString(2, telf);
            pstmtInsert.setString(3, nombre);
            pstmtInsert.setString(4, email);
            pstmtInsert.executeUpdate();

            pstmtQuery.setString(1, cif);
            try (ResultSet rs = pstmtQuery.executeQuery()) {
                if (rs.next()) {
                    int idProveedor = rs.getInt("cod_proveedor");
                    System.out.println("¡Proveedor registrado! Se ha generado el ID: " + idProveedor);
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
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
        String query = "SELECT 1 FROM proveedores WHERE Cod_proveedor = ?";
        boolean existe = false;
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    existe = true;
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
            existe = false;
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
                System.out.print("Elija una opción: ");
                opcion = ConexionBD.leerEntero(leer);

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
                
                if (opcion > 0 && opcion < 6) {
                    if (opcion == 5) {
                        System.out.println("Seleccione el nuevo estado:");
                        System.out.println("1. Activo");
                        System.out.println("2. Inactivo");
                        System.out.print("Elija una opción: ");
                        int opEst = ConexionBD.leerEntero(leer);
                        nuevo = (opEst == 1) ? "Activo" : "Inactivo";
                    } else {
                        System.out.println("Introduzca el nuevo valor para " + campo + ":");
                        nuevo = leer.nextLine();
                    }
                    try {
                        String query = "SELECT * FROM proveedores WHERE cod_proveedor = ?";
                        PreparedStatement pstmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        pstmt.setInt(1, cod);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            rs.updateString(campo, nuevo);
                            rs.updateRow();
                            System.out.println("Campo '" + campo + "' actualizado correctamente");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error al actualizar la base de datos: " + e.getMessage());
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
            System.out.print("Elija una opción: ");
            opcion = ConexionBD.leerEntero(leer);
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
        PreparedStatement stmt2 = null;
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
                String query2 = "SELECT * from productos where proveedor = ?";
                stmt2 = con.prepareStatement(query2);
                stmt2.setInt(1, cod);
                rs2 = stmt2.executeQuery();

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
        File dir = new File("Informes");
        if (!dir.exists()) dir.mkdirs();
        File f = new File(dir, "Informe_Proveedores.html");

        try (FileWriter fw = new FileWriter(f)) {
            fw.write("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Informe Proveedores</title>");
            fw.write("<style>");
            fw.write("body { font-family: Arial, sans-serif; margin: 20px; }");
            fw.write("h1 { color: #2c3e50; text-align: center; }");
            fw.write("h2 { color: #2980b9; }");
            fw.write("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            fw.write("th { background-color: #2980b9; color: white; padding: 10px; }");
            fw.write("td { padding: 8px; border: 1px solid #ddd; }");
            fw.write("tr:nth-child(even) { background-color: #f9f9f9; }");
            fw.write(".activo { color: green; font-weight: bold; }");
            fw.write(".inactivo { color: red; font-weight: bold; }");
            fw.write(".bajo-stock { color: orange; font-weight: bold; }");
            fw.write("</style></head><body>");
            fw.write("<h1>Informe de Proveedores y Productos</h1>");

            for (Proveedor p : proveedores) {
                fw.write("<h2>Proveedor: " + p.getNombre() + " (ID: " + p.getCodigo() + ")</h2>");
                fw.write("<table>");
                fw.write("<tr><th>CIF</th><th>Nombre</th><th>Email</th><th>Telefono</th><th>Estado</th></tr>");
                String classEst = (p.getEstado() != null && p.getEstado().equalsIgnoreCase("Activo")) ? "activo" : "inactivo";
                fw.write("<tr><td>" + p.getIdentificador() + "</td><td>" + p.getNombre() + "</td><td>" + p.getEmail() + "</td><td>" + p.getTelefono() + "</td><td class='" + classEst + "'>" + p.getEstado() + "</td></tr>");
                fw.write("</table>");

                fw.write("<h3>Catalogo de Productos</h3>");
                ArrayList<Producto> productos = p.getProductos();
                if (productos != null && !productos.isEmpty()) {
                    fw.write("<table>");
                    fw.write("<tr><th>Cod</th><th>Nombre</th><th>Precio</th><th>Stock</th><th>Stock Min</th><th>Estado</th></tr>");
                    for (Producto prod : productos) {
                        String classProd = (prod.getEstado() != null && prod.getEstado().equalsIgnoreCase("Activo")) ? "activo" : "inactivo";
                        String stockClass = (prod.getStock() < prod.getStockMin()) ? "bajo-stock" : "";
                        fw.write("<tr>");
                        fw.write("<td>" + prod.getCOD() + "</td>");
                        fw.write("<td>" + prod.getNombre() + "</td>");
                        fw.write("<td>" + String.format("%.2f", prod.getPrecio()) + " €</td>");
                        fw.write("<td class='" + stockClass + "'>" + prod.getStock() + "</td>");
                        fw.write("<td>" + prod.getStockMin() + "</td>");
                        fw.write("<td class='" + classProd + "'>" + prod.getEstado() + "</td>");
                        fw.write("</tr>");
                    }
                    fw.write("</table>");
                } else {
                    fw.write("<p><i>No hay productos registrados para este proveedor.</i></p>");
                }
                fw.write("<hr>");
            }

            fw.write("<p style='color:#7f8c8d; font-size:12px;'>Generado el " + java.time.LocalDate.now() + "</p>");
            fw.write("</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void printSQLException(SQLException e) {
        throw new UnsupportedOperationException("Error de Base de Datos: " + e.getMessage()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

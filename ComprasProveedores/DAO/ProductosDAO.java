/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComprasProveedores.DAO;

import java.io.*;
import java.sql.*;
import java.util.Scanner;

/**
 * Operaciones de persistencia y gestión del catálogo de productos en la base de datos.
 * @author María Herrero Rodríguez
 */
public class ProductosDAO {
    
    /**
     * Registra un nuevo producto en el sistema tras validar la existencia del proveedor.
     * @param leer Scanner para capturar los atributos (nombre, stock, precio, etc.).
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error en la inserción o el proveedor no existe.
     */
    public static void crearProducto(Scanner leer, Connection con) throws SQLException {

        ProveedorDAO provDAO = new ProveedorDAO();
        System.out.println("Introduzca el código del proveedor que vende el nuevo producto");
        int proveedor = leer.nextInt();
        leer.nextLine();
        if (provDAO.existeProveedor(con, proveedor) == false) {
            System.out.println("No existe el proveedor. Añadelo primero");
        } else {
            System.out.println("Introduzca el nombre del nuevo producto");
            String nombre = leer.nextLine();
            System.out.println("Introduzca la descripcion del nuevo producto");
            String desc = leer.nextLine();
            System.out.println("Introduzca el stock del producto");
            int stock = leer.nextInt();
            System.out.println("Introduzca el stock minimo del producto");
            int stockm = leer.nextInt();
            System.out.println("Introduzca el precio del producto");
            double precio = leer.nextDouble();

            ResultSet rs = null;
            Statement stmt = null;
            int idProveedor = 0;
            try {
                stmt = con.createStatement();
                String insert = "INSERT INTO productos (nombre, stock, stock_minimo, descripcion, proveedor, precio_unidad) VALUES ('" + nombre + "', " + stock + ", " + stockm + ", '" + desc + "', " + proveedor + ", " + precio + " )";
                stmt.executeUpdate(insert);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                stmt.close();
            }
        }
        
    }

    /**
     * Recupera y muestra por consola todos los productos registrados, 
     * incluyendo su stock actual y estado.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public static void mostrarProductos(Connection con) throws SQLException {
        Statement stmt = null;
        String query = "SELECT * from productos";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("ID_producto");
                System.out.println("ID del producto: " + id);
                String nombre = rs.getString("Nombre");
                System.out.println("Nombre: " + nombre);
                int stock = rs.getInt("Stock");
                System.out.println("Stock: " + stock);
                int stockm = rs.getInt("Stock_minimo");
                System.out.println("Stock minimo: " + stockm);
                String desc = rs.getString("Descripcion");
                System.out.println("Descripcion: " + desc);
                int prov = rs.getInt("Proveedor");
                System.out.println("Codigo proveedor: " + prov);
                double precio = rs.getDouble("Precio_unidad");
                System.out.println("Precio: " + precio);
                String estado = rs.getString("Estado");
                System.out.println("Estado del producto: " + estado);
                System.out.println("**********************************");
            }
        } catch (SQLException e) {
            printSQLException(e);
        } finally {
            stmt.close();

        }
    }

    /**
     * Verifica la existencia de un producto en la base de datos mediante su ID único.
     * @param con Conexión activa a la base de datos.
     * @param codigo Identificador numérico del producto.
     * @return true si el producto está registrado, false en caso contrario.
     */ 
    public static boolean existeProducto(Connection con, int codigo) {
        Statement stmt = null;
        String query = "SELECT ID_producto FROM productos";
        boolean existe = false;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int cod = rs.getInt("ID_producto");
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
     * Permite la edición interactiva de los campos de un producto (nombre, stock, precio, etc.)
     * utilizando ResultSets actualizables.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para capturar el ID del producto y los nuevos valores.
     * @throws SQLException Si el producto no existe o falla la actualización.
     */
    public static void modificarProducto(Connection con, Scanner leer) throws SQLException {
        int opcion = 0;
        int cod = 0;
        Statement stmt = null;
        String nuevo = "";
        String campo = "";
        System.out.println("Inserte el id del producto a modificar");
        cod = leer.nextInt();
        if (existeProducto(con, cod) == true) {
            // MENÚ PATA ELEGIR QUÉ MODIFICAR
            do {
                System.out.println("Elija qué modificar");
                System.out.println("1. Nombre");
                System.out.println("2. Stock");
                System.out.println("3. Stock_minimo");
                System.out.println("4. Descripcion");
                System.out.println("5. Precio");
                System.out.println("6. Proveedor");
                System.out.println("7. Modificar estado");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                leer.nextLine();

                switch (opcion) {
                case 1:
                    campo = "Nombre";
                    break;
                case 2:
                    campo = "Stock";
                    break;
                case 3:
                    campo = "Stock_minimo";
                    break;
                case 4:
                    campo = "Descripcion";
                    break;
                case 5:
                    campo = "Precio_unidad";
                    break;
                case 6:
                    campo = "Proveedor";
                    break;
                case 7:
                    campo = "Estado";
                    
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    continue;
                }

                System.out.println("Introduzca el nuevo valor para " + campo + ":");
                nuevo = leer.nextLine();
                
                try {
                    stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    String query = "SELECT * FROM productos WHERE ID_producto = " + cod;
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        rs.updateString(campo, nuevo);
                        rs.updateRow();
                        System.out.println("¡Producto actualizado correctamente!");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al actualizar: " + e.getMessage());
                } finally {
                    if (stmt != null) {
                        stmt.close();
                    }
                }

            } while (opcion != 0);
        } else {
            System.out.println("El producto con ID " + cod + " no existe.");
        }

    }

    /**
     * Proporciona un menú de navegación para las operaciones CRUD de productos.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de opciones del menú.
     * @throws SQLException Si ocurre un error en las operaciones invocadas.
     */
    public void menu(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("      SISTEMA DE GESTIÓN DE PRODUCTOS    ");
            System.out.println("1. Añadir producto");
            System.out.println("2. Modificar producto");
            System.out.println("3. Ver productos");
            System.out.println("4. Insertar productos desde un archivo .csv");
            System.out.println("0. Salir");
            opcion = leer.nextInt();
            leer.nextLine();
            switch (opcion) {
                case 1:
                    crearProducto(leer, con);
                    break;

                case 2:
                    modificarProducto(con, leer);
                    break;

                case 3:
                    mostrarProductos(con);
                    break;
                    
                case 4:
                    System.out.println("Inserte la ruta del archivo a cargar");
                    String ruta = leer.nextLine();
                    cargarProductosDesdeCSV(con, ruta);
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
     * Lee un archivo CSV de productos y los inserta en la base de datos.
     * Formato esperado del CSV: nombre, precio, stock, id_proveedor
     *
     * * @param con Conexión activa a la base de datos.
     * @param rutaArchivo Ruta del archivo .csv (ej: "productos.csv").
     */
    public void cargarProductosDesdeCSV(Connection con, String rutaArchivo) {
        String sql = "INSERT INTO Productos (Nombre, Precio_unidad, Stock, Proveedor) VALUES (?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo)); PreparedStatement pstmt = con.prepareStatement(sql)) {

            String linea;
            int filasInsertadas = 0;

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");

                try {
                    pstmt.setString(1, datos[0].trim());          
                    pstmt.setDouble(2, Double.parseDouble(datos[1].trim()));
                    pstmt.setInt(3, Integer.parseInt(datos[2].trim()));
                    pstmt.setInt(4, Integer.parseInt(datos[3].trim()));

                    pstmt.executeUpdate();
                    filasInsertadas++;

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Error en línea: " + linea + ". Saltando registro...");
                }
            }

            System.out.println("Carga finalizada. Se han insertado " + filasInsertadas + " productos.");

        } catch (IOException | SQLException e) {
            System.err.println("Error al procesar el archivo o la base de datos: " + e.getMessage());
        }
    }
    
    private static void printSQLException(SQLException e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

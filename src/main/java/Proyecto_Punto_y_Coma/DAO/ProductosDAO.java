/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Proyecto_Punto_y_Coma.DAO;

import Proyecto_Punto_y_Coma.ENTIDAD.Producto;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
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
        int proveedor = ConexionBD.leerEntero(leer);
        leer.nextLine();
        if (provDAO.existeProveedor(con, proveedor) == false) {
            System.out.println("No existe el proveedor. Añadelo primero");
        } else {
            System.out.println("Introduzca el nombre del nuevo producto");
            String nombre = leer.nextLine();
            System.out.println("Introduzca la descripcion del nuevo producto");
            String desc = leer.nextLine();
            System.out.println("Introduzca el stock del producto");
            int stock = ConexionBD.leerEntero(leer);
            System.out.println("Introduzca el stock minimo del producto");
            int stockm = ConexionBD.leerEntero(leer);
            System.out.println("Introduzca el precio del producto");
            double precio = ConexionBD.leerDouble(leer);

            String insert = "INSERT INTO productos (nombre, stock, stock_minimo, descripcion, proveedor, precio_unidad) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(insert)) {
                pstmt.setString(1, nombre);
                pstmt.setInt(2, stock);
                pstmt.setInt(3, stockm);
                pstmt.setString(4, desc);
                pstmt.setInt(5, proveedor);
                pstmt.setDouble(6, precio);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
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
        String query = "SELECT * from productos";
        System.out.println("--- LISTA DE PRODUCTOS ---");
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("ID_producto"),
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getInt("Stock"),
                        rs.getInt("Stock_minimo"),
                        rs.getString("Estado"),
                        rs.getInt("Proveedor"),
                        rs.getDouble("Precio_unidad")
                );
                System.out.println(p);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * Verifica la existencia de un producto en la base de datos mediante su ID único.
     * @param con Conexión activa a la base de datos.
     * @param codigo Identificador numérico del producto.
     * @return true si el producto está registrado, false en caso contrario.
     */ 
    public static boolean existeProducto(Connection con, int codigo) {
        String query = "SELECT 1 FROM productos WHERE ID_producto = ?";
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
     * Permite la edición interactiva de los campos de un producto (nombre, stock, precio, etc.)
     * utilizando ResultSets actualizables.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para capturar el ID del producto y los nuevos valores.
     * @throws SQLException Si el producto no existe o falla la actualización.
     */
    public static void modificarProducto(Connection con, Scanner leer) throws SQLException {
        int opcion = 0;
        int cod = 0;
        String nuevo = "";
        String campo = "";
        System.out.println("Inserte el id del producto a modificar");
        cod = ConexionBD.leerEntero(leer);
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
                System.out.print("Elija una opción: ");
                opcion = ConexionBD.leerEntero(leer);

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
                    try {
                        String qEst = "SELECT Estado FROM productos WHERE ID_producto = ?";
                        PreparedStatement pstEst = con.prepareStatement(qEst, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        pstEst.setInt(1, cod);
                        ResultSet rsEst = pstEst.executeQuery();
                        if (rsEst.next()) {
                            String estadoActual = rsEst.getString("Estado");
                            String nuevoEstado = (estadoActual != null && estadoActual.equalsIgnoreCase("Activo")) ? "Inactivo" : "Activo";
                            rsEst.updateString("Estado", nuevoEstado);
                            rsEst.updateRow();
                            System.out.println("Estado cambiado de " + estadoActual + " a " + nuevoEstado);
                        }
                    } catch (SQLException e) {
                        System.out.println("Error al cambiar estado: " + e.getMessage());
                    }
                    continue;

                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    continue;
                }

                if (opcion > 0 && opcion < 7) {
                System.out.println("Introduzca el nuevo valor para " + campo + ":");
                nuevo = leer.nextLine();
                
                try {
                    String query = "SELECT * FROM productos WHERE ID_producto = ?";
                    PreparedStatement pstmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    pstmt.setInt(1, cod);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        rs.updateString(campo, nuevo);
                        rs.updateRow();
                        System.out.println("¡Producto actualizado correctamente!");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al actualizar: " + e.getMessage());
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
            System.out.println("5. Ajustar stock de un producto");
            System.out.println("0. Salir");
            System.out.print("Elija una opción: ");
            opcion = ConexionBD.leerEntero(leer);
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
                    File dirCSV = new File("Importar");
                    if (!dirCSV.exists()) dirCSV.mkdirs();
                    File[] csvFiles = dirCSV.listFiles((d, name) -> name.toLowerCase().endsWith(".csv"));
                    if (csvFiles == null || csvFiles.length == 0) {
                        System.out.println("No hay archivos CSV en la carpeta 'Importar'.");
                    } else {
                        System.out.println("Archivos disponibles en 'Importar':");
                        for (int i = 0; i < csvFiles.length; i++) {
                            System.out.println((i + 1) + ". " + csvFiles[i].getName());
                        }
                        System.out.print("Seleccione un archivo (0 para cancelar): ");
                        int sel = ConexionBD.leerEntero(leer);
                        leer.nextLine();
                        if (sel > 0 && sel <= csvFiles.length) {
                            cargarProductosDesdeCSV(con, csvFiles[sel - 1].getAbsolutePath());
                        } else {
                            System.out.println("Operación cancelada.");
                        }
                    }
                    break;

                case 5:
                    ajustarStock(con, leer);
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
     * Permite añadir o quitar stock de un producto de forma rápida,
     * sin pasar por el menú completo de modificación.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para capturar ID, operación y cantidad.
     */
    public void ajustarStock(Connection con, Scanner leer) {
        System.out.println("Introduzca el ID del producto:");
        int id = ConexionBD.leerEntero(leer);

        if (existeProducto(con, id)) {
            String sqlStock = "SELECT Nombre, Stock FROM productos WHERE ID_producto = ?";
            try (PreparedStatement pstmt = con.prepareStatement(sqlStock)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String nombre = rs.getString("Nombre");
                        int stockActual = rs.getInt("Stock");
                        System.out.println("Producto: " + nombre + " | Stock actual: " + stockActual);

                        System.out.println("¿Añadir (1) o quitar (2) stock?");
                        System.out.print("Elija una opción: ");
                        int op = ConexionBD.leerEntero(leer);
                        System.out.println("Cantidad:");
                        int cantidad = ConexionBD.leerEntero(leer);

                        if (cantidad > 0) {
                            int nuevoStock;
                            if (op == 1) {
                                nuevoStock = stockActual + cantidad;
                                String update = "UPDATE productos SET Stock = ? WHERE ID_producto = ?";
                                try (PreparedStatement pstmtUpd = con.prepareStatement(update)) {
                                    pstmtUpd.setInt(1, nuevoStock);
                                    pstmtUpd.setInt(2, id);
                                    pstmtUpd.executeUpdate();
                                    System.out.println("Stock actualizado: " + stockActual + " → " + nuevoStock);
                                }
                            } else if (op == 2) {
                                nuevoStock = stockActual - cantidad;
                                if (nuevoStock >= 0) {
                                    String update = "UPDATE productos SET Stock = ? WHERE ID_producto = ?";
                                    try (PreparedStatement pstmtUpd = con.prepareStatement(update)) {
                                        pstmtUpd.setInt(1, nuevoStock);
                                        pstmtUpd.setInt(2, id);
                                        pstmtUpd.executeUpdate();
                                        System.out.println("Stock actualizado: " + stockActual + " → " + nuevoStock);
                                    }
                                } else {
                                    System.out.println("No se puede quitar más stock del disponible.");
                                }
                            } else {
                                System.out.println("Opción no válida.");
                            }
                        } else {
                            System.out.println("La cantidad debe ser positiva.");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("El producto no existe.");
        }
    }

    /**
     * Recupera los productos cuyo stock actual está por debajo del stock mínimo.
     * @param con Conexión activa a la base de datos.
     * @return Lista de productos con stock insuficiente.
     */
    public ArrayList<Producto> productosPorDebajoStockMinimo(Connection con) {
        ArrayList<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE Stock < Stock_minimo AND Estado = 'Activo'";

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("ID_producto"),
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getInt("Stock"),
                        rs.getInt("Stock_minimo"),
                        rs.getString("Estado"),
                        rs.getInt("Proveedor"),
                        rs.getDouble("Precio_unidad")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Lee un archivo CSV de productos y los inserta en la base de datos.
     * Formato esperado del CSV: nombre, precio, stock, id_proveedor
     *
     * @param con Conexión activa a la base de datos.
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
        throw new UnsupportedOperationException("Error de Base de Datos: " + e.getMessage()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

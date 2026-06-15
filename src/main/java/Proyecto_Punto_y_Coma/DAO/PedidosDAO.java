/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Proyecto_Punto_y_Coma.DAO;

import Proyecto_Punto_y_Coma.ENTIDAD.Empleado;
import Proyecto_Punto_y_Coma.ENTIDAD.LineaCompra;
import Proyecto_Punto_y_Coma.ENTIDAD.OrdenCompra;
import Proyecto_Punto_y_Coma.ENTIDAD.Producto;
import Proyecto_Punto_y_Coma.ENTIDAD.Proveedor;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Implementa la lógica de persistencia para órdenes de compra y sus líneas.
 * @author María Herrero Rodríguez
 */
public class PedidosDAO {

     /**
     * Registra un pedido completo (cabecera y líneas) usando transacciones SQL.
     * Al confirmar, genera automáticamente la factura en HTML.
     *
     * @param con Conexión activa.
     * @param leer Scanner para datos de entrada.
     * @param empleado Empleado que genera el pedido.
     * @param proveedores Lista de proveedores activos.
     */
        public void crearPedido(Connection con, Scanner leer, Empleado empleado, ArrayList<Proveedor> proveedores) {
        System.out.println("Indique codigo del proveedor al que quiera realizar el pedido");
        int cod = ConexionBD.leerEntero(leer);
        Proveedor proveedor = null;
        int opcion;

        boolean encontrado = false;
        for (int i = 0; i < proveedores.size() && !encontrado; i++) {
            if (proveedores.get(i).getCodigo() == cod && proveedores.get(i).getEstado().equalsIgnoreCase("Activo")) {
                proveedor = proveedores.get(i);
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("El proveedor no existe o no está activo");
        } else {
            PreparedStatement stmt = null;
            PreparedStatement stmt2 = null;
            PreparedStatement stmtUpd = null;
            ResultSet rs = null;

            try {
                con.setAutoCommit(false);

                String insertOrden = "INSERT INTO Orden_compra (Direccion, Telefono, Empleado, Proveedor, Precio_total) VALUES (?, ?, ?, ?, ?)";
                int idCompra = -1;

                stmt = con.prepareStatement(insertOrden, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, "Calle Principal de la Gestión, 123");
                stmt.setString(2, empleado.getTelefono());
                stmt.setInt(3, empleado.getCodigo());
                stmt.setInt(4, cod);
                stmt.setDouble(5, 0.0);

                stmt.executeUpdate();
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idCompra = rs.getInt(1);
                }

                proveedor.mostrarProductosActivos();
                int lineas = 1;
                double acumuladoTotal = 0.0;

                do {
                    System.out.println("1. Añadir producto a compra");
                    System.out.println("2. Ver compra");
                    System.out.println("3. Proceder con la compra");
                    System.out.println("0. Cancelar compra");
                    System.out.print("Elija una opción: ");
                    opcion = ConexionBD.leerEntero(leer);

                    switch (opcion) {
                        case 1:
                            System.out.println("Introduzca el id del producto a comprar");
                            int idProd = ConexionBD.leerEntero(leer);

                            boolean pertenece = false;
                            ArrayList<Producto> productosProv = proveedor.getProductos();
                            for (int i = 0; i < productosProv.size() && !pertenece; i++) {
                                if (productosProv.get(i).getCOD() == idProd) {
                                    pertenece = true;
                                }
                            }

                            if (!pertenece) {
                                System.out.println("Ese producto no pertenece al proveedor seleccionado.");
                                break;
                            }

                            System.out.println("Introduzca la cantidad a comprar");
                            int cantidad = ConexionBD.leerEntero(leer);

                            double precioUnitario = obtenerPrecio(con, idProd);

                            String insertLinea = "INSERT INTO Lineas_compra (No_compra, No_linea, Cantidad, Producto) VALUES (?, ?, ?, ?)";
                            stmt2 = con.prepareStatement(insertLinea);
                            stmt2.setInt(1, idCompra);
                            stmt2.setInt(2, lineas);
                            stmt2.setInt(3, cantidad);
                            stmt2.setInt(4, idProd);
                            stmt2.executeUpdate();

                            acumuladoTotal += (precioUnitario * cantidad);
                            lineas++;
                            stmt2.close();
                            break;

                        case 2:
                            mostrarCestaActual(con, idCompra);
                            break;

                        case 3:
                            double totalIva = acumuladoTotal + (acumuladoTotal * 0.21);
                            String updatePrecio = "UPDATE Orden_compra SET Precio_total = ? WHERE No_orden = ?";
                            stmtUpd = con.prepareStatement(updatePrecio);
                            stmtUpd.setDouble(1, totalIva);
                            stmtUpd.setInt(2, idCompra);
                            stmtUpd.executeUpdate();
                            stmtUpd.close();

                            con.commit();
                            System.out.println("La compra ha sido realizada con éxito. Total: " + totalIva);
                            break;
                        case 0:
                            System.out.println("Compra cancelada, saliendo...");
                            break;
                    }
                } while (opcion != 0 && opcion != 3 );
            } catch (SQLException e) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
        /**
     * Método auxiliar (Mejora 1) para obtener el precio unitario de un producto 
     * directamente desde la base de datos.
     * @param con Conexión activa a la base de datos.
     * @param idProducto Identificador del producto.
     * @return El precio del producto o 0.0 si no se encuentra.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    public double obtenerPrecio(Connection con, int idProducto) throws SQLException {
        String sql = "SELECT Precio_unidad FROM Productos WHERE ID_producto = ?";
        double precio = 0.0;

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    precio = rs.getDouble("Precio_unidad");
                }
            }
        }
        return precio;
    }

    /**
     * Consulta y lista por pantalla los productos que se han añadido temporalmente 
     * a una orden antes de confirmar la transacción.
     * @param con Conexión activa a la base de datos.
     * @param idCompra Identificador de la orden actual.
     */
    public void mostrarCestaActual(Connection con, int idCompra) {
        String sql = "SELECT lc.No_linea, lc.Cantidad, lc.Producto FROM Lineas_compra lc WHERE lc.No_compra = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idCompra);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LineaCompra lc = new LineaCompra(idCompra, rs.getInt("No_linea"), rs.getInt("Cantidad"), rs.getInt("Producto"));
                    System.out.println(lc);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al visualizar.");
        }
    }
    
     /**

       * Genera pedidos automáticos para productos por debajo del stock mínimo,
       * agrupados por proveedor. Pide confirmación antes de crear cada orden.
       * @param con Conexión activa a la base de datos.
       * @param leer Scanner para confirmar cada pedido.
       * @param empleado Empleado que ejecuta la operación.
       * @param proveedores Lista de proveedores activos.
       */

        public void generarPedidoAutomatico(Connection con, Scanner leer, Empleado empleado, ArrayList<Proveedor> proveedores) {
        ProductosDAO prodDAO = new ProductosDAO();
        ArrayList<Producto> bajos = prodDAO.productosPorDebajoStockMinimo(con);

        if (bajos.isEmpty()) {
            System.out.println("No hay productos por debajo del stock mínimo.");
        } else {

            for (Proveedor prov : proveedores) {
                ArrayList<Producto> productosProv = new ArrayList<>();
                for (Producto p : bajos) {
                    if (p.getProveedor() == prov.getCodigo()) {
                        productosProv.add(p);
                    }
                }

                if (!productosProv.isEmpty()) {

                    System.out.println("\nProveedor: " + prov.getNombre() + " (Código: " + prov.getCodigo() + ")");
                    for (Producto p : productosProv) {
                        System.out.printf("  - %s (Stock: %d / Mín: %d) → Pedir: 10%n",
                                p.getNombre(), p.getStock(), p.getStockMinimo());
                    }

                    System.out.print("¿Generar pedido para este proveedor? (S/N): ");
                    String resp = leer.nextLine();
                    if (resp.equalsIgnoreCase("S")) {

                        PreparedStatement stmtOrd = null;
                        PreparedStatement stmtLin = null;
                        PreparedStatement stmtUpd = null;
                        ResultSet rs = null;
                        double total = 0.0;
                        int numLinea = 1;

                        try {
                            con.setAutoCommit(false);
                            String insertOrd = "INSERT INTO Orden_compra (Direccion, Telefono, Empleado, Proveedor, Precio_total) VALUES (?, ?, ?, ?, ?)";
                            stmtOrd = con.prepareStatement(insertOrd, Statement.RETURN_GENERATED_KEYS);
                            stmtOrd.setString(1, "Pedido automático");
                            stmtOrd.setString(2, empleado.getTelefono());
                            stmtOrd.setInt(3, empleado.getCodigo());
                            stmtOrd.setInt(4, prov.getCodigo());
                            stmtOrd.setDouble(5, 0.0);
                            stmtOrd.executeUpdate();
                            rs = stmtOrd.getGeneratedKeys();
                            int idCompra = 0;
                            if (rs.next()) idCompra = rs.getInt(1);

                            String insertLin = "INSERT INTO Lineas_compra (No_compra, No_linea, Cantidad, Producto) VALUES (?, ?, ?, ?)";
                            for (Producto p : productosProv) {
                                double precio = obtenerPrecio(con, p.getCOD());
                                total += precio * 10;

                                stmtLin = con.prepareStatement(insertLin);
                                stmtLin.setInt(1, idCompra);
                                stmtLin.setInt(2, numLinea++);
                                stmtLin.setInt(3, 10);
                                stmtLin.setInt(4, p.getCOD());
                                stmtLin.executeUpdate();
                                stmtLin.close();

                            }

                            double totalIVA = (total + total * 0.21);

                            String updatePrecio = "UPDATE Orden_compra SET Precio_total = ? WHERE No_orden = ?";
                            stmtUpd = con.prepareStatement(updatePrecio);
                            stmtUpd.setDouble(1, totalIVA);
                            stmtUpd.setInt(2, idCompra);
                            stmtUpd.executeUpdate();
                            stmtUpd.close();

                            con.commit();
                            System.out.printf("Pedido #%d creado con éxito. Total: %.2f%n", idCompra, totalIVA);

                        } catch (SQLException e) {
                            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
                            e.printStackTrace();
                        } finally {
                            try {
                                if (rs != null) rs.close();
                                if (stmtOrd != null) stmtOrd.close();
                                con.setAutoCommit(true);
                            } catch (SQLException e) { e.printStackTrace(); }

                        }
                    }
                }
            }
        }
    } 


    /**
     * Muestra solo los pedidos con estado 'Pendiente' (pendientes de confirmar).
     * @param con Conexión activa a la base de datos.
     */
    public void listarPedidosPendientes(Connection con) {
        String sql = "SELECT No_orden, Direccion, Fecha, Telefono, Precio_total, Estado, Proveedor FROM Orden_Compra WHERE Estado = 'Pendiente'";
        System.out.println("--- LISTA DE PEDIDOS PENDIENTES ---");
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean hay = false;
            while (rs.next()) {
                hay = true;
                OrdenCompra oc = new OrdenCompra(
                        rs.getInt("No_orden"),
                        rs.getString("Direccion"),
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getString("Telefono"),
                        null,
                        rs.getInt("Proveedor"),
                        rs.getString("Estado")
                );
                oc.setPrecio_total(rs.getDouble("Precio_total"));
                System.out.println(oc);
            }
            if (!hay) System.out.println("No hay pedidos pendientes.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     /**
       * Menú principal de gestión de pedidos con todas las operaciones disponibles.
       * @param con Conexión activa a la base de datos.
       * @param leer Scanner para la navegación y entrada de datos.
       * @param empleado Empleado que maneja el menú.
       * @param proveedores Lista de proveedores activos.
       */
        public void menu(Connection con, Scanner leer, Empleado empleado, ArrayList<Proveedor> proveedores) {
        int opcion;
        do {
            System.out.println("      SISTEMA DE GESTIÓN DE PEDIDOS    ");
            System.out.println("1. Crear nuevo pedido (Orden de Compra)");
            System.out.println("2. Listar todos los pedidos");
            System.out.println("3. Ver detalle de un pedido específico");
            System.out.println("4. Modificar estado de un pedido");
            System.out.println("5. Generar pedido automático por stock mínimo");
            System.out.println("6. Ver pedidos pendientes");
            System.out.println("0. Salir");
            System.out.print("Elija una opción: ");
            opcion = ConexionBD.leerEntero(leer);

            switch (opcion) {
                case 1:
                    crearPedido(con, leer, empleado, proveedores);
                    break;
                case 2:
                    listarPedidos(con);
                    break;
                case 3:
                    verDetallePedido(con, leer);
                    break;
                case 4:
                    modificarEstadoPedido(con, leer);
                    break;
                case 5:
                    generarPedidoAutomatico(con, leer, empleado, proveedores);
                    break;
                case 6:
                    listarPedidosPendientes(con);
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
            }
        } while (opcion != 0);
    }
        
     /**
     * Recupera y muestra por pantalla un listado general de todos los pedidos 
     * almacenados en la base de datos, incluyendo ID, fecha, proveedor y total.
     * @param con Conexión activa a la base de datos.
     */
        public void listarPedidos(Connection con) {
        String sql = "SELECT oc.No_orden, oc.Direccion, oc.Fecha, oc.Telefono, oc.Precio_total, oc.Estado, oc.Proveedor, oc.Empleado, e.Nombre AS nom_emp "
                   + "FROM Orden_Compra oc LEFT JOIN Empleados e ON oc.Empleado = e.Cod_empleado";
        System.out.println("--- LISTA DE PEDIDOS ---");
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                OrdenCompra oc = new OrdenCompra(
                        rs.getInt("No_orden"),
                        rs.getString("Direccion"),
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getString("Telefono"),
                        null,
                        rs.getInt("Proveedor"),
                        rs.getString("Estado")
                );
                oc.setPrecio_total(rs.getDouble("Precio_total"));
                System.out.printf("Orden: %d | Fecha: %s | Empleado: %s | Total: %.2f | Estado: %s%n",
                        oc.getNumOrden(), oc.getFecha(),
                        rs.getString("nom_emp") != null ? rs.getString("nom_emp") : "---",
                        oc.getPrecioTotal(), oc.getEstado());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        
        
    /**
     * Genera un archivo HTML con la factura de un pedido usando datos
     * de la base de datos. El archivo se guarda en el directorio "Facturas".
     * @param con Conexión activa a la base de datos.
     * @param idPedido Identificador del pedido a facturar.
     */
    public void generarFactura(Connection con, int idPedido) {
        String sqlCab = "SELECT oc.No_orden, oc.Direccion, oc.Fecha, oc.Telefono, oc.Empleado, "
                      + "oc.Precio_total, oc.Proveedor, oc.Estado, pv.Nombre AS nom_prov, pv.Id_proveedor "
                      + "FROM Orden_compra oc JOIN Proveedores pv ON oc.Proveedor = pv.Cod_proveedor "
                      + "WHERE oc.No_orden = ?";
        String sqlLin = "SELECT lc.No_linea, lc.Cantidad, lc.Producto, p.Nombre, p.Precio_unidad "
                      + "FROM Lineas_compra lc JOIN Productos p ON lc.Producto = p.ID_producto "
                      + "WHERE lc.No_compra = ?";

        try (PreparedStatement pstmtCab = con.prepareStatement(sqlCab);
             PreparedStatement pstmtLin = con.prepareStatement(sqlLin)) {

            pstmtCab.setInt(1, idPedido);
            try (ResultSet rsCab = pstmtCab.executeQuery()) {

                if (rsCab.next()) {
                    int numOrden = rsCab.getInt("No_orden");
                    String direccion = rsCab.getString("Direccion");
                    LocalDate fecha = rsCab.getDate("Fecha").toLocalDate();
                    String telefono = rsCab.getString("Telefono");
                    int codEmpleado = rsCab.getInt("Empleado");
                    double totalBD = rsCab.getDouble("Precio_total");
                    String nomProv = rsCab.getString("nom_prov");
                    String idProv = rsCab.getString("Id_proveedor");

                    double subtotal = totalBD / 1.21;
                    double importeIva = totalBD - subtotal;

                    EmpleadosDAO empDAO = new EmpleadosDAO();
                    Empleado empleado = empDAO.obtenerEmpleadoPorId(con, codEmpleado);

                    File dir = new File("Facturas");
                    if (!dir.exists()) dir.mkdirs();

                    File f = new File(dir, "factura_Pedido_" + numOrden + ".html");
                    try (FileWriter fw = new FileWriter(f)) {
                        fw.write("<html><body><h1>Factura #" + numOrden + "</h1>\n");
                        fw.write("<p>Generado el " + java.time.LocalDate.now() + "</p>\n<hr>\n");

                        fw.write("<pre>\n");
                        fw.write("Fecha:       " + fecha + "\n");
                        fw.write("Proveedor:   " + nomProv + " (" + idProv + ")\n");
                        fw.write("Direccion:   " + direccion + "\n");
                        fw.write("Telefono:    " + telefono + "\n");
                        fw.write("Empleado:    " + (empleado != null ? empleado.getNombre() : "---") + "\n");
                        fw.write("</pre>\n<hr>\n<pre>\n");

                        pstmtLin.setInt(1, idPedido);
                        try (ResultSet rsLin = pstmtLin.executeQuery()) {
                            while (rsLin.next()) {
                                int noLinea = rsLin.getInt("No_linea");
                                String nomProd = rsLin.getString("Nombre");
                                int idProd = rsLin.getInt("Producto");
                                double precioUnidad = rsLin.getDouble("Precio_unidad");
                                int cantidad = rsLin.getInt("Cantidad");
                                double totalLinea = precioUnidad * cantidad;
                                fw.write(noLinea + "  " + nomProd + " (ID: " + idProd + ")"
                                       + "  " + String.format("%.2f", precioUnidad) + " €"
                                       + "  x" + cantidad
                                       + "  = " + String.format("%.2f", totalLinea) + " €\n");
                            }
                        }

                        // Desglose visual en el archivo HTML
                        fw.write("\n-------------------------------------------");
                        fw.write("\nSubtotal (Sin IVA): " + String.format("%.2f", subtotal) + " €");
                        fw.write("\nIVA (21%):          " + String.format("%.2f", importeIva) + " €");
                        fw.write("\nTOTAL FACTURA:      " + String.format("%.2f", totalBD) + " €\n");
                        fw.write("-------------------------------------------\n");
                        fw.write("</pre>\n</body></html>\n");
                    }

                    System.out.println("Factura generada con desglose de IVA: " + f.getAbsolutePath());
                    
                } else {
                    // Si el rsCab.next() devuelve false, cae directamente aquí de forma natural
                    System.out.println("Pedido no encontrado.");
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

       /**
       * Permite actualizar el estado logístico de un pedido (ej: de 'Pendiente' a 'Entregado').
        * Al marcar como Entregado, añade automáticamente las unidades al stock.
      * @param con Conexión activa a la base de datos.
      * @param leer Scanner para capturar el ID del pedido y el nuevo estado.
      * @author María Herrero Rodríguez
      */
        public void modificarEstadoPedido(Connection con, Scanner leer) {
        System.out.println("Indique el ID del pedido a modificar:");
        int id = ConexionBD.leerEntero(leer);
        System.out.println("Seleccione el nuevo estado:");
        System.out.println("1. Confirmado, 2. En preparación, 3. Enviado, 4. Entregado, 5. Cancelado");
        System.out.print("Elija una opción: ");
        int op = ConexionBD.leerEntero(leer);;
        String estado = "";
        leer.nextLine();

        switch (op) {
            case 1:
                estado = "Confirmado";
                break;
            case 2:
                estado = "En preparación";
                break;
            case 3:
                estado = "Enviado";
                break;
            case 4:
                estado = "Entregado";
                break;
            case 5:
                estado = "Cancelado";
                break;
            default:
                System.out.println("Opción no válida.");
                break;
        }

        try {
            con.setAutoCommit(false);

            PreparedStatement pstmt = con.prepareStatement("UPDATE Orden_Compra SET Estado = ? WHERE No_orden = ?");
            pstmt.setString(1, estado);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            pstmt.close();

            if (estado.equals("Entregado")) {
                PreparedStatement pstmtLin = con.prepareStatement(
                    "SELECT lc.Producto, lc.Cantidad FROM Lineas_compra lc WHERE lc.No_compra = ?");
                pstmtLin.setInt(1, id);
                ResultSet rs = pstmtLin.executeQuery();

                PreparedStatement pstmtStock = con.prepareStatement(
                    "UPDATE Productos SET Stock = Stock + ? WHERE ID_producto = ?");

                while (rs.next()) {
                    int producto = rs.getInt("Producto");
                    int cantidad = rs.getInt("Cantidad");
                    pstmtStock.setInt(1, cantidad);
                    pstmtStock.setInt(2, producto);
                    pstmtStock.executeUpdate();
                }
                pstmtStock.close();
                pstmtLin.close();
                rs.close();

            }

            if (estado.equals("Confirmado")){
                generarFactura(con, id);
            }

            con.commit();
            System.out.println("¡Estado actualizado!");

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.out.println("Error al actualizar: " + e.getMessage());
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
       
      /**
     * Muestra la cabecera y todas las líneas de un pedido específico.
     * @param con Conexión activa.
     * @param leer Scanner para el ID.
     */
       public void verDetallePedido(Connection con, Scanner leer) {
        System.out.println("ID del pedido:");
        int id = ConexionBD.leerEntero(leer);

        String sql = "SELECT oc.No_orden, oc.Direccion, oc.Fecha, oc.Telefono, oc.Precio_total, oc.Estado, oc.Proveedor, oc.Empleado, e.Nombre AS nom_emp "
                   + "FROM Orden_Compra oc LEFT JOIN Empleados e ON oc.Empleado = e.Cod_empleado WHERE oc.No_orden = ?";
        String sql2 = "SELECT lc.No_linea, lc.Cantidad, lc.Producto FROM Lineas_Compra lc WHERE lc.No_compra = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                OrdenCompra oc = null;
                String nomEmp = null;
                if (rs.next()) {
                    nomEmp = rs.getString("nom_emp");
                    oc = new OrdenCompra(
                            rs.getInt("No_orden"),
                            rs.getString("Direccion"),
                            rs.getDate("Fecha").toLocalDate(),
                            rs.getString("Telefono"),
                            null,
                            rs.getInt("Proveedor"),
                            rs.getString("Estado")
                    );
                    oc.setPrecio_total(rs.getDouble("Precio_total"));
                }

                if (oc != null) {
                    String empStr = nomEmp != null ? nomEmp : "---";
                    System.out.println(oc.toString().replaceFirst("Empleado: ---", "Empleado: " + empStr));

                    try (PreparedStatement pstmt2 = con.prepareStatement(sql2)) {
                        pstmt2.setInt(1, id);
                        try (ResultSet rs2 = pstmt2.executeQuery()) {
                            while (rs2.next()) {
                                LineaCompra lc = new LineaCompra(id, rs2.getInt("No_linea"), rs2.getInt("Cantidad"), rs2.getInt("Producto"));
                                System.out.println("  " + lc);
                            }
                        }
                    }
                } else {
                    System.out.println("Pedido no encontrado.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
       
}
        
            


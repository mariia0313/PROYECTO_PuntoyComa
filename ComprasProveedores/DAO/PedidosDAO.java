/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComprasProveedores.DAO;

import ComprasProveedores.ENTIDADES.Empleado;
import ComprasProveedores.ENTIDADES.Proveedor;
import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
/**
 * Implementa la lógica de persistencia para órdenes de compra y sus líneas.
 * @author María Herrero Rodríguez
 */
public class PedidosDAO {

     /**
     * Registra un pedido completo (cabecera y líneas) usando transacciones SQL.
     *
     * @param con Conexión activa.
     * @param leer Scanner para datos de entrada.
     * @param empleado Empleado que genera el pedido.
     * @param proveedores Lista de proveedores activos.
     */
        public void crearPedido(Connection con, Scanner leer, Empleado empleado, ArrayList<Proveedor> proveedores) {
        System.out.println("Indique codigo del proveedor al que quiera realizar el pedido");
        int cod = leer.nextInt();
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
                stmt.setString(1, "Calle wawawa");
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
                    System.out.println("0. Proceder con la compra");
                    opcion = leer.nextInt();

                    switch (opcion) {
                        case 1:
                            System.out.println("Introduzca el id del producto a comprar");
                            int idProd = leer.nextInt();
                            System.out.println("Introduzca la cantidad a comprar");
                            int cantidad = leer.nextInt();

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

                        case 0:
                            String updatePrecio = "UPDATE Orden_compra SET Precio_total = ? WHERE No_orden = ?";
                            stmtUpd = con.prepareStatement(updatePrecio);
                            stmtUpd.setDouble(1, acumuladoTotal);
                            stmtUpd.setInt(2, idCompra);
                            stmtUpd.executeUpdate();
                            stmtUpd.close();

                            con.commit();
                            System.out.println("La compra ha sido realizada con éxito. Total: " + acumuladoTotal);
                            break;
                    }
                } while (opcion != 0);

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
        String sql = "SELECT Precio_unidad FROM Productos WHERE ID_producto = " + idProducto;
        Statement stmt = null;
        ResultSet rs = null;
        double precio = 0.0;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                precio = rs.getDouble("Precio_unidad");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
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
        System.out.println("--- ARTÍCULOS EN TU PEDIDO ACTUAL ---");
        String sql = "SELECT * FROM Lineas_compra WHERE No_compra = " + idCompra;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println("Línea " + rs.getInt("No_linea") + " - Producto ID: " + rs.getInt("Producto") + " - Cantidad: " + rs.getInt("Cantidad"));
            }
        } catch (SQLException e) {
            System.out.println("Error al visualizar.");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
            }
        }
    }
    
     /**
     * Menú principal de gestión de pedidos.
     */
        public void menu(Connection con, Scanner leer, Empleado empleado, ArrayList<Proveedor> proveedores) {
        int opcion;
        do {
            System.out.println("      SISTEMA DE GESTIÓN DE PEDIDOS    ");
            System.out.println("1. Crear nuevo pedido (Orden de Compra)");
            System.out.println("2. Listar todos los pedidos");
            System.out.println("3. Ver detalle de un pedido específico");
            System.out.println("4. Modificar estado de un pedido");
            System.out.println("0. Salir");
            opcion = leer.nextInt();
            leer.nextLine();

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
        String sql = "SELECT * FROM Orden_Compra";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            System.out.println("\n--- LISTADO DE PEDIDOS ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("No_orden") + " | Total: " + rs.getDouble("Precio_total") + " | Estado: " + rs.getString("Estado"));
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
            } catch (SQLException ex) {
            }
        }
    }
        
        
     /**
     * Permite actualizar el estado logístico de un pedido (ej: de 'Pendiente' a 'Recibido').
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para capturar el ID del pedido y el nuevo estado.
     * @author María Herrero Rodríguez
     */
       public void modificarEstadoPedido(Connection con, Scanner leer) {
        System.out.println("Indique el ID del pedido a modificar:");
        int id = leer.nextInt();
        System.out.println("Seleccione el nuevo estado:");
        System.out.println("1. Confirmado, 2. En preparación, 3. Enviado, 4. Entregado, 5. Cancelado");
        int op = leer.nextInt();
        String estado = "";

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

        if (!estado.equals("")) {
            String sql = "UPDATE Orden_Compra SET Estado = ? WHERE No_orden = ?";
            PreparedStatement pstmt = null;
            try {
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, estado);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
                System.out.println("¡Estado actualizado!");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }
       
      /**
     * Muestra la cabecera y todas las líneas de un pedido específico.
     * @param con Conexión activa.
     * @param leer Scanner para el ID.
     */
       public void verDetallePedido(Connection con, Scanner leer) {
        System.out.println("ID del pedido:");
        int id = leer.nextInt();

        String sql = "SELECT * FROM Orden_Compra WHERE No_orden = " + id;
        String sql2 = "SELECT * FROM Lineas_Compra WHERE No_compra = " + id;

        Statement stmt = null;
        Statement stmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                System.out.println("\n=== PEDIDO #" + id + " ===");
                System.out.println("Estado: " + rs.getString("Estado"));
                System.out.println("Total: " + rs.getDouble("Precio_total"));

                stmt2 = con.createStatement();
                rs2 = stmt2.executeQuery(sql2);
                while (rs2.next()) {
                    System.out.println("- Línea " + rs2.getInt("No_linea") + ": Producto ID " + rs2.getInt("Producto") + " (Cant: " + rs2.getInt("Cantidad") + ")");
                }
            } else {
                System.out.println("Pedido no encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (stmt2 != null) {
                    stmt2.close();
                }
            } catch (SQLException ex) {
            }
        }
    }
}
        
            


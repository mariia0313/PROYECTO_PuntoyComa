package ComprasProveedores;
import ComprasProveedores.DAO.*;
import ComprasProveedores.ENTIDAD.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
/* María Herrero Rodríguez
MAIN DE PRUEBA PARA COMPRAS A PROVEEDORES
*/

public class Main {
    public static void main (String[] args) throws SQLException, Exception {
        
        // CREAMOS VARIABLES A UTILIZAR
        int opcion;
        ConexionBD _con = new ConexionBD();
        Connection con = _con.abrirConexion();
        Scanner leer = new Scanner(System.in);
        UsuarioDAO usu_dao = new UsuarioDAO();
        EmpleadosDAO emp_dao = new EmpleadosDAO();
        ProveedorDAO prov_dao = new ProveedorDAO();
        ProductosDAO prod_dao = new ProductosDAO();
        PedidosDAO ped_dao = new PedidosDAO();
        int opcion2 = 0;
        
        do {
            System.out.println("BIENVENIDO A LA GESTIÓN DEL HOTEL PUNTO Y COMA");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Crear usuario");
            System.out.println("0. Salir del programa");
            opcion = leer.nextInt();
            leer.nextLine();
            
            switch (opcion) {
                case 1:
                    Empleado empleado = usu_dao.inicioSesion(con, leer);
                    if (empleado != null) {
                        String cargo = empleado.getCargo();
                        switch (cargo) {
                            case "Admin":
                                do {
                                    System.out.println("      ADMINISTRACIÓN   ");
                                    System.out.println("1. Gestión de empleados");
                                    System.out.println("2. Gestión de usuarios");
                                    System.out.println("0. Salir");
                                    opcion2 = leer.nextInt();
                                    leer.nextLine();
                                    switch (opcion2){
                                        case 1:
                                            emp_dao.menu(con, leer);
                                            break;
                                        case 2:
                                            usu_dao.menu(con, leer);
                                            break;
                                    }
                                } while (opcion2 != 0);
                                break;

                            case "Encargado Compras":
                                ArrayList<Producto> bajos = prod_dao.productosPorDebajoStockMinimo(con);
                                if (bajos != null && !bajos.isEmpty()) {
                                    System.out.println("\n⚠  AVISO: Hay " + bajos.size() + " producto(s) por debajo del stock mínimo.");
                                    System.out.println("  Use la opción 'Generar pedido automático' en Gestión de Pedidos.\n");
                                }
                                do {
                                    System.out.println("      SISTEMA DE GESTIÓN DE COMPRAS A PROVEEDORES   ");
                                    System.out.println("1. Gestión de proveedores");
                                    System.out.println("2. Gestión de productos");
                                    System.out.println("3. Gestión de pedidos");
                                    System.out.println("0. Salir");
                                    opcion2 = leer.nextInt();
                                    leer.nextLine();
                                    switch (opcion2) {
                                        case 1:
                                            prov_dao.menu(con, leer);
                                            break;
                                        case 2:
                                            prod_dao.menu(con, leer);
                                            break;
                                        case 3:
                                            ped_dao.menu(con, leer, empleado, prov_dao.rellenarProductosProveedores(con));
                                            break;
                                        case 0:
                                            System.out.println("Saliendo...");
                                            break;
                                        default:
                                            System.out.println("Opción no válida");
                                    }
                                } while (opcion2 != 0);
                                break;

                            default:
                                System.out.println("Cargo no reconocido: " + cargo);
                                break;
                        }
                        break;
                    }
                    break;
                case 2:
                    usu_dao.crearUsuario(con, leer);
                    break;

                case 0:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción no válida");
                    break;
            }
        } while (opcion != 0);

        con.close();
        leer.close();
    }
}
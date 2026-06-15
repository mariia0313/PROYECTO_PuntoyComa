package Proyecto_Punto_y_Coma;
import Proyecto_Punto_y_Coma.DAO.*;
import Proyecto_Punto_y_Coma.ENTIDAD.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Punto de entrada del sistema de gestión del hotel Punto y Coma.
 * Proporciona un menú principal con inicio de sesión y creación de usuarios,
 * delegando en los DAOs correspondientes según el cargo del empleado.
 * @author María Herrero Rodríguez
 */
public class Main {
    /**
     * Método principal que inicia la aplicación, conecta a la base de datos
     * y gestiona el menú interactivo según el rol del usuario.
     * @param args Argumentos de línea de comandos (no utilizados).
     * @throws SQLException Si falla la conexión o las consultas SQL.
     * @throws Exception Si ocurre algún error inesperado.
     */
    public static void main (String[] args) throws SQLException, Exception {

        int opcion;
        ConexionBD _con = new ConexionBD();
        Connection con = _con.abrirConexion();
        Scanner leer = new Scanner(System.in);
        UsuarioDAO usu_dao = new UsuarioDAO();
        EmpleadosDAO emp_dao = new EmpleadosDAO();
        ProveedorDAO prov_dao = new ProveedorDAO();
        ProductosDAO prod_dao = new ProductosDAO();
        PedidosDAO ped_dao = new PedidosDAO();
        ClienteDAO cli_dao = new ClienteDAO();
        TipoReservaDAO recurso_dao = new TipoReservaDAO();
        ReservaDAO res_dao = new ReservaDAO();
        int opcion2 = 0;

        do {
            System.out.println("BIENVENIDO A LA GESTIÓN DEL HOTEL PUNTO Y COMA");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Crear usuario");
            System.out.println("0. Salir del programa");
            opcion = ConexionBD.leerEntero(leer);

            switch (opcion) {
                case 1:
                    Empleado empleado = usu_dao.inicioSesion(con, leer);
                    if (empleado != null) {
                        String cargo = empleado.getCargo();
                        switch (cargo) {
                            case "Administrador":
                                do {
                                    System.out.println("      ADMINISTRACIÓN   ");
                                    System.out.println("1. Gestión de empleados");
                                    System.out.println("2. Gestión de usuarios");
                                    System.out.println("3. Gestión de proveedores");
                                    System.out.println("4. Gestión de productos");
                                    System.out.println("5. Gestión de pedidos");
                                    System.out.println("6. Gestión de clientes");
                                    System.out.println("7. Gestión de recursos (Alojamientos/Actividades/Salas)");
                                    System.out.println("8. Gestión de reservas");
                                    System.out.println("0. Salir");
                                    opcion2 = ConexionBD.leerEntero(leer);
                                    switch (opcion2){
                                        case 1:
                                            emp_dao.menu(con, leer);
                                            break;
                                        case 2:
                                            usu_dao.menu(con, leer);
                                            break;
                                        case 3:
                                            prov_dao.menu(con, leer);
                                            break;
                                        case 4:
                                            prod_dao.menu(con, leer);
                                            break;
                                        case 5:
                                            ped_dao.menu(con, leer, empleado, prov_dao.rellenarProductosProveedores(con));
                                            break;
                                        case 6:
                                            cli_dao.menu(con, leer);
                                            break;
                                        case 7:
                                            recurso_dao.menu(con, leer);
                                            break;
                                        case 8:
                                            res_dao.menu(con, leer);
                                            break;
                                        case 0:
                                            System.out.println("Saliendo...");
                                            break;
                                        default:
                                            System.out.println("Opción no válida");
                                            break;
                                    }
                                } while (opcion2 != 0);
                                break;

                            case "Encargado Compras":
                                ArrayList<Producto> bajos = prod_dao.productosPorDebajoStockMinimo(con);
                                if (bajos != null && !bajos.isEmpty()) {
                                    System.out.println("\nAVISO: Hay " + bajos.size() + " producto(s) por debajo del stock mínimo.");
                                    System.out.println("  Use la opción 'Generar pedido automático' en Gestión de Pedidos.\n");
                                }
                                do {
                                    System.out.println("      SISTEMA DE GESTIÓN DE COMPRAS A PROVEEDORES   ");
                                    System.out.println("1. Gestión de proveedores");
                                    System.out.println("2. Gestión de productos");
                                    System.out.println("3. Gestión de pedidos");
                                    System.out.println("0. Salir");
                                    opcion2 = ConexionBD.leerEntero(leer);
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

                            case "Encargado Reservas":
                                do {
                                    System.out.println("      SISTEMA DE GESTIÓN DE RESERVAS   ");
                                    System.out.println("1. Gestión de clientes");
                                    System.out.println("2. Gestión de recursos (Alojamientos/Actividades/Salas)");
                                    System.out.println("3. Gestión de reservas");
                                    System.out.println("0. Salir");
                                    opcion2 = ConexionBD.leerEntero(leer);
                                    switch (opcion2) {
                                        case 1:
                                            cli_dao.menu(con, leer);
                                            break;
                                        case 2:
                                            recurso_dao.menu(con, leer);
                                            break;
                                        case 3:
                                            res_dao.menu(con, leer);
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

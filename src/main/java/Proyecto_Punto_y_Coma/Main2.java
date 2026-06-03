package Proyecto_Punto_y_Coma;

import Proyecto_Punto_y_Coma.DAO.ConexionBD;
import Proyecto_Punto_y_Coma.DAO.ClienteDAO;
import Proyecto_Punto_y_Coma.DAO.TipoReservaDAO;
import Proyecto_Punto_y_Coma.DAO.ReservaDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Aplicación principal del Sistema de Gestión de Reservas del Hotel.
 * Centraliza los accesos a los distintos menús del subsistema de Reservas.
 * @author David Catalán Aragó
 */
public class Main2 {

    public static void main(String[] args) {
        ConexionBD conexionBD = new ConexionBD();
        Connection con = null;
        Scanner leer = new Scanner(System.in);
        
        try {
            // Abrimos la conexión al iniciar la aplicación
            con = conexionBD.abrirConexion();
            
            if (con != null) {
                ClienteDAO clienteDAO = new ClienteDAO();
                TipoReservaDAO recursoDAO = new TipoReservaDAO();
                ReservaDAO reservaDAO = new ReservaDAO();
                
                int opcion = 0;
                do {
                    System.out.println("\n=========================================");
                    System.out.println("    SISTEMA DE GESTIÓN HOTELERA PUNTOYCOMA ");
                    System.out.println("=========================================");
                    System.out.println("1. Gestión de Clientes");
                    System.out.println("2. Gestión de Recursos (Alojamientos/Actividades/Salas)");
                    System.out.println("3. Gestión de Reservas");
                    System.out.println("0. Salir de la aplicación");
                    System.out.print("Elija una opción: ");
                    
                    opcion = leer.nextInt();
                    
                    switch (opcion) {
                        case 1:
                            clienteDAO.menu(con, leer);
                            break;
                        case 2:
                            recursoDAO.menu(con, leer);
                            break;
                        case 3:
                            reservaDAO.menu(con, leer);
                            break;
                        case 0:
                            System.out.println("Cerrando la aplicación... ¡Hasta pronto!");
                            break;
                        default:
                            System.out.println("Opción no válida. Por favor, intente de nuevo.");
                            break;
                    }
                } while (opcion != 0);
            }
            
        } catch (Exception e) {
            System.out.println("Ocurrió un error inesperado en la aplicación:");
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    conexionBD.cerrarConexion(con);
                }
                leer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

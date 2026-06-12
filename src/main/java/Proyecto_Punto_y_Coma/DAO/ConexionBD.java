/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Proyecto_Punto_y_Coma.DAO;
import java.sql.*;
import java.util.Scanner;
/**
 * Gestiona la conexión y desconexión con la base de datos MySQL del proyecto.
 * @author María Herrero Rodríguez
 */
public class ConexionBD {
    
    /**
     * Establece el enlace con la base de datos local 'proyecto_puntoycoma'.
     * @return Connection objeto de conexión.
     * @throws Exception si falla la carga del driver o las credenciales.
     */
    public Connection abrirConexion()throws Exception{
        Connection con = null;
        
    try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_puntoycoma?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "root", "yA20mnVB");
            System.out.println("Conexión exitosa");
            
        } catch (SQLException e) {
            System.out.println("Código de error SQL: " + e.getErrorCode());
            System.out.println("Estado SQL: " + e.getSQLState());
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    
    return con;
    
    }
    
    /**
     * Cierra una conexión JDBC activa.
     * @param con Conexión a cerrar.
     */
    public void cerrarConexion(Connection con) throws Exception {
    
        try {
        if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static int leerEntero(Scanner leer) {
        int numero = 0;
        boolean valido = false;
        while (!valido) {
            try {
                numero = Integer.parseInt(leer.nextLine());
                valido = true;
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida. Introduzca un numero: ");
            }
        }
        return numero;
    }

        public static double leerDouble(Scanner leer) {
        double numero = 0;
        boolean valido = false;
        while (!valido) {
            try {
                numero = Double.parseDouble(leer.nextLine());
                valido = true;
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida. Introduzca un numero: ");
            }
        }
        return numero;
    }
    
}

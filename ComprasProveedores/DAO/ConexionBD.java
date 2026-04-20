/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComprasProveedores.DAO;
import java.sql.*;
/**
 *
 * @author Usuario
 */
public class ConexionBD {
    public Connection abrirConexion()throws Exception{
        Connection con = null;
        
    try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_puntoycoma?useSSL=false&serverTimezone=UTC", "root", "yA20mnVB");
            System.out.println("Conexión exitosa");
            
        } catch (SQLException e) {
            System.out.println("Código de error SQL: " + e.getErrorCode());
            System.out.println("Estado SQL: " + e.getSQLState());
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    
    return con;
    
    }
    
    public void cerrarConexion(Connection con) throws Exception {
    
        try {
        if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}

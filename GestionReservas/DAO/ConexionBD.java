package GestionReservas.DAO;

import java.sql.*;

/**
 * Gestiona la conexión y desconexión con la base de datos MySQL del proyecto.
 * Utiliza el driver Connector/J de MySQL a través de JDBC.
 * @author David Catalán Aragó
 */
public class ConexionBD {

    /**
     * Establece el enlace con la base de datos local 'proyecto_puntoycoma'.
     * @return Connection objeto de conexión activa.
     * @throws Exception si falla la carga del driver o las credenciales.
     */
    public Connection abrirConexion() throws Exception {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_puntoycoma","root", "yA20mnVB");
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
     * @throws Exception si ocurre un error al cerrar.
     */
    public void cerrarConexion(Connection con) throws Exception {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Imprime por consola el detalle de una SQLException.
     * @param ex Excepción SQL a mostrar.
     */
    public static void printSQLException(SQLException ex) {
        ex.printStackTrace(System.err);
        System.err.println("SQLState: " + ex.getSQLState());
        System.err.println("Error Code: " + ex.getErrorCode());
        System.err.println("Message: " + ex.getMessage());
        Throwable t = ex.getCause();
        while (t != null) {
            System.out.println("Cause: " + t);
            t = t.getCause();
        }
    }
}

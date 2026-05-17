/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComprasProveedores.DAO;

import ComprasProveedores.ENTIDADES.Empleado;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Operaciones de persistencia y gestión de datos para la entidad Empleado en la base de datos.
 * @author María Herrero Rodríguez
 */
public class EmpleadosDAO {
    
    /**
     * Registra un nuevo empleado en la base de datos solicitando la información por consola.
     * Genera automáticamente un código de empleado tras la inserción.
     * @param leer Scanner para la entrada de datos.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante la ejecución del INSERT.
     */
    public static void crearEmpleado(Scanner leer, Connection con) throws SQLException {

        System.out.println("Introduzca el DNI del nuevo empleado");
        String dni = leer.nextLine();
        System.out.println("Introduzca el nombre del nuevo empleado");
        String nombre = leer.nextLine();
        System.out.println("Introduzca el email del nuevo empleado");
        String email = leer.nextLine();
        System.out.println("Introduzca el teléfono del nuevo empleado");
        String telf = leer.nextLine();
        System.out.println("Introduzca el cargo del nuevo empleado");
        String cargo = leer.nextLine();
        System.out.println("Introduzca el NUSS del nuevo empleado");
        String nuss = leer.nextLine();
        System.out.println("Introduzca el contrato del nuevo empleado");
        String contrato = leer.nextLine();
        System.out.println("Introduzca la fecha de nacimiento (YYYY-MM-DD)");
        String fNacStr = leer.nextLine();
        
        java.sql.Date fechaNac = java.sql.Date.valueOf(fNacStr);

        ResultSet rs = null;
        Statement stmt = null;
        int idEmpleado = 0;
        try {
            stmt = con.createStatement();
            String insert = "INSERT INTO empleados (Id_empleado, telefono, nombre, email, cargo, nss, tipo_contrato, fecha_nacimiento, estado)" + "VALUES ('" + dni + "', '" + telf + "', '" + nombre + "', '" + email + "', '" + cargo + "', '" + nuss + "', '" + contrato + "', '" + fNacStr + "', 'Activo')";
            stmt.executeUpdate(insert);
            String query = "SELECT cod_empleado FROM empleados WHERE Id_empleado = '" + dni + "'";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                idEmpleado = rs.getInt("cod_empleado");
                System.out.println("Se ha generado el ID: " + idEmpleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }
    }

    /**
     * Recupera y muestra por consola el listado completo de empleados almacenados 
     * con todos sus detalles laborales y personales.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public static void mostrarEmpleados(Connection con) throws SQLException {
        Statement stmt = null;
        String query = "SELECT * from empleados";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int codigo = rs.getInt("Cod_empleado");
                System.out.println("Código de empleado: " + codigo);
                String dni = rs.getString("Id_empleado");
                System.out.println("DNI: " + dni);
                String telef = rs.getString("Telefono");
                System.out.println("Telefono: " + telef);
                String nombre = rs.getString("Nombre");
                System.out.println("Nombre de empleado: " + nombre);
                String email = rs.getString("Email");
                System.out.println("Email de proveedor: " + email);
                String nuss = rs.getString("nss");
                System.out.println("NUSS: " + nuss);
                String contrato = rs.getString("tipo_contrato");
                System.out.println("Tipo de contrato: " + contrato);
                java.sql.Date fNac = rs.getDate("fecha_nacimiento");
                System.out.println("Fecha de nacimiento: " + fNac);
                java.sql.Date fAnt = rs.getDate("fecha_antiguedad");
                System.out.println("Fecha de antiguedad: " + fAnt);
                java.sql.Date fDesp = rs.getDate("fecha_despido");
                String despidoInfo = (fDesp == null) ? "En activo" : fDesp.toString();
                System.out.println("Fecha de despido: " + despidoInfo);
                String estado = rs.getString("Estado");
                System.out.println("Estado del empleado: " + estado);
                System.out.println("**********************************");
            }
        } catch (SQLException e) {
            printSQLException(e);
        } finally {
            stmt.close();

        }
    }

    /**
     * Verifica si un empleado existe en la base de datos a partir de su código identificador.
     * @param con Conexión activa a la base de datos.
     * @param codigo Código numérico del empleado a buscar.
     * @return true si el empleado existe, false en caso contrario.
     */
    public static boolean existeEmpleado(Connection con, int codigo) {
        Statement stmt = null;
        String query = "SELECT Cod_empleado FROM empleados";
        boolean existe = false;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int cod = rs.getInt("Cod_empleado");
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
     * Proporciona una interfaz de menú para modificar cualquier campo de un empleado existente.
     * Si se cambia el estado a 'Inactivo', se registra automáticamente la fecha de despido.
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para la navegación y entrada de nuevos datos.
     * @throws SQLException Si ocurre un error al actualizar los registros.
     */
    public static void modificarEmpleado(Connection con, Scanner leer) throws SQLException {
        int opcion = 0;
        int cod = 0;
        Statement stmt = null;
        String nuevo = "";
        String campo = "";
        System.out.println("Inserte el código del empleado a modificar");
        cod = leer.nextInt();
        leer.nextLine();
        if (existeEmpleado(con, cod) == true) {
            // MENÚ PATA ELEGIR QUÉ MODIFICAR
            do {
                System.out.println("Elija qué modificar");
                System.out.println("1. Dni");
                System.out.println("2. Nombre");
                System.out.println("3. Telefono");
                System.out.println("4. Email");
                System.out.println("5. Cargo");
                System.out.println("6. NUSS");
                System.out.println("7. Contrato");
                System.out.println("8. Fecha Nacimiento (YYYY-MM-DD)");
                System.out.println("9. Fecha Antigüedad (YYYY-MM-DD)");
                System.out.println("10. Estado");
                System.out.println("11. Fecha despido (YYYY-MM-DD)");
                System.out.println("0. Salir");
                opcion = leer.nextInt();
                leer.nextLine();

                switch (opcion) {
                    case 1:
                        campo = "Id_empleado";
                        break;
                    case 2:
                        campo = "Nombre";
                        break;
                    case 3:
                        campo = "Telefono";
                        break;
                    case 4:
                        campo = "Email";
                        break;
                    case 5:
                        campo = "Cargo";
                        break;
                    case 6:
                        campo = "nss";
                        break;
                    case 7:
                        campo = "tipo_contrato";
                        break;
                    case 8:
                        campo = "fecha_nacimiento";
                        break;
                    case 9:
                        campo = "fecha_antiguedad";
                        break;
                    case 10:
                        campo = "Estado";
                        break;
                        
                    case 11:
                        campo = "fecha_despido";
                        break;
                    default:
                        System.out.println("Opción no válida");
                        break;
                }
                
                System.out.println("Introduzca el nuevo " + campo);
                nuevo = leer.nextLine();
                

                try {
                    stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    String query = "SELECT * from empleados where cod_empleado = " + cod;
                    ResultSet rs = stmt.executeQuery(query);
                    java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
                    while (rs.next()) {
                        rs.updateString(campo, nuevo);
                        rs.updateRow();
                        if (campo.equalsIgnoreCase("Estado") && nuevo.equalsIgnoreCase("Inactivo")) {
                        rs.updateDate("fecha_despido", fechaActual );
                        rs.updateRow();
                    }
                        System.out.println("Campo actualizado correctamente");
                    }
                    
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    stmt.close();
                }

            } while (opcion != 0);
        } else {

            System.out.println("El empleado no ha sido encontrado en la base de datos");
        }

    }

    /**
     * Muestra el menú principal de gestión de empleados (Añadir, Modificar, Ver).
     * @param con Conexión activa a la base de datos.
     * @param leer Scanner para la entrada de opciones.
     * @throws SQLException Si ocurre un error en las operaciones llamadas.
     */
    public void menu(Connection con, Scanner leer) throws SQLException {
        int opcion;
        do {
            System.out.println("1. Añadir empleado");
            System.out.println("2. Modificar empleado");
            System.out.println("3. Ver empleados");
            System.out.println("0. Salir");
            opcion = leer.nextInt();
            leer.nextLine();
                switch (opcion) {
                    case 1:
                        crearEmpleado(leer, con);
                        break;
                    
                    case 2:
                        modificarEmpleado(con, leer);
                        break;
                        
                    case 3:
                        mostrarEmpleados(con);
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
     * Carga los datos de un empleado desde la base de datos y crea un objeto de tipo Empleado.
     * @param con Conexión activa a la base de datos.
     * @param codigo ID del empleado a recuperar.
     * @return Un objeto {@link Empleado} con sus datos cargados, o null si no se encuentra.
     * @throws SQLException Si hay un error en la ejecución de la consulta.
     */
    public static Empleado obtenerEmpleadoPorId(Connection con, int codigo) throws SQLException {
        Empleado empleado = null;
        Statement stmt = null;
        ResultSet rs = null;

        String query = "SELECT * FROM empleados WHERE Cod_empleado = " + codigo;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                int cod = rs.getInt("Cod_empleado");
                String dni = rs.getString("Id_empleado");
                String nombre = rs.getString("Nombre");
                String email = rs.getString("Email");
                String telf = rs.getString("Telefono");
                String cargo = rs.getString("Cargo");
                String contrato = rs.getString("tipo_contrato");
                java.sql.Date fNac = rs.getDate("fecha_nacimiento");
                java.sql.Date fAnt = rs.getDate("fecha_antiguedad");
                java.sql.Date fDesp = rs.getDate("fecha_despido");
                String estado = rs.getString("estado");
                empleado = new Empleado(cod, dni, nombre, email, telf, cargo, contrato, fNac, fAnt, fDesp, estado);
                empleado.setCodigo(codigo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }

        return empleado; // Si no lo encuentra, devuelve null
    }

    private static void printSQLException(SQLException e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

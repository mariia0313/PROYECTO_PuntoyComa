/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Proyecto_Punto_y_Coma.DAO;

import Proyecto_Punto_y_Coma.ENTIDAD.Empleado;
import Proyecto_Punto_y_Coma.ENTIDAD.Usuario;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Operaciones de persistencia y gestión de datos para la entidad Empleado en la base de datos.
 * @author María Herrero Rodríguez
 */
public class EmpleadosDAO {
    
    /**
     * Muestra un menú para seleccionar el cargo del empleado,
     * asegurando que coincida con los roles del sistema.
     */
    private String seleccionarCargo(Scanner leer) {
        String cargo = "";
        boolean valido = false;
        do {
            System.out.println("Seleccione el cargo del empleado:");
            System.out.println("1. Administrador");
            System.out.println("2. Encargado Compras");
            System.out.println("3. Encargado Reservas");
            System.out.println("4. Otro");
            System.out.print("Elija una opción: ");
            int opc = ConexionBD.leerEntero(leer);
            switch (opc) {
                case 1:
                    cargo = "Administrador";
                    valido = true;
                    break;
                case 2:
                    cargo = "Encargado Compras";
                    valido = true;
                    break;
                case 3:
                    cargo = "Encargado Reservas";
                    valido = true;
                    break;
                case 4:
                    System.out.print("Introduzca el cargo: ");
                    cargo = leer.nextLine();
                    valido = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } while (!valido);
        return cargo;
    }

    private String seleccionarContrato(Scanner leer) {
        String contrato = "";
        boolean valido = false;
        do {
            System.out.println("Seleccione el tipo de contrato:");
            System.out.println("1. Indefinido");
            System.out.println("2. Temporal");
            System.out.println("3. Practicas");
            System.out.println("4. Otro");
            System.out.print("Elija una opción: ");
            int opc = ConexionBD.leerEntero(leer);
            switch (opc) {
                case 1:
                    contrato = "Indefinido";
                    valido = true;
                    break;
                case 2:
                    contrato = "Temporal";
                    valido = true;
                    break;
                case 3:
                    contrato = "Practicas";
                    valido = true;
                    break;
                case 4:
                    System.out.print("Introduzca el tipo de contrato: ");
                    contrato = leer.nextLine();
                    valido = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } while (!valido);
        return contrato;
    }

    /**
     * Registra un nuevo empleado en la base de datos solicitando la información por consola.
     * Genera automáticamente un código de empleado tras la inserción.
     * @param leer Scanner para la entrada de datos.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante la ejecución del INSERT.
     */
    public void crearEmpleado(Scanner leer, Connection con) throws SQLException {

        System.out.println("Introduzca el DNI del nuevo empleado");
        String dni = leer.nextLine();
        System.out.println("Introduzca el nombre del nuevo empleado");
        String nombre = leer.nextLine();
        System.out.println("Introduzca el email del nuevo empleado");
        String email = leer.nextLine();
        System.out.println("Introduzca el teléfono del nuevo empleado");
        String telf = leer.nextLine();
        String cargo = seleccionarCargo(leer);
        System.out.println("Introduzca el NUSS del nuevo empleado");
        String nuss = leer.nextLine();
        String contrato = seleccionarContrato(leer);
        java.sql.Date fechaNac = null;
        do {
            System.out.println("Introduzca la fecha de nacimiento (YYYY-MM-DD)");
            String fNacStr = leer.nextLine();
            try {
                fechaNac = java.sql.Date.valueOf(fNacStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Formato incorrecto. Use YYYY-MM-DD (ej: 1990-05-15)");
            }
        } while (fechaNac == null);

        String insert = "INSERT INTO empleados (Id_empleado, telefono, nombre, email, cargo, nss, tipo_contrato, fecha_nacimiento, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Activo')";
        String query = "SELECT cod_empleado FROM empleados WHERE Id_empleado = ?";
        try (PreparedStatement pstmtInsert = con.prepareStatement(insert); PreparedStatement pstmtQuery = con.prepareStatement(query)) {
            pstmtInsert.setString(1, dni);
            pstmtInsert.setString(2, telf);
            pstmtInsert.setString(3, nombre);
            pstmtInsert.setString(4, email);
            pstmtInsert.setString(5, cargo);
            pstmtInsert.setString(6, nuss);
            pstmtInsert.setString(7, contrato);
            pstmtInsert.setDate(8, fechaNac);
            pstmtInsert.executeUpdate();

            pstmtQuery.setString(1, dni);
            try (ResultSet rs = pstmtQuery.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Se ha generado el ID: " + rs.getInt("cod_empleado"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recupera y muestra por consola el listado completo de empleados almacenados 
     * con todos sus detalles laborales y personales.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores en la consulta SELECT.
     */
    public void mostrarEmpleados(Connection con) throws SQLException {
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
    public boolean existeEmpleado(Connection con, int codigo) {
        Statement stmt = null;
        boolean existe = false;
        String query = "SELECT 1 FROM empleados WHERE cod_empleado = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    existe = true;
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    public void modificarEmpleado(Connection con, Scanner leer) throws SQLException {
        int opcion = 0;
        int cod = 0;
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
                System.out.print("Elija una opción: ");
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
                    case 0:
                        System.out.println("Saliendo...");
                    default:
                        System.out.println("Opción no válida");
                        break;
                }
                
                if (opcion == 5) {
                    nuevo = seleccionarCargo(leer);
                } else if (opcion == 7) {
                    nuevo = seleccionarContrato(leer);
                } else if (opcion == 10) {
                    System.out.println("Seleccione el nuevo estado:");
                    System.out.println("1. Activo");
                    System.out.println("2. Inactivo");
                    System.out.println("3. De baja");
                    System.out.println("4. Suspendido");
                    System.out.print("Elija una opción: ");
                    int opEst = leer.nextInt();
                    leer.nextLine();
                    switch (opEst) {
                        case 1: nuevo = "Activo"; break;
                        case 2: nuevo = "Inactivo"; break;
                        case 3: nuevo = "De baja"; break;
                        case 4: nuevo = "Suspendido"; break;
                        default: nuevo = "Activo"; break;
                    }
                } else if (opcion != 0) {
                    System.out.println("Introduzca el nuevo " + campo);
                    nuevo = leer.nextLine();
                }
                
                if (opcion !=0){
                    try {
                        String query = "SELECT * from empleados where cod_empleado = ?";
                        PreparedStatement pstmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        pstmt.setInt(1, cod);
                        ResultSet rs = pstmt.executeQuery();
                        java.sql.Date fechaActual = java.sql.Date.valueOf(java.time.LocalDate.now());
                        while (rs.next()) {
                            rs.updateString(campo, nuevo);
                            rs.updateRow();
                            if (campo.equalsIgnoreCase("Estado") && nuevo.equalsIgnoreCase("Inactivo")) {
                                rs.updateDate("fecha_despido", fechaActual);
                                rs.updateRow();
                            }
                            System.out.println("Campo actualizado correctamente");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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
    public void menu(Connection con, Scanner leer) throws SQLException, IOException {
        int opcion;
        do {
            System.out.println("1. Añadir empleado");
            System.out.println("2. Modificar empleado");
            System.out.println("3. Ver empleados");
            System.out.println("4. Crear informe completo de los empleados");
            System.out.println("0. Salir");
            System.out.print("Elija una opción: ");
            opcion = ConexionBD.leerEntero(leer);
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
                        
                    case 4:
                        crearInformeEmpleados(con);
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

        String query = "SELECT * FROM empleados WHERE Cod_empleado = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empleado; // Si no lo encuentra, devuelve null
    }
    
    /**
     * Genera un informe en archivo html que incluye la ficha del empleado
     * y, en caso de tenerlo, el detalle de su cuenta de usuario mediante su
     * toString.
     * * @param con Conexión activa a la base de datos.
     * @throws SQLException Si hay errores de acceso a datos.
     * @throws IOException Si hay errores al escribir el archivo.
     */
    public void crearInformeEmpleados(Connection con) throws SQLException, IOException {
        ArrayList<Empleado> listaTemporal = new ArrayList<>();
        String query = "SELECT * FROM empleados";

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Empleado emp = new Empleado(
                        rs.getInt("Cod_empleado"),
                        rs.getString("Id_empleado"),
                        rs.getString("Nombre"),
                        rs.getString("Email"),
                        rs.getString("Telefono"),
                        rs.getString("Cargo"),
                        rs.getString("tipo_contrato"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getDate("fecha_antiguedad"),
                        rs.getDate("fecha_despido"),
                        rs.getString("Estado")
                );
                listaTemporal.add(emp);
            }
        }

        File dir = new File("Informes");
        if (!dir.exists()) dir.mkdirs();
        File f = new File(dir, "Informe_Completo_Empleados.html");

        try (FileWriter fw = new FileWriter(f)) {
            fw.write("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Informe de Empleados</title>");
            fw.write("<style>");
            fw.write("body { font-family: Arial, sans-serif; margin: 20px; }");
            fw.write("h1 { color: #2c3e50; text-align: center; }");
            fw.write("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            fw.write("th { background-color: #3498db; color: white; padding: 10px; }");
            fw.write("td { padding: 8px; vertical-align: top; }");
            fw.write("tr:nth-child(even) { background-color: #f2f2f2; }");
            fw.write(".activo { color: green; font-weight: bold; }");
            fw.write(".inactivo { color: red; font-weight: bold; }");
            fw.write("</style></head><body>");
            fw.write("<h1>Informe Detallado de Empleados</h1>");

            fw.write("<table>");
            fw.write("<tr><th>Cod</th><th>Nombre</th><th>DNI</th><th>Cargo</th><th>Contrato</th><th>NUSS</th><th>Email</th><th>Telefono</th><th>F. Nacimiento</th><th>F. Antiguedad</th><th>F. Despido</th><th>Estado</th><th>Usuario</th></tr>");

            for (Empleado e : listaTemporal) {
                fw.write("<tr>");
                fw.write("<td>" + e.getCodigo() + "</td>");
                fw.write("<td>" + e.getNombre() + "</td>");
                fw.write("<td>" + e.getIdentificador() + "</td>");
                fw.write("<td>" + e.getCargo() + "</td>");
                fw.write("<td>" + e.getContrato() + "</td>");
                fw.write("<td>" + e.getNuss() + "</td>");
                fw.write("<td>" + e.getEmail() + "</td>");
                fw.write("<td>" + e.getTelefono() + "</td>");
                fw.write("<td>" + (e.getFecha_nac() != null ? e.getFecha_nac() : "---") + "</td>");
                fw.write("<td>" + (e.getFecha_antig() != null ? e.getFecha_antig() : "---") + "</td>");
                fw.write("<td>" + (e.getFecha_desp() != null ? e.getFecha_desp() : "---") + "</td>");
                String classEstado = (e.getEstado() != null && e.getEstado().equalsIgnoreCase("Activo")) ? "activo" : "inactivo";
                fw.write("<td class='" + classEstado + "'>" + e.getEstado() + "</td>");

                String queryUser = "SELECT * FROM usuarios WHERE empleado = ?";
                try (PreparedStatement pstmtU = con.prepareStatement(queryUser)) {
                    pstmtU.setInt(1, e.getCodigo());
                    try (ResultSet rsU = pstmtU.executeQuery()) {
                        if (rsU.next()) {
                            fw.write("<td>" + rsU.getString("nom_user") + "</td>");
                        } else {
                            fw.write("<td>---</td>");
                        }
                    }
                }
                fw.write("</tr>");
            }
            fw.write("</table>");
            fw.write("<p style='margin-top:20px; color:#7f8c8d; font-size:12px;'>Generado el " + java.time.LocalDate.now() + "</p>");
            fw.write("</body></html>");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void printSQLException(SQLException e) {
        throw new UnsupportedOperationException("Error de Base de Datos: " + e.getMessage()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

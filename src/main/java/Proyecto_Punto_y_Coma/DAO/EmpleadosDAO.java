package Proyecto_Punto_y_Coma.DAO;

import Proyecto_Punto_y_Coma.ENTIDAD.Empleado;
import Proyecto_Punto_y_Coma.ENTIDAD.Usuario;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Clase de acceso a datos para la gestión de empleados.
 * Proporciona operaciones CRUD, generación de informes HTML,
 * y métodos auxiliares para interactuar con la tabla de empleados.
 * @author María Herrero Rodríguez
 */
public class EmpleadosDAO {

    /**
     * Muestra un menú interactivo para seleccionar el cargo de un empleado.
     * @param leer Scanner para capturar la opción del usuario.
     * @return El cargo seleccionado como cadena.
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

    /**
     * Muestra un menú interactivo para seleccionar el tipo de contrato.
     * @param leer Scanner para capturar la opción del usuario.
     * @return El tipo de contrato seleccionado.
     */
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
     * Registra un nuevo empleado en la base de datos solicitando
     * todos los datos por consola y mostrando el ID autogenerado.
     * @param leer Scanner para la entrada de datos.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error durante la inserción.
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
     * Recupera y muestra por consola todos los empleados registrados.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si falla la consulta.
     */
    public void mostrarEmpleados(Connection con) throws SQLException {
        String query = "SELECT * from empleados";
        System.out.println("--- LISTA DE EMPLEADOS ---");
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Empleado emp = new Empleado(
                        rs.getInt("Cod_empleado"),
                        rs.getString("Id_empleado"),
                        rs.getString("Nombre"),
                        rs.getString("Email"),
                        rs.getString("Telefono"),
                        rs.getString("Cargo"),
                        rs.getString("nss"),
                        rs.getString("tipo_contrato"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getDate("fecha_antiguedad"),
                        rs.getDate("fecha_despido"),
                        rs.getString("Estado")
                );
                System.out.println(emp);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * Verifica si existe un empleado en la base de datos por su código.
     * @param con Conexión activa.
     * @param codigo Código del empleado a buscar.
     * @return true si existe, false en caso contrario.
     */
    public boolean existeEmpleado(Connection con, int codigo) {
        boolean existe = false;
        String query = "SELECT 1 FROM empleados WHERE cod_empleado = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    existe = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existe;
    }

    /**
     * Menú interactivo para modificar cualquier campo de un empleado
     * usando ResultSets actualizables.
     * @param con Conexión activa.
     * @param leer Scanner para seleccionar campo y nuevo valor.
     * @throws SQLException Si falla la actualización.
     */
    public void modificarEmpleado(Connection con, Scanner leer) throws SQLException {
        int opcion = 0;
        int cod = 0;
        String nuevo = "";
        String campo = "";
        System.out.println("Inserte el código del empleado a modificar");
        cod = leer.nextInt();
        leer.nextLine();
        if (existeEmpleado(con, cod)) {
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
                    case 1: campo = "Id_empleado"; break;
                    case 2: campo = "Nombre"; break;
                    case 3: campo = "Telefono"; break;
                    case 4: campo = "Email"; break;
                    case 5: campo = "Cargo"; break;
                    case 6: campo = "nss"; break;
                    case 7: campo = "tipo_contrato"; break;
                    case 8: campo = "fecha_nacimiento"; break;
                    case 9: campo = "fecha_antiguedad"; break;
                    case 10: campo = "Estado"; break;
                    case 11: campo = "fecha_despido"; break;
                    case 0: System.out.println("Saliendo..."); break;
                    default: System.out.println("Opción no válida"); break;
                }

                if (opcion == 5) {
                    nuevo = seleccionarCargo(leer);
                } else if (opcion == 7) {
                    nuevo = seleccionarContrato(leer);
                } else if (opcion == 10) {
                    System.out.println("Seleccione el nuevo estado:");
                    System.out.println("1. Activo");
                    System.out.println("2. Inactivo");
                    System.out.println("3. Baja Enfermedad");
                    System.out.println("4. Excedencia");
                    System.out.print("Elija una opción: ");
                    int opEst = leer.nextInt();
                    leer.nextLine();
                    switch (opEst) {
                        case 1: nuevo = "Activo"; break;
                        case 2: nuevo = "Inactivo"; break;
                        case 3: nuevo = "Baja Enfermedad"; break;
                        case 4: nuevo = "Excedencia"; break;
                        default: nuevo = "Activo"; break;
                    }
                } else if (opcion != 0) {
                    System.out.println("Introduzca el nuevo " + campo);
                    nuevo = leer.nextLine();
                }

                if (opcion != 0){
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
     * Menú principal del módulo de empleados con opciones CRUD e informe HTML.
     * @param con Conexión activa.
     * @param leer Scanner para la navegación.
     * @throws SQLException Si falla alguna operación SQL.
     * @throws IOException Si falla la generación del informe HTML.
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
                case 1: crearEmpleado(leer, con); break;
                case 2: modificarEmpleado(con, leer); break;
                case 3: mostrarEmpleados(con); break;
                case 4: crearInformeEmpleados(con); break;
                case 0: System.out.println("Saliendo..."); break;
                default: System.out.println("Opcion no valida"); break;
            }
        } while (opcion != 0);
    }

    /**
     * Obtiene un empleado completo desde la base de datos por su código.
     * @param con Conexión activa.
     * @param codigo Código del empleado.
     * @return Objeto Empleado con todos sus datos, o null si no existe.
     * @throws SQLException Si falla la consulta.
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
                    String nss = rs.getString("nss");
                    String contrato = rs.getString("tipo_contrato");
                    java.sql.Date fNac = rs.getDate("fecha_nacimiento");
                    java.sql.Date fAnt = rs.getDate("fecha_antiguedad");
                    java.sql.Date fDesp = rs.getDate("fecha_despido");
                    String estado = rs.getString("estado");
                    empleado = new Empleado(cod, dni, nombre, email, telf, cargo, nss, contrato, fNac, fAnt, fDesp, estado);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empleado;
    }

    /**
     * Genera un archivo HTML con la lista completa de empleados usando toString().
     * El archivo se guarda en el directorio "Informes" como "informe_empleados.html".
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si falla la consulta.
     * @throws IOException Si falla la escritura del archivo.
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
                        rs.getString("nss"),
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
        File f = new File(dir, "informe_empleados.html");

        try (FileWriter fw = new FileWriter(f)) {
            fw.write("<html><body><h1>Informe de Empleados</h1>\n");
            fw.write("<p>Generado el " + java.time.LocalDate.now() + "</p>\n<hr>\n");

            for (Empleado e : listaTemporal) {
                fw.write("<pre>" + e + "</pre>\n<hr>\n");
            }

            fw.write("</body></html>\n");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void printSQLException(SQLException e) {
        throw new UnsupportedOperationException("Error de Base de Datos: " + e.getMessage());
    }
}

# Informe de Proyecto: Punto y Coma
## SUBSISTEMA DE COMPRAS A PROVEEDORES: María Herrero Rodríguez

## 1. Descripción
Por el momento, he creado la base de mi subsistema:
- Tenemos una gestión de usuarios, en el main podemos iniciar sesión o crear uno nuevo. Cada usuario está vinculado a un empleado. Para probar el main, debes de crear un usuario con el cargo "Admin" y otro con el cargo "Encargado Compras", ya que sino no funcionará correctamente. En el futuro, me gustaría agregar una funcionalidad en la que simplemente el usuario tenga que elegir el cargo, y así evitar problema.
- Tenemos también la gestión de proveedores y sus productis. En el futuro, me gustaría añadir una funcionalidad para seleccionar el estado al querer modificarlo, ya que si no se escribe bien pueden surgir problemas al añadirlos a la base de datos. Lo mismo sucede al modificar el estado de un empleado.
- Por último, tenemos la gestión de pedidos. El funcionamiento se basa en crear una Orden de compra, mostrar los productos disponibles (activos) del proveedor al que se le va a comprar e ir añadiendolos a la compra. Se puede gestionar su estado. Me falta añadir la funcionalidad de realizar una compra automática cuando un producto llega al stock mínimo.

## 2. Fragmentos Relevantes
- Creación de informes HTML:
1. Informe con la información de los proveedores y los productos que tiene en nuestra base de datos.
```java
    public void crearInformeProveedores(Connection con) throws SQLException, IOException {
        ArrayList<Proveedor> proveedores = rellenarProductosProveedores(con);
        File f = new File("Informe_Proveedores.html");
        FileWriter fw = null;

        try {
            fw = new FileWriter(f);
            
            fw.write("<html><head><meta charset='UTF-8'><title>Informe Proveedores</title></head><body>");
            fw.write("<h1>Informe de Proveedores y Productos</h1>");

            fw.write("<table border='1' cellpadding='10' cellspacing='0' style='width:100%;'>");
            fw.write("<tr style='background-color: #eee;'><th>Datos del Proveedor</th><th>Catálogo de Productos</th></tr>");

            for (Proveedor p : proveedores) {
                fw.write("<tr>");

                fw.write("<td valign='top'>" + p.toString() + "</td>");

                fw.write("<td>");
                ArrayList<Producto> productos = p.getProductos();

                if (productos != null && !productos.isEmpty()) {
                    fw.write("<ul>");
                    for (Producto prod : productos) {
                        fw.write("<li>" + prod.toString() + "</li>");
                    }
                    fw.write("</ul>");
                } else {
                    fw.write("<i>No hay productos registrados para este proveedor.</i>");
                }

                fw.write("</td>");
                fw.write("</tr>");
            }

            fw.write("</table></body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fw.close();
        }
    }
```

2. Informe con la información de los empleados y si tienen un usuario asociado.
```Java
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

        File f = new File("Informe_Completo_Empleados.html");
        FileWriter fw = null;
        
        try {
            
            fw = new FileWriter(f);

            fw.write("<html><body>");
            fw.write("<h1>Informe Detallado de Empleados</h1>");
            fw.write("<table border='1' cellpadding='10' cellspacing='0'>");
            fw.write("<tr><th>Ficha del Empleado</th><th>Detalles de Usuario</th></tr>");

            for (Empleado e : listaTemporal) {
                fw.write("<tr>");

                fw.write("<td>" + e.toString() + "</td>");

                String queryUser = "SELECT * FROM usuarios WHERE empleado = " + e.getCodigo();
                try (Statement stmtU = con.createStatement(); ResultSet rsU = stmtU.executeQuery(queryUser)) {
                    if (rsU.next()) {
                        Usuario usu = new Usuario(
                                rsU.getString("nom_user"),
                                rsU.getString("contrasenya"),
                                rsU.getInt("empleado")
                        );
                        usu.setId(rsU.getInt("id_user"));

                        fw.write("<td>" + usu.toString() + "</td>");
                    } else {
                        fw.write("<td>[ Sin usuario asignado ]</td>");
                    }
                }
                fw.write("</tr>");
            }
            fw.write("</table></body></html>");
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            fw.close();
        }
    }
```
- Lectura de un fichero csv que permite añadir productos a la base de datos. Los datos se separan por comas y siguen el siguiente formato: Nombre del producto , Precio , Stock , ID_Proveedor. Adjunto un .csv para que se pueda probar.
```Java
public void cargarProductosDesdeCSV(Connection con, String rutaArchivo) {
        String sql = "INSERT INTO Productos (Nombre, Precio_unidad, Stock, Proveedor) VALUES (?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo)); PreparedStatement pstmt = con.prepareStatement(sql)) {

            String linea;
            int filasInsertadas = 0;

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");

                try {
                    pstmt.setString(1, datos[0].trim());          
                    pstmt.setDouble(2, Double.parseDouble(datos[1].trim()));
                    pstmt.setInt(3, Integer.parseInt(datos[2].trim()));
                    pstmt.setInt(4, Integer.parseInt(datos[3].trim()));

                    pstmt.executeUpdate();
                    filasInsertadas++;

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Error en línea: " + linea + ". Saltando registro...");
                }
            }

            System.out.println("Carga finalizada. Se han insertado " + filasInsertadas + " productos.");

        } catch (IOException | SQLException e) {
            System.err.println("Error al procesar el archivo o la base de datos: " + e.getMessage());
        }
    }
```

# Link de GitHub
Te paso en la entrega únicamente mi parte. A través de este link puedes ver todo el progreso en GitHub que llevamos como grupo.
[GitHub Punto Y Coma](https://github.com/mariia0313/PROYECTO_PuntoyComa.git)
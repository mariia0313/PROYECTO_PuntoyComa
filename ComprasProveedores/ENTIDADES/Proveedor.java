package ComprasProveedores.ENTIDAD;

/**
 * Clase que representa a un proveedor externo.
 * Gestiona una colección de productos que ofrece al sistema.
 * * @author María Herrero Rodríguez
 * @see Persona
 */

import java.util.ArrayList;
import java.util.Set;

public class Proveedor extends Persona{
    /** Lista de productos suministrados por este proveedor. */
    private ArrayList<Producto> productos = new ArrayList<>();

    /**
     * Constructor para la entidad Proveedor.
     * * @param cod Código de proveedor.
     * @param identificador CIF/NIF.
     * @param nombre Nombre de la empresa o profesional.
     * @param email Email de contacto comercial.
     * @param telefono Teléfono de atención.
     * @param estado Estado de la relación comercial.
     */
    public Proveedor(int cod, String identificador, String nombre, String email, String telefono, String estado){
        super(cod, identificador, nombre, email, telefono, estado);
    }

    /**
     * Añade un nuevo producto al catálogo del proveedor.
     * @param producto Objeto Producto a registrar.
     */
    public void addProducto(Producto producto){
        productos.add(producto);

    }

    /**
     * Busca productos en el catálogo por su nombre (sin distinguir mayúsculas de minúsculas)
     * y los muestra por consola si se encuentra una coincidencia exacta.
     * * @param nombre Nombre del producto que se desea buscar.
     */
    public void buscarProductoPorNombre(String nombre){
        for (int i = 0; i < productos.size(); i++ ){
            if (productos.get(i).getNombre().equalsIgnoreCase(nombre)){
                System.out.println(productos.get(i));
            }
        }
    }

    /**
     * Busca un producto en el catálogo mediante su código identificador
     * y muestra su información por consola si existe.
     * * @param cod Código numérico del producto a buscar.
     */
    public void buscarProductoPorCOD(int cod){
        for (int i = 0; i < productos.size(); i++ ){
            if (productos.get(i).getCOD()==cod){
                System.out.println(productos.get(i));
            }
        }
    }

    /**
     * Elimina un producto del catálogo del proveedor basándose en su código.
     * * @param cod Código del producto que se desea eliminar.
     */
    public void eliminarProductoPorCOD(int cod){
        for (int i = 0; i < productos.size(); i++ ){
            if (productos.get(i).getCOD()==cod){
                productos.remove(i);
            }
        }
    }

    /**
     * Muestra por consola la lista completa de productos registrados 
     * en el catálogo del proveedor.
     */
    public void mostrarProductos(){
        for (Producto x : productos){
            System.out.println(x);
        }
    }
    
    /**
     * Filtra y muestra únicamente los productos que tienen estado "Activo".
     */
    public void mostrarProductosActivos(){
        for (Producto x : productos){
            if(x.getEstado().equalsIgnoreCase("Activo")) {
                System.out.println(x);
            }
        }
    }

    /**
     * Devuelve una representación en texto de la ficha del proveedor,
     * incluyendo sus datos personales y el número de productos en catálogo.
     *
     * * @return String formateado con la información detallada del proveedor.
     */
    public String toString() {
        return "=== DATOS DEL PROVEEDOR ===\n"
                + "Código:        " + codigo + "\n"
                + "Identificador: " + identificador + "\n"
                + "Nombre:        " + nombre + "\n"
                + "Email:         " + email + "\n"
                + "Teléfono:      " + telefono + "\n"
                + "Estado:        " + estado + "\n"
                + "Catálogo:      " + productos.size() + " productos registrados\n"
                + "===========================";
    }
}
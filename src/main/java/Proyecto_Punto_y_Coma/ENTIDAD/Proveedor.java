package Proyecto_Punto_y_Coma.ENTIDAD;

import java.util.ArrayList;

/**
 * Representa un proveedor del sistema, que puede suministrar
 * uno o varios productos. Hereda de Persona los datos básicos
 * de contacto e identificador fiscal.
 * @author María Herrero Rodríguez
 */
public class Proveedor extends Persona{
    private ArrayList<Producto> productos = new ArrayList<>();

    /**
     * Constructor para un proveedor.
     * @param cod Código único del proveedor.
     * @param identificador CIF/NIF del proveedor.
     * @param nombre Nombre o razón social.
     * @param email Correo electrónico.
     * @param telefono Teléfono de contacto.
     * @param estado Estado (Activo/Inactivo).
     */
    public Proveedor(int cod, String identificador, String nombre, String email, String telefono, String estado){
        super(cod, identificador, nombre, email, telefono, estado);
    }

    /**
     * Añade un producto al catálogo del proveedor.
     * @param producto Producto a añadir.
     */
    public void addProducto(Producto producto){
        productos.add(producto);
    }

    /**
     * Busca productos cuyo nombre coincida (sin distinguir mayúsculas).
     * @param nombre Nombre o parte del nombre a buscar.
     */
    public void buscarProductoPorNombre(String nombre){
        for (int i = 0; i < productos.size(); i++ ){
            if (productos.get(i).getNombre().equalsIgnoreCase(nombre)){
                System.out.println(productos.get(i));
            }
        }
    }

    /**
     * Busca un producto por su código interno.
     * @param cod Código del producto.
     */
    public void buscarProductoPorCOD(int cod){
        for (int i = 0; i < productos.size(); i++ ){
            if (productos.get(i).getCOD()==cod){
                System.out.println(productos.get(i));
            }
        }
    }

    /**
     * Elimina un producto del catálogo del proveedor por su código.
     * @param cod Código del producto a eliminar.
     */
    public void eliminarProductoPorCOD(int cod){
        for (int i = 0; i < productos.size(); i++ ){
            if (productos.get(i).getCOD()==cod){
                productos.remove(i);
            }
        }
    }

    /** Muestra por consola todos los productos del proveedor. */
    public void mostrarProductos(){
        for (Producto x : productos){
            System.out.println(x);
        }
    }

      /** Muestra solo los productos con estado "Activo". */
    public void mostrarProductosActivos(){
        int contador = 0;
        for (Producto x : productos){
            if(x.getEstado().equalsIgnoreCase("Activo")) {
                System.out.println(x);
                contador++;
            }
        }

        if (contador == 0){
            System.out.println("No hay productos activos de este proveedor");
        }
    }

    /** @return Lista de productos del proveedor. */
    public ArrayList<Producto> getProductos(){
        return productos;
    }

    /**
     * Representación textual del proveedor con datos de contacto y número de productos.
     * @return String con la información del proveedor.
     */
    public String toString() {
        return String.format(
                "Código: %d | Identificador: %s%n" +
                "Nombre: %s | Email: %s | Teléfono: %s%n" +
                "Estado: %s | Productos: %d%n",
                codigo, identificador, nombre, email, telefono, estado, productos.size()
        );
    }
}

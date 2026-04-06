/* María Herrero Rodríguez
CLASE PROVEEDOR con arrraylist de los productos que vende
*/

import java.util.ArrayList;

public class Proveedor extends Persona{
    private ArrayList<Producto> productos = new ArrayList<>();

    public Proveedor(int cod, String nombre, String email, long telefono){
        super(cod, nombre, email, telefono);
        numproveedores++;
    }

    public void addAnyadirProducto(String n, String d, int s, int sm){
        Producto producto = new Producto(n, d, s, sm);
        productos.add(producto);

    }

    public void buscarProductoPorNombre(String nombre){
        for (int i = 0; i < productos.size(); i++ ){
            if (productos.get(i).getNombre().equalsIgnoreCase(nombre)){
                System.out.println(productos.get(i));
            }
        }
    }

    public void buscarProductoPorCOD(int cod){
        for (int i = 0; i < productos.size(); i++ ){
            if (productos.get(i).getCOD()==cod){
                System.out.println(productos.get(i));
            }
        }
    }

    public void eliminarProductoPorCOD(int cod){
        for (int i = 0; i < productos.size(); i++ ){
            if (productos.get(i).getCOD()==cod){
                productos.remove(i);
            }
        }
    }

    public void mostrarProductos(){
        for (Producto x : productos){
            System.out.println(x);
        }
    }

    public int getCod(){
        return codigo;
    }


    public String toString(){
         String rdo = "PROVEEDOR CON CÓDIGO: " + codigo + "\n Nombre: " + nombre + "\nEmail: " + email + "\nTeléfono: " + telefono;
        return rdo;
    }
}
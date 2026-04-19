package ComprasProveedores;

public class LineaCompra {
    private int num_linea;
    private double precio_unidad;
    private double precio;
    private int cantidad;

    public LineaCompra(int num, double precio, int cantidad){
        this.num_linea = num;
        this.precio_unidad = precio;
        this.cantidad = cantidad;
        this.precio = precio * cantidad;
    }

    public void modificarCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.precio = cantidad * precio;
    }

    public void modificarPrecio(double precio){
        this.precio_unidad = precio;
        this.precio = precio_unidad * cantidad;
    }

    public double getPrecioTotal() {
        return precio;
    }
}
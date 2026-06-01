package ComprasProveedores.ENTIDAD;

/**
 * Representa cada una de las líneas detalladas dentro de una Orden de Compra.
 */

public class LineaCompra {
    private int num_linea;
    private String nombreProducto;
    private double precio_unidad;
    private double precio;
    private int cantidad;

    /**
     * Constructor que inicializa la línea y calcula el precio total.
     */
    public LineaCompra(int num, String nombreProducto, double precioUnidad, int cantidad) {
        this.num_linea = num;
        this.nombreProducto = nombreProducto;
        this.precio_unidad = precioUnidad;
        this.cantidad = cantidad;
        this.precio = precioUnidad * cantidad;
    }

    public void setId(int linea){
        this.num_linea = linea;
    }

    public int getNumLinea() { return num_linea; }

    public String getNombreProducto() { return nombreProducto; }

    public double getPrecioUnidad() { return precio_unidad; }

    public int getCantidad() { return cantidad; }

    public double getPrecioTotal() { return precio; }

    public String toString() {
        return String.format("Línea %d: %s | Cant: %d | Precio unidad: %.2f € | Total: %.2f €",
                num_linea, nombreProducto, cantidad, precio_unidad, precio);
    }
}
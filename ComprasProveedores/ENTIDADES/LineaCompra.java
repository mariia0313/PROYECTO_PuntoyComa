package ComprasProveedores.ENTIDADES;

/**
 * Representa cada una de las líneas detalladas dentro de una Orden de Compra.
 */

public class LineaCompra {
    private int num_linea;
    private double precio_unidad;
    private double precio; // Precio total de la línea (precio_unidad * cantidad)
    private int cantidad;

    /**
     * Constructor que inicializa la línea y calcula el precio total de la
     * misma.
     *
     * @param num Número de posición de la línea en la orden.
     * @param precio Precio unitario del producto.
     * @param cantidad Cantidad de unidades.
     */
    public LineaCompra(int num, double precio, int cantidad){
        this.num_linea = num;
        this.precio_unidad = precio;
        this.cantidad = cantidad;
        this.precio = precio * cantidad;
    }

    /** @return Precio total de la línea */
    public double getPrecioTotal() {
        return precio;
    }
}
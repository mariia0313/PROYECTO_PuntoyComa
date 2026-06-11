package Proyecto_Punto_y_Coma.ENTIDAD;

/**
 * Representa una línea individual dentro de una orden de compra.
 * Cada línea vincula una cantidad con un producto, y pertenece
 * de forma exclusiva a una orden (no_compra).
 * @author María Herrero Rodríguez
 */
public class LineaCompra {
    private int no_compra;
    private int no_linea;
    private int cantidad;
    private int producto;

    /**
     * Constructor para una línea de compra.
     * @param no_compra Identificador de la orden a la que pertenece.
     * @param no_linea Número de línea dentro de la orden.
     * @param cantidad Cantidad de producto solicitada.
     * @param producto Identificador del producto.
     */
    public LineaCompra(int no_compra, int no_linea, int cantidad, int producto) {
        this.no_compra = no_compra;
        this.no_linea = no_linea;
        this.cantidad = cantidad;
        this.producto = producto;
    }

    /** @return La orden de compra a la que pertenece. */
    public int getNo_compra() { return no_compra; }
    /** @param no_compra Nueva orden de compra asociada. */
    public void setNo_compra(int no_compra) { this.no_compra = no_compra; }
    /** @return El número de línea dentro de la orden. */
    public int getNo_linea() { return no_linea; }
    /** @param no_linea Nuevo número de línea. */
    public void setNo_linea(int no_linea) { this.no_linea = no_linea; }
    /** @return La cantidad de producto solicitada. */
    public int getCantidad() { return cantidad; }
    /** @param cantidad Nueva cantidad. */
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    /** @return El identificador del producto. */
    public int getProducto() { return producto; }
    /** @param producto Nuevo producto asociado. */
    public void setProducto(int producto) { this.producto = producto; }

    /**
     * Representación textual de la línea de compra.
     * @return String con número de línea, producto y cantidad.
     */
    public String toString() {
        return String.format("Línea %d: Producto %d | Cant: %d", no_linea, producto, cantidad);
    }
}

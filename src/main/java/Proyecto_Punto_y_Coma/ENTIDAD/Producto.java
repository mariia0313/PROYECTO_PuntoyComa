package Proyecto_Punto_y_Coma.ENTIDAD;

/**
 * Representa un producto del catálogo, con datos de stock,
 * precio y proveedor asociado.
 * @author María Herrero Rodríguez
 */
public class Producto{
    private int cod;
    private String nombre;
    private String descripcion;
    private int stock;
    private int stock_minimo;
    private String estado;
    private int proveedor;
    private double precio_unidad;

    /**
     * Constructor completo para un producto.
     * @param cod Código único del producto.
     * @param n Nombre del producto.
     * @param d Descripción breve.
     * @param s Stock actual.
     * @param sm Stock mínimo permitido.
     * @param estado Estado (Activo/Inactivo).
     * @param proveedor Código del proveedor que lo suministra.
     * @param precio Precio unitario.
     */
    public Producto(int cod, String n, String d, int s, int sm, String estado, int proveedor, double precio) {
        this.cod = cod;
        this.nombre = n;
        this.descripcion = d;
        this.stock = s;
        this.stock_minimo = sm;
        this.estado = estado;
        this.proveedor = proveedor;
        this.precio_unidad = precio;
    }

    /** @param c Nuevo código de producto. */
    public void setCod(int c) { cod = c; }
    /** @param n Nuevo nombre. */
    public void setNombre(String n) { nombre = n; }
    /** @return Estado del producto. */
    public String getEstado() { return estado; }
    /** @return Nombre del producto. */
    public String getNombre() { return nombre; }
    /** @return Código único del producto. */
    public int getCOD() { return cod; }
    /** @return Stock actual. */
    public int getStock() { return stock; }
    /** @return Stock mínimo permitido. */
    public int getStockMinimo() { return stock_minimo; }
    /** @return Código del proveedor asociado. */
    public int getProveedor() { return proveedor; }
    /** @return Precio unitario del producto. */
    public double getPrecioUnidad() { return precio_unidad; }
    /** @param d Nueva descripción. */
    public void setDescripcion(String d) { descripcion = d; }
    /** @param s Nuevo stock. */
    public void setStock(int s) { stock = s; }

    /**
     * Establece el stock mínimo si el valor es positivo.
     * @param sm Nuevo stock mínimo.
     * @return true si se actualizó correctamente, false si el valor no es válido.
     */
    public boolean setStockMinimo(int sm) {
        boolean valido = sm > 0;
        if (valido) stock_minimo = sm;
        return valido;
    }

    /**
     * Representación textual del producto con todos sus datos.
     * @return String multilínea con la información del producto.
     */
    public String toString() {
        return String.format(
                "Código: %d | Nombre: %s%n" +
                "Descripción: %s%n" +
                "Stock: %d | Mínimo: %d | Precio: %.2f%n" +
                "Proveedor: %d | Estado: %s%n",
                cod, nombre, descripcion, stock, stock_minimo, precio_unidad, proveedor, estado
        );
    }
}

package Proyecto_Punto_y_Coma.ENTIDAD;

import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Representa una orden de compra realizada a un proveedor.
 * Contiene una cabecera con datos de la orden y una lista de líneas
 * de compra que detallan los productos solicitados.
 * @author María Herrero Rodríguez
 */
public class OrdenCompra {

    private int num_orden;
    private String direccion;
    private LocalDate fecha;
    private String telefono;
    private double precio_total;
    private Empleado empleado;
    private int proveedor;
    private String estado;
    private ArrayList<LineaCompra> lineas = new ArrayList<>();

    /**
     * Constructor para una orden de compra.
     * @param num_orden Número único de orden.
     * @param dir Dirección de envío.
     * @param fecha Fecha de creación de la orden.
     * @param telef Teléfono de contacto.
     * @param empleado Empleado que realizó el pedido.
     * @param proveedor Código del proveedor.
     * @param estado Estado inicial del pedido.
     */
    public OrdenCompra(int num_orden, String dir, LocalDate fecha, String telef, Empleado empleado, int proveedor, String estado) {
        this.num_orden = num_orden;
        this.direccion = dir;
        this.fecha = fecha;
        this.telefono = telef;
        this.precio_total = 0;
        this.empleado = empleado;
        this.proveedor = proveedor;
        this.estado = estado;
    }

    /**
     * Añade una línea de compra a la orden, asignándole automáticamente
     * el siguiente número de línea.
     * @param linea Línea de compra a añadir.
     */
    public void addLinea(LineaCompra linea) {
        linea.setNo_linea(lineas.size() + 1);
        lineas.add(linea);
    }

    /** @param precio_total Precio total de la orden. */
    public void setPrecio_total(double precio_total) { this.precio_total = precio_total; }
    /** @return Número único de orden. */
    public int getNumOrden() { return num_orden; }
    /** @return Dirección de envío. */
    public String getDireccion() { return direccion; }
    /** @return Fecha de la orden. */
    public LocalDate getFecha() { return fecha; }
    /** @return Teléfono de contacto. */
    public String getTelefono() { return telefono; }
    /** @return Precio total acumulado. */
    public double getPrecioTotal() { return precio_total; }
    /** @return Código del proveedor asociado. */
    public int getProveedor() { return proveedor; }
    /** @return Estado actual del pedido. */
    public String getEstado() { return estado; }
    /** @param estado Nuevo estado del pedido. */
    public void setEstado(String estado) { this.estado = estado; }
    /** @return Lista de líneas de compra. */
    public ArrayList<LineaCompra> getLineas() { return lineas; }
    /** @return Empleado que realizó el pedido. */
    public Empleado getEmpleado() { return empleado; }

    /**
     * Representación textual completa de la orden de compra,
     * incluyendo cabecera y todas sus líneas.
     * @return String multilínea con los datos de la orden.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Orden: %d | Fecha: %s%n", num_orden, fecha));
        sb.append(String.format("Dirección: %s | Teléfono: %s%n", direccion, telefono));
        sb.append(String.format("Proveedor: %d | Estado: %s%n", proveedor, estado));
        sb.append(String.format("Empleado: %s | Total: %.2f%n",
                empleado != null ? empleado.getNombre() : "---", precio_total));
        if (!lineas.isEmpty()) {
            sb.append("Líneas:\n");
            for (LineaCompra l : lineas) {
                sb.append("  ").append(l.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}

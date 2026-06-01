package ComprasProveedores.ENTIDAD;

import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Gestiona una orden de compra realizada por un empleado. Mantiene un contador
 * estático para la generación de números de orden.
 */

public class OrdenCompra {

    private int num_orden;
    private String direccion;
    private LocalDate fecha;
    private String telefono;
    private double precio_total;
    private ArrayList<LineaCompra> lineas = new ArrayList<>();
    private Empleado empleado;

   /**
     * Constructor para crear una instancia de OrdenCompra con un número de orden específico.
     * Inicializa el precio total a cero y establece los datos de entrega y el empleado responsable.
     * * @param num_orden El número identificador único asignado a esta orden.
     * @param dir       La dirección de entrega o destino del pedido.
     * @param fecha     La fecha en la que se registra la orden de compra.
     * @param telef     El teléfono de contacto asociado a la orden.
     * @param empleado  El objeto {@link Empleado} que gestiona o realiza el pedido.
    */
    public OrdenCompra (int num_orden, String dir, LocalDate fecha, String telef, Empleado empleado) {
        this.num_orden = num_orden;
        this.direccion = dir;
        this.fecha = fecha;
        this.precio_total = 0;
        this.empleado = empleado;
    }

    /**
     * Añade una nueva línea de producto al pedido y actualiza automáticamente
     * el precio total.
     *
     * *@param precio_unidad Precio del producto por unidad.
     * *@param cantidad Unidades solicitadas.
     */
    public void addLinea(LineaCompra linea) {
        int num = (lineas.size() + 1);
        linea.setId(num);
        lineas.add(linea);
        precio_total += linea.getPrecioTotal();
    }

    public int getNumOrden() { return num_orden; }

    public String getDireccion() { return direccion; }

    public LocalDate getFecha() { return fecha; }

    public String getTelefono() { return telefono; }

    public double getPrecioTotal() { return precio_total; }

    public ArrayList<LineaCompra> getLineas() { return lineas; }

    public Empleado getEmpleado() { return empleado; }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("           FACTURA - ORDEN DE COMPRA\n");
        sb.append("============================================\n");
        sb.append(String.format("Nº Orden: %d%n", num_orden));
        sb.append(String.format("Fecha:    %s%n", fecha));
        sb.append(String.format("Teléfono: %s%n", telefono));
        sb.append(String.format("Dirección: %s%n%n", direccion));
        sb.append("--- LÍNEAS ---\n");
        for (LineaCompra l : lineas) {
            sb.append(l.toString()).append("\n");
        }
        sb.append("--------------------------------------------\n");
        sb.append(String.format("TOTAL: %.2f €%n", precio_total));
        sb.append("============================================\n");
        return sb.toString();
    }
}
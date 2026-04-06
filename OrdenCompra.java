import java.util.ArrayList;
import java.time.LocalDate;

public class OrdenCompra {

    private static int contador = 0;
    private int num_orden;
    private String direccion;
    private LocalDate fecha;
    private String telefono;
    private double precio_total;
    private ArrayList<LineaCompra> lineas = new ArrayList<>();
    private Empleado empleado;

    public OrdenCompra (String dir, LocalDate fecha, String telef, Empleado empleado) {
        this.num_orden = contador;
        this.direccion = dir;
        this.fecha = fecha;
        this.precio_total = 0;
        this.empleado = empleado;
        contador++;
    }

    public void addLinea (double precio_unidad, int cantidad) {
        LineaCompra linea = new LineaCompra(lineas.size(), precio_unidad, cantidad);
        lineas.add(linea);
        precio_total += linea.getPrecioTotal();
    }
}
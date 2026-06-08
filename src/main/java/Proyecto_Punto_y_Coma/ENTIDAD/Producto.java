package Proyecto_Punto_y_Coma.ENTIDAD;

/**
 * Representa un producto dentro del catálogo del sistema. Contiene información
 * sobre stock, precios y el proveedor asociado.
 *
 * * @author María Herrero Rodríguez
 */

public class Producto{
    private int cod;
    private String nombre;
    private String descrip;
    private int stock;
    private int stock_min;
    private String estado;
    private int proveedor;
    private double precio;
    
    /**
     * Constructor completo para la creación de un Producto.
     * * @param cod Código identificador único del producto.
     * @param n Nombre comercial del producto.
     * @param d Descripción detallada.
     * @param s Cantidad actual en stock.
     * @param sm Umbral de stock mínimo para alertas.
     * @param estado Estado de disponibilidad (ej: "Activo").
     * @param proveedor Código del proveedor que suministra el producto.
     * @param precio Coste unitario del producto.
     */
    public Producto(int cod, String n, String d, int s, int sm, String estado, int proveedor, double precio) {
        this.cod = cod;
        this.nombre = n;
        this.descrip = d;
        this.stock = s;
        this.stock_min = sm;
        this.estado = estado;
        this.proveedor = proveedor;
        this.precio = precio;
    }

    public void setCod(int c){
        cod = c;
    }
    public void setNombre(String n){
        nombre = n;
    }
    
    public String getEstado(){
        return estado;
    }

    public String getNombre(){
        return nombre;
    }

    public int getCOD(){
        return cod;
    }

    public int getStock(){
        return stock;
    }

    public int getStockMin(){
        return stock_min;
    }

    public int getProveedor(){
        return proveedor;
    }

    public double getPrecio(){
        return precio;
    }

    public void setDesc(String d){
        descrip = d;
    }
    public void setStock(int s){
        stock = s;
    }
    
    /**
     * Establece el stock mínimo asegurando que sea un valor positivo.
     *
     * * @param sm El valor del stock mínimo a validar.
     * @return {@code true} si el valor es válido (>0), {@code false} en caso
     * contrario.
     */
    public boolean setStockMin(int sm){
        boolean pasa = true;
        if (sm <= 0) {
            pasa = false;
        }
        stock_min = sm;
        return pasa;
    }

    /**
     * Genera una ficha técnica del producto para mostrar por consola.
     * @return String multilínea con todos los detalles del producto.
     */
    public String toString() {
        return String.format(
                "    > [%3d] %-15s | Precio: %7.2f eur | Stock: %d | Min: %d | Est: %s",
                cod,
                nombre,
                precio,
                stock,
                stock_min,
                estado
        );
    }
}
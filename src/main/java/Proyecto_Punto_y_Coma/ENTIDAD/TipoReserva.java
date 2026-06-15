package Proyecto_Punto_y_Coma.ENTIDAD;

/**
 * Clase abstracta que representa un recurso reservable del hotel
 * (Alojamiento, Actividad o SalaEvento).
 * Contiene los atributos comunes a todos los tipos de recurso.
 * @author David Catalán Aragó
 */
public abstract class TipoReserva{

    protected int cod;
    protected String nombre;
    protected double precioBase;
    protected double iva;
    protected int capacidad;
    protected String estado;

    /**
     * Constructor de TipoReserva.
     * @param cod        Código identificador del recurso.
     * @param nombre     Nombre descriptivo del recurso.
     * @param precioBase Precio base del recurso.
     * @param iva        IVA aplicado (ej: 0.21).
     * @param capacidad  Capacidad máxima de personas.
     * @param estado     Estado del recurso (ej: "Disponible", "Ocupado"...).
     */
    public TipoReserva(int cod, String nombre, double precioBase, double iva, int capacidad, String estado) {
        this.cod = cod;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.iva = iva;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    /**
     * Calcula el importe del IVA sobre el precio base.
     * @return Importe del IVA.
     */
    public double precioIVA(){
        return this.precioBase * this.iva;
    }

    /**
     * Calcula el precio total sumando precio base e IVA.
     * @return Precio total.
     */
    public double total() {
        return this.precioBase + precioIVA();
    }

    /** @return Código del recurso. */
    public int getCod(){
        return cod;
    }

    /** @param cod Código a asignar. */
    public void setCod(int cod){
        this.cod = cod;
    }

    /** @return Nombre del recurso. */
    public String getNombre(){
        return nombre;
    }

    /** @param nombre Nombre a asignar. */
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    /** @return Precio base del recurso. */
    public double getPrecioBase(){
        return precioBase;
    }

    /** @param precioBase Precio base a asignar. */
    public void setPrecioBase(double precioBase){
        this.precioBase = precioBase;
    }

    /** @return IVA del recurso. */
    public double getIva(){
        return iva;
    }

    /**
     * Establece el IVA. Debe estar entre 0 (inclusivo) y 1 (exclusivo).
     * @param iva Valor de IVA a asignar.
     */
    public void setIva(double iva) {
        if (iva < 0 || iva >= 1) {
            System.out.println("IVA no válido. Debe estar entre 0 y 1.");
        } else {
            this.iva = iva;
        }
    }

    /** @return Capacidad del recurso. */
    public int getCapacidad(){
        return capacidad;
    }

    /** @param capacidad Capacidad a asignar. */
    public void setCapacidad(int capacidad){
        this.capacidad = capacidad;
    }

    /** @return Estado del recurso. */
    public String getEstado(){
        return estado;
    }

    /** @param estado Estado a asignar. */
    public void setEstado(String estado){
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Código: " + cod + "\nNombre: " + nombre + "\nPrecio Base: " + precioBase + "\nIVA: " + (iva * 100) + "%" + "\nCapacidad: " + capacidad + "\nPrecio Total: " + total();
    }
}

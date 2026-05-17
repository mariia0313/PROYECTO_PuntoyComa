package GestionReservas.ENTIDAD;

import java.util.Date;

/**
 * Representa una reserva concreta del hotel.
 * Une un Cliente con un TipoReserva para unas fechas determinadas.
 * Implementa Serializable para poder almacenar objetos Reserva en ficheros.
 * @author David Catalán Aragó
 */
public class Reserva implements java.io.Serializable {

    private int cod;
    private Cliente cliente;
    private TipoReserva tipoReserva;
    private Date fechaInicio;
    private Date fechaFin;
    private String estado;

    /**
     * Constructor de Reserva.
     * @param cod         Código identificador de la reserva.
     * @param cliente     Cliente que realiza la reserva.
     * @param tipoReserva Recurso reservado (Alojamiento, Actividad o SalaEvento).
     * @param fechaInicio Fecha de inicio de la reserva.
     * @param fechaFin    Fecha de fin de la reserva.
     */
    public Reserva(int cod, Cliente cliente, TipoReserva tipoReserva, Date fechaInicio, Date fechaFin) {
        this.cod = cod;
        this.cliente = cliente;
        this.tipoReserva = tipoReserva;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = "Alta";
    }

    /**
     * Calcula el precio total de la reserva.
     * Si el tipo de reserva es Actividad el precio no se multiplica por días.
     * @return Precio total de la reserva.
     */
    public double calcularPrecioTotal() {
        long dias;
        if (tipoReserva instanceof Actividad) {
            dias=1;
        } else {
            dias = (fechaFin.getTime() - fechaInicio.getTime()) / (1000 * 60 * 60 * 24);
            if (dias <= 0) dias = 1;
        }
        return tipoReserva.total() * dias;
    }

    /**
     * Comprueba si esta reserva se solapa en fechas con otra reserva del mismo recurso.
     * Dos reservas se solapan si: inicioA menor que finB Y finA mayor que inicioB.
     * @param otraFechaInicio Fecha de inicio de la otra reserva.
     * @param otraFechaFin    Fecha de fin de la otra reserva.
     * @return true si hay solapamiento de fechas.
     */
    public boolean seSolapa(Date otraFechaInicio, Date otraFechaFin){
        return this.fechaInicio.before(otraFechaFin) && this.fechaFin.after(otraFechaInicio);
    }

    /** Cancela la reserva cambiando su estado a Baja. */
    public void cancelar(){
        this.estado = "Baja";
    }

    /** @return Código de la reserva. */
    public int getCod(){
        return cod;
    }

    /** @param cod Código a asignar. */
    public void setCod(int cod){
        this.cod = cod;
    }

    /** @return Cliente de la reserva. */
    public Cliente getCliente(){
        return cliente;
    }

    /** @param cliente Cliente a asignar. */
    public void setCliente(Cliente cliente){
        this.cliente = cliente;
    }

    /** @return TipoReserva (recurso) asociado. */
    public TipoReserva getTipoReserva(){
        return tipoReserva;
    }

    /** @param tipoReserva TipoReserva a asignar. */
    public void setTipoReserva(TipoReserva tipoReserva){
        this.tipoReserva = tipoReserva;
    }

    /** @return Fecha de inicio de la reserva. */
    public Date getFechaInicio(){
        return fechaInicio;
    }

    /** @param fechaInicio Fecha de inicio a asignar. */
    public void setFechaInicio(Date fechaInicio){
        this.fechaInicio = fechaInicio;
    }

    /** @return Fecha de fin de la reserva. */
    public Date getFechaFin(){
        return fechaFin;
    }

    /** @param fechaFin Fecha de fin a asignar. */
    public void setFechaFin(Date fechaFin){
        this.fechaFin = fechaFin; 
    }

    /** @return Estado de la reserva. */
    public String getEstado(){
        return estado;
    }

    /** @param estado Estado a asignar. */
    public void setEstado(String estado){
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "========== RESERVA ==========" + "\nCódigo Reserva: " + cod + "\nEstado: " + estado + "\nFecha Inicio: " + fechaInicio + "\nFecha Fin: " + fechaFin + "\nPrecio Total: " + calcularPrecioTotal() + " €" + "\n" + cliente.toString() + "\n" + tipoReserva.toString() + "\n=============================";
    }
}

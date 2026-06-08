package Proyecto_Punto_y_Coma.ENTIDAD;

import java.time.LocalTime;
/**
 * Representa una reserva de tipo actividad.
 * Extiende TipoReserva añadiendo horaInicio, horaFin y estado.
 * @author David Catalán Aragó
 * @see TipoReserva
 */
public class Actividad extends TipoReserva {

    private LocalTime horaInicio;
    private LocalTime horaFin;

    /**
     * Constructor de Actividad.
     * @param cod        Código identificador.
     * @param nombre     Nombre de la actividad.
     * @param precioBase Precio base de la actividad.
     * @param iva        IVA aplicado (entre 0 y 1).
     * @param capacidad  Capacidad máxima de participantes.
     * @param horaInicio Hora de inicio (ej: "10:00").
     * @param horaFin    Hora de fin (ej: "12:00").
     * @param estado     Estado (ej: "Disponible", "Completa", "Cancelada").
     */
    public Actividad(int cod, String nombre, double precioBase, double iva, int capacidad, LocalTime horaInicio, LocalTime horaFin, String estado) {
        super(cod, nombre, precioBase, iva, capacidad, estado);
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    /** @return Hora de inicio de la actividad. */
    public LocalTime getHoraInicio(){
        return horaInicio;
    }

    /** @param horaInicio Hora de inicio a asignar. */
    public void setHoraInicio(LocalTime horaInicio){
        this.horaInicio = horaInicio;
    }

    /** @return Hora de fin de la actividad. */
    public LocalTime getHoraFin(){
        return horaFin;
    }

    /** @param horaFin Hora de fin a asignar. */
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    /** @return Estado de la actividad. */
    public String getEstado(){
        return estado;
    }

    /** @param estado Estado a asignar. */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "--- ACTIVIDAD ---" + "\n" + super.toString() + "\nHora Inicio: " + horaInicio + "\nHora Fin: " + horaFin + "\nEstado: " + estado;
    }
}

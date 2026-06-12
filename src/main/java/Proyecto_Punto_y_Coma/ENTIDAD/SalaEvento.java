package Proyecto_Punto_y_Coma.ENTIDAD;

import java.time.LocalTime;
/**
 * Representa una reserva de tipo sala de evento.
 * Extiende TipoReserva añadiendo horaInicio y horaFin.
 * @author David Catalán Aragó
 * @see TipoReserva
 */
public class SalaEvento extends TipoReserva {

    private LocalTime horaInicio;
    private LocalTime horaFin;

    /**
     * Constructor de SalaEvento.
     * @param cod        Código identificador.
     * @param nombre     Nombre de la sala.
     * @param precioBase Precio base por hora.
     * @param iva        IVA aplicado (entre 0 y 1).
     * @param capacidad  Capacidad máxima de personas.
     * @param horaInicio Hora de inicio disponible (ej: "09:00").
     * @param horaFin    Hora de fin disponible (ej: "22:00").
     */
    public SalaEvento(int cod, String nombre, double precioBase, double iva, int capacidad, LocalTime horaInicio, LocalTime horaFin, String estado) {
        super(cod, nombre, precioBase, iva, capacidad, estado);
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    /** @return Hora de inicio de disponibilidad de la sala. */
    public LocalTime getHoraInicio(){
        return horaInicio;
    }

    /** @param horaInicio Hora de inicio a asignar. */
    public void setHoraInicio(LocalTime horaInicio){
        this.horaInicio = horaInicio;
    }

    /** @return Hora de fin de disponibilidad de la sala. */
    public LocalTime getHoraFin(){
        return horaFin;
    }

    /** @param horaFin Hora de fin a asignar. */
    public void setHoraFin(LocalTime horaFin){
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return "--- SALA EVENTO ---" + "\n" + super.toString() + "\nHora Inicio: " + horaInicio + "\nHora Fin: " + horaFin;
    }
}

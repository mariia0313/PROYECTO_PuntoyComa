package GestionReservas.ENTIDAD;

/**
 * Representa una reserva de tipo sala de evento.
 * Extiende TipoReserva añadiendo horaInicio y horaFin.
 * @author David Catalán Aragó
 * @see TipoReserva
 */
public class SalaEvento extends TipoReserva {

    private String horaInicio;
    private String horaFin;

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
    public SalaEvento(int cod, String nombre, double precioBase, double iva, int capacidad, String horaInicio, String horaFin) {
        super(cod, nombre, precioBase, iva, capacidad);
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    @Override
    public double precioIVA(){
        return super.precioBase * super.iva;
    }

    /** @return Hora de inicio de disponibilidad de la sala. */
    public String getHoraInicio(){
        return horaInicio;
    }

    /** @param horaInicio Hora de inicio a asignar. */
    public void setHoraInicio(String horaInicio){
        this.horaInicio = horaInicio;
    }

    /** @return Hora de fin de disponibilidad de la sala. */
    public String getHoraFin(){
        return horaFin;
    }

    /** @param horaFin Hora de fin a asignar. */
    public void setHoraFin(String horaFin){
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return "--- SALA EVENTO ---" + "\n" + super.toString() + "\nHora Inicio: " + horaInicio + "\nHora Fin: " + horaFin;
    }
}

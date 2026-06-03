package Proyecto_Punto_y_Coma.ENTIDAD;

/**
 * Representa una reserva de tipo alojamiento.
 * Extiende TipoReserva añadiendo el tipo de alojamiento.
 * @author David Catalán Aragó
 * @see TipoReserva
 */
public class Alojamiento extends TipoReserva {

    private String tipoAlojamiento;

    /**
     * Constructor de Alojamiento.
     * @param cod             Código identificador.
     * @param nombre          Nombre del alojamiento.
     * @param precioBase      Precio base por noche.
     * @param iva             IVA aplicado (entre 0 y 1).
     * @param capacidad       Capacidad de personas.
     * @param tipoAlojamiento Tipo (ej: "Habitación Doble", "Suite"...).
     */
    public Alojamiento(int cod, String nombre, double precioBase, double iva, int capacidad, String tipoAlojamiento) {
        super(cod, nombre, precioBase, iva, capacidad);
        this.tipoAlojamiento = tipoAlojamiento;
    }

    @Override
    public double precioIVA(){
        return super.precioBase * super.iva;
    }

    /** @return Tipo de alojamiento. */
    public String getTipoAlojamiento(){
        return tipoAlojamiento;
    }

    /** @param tipoAlojamiento Tipo de alojamiento a asignar. */
    public void setTipoAlojamiento(String tipoAlojamiento){
        this.tipoAlojamiento = tipoAlojamiento;
    }

    @Override
    public String toString(){
        return "--- ALOJAMIENTO ---" + "\n" + super.toString() + "\nTipo Alojamiento: " + tipoAlojamiento;
    }
}

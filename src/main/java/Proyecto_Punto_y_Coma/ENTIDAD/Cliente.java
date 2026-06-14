package Proyecto_Punto_y_Coma.ENTIDAD;

/**
 * Representa a un cliente del hotel.
 * @author David Catalán Aragó
 */
public class Cliente extends Persona implements java.io.Serializable {

    /**
     * Constructor de Cliente.
     * @param cod      Código identificador del cliente.
     * @param nombre   Nombre completo.
     * @param identificador      DNI/NIE del cliente.
     * @param email    Email de contacto.
     * @param telefono Teléfono de contacto.
     * @param Estado Estado del cliente
     */
    public Cliente(int cod, String nombre, String identificador, String email, String telefono, String Estado){
        super(cod, identificador, nombre, email, telefono, Estado);
    }

    @Override
    public String toString() {
        return "--- CLIENTE ---" + "\nCódigo: " + super.codigo + "\nNombre: " + super.nombre + "\nDNI: " + super.identificador + "\nEmail: " + super.email + "\nTeléfono: " + super.telefono + "\nEstado: " + super.estado;
    }
}

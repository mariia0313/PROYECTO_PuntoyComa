package Proyecto_Punto_y_Coma.ENTIDAD;

/**
 * Representa a un cliente del hotel.
 * @author David Catalán Aragó
 */
public class Cliente2 implements java.io.Serializable {

    private int cod;
    private String nombre;
    private String dni;
    private String email;
    private String telefono;

    /**
     * Constructor de Cliente.
     * @param cod      Código identificador del cliente.
     * @param nombre   Nombre completo.
     * @param dni      DNI/NIE del cliente.
     * @param email    Email de contacto.
     * @param telefono Teléfono de contacto.
     */
    public Cliente2(int cod, String nombre, String dni, String email, String telefono){
        this.cod = cod;
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
    }

    /** @return Código del cliente. */
    public int getCod(){
        return cod;
    }

    /** @param cod Código a asignar. */
    public void setCod(int cod){
        this.cod = cod;
    }

    /** @return Nombre del cliente. */
    public String getNombre(){
        return nombre;
    }

    /** @param nombre Nombre a asignar. */
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    /** @return DNI del cliente. */
    public String getDni(){
        return dni;
    }

    /** @param dni DNI a asignar. */
    public void setDni(String dni){
        this.dni = dni;
    }

    /** @return Email del cliente. */
    public String getEmail(){
        return email;
    }

    /** @param email Email a asignar. */
    public void setEmail(String email){
        this.email = email;
    }

    /** @return Teléfono del cliente. */
    public String getTelefono(){
        return telefono;
    }

    /** @param telefono Teléfono a asignar. */
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "--- CLIENTE ---" + "\nCódigo: " + cod + "\nNombre: " + nombre + "\nDNI: " + dni + "\nEmail: " + email + "\nTeléfono: " + telefono;
    }
}

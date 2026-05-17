package ComprasProveedores.ENTIDADES;

/**
 * Clase Persona que sirve como base para unificar los atributos y métodos comunes 
 * de los diferentes tipos de personas o entidades en el sistema de Compras y Proveedores.
 * * @author María Herrero Rodríguez
 */

public class Persona{
    protected int codigo;
    protected String identificador;
    protected String nombre;
    protected String email;
    protected String telefono;
    protected String estado;

    /**
     * Constructor principal para la clase Persona.
     * * @param cod Código de la persona (Identificador único en el sistema).
     * @param identificador Identificador legal (DNI/CIF/NIF).
     * @param nombre Nombre completo.
     * @param email Correo electrónico.
     * @param telefono Teléfono de contacto.
     * @param estado Estado inicial.
     */
    public Persona(int cod, String identificador, String nombre, String email, String telefono, String estado) {
        this.codigo = cod;
        this.identificador = identificador;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado;
    }

    /** @param estado Nuevo estado a asignar. */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** @return El nombre de la persona. */
    public String getNombre(){
        return nombre;
    }
    
    /** @return El estado de la persona. */
    public String getEstado(){
        return estado;
    }

    /** @return El codigo de la persona. */
    public int getCodigo(){
        return codigo;
    }

    /** @return El email de la persona. */
    public String getEmail(){
        return email;
    }

    /** @return El telefono de la persona. */
    public String getTelefono(){
        return telefono;
    }

    /** @param nombre Nuevo nombre a asignar. */
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    /** @param codigo Nuevo codigo a asignar. */
    public void setCodigo(int codigo){
        this.codigo = codigo;
    }

    /** @param email Nuevo email a asignar. */
    public void setEmail(String email){
        this.email = email;
    }

    /** @param telefono Nuevo telefono a asignar. */
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

}
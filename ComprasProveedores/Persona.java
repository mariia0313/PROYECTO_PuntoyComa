package ComprasProveedores;

/* María Herrero Rodríguez
CLASE PERSONA, creada para unificar clases que comparten mismo atributos y métodos
*/

public class Persona{
    protected int codigo;
    protected String identificador;
    protected String nombre;
    protected String email;
    protected String telefono;


    public Persona(String identificador, String nombre, String email, String telefono) {
        this.identificador = identificador;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public String getNombre(){
        return nombre;
    }

    public int getCodigo(){
        return codigo;
    }

    public String getEmail(){
        return email;
    }

    public String getTelefono(){
        return telefono;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setCodigo(int codigo){
        this.codigo = codigo;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

}
/* María Herrero Rodríguez
CLASE PERSONA, creada para unificar clases que comparten mismo atributos y métodos
*/

public class Persona{
    public static int numempleados = 0;
    public static int numclientes = 0;
    public static int numproveedores = 0;
    protected int codigo;
    protected String nombre;
    protected String email;
    protected String telefono;

    public Persona() {
        codigo = 0;
        nombre = "No especificado";
        email = "No especificado";
        telefono = "";
    }

    public Persona(int codigo, String nombre, String email, String telefono) {
        this.codigo = codigo;
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

     public int getEmpleados(){
        return numempleados;
    }
    public int getClientes(){
        return numclientes;
    }
}
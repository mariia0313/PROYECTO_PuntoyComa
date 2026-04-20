package ComprasProveedores;

/* María Herrero Rodríguez
CLASE EMPLEADO
*/

public class Empleado extends Persona {

    public Empleado(String identificador, String nombre, String email, String telefono){
        super(identificador, nombre, email, telefono);
    }

    public String toString() {
        String rdo = "EMPLEADO CON CÓDIGO: " + super.codigo + "\n Nombre: " + super.nombre + "\nEmail: " + super.email + "\nTeléfono: " + telefono;
        return rdo;
    }

   
}
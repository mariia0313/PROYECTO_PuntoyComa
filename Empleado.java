public class Empleado extends Persona {
    public Empleado(){
        super();
        numempleados++;
    }

    public Empleado(int cod, String nombre, String email, long telefono){
        super(cod, nombre, email, telefono);
        numempleados++;
    }

    public String toString() {
        String rdo = "EMPLEADO CON CÓDIGO: " + super.codigo + "\n Nombre: " + super.nombre + "\nEmail: " + super.email + "\nTeléfono: " + telefono;
        return rdo;
    }

   
}
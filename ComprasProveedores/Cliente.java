package ComprasProveedores;

/* María Herrero Rodríguez
CLASE CLIENTE, arraylist de las reservas que ha hecho.
*/

import java.util.ArrayList;

public class Cliente extends Persona{
    
    private ArrayList<Reserva> reservas;

    public Cliente(String identificador, String nombre, String email, String telefono){
        super(identificador, nombre, email, telefono);
        this.reservas = new ArrayList<>();
    }

    public void addReserva(Reserva r){
        reservas.add(r);
    }

    public ArrayList<Reserva> getReservas(){
        return reservas;
    }

    public String toString() {
        String rdo = "CLIENTE CON CÓDIGO: " + super.codigo + "\n Nombre: " + super.nombre + "\nEmail: " + super.email + "\nTeléfono: " + telefono;
        if(reservas.size() == 0){
            rdo += "\nNo tiene reservas.";
        } else {
            rdo += "\n--- RESERVAS DEL CLIENTE ---\n";
            for(Reserva r : reservas){
                rdo += r + "\n";
            }
        }
        return rdo;
    }

    
}
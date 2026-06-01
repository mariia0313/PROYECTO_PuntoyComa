package ComprasProveedores.ENTIDAD;

/**
 * Representa a un cliente del sistema.
 * Mantiene un historial de todas las reservas que ha realizado.
 * * @author María Herrero Rodríguez
 * @see Persona
 */

import ComprasProveedores.ENTIDAD.Persona;
import ComprasProveedores.Reserva;
import java.util.ArrayList;

public class Cliente extends Persona{
    /** Histórico de reservas realizadas por el cliente. */
    private ArrayList<Reserva> reservas;

    /**
     * Constructor para la clase Cliente.
     * * @param cod Código de cliente.
     * @param identificador DNI/NIE.
     * @param nombre Nombre completo.
     * @param email Correo electrónico.
     * @param telefono Teléfono de contacto.
     * @param estado Estado del cliente en la plataforma.
     */
    public Cliente(int cod, String identificador, String nombre, String email, String telefono, String estado){
        super(cod, identificador, nombre, email, telefono, estado);
        this.reservas = new ArrayList<>();
    }

    /**
     * Vincula una nueva reserva al perfil del cliente.
     * @param r Objeto Reserva a añadir.
     */
    public void addReserva(Reserva r){
        reservas.add(r);
    }

    /** @return La lista completa de reservas del cliente. */
    public ArrayList<Reserva> getReservas(){
        return reservas;
    }

    /**
     * Devuelve una cadena con los datos del cliente.
     * Si tiene reservas, las añade al listado; si no, indica que no tiene.
     * * @return Ficha del cliente y sus reservas (si existen).
     */
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
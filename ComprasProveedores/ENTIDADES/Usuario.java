/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComprasProveedores.ENTIDADES;

/**
 * Clase que gestiona las credenciales de acceso al sistema para los empleados.
 * @author María Herrero Rodríguez
 */
public class Usuario {
    private int id;
    private String nombre;
    private String contra;
    /** Vínculo con el código del empleado asociado a este usuario. */
    private int empleado;
    
    /**
     * Constructor para crear un nuevo usuario de acceso.
     * * @param nombre Nombre de usuario (login).
     * @param contra Contraseña de acceso.
     * @param empleado Código del empleado vinculado.
     */
    public Usuario (String nombre, String contra, int empleado) {
        this.nombre = nombre;
        this.contra = contra;
        this.empleado = empleado;
    }

    /**
     * Asigna un ID único de base de datos al usuario tras su inserción.
     * @param id Identificador generado.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    public String toString() {
        return "    > CREDENCIALES ACCESO | Usuario: " + nombre + " | ID Sistema: " + id;
    }
    
}

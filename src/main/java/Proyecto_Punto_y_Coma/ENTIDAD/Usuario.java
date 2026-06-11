package Proyecto_Punto_y_Coma.ENTIDAD;

/**
 * Representa las credenciales de acceso de un empleado al sistema.
 * Cada usuario se vincula a un único empleado mediante su código.
 * @author María Herrero Rodríguez
 */
public class Usuario {
    private int id;
    private String nom_user;
    private String contrasenya;
    private int empleado;

    /**
     * Constructor para un usuario del sistema.
     * @param nom_user Nombre de inicio de sesión.
     * @param contrasenya Contraseña asociada.
     * @param empleado Código del empleado vinculado.
     */
    public Usuario(String nom_user, String contrasenya, int empleado) {
        this.nom_user = nom_user;
        this.contrasenya = contrasenya;
        this.empleado = empleado;
    }

    /** @param id Identificador único de usuario. */
    public void setId(int id) { this.id = id; }
    /** @return Nombre de usuario. */
    public String getNom_user() { return nom_user; }
    /** @return Identificador único del usuario. */
    public int getId() { return id; }
    /** @return Código del empleado asociado. */
    public int getEmpleado() { return empleado; }

    /**
     * Representación textual del usuario.
     * @return String con ID, nombre de usuario y empleado vinculado.
     */
    public String toString() {
        return String.format("ID: %d | Usuario: %s | Empleado: %d", id, nom_user, empleado);
    }
}

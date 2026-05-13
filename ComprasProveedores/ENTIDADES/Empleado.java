package ComprasProveedores.ENTIDAD;

import ComprasProveedores.ENTIDAD.Persona;
import java.util.Date;

/**
 * Representa a un empleado dentro de la organización, extendiendo la información básica de Persona
 * con datos contractuales, profesionales y fechas de relevancia laboral.
 * * @author María Herrero Rodríguez
 * @see Persona
 */

public class Empleado extends Persona {
    private String cargo;
    private String nuss;
    private Date fecha_nac;
    private String contrato;
    private Date fecha_antig;
    private Date fecha_desp;
    
    /**
     * Constructor detallado para la creación de un objeto Empleado.
     * @param cod Código del empleado.
     * @param identificador DNI/NIE.
     * @param nombre Nombre y apellidos.
     * @param email Email corporativo.
     * @param telefono Teléfono móvil.
     * @param cargo Puesto que desempeña.
     * @param contrato Tipo de contrato.
     * @param fecha_nac Fecha de nacimiento.
     * @param fecha_antig Fecha de alta en la empresa.
     * @param fecha_desp Fecha de cese (si aplica).
     * @param estado Estado administrativo (Activo/Baja).
     */
    public Empleado(int cod, String identificador, String nombre, String email, String telefono, String cargo, String contrato, Date fecha_nac, Date fecha_antig, Date fecha_desp, String estado ) {
        super(cod, identificador, nombre, email, telefono, estado);
        this.cargo = cargo;
        this.contrato = contrato;
        this.fecha_nac = fecha_nac;
        this.fecha_antig = fecha_antig;
        this.fecha_desp = fecha_desp;
    }

    /** @return Cargo o puesto del empleado. */
    public String getCargo() {
        return cargo;
    }

    /** @param cargo cargo a asignar. */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /** @return Nuss del empleado. */
    public String getNuss() {
        return nuss;
    }

    /** @param nuss Número de la Seguridad Social a asignar. */
    public void setNuss(String nuss) {
        this.nuss = nuss;
    }

    /** @return Fecha de nacimiento del empleado. */
    public Date getFecha_nac() {
        return fecha_nac;
    }

    /** @param fecha_nac fecha de nacimiento a asignar. */
    public void setFecha_nac(Date fecha_nac) {
        this.fecha_nac = fecha_nac;
    }

    /** @return Contrato del empleado. */
    public String getContrato() {
        return contrato;
    }

    /** @param contrato contrato a asignar. */
    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    /** @return Fecha de antiguedad del empleado. */
    public Date getFecha_antig() {
        return fecha_antig;
    }

    /** @param fecha_antig fecha de antiguedad a asignar. */
    public void setFecha_antig(Date fecha_antig) {
        this.fecha_antig = fecha_antig;
    }

    /** @return Fecha de despido del empleado. */
    public Date getFecha_desp() {
        return fecha_desp;
    }

    /** @param fecha_desp fecha_desp a asignar. */
    public void setFecha_desp(Date fecha_desp) {
        this.fecha_desp = fecha_desp;
    }
   
/**
     * Genera una representación en texto de los datos básicos del empleado.
     * @return String con código, nombre, email, teléfono y cargo.
     */
    public String toString() {

        return "----------------------------------------------------\n"
                + "  FICHA DE EMPLEADO [ ID: " + codigo + " ]\n"
                + "----------------------------------------------------\n"
                + "  Nombre:       " + nombre + "\n"
                + "  Cargo:        " + cargo + "\n"
                + "  DNI/NIE:      " + identificador + "\n"
                + "  Contrato:     " + contrato + "\n"
                + "  Alta:         " + (fecha_antig != null ? fecha_antig : "---") + "\n"
                + "  Email:        " + email + "\n"
                + "  Estado:       " + estado + "\n"
                + "----------------------------------------------------";
    }

   
}
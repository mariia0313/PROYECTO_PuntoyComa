package Proyecto_Punto_y_Coma.ENTIDAD;

import Proyecto_Punto_y_Coma.ENTIDAD.Persona;
import java.util.Date;

/**
 * Representa a un empleado del hotel con información laboral y personal.
 * Extiende de Persona añadiendo datos específicos como cargo, NSS,
 * tipo de contrato y fechas relevantes.
 * @author María Herrero Rodríguez
 */
public class Empleado extends Persona {
    private String cargo;
    private String nss;
    private Date fecha_nacimiento;
    private String tipo_contrato;
    private Date fecha_antiguedad;
    private Date fecha_despido;

    /**
     * Constructor completo para Empleado.
     * @param cod Código único del empleado en el sistema.
     * @param identificador DNI/NIF del empleado.
     * @param nombre Nombre completo.
     * @param email Correo electrónico.
     * @param telefono Teléfono de contacto.
     * @param cargo Cargo que ocupa en el hotel.
     * @param nss Número de la Seguridad Social.
     * @param tipo_contrato Tipo de contrato (Indefinido, Temporal, etc.).
     * @param fecha_nacimiento Fecha de nacimiento.
     * @param fecha_antiguedad Fecha de antigüedad en la empresa.
     * @param fecha_despido Fecha de despido o baja (puede ser null).
     * @param estado Estado actual (Activo, Inactivo, etc.).
     */
    public Empleado(int cod, String identificador, String nombre, String email, String telefono, String cargo, String nss, String tipo_contrato, Date fecha_nacimiento, Date fecha_antiguedad, Date fecha_despido, String estado) {
        super(cod, identificador, nombre, email, telefono, estado);
        this.cargo = cargo;
        this.nss = nss;
        this.tipo_contrato = tipo_contrato;
        this.fecha_nacimiento = fecha_nacimiento;
        this.fecha_antiguedad = fecha_antiguedad;
        this.fecha_despido = fecha_despido;
    }

    /** @return El cargo del empleado. */
    public String getCargo() { return cargo; }
    /** @param cargo Nuevo cargo a asignar. */
    public void setCargo(String cargo) { this.cargo = cargo; }

    /** @return El Número de la Seguridad Social. */
    public String getNss() { return nss; }
    /** @param nss Nuevo NSS a asignar. */
    public void setNss(String nss) { this.nss = nss; }

    /** @return La fecha de nacimiento. */
    public Date getFecha_nacimiento() { return fecha_nacimiento; }
    /** @param fecha_nacimiento Nueva fecha de nacimiento. */
    public void setFecha_nacimiento(Date fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }

    /** @return El tipo de contrato. */
    public String getTipo_contrato() { return tipo_contrato; }
    /** @param tipo_contrato Nuevo tipo de contrato. */
    public void setTipo_contrato(String tipo_contrato) { this.tipo_contrato = tipo_contrato; }

    /** @return La fecha de antigüedad en la empresa. */
    public Date getFecha_antiguedad() { return fecha_antiguedad; }
    /** @param fecha_antiguedad Nueva fecha de antigüedad. */
    public void setFecha_antiguedad(Date fecha_antiguedad) { this.fecha_antiguedad = fecha_antiguedad; }

    /** @return La fecha de despido (puede ser null). */
    public Date getFecha_despido() { return fecha_despido; }
    /** @param fecha_despido Nueva fecha de despido. */
    public void setFecha_despido(Date fecha_despido) { this.fecha_despido = fecha_despido; }

    /**
     * Devuelve una representación formateada del empleado con todos sus datos.
     * @return String multilínea con la información del empleado.
     */
    public String toString() {
        String desp = (fecha_despido != null) ? String.format("%td/%<tm/%<tY", fecha_despido) : "---";
        return String.format(
                "Código: %d | Identificador: %s | Nombre: %s%n" +
                "Cargo: %s | NSS: %s | Contrato: %s%n" +
                "Email: %s | Teléfono: %s | Estado: %s%n" +
                "F.Nacimiento: %s | F.Antigüedad: %s | F.Despido: %s%n",
                codigo, identificador, nombre,
                cargo, nss, tipo_contrato,
                email, telefono, estado,
                (fecha_nacimiento != null ? String.format("%td/%<tm/%<tY", fecha_nacimiento) : "---"),
                (fecha_antiguedad != null ? String.format("%td/%<tm/%<tY", fecha_antiguedad) : "---"),
                desp
        );
    }
}

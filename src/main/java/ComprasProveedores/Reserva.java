package ComprasProveedores;

public abstract class Reserva {

    protected static int cont = 1;
    protected int cod;
    protected double precioBase;
    protected String estado;

    public Reserva(double p) {
        this.cod = cont;
        this.precioBase = p;
        this.estado = "Alta";
        cont++;
    }

    public abstract double precioIVA();

    public double total() {
        return this.precioBase + precioIVA();
    }

    public int getCod() {
        return this.cod;
    }
    
    public void setPrecioBase(double p) {
        this.precioBase = p;
        this.estado = "Modificado";
    }

    public double getPrecioBase() {
        return this.precioBase;
    }

    public void cancelar() {
        this.estado = "Baja";
    }

    @Override
    public String toString() {
        return "Reserva: \n" + "CodReserva: " + this.cod + "\nPrecioBase: " + this.precioBase + "\nEstado: " + this.estado;
    }
}
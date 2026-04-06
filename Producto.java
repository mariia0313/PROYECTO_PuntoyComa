public class Producto{
    private int cod;
    private String nombre;
    private String descrip;
    private int stock;
    private int stock_min;

    public Producto() {
        cod = contador;
        nombre = "No especificado";
        descrip = "No especificado";
        stock = 0;
        stock_min = 0;
    }

    public Producto(String n, String d, int s, int sm) {
        cod = contador;
        nombre = n;
        descrip = d;
        stock = s;
        stock_min = sm;
    }

    public void setCod(int c){
        cod = c;
    }
    public void setNombre(String n){
        nombre = n;
    }

    public String getNombre(){
        return nombre;
    }

    public int getCOD(){
        return cod;
    }

    public void setDesc(String d){
        descrip = d;
    }
    public void setStock(int s){
        stock = s;
    }
    public boolean setStockMin(int sm){
        boolean pasa = true;
        if (sm <= 0) {
            pasa = false;
        }
        stock_min = sm;
        return pasa;
    }

    public String toString() {
        return "Código producto: " + cod + "\nNombre producto: " + nombre + "\nDescripción producto: " + descrip + "\nStock actual del producto: " + stock + "\nStock mínimo definido: " + stock_min;
    }
}
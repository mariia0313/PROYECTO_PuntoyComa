package ComprasProveedores.ENTIDAD;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class PedidoTest {

    @Test
    void testNominal() {
        LineaCompra lc1 = new LineaCompra(1, "Leche", 5, 10);
        LineaCompra lc2 = new LineaCompra(2, "Chocolate", 3.5, 15);
        OrdenCompra od = new OrdenCompra(1, "Avenida de las palomas", LocalDate.now(), null, null);
        od.addLinea(lc1);
        od.addLinea(lc2);
        double precio_final_lc1 = lc1.getPrecioUnidad() * lc1.getCantidad();
        double precio_final_lc2 = lc2.getPrecioUnidad() * lc2.getCantidad();
        double precio_final_pedido = precio_final_lc1 + precio_final_lc2;
        assertEquals(precio_final_lc1, lc1.getPrecioTotal(), "El precio guardado en el objeto es " + lc1.getPrecioTotal());
        assertEquals(precio_final_lc2, lc2.getPrecioTotal(), "El precio guardado en el objeto es " + lc2.getPrecioTotal());
        assertEquals(precio_final_pedido, od.getPrecioTotal(), "El precio guardado en el objeto es " + od.getPrecioTotal()) ;


    }
 
    @Test
    void testLimite() {
        LineaCompra lc = new LineaCompra(1, null, 1, Integer.MAX_VALUE);
        double precio_final = lc.getCantidad() * lc.getPrecioUnidad();
        assertEquals(precio_final, lc.getPrecioTotal(), "El precio guardado en el objeto es " + lc.getPrecioTotal());
    }

    @Test
    void testErroneo() {
        OrdenCompra orden = new OrdenCompra(1, "Dir", LocalDate.now(), "Tel", null);
        assertThrows(NullPointerException.class, () -> orden.addLinea(null));
    }

    void testErroneoTiraError() {
        OrdenCompra orden = new OrdenCompra(1, "Dir", LocalDate.now(), "Tel", null);
        LineaCompra lc = new LineaCompra(1, null, 1, Integer.MAX_VALUE);
        orden.addLinea(lc);
        assertThrows(NullPointerException.class, () -> orden.addLinea(null));
    }
}

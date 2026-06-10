package Proyecto_Punto_y_Coma.ENTIDAD;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Pruebas unitarias para las clases OrdenCompra y LineaCompra.
 * Cubre casos nominales, límite y erróneos.
 * @author María Herrero Rodríguez
 */
public class PedidoTest {

    /** Crea una orden con dos líneas y verifica todos los atributos. */
    @Test
    void testNominal() {
        LineaCompra lc1 = new LineaCompra(1, 1, 10, 1);
        LineaCompra lc2 = new LineaCompra(1, 2, 15, 2);
        OrdenCompra od = new OrdenCompra(1, "Avenida de las palomas", LocalDate.now(), null, null, 1, "Pendiente");
        od.addLinea(lc1);
        od.addLinea(lc2);
        assertEquals(1, lc1.getNo_compra());
        assertEquals(1, lc1.getNo_linea());
        assertEquals(10, lc1.getCantidad());
        assertEquals(1, lc1.getProducto());
        assertEquals(2, lc2.getNo_linea());
        assertEquals(15, lc2.getCantidad());
        assertEquals(2, lc2.getProducto());
        assertEquals(2, od.getLineas().size());
    }

    /** Verifica que se acepta el valor máximo de cantidad en una línea. */
    @Test
    void testLimite() {
        LineaCompra lc = new LineaCompra(1, 1, Integer.MAX_VALUE, 1);
        assertEquals(Integer.MAX_VALUE, lc.getCantidad());
        assertEquals(1, lc.getNo_linea());
    }

    /** Verifica que añadir una línea nula lanza NullPointerException. */
    @Test
    void testErroneo() {
        OrdenCompra orden = new OrdenCompra(1, "Dir", LocalDate.now(), "Tel", null, 1, "Pendiente");
        assertThrows(NullPointerException.class, () -> orden.addLinea(null));
    }

    /** Inserta una línea válida y luego una nula para confirmar que se lanza la excepción. */
    @Test
    void testErroneoTiraError() {
        OrdenCompra orden = new OrdenCompra(1, "Dir", LocalDate.now(), "Tel", null, 1, "Pendiente");
        LineaCompra lc = new LineaCompra(1, 1, Integer.MAX_VALUE, 1);
        orden.addLinea(lc);
        assertThrows(NullPointerException.class, () -> orden.addLinea(null));
    }
}

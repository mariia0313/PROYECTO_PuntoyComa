package Proyecto_Punto_y_Coma.ENTIDAD;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ReservaTest {

    @Test
    void testNominal() {
        Cliente cliente = new Cliente(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456", "Activo");
        TipoReserva tipo = new Alojamiento(1, "Habitación 1", 50.0, 0.1, 2, "Habitación doble", "Disponible");

        int anioInicio = 2026;
        int mesInicio = 6;
        int diaInicio = 1;

        int anioFin = 2026;
        int mesFin = 6;
        int diaFin = 4;

        java.sql.Date inicio = new java.sql.Date(anioInicio - 1900, mesInicio - 1, diaInicio);
        java.sql.Date fin = new java.sql.Date(anioFin - 1900, mesFin - 1, diaFin);
        
        Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin, "Alojamiento");
        double precioCalculado = reserva.calcularPrecioTotal();

        double precioEsperado = 165.0;
        System.out.println(precioCalculado+"----------");
        assertEquals(precioEsperado, precioCalculado, 0.001, "El cálculo del precio nominal para 3 días es incorrecto");
    }

    @Test
    void testLimite() {
        Cliente cliente = new Cliente(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456", "Activo");
        TipoReserva tipo = new Alojamiento(1, "Suite Premium", 100.0, 0.21, 2, "Suite", "Disponible");

        int anio = 2026;
        int mes = 6;
        int dia = 1;

        Date inicio = new Date(anio - 1900, mes - 1, dia);
        Date fin = new Date(anio - 1900, mes - 1, dia);

        Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin, "Alojamiento");

        double precioCalculado = reserva.calcularPrecioTotal();

        double precioEsperado = tipo.total();

        assertEquals(precioEsperado, precioCalculado, 0.001,"Una reserva de 0 días debería retornar un precio total de 0.0");
    }

    @Test
    void testErroneo() {
        Cliente cliente = new Cliente(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456", "Activo");

        TipoReserva tipo = new Alojamiento(1, "Habitación Doble", 50.0, 0.10,2, "Habitación doble", "Disponible");

        Date inicio = new Date(2026 - 1900, 6 - 1, 10);
        Date fin = new Date(2026 - 1900, 6 - 1, 5);

        Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin, "Alojamiento");

        assertThrows(IllegalArgumentException.class,() -> reserva.calcularPrecioTotal(),"Se esperaba una excepción al introducir fechas invertidas");
    }

    @Test
    void testErroneo2() {
        Cliente cliente = new Cliente(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456", "Activo");

        Date inicio = new Date(2026 - 1900, 6 - 1, 1);
        Date fin = new Date(2026 - 1900, 6 - 1, 4);

        Reserva reserva = new Reserva(1, cliente, null, inicio, fin, "Alojamiento");

        assertThrows(NullPointerException.class, () -> {
            reserva.calcularPrecioTotal();
        }, "Se esperaba un NullPointerException debido a que el TipoReserva es nulo");
    }

    @Test
    void testSeSolapa() {
        Cliente cliente = new Cliente(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456", "Activo");

        TipoReserva tipo = new Alojamiento(1, "Habitación Doble", 50.0, 0.10, 2, "Habitación doble", "Disponible");

        Date inicio = new Date(2026 - 1900, 6 - 1, 1);
        Date fin = new Date(2026 - 1900, 6 - 1, 5);

        Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin, "Alojamiento");

        Date otraInicio = new Date(2026 - 1900, 6 - 1, 3);
        Date otraFin = new Date(2026 - 1900, 6 - 1, 7);

        assertTrue(reserva.seSolapa(otraInicio, otraFin), "Las reservas deberían solaparse");
    }

    @Test
    void testCancelar() {
        Cliente cliente = new Cliente(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456", "Activo");

        TipoReserva tipo = new Alojamiento(1, "Habitación Doble", 50.0, 0.10, 2, "Habitación doble", "Disponible");

        Date inicio = new Date(2026 - 1900, 6 - 1, 1);
        Date fin = new Date(2026 - 1900, 6 - 1, 5);

        Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin, "Alojamiento");

        reserva.cancelar();

        assertEquals("Baja", reserva.getEstado(), "El estado de la reserva debería pasar a Baja");
    }
}
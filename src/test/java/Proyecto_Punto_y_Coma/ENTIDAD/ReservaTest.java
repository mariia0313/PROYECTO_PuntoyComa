// package Proyecto_Punto_y_Coma.ENTIDAD;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

// import java.sql.Date;

// public class ReservaTest {

//     @Test
//     void testNominal() {
//         Cliente2 cliente = new Cliente2(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456");
//         TipoReserva tipo = new Alojamiento(1, "Habitación 1", 50.0, 0.1, 2, "Habitación doble");

//         int anioInicio = 2026;
//         int mesInicio = 6;
//         int diaInicio = 1;

//         int anioFin = 2026;
//         int mesFin = 6;
//         int diaFin = 4;

//         Date inicio = new Date(anioInicio - 1900, mesInicio - 1, diaInicio);
//         Date fin = new Date(anioFin - 1900, mesFin - 1, diaFin);
        
//         Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin);

//         // 2. Ejecución del método a probar
//         double precioCalculado = reserva.calcularPrecioTotal();

//         double precioEsperado = 165.0;
//         System.out.println(precioCalculado+"----------");
//         assertEquals(precioEsperado, precioCalculado, 0.001, "El cálculo del precio nominal para 3 días es incorrecto");
//     }

//     @Test
//     void testLimite() {
//         Cliente2 cliente = new Cliente2(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456");
//         TipoReserva tipo = new Alojamiento(1, "Suite Premium", 100.0, 0.21, 2, "Suite");

//         int anio = 2026;
//         int mes = 6;
//         int dia = 1;

//         Date inicio = new Date(anio - 1900, mes - 1, dia);
//         Date fin = new Date(anio - 1900, mes - 1, dia);

//         Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin);

//         double precioCalculado = reserva.calcularPrecioTotal();

//         double precioEsperado = tipo.total();

//         assertEquals(precioEsperado, precioCalculado, 0.001,"Una reserva de 0 días debería retornar un precio total de 0.0");
//     }

//     @Test
//     void testErroneo() {
//         Cliente2 cliente = new Cliente2(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456");

//         TipoReserva tipo = new Alojamiento(1, "Habitación Doble", 50.0, 0.10,2, "Habitación doble");

//         Date inicio = new Date(2026 - 1900, 6 - 1, 10);
//         Date fin = new Date(2026 - 1900, 6 - 1, 5);

//         Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin);

//         assertThrows(IllegalArgumentException.class,() -> reserva.calcularPrecioTotal(),"Se esperaba una excepción al introducir fechas invertidas");
//     }

//     @Test
//     void testErroneo2() {
//         Cliente2 cliente = new Cliente2(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456");

//         Date inicio = new Date(2026 - 1900, 6 - 1, 1);
//         Date fin = new Date(2026 - 1900, 6 - 1, 4);

//         Reserva reserva = new Reserva(1, cliente, null, inicio, fin);

//         assertThrows(NullPointerException.class, () -> {
//             reserva.calcularPrecioTotal();
//         }, "Se esperaba un NullPointerException debido a que el TipoReserva es nulo");
//     }

//     @Test
//     void testSeSolapa() {
//         Cliente2 cliente = new Cliente2(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456");

//         TipoReserva tipo = new Alojamiento(1, "Habitación Doble", 50.0, 0.10, 2, "Habitación doble");

//         Date inicio = new Date(2026 - 1900, 6 - 1, 1);
//         Date fin = new Date(2026 - 1900, 6 - 1, 5);

//         Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin);

//         Date otraInicio = new Date(2026 - 1900, 6 - 1, 3);
//         Date otraFin = new Date(2026 - 1900, 6 - 1, 7);

//         assertTrue(reserva.seSolapa(otraInicio, otraFin), "Las reservas deberían solaparse");
//     }

//     @Test
//     void testCancelar() {
//         Cliente2 cliente = new Cliente2(1, "Juan Pérez", "12345678A", "ejemplo@gmail.com", "600123456");

//         TipoReserva tipo = new Alojamiento(1, "Habitación Doble", 50.0, 0.10, 2, "Habitación doble");

//         Date inicio = new Date(2026 - 1900, 6 - 1, 1);
//         Date fin = new Date(2026 - 1900, 6 - 1, 5);

//         Reserva reserva = new Reserva(1, cliente, tipo, inicio, fin);

//         reserva.cancelar();

//         assertEquals("Baja", reserva.getEstado(), "El estado de la reserva debería pasar a Baja");
//     }
// }
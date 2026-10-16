package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;
import com.example.service.ReservationServiceImpl;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ReservationLockingTest {

    private ReservationService service;

    @Before
    public void setUp() {
        service = new ReservationServiceImpl();
    }

    @Test
    public void testOptimisticLockExceptionOnConcurrentUpdate() {
        Reservation reservation = new Reservation("Salle Réunion 1", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2));
        reservation = service.creerReservation(reservation);

        Reservation tx1 = service.trouverParId(reservation.getId());
        Reservation tx2 = service.trouverParId(reservation.getId());

        assertEquals(0, tx1.getVersion());
        assertEquals(0, tx2.getVersion());

        // Tx1 modifie la réservation
        tx1.setSalle("Salle Conférence A");
        Reservation updated1 = service.modifierReservation(tx1);
        assertEquals(1, updated1.getVersion());

        // Tx2 tente de modifier avec sa version 0 obsolète -> OptimisticLockException attendue
        tx2.setSalle("Salle Conférence B");
        try {
            service.modifierReservation(tx2);
            fail("Une OptimisticLockException aurait dû être levée");
        } catch (OptimisticLockException e) {
            assertTrue(e.getMessage().contains("Row was updated or deleted by another transaction"));
        }
    }

    @Test
    public void testRetryHandlerResolvesConflict() {
        ReservationRetryHandler retryHandler = new ReservationRetryHandler(service);

        Reservation reservation = new Reservation("Salle A", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2));
        reservation = service.creerReservation(reservation);

        Reservation tx1 = service.trouverParId(reservation.getId());
        Reservation tx2 = service.trouverParId(reservation.getId());

        tx1.setSalle("Salle B");
        service.modifierReservation(tx1);

        tx2.setSalle("Salle C");
        Reservation updated2 = retryHandler.modifierAvecRetry(tx2);

        assertNotNull(updated2);
        assertEquals("Salle C", updated2.getSalle());
        assertEquals(2, updated2.getVersion());
    }
}

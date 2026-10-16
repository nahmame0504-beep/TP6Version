package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;
import javax.persistence.OptimisticLockException;

public class ReservationRetryHandler {

    private static final int MAX_RETRIES = 3;
    private ReservationService service;

    public ReservationRetryHandler(ReservationService service) {
        this.service = service;
    }

    public Reservation modifierAvecRetry(Reservation reservation) {
        int tentative = 0;
        String nouvelleSalle = reservation.getSalle();
        java.time.LocalDateTime dateDebut = reservation.getDateDebut();
        java.time.LocalDateTime dateFin = reservation.getDateFin();

        while (tentative < MAX_RETRIES) {
            try {
                return service.modifierReservation(reservation);
            } catch (OptimisticLockException e) {
                tentative++;
                System.out.println("Conflit détecté, tentative de retry " + tentative);
                // Recharger la réservation pour obtenir la dernière version
                reservation = service.trouverParId(reservation.getId());
                // Appliquer les modifications nécessaires (réappliquer les changements voulus)
                reservation.setSalle(nouvelleSalle);
                reservation.setDateDebut(dateDebut);
                reservation.setDateFin(dateFin);
            }
        }
        throw new OptimisticLockException("Échec après " + MAX_RETRIES + " tentatives");
    }
}

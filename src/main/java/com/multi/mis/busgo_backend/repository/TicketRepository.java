package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByPassengerEmail(String passengerEmail);
    List<Ticket> findByBookingId(String bookingId);
    // Add more query methods as needed
}

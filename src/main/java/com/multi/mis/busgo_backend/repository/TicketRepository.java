package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.dto.TicketDTO;
import com.multi.mis.busgo_backend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.repository.query.Param;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByPassengerEmail(String passengerEmail);
    List<Ticket> findByBookingId(Long bookingId);
    @Query("select new com.multi.mis.busgo_backend.dto.TicketDTO(u.id, u.bookingId, u.passengerName, u.origin, u.destination, u.departureDate, u.seatNumbers, u.status, u.price) " +
            "from Ticket u join BusBooking h on u.bookingId = h.bookingId join BusSchedule v on h.schedule = v " +
            "where v.company.companyId = :companyId")
    List<TicketDTO> findByCompanyId(@Param("companyId") Long companyId);
    // Add more query methods as needed
}

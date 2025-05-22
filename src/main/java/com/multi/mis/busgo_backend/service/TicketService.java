package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.dto.TicketDTO;
import com.multi.mis.busgo_backend.model.Ticket;
import com.multi.mis.busgo_backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    /**
     * Get all tickets with optional filtering
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Get tickets with filtering options
     */
    public List<Ticket> getFilteredTickets(String status, String routeId, Date date, 
                                         String searchTerm, String companyId, 
                                         Integer page, Integer size) {
        // This is a placeholder for more complex filtering logic
        // In a real implementation, you would use a more sophisticated query mechanism
        List<Ticket> allTickets = ticketRepository.findAll();
        
        // Apply filters
        if (status != null && !status.isEmpty()) {
            allTickets = allTickets.stream()
                .filter(ticket -> status.equals(ticket.getStatus()))
                .collect(Collectors.toList());
        }
        
        // More filters could be applied here
        
        // Apply pagination
        if (page != null && size != null) {
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, allTickets.size());
            
            if (fromIndex < allTickets.size()) {
                return allTickets.subList(fromIndex, toIndex);
            } else {
                return List.of();
            }
        }
        
        return allTickets;
    }
    /**
     * Get ticket by companyId
     *
     */

    public List<TicketDTO> getTicketByCompanyId(Long companyId) {
        return ticketRepository.findByCompanyId(companyId);
    }
    /**
     * Get ticket by ID
     */
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    /**
     * Save a ticket
     */
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    /**
     * Delete a ticket
     */
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    /**
     * Get tickets by passenger email
     */
    public List<Ticket> getTicketsByPassengerEmail(String email) {
        return ticketRepository.findByPassengerEmail(email);
    }

    /**
     * Get tickets by booking ID
     */
    public List<Ticket> getTicketsByBookingId(Long bookingId) {
        return ticketRepository.findByBookingId(bookingId);
    }
    
    /**
     * Get ticket statistics for a company
     */
    public Map<String, Object> getTicketStats(String companyId) {
        // This is a placeholder for actual statistics calculation
        // In a real implementation, you would query the database for these metrics
        Map<String, Object> stats = new HashMap<>();
        
        List<Ticket> allTickets = ticketRepository.findAll();
        
        // Count tickets by status
        long confirmedCount = allTickets.stream()
                .filter(t -> "CONFIRMED".equals(t.getStatus()))
                .count();
        
        long pendingCount = allTickets.stream()
                .filter(t -> "PENDING".equals(t.getStatus()))
                .count();
        
        long cancelledCount = allTickets.stream()
                .filter(t -> "CANCELLED".equals(t.getStatus()))
                .count();
        
        long completedCount = allTickets.stream()
                .filter(t -> "COMPLETED".equals(t.getStatus()))
                .count();
        
        // Calculate total revenue (only from confirmed and completed tickets)
        double totalRevenue = allTickets.stream()
                .filter(t -> "CONFIRMED".equals(t.getStatus()) || "COMPLETED".equals(t.getStatus()))
                .mapToDouble(Ticket::getPrice)
                .sum();
        
        stats.put("totalTickets", allTickets.size());
        stats.put("confirmedTickets", confirmedCount);
        stats.put("pendingTickets", pendingCount);
        stats.put("cancelledTickets", cancelledCount);
        stats.put("completedTickets", completedCount);
        stats.put("totalRevenue", totalRevenue);
        
        return stats;
    }
}

package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.Ticket;
import com.multi.mis.busgo_backend.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // Get all tickets with optional filters
    @GetMapping
    public ResponseEntity<?> getTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String routeId,
            @RequestParam(required = false) Date date,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        
        try {
            List<Ticket> tickets = ticketService.getFilteredTickets(status, routeId, date, searchTerm, companyId, page, size);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tickets: " + e.getMessage());
        }
    }

    // Get company tickets
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getCompanyTickets(
            @PathVariable String companyId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String routeId,
            @RequestParam(required = false) Date date,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        
        try {
            if (companyId == null || companyId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Company ID is required");
            }
            List<Ticket> tickets = ticketService.getFilteredTickets(status, routeId, date, searchTerm, companyId, page, size);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving company tickets: " + e.getMessage());
        }
    }

    // Get user tickets
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getUserTickets(@PathVariable String userId) {
        // Assuming user email is stored in the userId parameter
        return ResponseEntity.ok(ticketService.getTicketsByPassengerEmail(userId));
    }

    // Get ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update ticket status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        
        return ticketService.getTicketById(id)
                .map(ticket -> {
                    ticket.setStatus(statusUpdate.get("status"));
                    return ResponseEntity.ok(ticketService.saveTicket(ticket));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Update check-in status
    @PatchMapping("/{id}/check-in")
    public ResponseEntity<Ticket> updateCheckInStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> checkInUpdate) {
        
        return ticketService.getTicketById(id)
                .map(ticket -> {
                    boolean isCheckedIn = checkInUpdate.get("isCheckedIn");
                    ticket.setCheckInStatus(isCheckedIn ? "CHECKED_IN" : "NOT_CHECKED_IN");
                    if (isCheckedIn) {
                        ticket.setCheckInTime(new Date());
                    }
                    return ResponseEntity.ok(ticketService.saveTicket(ticket));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Update ticket notes
    @PatchMapping("/{id}/notes")
    public ResponseEntity<Ticket> updateTicketNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> notesUpdate) {
        
        return ticketService.getTicketById(id)
                .map(ticket -> {
                    ticket.setNotes(notesUpdate.get("notes"));
                    return ResponseEntity.ok(ticketService.saveTicket(ticket));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Cancel ticket
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Ticket> cancelTicket(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> cancelRequest) {
        
        return ticketService.getTicketById(id)
                .map(ticket -> {
                    ticket.setStatus("CANCELLED");
                    ticket.setPaymentStatus("REFUNDED");
                    if (cancelRequest != null && cancelRequest.containsKey("reason")) {
                        String notes = ticket.getNotes() != null ? ticket.getNotes() : "";
                        notes += "\nCancellation reason: " + cancelRequest.get("reason");
                        ticket.setNotes(notes.trim());
                    }
                    return ResponseEntity.ok(ticketService.saveTicket(ticket));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get ticket statistics for a company
    @GetMapping("/company/{companyId}/stats")
    public ResponseEntity<Map<String, Object>> getTicketStats(@PathVariable String companyId) {
        return ResponseEntity.ok(ticketService.getTicketStats(companyId));
    }

    // Create a new ticket
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        ticket.setCreatedAt(new Date());
        if (ticket.getStatus() == null) {
            ticket.setStatus("PENDING");
        }
        if (ticket.getPaymentStatus() == null) {
            ticket.setPaymentStatus("PENDING");
        }
        if (ticket.getCheckInStatus() == null) {
            ticket.setCheckInStatus("NOT_CHECKED_IN");
        }
        
        Ticket savedTicket = ticketService.saveTicket(ticket);
        return new ResponseEntity<>(savedTicket, HttpStatus.CREATED);
    }

    // Delete a ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        if (!ticketService.getTicketById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
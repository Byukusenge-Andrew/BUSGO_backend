package com.multi.mis.busgo_backend.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Setter
@Getter
public class TicketDTO {
    private String id;
    private String bookingId;
    private String customerName;
    private String route;
    private Date date;
    private int seats;
    private String status;
    // Getter and setter for price
    private Double price; // Added for stats

    public TicketDTO(Long id, Long bookingId, String passengerName, String origin, String destination, Date departureDate, List<String> seatNumbers, String status, Double price) {
        this.id = id != null ? id.toString() : null;
        this.bookingId = bookingId != null ? bookingId.toString() : null;
        this.customerName = passengerName;
        this.route = origin != null && destination != null ? origin + " - " + destination : null;
        this.date = departureDate;
        this.seats = seatNumbers != null ? seatNumbers.size() : 0;
        this.status = status;
        this.price = price;
    }

    // Other getters and setters unchanged

}
package com.multi.mis.busgo_backend.dto;
import java.util.Date;
import java.util.List;
public class TicketDTO {
    private String id;
    private String bookingId;
    private String customerName;
    private String route;
    private Date date;
    private int seats;
    private String status;
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

    // Getter and setter for price
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    // Other getters and setters unchanged

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
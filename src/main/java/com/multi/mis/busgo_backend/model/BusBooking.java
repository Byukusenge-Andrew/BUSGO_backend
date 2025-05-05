package com.multi.mis.busgo_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bus_bookings")
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON
public class BusBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private BusSchedule schedule;

    private Date bookingDate;
    private String status; // Confirmed, Cancelled, Pending
    private Integer numberOfSeats;
    private Double totalFare;
    private String seatNumbers;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;

    // Default constructor
    public BusBooking() {
    }

    // Constructor with fields
    public BusBooking(User user, BusSchedule schedule, Date bookingDate, String status,
                      Integer numberOfSeats, Double totalFare, String seatNumbers,
                      String paymentMethod, String paymentStatus, String transactionId) {
        this.user = user;
        this.schedule = schedule;
        this.bookingDate = bookingDate;
        this.status = status;
        this.numberOfSeats = numberOfSeats;
        this.totalFare = totalFare;
        this.seatNumbers = seatNumbers;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getId() {
        return bookingId;
    }

    public void setId(Long id) {
        this.bookingId = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BusSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(BusSchedule schedule) {
        this.schedule = schedule;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    @JsonIgnore // Prevent serialization of getCompany() to avoid NullPointerException
    public BusCompany getCompany() {
        return this.schedule != null ? this.schedule.getCompany() : null;
    }

    @JsonIgnore // Prevent serialization of GetCompanyId()
    public Long GetCompanyId() {
        return this.schedule != null && this.schedule.getCompany() != null ? this.schedule.getCompany().getId() : null;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public String getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(String seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
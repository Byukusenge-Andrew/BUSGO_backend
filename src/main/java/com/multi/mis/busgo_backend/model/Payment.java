package com.multi.mis.busgo_backend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private BusBooking booking;
    
    private double amount;
    private String paymentMethod; // CASH, CREDIT_CARD, DEBIT_CARD, UPI, etc.
    private String transactionId;
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED
    private Date paymentDate;
    private String paymentDetails; // Additional payment details in JSON format
    
    // Default constructor
    public Payment() {
    }
    
    // Constructor with fields
    public Payment(BusBooking booking, double amount, String paymentMethod,
                  String transactionId, String status, Date paymentDate,
                  String paymentDetails) {
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.paymentDate = paymentDate;
        this.paymentDetails = paymentDetails;
    }
    
    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public BusBooking getBooking() {
        return booking;
    }

    public void setBooking(BusBooking booking) {
        this.booking = booking;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
} 
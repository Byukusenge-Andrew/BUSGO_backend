package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.Payment;
import com.multi.mis.busgo_backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    public Payment createPayment(Payment payment) {
        payment.setPaymentDate(new Date());
        return paymentRepository.save(payment);
    }
    
    public Optional<Payment> updatePayment(Long id, Payment updatedPayment) {
        return paymentRepository.findById(id)
                .map(existingPayment -> {
                    if (updatedPayment.getBooking() != null) {
                        existingPayment.setBooking(updatedPayment.getBooking());
                    }
                    if (updatedPayment.getAmount() != 0) {
                        existingPayment.setAmount(updatedPayment.getAmount());
                    }
                    if (updatedPayment.getPaymentMethod() != null) {
                        existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
                    }
                    if (updatedPayment.getTransactionId() != null) {
                        existingPayment.setTransactionId(updatedPayment.getTransactionId());
                    }
                    if (updatedPayment.getStatus() != null) {
                        existingPayment.setStatus(updatedPayment.getStatus());
                    }
                    if (updatedPayment.getPaymentDetails() != null) {
                        existingPayment.setPaymentDetails(updatedPayment.getPaymentDetails());
                    }
                    return paymentRepository.save(existingPayment);
                });
    }
    
    public boolean deletePayment(Long id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<Payment> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingBookingId(bookingId);
    }
    
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }
    
    public List<Payment> getPaymentsByMethod(String paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod);
    }
    
    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        List<Payment> payments = paymentRepository.findByTransactionId(transactionId);
        return payments.isEmpty() ? Optional.empty() : Optional.of(payments.get(0));
    }
} 
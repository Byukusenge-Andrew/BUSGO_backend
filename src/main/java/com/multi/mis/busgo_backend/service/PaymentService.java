package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.Payment;
import com.multi.mis.busgo_backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private BusBookingService busBookingService;
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    @Transactional
    public Payment createPayment(Payment payment) {
        // Set payment date if not already set
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(new Date());
        }
        
        // Set status to PENDING if not provided
        if (payment.getStatus() == null || payment.getStatus().isEmpty()) {
            payment.setStatus("PENDING");
        }
        
        // Generate transaction ID if not provided
        if (payment.getTransactionId() == null || payment.getTransactionId().isEmpty()) {
            payment.setTransactionId("TXN-" + System.currentTimeMillis());
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update the corresponding booking status only if payment is completed
        if (savedPayment.getBooking() != null && "COMPLETED".equals(savedPayment.getStatus())) {
            Long bookingId = savedPayment.getBooking().getBookingId();
            try {
                busBookingService.confirmBookingAfterPayment(bookingId);
            } catch (Exception e) {
                System.err.println("Failed to update booking status for booking ID: " + bookingId + " after payment ID: " + savedPayment.getPaymentId());
                throw new RuntimeException("Failed to update booking status after payment.", e);
            }
        } else if (savedPayment.getBooking() == null) {
            // Handle case where payment is somehow created without a booking link
            System.err.println("Payment created (ID: " + savedPayment.getPaymentId() + ") but has no associated booking.");
        }
        
        return savedPayment;
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
    
    /**
     * Retrieves all payments associated with a specific company.
     * Requires PaymentRepository.findByCompanyId(Long companyId) method.
     * @param companyId The ID of the company.
     * @return A list of payments for that company.
     */
    public List<Payment> getPaymentsByCompanyId(Long companyId) {
        // This relies on the custom query defined in PaymentRepository
        return paymentRepository.findByCompanyId(companyId);
    }

    /**
     * Calculates payment statistics for a specific company.
     * Requires relevant aggregation queries in PaymentRepository.
     * @param companyId The ID of the company.
     * @return A map containing payment statistics (e.g., count by status, total amount).
     */
    public Map<String, Object> getPaymentStatsByCompanyId(Long companyId) {
        Map<String, Object> stats = new HashMap<>();

        // Example: Get count by status
        List<Object[]> statusCounts = paymentRepository.countPaymentsByStatusForCompany(companyId);
        Map<String, Long> counts = new HashMap<>();
        for (Object[] result : statusCounts) {
            counts.put((String) result[0], (Long) result[1]);
        }
        stats.put("countByStatus", counts);

        // Example: Get total completed payment amount
        Double totalCompletedAmount = paymentRepository.sumCompletedPaymentsForCompany(companyId);
        stats.put("totalCompletedAmount", totalCompletedAmount != null ? totalCompletedAmount : 0.0);

        // Add more stats as needed using other repository methods

        return stats;
    }
    
    /**
     * Process payment and update payment status
     */
    @Transactional
    public Payment processPayment(Long paymentId, String paymentMethod, String transactionId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }
        
        Payment payment = paymentOpt.get();
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(new Date());
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update booking status after successful payment
        if (savedPayment.getBooking() != null) {
            Long bookingId = savedPayment.getBooking().getBookingId();
            try {
                busBookingService.confirmBookingAfterPayment(bookingId);
            } catch (Exception e) {
                System.err.println("Failed to update booking status for booking ID: " + bookingId);
                throw new RuntimeException("Failed to update booking status after payment.", e);
            }
        }
        
        return savedPayment;
    }
    
    /**
     * Refund payment
     */
    @Transactional
    public Payment refundPayment(Long paymentId, String reason) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }
        
        Payment payment = paymentOpt.get();
        if (!"COMPLETED".equals(payment.getStatus())) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        payment.setStatus("REFUNDED");
        payment.setPaymentDetails(payment.getPaymentDetails() + " | Refund Reason: " + reason);
        
        return paymentRepository.save(payment);
    }
} 

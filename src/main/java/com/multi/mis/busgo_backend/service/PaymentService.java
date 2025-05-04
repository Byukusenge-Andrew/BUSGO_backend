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
        payment.setPaymentDate(new Date());
        // Explicitly set the status to COMPLETED before saving
        payment.setStatus("COMPLETED");
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update the corresponding booking status AND paymentStatus
        if (savedPayment.getBooking() != null) {
            Long bookingId = savedPayment.getBooking().getBookingId();
            try {
                // Call the new method in BusBookingService
                busBookingService.confirmBookingAfterPayment(bookingId);
            } catch (Exception e) {
                // Handle potential exceptions during booking status update
                System.err.println("Failed to update booking status/paymentStatus for booking ID: " + bookingId + " after payment ID: " + savedPayment.getPaymentId());
                throw new RuntimeException("Failed to update booking status and paymentStatus after payment.", e);
            }
        } else {
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
} 

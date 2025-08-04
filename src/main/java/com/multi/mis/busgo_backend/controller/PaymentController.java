package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.Payment;
import com.multi.mis.busgo_backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId)
                .map(payment -> ResponseEntity.ok(payment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/payments")
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }
    
    @PutMapping("/payments/{paymentId}")
    public ResponseEntity<?> updatePayment(
            @PathVariable Long paymentId, 
            @RequestBody Payment payment) {
        return paymentService.updatePayment(paymentId, payment)
                .map(updatedPayment -> ResponseEntity.ok(updatedPayment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<?> deletePayment(@PathVariable Long paymentId) {
        boolean deleted = paymentService.deletePayment(paymentId);
        
        if (deleted) {
            return ResponseEntity.ok("Payment deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/payments/booking/{bookingId}")
    public ResponseEntity<List<Payment>> getPaymentsByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId));
    }
    
    @GetMapping("/payments/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }
    
    @GetMapping("/payments/method/{paymentMethod}")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@PathVariable String paymentMethod) {
        return ResponseEntity.ok(paymentService.getPaymentsByMethod(paymentMethod));
    }
    
    @GetMapping("/payments/transaction/{transactionId}")
    public ResponseEntity<?> getPaymentByTransactionId(@PathVariable String transactionId) {
        return paymentService.getPaymentByTransactionId(transactionId)
                .map(payment -> ResponseEntity.ok(payment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/companies/{companyId}/payments
     * Retrieves all payments associated with a specific company.
     */
    @GetMapping("/companies/{companyId}/payments")
    public ResponseEntity<List<Payment>> getPaymentsByCompany(@PathVariable Long companyId) {
        List<Payment> payments = paymentService.getPaymentsByCompanyId(companyId);
        return ResponseEntity.ok(payments);
    }

    /**
     * GET /api/companies/{companyId}/payment-stats
     * Retrieves payment statistics for a specific company.
     */
    @GetMapping("/companies/{companyId}/payment-stats")
    public ResponseEntity<Map<String, Object>> getPaymentStatsByCompany(@PathVariable Long companyId) {
        Map<String, Object> stats = paymentService.getPaymentStatsByCompanyId(companyId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * POST /api/payments/process
     * Process a payment
     */
    @PostMapping("/payments/process")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> paymentRequest) {
        try {
            Long paymentId = Long.valueOf(paymentRequest.get("paymentId").toString());
            String paymentMethod = (String) paymentRequest.get("paymentMethod");
            String transactionId = (String) paymentRequest.get("transactionId");
            
            Payment processedPayment = paymentService.processPayment(paymentId, paymentMethod, transactionId);
            return ResponseEntity.ok(processedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing payment: " + e.getMessage());
        }
    }
    
    /**
     * POST /api/payments/{paymentId}/refund
     * Refund a payment
     */
    @PostMapping("/payments/{paymentId}/refund")
    public ResponseEntity<?> refundPayment(@PathVariable Long paymentId, @RequestBody Map<String, String> refundRequest) {
        try {
            String reason = refundRequest.getOrDefault("reason", "Refund requested");
            Payment refundedPayment = paymentService.refundPayment(paymentId, reason);
            return ResponseEntity.ok(refundedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error refunding payment: " + e.getMessage());
        }
    }
} 

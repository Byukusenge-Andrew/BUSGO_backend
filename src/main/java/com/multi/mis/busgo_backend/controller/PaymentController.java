package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.Payment;
import com.multi.mis.busgo_backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId)
                .map(payment -> ResponseEntity.ok(payment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }
    
    @PutMapping("/{paymentId}")
    public ResponseEntity<?> updatePayment(
            @PathVariable Long paymentId, 
            @RequestBody Payment payment) {
        return paymentService.updatePayment(paymentId, payment)
                .map(updatedPayment -> ResponseEntity.ok(updatedPayment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<?> deletePayment(@PathVariable Long paymentId) {
        boolean deleted = paymentService.deletePayment(paymentId);
        
        if (deleted) {
            return ResponseEntity.ok("Payment deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Payment>> getPaymentsByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }
    
    @GetMapping("/method/{paymentMethod}")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@PathVariable String paymentMethod) {
        return ResponseEntity.ok(paymentService.getPaymentsByMethod(paymentMethod));
    }
    
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<?> getPaymentByTransactionId(@PathVariable String transactionId) {
        return paymentService.getPaymentByTransactionId(transactionId)
                .map(payment -> ResponseEntity.ok(payment))
                .orElse(ResponseEntity.notFound().build());
    }
} 
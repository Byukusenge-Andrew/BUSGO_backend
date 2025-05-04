package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingBookingId(Long bookingId);
    List<Payment> findByStatus(String status);
    List<Payment> findByPaymentMethod(String paymentMethod);
    List<Payment> findByTransactionId(String transactionId);

    // Corrected Query: Find payments linked to bookings whose schedule belongs to a specific company
    @Query("SELECT p FROM Payment p JOIN p.booking b JOIN b.schedule s JOIN s.company c WHERE c.companyId = :companyId")
    List<Payment> findByCompanyId(@Param("companyId") Long companyId);

    // Corrected Query: Count payments by status for a specific company
    @Query("SELECT p.status, COUNT(p) FROM Payment p JOIN p.booking b JOIN b.schedule s JOIN s.company c WHERE c.companyId = :companyId GROUP BY p.status")
    List<Object[]> countPaymentsByStatusForCompany(@Param("companyId") Long companyId); // Renamed method for clarity

    // Corrected Query: Sum completed payment amounts for a specific company
    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.booking b JOIN b.schedule s JOIN s.company c WHERE c.companyId = :companyId AND p.status = 'COMPLETED'")
    Double sumCompletedPaymentsForCompany(@Param("companyId") Long companyId);

} 
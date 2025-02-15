package datn.service.parking.repository;

import datn.service.parking.entity.Account;
import datn.service.parking.entity.CheckoutManually;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface CheckoutManuallyRepository extends JpaRepository<CheckoutManually,UUID> {
    // Hoặc sử dụng @Query
    List<CheckoutManually> findAllByStaffId(UUID staffId);
}

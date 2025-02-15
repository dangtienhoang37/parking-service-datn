package datn.service.parking.repository;

import datn.service.parking.entity.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, UUID> {
    // Truy vấn Parking bằng accountId
    Parking findByAccount_Id(UUID accountId);

}

package datn.service.parking.repository;


import datn.service.parking.entity.Account;
import datn.service.parking.entity.Parking;
import datn.service.parking.entity.ReservationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationScheduleRepository extends JpaRepository<ReservationSchedule, UUID> {
    Optional<ReservationSchedule> findByAccount(Account existedAcc);

    List<ReservationSchedule> findAllByParking(Parking targetParking);
    @Query("SELECT COUNT(b) FROM ReservationSchedule b WHERE :inputTime BETWEEN b.startTime AND b.endTime")
    int countBookingsAtTime(@Param("inputTime") Instant inputTime);
}

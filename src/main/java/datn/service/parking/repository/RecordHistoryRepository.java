package datn.service.parking.repository;

import datn.service.parking.entity.Account;
import datn.service.parking.entity.RecordHistory;
import datn.service.parking.entity.ReservationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecordHistoryRepository extends JpaRepository<RecordHistory, UUID> {
    Optional<RecordHistory> findByAccount(Account existedAcc);


    List<RecordHistory> findAllByAccount(Account existedAcc);

}

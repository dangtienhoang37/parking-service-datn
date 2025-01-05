package datn.service.parking.repository;

import datn.service.parking.entity.Account;
import datn.service.parking.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID> {
    Optional<Record> findByAccount(Account existedAcc);
}

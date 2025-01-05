package datn.service.parking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservation_schedules")
public class ReservationSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    @JoinColumn(name = "accountId",referencedColumnName = "id")
    private Account account;
    @ManyToOne
    @JoinColumn(name = "parkingId", referencedColumnName = "id")
    private Parking parking;
    private Instant startTime;
    private Instant endTime;
    private Boolean valid;
    private String moreInfor;

}

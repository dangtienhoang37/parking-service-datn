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
@Table(name = "record")

public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "parkingId", referencedColumnName = "id")
    private Parking parking;
    @ManyToOne
    @JoinColumn(name = "accountId", referencedColumnName = "id")
    private Account account;
    private int spotIndex;
    private String lPlateNumber;
    private Instant entryTime;
    private UUID sessionId;
    private String imageUrlIn;
    private String imageUrlOut;
    @OneToOne
    @JoinColumn(name="ReservationScheduleId", referencedColumnName = "id")
    private ReservationSchedule reservationSchedule;


}

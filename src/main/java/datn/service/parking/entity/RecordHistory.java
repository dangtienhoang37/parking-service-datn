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
@Table(name = "record_histories")
public class RecordHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "parkingId", referencedColumnName = "id")
    private Parking parking;
//    @ManyToOne
//    @JoinColumn(name = "currentRecordId", referencedColumnName = "id")
    @ManyToOne
    @JoinColumn(name = "accountId", referencedColumnName = "id")
    private Account account;
    private int spotIndex;
    private String lPlateNumber;
    private String imageUrlIn;
    private String imageUrlOut;
    private UUID sessionId;
    private Instant entryTime;
    private Instant endTime;
    private int dayTime;
    private int nightTime;
    private Long totalCost;

}

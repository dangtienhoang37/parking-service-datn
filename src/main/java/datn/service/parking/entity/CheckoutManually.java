package datn.service.parking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "checkout_manually")
public class CheckoutManually {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID accountId;
    private UUID staffId;
    private String imageUrlIn;
    private String imageUrlOut;
    @OneToOne
    @JoinColumn(name = "recordId", referencedColumnName = "id")
    private Record record;
    @OneToOne
    @JoinColumn(name = "recordHistoryId", referencedColumnName = "id")
    private RecordHistory recordHistory;
    @ManyToOne
    @JoinColumn(name = "parkingId", referencedColumnName = "id")
    private Parking parking;
    @Column(nullable = false)
    private Boolean isDone = false;

}

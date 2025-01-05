package datn.service.parking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "parking")
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "wardId", referencedColumnName = "id")
    private Ward ward;
    @ManyToOne
    @JoinColumn(name = "districtId", referencedColumnName = "id")
    private District district;
    @OneToOne
    @JoinColumn(name ="priceId" , referencedColumnName = "id")
    private Price price;

    private UUID deviceId;
    @ManyToOne
    @JoinColumn(name ="staffId" , referencedColumnName = "id")
    private Account account;
    private Double latitude;
    private Double longtitude;
    private String parkingName;
    private int directSpacesCap;
    private int directSpacesAvailible;
    private int reservedSpacesCap;
    private int reservedSpacesAvailible;

}

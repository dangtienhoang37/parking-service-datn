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
@Table(name = "prices")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private Long dayTimeRate;
    private Long nightTimeRate;
    @Column(nullable = false)
    private Boolean isActive = true;

}

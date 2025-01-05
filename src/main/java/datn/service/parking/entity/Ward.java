package datn.service.parking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wards")
public class Ward {
    @Id
    String id;
    String name;
    String enName;
    String fullName;
    String enFullName;
    String codeName;
    @ManyToOne
    @JoinColumn(name = "districtId", referencedColumnName = "id")
    District district;


}

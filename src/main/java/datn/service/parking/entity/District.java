package datn.service.parking.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "districts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class District {
    @Id
    String id;
    @Column(nullable = false)
    String name;
    String enName;
    String fullName;
    String enFullName;
    String codeName;
    @JsonCreator
    public static District fromId(@JsonProperty("id") String id) {
        District district = new District();
        district.setId(id);
        // Bạn có thể thêm các logic khác ở đây, ví dụ như tìm district từ database bằng id
        return district;
    }
}

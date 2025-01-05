package datn.service.parking.dto.dbobj;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class warDTO {

    String id;
    String name;
    String enName;
    String fullName;
    String enFullName;
    String codeName;
    String district_id;

}

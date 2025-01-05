package datn.service.parking.dto.request.Creation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AreaCreationRequest {
    String areaName;
    UUID staffId;
    String Ward;
    String District;
    String City;


}

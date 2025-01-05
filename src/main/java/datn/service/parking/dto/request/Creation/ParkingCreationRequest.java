package datn.service.parking.dto.request.Creation;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParkingCreationRequest {
    String districtId;
    String wardId;
    UUID priceId;
    UUID staffId;
    Double latitude;
    Double longtitude;
    String parkingName;
    UUID deviceId;
    int directSpacesCap;
    int directSpacesAvailible;
    int reservedSpacesCap;
    int reservedSpacesAvailible;
}

package datn.service.parking.dto.request.GetData;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetRemainingByTimeRequest {
    Instant time;
    UUID parkingId;
}

package datn.service.parking.dto.request.Creation;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreationRequest {
    UUID parkingId;
    Instant startTime;
    Instant endTime;
}

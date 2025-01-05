package datn.service.parking.dto.request.Creation;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceCreationRequest {
   String name;
   Long dayTimeRate;
   Long nightTimeRate;
}

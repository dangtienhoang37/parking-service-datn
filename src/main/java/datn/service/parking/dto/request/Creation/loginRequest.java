package datn.service.parking.dto.request.Creation;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class loginRequest {
    private String userName;
    private String password;

}

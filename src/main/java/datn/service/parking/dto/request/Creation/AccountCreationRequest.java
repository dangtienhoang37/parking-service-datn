package datn.service.parking.dto.request.Creation;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountCreationRequest {
    String userName;
    @Size(min = 10, message = "password must be at least 10 char")
    String password;

}

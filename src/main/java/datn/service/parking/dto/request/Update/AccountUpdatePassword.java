package datn.service.parking.dto.request.Update;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AccountUpdatePassword {
    String currentPassword;

    @Size(min = 10, message = "password must be at least 10 char")
    String newPassword;
    String confirmPassword;
}

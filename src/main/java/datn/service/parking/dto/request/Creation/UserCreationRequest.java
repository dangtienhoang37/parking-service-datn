package datn.service.parking.dto.request.Creation;

import datn.service.parking.enumvar.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
     String fullName;
     Gender gender;
     String phoneNumber;
     String PID;
     String email;
     String address;
     String userImg;
//     Optional<MultipartFile> file;
}

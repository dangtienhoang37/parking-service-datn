package datn.service.parking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import datn.service.parking.enumvar.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String userName;
    @JsonIgnore
    String password;
    @Enumerated(EnumType.STRING)
    Role role;
    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    User user;
    @Builder.Default
    boolean isActive=true;
    @Column(nullable = false)
    @Builder.Default
    boolean init = true;


}

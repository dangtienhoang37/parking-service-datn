package datn.service.parking.entity;

import datn.service.parking.enumvar.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String fullName;
    private Gender gender;
    private String phoneNumber;
    private String PID;
    private String email;
    private String address;
    private String userImg;
    @JoinColumn(name = "walletId", referencedColumnName = "id")
    @OneToOne
    private TransactionWallet transactionWallet;



}

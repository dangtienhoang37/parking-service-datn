package datn.service.parking.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transaction_wallet")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    int balance;
    @Column(nullable = false)
    UUID accountId;


}

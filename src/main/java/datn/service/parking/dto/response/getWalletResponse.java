package datn.service.parking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import datn.service.parking.entity.Account;
import datn.service.parking.entity.TransactionWallet;
import lombok.*;

@Setter
@Getter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class getWalletResponse {
    private int code;
    private String message;
    private boolean success;
    private TransactionWallet data;




}
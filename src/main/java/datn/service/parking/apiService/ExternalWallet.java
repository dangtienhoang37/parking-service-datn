package datn.service.parking.apiService;

import datn.service.parking.dto.ApiResponse;
import datn.service.parking.dto.externalRequest.SubBalanceRequest;
import datn.service.parking.dto.response.getWalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@FeignClient(name = "wallet", url = "localhost:8083")
public interface ExternalWallet {
    @PostMapping("/api/v3/payment/user/sub-balance")
    ApiResponse makeNewTrans(@RequestBody SubBalanceRequest request);
    @GetMapping("/api/v3/payment/user")
    getWalletResponse getDetailWallet(@RequestHeader("Authorization") String token);
}

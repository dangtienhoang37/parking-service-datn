package datn.service.parking.apiService;

import datn.service.parking.dto.response.GetParkingResponse;
import datn.service.parking.dto.response.getAccountResponse;
import datn.service.parking.entity.Account;
import datn.service.parking.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "common", url = "common-service:8081/api/v1")
public interface ExternalApiClient{
//    private final String apikey = "internalApikey";
    @GetMapping("/account/infor")
    getAccountResponse getUserInfo(@RequestHeader("Authorization") String token,@RequestHeader("X-api-key") String apikey);

    @GetMapping("/parking/{id}")
    GetParkingResponse getParkingInfor(@RequestHeader("Authorization") String token, @PathVariable UUID id,@RequestHeader("X-api-key") String apikey);



}

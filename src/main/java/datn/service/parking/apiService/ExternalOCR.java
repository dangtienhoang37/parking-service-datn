package datn.service.parking.apiService;

import datn.service.parking.dto.response.GetParkingResponse;
import datn.service.parking.dto.response.getAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@FeignClient(name = "OCR", url = "localhost:8000")
public interface ExternalOCR {
    //    private final String apikey = "internalApikey";
//    @GetMapping("/account/infor")
//    getAccountResponse getUserInfo(@RequestHeader("Authorization") String token, @RequestHeader("X-api-key") String apikey);
//
//    @GetMapping("/parking/{id}")
//    GetParkingResponse getParkingInfor(@RequestHeader("Authorization") String token, @PathVariable UUID id, @RequestHeader("X-api-key") String apikey);
    @PostMapping("/upload")
    Object sendOcr(@RequestParam("file")MultipartFile file, @RequestParam("id") UUID id);
}

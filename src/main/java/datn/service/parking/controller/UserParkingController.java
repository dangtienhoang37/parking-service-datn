package datn.service.parking.controller;



import datn.service.parking.dto.response.ApiResponse;
import datn.service.parking.service.InOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user-parking")
public class UserParkingController {
    // checkin
    @Autowired
    private InOutService inOutService;
    @PostMapping("/checkin")
    public ApiResponse checkin(
                                @RequestParam("deviceId") UUID deviceId,
                                @RequestParam("sessionId") UUID sessionId,
                                @RequestParam("parkingId")UUID parkingId,
                                @RequestParam("accountId") UUID accountId,
                                @RequestParam("timestamp") long timestamp,
                                @RequestHeader("Authorization") String token

                                ) {

        return inOutService.checkin(deviceId, accountId,sessionId, timestamp,token, parkingId);
    }
    @PostMapping("/checkout")
    public ApiResponse checkout(
                                @RequestParam("deviceId") UUID deviceId,
                                @RequestParam("parkingId") UUID parkingId,
                                @RequestParam("sessionId") UUID sessionId,
                                @RequestParam("accountId") UUID accountId,
                                @RequestParam("timestamp") long timestamp,
                                @RequestHeader("Authorization") String token
                                ) {
        return inOutService.checkout(deviceId, accountId,sessionId, timestamp,token,parkingId);
    }
    @GetMapping("/detail")
    public ApiResponse currentDetail(@RequestHeader("Authorization") String token){
        return inOutService.currentDetail(token);
    }
    // checkout
    // user send checkout manually
    @PostMapping("/manually/checkout")
    public ApiResponse userSendChechoutManually(@RequestHeader("Authorization")String token){
        return inOutService.userCheckoutManually(token);
    }
//    @GetMapping("/manually/checkout/")
    // get list manually by parking
    @PostMapping("/manually/checkout/{id}")
    public ApiResponse confirmChechoutManually(@RequestHeader("Authorization")String token, @PathVariable("id") UUID recordCM){
        return inOutService.confirmCheckoutManually(token,recordCM);
    }
    @GetMapping("/manually")
    public ApiResponse getAll(@RequestHeader("Authorization") String token){
        return inOutService.getAllmanually(token);
    }
//    @GetMapping("/manually")
//    public ApiResponse gee(@RequestHeader("Authorization") String token){
//        return inOutService.getAllmanually(token);
//    }
}

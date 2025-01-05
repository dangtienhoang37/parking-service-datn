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
    // checkout

}

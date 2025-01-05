package datn.service.parking.controller;



import datn.service.parking.dto.request.Creation.BookingCreationRequest;
import datn.service.parking.dto.request.GetData.GetRemainingByTimeRequest;
import datn.service.parking.dto.response.ApiResponse;
import datn.service.parking.service.ReservationScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private ReservationScheduleService reservationScheduleService;
    // đặt trước
    @PostMapping()
    public ApiResponse booking(@RequestBody BookingCreationRequest request, @RequestHeader("Authorization") String token) {
        return reservationScheduleService.booking(request,token);
    }
    // check xem con bao nhieu cho co the dat truoc trong thoi gian do
    @PostMapping("/check-remaining-by-time")
    public ApiResponse checkRemainByTime (@RequestBody GetRemainingByTimeRequest request, @RequestHeader("Authorization") String token){
        return reservationScheduleService.checkRemainingByTime(request,token);
    }



    // get all by Parking
    @GetMapping("/{parkingId}")
    public ApiResponse getAll(@PathVariable UUID parkingId,@RequestHeader("Authorization") String token) {
        return reservationScheduleService.getAllByParking(parkingId,token);
    }

    // kiểm tra đặt trước - get all by parking
    // get all by Account ID
    @GetMapping({"/user/{id}", "/user"})
    public ApiResponse getAllByAcc(@PathVariable(required = false) UUID id,@RequestHeader("Authorization") String token) {
        return reservationScheduleService.getBookingByUser(id,token);
    }
    // for user: get booking history
//    @GetMapping("/history")


}

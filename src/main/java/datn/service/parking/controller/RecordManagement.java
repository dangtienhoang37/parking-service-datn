package datn.service.parking.controller;

import com.netflix.discovery.converters.Auto;
import datn.service.parking.dto.response.ApiResponse;
import datn.service.parking.service.InOutService;
import datn.service.parking.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("/admin-record")
public class RecordManagement {
    @Autowired
    private RecordService recordService;

    @Autowired
    private  InOutService inOutService;
    // get all record by parking: parking id
    @GetMapping("/get-all/{parkingId}")
    ApiResponse getAllRecord(@PathVariable UUID parkingId, @RequestHeader("Authorization") String token){
        return recordService.getAllByParking(parkingId,token);
    }
    // get detail record: record id
    @GetMapping("/{RecordId}")
    ApiResponse getDetailRecord(@PathVariable UUID RecordId, @RequestHeader("Authorization") String token){
        return recordService.getDetailRecord(RecordId,token);
    }
//    // checkout: record id
//    @PostMapping("")
//    ApiResponse CheckoutManually(@PathVariable UUID RecordId, @RequestHeader("Authorization") String token){
//        return inOutService.checkoutManually(RecordId,token);
//    }

    // get all history record by parking: parking id
    // get detail parking record
}

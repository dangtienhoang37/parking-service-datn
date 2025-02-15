package datn.service.parking.controller;


import datn.service.parking.dto.response.ApiResponse;
import datn.service.parking.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("/history")
public class UserHistoryController {
@Autowired
    private HistoryService historyService;

    @GetMapping()
    public ApiResponse getHistory(@RequestHeader("Authorization") String token){
        return historyService.getHistory(token);
    }
    @GetMapping("/{id}")
    public ApiResponse detailHistory(@PathVariable UUID id){
        return historyService.detailHistory(id);
    }
}

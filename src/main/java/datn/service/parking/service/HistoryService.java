package datn.service.parking.service;

import datn.service.parking.dto.response.ApiResponse;

import java.util.UUID;

public interface HistoryService {
    ApiResponse getHistory(String token);

    ApiResponse detailHistory(UUID id);
}

package datn.service.parking.service;

import datn.service.parking.dto.response.ApiResponse;

import java.util.UUID;

public interface RecordService {
    public ApiResponse getAllByParking(UUID parkingId, String token);

    public ApiResponse getDetailRecord(UUID RecordId, String token);
}

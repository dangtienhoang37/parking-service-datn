package datn.service.parking.service;

import datn.service.parking.dto.response.ApiResponse;

import java.util.UUID;

public interface InOutService {
      ApiResponse checkin(UUID deviceId, UUID accountId,UUID sessionId, long timestamp,String token, UUID parkingId);

     ApiResponse checkout(UUID deviceId, UUID accountId,UUID sessionId, long timestamp,String token,UUID parkingId);

}

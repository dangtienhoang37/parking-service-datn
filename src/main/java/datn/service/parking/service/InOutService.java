package datn.service.parking.service;

import datn.service.parking.dto.response.ApiResponse;

import java.util.UUID;

public interface InOutService {
    ApiResponse checkoutManually(UUID RecordId, String token);

    ApiResponse checkin(UUID deviceId, UUID accountId, UUID sessionId, long timestamp, String token, UUID parkingId);

     ApiResponse checkout(UUID deviceId, UUID accountId,UUID sessionId, long timestamp,String token,UUID parkingId);
     String getLCPFromCache(UUID sessionId);

    String getAccountIdFromCache(UUID sessionId);

    ApiResponse currentDetail(String token);

    ApiResponse userCheckoutManually(String token);

    ApiResponse confirmCheckoutManually(String token, UUID recordCM);

    ApiResponse getAllmanually(String token);
}

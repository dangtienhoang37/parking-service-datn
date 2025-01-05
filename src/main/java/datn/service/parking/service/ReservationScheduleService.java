package datn.service.parking.service;



import datn.service.parking.dto.request.Creation.BookingCreationRequest;
import datn.service.parking.dto.request.GetData.GetRemainingByTimeRequest;
import datn.service.parking.dto.response.ApiResponse;

import java.util.UUID;

public interface ReservationScheduleService {
    ApiResponse booking(BookingCreationRequest request, String token);
    ApiResponse getAllService();
//    ApiResponse getAllByAreaService();

    ApiResponse getAllByParking(UUID parkingId, String token);

    ApiResponse getBookingByUser(UUID id, String token);

    ApiResponse checkRemainingByTime(GetRemainingByTimeRequest request, String token);
    // get all theo staff
}

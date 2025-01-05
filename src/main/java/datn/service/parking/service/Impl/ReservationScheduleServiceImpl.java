package datn.service.parking.service.Impl;


import datn.service.parking.apiService.ExternalApiClient;
import datn.service.parking.apiService.ExternalWallet;
import datn.service.parking.dto.externalRequest.SubBalanceRequest;
import datn.service.parking.dto.request.Creation.BookingCreationRequest;
import datn.service.parking.dto.request.GetData.GetRemainingByTimeRequest;
import datn.service.parking.dto.response.ApiResponse;
import datn.service.parking.dto.response.GetParkingResponse;
import datn.service.parking.dto.response.getAccountResponse;
import datn.service.parking.entity.Account;
import datn.service.parking.entity.Parking;
import datn.service.parking.entity.ReservationSchedule;
import datn.service.parking.enumvar.Role;
import datn.service.parking.repository.ReservationScheduleRepository;
import datn.service.parking.service.ReservationScheduleService;
import datn.service.parking.utils.JWToken;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class ReservationScheduleServiceImpl implements ReservationScheduleService {
    @Autowired
    private JWToken jwToken;
    @Autowired
    private ReservationScheduleRepository reservationScheduleRepository;
    @Autowired
    private ExternalApiClient externalApiClient;
    @Autowired
    private ExternalWallet externalWallet;


    @Override
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    public ApiResponse booking(BookingCreationRequest request, String token) {
        try{
            var ParkingId = request.getParkingId();
            UUID AccountId = jwToken.getIdFromToken(token);


            if(Objects.isNull(request.getStartTime()) || Objects.isNull(request.getEndTime()) || request.getStartTime().isAfter(request.getEndTime()) ||request.getStartTime().isBefore(Instant.now(Clock.system(ZoneId.of("Asia/Ho_Chi_Minh"))))){
                throw new RuntimeException("please choose  right checkin time / checkout time");
            }
            final Instant startTime = request.getStartTime();
            final Instant endTime = request.getEndTime();

            // check thoi gian checnh lech
            log.info("thoi gian bat dau: {}", startTime);
            log.info("thoi gian ket thuc: {}", endTime);
            Duration duration= Duration.ofDays((Duration.between(startTime, endTime)).toDays());
            long numOfDay = duration.toDays();
            log.info("gui xe trong: {}", numOfDay);
            if( numOfDay > 4) {
                throw new RuntimeException("longest time is 3 days");
            }
            // check con cho trong
            GetRemainingByTimeRequest virtualRequest = GetRemainingByTimeRequest.builder()
                    .parkingId(ParkingId)
                    .time(startTime)
                    .build();
            ApiResponse checkRemainingByTime = checkRemainingByTime(virtualRequest,token);
            log.warn("remaining hhaha: {}",checkRemainingByTime.getData());
//
            if ((int)checkRemainingByTime.getData() <= 0) {
                throw new RuntimeException(" da het cho dat truoc");
            }
            if(AccountId == null) {
                throw new RuntimeException("no account id");
            }
            // call api from base service - datn get user infor

            getAccountResponse account = externalApiClient.getUserInfo(token,"internalApikey");
            log.warn("id from api {}",account.getData().getId());
            log.warn("id from token {}",AccountId);
            if(!account.getData().getId().equals(AccountId)){
                throw new RuntimeException("no account found");
            }
            Account targetAccount = account.getData();
//        Account existedAcc =
            GetParkingResponse existedParking = externalApiClient.getParkingInfor(token,ParkingId,"internalApikey");
            if(!existedParking.getData().getId().equals(ParkingId)){
                throw new RuntimeException("no parking found");
            }
            Parking targetParking = existedParking.getData();
//        var targetParking = parkingRepository.findById(request.getParkingId()).orElse(null);
//        if(Objects.isNull(targetParking)) {
//            throw new RuntimeException("no parking found");
//        }
            // call api from base service - datn find account
            var existedBooking = reservationScheduleRepository.findByAccount(targetAccount).orElse(null);
            if(Objects.nonNull(existedBooking)) {
                throw new RuntimeException("existed, cant create booking");
            }
            // check tiền trong tk -> trừ tiền
           datn.service.parking.dto.ApiResponse walletRes = externalWallet.makeNewTrans(SubBalanceRequest.builder()
                            .accountId(AccountId)
                            .balance(20000)
                            .build());
            if(!walletRes.isSucess()){
                throw new RuntimeException("fail");
            }
            log.warn("gui api ok");

            ReservationSchedule target = ReservationSchedule.builder()
                    .account(targetAccount)
                    .parking(targetParking)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .moreInfor("")
                    .valid(true)
                    .build();
            new ApiResponse<>();
            reservationScheduleRepository.save(target);
            return ApiResponse.builder()
                    .code(1000)
                    .message("create sucessfully")
                    .isSucess(true)
                    .data(reservationScheduleRepository.findByAccount(account.getData()))
                    .build();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResponse getAllService() {
        return null;
    }

//    @Override
//    public ApiResponse getAllByAreaService() {
//        return null;
//    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public ApiResponse getAllByParking(UUID parkingId,String token) {
        boolean flag = Objects.equals(jwToken.getRoleFromToken(token), Role.ADMIN.toString());
        var AccountId = jwToken.getIdFromToken(token);
        if(AccountId == null) {
            throw new RuntimeException("no account id");
        }
        getAccountResponse account = externalApiClient.getUserInfo(token,"internalApikey");
        if(!account.getData().getId().equals(AccountId)){
            throw new RuntimeException("no account found");
        }
        Account targetAccount = account.getData();
//        Account existedAcc =
        GetParkingResponse existedParking = externalApiClient.getParkingInfor(token,parkingId,"internalApikey");
        if(!existedParking.getData().getId().equals(parkingId)){
            throw new RuntimeException("no parking found");
        }
        Parking targetParking = existedParking.getData();
//        Account existedAcc = accountRepository.findById(AccountId).orElse(null);
//        if(Objects.isNull(existedAcc)) {
//            throw new RuntimeException("no account found");
//        }
//
//        var targetParking = parkingRepository.findById(parkingId).orElse(null);
//        if(Objects.isNull(targetParking)){
//            throw new RuntimeException("cant find parking");
//        }

        if(Objects.equals(targetParking.getAccount(),targetAccount)){
            flag =true;
        }


        if(flag) {
            // xử lý payment tại đây
            new ApiResponse<>();
            return ApiResponse.builder()
                    .code(1000)
                    .message("find all sucessfully")
                    .isSucess(true)
                    .data(reservationScheduleRepository.findAllByParking(targetParking))
                    .build();
        } else {
            throw new RuntimeException("dont have permission");
        }



    }

    @Override
    public ApiResponse getBookingByUser(UUID id, String token) {
        try {
            // trường hợp là user
            if(Objects.equals(jwToken.getRoleFromToken(token),Role.USER.toString())){
//                var targetAcc = accountRepository.findById(jwToken.getIdFromToken(token)).orElse(null);
                getAccountResponse account = externalApiClient.getUserInfo(token,"internalApikey");
                if(account.getData().getId() == null ){
                    throw new RuntimeException("no account found");
                }
                Account targetAcc = account.getData();

                if(Objects.isNull(targetAcc)){
                    throw new Exception("acc not existed");
                }
                new ApiResponse<>();
                return ApiResponse.builder()
                        .code(1000)
                        .message("find all sucessfully")
                        .isSucess(true)
                        .data(reservationScheduleRepository.findByAccount(targetAcc))
                        .build();
            } if(Objects.equals(jwToken.getRoleFromToken(token),Role.ADMIN.toString())){
//                var targetAcc = accountRepository.findById(id).orElse(null);
                getAccountResponse account = externalApiClient.getUserInfo(token,"internalApikey");
                Account targetAcc = account.getData();
                if(Objects.isNull(targetAcc)){
                    throw new Exception("acc not existed");
                }
                new ApiResponse<>();
                return ApiResponse.builder()
                        .code(1000)
                        .message("find all sucessfully")
                        .isSucess(true)
                        .data(reservationScheduleRepository.findByAccount(targetAcc))
                        .build();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ApiResponse checkRemainingByTime(GetRemainingByTimeRequest request, String token) {
        UUID parkingId = request.getParkingId();
        Instant timeCheckin = request.getTime();
        GetParkingResponse parking = externalApiClient.getParkingInfor(token,parkingId,"internalApikey");
        var Caps= parking.getData().getReservedSpacesCap();
        var existedBookingInTime = reservationScheduleRepository.countBookingsAtTime(timeCheckin);
        return ApiResponse.builder()
                .code(1000)
                .message("count sucessfully")
                .isSucess(true)
                .data(
                        Caps- existedBookingInTime
                )
                .build();

    }
}

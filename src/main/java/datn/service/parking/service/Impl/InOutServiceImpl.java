package datn.service.parking.service.Impl;


import datn.service.parking.apiService.ExternalApiClient;
import datn.service.parking.apiService.ExternalWallet;
import datn.service.parking.dto.response.ApiResponse;
import datn.service.parking.dto.response.GetParkingResponse;
import datn.service.parking.dto.response.getAccountResponse;
import datn.service.parking.entity.Parking;
import datn.service.parking.entity.Record;
import datn.service.parking.entity.RecordHistory;
import datn.service.parking.entity.ReservationSchedule;
import datn.service.parking.repository.RecordHistoryRepository;
import datn.service.parking.repository.RecordRepository;
import datn.service.parking.repository.ReservationScheduleRepository;
import datn.service.parking.service.InOutService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class InOutServiceImpl implements InOutService {

    @Autowired
    private ReservationScheduleRepository reservationScheduleRepository;
    @Autowired
    private ExternalApiClient externalApiClient;
    @Autowired
    private ExternalWallet externalWallet;
    @Autowired
    private RecordHistoryRepository recordHistoryRepository;
    @Autowired
    private RecordRepository recordRepository;
    @Override
    @Transactional
    public ApiResponse checkin(UUID deviceId, UUID accountId,UUID sessionId, long timestamp,String token,UUID parkingId) {

        //lấy session id( khi ocr service thực hiện thì sẽ send img lên trên cloudinary và đặt tên ảnh là sessionId
        // sau đó đồng thời post lên cache lên bao gồm sessionId và biển số. parking service kéo về . khi hợp lệ thì lưu vào db,
        UUID targetSessionId = sessionId;

        //kiem tra timestamp cua hien tai
        System.out.println("entry in");
        long currentTimest = System.currentTimeMillis()/1000;// sang time second
        boolean isValidTimestamp = (Math.abs(currentTimest - timestamp) < 100 * 1000);
        System.out.println("1 "+currentTimest);
        System.out.println("2 "+timestamp);
        System.out.println(isValidTimestamp);
        if(!isValidTimestamp){
            throw new RuntimeException("thoi gian khong hop le");
        }



        // lay account
        getAccountResponse account = externalApiClient.getUserInfo(token,"internalApikey");
        var targetAcc = account.getData();
        // check ton  tại tài khoản
        if(Objects.isNull(targetAcc)){
            throw new RuntimeException("account id not valid");
        }
        // check xem co dang gui xe khong
        var Record = recordRepository.findByAccount(targetAcc).orElse(null);
        if(Objects.nonNull(Record)){
            throw new RuntimeException("Tai khoan dang gui xe, vui long khong thao tac gui xe");
        }

        // check device
        GetParkingResponse parking = externalApiClient.getParkingInfor(token,parkingId,"internalApikey");
        var targetParking = parking.getData();
        // check deviceid
        if(!Objects.equals(targetParking.getDeviceId(), deviceId)){
            throw new RuntimeException("different device id");
        }

        // tim xem co dat truoc khong
        ReservationSchedule targetReservation = reservationScheduleRepository.findByAccount(targetAcc).orElse(null);


        if(Objects.isNull(targetReservation)){
            // code xu ly khong dat truoc
            System.out.println("khong dat truoc ");
            // check vi xem co du tien khong
            // find by acc id
            var targetWallet = externalWallet.getDetailWallet(token);
            if(targetWallet.getCode() != 1000) {
                throw new RuntimeException("fetch wallet fail");
            }
            int targetBalance = targetWallet.getData().getBalance();

            if(targetBalance < 100000) {
//                throw new RuntimeException("khong du so du");
                System.out.println("khong du so du, vui long nap them");
                //ocr
                // lay data tu trong redis bang cach tim theo session id
                final String LPlate = "test plate";
                Record newRecord =  datn.service.parking.entity.Record.builder()
                        .parking(targetParking)
                        .account(targetAcc)
                        .spotIndex(0)
                        .lPlateNumber(LPlate)
                        .entryTime(Instant.now())
                        .build();
                // mo barie vao bai khong dat truo
                System.out.println("mo barie, vao bai khong dat truoc ben phai");

                final String message = "checkin thanh cong khong dat truoc voi bien so "+ LPlate;
                recordRepository.save(newRecord);
                // lưu accountId vào cache -> phục vụ ra vào bãi
                ApiResponse response = ApiResponse.builder()
                        .code(1000)
                        .isSucess(true)
                        .message(message)
                        .build();
                return  response;


            }

        }
        else{
            System.out.println("dat truoc ");
            // chi can check status dat truoc. -> tao cronjob de thuc thi tai cac moc thoi gian
//            if(targetReservation.get)
            // code xu li dat truoc
            // kiem tra thoi gian bay gio co hop le so voi dat truoc khong - trong khoang tu tg dat truoc den +30p
            Instant checkinTimeReservation = Instant.ofEpochMilli(timestamp);
            Duration duration = Duration.between(checkinTimeReservation, Instant.now());
            boolean flag = true;
            if (duration.getSeconds() > 5){
                flag = false;
            }
            if(duration.getSeconds() < -30){
                flag = false;
            }
            if(!flag){
                // check neu muon hon qua lau => throw va set booking -> valid: false: li do la muon gio qua lau

                throw new RuntimeException(" thoi gian checkin khong hop le do som/ muon hon thoi gian dat lich qua lau");
            }
            if(!targetReservation.getValid()){
                var message = "khong hop le do:" + targetReservation.getMoreInfor();
                throw new RuntimeException(message);
            }

            Parking targetParkingBooking = targetReservation.getParking();
            if(!Objects.equals(targetParking.getId(), targetParkingBooking.getId())){
                // vao nham bai, mo barie di ra ngoai
                System.out.println("vao nham bai do xe ");
                ApiResponse response =  ApiResponse.builder()
                        .code(1000)
                        .isSucess(true)
                        .message("quay lai")
                        .build();
                return  response;

            } else{
                // vao dung bai
                System.out.println("dat truoc ok, vao dung bai ");
                // tao record
                // ocr tra ve -> ghi vao plate
                final String LPlate = "test plate";
                Record newRecord =  datn.service.parking.entity.Record.builder()
                        .parking(targetParkingBooking)
                        .account(targetAcc)
                        .spotIndex(0)
                        .lPlateNumber(LPlate)
                        .entryTime(Instant.now())
                        .reservationSchedule(targetReservation)
                        .build();
                // mo barie
                final String message = "checkin thanh cong voi bien so "+ LPlate;

                recordRepository.save(newRecord);
                System.out.println("mo barie, vao bai ben trai- dat truoc");

                ApiResponse response = ApiResponse.builder()
                        .code(1000)
                        .isSucess(true)
                        .message(message)
                        .build();
                return  response;
            }
        }

        return null;
    }

    @Override
    public ApiResponse checkout(UUID deviceId, UUID accountId,UUID sessionId, long timestamp,String token,UUID parkingId) {
        //xe tien vao checkout.
        // kiem tra token
        // kiem tra deviceid khop voi parking id
        // kiem tra timestamp
        //kiem tra timestamp cua hien tai
        // check record
        // check record co dung voi bai gui xe ko

        System.out.println("entry in");
        long currentTimest = System.currentTimeMillis()/1000;// sang time second
        boolean isValidTimestamp = (Math.abs(currentTimest - timestamp) < 100 * 1000);
        System.out.println("1 "+currentTimest);
        System.out.println("2 "+timestamp);
        System.out.println(isValidTimestamp);
        if(!isValidTimestamp){
            throw new RuntimeException("thoi gian khong hop le");
        }



        // lay account
        getAccountResponse account = externalApiClient.getUserInfo(token,"internalApikey");
        var targetAcc = account.getData();
        // check ton  tại tài khoản
        if(Objects.isNull(targetAcc)){
            throw new RuntimeException("account id not valid");
        }
        // check device
        GetParkingResponse parking = externalApiClient.getParkingInfor(token,parkingId,"internalApikey");
        var targetParking = parking.getData();
        // check deviceid
        if(!Objects.equals(targetParking.getDeviceId(), deviceId)){
            throw new RuntimeException("different device id");
        }
        // check xem co dang gui xe khong
        var record = recordRepository.findByAccount(targetAcc).orElse(null);
        if(!Objects.nonNull(record)){
            throw new RuntimeException("Khong tim thay record gui xe, kiem tra lai");
        }
        // check xem dung bai do khong
        if (!Objects.equals(record.getParking().getId(),parkingId)){
            throw new RuntimeException("khong dung bai do, kiem tra lai");
        }
        // ocr - check voi bien so da gui

        // neu dung thi tru tien


        ApiResponse response = new ApiResponse().builder()
                .code(1000)
                .isSucess(true)
                .message("check out voi bien so ok")
                .data("ok")
                .build();
        return response;
    }
}

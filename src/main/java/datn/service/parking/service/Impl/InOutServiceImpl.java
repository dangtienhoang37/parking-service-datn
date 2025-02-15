package datn.service.parking.service.Impl;


import datn.service.parking.apiService.ExternalApiClient;
import datn.service.parking.apiService.ExternalWallet;
import datn.service.parking.dto.externalRequest.SubBalanceRequest;
import datn.service.parking.dto.response.ApiResponse;
import datn.service.parking.dto.response.GetParkingResponse;
import datn.service.parking.dto.response.getAccountResponse;
import datn.service.parking.entity.*;
import datn.service.parking.entity.Record;
import datn.service.parking.repository.*;
import datn.service.parking.service.CloudinaryService;
import datn.service.parking.service.InOutService;
import datn.service.parking.service.RedisService;
import datn.service.parking.utils.JWToken;
import datn.service.parking.utils.MQTT;
import datn.service.parking.utils.TimeUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class InOutServiceImpl implements InOutService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private CheckoutManuallyRepository checkoutManuallyRepository;
    @Autowired
    private ReservationScheduleRepository reservationScheduleRepository;
    @Autowired
    private MQTT mqttUtils;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ExternalApiClient externalApiClient;
    @Autowired
    private ExternalWallet externalWallet;
    @Autowired
    private RecordHistoryRepository recordHistoryRepository;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TimeUtils timeUtils;
    @Autowired
    private JWToken jwToken;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ParkingRepository parkingRepository;
    @Autowired
    private CloudinaryService cloudinaryService;



    @Override
    public ApiResponse checkoutManually(UUID RecordId, String token) {

        return null;
    }

    @Override
    @Transactional
    public ApiResponse checkin(UUID deviceId, UUID accountId,UUID sessionId, long timestamp,String token,UUID parkingId) {
        String MQTTTopic = "checkin:" + parkingId;

        //lấy session id( khi ocr service thực hiện thì sẽ send img lên trên cloudinary và đặt tên ảnh là sessionId
        // sau đó đồng thời post lên cache lên bao gồm sessionId và biển số. parking service kéo về . khi hợp lệ thì lưu vào db,
//        UUID targetSessionId = sessionId;

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

        // code xu ly khong dat truoc////////////////////////////////////////////////////////
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
                String licensePlate = getLCPFromCache(sessionId);
                if (licensePlate == null) {
                    throw new RuntimeException("Không tìm thấy biển số xe trong cache!");
                }
                System.out.println("Biển số xe lấy từ cache: " + licensePlate);
//                final String LPlate = "test plate";
                Record newRecord =  datn.service.parking.entity.Record.builder()
                        .parking(targetParking)
                        .account(targetAcc)
                        .spotIndex(0)
                        .lPlateNumber(licensePlate)
                        .imageUrlIn(cloudinaryService.getImageUrl(String.valueOf(sessionId)))
                        .sessionId(sessionId)
                        .entryTime(Instant.now())
                        .build();
                // mo barie vao bai khong dat truo
                System.out.println("mo barie, vao bai khong dat truoc ben phai");
                mqttUtils.sendMessagetoTopic("1",MQTTTopic);
                final String message = "checkin thanh cong khong dat truoc voi bien so "+ licensePlate;
                recordRepository.save(newRecord);
                // lưu accountId vào cache -> phục vụ ra vào bãi
                ApiResponse response = ApiResponse.builder()
                        .code(1000)
                        .isSucess(true)
                        .message(message)
                        .build();
                return  response;


            }
            // lay data tu trong redis bang cach tim theo session id
            String licensePlate = getLCPFromCache(sessionId);
            if (licensePlate == null) {
                throw new RuntimeException("Không tìm thấy biển số xe trong cache!");
            }
            System.out.println("Biển số xe lấy từ cache: " + licensePlate);
//            final String LPlate = "test plate";
            Record newRecord =  datn.service.parking.entity.Record.builder()
                    .parking(targetParking)
                    .account(targetAcc)
                    .spotIndex(0)
                    .imageUrlIn(cloudinaryService.getImageUrl(String.valueOf(sessionId)))

                    .lPlateNumber(licensePlate)
                    .sessionId(sessionId)
                    .entryTime(Instant.now())
                    .build();
            // mo barie vao bai khong dat truo
            System.out.println("mo barie, vao bai khong dat truoc ben phai");
            mqttUtils.sendMessagetoTopic("1",MQTTTopic);
            targetParking.setDirectSpacesAvailible(targetParking.getDirectSpacesAvailible() -1);
            parkingRepository.save(targetParking);
            final String message = "checkin thanh cong khong dat truoc voi bien so "+ licensePlate;
            recordRepository.save(newRecord);
            // lưu accountId vào cache -> phục vụ ra vào bãi
            ApiResponse response = ApiResponse.builder()
                    .code(1000)
                    .isSucess(true)
                    .message(message)
                    .build();
            return  response;

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
            if (duration.getSeconds() > 30){
                flag = false;
            }
            if(duration.getSeconds() < -30){
                flag = false;
            }
            // check neu muon hon qua lau => throw va set booking -> valid: false: li do la muon gio qua lau
            if(!flag){

                throw new RuntimeException(" thoi gian checkin khong hop le do som/ muon hon thoi gian dat lich qua lau");
            }
            if(!targetReservation.getValid()){
                var message = "khong hop le do:" + targetReservation.getMoreInfor();
                throw new RuntimeException(message);
            }

            Parking targetParkingBooking = targetReservation.getParking();
            if(!Objects.equals(targetParking.getId(), targetParkingBooking.getId())){
                // vao nham bai, mo barie di ra ngoai
                mqttUtils.sendMessagetoTopic("2",MQTTTopic);
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

//                final String LPlate = "test plate";
                // lay data tu trong redis bang cach tim theo session id
                String licensePlate = getLCPFromCache(sessionId);
                if (licensePlate == null) {
                    throw new RuntimeException("Không tìm thấy biển số xe trong cache!");
                }
                System.out.println("Biển số xe lấy từ cache: " + licensePlate);
                Record newRecord =  datn.service.parking.entity.Record.builder()
                        .parking(targetParkingBooking)
                        .account(targetAcc)
                        .spotIndex(0)
                        .imageUrlIn(cloudinaryService.getImageUrl(String.valueOf(sessionId)))

                        .sessionId(sessionId)
                        .lPlateNumber(licensePlate)
                        .entryTime(Instant.now())
                        .reservationSchedule(targetReservation)
                        .build();
                // mo barie
                final String message = "checkin thanh cong voi bien so "+ licensePlate;
                targetParking.setDirectSpacesAvailible(targetParking.getReservedSpacesAvailible() -1);
                parkingRepository.save(targetParking);
                recordRepository.save(newRecord);
                System.out.println("mo barie, vao bai ben trai- dat truoc");
                mqttUtils.sendMessagetoTopic("1",MQTTTopic);


                ApiResponse response = ApiResponse.builder()
                        .code(1000)
                        .isSucess(true)
                        .message(message)
                        .build();
                return  response;
            }
        }

//        return null;
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
        String MQTTTopic = "checkout:" + parkingId;
        System.out.println("entry in");
        System.out.println("check out");

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
        // check xem co dang gui xe khong - lay ra record
        var record = recordRepository.findByAccount(targetAcc).orElse(null);
        if(!Objects.nonNull(record)){
            throw new RuntimeException("Khong tim thay record gui xe, kiem tra lai");
        }
        // check xem dung bai do khong
        if (!Objects.equals(record.getParking().getId(),parkingId)){
            throw new RuntimeException("khong dung bai do, kiem tra lai");
        }
        // ocr - check voi bien so da gui
        String licensePlate = getLCPFromCache(sessionId);
        if (licensePlate == null) {
            throw new RuntimeException("Không tìm thấy biển số xe trong cache!");
        }
        System.out.println("Biển số xe lấy từ cache: " + licensePlate);

        if(!Objects.equals(record.getLPlateNumber(), licensePlate)){
            // nếu không khớp, update ảnh
            String imgUrlOut = cloudinaryService.getImageUrl(String.valueOf(sessionId));
            record.setImageUrlOut(imgUrlOut);
            recordRepository.save(record);

            // send chụp lại
            mqttUtils.sendMessagetoTopic(String.valueOf(UUID.randomUUID()),"CameraReceiver_checkout");
            throw new RuntimeException("Sai biển số xe");
        }

        // neu dung thi tru tien
        // kiêm tra tg gửi
        int targetDaytime = timeUtils.dayTime(record.getEntryTime());
        int targetNighttime =timeUtils.nightTime(record.getEntryTime());
        Price price = parking.getData().getPrice();
        // tru tien tk*****************************************************************************************
        long totalcost = targetNighttime* price.getNightTimeRate() + targetDaytime* price.getDayTimeRate();
        // check xem dat truoc hay khong
//        if()
        //check reservation

        var targetReservation = reservationScheduleRepository.findByAccount(targetAcc).orElse(null);
        if(Objects.nonNull(targetReservation)){
             reservationScheduleRepository.delete(targetReservation);
             totalcost-= 20000;
        }

//        targetParking.set
//        parkingRepository.save(targetParking);
        RecordHistory newRecordHistory = RecordHistory.builder()
                .id(record.getId())
                .dayTime(targetDaytime)
                .nightTime(targetNighttime)
                .account(record.getAccount())
                .spotIndex(record.getSpotIndex())
                .sessionId(sessionId)
                .imageUrlIn(record.getImageUrlIn())
                .imageUrlOut(record.getImageUrlOut())
                .lPlateNumber(record.getLPlateNumber())
                .entryTime(record.getEntryTime())
                .endTime(Instant.now())
                .parking(record.getParking())
                .totalCost(totalcost)
                .build();
        recordHistoryRepository.save(newRecordHistory);
        recordRepository.delete(record);
        datn.service.parking.dto.ApiResponse walletRes = externalWallet.makeNewTrans(SubBalanceRequest.builder()
                .accountId(accountId)
                .balance((int) totalcost)
                .build());
        if(!walletRes.isSucess()){
            throw new RuntimeException("fail");
        }
        log.warn("gui api ok");
        String message = "check out voi bien so " + licensePlate + "so tien la" + totalcost + "VND";
        mqttUtils.sendMessagetoTopic("1",MQTTTopic);

        ApiResponse response = new ApiResponse().builder()
                .code(1000)
                .isSucess(true)
                .message(message)
                .data("ok")
                .build();
        return response;
    }

    @Override
    public String getLCPFromCache(UUID sessionId) {
        // Tạo key theo định dạng ocrCache:{id}
        String key = "ocrCache:" + sessionId;

        // Lấy giá trị từ Redis bằng key
        String licensePlate = redisTemplate.opsForValue().get(key);


        return licensePlate;
    }
    @Override
    public String getAccountIdFromCache(UUID AccountId) {
        // Tạo key theo định dạng ocrCache:{id}
        String key = "checkoutManually:" + AccountId;

        // Lấy giá trị từ Redis bằng key
        String licensePlate = redisTemplate.opsForValue().get(key);

        return licensePlate;
    }

    @Override
    public ApiResponse currentDetail(String token) {
        var targetAcc = accountRepository.findById(jwToken.getIdFromToken(token)).orElse(null);
        if(Objects.isNull(targetAcc)){
            throw new RuntimeException("fail");
        }
        var record = recordRepository.findByAccount(targetAcc);
        var response = new ApiResponse().builder()
                .code(1000)
                .isSucess(true)
                .data(record)
                .build();
        return response;
    }

    @Override
    @Transactional
    public ApiResponse userCheckoutManually(String token) {
        // check record hiện tại xem hết bao nhiêu tiền
        var targetAccId = jwToken.getIdFromToken(token);
        var targetAcc = accountRepository.findById(targetAccId).orElse(null);
        if(Objects.isNull(targetAcc)){
            throw new RuntimeException("Acc not exist");
        }
        var targetRecord = recordRepository.findByAccount(targetAcc).orElse(null);
        if(Objects.isNull(targetRecord)){
            throw new RuntimeException("record not exist");
        }
        var targetPrice = targetRecord.getParking().getPrice();
        var checkinTime = targetRecord.getEntryTime();
        var dayTime = timeUtils.dayTime(checkinTime);
        var nightTime = timeUtils.nightTime(checkinTime);
        var totalCostEst = dayTime*targetPrice.getDayTimeRate() + nightTime*targetPrice.getNightTimeRate();
        // kiểm tra tài khoản đủ tiền
        var targetWallet = externalWallet.getDetailWallet(token);
        if(totalCostEst > targetWallet.getData().getBalance()){
            String throwStr = "vui lòng nạp thêm tối thiểu: " + (totalCostEst - targetWallet.getData().getBalance());
            throw new RuntimeException(throwStr);
        }
        // lưu data ở đâu để cho admin xác nhận - redis
        String key = "checkoutManually:" + targetAccId;

        redisService.saveToRedis(key,targetRecord.getId(),600);
        CheckoutManually record = new CheckoutManually().builder()
                .accountId(targetAccId)
                .record(targetRecord)
                .imageUrlIn(targetRecord.getImageUrlIn())
                .imageUrlOut(targetRecord.getImageUrlOut())
                .isDone(false)
                .staffId(targetRecord.getParking().getAccount().getId())
                .parking(targetRecord.getParking())
                .build();
        checkoutManuallyRepository.save(record);
        var response = new ApiResponse<>().builder()
                .code(1000)
                .isSucess(true)
                .message("vui lòng chờ admin xác nhận")
                .build();
        return response;
    }

    @Override
    @PreAuthorize("hasAuthority('STAFF')")
    @Transactional
    public ApiResponse confirmCheckoutManually(String token, UUID recordCM) {
        // không phải parking mà là recordCheckoutManually
        // lấy data trong repo
        var targetRecordCheckoutManually = checkoutManuallyRepository.findById(recordCM).orElse(null);
        if(Objects.isNull(targetRecordCheckoutManually)){
            throw new RuntimeException("cant find record %%");
        }
        var targetCheckoutRecord = targetRecordCheckoutManually.getRecord();
//        GetParkingResponse parking = externalApiClient.getParkingInfor(token,parkingId,"internalApikey");
//        var targetParking = parking.getData();

        // lấy data từ redis
        // tìm record



        //
        int targetDaytime = timeUtils.dayTime(targetCheckoutRecord.getEntryTime());
        int targetNighttime =timeUtils.nightTime(targetCheckoutRecord.getEntryTime());
//        Price price = parking.getData().getPrice();
        Price price = targetCheckoutRecord.getParking().getPrice();
        // tru tien tk*****************************************************************************************
        long totalcost = targetNighttime* price.getNightTimeRate() + targetDaytime* price.getDayTimeRate();

        RecordHistory newRecordHistory = RecordHistory.builder()
                .id(targetCheckoutRecord.getId())
                .dayTime(targetDaytime)
                .nightTime(targetNighttime)
                .account(targetCheckoutRecord.getAccount())
                .spotIndex(targetCheckoutRecord.getSpotIndex())
                .imageUrlIn(targetCheckoutRecord.getImageUrlIn())
                .imageUrlOut(targetCheckoutRecord.getImageUrlOut())
                .sessionId(targetRecordCheckoutManually.getRecord().getSessionId())
                .lPlateNumber(targetCheckoutRecord.getLPlateNumber())
                .entryTime(targetCheckoutRecord.getEntryTime())
                .endTime(Instant.now())
                .parking(targetCheckoutRecord.getParking())
                .totalCost(totalcost)
                .build();
        recordHistoryRepository.save(newRecordHistory);
        targetRecordCheckoutManually.setIsDone(true);
        targetRecordCheckoutManually.setRecord(null);
        targetRecordCheckoutManually.setRecordHistory(newRecordHistory);

        recordRepository.delete(targetCheckoutRecord);
        datn.service.parking.dto.ApiResponse walletRes = externalWallet.makeNewTrans(SubBalanceRequest.builder()
                .accountId(targetRecordCheckoutManually.getAccountId())
                .balance((int) totalcost)
                .build());
        if(!walletRes.isSucess()){
            throw new RuntimeException("fail");
        }
        log.warn("gui api ok");
        String message = "check out voi bien so " + targetCheckoutRecord.getLPlateNumber() + "so tien la" + totalcost + "VND";
        String MQTTTopic = "checkout:" + targetCheckoutRecord.getParking().getId();
        mqttUtils.sendMessagetoTopic("1",MQTTTopic);
        ApiResponse response = new ApiResponse().builder()
                .code(1000)
                .isSucess(true)
                .message(message)
                .data("done checkout manually")
                .build();
        return response;
    }

    @Override
    @PreAuthorize("hasAuthority('STAFF')")
    public ApiResponse getAllmanually(String token) {
        var targetAccountId = jwToken.getIdFromToken(token);
        var ListManually = checkoutManuallyRepository.findAllByStaffId(targetAccountId);
        ApiResponse response = new ApiResponse().builder()
                .code(1000)
                .isSucess(true)
                .message("list manually")
                .data(ListManually)
                .build();

        return response;
    }


}





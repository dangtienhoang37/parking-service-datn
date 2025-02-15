package datn.service.parking.service.Impl;


import com.netflix.discovery.converters.Auto;
import datn.service.parking.apiService.ExternalApiClient;
import datn.service.parking.dto.response.ApiResponse;
import datn.service.parking.dto.response.GetParkingResponse;
import datn.service.parking.dto.response.getAccountResponse;
import datn.service.parking.enumvar.Role;
import datn.service.parking.repository.AccountRepository;
import datn.service.parking.repository.RecordRepository;
import datn.service.parking.service.RecordService;
import datn.service.parking.utils.JWToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class RecordServiceImpl implements RecordService {
    @Autowired
    private JWToken jwToken;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ExternalApiClient externalApiClient;
    @Autowired
    private RecordRepository recordRepository;

    @Override
    public ApiResponse getAllByParking(UUID parkingId, String token) {
        var accountId = jwToken.getIdFromToken(token);
        getAccountResponse account = externalApiClient.getUserInfo(token,"internalApikey");
        var targetAcc = account.getData();
        // check ton  tại tài khoản
        if(Objects.isNull(targetAcc)){
            throw new RuntimeException("account id not valid");
        }
        if(targetAcc.getRole() == Role.USER){
            throw new RuntimeException("you dont have permission1");
        }
        if(targetAcc.getRole() == Role.ADMIN){
            var listRecord = recordRepository.findAllByParkingId(parkingId);
            new ApiResponse<>();
            return ApiResponse.builder()
                    .code(1000)
                    .isSucess(true)
                    .message("get all record sucessfully")
                    .data(listRecord)
                    .build();

        }

        GetParkingResponse parking = externalApiClient.getParkingInfor(token, parkingId, "internalApikey");
        var targetParking = parking.getData();
        System.out.println(accountId);
        System.out.println(123123);
        System.out.println(targetParking.getAccount().getId());

        if (!Objects.equals(accountId,targetParking.getAccount().getId())) {
            throw new RuntimeException("you dont have permission2");
        }
        var listRecord = recordRepository.findAllByParkingId(parkingId);
        new ApiResponse<>();
        return ApiResponse.builder()
                .code(1000)
                .isSucess(true)
                .message("get all record sucessfully")
                .data(listRecord)
                .build();

    }

    @Override
    public ApiResponse getDetailRecord(UUID RecordId, String token) {
        var role = jwToken.getRoleFromToken(token);
        var targetAcc = accountRepository.findById(jwToken.getIdFromToken(token)).orElse(null);
        if(Objects.isNull(targetAcc)){
            throw new RuntimeException("cant find acc");
        }
        if(Objects.equals(role, "USER")){
            throw new RuntimeException("you dont have permission");
        }
        var record = recordRepository.findById(RecordId).orElse(null);
        if(Objects.isNull(record)){
            throw new RuntimeException("cant find record");
        }
        if(Objects.equals(record.getParking().getAccount(),targetAcc) || Objects.equals(role, "ADMIN")){
            var response = new ApiResponse().builder()
                    .code(1000)
                    .isSucess(true)
                    .data(record)
                    .build();
            return response;
        }

        throw new RuntimeException("dont have permissions");
    }
}

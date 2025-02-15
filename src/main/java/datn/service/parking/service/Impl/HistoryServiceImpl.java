package datn.service.parking.service.Impl;

import datn.service.parking.dto.response.ApiResponse;
import datn.service.parking.repository.AccountRepository;
import datn.service.parking.repository.RecordHistoryRepository;
import datn.service.parking.service.HistoryService;
import datn.service.parking.utils.JWToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class HistoryServiceImpl implements HistoryService {
    @Autowired
    private RecordHistoryRepository recordHistoryRepository;
    @Autowired
    private JWToken jwToken;
    @Autowired
    private AccountRepository accountRepository;
    @Override
    public ApiResponse getHistory(String token) {
        var AccountId = jwToken.getIdFromToken(token);
        var history = recordHistoryRepository.findAllByAccount(accountRepository.findById(AccountId).orElse(null));
        ApiResponse response = ApiResponse.builder()
                .code(1000)
                .message("")
                .isSucess(true)
                .data(history)
                .build();
        return  response;
    }

    @Override
    public ApiResponse detailHistory(UUID id) {
        var history = recordHistoryRepository.findById(id);
        ApiResponse response = ApiResponse.builder()
                .code(1000)
                .message("")
                .isSucess(true)
                .data(history)
                .build();
        return  response;
    }
}

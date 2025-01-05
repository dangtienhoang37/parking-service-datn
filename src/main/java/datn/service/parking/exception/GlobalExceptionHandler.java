package datn.service.parking.exception;

import datn.service.parking.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception) {

        String message = exception.getMessage();
        if (message.contains(":")) {
            message = message.substring(message.lastIndexOf(":") + 1).trim();
        }
        ApiResponse<String> res = new ApiResponse<>();
        res.setCode(1001);
        res.setMessage(message);
//        res.setResult(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<String> response = new ApiResponse<>();
        response.setData(null);
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @ExceptionHandler(value = CustomException.class)
//    ResponseEntity<ApiResponse> handlingCustomException(CustomException exception) {
//        ErrorCode errorCode = exception.getErrorCode();
//
//        ApiResponse<String> res = new ApiResponse<>();
//        res.setCode(errorCode.getCode());
//        res.setMessage(errorCode.getMessage());
//        res.setResult(null);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
//    }
}

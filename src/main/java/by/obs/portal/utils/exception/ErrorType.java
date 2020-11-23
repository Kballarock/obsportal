package by.obs.portal.utils.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ErrorType {
    APP_ERROR("error.appError", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("error.pageNotFound", HttpStatus.NOT_FOUND),
    DATA_NOT_FOUND("error.dataNotFound", HttpStatus.UNPROCESSABLE_ENTITY),
    DATA_ERROR("error.dataError", HttpStatus.CONFLICT),
    VALIDATION_ERROR("error.validationError", HttpStatus.UNPROCESSABLE_ENTITY);

    String errorCode;
    HttpStatus status;

    ErrorType(String errorCode, HttpStatus status) {
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
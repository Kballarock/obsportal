package by.obs.portal.utils.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ApplicationException extends RuntimeException {

    ErrorType type;
    String msgCode;
    String[] args;

    ApplicationException(ErrorType type, String msgCode, String... args) {
        super(String.format("type=%s, msgCode=%s, args=%s", type, msgCode, Arrays.toString(args)));
        this.type = type;
        this.msgCode = msgCode;
        this.args = args;
    }
}
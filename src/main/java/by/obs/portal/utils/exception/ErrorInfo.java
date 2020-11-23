package by.obs.portal.utils.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ErrorInfo {
    String url;
    ErrorType type;
    String typeMessage;
    String[] details;

    public ErrorInfo(CharSequence url, ErrorType type, String typeMessage, String... details) {
        this.url = url.toString();
        this.type = type;
        this.typeMessage = typeMessage;
        this.details = Arrays.stream(details).distinct().toArray(String[]::new);
    }
}
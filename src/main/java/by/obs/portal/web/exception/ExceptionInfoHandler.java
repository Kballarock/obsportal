package by.obs.portal.web.exception;

import by.obs.portal.utils.exception.*;
import by.obs.portal.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Optional;

import static by.obs.portal.utils.exception.ErrorType.*;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionInfoHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MessageUtil messageUtil;
    public static final String EXCEPTION_DUPLICATE_EMAIL = "exception.user.duplicateEmail";
    public static final String EXCEPTION_DUPLICATE_ROLE = "exception.user.duplicateRole";
    public static final String REPORT_GENERATOR_DUPLICATE_EMAIL = "exception.rep.gen.duplicateEmail";

    private static final Map<String, String> CONSTRAINS_I18N_MAP =
            Map.of(
                    "users_unique_email_idx", EXCEPTION_DUPLICATE_EMAIL,
                    "users_role_idx", EXCEPTION_DUPLICATE_ROLE,
                    "report_generator_email_idx", REPORT_GENERATOR_DUPLICATE_EMAIL);

    @Autowired
    public ExceptionInfoHandler(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorInfo> applicationError(HttpServletRequest req, ApplicationException appEx) {
        var errorInfo = logAndGetErrorInfo(req, appEx, false, appEx.getType(), messageUtil.getMessage(appEx));
        return ResponseEntity.status(appEx.getType().getStatus()).body(errorInfo);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, HttpServletResponse res, DataIntegrityViolationException e) {
        var rootMsg = ValidationUtil.getRootCause(e).getMessage();
        if (rootMsg != null) {
            var lowerCaseMsg = rootMsg.toLowerCase();
            Optional<Map.Entry<String, String>> entry = CONSTRAINS_I18N_MAP.entrySet().stream()
                    .filter(it -> lowerCaseMsg.contains(it.getKey()))
                    .findAny();
            if (entry.isPresent()) {
                res.setStatus(422);
                return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, messageUtil.getMessage(entry.get().getValue()));
            }
        }
        res.setStatus(409);
        return logAndGetErrorInfo(req, e, true, DATA_ERROR);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ErrorInfo bindValidationError(HttpServletRequest req, Exception e) {
        var result = e instanceof BindException ?
                ((BindException) e).getBindingResult() : ((MethodArgumentNotValidException) e).getBindingResult();

        String[] details = result.getFieldErrors().stream()
                .map(messageUtil::getMessage)
                .toArray(String[]::new);

        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, details);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({IllegalRequestDataException.class,
            MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ErrorInfo illegalRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, APP_ERROR);
    }

    private ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e,
                                         boolean logException, ErrorType errorType, String... details) {
        Throwable rootCause = ValidationUtil.logAndGetRootCause(log, req, e, logException, errorType);
        return new ErrorInfo(req.getRequestURL(), errorType,
                messageUtil.getMessage(errorType.getErrorCode()),
                details.length != 0 ? details : new String[]{ValidationUtil.getMessage(rootCause)});
    }
}
package by.obs.portal.web.exception;

import by.obs.portal.utils.exception.MessageUtil;
import by.obs.portal.utils.exception.ApplicationException;
import by.obs.portal.utils.exception.ErrorType;
import by.obs.portal.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.IllformedLocaleException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MessageUtil messageUtil;

    @Autowired
    public GlobalExceptionHandler(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView pageNotFoundHandler(HttpServletRequest req, NoHandlerFoundException e) {
        return logAndGetExceptionView(req, e, false, ErrorType.NOT_FOUND, null);
    }

    @ExceptionHandler(ApplicationException.class)
    public ModelAndView applicationErrorHandler(HttpServletRequest req, ApplicationException appEx) {
        return logAndGetExceptionView(req, appEx, true, appEx.getType(), messageUtil.getMessage(appEx));
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
        log.error("Exception at request " + req.getRequestURL(), e);
        return logAndGetExceptionView(req, e, true, ErrorType.APP_ERROR, null);
    }

    @ExceptionHandler(IllformedLocaleException.class)
    public ModelAndView localeHandler(HttpServletRequest req, IllformedLocaleException e) {
        log.error("Exception at request " + req.getRequestURL(), e);
        return logAndGetExceptionView(req, e, true, ErrorType.APP_ERROR, null);
    }

    private ModelAndView logAndGetExceptionView(HttpServletRequest req, Exception e, boolean logException,
                                                ErrorType errorType, String msg) {
        Throwable rootCause = ValidationUtil.logAndGetRootCause(log, req, e, logException, errorType);

        HttpStatus httpStatus = errorType.getStatus();
        ModelAndView mav = new ModelAndView("exception",
                Map.of("exception", rootCause,
                        "message", msg != null ? msg : ValidationUtil.getMessage(rootCause),
                        "typeMessage", messageUtil.getMessage(errorType.getErrorCode()),
                        "status", " " + httpStatus));
        mav.setStatus(httpStatus);
        return mav;
    }
}
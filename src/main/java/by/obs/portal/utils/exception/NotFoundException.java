package by.obs.portal.utils.exception;

public class NotFoundException extends ApplicationException {
    private static final String NOT_FOUND_EXCEPTION = "exception.common.notFound";

    public NotFoundException(String arg) {
        super(ErrorType.DATA_NOT_FOUND, NOT_FOUND_EXCEPTION, arg);
    }
}
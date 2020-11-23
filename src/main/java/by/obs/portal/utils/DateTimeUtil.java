package by.obs.portal.utils;

import by.obs.portal.common.Constants;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);
    private static final LocalDateTime MIN_DATE = LocalDateTime.of(2019, 1, 1, 0, 0);
    private static final LocalDateTime MAX_DATE = LocalDateTime.of(2100, 1, 1, 0, 0);

    private static Clock clock = Clock.systemDefaultZone();
    private static ZoneId zoneId = ZoneId.systemDefault();

    public static String toString(LocalDate ld) {
        return ld == null ? "" : ld.format(DATE_FORMATTER);
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static @Nullable
    LocalDate parseLocalDate(@Nullable String str) {
        return StringUtils.isEmpty(str) ? null : LocalDate.parse(str);
    }

    public static @Nullable
    LocalTime parseLocalTime(@Nullable String str) {
        return StringUtils.isEmpty(str) ? null : LocalTime.parse(str);
    }

    public static LocalDate getStartInclusive(LocalDate localDate) {
        return localDate != null ? localDate : MIN_DATE.toLocalDate();
    }

    public static LocalDate getEndInclusive(LocalDate localDate) {
        return localDate != null ? localDate : MAX_DATE.toLocalDate();
    }

    public static LocalDate getCurrentDate(LocalDate localDate) {
        return localDate != null ? localDate : LocalDate.now();
    }

    public static void useFixedClockAt(LocalDateTime date) {
        clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId);
    }

    public static LocalDate now() {
        return LocalDate.now(getClock());
    }

    public static LocalTime nowTime() {
        return LocalTime.now(getClock());
    }

    private static Clock getClock() {
        return clock;
    }
}
package sagan.support;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * A temporary helper class to facilitate a phased migration to the JDK 8 Date/Time API.
 */
public class DateConverter {

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.of(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), date.getHours(),
                date.getMinutes());
    }

    public static Date toDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getYear() - 1900, date.getMonthValue() - 1, date.getDayOfMonth(), date.getHour(),
                date.getMinute());
    }

    public static Date toZonedDate(LocalDateTime date, TimeZone zone) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(zone);
        calendar.set(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth(), date.getHour(),
                date.getMinute());
        return calendar.getTime();
    }
}

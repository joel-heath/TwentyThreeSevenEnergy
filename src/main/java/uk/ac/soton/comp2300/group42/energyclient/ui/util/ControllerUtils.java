package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import javafx.util.StringConverter;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public class ControllerUtils {

    public static boolean isDebugging() {
        return ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .stream()
                .anyMatch(arg -> arg.contains("jdwp"));
    }

    public static <T> StringConverter<T> createConverter(Function<T, String> nameExtractor) {
        return new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : nameExtractor.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null; // Not needed for non-editable ComboBoxes
            }
        };
    }

    public static String formatDay(LocalDateTime targetDateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime targetDay = targetDateTime.truncatedTo(ChronoUnit.DAYS);
        long daysBetween = ChronoUnit.DAYS.between(today, targetDay);

        if (daysBetween == 0) {
            return "Today";
        } else if (daysBetween == 1) {
            return "Tomorrow";
        }

        if (daysBetween > 1 && daysBetween < 7) {
            return targetDay.getDayOfWeek().toString()
                    .substring(0, 1).toUpperCase() +
                    targetDay.getDayOfWeek().toString().substring(1).toLowerCase();
        }

        /* Could keep this, but can get confusing and ambiguous
           (e.g., it is Wednesday; does "Next Tuesday" mean in 6 days or 13 days?)

        if (daysBetween >= 7 && daysBetween < 14) {
            String dayName = targetDay.getDayOfWeek().toString();
            return "Next " + dayName.substring(0, 1).toUpperCase() +
                    dayName.substring(1).toLowerCase();
        }
         */

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return targetDateTime.format(formatter);
    }
}

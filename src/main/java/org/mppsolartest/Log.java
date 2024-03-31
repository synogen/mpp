package org.mppsolartest;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Log {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS O")
            .withZone(ZoneId.systemDefault());
    public static boolean showLogCaller = false;
    public static void log(String logMessage) {
        var logElements = new ArrayList<String>();

        logElements.add(formatter.format(Instant.now()));
        if (showLogCaller) {
            var stacktrace = Thread.currentThread().getStackTrace();
            if (stacktrace.length > 2) logElements.add(stacktrace[2].toString());
        }
        logElements.add(logMessage);

        System.out.println(logElements.stream().collect(Collectors.joining(" ")));
    }
}

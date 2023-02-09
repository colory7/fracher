package com.colory7.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtil {

    private static final String DATE_FORMAT_STR = "yyyyMMdd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_STR);

    public static String today() {
        LocalDate today = LocalDate.now();
        return DATE_FORMATTER.format(today);
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}

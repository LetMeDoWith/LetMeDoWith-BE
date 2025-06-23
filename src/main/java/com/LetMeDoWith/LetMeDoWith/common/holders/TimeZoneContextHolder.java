package com.LetMeDoWith.LetMeDoWith.common.holders;

import java.time.ZoneId;
import java.util.Optional;

public class TimeZoneContextHolder {

    private static final ThreadLocal<ZoneId> timeZoneHolder = new ThreadLocal<>();
    private static final ZoneId DEFAULT_TIME_ZONE_ID = ZoneId.of("Asia/Seoul");

    public static ZoneId getTimeZone() {
        return Optional.ofNullable(timeZoneHolder.get()).orElse(DEFAULT_TIME_ZONE_ID);
    }

    public static void setTimeZone(ZoneId zoneId) {
        timeZoneHolder.set(zoneId);
    }

    public static void clearTimeZoneHolder() {
        timeZoneHolder.remove();
    }
}

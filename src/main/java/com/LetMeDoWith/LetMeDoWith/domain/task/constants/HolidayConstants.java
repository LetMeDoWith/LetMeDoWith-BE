package com.LetMeDoWith.LetMeDoWith.domain.task.constants;

import java.time.MonthDay;
import java.util.Arrays;
import java.util.List;

public class HolidayConstants {

    public static final List<MonthDay> FIXED_HOLIDAYS = Arrays.asList(
            MonthDay.of(1, 1), // 신정
            MonthDay.of(3, 1), // 삼일절
            MonthDay.of(5, 5), // 어린이날
            MonthDay.of(6, 6), // 현충일
            MonthDay.of(8, 15), // 광복절
            MonthDay.of(10, 3), // 개천절
            MonthDay.of(10, 9) // 한글날
    );
}
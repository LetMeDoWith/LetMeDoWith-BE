package com.LetMeDoWith.LetMeDoWith.common.provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface TimeProvider {
    
    LocalDateTime now();
    
    LocalDate nowDate();
    
    LocalTime nowTime();
    
}

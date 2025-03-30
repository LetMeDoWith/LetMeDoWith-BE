package com.LetMeDoWith.LetMeDoWith.common.provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class SystemTimeProvider implements TimeProvider {
    
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    @Override
    public LocalDate nowDate() {
        return LocalDate.now();
    }
    
    @Override
    public LocalTime nowTime() {
        return LocalTime.now();
    }
    
}

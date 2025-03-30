package com.LetMeDoWith.LetMeDoWith.common.provider;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class ClockTimeProvider implements TimeProvider {
    
    private Clock clock = Clock.systemDefaultZone();
    
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
    
    @Override
    public LocalDate nowDate() {
        return LocalDate.now(clock);
    }
    
    @Override
    public LocalTime nowTime() {
        return LocalTime.now(clock);
    }
    
    public void setClock(Clock clock) {
        this.clock = clock;
    }
    
    public void resetClock() {
        this.clock = Clock.systemDefaultZone();
    }
    
}

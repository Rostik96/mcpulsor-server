package dev.rost.mcpulsorserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
class PulseCalculator {

    private static final Random random = new Random();


    int getPulse(String name) {
        var subject = name != null ? name : "John Doe";
        log.info("We're starting to check pulse for {}", subject);
        var pulse = random.nextInt(100) + 1;
        log.info("{} pulse is {} bpm", subject, pulse);
        return pulse;
    }
}

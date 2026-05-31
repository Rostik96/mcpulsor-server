package dev.rost.mcpulsorserver;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
class BioSensor {

    private final PulseCalculator pulseCalculator;

    @McpTool(
            name = "bioSensor",
            title = "Human Vital Pulse Sensor",
            description = "Returns the current heart rate and related health indicators")
    Map<String, Object> readPulse(
            @McpToolParam(description = "Number of past days to include in the pulse reading request") int days) {
        return Map.of(
                "pulse", pulseCalculator.getPulse(null),
                "state", "нормально",
                "sleepDeprivation", true);
    }
}

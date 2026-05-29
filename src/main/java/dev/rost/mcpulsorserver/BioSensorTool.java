package dev.rost.mcpulsorserver;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
class BioSensorTool implements Tool {

    private final McpJsonMapper mapper;
    @Value("classpath:schemas/bioSensorInput.json")
    private final ClassPathResource inputSchema;
    @Value("classpath:schemas/bioSensorOutput.json")
    private final ClassPathResource outputSchema;


    @Override
    @SneakyThrows
    public McpSchema.Tool tool() {
        return McpSchema.Tool.builder()
                .name("bioSensor")
                .title("Human Vital Pulse Sensor")
                .description("Returns the current heart rate of the user as a simple string value")
                .inputSchema(mapper, inputSchema.getContentAsString(StandardCharsets.UTF_8))
                .outputSchema(mapper, outputSchema.getContentAsString(StandardCharsets.UTF_8))
                .build();
    }

    @Override
    public McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        return McpSchema.CallToolResult.builder()
                .structuredContent(Map.of(
                        "pulse", 128,
                        "state", "нормально",
                        "sleepDeprivation", true))
                .build();
    }
}

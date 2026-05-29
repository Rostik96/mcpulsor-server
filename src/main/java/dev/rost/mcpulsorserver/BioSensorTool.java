package dev.rost.mcpulsorserver;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
class BioSensorTool implements McpTool {

    private final McpJsonMapper mapper;
    @Value("classpath:schemas/bioSensorInput.json")
    private final Resource inputSchema;
    @Value("classpath:schemas/bioSensorOutput.json")
    private final Resource outputSchema;


    @Override
    @SneakyThrows
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder()
                .name("bioSensor")
                .title("Human Vital Pulse Sensor")
                .description("Returns the current heart rate and related health indicators")
                .inputSchema(mapper, inputSchema.getContentAsString(StandardCharsets.UTF_8))
                .outputSchema(mapper, outputSchema.getContentAsString(StandardCharsets.UTF_8))
                .build();
    }

    @Override
    public McpSchema.CallToolResult execute(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        return McpSchema.CallToolResult.builder()
                .structuredContent(Map.of(
                        "pulse", 128,
                        "state", "нормально",
                        "sleepDeprivation", true))
                .build();
    }
}

package dev.rost.mcpulsorserver;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.SamplingMessage;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@RequiredArgsConstructor
class Diagnostator implements McpTool {

    private final McpJsonMapper mapper;
    @Value("classpath:tool/diagnostator/input.json")
    private final Resource inputSchema;
    @Value("classpath:tool/diagnostator/system-prompt.txt")
    private final Resource systemPrompt;

    private final PulseCalculator pulseCalculator;
    private final  MedicalProfileProvider medicalProfileProvider;


    @Override
    @SneakyThrows
    public McpSchema.CallToolResult execute(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        log.info("OK, let's go!");
        log.info("Can the client do sampling: {}", exchange.getClientCapabilities().sampling() != null);
        var name = String.valueOf(request.arguments().get("name"));
        var samplingPrompt = "this is our patient, here is his medical chart %s and here is his current pulse: %s"
                .formatted(medicalProfileProvider.getMedicalProfile(name), pulseCalculator.getPulse(name));
        log.info("sampling prompt: {}", samplingPrompt);
        var samplingRequest = McpSchema.CreateMessageRequest.builder()
                .systemPrompt(systemPrompt.getContentAsString(UTF_8))
                .temperature(0.1)
                .maxTokens(50)
                .messages(List.of(new SamplingMessage(McpSchema.Role.USER, new TextContent(samplingPrompt))))
                .build();
        return McpSchema.CallToolResult.builder()
                .addContent(exchange.createMessage(samplingRequest).content())
                .build();
    }

    @Override
    @SneakyThrows
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder()
                .name("diagnostator")
                .title("Diagnostic by name")
                .description("Used to get a diagnosis by a person’s name. Always returns either the name of a disease or a message that the person has no diseases.")
                .inputSchema(mapper, inputSchema.getContentAsString(UTF_8))
                .build();
    }
}

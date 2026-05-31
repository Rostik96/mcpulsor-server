package dev.rost.mcpulsorserver;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.SamplingMessage;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
class Diagnostator {

    @Value("classpath:tool/diagnostator/system-prompt.txt")
    private final Resource systemPrompt;
    private final PulseCalculator pulseCalculator;
    private final MedicalProfileProvider medicalProfileProvider;

    @McpTool(
            name = "diagnostator",
            title = "Diagnostic by name",
            description = "Used to get a diagnosis by a person’s name. Always returns either the name of a disease or a message that the person has no diseases.")
    @SneakyThrows
    String diagnose(
            McpSyncRequestContext ctx,
            @McpToolParam(description = "Name of the patient for whom the current diagnosis needs to be determined.") String name) {
        log.info("OK, let's go!");
        var samplingPrompt = "this is our patient, here is his medical chart %s and here is his current pulse: %s"
                .formatted(medicalProfileProvider.getMedicalProfile(name), pulseCalculator.getPulse(name));
        log.info("sampling prompt: {}", samplingPrompt);
        var messages = List.of(new SamplingMessage(McpSchema.Role.USER, new TextContent(samplingPrompt)));
        var samplingRequest = McpSchema.CreateMessageRequest.builder(messages, 50)
                .systemPrompt(systemPrompt.getContentAsString(UTF_8))
                .temperature(0.1)
                .build();
        return ((TextContent) ctx.sample(samplingRequest).content()).text();
    }
}

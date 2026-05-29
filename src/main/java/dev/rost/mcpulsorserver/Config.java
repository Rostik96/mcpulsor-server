package dev.rost.mcpulsorserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import jakarta.servlet.http.HttpServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
class Config {

    @Bean
    ApplicationListener<ApplicationReadyEvent> onStartup(List<SyncToolSpecification> tools) {
        return _ -> McpServer.sync(mcpTransportProvider())
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .build())
                .tools(tools)
                .build();
    }


    @Bean
    ServletRegistrationBean<HttpServlet> mcpServlet() {
        return new ServletRegistrationBean<>(mcpTransportProvider(), "/mcpulsor", "/mcpulsor/*");
    }


    @Bean
    SyncToolSpecification bioSensorTool(@Value("schemas/bioSensor.json") ClassPathResource bioSensorSchema) throws IOException {
        var inputSchema = bioSensorSchema.getContentAsString(StandardCharsets.UTF_8);
        return SyncToolSpecification.builder()
                .tool(McpSchema.Tool.builder()
                        .name("bioSensor")
                        .title("Human Vital Pulse Sensor")
                        .description("Returns the current heart rate of the user as a simple string value")
                        .inputSchema(jsonMapper(), inputSchema)
                        .build())
                .callHandler(((_, req) -> new CallToolResult(
                        "The user's heart rate for the last %s days was 128.".formatted(req.arguments().get("days")),
                        false)))
                .build();
    }


    @Bean
    HttpServletStreamableServerTransportProvider mcpTransportProvider() {
        return HttpServletStreamableServerTransportProvider.builder()
                .jsonMapper(jsonMapper())
                .mcpEndpoint("/mcpulsor")
                .build();
    }


    private McpJsonMapper jsonMapper() {
        return new JacksonMcpJsonMapper(new ObjectMapper());
    }
}

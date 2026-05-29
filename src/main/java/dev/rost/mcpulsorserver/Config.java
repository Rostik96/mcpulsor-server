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
import java.util.Map;

@Configuration
class Config {

    @Bean
    ApplicationListener<ApplicationReadyEvent> onStartup(List<Tool> tools) {
        return _ -> McpServer.sync(mcpTransportProvider())
                .capabilities(McpSchema.ServerCapabilities.builder().tools(true).build())
                .tools(tools.stream().map(Tool::instance).toList())
                .build();
    }


    @Bean
    ServletRegistrationBean<HttpServlet> mcpServlet() {
        return new ServletRegistrationBean<>(mcpTransportProvider(), "/mcpulsor", "/mcpulsor/*");
    }


    @Bean
    SyncToolSpecification bioSensorTool(@Value("classpath:schemas/bioSensorInput.json") ClassPathResource inputSchema,
                                        @Value("classpath:schemas/bioSensorOutput.json") ClassPathResource outputSchema) throws IOException {
        var mapper = jsonMapper();
        return SyncToolSpecification.builder()
                .tool(McpSchema.Tool.builder()
                        .name("bioSensor")
                        .title("Human Vital Pulse Sensor")
                        .description("Returns the current heart rate of the user as a simple string value")
                        .inputSchema(mapper, inputSchema.getContentAsString(StandardCharsets.UTF_8))
                        .outputSchema(mapper, outputSchema.getContentAsString(StandardCharsets.UTF_8))
                        .build())
                .callHandler(((_, _) -> CallToolResult.builder()
                        .structuredContent(Map.of(
                                "pulse", 128,
                                "state", "нормально",
                                "sleepDeprivation", true))
                        .build()))
                .build();
    }


    @Bean
    HttpServletStreamableServerTransportProvider mcpTransportProvider() {
        return HttpServletStreamableServerTransportProvider.builder()
                .jsonMapper(jsonMapper())
                .mcpEndpoint("/mcpulsor")
                .build();
    }


    @Bean
    McpJsonMapper jsonMapper() {
        return new JacksonMcpJsonMapper(new ObjectMapper());
    }
}

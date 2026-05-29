package dev.rost.mcpulsorserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.servlet.http.HttpServlet;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class Config {

    @Bean
    ApplicationListener<ApplicationReadyEvent> onStartup(List<McpTool> tools) {
        return _ -> McpServer.sync(mcpTransportProvider())
                .capabilities(McpSchema.ServerCapabilities.builder().tools(true).build())
                .tools(tools.stream().map(McpTool::specification).toList())
                .build();
    }


    @Bean
    ServletRegistrationBean<HttpServlet> mcpServlet() {
        return new ServletRegistrationBean<>(mcpTransportProvider(), "/mcpulsor", "/mcpulsor/*");
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

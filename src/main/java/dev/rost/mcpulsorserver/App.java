package dev.rost.mcpulsorserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

@Slf4j
class App {

    @SneakyThrows
    static void main() {
        log.info("Server Application Started");
        var transportProvider = HttpServletStreamableServerTransportProvider.builder()
                .mcpEndpoint("/mcpulsor")
                .build();
        var bioSensor = McpServerFeatures.SyncToolSpecification.builder()
                .tool(McpSchema.Tool.builder()
                        .name("bioSensor")
                        .title("Human Vital Pulse Sensor")
                        .description("Returns the current heart rate of the user as a simple string value")
                        .inputSchema(
                                new JacksonMcpJsonMapper(new ObjectMapper()),
                                new ObjectMapper().createObjectNode().put("type", "object").toString())
                        .build())
                .callHandler(((_, _) -> new McpSchema.CallToolResult("The user's pulse is 42.", false)))
                .build();
        McpServer
                .sync(transportProvider)
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .build())
                .tools(bioSensor)
                .build();
        var server = new Server(8090);
        var handler = new ServletContextHandler(SESSIONS);
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(transportProvider), "/*");
        server.setHandler(handler);
        server.start();
        server.join();
    }
}

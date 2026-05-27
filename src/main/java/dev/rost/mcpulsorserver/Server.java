package dev.rost.mcpulsorserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

class Server {

    static void main() {
        var jsonMapper = new JacksonMcpJsonMapper(new ObjectMapper());
        var transportProvider = new StdioServerTransportProvider(jsonMapper);
        var bioSensor = McpServerFeatures.SyncToolSpecification.builder()
                .tool(McpSchema.Tool.builder()
                        .name("bioSensor")
                        .title("Human Vital Pulse Sensor")
                        .description("Returns the current heart rate of the user as a simple string value")
                        .inputSchema(
                                jsonMapper,
                                new ObjectMapper().createObjectNode().put("type", "object").toString())
                        .build())
                .callHandler(((_, _) -> new McpSchema.CallToolResult("The user's pulse is 42.", false)))
                .build();
        McpServer
                .sync(transportProvider)
                .serverInfo("mcpulsor-server", "0.0.1-SNAPSHOT")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .build())
                .tools(bioSensor)
                .build();
    }
}

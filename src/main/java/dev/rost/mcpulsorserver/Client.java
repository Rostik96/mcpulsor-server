package dev.rost.mcpulsorserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;

class Client {

    static void main() {
        var stdioTransport = new StdioClientTransport(
                ServerParameters.builder("java")
                        .args("-jar", "target/mcpulsor-server-0.0.1-SNAPSHOT-fat.jar")
                        .build(),
                new JacksonMcpJsonMapper(new ObjectMapper()));
        var client = McpClient.sync(stdioTransport).build();
        try {
            client.initialize();
            client.listTools().tools().forEach(System.out::println);
            client.callTool(McpSchema.CallToolRequest.builder()
                            .name("bioSensor")
                            .build())
                    .content()
                    .forEach(System.out::println);
        } finally {
            client.closeGracefully();
        }
    }
}

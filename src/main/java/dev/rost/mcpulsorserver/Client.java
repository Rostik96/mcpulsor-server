package dev.rost.mcpulsorserver;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

class Client {
    static void main() {
        var clientTransport = HttpClientStreamableHttpTransport
                .builder("http://localhost:8090")
                .endpoint("/mcpulsor")
                .build();
        var client = McpClient.sync(clientTransport).build();
        client.initialize();
        client.listTools().tools().forEach(System.out::println);
        client.callTool(McpSchema.CallToolRequest
                        .builder("bioSensor")
                        .arguments(Map.of("days", 7))
                        .build())
                .content()
                .forEach(System.out::println);
    }
}

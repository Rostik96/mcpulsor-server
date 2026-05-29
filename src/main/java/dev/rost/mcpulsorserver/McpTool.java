package dev.rost.mcpulsorserver;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

public interface McpTool {

    McpSchema.Tool definition();

    McpSchema.CallToolResult execute(McpSyncServerExchange exchange, McpSchema.CallToolRequest request);

    default McpServerFeatures.SyncToolSpecification specification() {
        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(definition())
                .callHandler(this::execute)
                .build();
    }
}

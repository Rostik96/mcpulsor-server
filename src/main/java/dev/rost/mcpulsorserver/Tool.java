package dev.rost.mcpulsorserver;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

public interface Tool {

    McpSchema.Tool tool();

    McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request);

    default McpServerFeatures.SyncToolSpecification instance() {
        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(tool())
                .callHandler(this::call)
                .build();
    }
}

FROM maven:3.9-eclipse-temurin-26 AS build

WORKDIR /src
COPY . /src

# Build fat jar via shade plugin
RUN --mount=type=cache,target=/root/.m2 \
    set -eux; \
    mvn -q -DskipTests -f pom.xml package; \
    mkdir -p /app; \
    cp target/*-fat.jar /app/app.jar

# ---------- Runtime: STDIO + Inspector (one container) ----------
FROM eclipse-temurin:26-jre-alpine AS stdio
WORKDIR /app
RUN apk add --no-cache nodejs npm \
    && npm install -g @modelcontextprotocol/inspector@0.21.2
COPY --from=build /app/app.jar /app/app.jar


EXPOSE 6274 6277
# Inspector launches your server (fat jar has Main-Class set)
CMD sh -c 'mcp-inspector -e MCP_TRANSPORT=$MCP_TRANSPORT -- java -jar /app/app.jar'

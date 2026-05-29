# ---------- Build ----------
FROM maven:3.9-eclipse-temurin-26 AS build

WORKDIR /src
COPY pom.xml lombok.config ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    set -eux; \
    mvn -q -DskipTests package; \
    java -Djarmode=tools -jar target/mcpulsor-server-*.jar extract --layers --launcher; \
    mv mcpulsor-server-* layers

# ---------- Runtime base (layered JAR) ----------
FROM eclipse-temurin:26-jre-alpine AS runtime-base
WORKDIR /app
COPY --from=build /src/layers/dependencies/ ./
COPY --from=build /src/layers/spring-boot-loader/ ./
COPY --from=build /src/layers/snapshot-dependencies/ ./
COPY --from=build /src/layers/application/ ./

# ---------- Runtime: HTTP server only (inspector separate) ----------
FROM runtime-base AS http
EXPOSE 8090
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

# ---------- Runtime: STDIO + Inspector (one container) ----------
FROM runtime-base AS stdio
RUN apk add --no-cache nodejs npm
EXPOSE 6274 6277
CMD ["sh", "-c", "npx @modelcontextprotocol/inspector -e MCP_TRANSPORT=$MCP_TRANSPORT -- java org.springframework.boot.loader.launch.JarLauncher"]

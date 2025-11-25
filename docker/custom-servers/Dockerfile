FROM eclipse-temurin:17-jre
RUN mkdir -p /app
ADD https://github.com/Pugmatt/BedrockConnect/releases/latest/download/BedrockConnect-1.0-SNAPSHOT.jar /app
WORKDIR /app
EXPOSE 19132/udp
CMD ["java", "-Xms512M", "-Xmx512M", "-jar", "BedrockConnect-1.0-SNAPSHOT.jar", "custom_servers=/app/custom_servers.json"]

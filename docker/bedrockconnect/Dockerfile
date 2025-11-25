FROM eclipse-temurin:17-jre
RUN mkdir -p /docker/brc
ADD https://github.com/Pugmatt/BedrockConnect/releases/latest/download/BedrockConnect-1.0-SNAPSHOT.jar /docker/brc
WORKDIR /docker/brc
EXPOSE 19132/udp
CMD ["java", "-Xms256M", "-Xmx256M", "-jar", "BedrockConnect-1.0-SNAPSHOT.jar"]

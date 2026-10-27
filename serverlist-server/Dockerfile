FROM eclipse-temurin:17-jre

WORKDIR /docker/brc

COPY target/BedrockConnect-1.0-SNAPSHOT.jar /docker/brc/BedrockConnect-1.0-SNAPSHOT.jar

EXPOSE 19132/udp
EXPOSE 19132/tcp

ENTRYPOINT ["java", "-jar", "BedrockConnect-1.0-SNAPSHOT.jar"]
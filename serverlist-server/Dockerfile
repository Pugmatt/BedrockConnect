# Not distroless: NetherNet's WebRTC library needs libssl3/libcrypto3, which distroless/java21 lacks
FROM eclipse-temurin:21-jre
ADD target/BedrockConnect-1.0-SNAPSHOT.jar /docker/brc/
WORKDIR /docker/brc
EXPOSE 19132/udp
EXPOSE 19132/tcp
EXPOSE 19133/udp
ENTRYPOINT ["java", "-jar"]
CMD ["BedrockConnect-1.0-SNAPSHOT.jar"]

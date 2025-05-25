FROM gcr.io/distroless/java21
ADD target/BedrockConnect-1.0-SNAPSHOT.jar /docker/brc
WORKDIR /docker/brc
EXPOSE 19132/udp
CMD ["BedrockConnect-1.0-SNAPSHOT.jar"]

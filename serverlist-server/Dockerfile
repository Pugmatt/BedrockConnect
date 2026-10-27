FROM gcr.io/distroless/java21
RUN apt update
RUN apt install --reinstall libssl3 openssl
ADD target/BedrockConnect-1.0-SNAPSHOT.jar /docker/brc
WORKDIR /docker/brc
EXPOSE 19132/udp
EXPOSE 19132/tcp
CMD ["BedrockConnect-1.0-SNAPSHOT.jar"]

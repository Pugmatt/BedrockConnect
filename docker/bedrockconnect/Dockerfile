FROM eclipse-temurin:17-jre
RUN apt update
RUN apt install --reinstall libssl3 openssl
RUN mkdir -p /docker/brc
ADD https://github.com/Pugmatt/BedrockConnect/releases/latest/download/BedrockConnect-1.0-SNAPSHOT.jar /docker/brc
WORKDIR /docker/brc
EXPOSE 19132/udp
EXPOSE 19132/tcp
CMD ["java", "-Xms256M", "-Xmx256M", "-jar", "BedrockConnect-1.0-SNAPSHOT.jar"]

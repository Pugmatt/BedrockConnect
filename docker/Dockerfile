FROM hypriot/rpi-java
RUN mkdir /brc
COPY BedrockConnect-1.0-SNAPSHOT.jar /brc
WORKDIR /brc
EXPOSE 19132/udp
CMD ["java", "-Xms1G", "-Xmx1G", "-jar", "BedrockConnect-1.0-SNAPSHOT.jar", "nodb=true"]

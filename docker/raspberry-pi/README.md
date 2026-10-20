# BedrockConnect on Docker on Raspberry Pi

- Grab the `Dockerfile`
- Configure the `CMD` line in the `Dockerfile` to fit your needs
- run `docker build -t bedrock-connect .`
- run `docker run --name "bedrock-c" -d --restart always -p 19132:19132/udp -p 19132:19132/tcp -p 19133:19133/udp -v bedrock-c-data:/data -e BC_NETHERNET_IDENTITY=/data/identity.pem bedrock-connect`

NOTE: This setup only includes the BedrockConnect instance portion, and not currently the DNS server. You can set a DNS server up with the install-bind.sh script: https://raw.githubusercontent.com/Pugmatt/BedrockConnect/master/scripts/install-bind.sh (TODO: Seperate docker image for BIND setup)

# Docker compose example
```
  bedrock-connect:
    container_name: bedrock-connect
    build:
      context: /home/pi/BedrockConnect/docker/raspberrypi
      dockerfile: ./Dockerfile
    environment:
      - BC_NETHERNET_IDENTITY=/brc/data/identity.pem
    volumes:
      - /home/pi/BedrockConnect/docker/raspberrypi/custom_servers.json:/brc/custom_servers.json
      - /home/pi/BedrockConnect/docker/raspberrypi/data:/brc/data
    ports:
      - 19132:19132/udp
      - 19132:19132/tcp
      - 19133:19133/udp
    restart: unless-stopped
```

Replace "/home/pi/BedrockConnect/docker/raspberrypi" with the location of the Dockerfile, and custom_servers.json. The `data` folder keeps the NetherNet identity (`identity.pem`) when the container is recreated, so players are not asked to trust the server again.

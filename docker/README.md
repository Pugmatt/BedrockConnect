# BedrockConnect on Docker

- Install `docker` and `docker-compose`
- Simply run `docker-compose up -d`

##For custom server config
[README](custom-servers/README.md)


### OUTDATED
- Put the `Dockerfile` and the `BedrockConnect-1.0-SNAPSHOT.jar` in one directory
- Configure the `CMD` line in the `Dockerfile` to fit your needs
- run `docker build -t bedrock-connect .`
- run `docker run --name "bedrock-c" -d --restart always -p 19132:19132/udp bedrock-connect`

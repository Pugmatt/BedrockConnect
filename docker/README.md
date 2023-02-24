# BedrockConnect on Docker

- Install `docker` and `docker-compose`
- Run `docker-compose up` (`docker-compose up -d` for detached mode)

# BedrockConnect on Docker Custom DNS

If setting up this container enviornment on a local network or in the cloud you can specify custom DNS settings for your BedrockConnect server.
By default the IP addresses are set to `104.238.130.180` for the BedrockConnect server and the Name server for DNS.

To update the DNS settings, update the fields `BCIP` and `NSIP` in the docker-compose.yml to the IP address of your docker host.

Also make sure to stop and disable the `resolvconf.service` so your dns container can bind to the system port 53 before starting your container. Otherwise it will fail to deploy.

# Extra
[Custom Servers](custom-servers/README.md)

[Raspberry PI](raspberry-pi/README.md)

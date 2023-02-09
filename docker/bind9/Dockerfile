FROM ubuntu:jammy

RUN apt update\
  && apt dist-upgrade -y\
  && apt install wget -y


#Setting for future env vars later maybe?
ARG BCIP="104.238.130.180"
ARG NSIP="104.238.130.180"

RUN wget -O - https://raw.githubusercontent.com/xTITUSMAXIMUSX/BedrockConnect/master/scripts/docker-install-bind.sh $BCIP $NSIP | bash

#run forever
CMD ["/bin/bash", "-c", "while :; do sleep 10; done"]
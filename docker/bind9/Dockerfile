FROM ubuntu:jammy

RUN apt update\
  && apt dist-upgrade -y\
  && apt install wget bash -y

ARG BCIP="104.238.130.180"
ARG NSIP="104.238.130.180"

RUN mkdir -p /docker/bind9
ADD https://github.com/Pugmatt/BedrockConnect/blob/master/scripts/install-bind.sh /docker/bind9
WORKDIR /docker/bind9
RUN chmod +x /docker/bind9/install-bind.sh
#CMD [ "/docker/bind9/install-bind.sh", "${BCIP}", "${NSIP}" ] 
RUN wget -O - https://raw.githubusercontent.com/Pugmatt/BedrockConnect/master/scripts/install-bind.sh | bash

#run forever
CMD ["/bin/bash", "-c", "while :; do sleep 10; done"
FROM ubuntu:jammy

RUN apt update\
  && apt dist-upgrade -y\
  && apt install wget -y


#Setting for future env vars later maybe?
ARG BCIP
ARG NSIP

#download install script
RUN mkdir -p /docker/bind
RUN wget -O /docker/bind/docker-install-bind.sh https://raw.githubusercontent.com/Pugmatt/BedrockConnect/master/scripts/docker-install-bind.sh 
RUN bash /docker/bind/docker-install-bind.sh ${BCIP} ${NSIP}

#run forever
CMD ["/bin/bash", "-c", "/etc/init.d/named start; while :; do sleep 10; done"]
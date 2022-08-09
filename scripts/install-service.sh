#!/usr/bin/env bash

SCRIPTS_DIR=$(dirname $0)

# Requires root

if [[ "$(whoami)" != "root" ]]; then
  echo "Error: This script need to be ran as root (or using sudo)."
  exit 1
fi

# Check OS and dependencies

if [[ -f /etc/os-release ]]; then
  source /etc/os-release
  if [[ "$ID" == "ubuntu" || "$ID_LIKE" == "ubuntu" ]]; then
    apt install -y curl jq
    ADDUSER=useradd
  elif [[ "$ID" == "centos" ]]; then
    yum install -y epel-release
    yum install -y curl jq
    ADDUSER=adduser
  else
    echo "Unsupported OS"
    exit 2
  fi
else
  echo "Unsupported OS"
  exit 2
fi

# Add user

$ADDUSER \
   --shell /dev/null \
   --user-group \
   --password "$(dd bs=36 count=1 if=/dev/urandom 2>/dev/null | base64)" \
   --home /home/mcbc \
   --create-home \
   mcbc

# Download latest

curl "$(curl -s https://api.github.com/repos/Pugmatt/BedrockConnect/releases/latest | jq -r .assets[0].browser_download_url)" \
  -sLo /home/mcbc/BedrockConnect-1.0-SNAPSHOT.jar

chown mcbc:mcbc /home/mcbc/BedrockConnect-1.0-SNAPSHOT.jar

# Copy systemd script

cp "$SCRIPTS_DIR/bedrock_connect.service" /etc/systemd/system/

# Reload, enable and start

systemctl daemon-reload

systemctl enable bedrock_connect

systemctl start bedrock_connect

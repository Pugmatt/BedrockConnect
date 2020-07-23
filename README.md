# BedrockConnect

![Logo](https://i.imgur.com/H9zVzGT.png)

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[![HitCount](http://hits.dwyl.com/Pugmatt/BedrockConnect.svg)](http://hits.dwyl.com/Pugmatt/BedrockConnect)

On Minecraft Bedrock Edition, players on Xbox One, Nintendo Switch, and PS4 are limited to playing on 'Featured Servers' approved by Mojang/Microsoft. These players are not able to join servers via an IP/address. This is an issue for me and others, as the server community on Java edition was one of the major parts that made Minecraft what it was, and what also made what the servers that are now considered 'Mojang Server Partners' what they are today. I wanted to fix this, so I made a solution that anyone can setup easily.

BedrockConnect is an easy to use solution for Minecraft Bedrock Edition players on Xbox One, Nintendo Switch, PS4 to join any server IP, while also having access to a serverlist that allows you to manage a list of servers. It doesn't require any downloads, just a few changes to settings.

Here's the final result in action: https://www.youtube.com/watch?v=Uz-XYXAxd8Q

Here's tutorials to get it setup yourself. It takes only a few minutes to get setup:

Switch: https://www.youtube.com/watch?v=zalT_oR1nPM

Xbox: https://www.youtube.com/watch?v=g8mHvasVHMs

PS4: Although this is technically possible, I don't have a PS4 to make a tutorial video on. If anyone has a PS4 and would like to make a tutorial, send me the video and I'll put the link of the video here. :)

# FAQ

**How does it work?** In Minecraft Bedrock Edition, players on any version can join the available 'Featured Servers'. By using a DNS server, we can make the domains that are used to join these servers, and make them direct to the BedrockConnect serverlist server, rather than their actual servers.

The BedrockConnect serverlist server, is a specially made Minecraft server that serves the purpose of joining Minecraft servers. Yes, you join Minecraft servers, from a Minecraft server. The server can transfer you to the server you want, and you can store servers as well, just like a regular serverlist.

**What is a DNS server?** A DNS server is what devices uses to know what domain names go with what IP address. Your device sends the DNS server a domain name and asks what IP is associated with it, and the DNS server sends an IP back for the device to connect to. Commonly used ones include Google or Cloudflare DNS. Anyone can technically create a DNS server, and have it associate whatever IP they want to a domain. In this case, we make the 'Featured Server' domains direct to our own server.

**I don't trust your DNS server...** The public BedrockConnect DNS server only redirects the domains of the "Featured Servers" in Minecraft to the BedrockConnect serverlist. (Full list of records under the "Using your own DNS server" section) It's understandable though why some might not want to use a random DNS server.  If you fear a MITM attack, you can also verify any domains you fear the DNS server are overriding by pinging them in command line or another tool. If you still don't feel comfortable using the BedrockConnect DNS server, you can also make your own. Look under 'Using your own DNS server' further down this page for more on that.

# Hosting your own serverlist server

If you want a full tutorial on how to host your own BedrockConnect server with a DNS server, watch the following video: https://www.youtube.com/watch?v=AW5X7-qnvLk

The instructions below will show how to run the barebone JAR.


**Requirements:**
- Java 8 or higher

Download the latest release of the BedrockConnect serverlist software here: https://github.com/Pugmatt/BedrockConnect/releases

Run the jar with the following command
```
java -jar BedrockConnect-1.0-SNAPSHOT.jar nodb=true
```
(```nodb=true``` allows the software to run without a database. If you want to use a database, remove this argument)

The following arguments can be placed in the startup command to ajust settings:

| Argument  | Description | Default Value |
| ------------- | ------------- | ------------- |
| mysql_host  | MySQL Host  | localhost |
| mysql_db | MySQL Database Name  | bedrock-connect |
| mysql_user | MySQL Username  | root |
| mysql_pass | MySQL Password  |  |
| server_limit | How many servers a new player can have in their serverlist  | 100 |
| port | Port of the server (Should only be changed for debugging on PC. Port needs to be on 19132 for the bypass to work on game consoles) | 19132 |
| nodb | If true, use JSON files for data instead of MySQL | false |
| generatedns | If true, generate a DNS zone file using user input (Only needed if you're using the mod0Umleitung DNS software) | false |

MySQL example:
```
java -jar BedrockConnect-1.0-SNAPSHOT.jar mysql_pass=test123 server_limit=10
```

# Libraries used
- [NukkitX Bedrock Protocol Library](https://github.com/NukkitX/Protocol)

# Using your own DNS server

In the case where you want to use your own DNS server instead of the one I supplied, this is what zones you'll need to set your DNS to in order for BedrockConnect to work:

| Domain  | IP |
| ------------- | ------------- |
| geo.hivebedrock.network  | 104.238.130.180  |
| hivebedrock.network  | 104.238.130.180  |
| mco.mineplex.com | 104.238.130.180  | 
| play.inpvp.net | 104.238.130.180  |
| mco.lbsg.net | 104.238.130.180  |
| mco.cubecraft.net | 104.238.130.180  |

*104.238.130.180 is the IP to the BedrockConnect serverlist server. If you are hosting your own BedrockConnect serverlist server as well, obviously use that IP instead*

Here's an easy way to setup BIND (DNS server software) on Linux: https://github.com/Pugmatt/BedrockConnect/blob/master/BIND_Install.txt


# Donations

If you like what you see, feel free to throw a few bucks. I won't ever charge for this service, so currently everything is out of pocket.

http://paypal.me/Pugmatt


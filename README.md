# BedrockConnect

![Logo](https://i.imgur.com/H9zVzGT.png)

On Minecraft Bedrock Edition, players on Xbox One and Nintendo Switch are limited to playing on 'Featured Servers' approved by Mojang/Microsoft. These players are not able to join servers via an IP/address. This is an issue for me and others, as the server community on Java edition was one of the major parts that made Minecraft what it was, and what also made what the servers that are now considered 'Mojang Server Partners' what they are today. I wanted to fix this, so I made a solution that anyone can setup easily.

BedrockConnect is an easy to use solution for Minecraft Bedrock Edition players on Xbox One and Nintendo Switch to join any server IP, while also having access to a serverlist that allows you to manage a list of servers. It doesn't require any downloads, just a few changes to settings.

Here's the final result in action: https://www.youtube.com/watch?v=Uz-XYXAxd8Q

Here's tutorials to get it setup yourself. It takes only a few minutes to get setup:

Switch: https://www.youtube.com/watch?v=zalT_oR1nPM

Xbox: https://www.youtube.com/watch?v=g8mHvasVHMs

# FAQ

**How does it work?** In Minecraft Bedrock Edition, players on any version can join the available 'Featured Servers'. By using a DNS server, we can make the domains that are used to join these servers, and make them direct to the BedrockConnect serverlist server, rather than their actual servers.

The BedrockConnect serverlist server, is a specially made Minecraft server that serves the purpose of joining Minecraft servers. Yes, you join Minecraft servers, from a Minecraft server. The server can transfer you to the server you want, and you can store servers as well, just like a regular serverlist.

**What is a DNS server?** A DNS server is what devices uses to know what domain names go with what IP address. Your device sends the DNS server a domain name and asks what IP is associated with it, and the DNS server sends an IP back for the device to connect to. Commonly used ones include Google or Cloudflare DNS. Anyone can technically create a DNS server, and have it associate whatever IP they want to a domain. In this case, we make the 'Featured Server' domains direct to our own server.

**I don't trust your DNS server...** It's understandable why some might not want to use a random DNS server. Keep in mind, that a DNS server does not have any access to the information you send to a server/website. It only knows the domains you are currently trying to use so it can respond back with an IP to connect to, but nothing that has to do with information you send to an actual server. If you still don't feel comfortable using the BedrockConnect DNS server, you can also make your own. Look under 'Using your own DNS server' further down this page for more on that.



# Hosting your own serverlist server

In the case where you want to host the BedrockConnect software yourself to host your own serverlist, here's how:


**Requirements:**
- Have MySQL running on your computer. XAMPP is the easiest software to use to get one running: https://www.apachefriends.org/index.html
- Java 8 or higher

Download the latest release of the BedrockConnect serverlist software here: https://github.com/Pugmatt/BedrockConnect/releases/tag/1.0

Run the jar with the following command
```
java -jar BedrockConnect-1.0-SNAPSHOT.jar
```

The following arguments can be placed in the startup command to ajust settings:

| Argument  | Description | Default Value |
| ------------- | ------------- | ------------- |
| mysql_host  | MySQL Host  | localhost |
| mysql_db | MySQL Database Name  | bedrock-connect |
| mysql_user | MySQL Username  | root |
| mysql_pass | MySQL Password  |  |
| server_limit | How many servers a new player can have in their serverlist  | 100 |
| port | Port of the server  | 19132 |

Example:
```
java -jar BedrockConnect-1.0-SNAPSHOT.jar mysql_pass=test123 server_limit=10
```

# Using your own DNS server

In the case where you want to use your own DNS server instead of the one I supplied, this is what zones you'll need to set your DNS to in order for BedrockConnect to work:

| Domain  | IP |
| ------------- | ------------- |
| hivebedrock.network  | 104.238.130.180  |
| mco.mineplex.com | 104.238.130.180  | 
| play.inpvp.net | 104.238.130.180  |
| mco.lbsg.net | 104.238.130.180  |
| mco.cubecraft.net | 104.238.130.180  |

*104.238.130.180 is the IP to the BedrockConnect serverlist server. If you are hosting your own BedrockConnect serverlist server as well, obviously use that IP instead*

Here's an easy way to setup BIND (DNS server software) on Linux: https://github.com/Pugmatt/BedrockConnect/blob/master/BIND_Install.txt

# Donations

If you like what you see, feel free to throw a few bucks. I won't ever charge for this service, so currently everything is out of pocket.

https://www.paypal.com/Pugmatt

# BedrockConnect

![Logo](https://i.imgur.com/H9zVzGT.png)

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[![HitCount](http://hits.dwyl.com/Pugmatt/BedrockConnect.svg)](http://hits.dwyl.com/Pugmatt/BedrockConnect)

On Minecraft Bedrock Edition, players on Xbox One, Nintendo Switch, and PS4/PS5 are limited to playing on 'Featured Servers' approved by Mojang/Microsoft. These players are not able to join servers via an IP/address. This is an issue for me and others, as the server community on Java edition was one of the major parts that made Minecraft what it was, and what also made what the servers that are now considered 'Mojang Server Partners' what they are today. I wanted to fix this, so I made a solution that anyone can setup easily.

BedrockConnect is an easy to use solution for Minecraft Bedrock Edition players on Xbox One, Nintendo Switch, PS4/PS5 to join any server IP, while also having access to a serverlist that allows you to manage a list of servers. It doesn't require any downloads, just a few changes to settings.

Here's the final result in action: https://www.youtube.com/watch?v=Uz-XYXAxd8Q

Here's tutorials to get it setup yourself. It takes only a few minutes to get setup:

Switch: https://www.youtube.com/watch?v=zalT_oR1nPM

Xbox: https://www.youtube.com/watch?v=g8mHvasVHMs

PS4/PS5: https://youtu.be/0MJVVhDeu2s?t=347

Joining Java Edition Servers: https://www.youtube.com/watch?v=B_oPHl5gz_c

If you're having trouble connecting to the serverlist, take a look at the troubleshooting page: https://github.com/Pugmatt/BedrockConnect/wiki/Troubleshooting

Table of contents
=================

   * [FAQ](#faq)
   * [Publicly available BedrockConnect instances](#publicly-available-bedrockconnect-instances)
   * [Hosting your own serverlist server](#hosting-your-own-serverlist-server)
   * [Defining your own custom servers](#defining-your-own-custom-servers)
   * [Change wording of serverlist](#change-wording-of-serverlist)
   * [Using your own DNS server](#using-your-own-dns-server)
   * [Libraries used](#libraries-used)
   * [Donations](#donations)

# FAQ

**How does it work?** In Minecraft Bedrock Edition, players on any version can join the available 'Featured Servers'. By using a DNS server, we can make the domains that are used to join these servers, and make them direct to the BedrockConnect serverlist server, rather than their actual servers.

The BedrockConnect serverlist server, is a specially made Minecraft server that serves the purpose of joining Minecraft servers. Yes, you join Minecraft servers, from a Minecraft server. The server can transfer you to the server you want, and you can store servers as well, just like a regular serverlist.

**What is a DNS server?** A DNS server is what devices uses to know what domain names go with what IP address. Your device sends the DNS server a domain name and asks what IP is associated with it, and the DNS server sends an IP back for the device to connect to. Commonly used ones include Google or Cloudflare DNS. Anyone can technically create a DNS server, and have it associate whatever IP they want to a domain. In this case, we make the 'Featured Server' domains direct to our own server.

**I don't trust your DNS server...** The public BedrockConnect DNS server only redirects the domains of the "Featured Servers" in Minecraft to the BedrockConnect serverlist. (Full list of records under the "Using your own DNS server" section) It's understandable though why some might not want to use a random DNS server.  If you fear a MITM attack, you can also verify any domains you fear the DNS server are overriding by pinging them in command line or another tool. If you still don't feel comfortable using the BedrockConnect DNS server, you can also make your own. Look under 'Using your own DNS server' further down this page for more on that.

**Some featured server aren't redirecting to the serverlist** If some featured servers are redirecting to the BedrockConnect serverlist, and some aren't, this can be an issue with the DNS cache on the device/game console not updating. Nothing can really be done except wait when on the game console for the cache to clear, as there isn't a manual way to do it on these devices.

Another possible issue is that some of the featured servers such the Hive, use DNSSEC, which is used to protect itself from being overidden by DNS servers such as BedrockConnect. This is still being tested, and seems to work on some people's consoles and not on others.

# Publicly available BedrockConnect instances

There are multiple BedrockConnect serverlist servers available that can be used, giving you multiple options to connect to. Currently, they do NOT share the same player database, so if you have added a server to your list on any of the given servers and connect to a different one, you will need to save that data again.

| IP Address | Location | Maintainer | Note |
| ------------- | ------------- | ------------- | ------------- |
| 104.238.130.180 | <img src="https://flagicons.lipis.dev/flags/4x3/us.svg" height="20"> | [Pugmatt](https://github.com/Pugmatt) | Main instance. Multiple load balanced servers. Might be blocked on PS4, try another instance if you experience issues. |
| 173.82.100.84 | <img src="https://flagicons.lipis.dev/flags/4x3/us.svg" height="20"> | [jdextraze](https://github.com/jdextraze) | |
| 213.171.211.142 | <img src="https://flagicons.lipis.dev/flags/4x3/gb.svg" height="20"> | [kmpoppe](https://github.com/kmpoppe) | No DNS service, only BedrockConnect server  |
| 217.160.58.93 | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [kmpoppe](https://github.com/kmpoppe) | No DNS service, only BedrockConnect server |
| 188.165.49.178 | <img src="https://flagicons.lipis.dev/flags/4x3/fr.svg" height="20"> | [Darkmoi3108](https://github.com/darkmoi3108) | |
| 134.255.231.119 | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [ZAP-Hosting](https://github.com/zaphosting) | |
| 91.218.66.13 | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [LazyBirb](https://github.com/LazyBirb) | |

If you are currently hosting a BedrockConnect instance and are interested in adding it to this list, create a pull request adding it to the table above.

# Hosting your own serverlist server

If you want a full tutorial on how to host your own BedrockConnect server with a DNS server, watch the following video: https://www.youtube.com/watch?v=AW5X7-qnvLk

Or, if you're on Linux: https://github.com/Pugmatt/BedrockConnect/wiki/Setting-up-on-Linux

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
| bindip | IP that the BedrockConnect server will bind to | 0.0.0.0 |
| nodb | If true, use JSON files for data instead of MySQL | false |
| generatedns | If true, generate a DNS zone file using user input (Only needed if you're using the mod0Umleitung DNS software) | false |
| kick_inactive | If true, players will be kicked after 10 minutes of inactivity with the serverlist UI | true |
| custom_servers| Sets the path to a custom server file, for specifying your servers in the list for all players. See [custom servers](#defining-your-own-custom-servers). |  |
| user_servers | If true, players can add and remove servers on the serverlist. If false, the options are hidden. | true |
| featured_servers | If true, the featured servers will be displayed in the serverlist.  If false, the servers are hidden. | true |
| whitelist | Specify file containing list of whitelisted players. (Should be a text file with the player names specified on seperate lines) | |
| fetch_featured_ips | If true, dynamically grab the featured server IPs from the domain names. If false, a file ```featured_server_ips.json``` will be generated, containing the hard-coded featured server IPs, and to allow changing them if needed.  | true |
| language | Specify a file containing language customizations. See [guide for changing wording](#change-wording-of-serverlist) | |

MySQL example:
```
java -jar BedrockConnect-1.0-SNAPSHOT.jar mysql_pass=test123 server_limit=10
```

# Defining your own custom servers

When hosting your own serverlist server, you add your own custom servers to the top of the serverlist for all players. To get started, create a JSON file and follow this format:
```json
[
	{
		"name": "My Custom Server 1",
		"iconUrl": "https://i.imgur.com/3BmFZRE.png",
		"address": "mc1.example.com",
		"port": 19132
	},
	{
		"name": "My Custom Server 2",
		"iconUrl": "https://i.imgur.com/3BmFZRE.png",
		"address": "mc2.example.com",
		"port": 19132
	}
]
```

You can also specify groups, such as the following format:
```json
[
        {
		"name": "My Server Group",
		"iconUrl": "https://i.imgur.com/3BmFZRE.png",
		"content": [
			{
				"name": "Server in Group 1",
				"iconUrl": "https://i.imgur.com/3BmFZRE.png",
				"address": "mc1.example.com",
				"port": 19132
			},
		]
	},
	{
		"name": "My Custom Server 1",
		"iconUrl": "https://i.imgur.com/3BmFZRE.png",
		"address": "mc1.example.com",
		"port": 19132
	},
	{
		"name": "My Custom Server 2",
		"iconUrl": "https://i.imgur.com/3BmFZRE.png",
		"address": "mc2.example.com",
		"port": 19132
	}
]
```


Then, add this argument to your startup script: `custom_servers=[path to json file]`

The icon URL is not required, if omitted it will show the default icon.

# Change wording of serverlist

For cases where you want to change the wording/language of your BedrockConnect server, you can do this by creating a JSON file in the same directory as the BedrockConnect JAR. The contents of this file should contain the parts of the wording you want to overwrite. 

You can find all the options that be overwritten here: https://github.com/Pugmatt/BedrockConnect/blob/master/serverlist-server/src/main/resources/language.json

Example custom language file:
```json
{
	"main": {
		"heading": "My Cool ServerList",
		"connectBtn" : "Hop in a server!"
	},
	"disconnect": {
		"exit": "Goodbye!"
	}
}
```

Once finished, you include it in your server by adding the following arguement to your startup command: ```language=my_lang.json``` (Replace "my_lang" with the name of your file")

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
| play.galaxite.net | 104.238.130.180 |
| play.pixelparadise.gg | 104.238.130.180 |

*104.238.130.180 is the IP to the BedrockConnect serverlist server. If you are hosting your own BedrockConnect serverlist server as well, obviously use that IP instead*

Here's a script to setup BIND (DNS server software) on Linux: https://github.com/Pugmatt/BedrockConnect/blob/master/scripts/install-bind.sh

# Libraries used
- [NukkitX Bedrock Protocol Library](https://github.com/NukkitX/Protocol)


# Donations

If you like what you see, feel free to throw a few bucks. I won't ever charge for this service. Donations go toward hosting the main BedrockConnect instance, 104.238.130.180.

https://paypal.me/Pugmatt


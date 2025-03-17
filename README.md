# BedrockConnect

![Logo](https://i.imgur.com/H9zVzGT.png)

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[![HitCount](http://hits.dwyl.com/Pugmatt/BedrockConnect.svg)](http://hits.dwyl.com/Pugmatt/BedrockConnect)

On Minecraft Bedrock Edition, players on Xbox One, Nintendo Switch, and PS4/PS5 are limited to playing on 'Featured Servers' approved by Mojang/Microsoft. These players are not able to join servers via an IP/address. This is an issue for me and others, as the server community on Java edition was one of the major parts that made Minecraft what it was, and what also made what the servers that are now considered 'Mojang Server Partners' what they are today. I wanted to fix this, so I made a solution that anyone can setup easily.

BedrockConnect is an easy to use solution for Minecraft Bedrock Edition players on Xbox One, Nintendo Switch, PS4/PS5 to join any server IP, while also having access to a serverlist that allows you to manage a list of servers. It doesn't require any downloads, just a few changes to settings.

Here's the final result in action: https://www.youtube.com/watch?v=Uz-XYXAxd8Q

### Tutorials

Here's tutorials to get it setup yourself, it takes only a few minutes to get setup.

There are two methods available to join BedrockConnect:

#### "Add Friend" Method

- In the Minecraft main menu, click "Play" and then go to the "Friends" tab, and click "Add Friend" or "Find Cross-Platform Friends" or "Search for players" (whichever is available on your game version)

- Search for the gamer tag ***BCMain*** (Or any of [these alts](https://github.com/Pugmatt/BedrockConnect/wiki/Alternate-gamer-tags-for-%22Add-Friend%22-method)), and add this user as friend

- Return to the Minecraft main menu, and wait about 20 seconds. Then click "Play" and return to the "Friends" tab

- Wait a moment, and you should soon see a joinable instance show up, "Join to Open Server List". Or, you should see BCMain under the "Online" section with a joinable instance.

- Join instance to connect to BedrockConnect server list

<sub>(This method utilizes [MCXboxBroadcast](https://github.com/rtm516/MCXboxBroadcast) to supply this join option)</sub>

*In order to make room in the friendslist, you will be removed from the friendslist if inactive for ~3 or more days. (Threshold varies depending on current traffic the bot is getting) If this happens, simply add back the gamertag.*
  
#### DNS Method

Switch: https://www.youtube.com/watch?v=zalT_oR1nPM

Xbox:
- Go into your console's internet settings, and set the primary DNS to 104.238.130.180 and secondary DNS to 8.8.8.8
- For a video walkthrough: https://www.youtube.com/watch?v=g8mHvasVHMs

PS4/PS5:
- Go into your console's internet settings, and set the primary DNS to 45.55.68.52 and secondary DNS to 8.8.8.8

Joining Java Edition Servers: https://www.youtube.com/watch?v=B_oPHl5gz_c

If you're having trouble connecting to the serverlist, take a look at the troubleshooting page: https://github.com/Pugmatt/BedrockConnect/wiki/Troubleshooting

Table of contents
=================

   * [FAQ](#faq)
   * [Publicly available BedrockConnect instances](#publicly-available-bedrockconnect-instances)
   * [Hosting your own serverlist server](#hosting-your-own-serverlist-server)
   * [Configuration](#configuration)
   * [Defining your own custom servers](#defining-your-own-custom-servers)
   * [Change wording of serverlist](#change-wording-of-serverlist)
   * [Using your own DNS server](#using-your-own-dns-server)
   * [Libraries used](#libraries-used)
   * [Donations](#donations)

# FAQ

**How does it work?** In Minecraft Bedrock Edition, players on any version can join the available 'Featured Servers'. By using a DNS server, we can make the domains that are used to join these servers, and make them direct to the BedrockConnect serverlist server, rather than their actual servers. Or through the "Add Friend" method, we direct the user to the BedrockConnect serverlist server via a workaround supplied by [MCXboxBroadcast](https://github.com/rtm516/MCXboxBroadcast).

The BedrockConnect serverlist server, is a specially made Minecraft server that serves the purpose of joining Minecraft servers. Yes, you join Minecraft servers, from a Minecraft server. The server can transfer you to the server you want, and you can store servers as well, just like a regular serverlist.

**What is a DNS server?** A DNS server is what devices uses to know what domain names go with what IP address. Your device sends the DNS server a domain name and asks what IP is associated with it, and the DNS server sends an IP back for the device to connect to. Commonly used ones include Google or Cloudflare DNS. Anyone can technically create a DNS server, and have it associate whatever IP they want to a domain. In this case, we make the 'Featured Server' domains direct to our own server.

**I don't trust your DNS server...** The public BedrockConnect DNS server only redirects the domains of the "Featured Servers" in Minecraft to the BedrockConnect serverlist. (Full list of records under the "Using your own DNS server" section) It's understandable though why some might not want to use a random DNS server.  If you fear a MITM attack, you can also verify any domains you fear the DNS server are overriding by pinging them in command line or another tool. If you still don't feel comfortable using the BedrockConnect DNS server, you can also make your own. Look under 'Using your own DNS server' further down this page for more on that. Or, try the ["Add Friend" Method](#add-friend-method) instead of the DNS method.

**Some featured server aren't redirecting to the serverlist using the DNS** If some featured servers are redirecting to the BedrockConnect serverlist, and some aren't, this can be an issue with the DNS cache on the device/game console not updating. Nothing can really be done except wait when on the game console for the cache to clear, as there isn't a manual way to do it on these devices.

Another possible issue is that some of the featured servers such the Hive, use DNSSEC, which is used to protect itself from being overidden by DNS servers such as BedrockConnect. This is still being tested, and seems to work on some people's consoles and not on others.

# Publicly available BedrockConnect instances

There are multiple BedrockConnect serverlist servers available that can be used, giving you multiple options to connect to. Currently, they do NOT share the same player database, so if you have added a server to your list on any of the given servers and connect to a different one, you will need to save that data again.

| IP Address | Gamertag | Location | Maintainer | Note |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| 104.238.130.180 | BCMain, BCMain1 | <img src="https://flagicons.lipis.dev/flags/4x3/us.svg" height="20"> | [Pugmatt](https://github.com/Pugmatt) | Main instance. Multiple load balanced servers. If issues occur on PS4/PS5 with DNS, try the ["Add Friend" Method](#add-friend-method), or replace the primary DNS address with 45.55.68.52. |
| 5.161.83.73 | Cybrancee | <img src="https://flagicons.lipis.dev/flags/4x3/us.svg" height="20"> | [Cybrancee](https://github.com/cybrancee) |  Located in Virginia, United States. No DNS service, only BedrockConnect server  |
| 213.171.211.142 | N/A | <img src="https://flagicons.lipis.dev/flags/4x3/gb.svg" height="20"> | [kmpoppe](https://github.com/kmpoppe) | No DNS service, only BedrockConnect server  |
| 217.160.58.93 | N/A | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [kmpoppe](https://github.com/kmpoppe) | No DNS service, only BedrockConnect server |
| 134.255.231.119 | bedrocklist | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [ZAP-Hosting](https://github.com/zaphosting) | MCXboxBroadcast instance is unofficially maintained by [Dinushay](https://github.com/dinushay) |
| 45.88.109.66 | McBEFC | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [LazyBirb](https://github.com/lazybirb) | Located in Frankfurt am Main, Germany. [More Infos!](https://github.com/LazyBirb/LazyBirb/blob/master/bedrockconnect/README.MD) |
| 185.169.180.190 | N/A | <img src="https://flagicons.lipis.dev/flags/4x3/tr.svg" height="20"> | [hasankayra04](https://github.com/hasankayra04) | Dns service with NextDNS [Status Page](https://status.hasankayra04.com) (Listed as "Dns Listener") |
| 116.255.1.195 2401:d002:5c06:ca01:be24:11ff:fe78:41ad | TRBMCXB | <img src="https://flagicons.lipis.dev/flags/4x3/au.svg" height="20"> | [xavierhorwood](https://github.com/xavierhorwood) | Located in Brisbane, Australia, Dns service powered by PowerDNS |
| 140.238.204.159 | TRBSAU | <img src="https://flagicons.lipis.dev/flags/4x3/au.svg" height="20"> | [xavierhorwood](https://github.com/xavierhorwood) | Located in Sydney, Australia |

[Status Page for all public instances](https://bcstatus.teamriverbubbles.com/status/bedrock)


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

Alternatively, BedrockConnect can also be ran on Docker through the public image ```pugmatt/bedrock-connect```

```
docker run -p 19132:19132/udp pugmatt/bedrock-connect
```

# Configuration

BedrockConnect can be configured through three ways:

- Through startup arguments (e.g. ```java -jar BedrockConnect-1.0-SNAPSHOT.jar nodb=true user_servers=false server_limit=100```)

- Configuration file, by adding the file ```config.yml``` to the root directory where your BedrockConnect jar is present, containing settings in YAML format. Example:
```
user_servers: false
server_limit: 100
```

- Environment variables. Any setting can be defined through an environment variable, as long as it's prefixed with ```BC_``` (e.g. ```BC_USER_SERVERS```, ```BC_SERVER_LIMIT```, etc)

The following is the full list of settings available:

| Setting             | Description                                                                                                                                                                                                                      | Default Value   |
|---------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------|
| db_type             | Database Type (accepts values *mysql*, *postgres*, *mariadb*, or *none*)                                                                                                                                                                                                                    | mysql       |
| db_host             | Database Host                                                                                                                                                                                                                    | localhost       |
| db_db               | Database Name                                                                                                                                                                                                                    | bedrock-connect |
| db_user             | Database Username                                                                                                                                                                                                                | root            |
| db_pass             | Database Password                                                                                                                                                                                                                |                 |
| server_limit        | How many servers a new player can have in their serverlist                                                                                                                                                                       | 100             |
| port                | Port of the server (Should only be changed for debugging on PC. Port needs to be on 19132 for the bypass to work on game consoles)                                                                                               | 19132           |
| bindip              | IP that the BedrockConnect server will bind to                                                                                                                                                                                   | 0.0.0.0         |
| nodb                | If true, use JSON files                                                                                                                                                                                                          | true            |
| auto_reconnect      | If true, Make Mysql and MairaDB auto reconnect to the database when disconnected                                                                                                                                                 | false           |
| generatedns         | If true, generate a DNS zone file using user input (Only needed if you're using the mod0Umleitung DNS software)                                                                                                                  | false           |
| kick_inactive       | If true, players will be kicked after 10 minutes of inactivity with the serverlist UI                                                                                                                                            | true            |
| custom_servers      | Sets the path to a custom server file, for specifying your servers in the list for all players. See [custom servers](#defining-your-own-custom-servers).                                                                         |                 |
| user_servers        | If true, players can add and remove servers on the serverlist. If false, the options are hidden.                                                                                                                                 | true            |
| featured_servers    | If true, the featured servers will be displayed in the serverlist.  If false, the servers are hidden.                                                                                                                            | true            |
| whitelist           | Specify file containing list of whitelisted players. (Should be a text file with the player names specified on seperate lines)                                                                                                   |                 |
| fetch_featured_ips  | If true, dynamically grab the featured server IPs from the domain names. If false, a file ```featured_server_ips.json``` will be generated, containing the hard-coded featured server IPs, and to allow changing them if needed. | true            |
| fetch_ips           | If true, dynamically grab the server IPs from domain names, of any server a user is attempting to join.                                                                                                                          | false           |
| language            | Specify a file containing language customizations. See [guide for changing wording](#change-wording-of-serverlist)                                                                                                               |                 |
| store_display_names | If true, player displays names will be included in the stored player data.                                                                                                                                                       | true            |
| packet_limit        | Number of datagram packets each address can send within one tick (10ms)                                                                                                                                                          | 200             |
| global_packet_limit | Number of all datagrams that will be handled within one tick (10ms) before server starts dropping any incoming data.                                                                                                             | 100000          |

# Defining your own custom servers

When hosting your own serverlist server, you add your own custom servers to the top of the serverlist for all players. To get started, create a JSON file and follow this format:
```json
[
	{
		"name": "My Custom Server 1",
		"iconUrl": "https://i.imgur.com/nhumQVP.png",
		"address": "mc1.example.com",
		"port": 19132
	},
	{
		"name": "My Custom Server 2",
		"iconUrl": "https://i.imgur.com/nhumQVP.png",
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

Then, set ```custom_servers``` in your BedrockConnect [configuration](#configuration) to the path of the json file. (e.g. Setting through an argument to your startup script: `custom_servers=[path to json file]`)

The icon URL is not required, if omitted it will show the default icon.

# Change wording of serverlist

For cases where you want to change the wording/language of your BedrockConnect server, you can do this by creating a JSON file in the same directory as the BedrockConnect JAR. The contents of this file should contain the parts of the wording you want to overwrite. 

You can find all the options that can be overwritten here: https://github.com/Pugmatt/BedrockConnect/blob/master/serverlist-server/src/main/resources/language.json

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

Then, set ```language``` in your BedrockConnect [configuration](#configuration) to the path of the json file. (e.g. Setting through an argument to your startup script: `language=my_lang.json` Replace "my_lang" with the name of your file")

# Using your own DNS server

In the case where you want to use your own DNS server instead of the one I supplied, this is what zones you'll need to set your DNS to in order for BedrockConnect to work:

| Server | Domain  | IP |
| ------------- | ------------- | ------------- |
| The Hive | geo.hivebedrock.network  | 104.238.130.180  |
| The Hive | hivebedrock.network  | 104.238.130.180  |
| Mineville | play.inpvp.net | 104.238.130.180  |
| Lifeboat | mco.lbsg.net | 104.238.130.180  |
| Galaxite | play.galaxite.net | 104.238.130.180 |
| Enchanted Dragons | play.enchanted.gg | 104.238.130.180 |

<sub>["Why is CubeCraft not included in the above list?"](https://github.com/Pugmatt/BedrockConnect/pull/456)</sub>

*104.238.130.180 is the IP to the BedrockConnect serverlist server. If you are hosting your own BedrockConnect serverlist server as well, obviously use that IP instead*

Here's a script to setup BIND (DNS server software) on Linux: https://github.com/Pugmatt/BedrockConnect/blob/master/scripts/install-bind.sh

Alternatively, instead of using a DNS, you can also use other tools such as [MCXboxBroadcast](https://github.com/rtm516/MCXboxBroadcast) to join the BedrockConnect instance.

# Libraries used
- [NukkitX Bedrock Protocol Library](https://github.com/NukkitX/Protocol)


# Donations

If you like what you see, feel free to throw a few bucks. I won't ever charge for this service. Donations go toward hosting the main BedrockConnect instance, 104.238.130.180.

https://paypal.me/Pugmatt


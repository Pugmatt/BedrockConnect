# BedrockConnect

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[<img src="https://ko-fi.com/img/githubbutton_sm.svg" alt="Ko-fi" width="150">](https://ko-fi.com/Z8Z09Z56K)

<img src="https://i.imgur.com/H9zVzGT.png" alt="Bedrock Block" align="right" width="200">


BedrockConnect is a minimal *Minecraft: Bedrock Edition* server software that provides a server-list solution to players. Players can connect to any Bedrock Edition game servers, along with storing and managing an in-game list of their servers. 

This is primarily used for players on the game console version of the game (Nintendo Switch, Xbox, PlayStation, etc) where a serverlist to connect to 3rd-party servers is not natively available inside the game. This project aims to provide a quick straight forward solution that any player can setup directly on their game console without having to download anything.

Here's a small demo of it in action: https://www.youtube.com/watch?v=Uz-XYXAxd8Q

## ⭐ Quick setup

Below are instructions for setting up BedrockConnect on your game console, it typically takes only a few minutes to get setup and requires no downloads.

There are two primary methods available to join BedrockConnect:

### DNS Method
<details><summary>(Click to expand)</summary>

**Nintendo Switch**:
- Go into your console's internet settings, and set the primary DNS to 104.238.130.180 and secondary DNS to 8.8.8.8
- Video walkthrough: https://www.youtube.com/watch?v=zalT_oR1nPM

**Xbox**:
- Go into your console's internet settings, and set the primary DNS to 104.238.130.180 and secondary DNS to 8.8.8.8
- Video walkthrough: https://www.youtube.com/watch?v=g8mHvasVHMs

**PlayStation**:
- Go into your console's internet settings, and set the primary DNS to 45.55.68.52 and secondary DNS to 8.8.8.8

If you're having trouble connecting to the serverlist, you can try running through the [troubleshooting page](https://github.com/Pugmatt/BedrockConnect/wiki/Troubleshooting). Or, try the "Add Friend" Method below

</details>

### "Add Friend" Method
<details><summary>(Click to expand)</summary>

<sub>(This method utilizes [MCXboxBroadcast](https://github.com/rtm516/MCXboxBroadcast) to supply this join option)</sub>
	
- In the Minecraft main menu, click "Play" and then go to the "Friends" tab, and click "Add Friend" or "Find Cross-Platform Friends" or "Search for players" (whichever is available on your game version)

- Search for the gamer tag ***BCMain*** (Or ***BCMain1***, if BCMain is full or experiencing issues), and add this user as friend

- Return to the Minecraft main menu, and wait about 20 seconds. Then click "Play" and return to the "Friends" tab

- Wait a moment, and you should soon see a joinable instance show up, "Join to Open Server List". Or, you should see BCMain under the "Online" section with a joinable instance. (If the join option doesn't appear, you may need to wait another minute for the bot to process the friend request)

- Join instance to connect to BedrockConnect server list

*In order to make room in the friendslist, BCMain/BCMain1 routinely removes players from it's list that are inactive for ~1-3 days (Threshold varies depending on current traffic the bot is getting) If this happens, simply add back the gamertag.*
</details>

## How does it work?

When a player connects to BedrockConnect, they are met with a server-list UI sent by the server. When the player enters the details for the server they are looking to join, BedrockConnect sends a packet that transfers the player off of BedrockConnect and on to the server they entered.

To actually get on the BedrockConnect server on game console, we primarily utilize one of two methods. 

**DNS Method** - In Minecraft Bedrock Edition, players on any version can join the available 'Featured Servers'. By using a DNS server, we can redirect the domains used by these servers to instead go to the BedrockConnect serverlist server.

**"Add Friend" method** - By utilizing the 'Join Game' button in the game's Friends menu, we can redirect them to the BedrockConnect instance. This method is powered by [MCXboxBroadcast](https://github.com/rtm516/MCXboxBroadcast).

## Community-hosted instances

There are multiple BedrockConnect serverlist servers available hosted by the community that can be used. Currently, they do NOT share the same player database, so if you have added a server to your list on any of the given servers and connect to a different one, you will need to save that data again.

### List of Servers
<details><summary>(Click to expand to see list)</summary>
	
| IP Address | Gamertag | Location | Maintainer | Note |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| 104.238.130.180 | BCMain, BCMain1 | <img src="https://flagicons.lipis.dev/flags/4x3/us.svg" height="20"> | [Pugmatt](https://github.com/Pugmatt) | Main instance. Multiple load balanced servers. If issues occur on PS4/PS5 with DNS, try the ["Add Friend" Method](#add-friend-method), or replace the primary DNS address with 45.55.68.52. |
| 5.161.83.73 | Cybrancee | <img src="https://flagicons.lipis.dev/flags/4x3/us.svg" height="20"> | [Cybrancee](https://github.com/cybrancee) |  Located in Virginia, United States. No DNS service, only BedrockConnect server  |
| 213.171.211.142 | N/A | <img src="https://flagicons.lipis.dev/flags/4x3/gb.svg" height="20"> | [kmpoppe](https://github.com/kmpoppe) | No DNS service, only BedrockConnect server  |
| 217.160.58.93 | N/A | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [kmpoppe](https://github.com/kmpoppe) | No DNS service, only BedrockConnect server |
| 134.255.231.119 | bedrocklist | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [ZAP-Hosting](https://github.com/zaphosting) | MCXboxBroadcast instance is unofficially maintained by [Dinushay](https://github.com/dinushay) |
| 185.169.180.190 | N/A | <img src="https://flagicons.lipis.dev/flags/4x3/tr.svg" height="20"> | [hasankayra04](https://github.com/hasankayra04) | Dns service with NextDNS [Status Page](https://status.hasankayra04.com) (Listed as "Dns Listener") |
| 116.255.1.195 2401:d002:5c06:ca01:be24:11ff:fe78:41ad | TRBMCXB | <img src="https://flagicons.lipis.dev/flags/4x3/au.svg" height="20"> | [xavierhorwood](https://github.com/xavierhorwood) | Located in Brisbane, Australia, Dns service powered by PowerDNS |
| 140.238.204.159 | TRBSAU | <img src="https://flagicons.lipis.dev/flags/4x3/au.svg" height="20"> | [xavierhorwood](https://github.com/xavierhorwood) | Located in Sydney, Australia |

[Status Page for all public instances](https://bcstatus.teamriverbubbles.com/status/bedrock)

</details>


[Status Page for all public instances](https://bcstatus.teamriverbubbles.com/status/bedrock)

<sub>If you are currently hosting a BedrockConnect instance and are interested in adding it to this list, create a pull request adding it to the table above.</sub>

## Hosting your own BedrockConnect instance

### Running the BedrockConnect software

**Requirements:**
- Java 8 or higher

Download the latest release of the BedrockConnect serverlist software here: https://github.com/Pugmatt/BedrockConnect/releases

Run the jar with the following command
```
java -jar BedrockConnect-1.0-SNAPSHOT.jar
```

Alternatively, BedrockConnect can also be ran on Docker through the public image ```pugmatt/bedrock-connect```

```bash
docker run -p 19132:19132/udp pugmatt/bedrock-connect
```

### Configuration
[See wiki page here](https://github.com/Pugmatt/BedrockConnect/wiki/Configuration) for how to further configure BedrockConnect and a list of available settings.

### Setting up a join method for game consoles

After you set up BedrockConnect, you will need to set up a join method to connect to it on game consoles. Some helpful resources:

- Windows tutorial (DNS Method): https://www.youtube.com/watch?v=AW5X7-qnvLk

- Linux tutorial: https://github.com/Pugmatt/BedrockConnect/wiki/Setting-up-on-Linux

- "Add Friend" method - [MCXboxBroadcast repo](https://github.com/MCXboxBroadcast/Broadcaster?tab=readme-ov-file#standalone) (Install and set target server to BedrockConnect instance)

- If you're looking to host a DNS server from scratch for the DNS method, [click here details on what zones to configure](https://github.com/Pugmatt/BedrockConnect/wiki/Using-your-own-DNS-server)

- A docker-compose file with BedrockConnect and bind9 DNS included can be found in the [docker folder of the repo](https://github.com/Pugmatt/BedrockConnect/tree/master/docker), along with other docker resources.

## Libraries used
- [NukkitX Bedrock Protocol Library](https://github.com/NukkitX/Protocol)


## Sponsors
Sponsors go a long way in helping BedrockConnect's continued development and keeping server bills paid!

If you're interested in sponsoring projects like this one (thank you!) feel free to check out my Patreon - https://www.patreon.com/Pugmatt

<!-- ![Sponsors](https://raw.githubusercontent.com/Pugmatt/Pugmatt-SponsorKit/refs/heads/static/sponsors.part1.svg) -->

<!-- ### All Sponsors -->
<!-- ![Sponsors](https://raw.githubusercontent.com/Pugmatt/Pugmatt-SponsorKit/refs/heads/circle/sponsors.part1.svg) -->



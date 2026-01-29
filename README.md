# BedrockConnect

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[<img src="https://ko-fi.com/img/githubbutton_sm.svg" alt="Ko-fi" width="150">](https://ko-fi.com/Z8Z09Z56K)

<img src="https://i.imgur.com/H9zVzGT.png" alt="Bedrock Block" align="right" width="200">


BedrockConnect is a minimal *Minecraft: Bedrock Edition* server software that provides a server-list interface to players. Players can connect to any Bedrock Edition game servers, along with storing and managing an in-game list of their servers. 

This is primarily helpful on console versions of the game (Switch, Xbox, PlayStation) where a list for 3rd-party servers is not natively available in-game. This project aims to provide a straight-forward solution that any player can setup directly on their game console in a few minutes.

Demo video: https://www.youtube.com/watch?v=Uz-XYXAxd8Q

*<sub>BedrockConnect is and always has been free and open-source since its release in 2019. To ensure you are using the free tool/servers provided by this project, only use or download BedrockConnect resources found on this page/repository. We **do NOT** have an official mobile app on the Apple/Google Play store, and any app with the same name is NOT associated with this project.</sub>*

## ⭐ Quick setup

Below are instructions for setting up BedrockConnect on your game console, it typically takes only a few minutes to get setup and requires no downloads.

There are two primary methods available to join BedrockConnect:

### DNS Method (recommended)
<details><summary>📟 (Click to expand)</summary>

**Nintendo Switch**:
- Go into your console's internet settings, and set the primary DNS to 104.238.130.180 and secondary DNS to 8.8.8.8
- Open Minecraft and go to the "Servers" tab. Join a redirect-compatible featured server to open BedrockConnect
   - **Redirect-compatible servers**: Mineville, Lifeboat, Enchanted, Galaxite, The Hive
- Video walkthrough: https://www.youtube.com/watch?v=zalT_oR1nPM

**Xbox**:
- Go into your console's internet settings, and set the primary DNS to 104.238.130.180 and secondary DNS to 8.8.8.8
- Open Minecraft and go to the "Servers" tab. Join a redirect-compatible featured server to open BedrockConnect
   - **Redirect-compatible servers**: Mineville, Lifeboat, Enchanted, Galaxite, The Hive
- Video walkthrough: https://www.youtube.com/watch?v=g8mHvasVHMs

**PlayStation**:
- Go into your console's internet settings, and set the primary DNS to 45.55.68.52 and secondary DNS to 8.8.8.8 (See 'How to change DNS' guides below)
- Open Minecraft and go to the "Servers" tab. Join a redirect-compatible featured server to open BedrockConnect
   - **Redirect-compatible servers**: Mineville, Lifeboat, Enchanted, Galaxite, The Hive

<details><summary>How to change DNS on PS5 (Click to expand)</summary>
	
1. In the PS5 home screen, go to "Settings" and then "Network"
	
2. Select "Settings" and choose "Set up internet connection"

3. In "Advance Settings", set the "DNS settings" to "Manual"

4. Enter 45.55.68.52 for the primary DNS and 8.8.8.8 for the secondary DNS, and select "Ok"

5. Wait for connection to test

</details>

<details><summary>How to change DNS on PS4 (Click to expand)</summary>
	
1. In the PS4 home screen, go to "Settings" and then "Network"
	
2. Select "Set up internet connection"
	
3. Select your internet's connection type

4. When prompted for how to set up the internet connection, select "Custom"

5. Select your connection from the list

6. Select "Automatic" for "IP address settings"

7. Select "Do not specify" for "DHCP host name"

8. For the "DNS settings" screen, select “Manual”

9. Enter 45.55.68.52 for the primary DNS and 8.8.8.8 for the secondary DNS, and click "Next"

10. Select "Automatic" for "MTU settings"

11. Select "Do not use" for "Proxy server"

12. Test connection

</details>

</details>

#### "Add Friend" Method
<details><summary>👥 (Click to expand)</summary>

<sub>(This method utilizes [MCXboxBroadcast](https://github.com/rtm516/MCXboxBroadcast) to supply this join option)</sub>

**NOTE** - The bots for this method have limited friend slots and can be prone to slow down due to limitations set by Microsoft's friend system. It is recommended to first try the "DNS Method" before resorting to this method, as the "DNS Method" does not suffer from the same limitations. If you have already tried the DNS Method or want to try this method anyway, read on:
	
- In the Minecraft main menu, click "Play" and then go to the "Friends" tab, and click "Add Friend" or "Find Cross-Platform Friends" or "Search for players" (whichever is available on your game version)

- Search for any of the following gamer tags:
  	- ***BCMain*** / ***BCMain1*** / ***BCMain2*** / ***BCMain3*** / ***BCMain4***
  	
	... and add this user as friend

- Return to the Minecraft main menu, and wait about 30 seconds. Then click "Play" and return to the "Friends" tab

- Wait a moment, and you should soon see a joinable instance show up, "Join to Open Server List". Or, you should see BCMain under the "Online" section with a joinable instance. (If the join option doesn't appear, you may need to wait another minute for the bot to process the friend request, or try adding a different bot gamer tag from above)

- Join instance to connect to BedrockConnect server list

*In order to make room in the friendslist, main instance bots routinely removes players from it's list that are inactive for ~1-3 days (Threshold varies depending on current traffic the bot is getting) If this happens, simply add back the gamertag.*
</details>


If you're having trouble connecting to the serverlist, try running through the [troubleshooting page](https://github.com/Pugmatt/BedrockConnect/wiki/Troubleshooting)

## How does it work?

When a player connects to BedrockConnect, they are met with a server-list interface sent by the server. When the player enters the details for the server they are looking to join, BedrockConnect sends a packet that transfers the player off of BedrockConnect and on to the server they entered.

To access BedrockConnect on game consoles, we primarily utilize one of two methods:

- **DNS Method** - By using a custom DNS server, we can redirect the domains used by 'Featured Servers' to instead go to the BedrockConnect serverlist server. This allows players to connect through 'Featured Servers' entries.

- **"Add Friend" method** - By utilizing the 'Join Game' button in the game's Friends menu, we can redirect them to the BedrockConnect instance. This method is powered by [MCXboxBroadcast](https://github.com/rtm516/MCXboxBroadcast).

## Community-hosted instances

There are multiple BedrockConnect instances available hosted by the community available to use:

### List of Instances
<details><summary>📁 (Click to expand list)</summary>
	
| IP Address | DNS-Method Enabled | Gamertag (Add-Friend Method) | Location | Maintainer | Note |
| ------------- | ------------- | ------------- | ------------- | ------------- | ------------- |
| 104.238.130.180 | ✔️ | BCMain, BCMain1, BCMain2, BCMain3, BCMain4 | <img src="https://flagicons.lipis.dev/flags/4x3/us.svg" height="20"> | [Pugmatt](https://github.com/Pugmatt) | Main instance. Multiple load balanced servers. If issues occur on PS4/PS5 with DNS method, replace primary DNS address with 45.55.68.52 |
| 5.161.83.73 | | Cybrancee | <img src="https://flagicons.lipis.dev/flags/4x3/us.svg" height="20"> | [Cybrancee](https://github.com/cybrancee) |  Located in Virginia, United States  |
| 134.255.231.119 | ✔️ | | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [ZAP-Hosting](https://github.com/zaphosting) |  |
| 185.169.180.190 | ✔️ | HasanBC1 | <img src="https://flagicons.lipis.dev/flags/4x3/tr.svg" height="20"> | [hasankayra04](https://github.com/hasankayra04) | DNS service with NextDNS [Status Page](https://status.hasankayra04.com) (Listed as "Dns Resolver" & "BedrockConnect") |
| 213.171.211.142 | | | <img src="https://flagicons.lipis.dev/flags/4x3/gb.svg" height="20"> | [kmpoppe](https://github.com/kmpoppe) |  |
| 217.160.58.93 | | | <img src="https://flagicons.lipis.dev/flags/4x3/de.svg" height="20"> | [kmpoppe](https://github.com/kmpoppe) | |
| 2.59.252.99 | | | <img src="https://flagicons.lipis.dev/flags/4x3/kr.svg" height="20"> | [Minjae](https://github.com/minj-ae) | Located in Seoul, South Korea |
</details>


[Status Page for public instances](https://bcstatus.xyz/status/bedrock)

Currently, the instances do NOT share the same player database, so if you have added a server to your list on any of the given servers and connect to a different one, you will need to save that data again.

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

If your console is Playstation or Xbox and on the same network/LAN as the machine running BedrockConnect, you can join BedrockConnect as-is via LAN by going to the "Worlds" tab in-game. 

If you are on a different game console or want to connect outside of your network, you will need to set-up a join method by checking out the resources below:

<details><summary>📁 (Click to expand resources)</summary>

- "Add Friend" method
    - Follow ["Standalone" install instructions from MCXboxBroadcast repo](https://github.com/MCXboxBroadcast/Broadcaster?tab=readme-ov-file#standalone) and set target server to your BedrockConnect instance
      - If game console's on the same network as your hosting machine of BedrockConnect, IP would be the [local address of the hosting machine](https://www.whatismybrowser.com/detect/what-is-my-local-ip-address/)
      - Or, for outside connections, IP would be your [external address](https://whatismyipaddress.com/) (Will also need to port-forward 19132)
 
- Windows tutorial (DNS Method) (LAN): https://www.youtube.com/watch?v=AW5X7-qnvLk

- Linux tutorial: https://github.com/Pugmatt/BedrockConnect/wiki/Setting-up-on-Linux

- A docker-compose file with BedrockConnect and bind9 DNS included can be found in the [docker folder of the repo](https://github.com/Pugmatt/BedrockConnect/tree/master/docker), along with other docker resources.

- If you're looking to host a DNS server using a different DNS software, [click here details on what zones to configure](https://github.com/Pugmatt/BedrockConnect/wiki/Using-your-own-DNS-server)

</details>

## Libraries used
- [NukkitX Bedrock Protocol Library](https://github.com/NukkitX/Protocol)


## Thank you Sponsors!
Sponsors go a long way in helping BedrockConnect's continued development and keeping server bills paid!

If you're interested in sponsoring projects like this one (thank you!) feel free to check out my [GitHub Sponsors](https://github.com/sponsors/Pugmatt) or [Patreon](https://www.patreon.com/Pugmatt)

<p align="center">
<img src="https://raw.githubusercontent.com/Pugmatt/Pugmatt-SponsorKit/refs/heads/static/sponsors.svg" alt="Silver Sponsors">
</p>

<div align="center">
<h3>All Sponsors</h3>
</div>
<p align="center">
<img src="https://raw.githubusercontent.com/Pugmatt/Pugmatt-SponsorKit/refs/heads/circle/sponsors.svg" alt="All Sponsors"">
</p>

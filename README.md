[![GitHub last commit](https://img.shields.io/github/last-commit/mastercake10/TelegramChat.svg)](https://github.com/mastercake10/TelegramChat/commits/master)
[![build status](https://ci.spaceio.xyz/buildStatus/icon?job=TelegramChat)](https://ci.spaceio.xyz/job/TelegramChat/lastBuild/xyz.spaceio$telegramchat/)
[![discord](https://discordapp.com/api/guilds/330725294749122561/widget.png)](https://discord.gg/3xgsPh8)
[![view on SpigotMC](https://img.shields.io/badge/view%20on-spigotmc-orange.svg)](https://www.spigotmc.org/resources/telegramchat.16576/)

![resource icon](https://www.spigotmc.org/data/resource_icons/16/16576.jpg?1476392100)

## Welcome to the TelegramChat GitHub repository!
TelegramChat is a Bukkit plugin compatible with Paper/Spigot versions 1.7 through 1.18.*, that connects Telegram with Minecraft.

## Usage
1. Create a new bot by messaging the @BotFather and following the instructions.
2. Obtain the token (`/tokentype` in Telegram) and type  `/linktelegram <token>` to link the newly created Bot to your Server. This needs to be done once.
3. Each user needs to be linked to their Telegram account in order to chat mc <-> telegram.
 
### Private chats
1. Your users need to type `/telegram` in-game to get a temporary code for linking
2. The code is then sent to the Telegram bot.

### Group chats
1. As an admin, you need to set the privacy setting to disabled using the BotFather. This is done by typing `/setprivacy` and then disabled.
2. Users just need to join the group to see the MC chat. They might want to link their account by posting their /linktelegram code in the group chat.

## Developers
* [Jenkins](https://ci.spaceio.xyz/job/TelegramChat/lastBuild/xyz.spaceio$telegramchat/) latest builds, including unstable ones
* [Repository](https://repo.spaceio.xyz/#browse/browse:maven-snapshots:xyz%2Fspaceio%2Ftelegramchat)

### API
```xml
<repository>
  <id>spaceio-repo</id>
  <url>https://repo.spaceio.xyz/repository/maven-public/</url>
</repository>

<dependencies>
  <dependency>
    <groupId>xyz.spaceio</groupId>
    <artifactId>telegramchat</artifactId>
    <version>VERSION_HERE-SNAPSHOT</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

## Developers
* [Jenkins](https://ci.spaceio.xyz/job/TelegramChat/lastBuild/xyz.spaceio$telegramchat/) latest builds, including unstable ones
* [Repository](https://repo.spaceio.xyz/#browse/browse:maven-snapshots:xyz%2Fspaceio%2Ftelegramchat)

### API
```xml
<repository>
  <id>spaceio-repo</id>
  <url>https://repo.spaceio.xyz/repository/maven-public/</url>
</repository>

<dependencies>
  <dependency>
    <groupId>xyz.spaceio</groupId>
    <artifactId>telegramchat</artifactId>
    <version>VERSION_HERE-SNAPSHOT</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

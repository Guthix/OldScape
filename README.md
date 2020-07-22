# OldScape Server
[![Build Status](https://github.com/guthix/oldscape-server/workflows/Build/badge.svg)](https://github.com/guthix/Oldscape-Server/actions?workflow=Build)
[![Revision](https://img.shields.io/badge/revision-189-blueviolet)](https://oldschool.runescape.wiki/w/Update:Chambers_of_Xeric_Improvements)
[![License](https://img.shields.io/github/license/guthix/OldScape-Server)](https://github.com/guthix/OldScape-Server/blob/master/LICENSE)
[![JDK](https://img.shields.io/badge/JDK-11%2B-blue)](https://openjdk.java.net/projects/jdk/11/)
[![Discord](https://img.shields.io/discord/538667877180637184?color=%237289da&logo=discord)](https://discord.gg/AFyGxNp)


OldScape-server is an emulator that simulates the oldschool runescape server. The goal of the project is to closely 
emulated the server and improve on it whenever possible. OldScape-server puts an emphasize on ease of use for content
developers.

## How To Run
1. Download a cache and put it under `src/main/resources/cache` together with an `xteas.json`
2. Download/Decompile a client
3. Modify the RSA keys in the client to match the ones under `src/main/resources/Config.yaml`
4. Execute `gradlew run`

If you run into any issues you can get help from our [discord](https://discord.gg/AFyGxNp) channel.
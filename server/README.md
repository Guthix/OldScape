# OldScape Server
OldScape-server is an emulator that simulates the oldschool runescape server. The goal of the project is to closely 
emulated the server and improve on it whenever possible. OldScape-server puts an emphasize on ease of use for content
developers.

## How To Run
1. Download a cache and put it under `src/main/resources/cache` together with an `xteas.json`
2. Download/Decompile a client
3. Modify the RSA keys in the client to match the ones under `src/main/resources/Config.yaml`
4. Execute `gradlew run`

If you run into any issues you can get help from our [discord](https://discord.gg/AFyGxNp) channel.
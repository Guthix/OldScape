# OldScape Server
OldScape-server is an emulator that simulates the Oldschool Runescape server. The goal of the project is to closely 
emulated the server and improve on it whenever possible. OldScape-server puts an emphasis on ease of use for content
developers.

## Requirements
* JDK 11+
* PostgresSQL 13.1+

### How To Run
1. Download a cache and put it under `src/main/resources/cache` together with an `xteas.json`
2. Download and Decompile a client
3. Download, Install and run PostgresSQL
3. Modify `src/main/resources/Config.yaml` when needed, the RSA keys should match the ones in the client and the 
database configuration should match your local PostgresSQL configuration.
4. Execute `gradlew run`
5. Create a Player inside the `player` table in your PostgresSQL database.
6. Login to the game using your client with the account you just created.

If you run into any issues you can get help from our [discord](https://discord.gg/AFyGxNp) channel.

### Development
We recommend using [IntelliJ](https://www.jetbrains.com/idea/). To get code highlighting in the Kotlin scripts downgrade
your Kotlin plugin version to `1.4.10`(see this [issue](https://youtrack.jetbrains.com/issue/KTIJ-417) for more info).
Make sure to import the project as a Gradle project and if you still don't get highlighting in the Kotlin scripts build
the full project first before opening the scripts in Intellij.
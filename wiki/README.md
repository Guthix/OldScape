# OldScape Wiki
[![Build Status](https://github.com/guthix/oldscape-wiki/workflows/Build/badge.svg)](https://github.com/guthix/Oldscape-Wiki/actions?workflow=Build)
[![License](https://img.shields.io/github/license/guthix/OldScape-Wiki)](https://github.com/guthix/OldScape-Wiki/blob/master/LICENSE)
[![JDK](https://img.shields.io/badge/JDK-11%2B-blue)](https://openjdk.java.net/projects/jdk/11/)
[![Discord](https://img.shields.io/discord/538667877180637184?color=%237289da&logo=discord)](https://discord.gg/AFyGxNp)

A library for retrieving data from the [Oldschool Runescape Wiki](https://oldschool.runescape.wiki/).

This repository contains 3 different projects.
* [Parser](https://github.com/guthix/OldScape-Wiki/tree/master/parser) a library for parsing wikitext.
* [Downloader](https://github.com/guthix/OldScape-Wiki/tree/master/downloader) a library for downloading wiki pages.
[OldScape-Server](https://github.com/guthix/OldScape-Server).

### Gradle
```Kotlin
implementation(group = "io.guthix.oldscape", name = "oldscape-wiki-parser", version = "0.1.0")
```
```Kotlin
implementation(group = "io.guthix.oldscape", name = "oldscape-wiki-downloader", version = "0.1.0")
```
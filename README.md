# OldScape Server

Oldschool Runescape Server Emulation. There OSRS server provides 2 types
of services:

* A game service which is used for game state synchronization
* Jagex Store 5 (JS5) file server

Both services listen to the same port, when connecting to the server the
type of service can be chosen by the client.

## Game state synchronization service
This is the general game server. The server maintains the game state and
is able to take commands from the connected clients. The server 
synchronizes the game state of all the clients at a tick of 600ms. The 
server can be configured via a config file.

## Jagex Store 5 file server
An on demand services for streaming game assets. The OldScape JS5 file
server is an OS-level zero copy implementation. When building a cache
should be present in the `main/resources/cache` folder.
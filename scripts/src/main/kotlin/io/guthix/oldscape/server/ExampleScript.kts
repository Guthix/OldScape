import io.guthix.oldscape.server.event.ExampleEvent

on(ExampleEvent(3)).then {
    println(player.index)
    println(world.isFull)
    println(event.someInteger)
}
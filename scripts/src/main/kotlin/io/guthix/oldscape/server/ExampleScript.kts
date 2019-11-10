import io.guthix.oldscape.server.event.ExampleEvent

on(ExampleEvent::class).where { testValue == 3 }.then {
    println(testValue)
    println(world.isFull)
    println(player.index)
}
package io.guthix.oldscape.server.equipment

import io.guthix.oldscape.server.event.*
import io.guthix.oldscape.server.net.state.game.outp.PlayerInfoPacket
import io.guthix.oldscape.server.world.entity.character.player.HeadEquipment

on(InventoryHeadClickEvent::class).where { event.option == "Wear" }.then {
    val obj = player.inventory.removeObject(event.inventorySlot) ?: return@then
    player.appearance.equipment.head = HeadEquipment(event.objBlueprint, 1)
    player.updateFlags.add(PlayerInfoPacket.appearance)
    player.equipmentInventory.setObject(event.objBlueprint.slot.id, obj)
}
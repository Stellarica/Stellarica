package net.stellarica.server.craft

import net.kyori.adventure.audience.Audience
import net.stellarica.common.coordinate.BlockPosition
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class Starship: BasicCraft(), Rideable, CraftContainer, Pilotable {

	override var pilot: Player? = null
		private set
	override val passengers: MutableList<LivingEntity> = mutableListOf()

	override fun addPassenger(passenger: LivingEntity) {
		TODO("Not yet implemented")
	}

	override fun removePassenger(passenger: LivingEntity) {
		TODO("Not yet implemented")
	}

	override fun audiences(): MutableIterable<Audience> {
		return passengers.toMutableList()
	}

	override fun pilot(pilot: Player) {
		this.pilot = pilot
	}

	override fun unpilot() {
		this.pilot = null
	}

	override fun addSubCraft(craft: Craft) {
		TODO("Not yet implemented")
	}

	override fun removeSubCraft(craft: Craft) {
		TODO("Not yet implemented")
	}

	override fun contains(block: BlockPosition): Boolean {
		return super.contains(block) || TODO() // check subcrafts
	}

	fun detect() {
		TODO()
	}
}
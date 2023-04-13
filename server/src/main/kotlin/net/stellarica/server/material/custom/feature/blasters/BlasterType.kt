package net.stellarica.server.material.custom.feature.blasters

import net.stellarica.server.projectiles.LinearProjectile
import net.stellarica.server.projectiles.Projectile
import net.stellarica.server.material.custom.items.CustomItem
import net.stellarica.server.material.custom.items.CustomItems
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound

@Suppress("unused")
enum class BlasterType(
	val item: CustomItem,
	val projectile: Projectile<*>,
	val powerCost: Int,
	val cooldown: Int
) {
	TEST_BLASTER(
		CustomItems.TEST_BLASTER,
		LinearProjectile(
			0f,
			4.0,
			Particle.REDSTONE,
			Sound.BLOCK_BAMBOO_HIT,
			10,
			2.0,
			Particle.DustOptions(Color.LIME, 0.5f)
		),
		3,
		10
	)
}
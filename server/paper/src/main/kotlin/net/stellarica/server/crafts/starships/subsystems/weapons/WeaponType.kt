package net.stellarica.server.crafts.starships.subsystems.weapons

import net.minecraft.resources.ResourceLocation
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.LightCannonProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.Projectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.TestProjectile
import net.stellarica.server.multiblocks.MultiblockHandler
import kotlin.math.PI

enum class WeaponType(
	val projectile: Projectile,
	val direction: OriginRelative,
	val cone: Double,
	val mount: OriginRelative,
	val priority: Int,

	private val multiblockId: ResourceLocation
) {
	TEST_WEAPON(TestProjectile, OriginRelative(3, 0, 0), PI / 8, OriginRelative(2, 0, 0), 1, identifier("test_weapon"));
	// LIGHT_CANNON(LightCannonProjectile, OriginRelative(6, 0, 0), PI / 8, OriginRelative(5, 0, 0), 1, identifier("light_cannon"));

	val multiblockType by lazy {
		MultiblockHandler.types.first { it.id == multiblockId }
	}
}

package net.stellarica.server.crafts.pilotables.starships.subsystems.weapons

import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.crafts.pilotables.starships.subsystems.weapons.projectiles.LightCannonProjectile
import net.stellarica.server.crafts.pilotables.starships.subsystems.weapons.projectiles.Projectile
import net.stellarica.server.crafts.pilotables.starships.subsystems.weapons.projectiles.TestProjectile
import net.stellarica.server.multiblocks.Multiblocks
import kotlin.math.PI

enum class WeaponType(
	val projectile: Projectile,
	val direction: OriginRelative,
	val cone: Double,
	val mount: OriginRelative,
	val priority: Int,

	private val multiblockId: String
) {
	TEST_WEAPON(TestProjectile, OriginRelative(3, 0, 0), PI / 8, OriginRelative(2, 0, 0), 1, "test_weapon"),
	LIGHT_CANNON(LightCannonProjectile, OriginRelative(6, 0, 0), PI / 8, OriginRelative(5, 0, 0), 1, "light_cannon");

	val multiblockType by lazy {
		Multiblocks.types.first { it.name == multiblockId }
	}
}

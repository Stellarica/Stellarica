package io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.weapons

import io.github.hydrazinemc.hydrazine.common.utils.OriginRelative
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.weapons.projectiles.Projectile
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.weapons.projectiles.TestProjectile
import io.github.hydrazinemc.hydrazine.server.multiblocks.Multiblocks
import kotlin.math.PI

enum class WeaponType(
	val projectile: Projectile,
	val direction: OriginRelative,
	val cone: Double,
	val mount: OriginRelative,
	val priority: Int,

	private val multiblockId: String
) {
	TEST_WEAPON(TestProjectile, OriginRelative(3, 0, 0), PI / 8, OriginRelative(2, 0, 0),1,"test_weapon");

	val multiblockType by lazy { // is this even safe, considering multiblock types can be reloaded?
		Multiblocks.types.first { it.name == multiblockId }
	}
}
package io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.weapons

import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.weapons.projectiles.Projectile
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.weapons.projectiles.TestProjectile
import io.github.hydrazinemc.hydrazine.server.multiblocks.Multiblocks

enum class WeaponType(val projectile: Projectile, private val multiblockId: String) {
	TEST_WEAPON(TestProjectile, "test_weapon");

	val multiblockType by lazy { // is this even safe, considering multiblock types can be reloaded?
		Multiblocks.types.first { it.name == multiblockId }
	}
}
package net.stellarica.server.crafts.starships.subsystems.weapons

import net.minecraft.resources.ResourceLocation
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.AcceleratingProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.InstantProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.LinearProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.Projectile
import net.stellarica.server.multiblocks.MultiblockHandler
import kotlin.math.PI

enum class WeaponType(
	val projectile: Projectile<*>,
	val direction: OriginRelative,
	val cone: Double,
	val mount: OriginRelative,
	val priority: Int,

	private val multiblockId: ResourceLocation
) {
	TEST_LINEAR_WEAPON(
		LinearProjectile(30, 3.0),
		OriginRelative(3, 0, 0),
		PI / 8,
		OriginRelative(2, 0, 0),
		1,
		identifier("test_linear_weapon")
	),
	TEST_ACCELERATING_WEAPON(
		AcceleratingProjectile(40, 0.2, 0.2),
		OriginRelative(3, 0, 0),
		PI / 8,
		OriginRelative(2, 0, 0),
		1,
		identifier("test_accelerating_weapon")
	),
	TEST_INSTANT_WEAPON(
		InstantProjectile(100),
		OriginRelative(3, 0, 0),
		PI / 8,
		OriginRelative(2, 0, 0),
		1,
		identifier("test_instant_weapon")
	);

	val multiblockType by lazy {
		MultiblockHandler.types.first { it.id == multiblockId }
	}
}

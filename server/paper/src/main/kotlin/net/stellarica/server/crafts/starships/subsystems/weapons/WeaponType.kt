package net.stellarica.server.crafts.starships.subsystems.weapons

import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.AcceleratingProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.InstantProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.RailgunBullet
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.LinearProjectile
import net.stellarica.server.crafts.starships.subsystems.weapons.projectiles.Projectile
import net.stellarica.server.multiblocks.MultiblockType
import net.stellarica.server.multiblocks.Multiblocks
import kotlin.math.PI

enum class WeaponType(
	val projectile: Projectile<*>,
	val direction: OriginRelative,
	val cone: Double,
	val mount: OriginRelative,
	val priority: Int,
	val multiblock: MultiblockType
) {
	TEST_LINEAR_WEAPON(
		LinearProjectile(30, 3.0),
		OriginRelative(3, 0, 0),
		PI / 4,
		OriginRelative(2, 0, 0),
		1,
		Multiblocks.TEST_LINEAR_WEAPON
	),
	TEST_ACCELERATING_WEAPON(
		AcceleratingProjectile(40, 0.2, 0.2),
		OriginRelative(3, 0, 0),
		PI / 4,
		OriginRelative(2, 0, 0),
		1,
		Multiblocks.TEST_ACCELERATING_WEAPON
	),
	TEST_INSTANT_WEAPON(
		InstantProjectile(100),
		OriginRelative(3, 0, 0),
		PI / 4,
		OriginRelative(2, 0, 0),
		1,
		Multiblocks.TEST_INSTANT_WEAPON
	),
	DEMOMAN_RAILGUN(
		RailgunBullet(200),
		OriginRelative(6, 0, 0),
		PI / 5,
		OriginRelative(5, 0, 0),
		1,
		Multiblocks.DEMOMAN_RAILGUN
	);
}

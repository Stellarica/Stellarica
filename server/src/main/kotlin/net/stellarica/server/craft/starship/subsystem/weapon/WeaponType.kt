package net.stellarica.server.craft.starship.subsystem.weapon

import net.stellarica.common.util.OriginRelative
import net.stellarica.server.multiblock.MultiblockType
import net.stellarica.server.multiblock.type.WeaponMultiblocks
import net.stellarica.server.projectile.AcceleratingProjectile
import net.stellarica.server.projectile.InstantProjectile
import net.stellarica.server.projectile.LinearProjectile
import net.stellarica.server.projectile.NovanProjectileIdkWhatToCallIt
import net.stellarica.server.projectile.Projectile
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import kotlin.math.PI

@Suppress("unused")
enum class WeaponType(
	val projectile: Projectile<*>,
	val direction: OriginRelative,
	val cone: Double,
	val mount: OriginRelative,
	val priority: Int,
	val multiblock: MultiblockType
) {
	LIGHT_RAILGUN(
		LinearProjectile(
			1f,
			5.0,
			Particle.FLAME,
			Sound.ITEM_TOTEM_USE,
			120,
			5.0
		),
		OriginRelative(8, 0, 0),
		PI / 6,
		OriginRelative(7, 0, 0),
		1,
		WeaponMultiblocks.LIGHT_RAILGUN
	),
	PLASMA_CANNON(
		AcceleratingProjectile(
			2f,
			8.0,
			Particle.SONIC_BOOM,
			Sound.BLOCK_CONDUIT_ACTIVATE,
			50,
			0.2,
			0.2
		),
		OriginRelative(8, 0, 0),
		PI / 8,
		OriginRelative(7, 0, 0),
		1,
		WeaponMultiblocks.PLASMA_CANNON
	),
	BATTLE_CANNON(
		NovanProjectileIdkWhatToCallIt(
			4f,
			2f,
			8.0,
			Particle.CAMPFIRE_COSY_SMOKE,
			Sound.ENTITY_GENERIC_EXPLODE,
			80,
			3.0
		),
		OriginRelative(9, 0, 0),
		PI / 6,
		OriginRelative(7, 0, 0),
		1,
		WeaponMultiblocks.BATTLE_CANNON
	),
	PULSE_LASER(
		InstantProjectile(
			2f,
			3.0,
			Particle.REDSTONE,
			Sound.ENTITY_BEE_HURT,
			120,
			Particle.DustOptions(Color.RED, 1f)
		),
		OriginRelative(5, 0, 0),
		PI / 7,
		OriginRelative(4, 0, 0),
		1,
		WeaponMultiblocks.PULSE_LASER
	);
}

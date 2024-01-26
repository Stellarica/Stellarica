package net.stellarica.server.craft

open class BasicSubcraft(
	override val parent: CraftContainer,
) : BasicCraft(), Subcraft {
	override var blockCount: Int = 0
		protected set


}

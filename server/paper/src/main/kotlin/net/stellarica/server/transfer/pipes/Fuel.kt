package net.stellarica.server.transfer.pipes

import net.stellarica.server.transfer.Node
import net.stellarica.server.transfer.Transferable

@JvmInline
value class Fuel(val amount: Int): Transferable {
	override fun shouldTransfer(from: Node<out Transferable>, to: Node<out Transferable>): Boolean {
		return (from.contents as Fuel).amount > (to.contents as Fuel).amount
	}
}
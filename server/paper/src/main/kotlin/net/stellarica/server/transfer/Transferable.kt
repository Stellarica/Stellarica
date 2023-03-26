package net.stellarica.server.transfer

interface Transferable {
	fun transfer(from: Node<Transferable>, to: Node<Transferable>) {
		if (!shouldTransfer(from, to)) return

	}

	fun shouldTransfer(from: Node<out Transferable>, to: Node<out Transferable>): Boolean
}
package net.stellarica.server.transfer

abstract class Node<T: Transferable>(val network: NodeNetwork<T>) {
	abstract val contents: T
	val connections = mutableSetOf<Node<T>>()

	fun tick() {
		for (node in connections) {
			if (contents.shouldTransfer(this, node)) {
				TODO("transfer to that node")
			}
		}
	}

	abstract fun attemptMakeConnections()
	abstract fun pruneInvalidConnections()
}
package net.stellarica.server.transfer

abstract class NodeNetwork<T: Transferable> {
	abstract val primaryNode: Node<T>

	abstract fun detect()

	fun tick() {
		
	}

	private fun transferDown(node: Node<T>) {
		node.transfer().forEach { transferDown(it) }
	}

	private fun validateDown(node: Node<T>) {
		node.validateOutgoingConnections()
		node.outgoingConnections.forEach { validateDown(it) }
	}
}
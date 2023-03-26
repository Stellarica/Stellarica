package net.stellarica.server.transfer

abstract class NodeNetwork<T: Transferable> {
	val inputs = mutableSetOf<Node<T>>()

	abstract fun detect()

	fun tick() {
		for (input in inputs) {
			validateDown(input)
			transferDown(input)
		}
	}

	private fun transferDown(node: Node<T>) {
		node.transfer().forEach { transferDown(it) }
	}

	private fun validateDown(node: Node<T>) {
		node.validateOutgoingConnections()
		node.outgoingConnections.forEach { validateDown(it) }
	}
}
package net.stellarica.server.transfer

abstract class NodeNetwork<T: Transferable> {
	abstract val primaryNode: Node<T>

	abstract fun detect()

	fun tick() {
		iterate(primaryNode) {

		}
	}

	fun validate() {
		iterate(primaryNode) { node ->
			node.outgoingConnections.forEach { connection ->
				if (!connection.isValid()) {
					throw IllegalStateException("Invalid connection: $connection")
				}
			}
		}
	}

	fun transfer() {
		iterate(primaryNode) { node ->
			node.outgoingConnections.forEach { connection ->
				TODO()
			}
		}
	}

	private fun iterate(node: Node<T>, action: (Node<T>) -> Unit){
		action(node)
		node.outgoingConnections.forEach {
			iterate(it.to, action)
		}
	}
}
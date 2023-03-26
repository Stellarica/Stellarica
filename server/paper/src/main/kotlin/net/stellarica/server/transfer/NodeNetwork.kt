package net.stellarica.server.transfer

abstract class NodeNetwork<T: Transferable> {
	protected val nodes = mutableSetOf<Node<T>>()

	fun detectNodes(node: Node<T>) {
		node.attemptMakeConnections()
		for (connection in node.connections.filterNot { it in nodes }) {
			nodes.add(connection)
			detectNodes(connection)
		}
	}

	fun tickNodes(node: Node<T>, tickedNodes: MutableSet<Node<T>>) {
		node.tick()
		tickedNodes.add(node)
		for (connection in node.connections.filterNot { it in tickedNodes }) {
			tickNodes(connection, tickedNodes)
		}
	}

	fun tickNodes(root: Node<T>) {
		tickNodes(root, mutableSetOf())
	}

	abstract fun tick()
}
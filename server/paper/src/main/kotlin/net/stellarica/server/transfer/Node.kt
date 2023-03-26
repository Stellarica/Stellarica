package net.stellarica.server.transfer

abstract class Node<T: Transferable> {
	var contents: T? = null
	val incomingConnections = mutableSetOf<Node<T>>()
	val outgoingConnections = mutableSetOf<Node<T>>()

	/** Validate all downstream connections, and remove any that are invalid. */
	abstract fun validateOutgoingConnections()

	/**
	 * Tranfer [contents] to outgoing connections.
	 * @return the nodes that were transferred to.
	 */
	abstract fun transfer(): Set<Node<T>>
}
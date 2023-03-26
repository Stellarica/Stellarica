package net.stellarica.server.transfer

interface Node<T: Transferable> {
	var contents: T?
	val incomingConnections: MutableSet<Connection<T>>
	val outgoingConnections: MutableSet<Connection<T>>
}
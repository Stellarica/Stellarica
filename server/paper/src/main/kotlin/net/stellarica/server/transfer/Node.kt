package net.stellarica.server.transfer

abstract class Node<T: Transferable> {
	var contents: T? = null
	val connections = mutableSetOf<Node<T>>()
}
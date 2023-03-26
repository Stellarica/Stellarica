package net.stellarica.server.transfer

abstract class NodeNetwork<T: Transferable> {
	val nodes = mutableSetOf<Node<T>>()

	abstract fun detect()

	fun validate

}
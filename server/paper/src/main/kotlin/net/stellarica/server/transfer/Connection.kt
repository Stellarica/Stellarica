package net.stellarica.server.transfer

interface Connection<T: Transferable> {
	var from: Node<T>
	var to: Node<T>

	fun isValid(): Boolean
}
package net.stellarica.server.transfer.pipes

import net.stellarica.server.transfer.Transferable

@JvmInline
value class Fuel(val amount: Int): Transferable {

}
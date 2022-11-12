package io.github.hydrazinemc.hydrazine.multiblocks.events

import io.github.hydrazinemc.hydrazine.events.HydrazineCancellableEvent
import io.github.hydrazinemc.hydrazine.multiblocks.MultiblockInstance

class MultiblockDetectEvent(val multiblock: MultiblockInstance): HydrazineCancellableEvent()
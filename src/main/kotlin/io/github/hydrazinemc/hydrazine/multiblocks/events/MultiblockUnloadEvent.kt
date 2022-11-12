package io.github.hydrazinemc.hydrazine.multiblocks.events

import io.github.hydrazinemc.hydrazine.events.HydrazineEvent
import io.github.hydrazinemc.hydrazine.multiblocks.MultiblockInstance

class MultiblockUnloadEvent(val multiblock: MultiblockInstance): HydrazineEvent()
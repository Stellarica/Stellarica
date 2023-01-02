import io.github.hydrazinemc.hydrazine.server.crafts.pilotables
import io.github.hydrazinemc.hydrazine.server.HydrazineServer.Companion.pilotedCrafts
import io.github.hydrazinemc.hydrazine.server.crafts.Craft
import io.github.hydrazinemc.hydrazine.server.utils.AlreadyPilotedException
import io.github.hydrazinemc.hydrazine.server.utils.locations.BlockLocation
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class SomeListener: Listener {
  @EventHandler
  fun onpilotDeathEvent(event: pilotDeathEvent) {
deactivateCraft()
  }
}

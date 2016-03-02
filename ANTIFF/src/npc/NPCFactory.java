package npc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class NPCFactory
  implements Listener
{
  private final Plugin plugin;
  private final NPCNetworkManager networkManager;
  
  public NPCFactory(Plugin plugin)
  {
    this.plugin = plugin;
    this.networkManager = new NPCNetworkManager();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }
  
  public NPC spawnHumanNPC(Location location, NPCProfile profile)
  {
    World world = location.getWorld();
    WorldServer worldServer = ((CraftWorld)world).getHandle();
    NPCEntity entity = new NPCEntity(world, profile, this.networkManager);
    entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    worldServer.addEntity(entity);
    worldServer.players.remove(entity);
    entity.getBukkitEntity().setMetadata("NPC", new FixedMetadataValue(this.plugin, Boolean.valueOf(true)));
    return entity;
  }
  
  public NPC getNPC(Entity entity)
  {
    if (!isNPC(entity)) {
      return null;
    }
    NPCEntity npcEntity = (NPCEntity)((CraftEntity)entity).getHandle();
    return npcEntity;
  }
  
  public List<NPC> getNPCs()
  {
    List<NPC> npcList = new ArrayList();
    for (World world : Bukkit.getWorlds()) {
      npcList.addAll(getNPCs(world));
    }
    return npcList;
  }
  
  public List<NPC> getNPCs(World world)
  {
    List<NPC> npcList = new ArrayList();
    for (Entity entity : world.getEntities()) {
      if (isNPC(entity)) {
        npcList.add(getNPC(entity));
      }
    }
    return npcList;
  }
  
  public boolean isNPC(Entity entity)
  {
    return entity.hasMetadata("NPC");
  }
  
  public void despawnAll()
  {
    for (World world : ) {
      despawnAll(world);
    }
  }
  
  public void despawnAll(World world)
  {
    for (Entity entity : world.getEntities()) {
      if (entity.hasMetadata("NPC")) {
        entity.remove();
      }
    }
  }
  
  @EventHandler
  public void onPluginDisable(PluginDisableEvent event)
  {
    if (event.getPlugin().equals(this.plugin)) {
      despawnAll();
    }
  }
}
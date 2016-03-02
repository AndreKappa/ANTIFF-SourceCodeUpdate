package me.heirteir;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import commands.Commands;
import config.Configurations;
import config.CreateConfig;
import listeners.PlayerListener;
import combat.CombatListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main
  extends JavaPlugin
  implements Listener
{
  public CombatListener combat;
  private PlayerListener plr;
  
  public void onEnable()
  {
    if (!checkDependencies())
    {
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }
    CreateConfig.updateConfig(this);
    
    Configurations.reloadMessages(this);
    
    this.plr = new PlayerListener(this, hasEssentials());
    this.combat = new CombatListener(this);
    
    initUpdater();
    
    Bukkit.getPluginManager().registerEvents(this, this);
    Bukkit.getPluginManager().registerEvents(this.combat, this);
    Bukkit.getPluginManager().registerEvents(this.plr, this);
    if (Configurations.isGENERATE_LOG()) {
      generateHackerFile();
    }
    getCommand("antiff").setExecutor(new Commands(this));
  }
  
  public boolean checkDependencies()
  {
    PluginManager pm = Bukkit.getPluginManager();
    if (Configurations.allowAntiLag())
    {
      if ((pm.getPlugin("WorldGuard") != null) && (pm.getPlugin("WorldGuard").isEnabled()))
      {
        PlayerTeleportEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("WorldGuard"));
        PlayerMoveEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("WorldGuard"));
      }
      if ((pm.getPlugin("Essentials") != null) && (pm.getPlugin("Essentials").isEnabled()))
      {
        PlayerTeleportEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("Essentials"));
        PlayerMoveEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("Essentials"));
        PlayerInteractEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("Essentials"));
        BlockBreakEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("Essentials"));
      }
      if ((pm.getPlugin("WorldEdit") != null) && (pm.getPlugin("WorldEdit").isEnabled()))
      {
        PlayerTeleportEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("WorldEdit"));
        PlayerMoveEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("WorldEdit"));
        PlayerInteractEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("WorldEdit"));
        BlockBreakEvent.getHandlerList().unregister(Bukkit.getPluginManager().getPlugin("WorldEdit"));
      }
    }
    if ((pm.getPlugin("ProtocolLib") == null) || (!pm.getPlugin("ProtocolLib").isEnabled()))
    {
      Logger logger = Bukkit.getLogger();
      logger.log(Level.SEVERE, "#<|>><><><><><><><><><><><><><><><<<|>#");
      logger.log(Level.SEVERE, "#<|>>=============================<<|>#");
      logger.log(Level.SEVERE, "#<|>>======[ANTI-FORCEFIELD]======<<|>#");
      logger.log(Level.SEVERE, "#<|>>========[ProtocolLib]========<<|>#");
      logger.log(Level.SEVERE, "#<|>>=Not Found or isn't enabled!=<<|>#");
      logger.log(Level.SEVERE, "#<|>>==Please get Protocolib or===<<|>#");
      logger.log(Level.SEVERE, "#<|>>====Plugin won't be usable===<<|>#");
      logger.log(Level.SEVERE, "#<|>>======[ANTI-FORCEFIELD]======<<|>#");
      logger.log(Level.SEVERE, "#<|>>=============================<<|>#");
      logger.log(Level.SEVERE, "#<|>><><><><><><><><><><><><><><><<<|>#");
      return false;
    }
    return true;
  }
  
  public boolean hasEssentials()
  {
    PluginManager pm = Bukkit.getPluginManager();
    if ((pm.getPlugin("Essentials") == null) || (!pm.getPlugin("Essentials").isEnabled())) {
      return false;
    }
    return true;
  }
  
  public void generateHackerFile()
  {
    File hackerfile = new File(getDataFolder(), "hackers.yml");
    if (!hackerfile.exists()) {
      try
      {
        hackerfile.createNewFile();
      }
      catch (IOException e)
      {
        e.printStackTrace();
        Bukkit.getPluginManager().disablePlugin(this);
      }
    }
  }
  
  public void initUpdater()
  {
    if (!Bukkit.getOnlineMode())
    {
      Bukkit.getLogger().log(Level.SEVERE, "Couldn't check for update as server is in offline mode.");
      return;
    }
  }
}
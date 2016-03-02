package listeners;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import me.heirteir.Main;
import config.Configurations;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class UserEditor
{
  Main main;
  Player player;
  
  public UserEditor(Player player, Main main)
  {
    this.main = main;
    this.player = player;
  }
  
  public void updatePlayer()
  {
    if (!Configurations.isGENERATE_LOG()) {
      return;
    }
    File hackerfile = new File(this.main.getDataFolder(), "hackers.yml");
    FileConfiguration config = YamlConfiguration.loadConfiguration(hackerfile);
    
    Date d = new Date(System.currentTimeMillis());
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String date = format.format(d).toString();
    if (config.get(this.player.getName()) == null)
    {
      config.set(this.player.getName() + ".uuid", this.player.getUniqueId().toString());
      config.set(this.player.getName() + ".amount", Integer.valueOf(1));
      config.set(this.player.getName() + ".ip", this.player.getAddress().getAddress().toString().replace("/", ""));
      config.set(this.player.getName() + ".latestdate", date);
      config.set(this.player.getName() + ".banned", Boolean.valueOf(this.player.isBanned()));
    }
    else
    {
      config.set(this.player.getName() + ".amount", Integer.valueOf(config.getInt(this.player.getName() + ".amount") + 1));
      config.set(this.player.getName() + ".ip", this.player.getAddress().getAddress().toString().replace("/", ""));
      config.set(this.player.getName() + ".latestdate", date);
      config.set(this.player.getName() + ".banned", Boolean.valueOf(this.player.isBanned()));
    }
    int amount = config.getInt(this.player.getName() + ".amount");
    if ((amount >= Configurations.getCHANCES()) && 
      (Configurations.getCHANCES() > 0))
    {
      for (String s : Configurations.getOUTOFCHANCES()) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", this.player.getName()).replace("/", ""));
      }
      config.set(this.player.getName() + ".banned", Boolean.valueOf(this.player.isBanned()));
    }
    try
    {
      config.save(hackerfile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      Bukkit.getPluginManager().disablePlugin(this.main);
    }
  }
}
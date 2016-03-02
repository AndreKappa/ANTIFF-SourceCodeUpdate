package listeners;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import me.heirteir.EntityHider;
import me.heirteir.Main;
import me.heirteir.Policy;
import config.Configurations;
import npc.NPC;
import npc.NPCDamageEvent;
import npc.NPCFactory;
import npc.NPCProfile;
import npc.NameGen;
import combat.CombatListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class PlayerListener
  implements Listener
{
  public static HashMap<String, String> suspects = new HashMap();
  List<String> cooldown = new ArrayList();
  public EntityHider hider;
  Main main;
  PlayerListener plr = this;
  Essentials ess;
  boolean hasEssentials;
  final NPCFactory np;
  
  public PlayerListener(Main main, boolean hasEssentials)
  {
    this.main = main;
    
    this.hider = new EntityHider(main, Policy.BLACKLIST);
    
    this.hasEssentials = hasEssentials;
    if (hasEssentials) {
      this.ess = ((Essentials)Bukkit.getPluginManager().getPlugin("Essentials"));
    }
    this.np = new NPCFactory(main);
    
    beginTask(main);
  }
  
  @EventHandler
  public void playerLeave(PlayerQuitEvent e)
  {
    if (!suspects.containsKey(e.getPlayer().getName())) {
      return;
    }
    suspects.remove(e.getPlayer().getName());
  }
  
  @EventHandler
  public void playerLeave(PlayerKickEvent e)
  {
    if (!suspects.containsKey(e.getPlayer().getName())) {
      return;
    }
    suspects.remove(e.getPlayer().getName());
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void NPCDamage(NPCDamageEvent e)
  {
    if (!e.getNpc().getBukkitEntity().isDead()) {
      e.getNpc().getBukkitEntity().remove();
    }
    if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
      return;
    }
    if (!(e.getDamager() instanceof Player)) {
      return;
    }
    Player playerHitter = (Player)e.getDamager();
    if (!suspects.containsKey(playerHitter.getName())) {
      return;
    }
    if (!((String)suspects.get(playerHitter.getName())).equals(e.getNpc().getBukkitEntity().getUniqueId().toString())) {
      return;
    }
    if (Configurations.isKILL_PLAYER()) {
      playerHitter.setHealth(0.0D);
    }
    suspects.remove(playerHitter.getName());
    if (Configurations.getREPORT().equalsIgnoreCase("all"))
    {
      Bukkit.broadcastMessage(Configurations.getREPORT_MESSAGE());
    }
    else if (Configurations.getREPORT().equalsIgnoreCase("staff"))
    {
      Player[] arrayOfPlayer;
      int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
      for (int i = 0; i < j; i++)
      {
        Player staff = arrayOfPlayer[i];
        if (staff.hasPermission("antiff.notify")) {
          staff.sendMessage(ChatColor.translateAlternateColorCodes('&', Configurations.getREPORT_MESSAGE().replace("%player%", playerHitter.getName())));
        }
      }
    }
    List<String> commands = Configurations.getCOMMANDS();
    for (String s : commands)
    {
      String command = s.replace("%player%", playerHitter.getName()).replace("/", "");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
    if (Configurations.isGENERATE_LOG()) {
      new UserEditor(playerHitter, this.main).updatePlayer();
    }
  }
  
  public void beginTask(final Main main)
  {
    Bukkit.getScheduler().runTaskTimer(main, new Runnable()
    {
      public void run()
      {
        if (main.combat.time.isEmpty()) {
          return;
        }
        ArrayList<String> temp = new ArrayList();
        for (String s : main.combat.time) {
          if ((!PlayerListener.this.cooldown.contains(s)) && (!PlayerListener.suspects.containsKey(s))) {
            temp.add(s.split("-")[0]);
          }
        }
        final Player playerHitter = Bukkit.getPlayer((String)temp.get(new Random().nextInt(temp.size())));
        temp.clear();
        if (!playerHitter.isOnGround()) {
          return;
        }
        if (PlayerListener.this.np.getNPCs().size() > 0) {
          PlayerListener.this.np.despawnAll();
        }
        final NPC npc = PlayerListener.this.np.spawnHumanNPC(playerHitter.getLocation().subtract(playerHitter.getEyeLocation().getDirection().normalize()).add(1.0D, 3.5D, 1.0D), new NPCProfile(NameGen.newName()));
        npc.setInvulnerable(false);
        npc.setGravity(false);
        
        npc.getBukkitEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 8, 2));
        Player[] arrayOfPlayer;
        int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
        for (int i = 0; i < j; i++)
        {
          Player p = arrayOfPlayer[i];
          if ((!p.getName().equalsIgnoreCase(playerHitter.getName())) && (!p.hasPermission("antiff.shownpcs"))) {
            PlayerListener.this.hider.hideEntity(p, npc.getBukkitEntity());
          }
        }
        if (PlayerListener.this.hasEssentials)
        {
          User user = PlayerListener.this.ess.getUser(npc.getBukkitEntity().getName());
          if (!user.isNPC())
          {
            user.setNPC(true);
            user.setPlayerListName("");
          }
        }
        PlayerListener.this.cooldown.add(playerHitter.getName());
        
        PlayerListener.suspects.put(playerHitter.getName(), npc.getBukkitEntity().getUniqueId().toString());
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable()
        {
          public void run()
          {
            PlayerListener.this.cooldown.remove(playerHitter.getName());
          }
        }, Configurations.getPLAYER_COOLDOWN());
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable()
        {
          public void run()
          {
            if (!npc.getBukkitEntity().isDead())
            {
              npc.getBukkitEntity().remove();
              PlayerListener.suspects.remove(playerHitter.getName());
            }
          }
        }, 8L);
      }
    }, 0L, Configurations.getSPAWN_RATE());
  }
}
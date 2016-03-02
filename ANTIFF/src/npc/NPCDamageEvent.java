package npc;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class NPCDamageEvent
  extends Event
  implements Cancellable
{
  private static final HandlerList handlerList = new HandlerList();
  private boolean cancelled = false;
  private final NPC npc;
  private final Entity damager;
  private final EntityDamageEvent.DamageCause cause;
  private double damage;
  
  public boolean isCancelled()
  {
    return this.cancelled;
  }
  
  public void setCancelled(boolean cancelled)
  {
    this.cancelled = cancelled;
  }
  
  public NPCDamageEvent(NPC npc, Entity damager, EntityDamageEvent.DamageCause cause, double damage)
  {
    this.npc = npc;
    this.damager = damager;
    this.cause = cause;
    this.damage = damage;
  }
  
  public EntityDamageEvent.DamageCause getCause()
  {
    return this.cause;
  }
  
  public double getDamage()
  {
    return this.damage;
  }
  
  public void setDamage(double damage)
  {
    this.damage = damage;
  }
  
  public NPC getNpc()
  {
    return this.npc;
  }
  
  public Entity getDamager()
  {
    return this.damager;
  }
  
  public HandlerList getHandlers()
  {
    return handlerList;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlerList;
  }
}
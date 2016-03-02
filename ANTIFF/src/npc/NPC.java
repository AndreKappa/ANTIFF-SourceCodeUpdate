package npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract interface NPC
{
  public abstract Player getBukkitEntity();
  
  public abstract boolean isInvulnerable();
  
  public abstract void setInvulnerable(boolean paramBoolean);
  
  public abstract boolean isGravity();
  
  public abstract void setGravity(boolean paramBoolean);
  
  public abstract void setTarget(Entity paramEntity);
  
  public abstract Entity getTarget();
  
  public abstract void lookAt(Location paramLocation);
  
  public abstract void setYaw(float paramFloat);
  
  public abstract boolean getEntityCollision();
  
  public abstract void setEntityCollision(boolean paramBoolean);
}

package npc;

import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.EntityDamageSource;
import net.minecraft.server.v1_7_R4.EntityDamageSourceIndirect;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EnumGamemode;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.event.CraftEventFactory;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class NPCEntity
  extends EntityPlayer
  implements NPC
{
  private boolean entityCollision = true;
  private boolean invulnerable = true;
  private boolean gravity = true;
  private org.bukkit.entity.Entity target;
  
  public NPCEntity(World world, NPCProfile profile, NPCNetworkManager networkManager)
  {
    super(((CraftServer)Bukkit.getServer()).getServer(), ((CraftWorld)world).getHandle(), profile, new PlayerInteractManager(((CraftWorld)world).getHandle()));
    this.playerInteractManager.b(EnumGamemode.SURVIVAL);
    this.playerConnection = new NPCPlayerConnection(networkManager, this);
    this.fauxSleeping = true;
    this.bukkitEntity = new CraftPlayer((CraftServer)Bukkit.getServer(), this);
  }
  
  public CraftPlayer getBukkitEntity()
  {
    return (CraftPlayer)this.bukkitEntity;
  }
  
  public boolean isGravity()
  {
    return this.gravity;
  }
  
  public void setGravity(boolean gravity)
  {
    this.gravity = gravity;
  }
  
  public boolean isInvulnerable()
  {
    return this.invulnerable;
  }
  
  public void setInvulnerable(boolean invulnerable)
  {
    this.invulnerable = invulnerable;
  }
  
  public void setTarget(org.bukkit.entity.Entity target)
  {
    this.target = target;
    lookAt(target.getLocation());
  }
  
  public org.bukkit.entity.Entity getTarget()
  {
    return this.target;
  }
  
  public void lookAt(Location location)
  {
    setYaw(getLocalAngle(new Vector(this.locX, 0.0D, this.locZ), location.toVector()));
  }
  
  public void setYaw(float yaw)
  {
    this.yaw = yaw;
    this.aP = yaw;
    this.aO = yaw;
  }
  
  private final float getLocalAngle(Vector point1, Vector point2)
  {
    double dx = point2.getX() - point1.getX();
    double dz = point2.getZ() - point1.getZ();
    float angle = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
    if (angle < 0.0F) {
      angle += 360.0F;
    }
    return angle;
  }
  
  public boolean damageEntity(DamageSource source, float damage)
  {
    if ((this.invulnerable) || (this.noDamageTicks > 0)) {
      return false;
    }
    EntityDamageEvent.DamageCause cause = null;
    org.bukkit.entity.Entity bEntity = null;
    if ((source instanceof EntityDamageSource))
    {
      net.minecraft.server.v1_7_R4.Entity damager = source.getEntity();
      cause = EntityDamageEvent.DamageCause.ENTITY_ATTACK;
      if ((source instanceof EntityDamageSourceIndirect))
      {
        damager = ((EntityDamageSourceIndirect)source).getProximateDamageSource();
        if ((damager.getBukkitEntity() instanceof ThrownPotion)) {
          cause = EntityDamageEvent.DamageCause.MAGIC;
        } else if ((damager.getBukkitEntity() instanceof Projectile)) {
          cause = EntityDamageEvent.DamageCause.PROJECTILE;
        }
      }
      bEntity = damager.getBukkitEntity();
    }
    else if (source == DamageSource.FIRE)
    {
      cause = EntityDamageEvent.DamageCause.FIRE;
    }
    else if (source == DamageSource.STARVE)
    {
      cause = EntityDamageEvent.DamageCause.STARVATION;
    }
    else if (source == DamageSource.WITHER)
    {
      cause = EntityDamageEvent.DamageCause.WITHER;
    }
    else if (source == DamageSource.STUCK)
    {
      cause = EntityDamageEvent.DamageCause.SUFFOCATION;
    }
    else if (source == DamageSource.DROWN)
    {
      cause = EntityDamageEvent.DamageCause.DROWNING;
    }
    else if (source == DamageSource.BURN)
    {
      cause = EntityDamageEvent.DamageCause.FIRE_TICK;
    }
    else if (source == CraftEventFactory.MELTING)
    {
      cause = EntityDamageEvent.DamageCause.MELTING;
    }
    else if (source == CraftEventFactory.POISON)
    {
      cause = EntityDamageEvent.DamageCause.POISON;
    }
    else if (source == DamageSource.MAGIC)
    {
      cause = EntityDamageEvent.DamageCause.MAGIC;
    }
    else if (source == DamageSource.OUT_OF_WORLD)
    {
      cause = EntityDamageEvent.DamageCause.VOID;
    }
    if (cause != null)
    {
      NPCDamageEvent event = new NPCDamageEvent(this, bEntity, cause, damage);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
        return super.damageEntity(source, (float)event.getDamage());
      }
      return false;
    }
    if (super.damageEntity(source, damage))
    {
      if (bEntity != null)
      {
        net.minecraft.server.v1_7_R4.Entity e = ((CraftEntity)bEntity).getHandle();
        double d0 = e.locX - this.locX;
        for (double d1 = e.locZ - this.locZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
          d0 = (Math.random() - Math.random()) * 0.01D;
        }
        a(e, damage, d0, d1);
      }
      return true;
    }
    return false;
  }
  
  public boolean getEntityCollision()
  {
    return this.entityCollision;
  }
  
  public void setEntityCollision(boolean entityCollision)
  {
    this.entityCollision = entityCollision;
  }
  
  public void g(double x, double y, double z)
  {
    if ((getBukkitEntity() != null) && (getEntityCollision()))
    {
      super.g(x, y, z);
      return;
    }
  }
}
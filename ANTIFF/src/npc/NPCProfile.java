package npc;

import com.google.common.base.Charsets;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import net.minecraft.util.org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class NPCProfile
  extends GameProfile
{
  private UUID uuid;
  private String name;
  
  public NPCProfile()
  {
    super(UUID.randomUUID(), "internal");
  }
  
  public NPCProfile(GameProfile profile)
  {
    this();
    this.uuid = profile.getId();
    this.name = profile.getName();
    for (Map.Entry<String, Collection<Property>> entry : profile.getProperties().asMap().entrySet()) {
      profile.getProperties().putAll((String)entry.getKey(), (Iterable)entry.getValue());
    }
  }
  
  public NPCProfile(String name)
  {
    this();
    this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
    this.name = name;
  }
  
  public NPCProfile(String name, String skinOwner)
  {
    this(name, parseUUID(getUUID(name)), skinOwner);
  }
  
  public NPCProfile(String name, UUID uuid)
  {
    this(name, uuid, uuid);
  }
  
  public NPCProfile(String name, UUID uuid, String skinOwner)
  {
    this(name, uuid, parseUUID(getUUID(skinOwner)));
  }
  
  public NPCProfile(String name, UUID uuid, UUID skinUUID)
  {
    this();
    this.uuid = uuid;
    this.name = name;
    addProperties(this, skinUUID);
  }
  
  public UUID getId()
  {
    return this.uuid;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public boolean isComplete()
  {
    return (this.uuid != null) && (StringUtils.isNotBlank(getName()));
  }
  
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GameProfile)) {
      return false;
    }
    GameProfile that = (GameProfile)o;
    if (this.uuid != null ? !this.uuid.equals(that.getId()) : that.getId() != null) {
      return false;
    }
    if (this.name != null ? !this.name.equals(that.getName()) : that.getName() != null) {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int result = this.uuid != null ? this.uuid.hashCode() : 0;
    result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
    return result;
  }
  
  public String toString()
  {
    return 
    
      new ToStringBuilder(this).append("id", this.uuid).append("name", this.name).append("properties", getProperties()).append("legacy", isLegacy()).toString();
  }
  
  private static void addProperties(GameProfile profile, UUID id)
  {
    String uuid = id.toString().replaceAll("-", "");
    try
    {
      URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
      URLConnection uc = url.openConnection();
      uc.setUseCaches(false);
      uc.setDefaultUseCaches(false);
      uc.addRequestProperty("User-Agent", "Mozilla/5.0");
      uc.addRequestProperty("Cache-Control", 
        "no-cache, no-store, must-revalidate");
      uc.addRequestProperty("Pragma", "no-cache");
      
      Scanner scanner = new Scanner(uc.getInputStream(), "UTF-8");
      String json = scanner.useDelimiter("\\A").next();
      scanner.close();
      JSONParser parser = new JSONParser();
      Object obj = parser.parse(json);
      JSONArray properties = 
        (JSONArray)((JSONObject)obj).get("properties");
      for (int i = 0; i < properties.size(); i++) {
        try
        {
          JSONObject property = (JSONObject)properties.get(i);
          String name = (String)property.get("name");
          String value = (String)property.get("value");
          String signature = property.containsKey("signature") ? 
            (String)property.get("signature") : null;
          if (signature != null) {
            profile.getProperties().put(name, 
              new Property(name, value, signature));
          } else {
            profile.getProperties().put(name, 
              new Property(value, name));
          }
        }
        catch (Exception e)
        {
          Bukkit.getLogger().log(Level.WARNING, "Failed to apply auth property", e);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private static String getUUID(String name)
  {
    return Bukkit.getOfflinePlayer(name).getUniqueId().toString().replaceAll("-", "");
  }
  
  private static UUID parseUUID(String uuidStr)
  {
    String[] uuidComponents = { uuidStr.substring(0, 8), 
      uuidStr.substring(8, 12), uuidStr.substring(12, 16), 
      uuidStr.substring(16, 20), 
      uuidStr.substring(20, uuidStr.length()) };
    
    StringBuilder builder = new StringBuilder();
    String[] arrayOfString1;
    int j = (arrayOfString1 = uuidComponents).length;
    for (int i = 0; i < j; i++)
    {
      String component = arrayOfString1[i];
      builder.append(component).append('-');
    }
    builder.setLength(builder.length() - 1);
    return UUID.fromString(builder.toString());
  }
}
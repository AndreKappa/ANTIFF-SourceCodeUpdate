package npc;

import java.net.SocketAddress;
import net.minecraft.util.io.netty.channel.AbstractChannel;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelConfig;
import net.minecraft.util.io.netty.channel.ChannelMetadata;
import net.minecraft.util.io.netty.channel.ChannelOutboundBuffer;
import net.minecraft.util.io.netty.channel.DefaultChannelConfig;
import net.minecraft.util.io.netty.channel.EventLoop;

public class NPCChannel
  extends AbstractChannel
{
  private final ChannelConfig config = new DefaultChannelConfig(this);
  
  protected NPCChannel(Channel parent)
  {
    super(parent);
  }
  
  public ChannelConfig config()
  {
    this.config.setAutoRead(true);
    return this.config;
  }
  
  public boolean isActive()
  {
    return false;
  }
  
  public boolean isOpen()
  {
    return false;
  }
  
  public ChannelMetadata metadata()
  {
    return null;
  }
  
  protected void doBeginRead()
    throws Exception
  {}
  
  protected void doBind(SocketAddress arg0)
    throws Exception
  {}
  
  protected void doClose()
    throws Exception
  {}
  
  protected void doDisconnect()
    throws Exception
  {}
  
  protected void doWrite(ChannelOutboundBuffer arg0)
    throws Exception
  {}
  
  protected boolean isCompatible(EventLoop arg0)
  {
    return true;
  }
  
  protected SocketAddress localAddress0()
  {
    return null;
  }
  
  protected AbstractChannel.AbstractUnsafe newUnsafe()
  {
    return null;
  }
  
  protected SocketAddress remoteAddress0()
  {
    return null;
  }
}

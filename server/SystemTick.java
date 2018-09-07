package vidar.server;

import java.util.*;

import vidar.server.process_server.*;
import vidar.game.model.*;

public class SystemTick extends TimerTask implements Runnable
{
	private final Timer sysTickTimer = new Timer ("SystemTick") ;
	private SessionHandler handle;

	public SystemTick (PcInstance pc) {
		handle = pc.getHandler () ;
	}
	
	public void run () {
		handle.sendPacket (new GameTime().getRaw());
	}
	
	public void start () {
		sysTickTimer.scheduleAtFixedRate (this, 0, 1000) ;
	}
	
	public void stop () {
		sysTickTimer.cancel () ;
	}
}

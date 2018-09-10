package vidar.server.process_server;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.opcodes.*;

/*
 * 登入公告訊息
 */
public class LoginAnnounce
{
	public LoginAnnounce (SessionHandler handle) {
		PacketBuilder packet = new PacketBuilder ();
		String defaultMessage = String.format ("帳號:%s\n密碼:%s\n登入IP:%s\n", handle.account.userName, handle.account.userPassword, handle.getIP () ) ;
		
		packet.writeByte (ServerOpcodes.LOGIN_WELCOME_MSG);
		packet.writeString (defaultMessage);
		handle.sendPacket (packet.getPacket ());
	}
	
	public LoginAnnounce (SessionHandler handle, String message) {
		PacketBuilder packet = new PacketBuilder ();
		
		packet.writeByte (ServerOpcodes.LOGIN_WELCOME_MSG);
		packet.writeString (message);
		handle.sendPacket (packet.getPacket ());
	}
}

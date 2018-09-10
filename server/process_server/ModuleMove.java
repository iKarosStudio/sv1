package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

/*
 * 由Server端告知物件移動使用的封包
 * 令UUID物件由位置(x,y)往Heading方向移動一單位
 * 發送封包後記得更新原物件(x,y)
 */
public class ModuleMove
{
	PacketBuilder packet = null;
	
	public ModuleMove (int uuid, int x, int y, int heading) {
		packet = new PacketBuilder () ;

		packet.writeByte (ServerOpcodes.MOVE_NODE) ;
		packet.writeDoubleWord (uuid) ;
		packet.writeWord (x) ;
		packet.writeWord (y) ;
		packet.writeByte (heading) ;
		packet.writeByte (129) ;
		packet.writeDoubleWord (0) ;
	}
	
	public byte[] getRaw () {
		return packet.getPacket () ;
	}
}

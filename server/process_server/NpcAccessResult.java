package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

/*
 * 告知客戶端NPC含有對話內容的HTML ID編號
 */
public class NpcAccessResult
{
	private PacketBuilder builder = new PacketBuilder () ;
	
	public NpcAccessResult (int NpcId, String HtmlKey) {
		
		builder.writeByte (ServerOpcodes.NPC_RESULT) ;
		builder.writeDoubleWord (NpcId) ;
		builder.writeString (HtmlKey) ;
		//有參數在這邊帶入
		//參考S_NPCTalksReturn.java
		builder.writeWord (0x00) ;
		builder.writeWord (0x00) ;

	}
	
	public byte[] getRaw () {
		return builder.getPacket () ;
	}
}

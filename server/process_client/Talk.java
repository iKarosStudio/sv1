package vidar.server.process_client;

import vidar.config.*;
import vidar.server.*;
import vidar.server.igcp.*;
import vidar.server.opcodes.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;
import vidar.game.model.*;

public class Talk
{
	public Talk (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data) ;
		
		PcInstance pc = handle.getPc ();
		int talkType = packetReader.readByte ();
		String talkText = packetReader.readString ();
		
		
		/* 說壞壞的字>3<
		if (Talks.contains ("壞") ) {
		}
		*/
		
		/* IGCP */
		InGameCommandParser igcp = new InGameCommandParser (handle, talkText);
		if(igcp.parse ()) {//is a command
			return;
		}
		
		NodeTalks chat = new NodeTalks (pc.uuid, pc.name, talkText, ServerOpcodes.NORMAL_TALKS, talkType);
		chat.withName ();
		byte[] chatPacket = chat.getRaw ();  
		
		if (talkType == 0x00) { //普通對話
			handle.sendPacket (chatPacket) ;
			pc.boardcastPcInsight (chatPacket) ;
		} else if (talkType == 0x02) {//大喊
			//
		} else if (talkType == 0x03) {//隊伍頻道
			//
		} else if (talkType == 0x04) {//血盟頻道
			//
		}
		
		if (Configurations.DISPLAY_CHAT) {
			String talkTypeName;
			switch (talkType) {
				case 0 : talkTypeName = "一般"; break;
				case 2 : talkTypeName = "大喊"; break;
				case 3 : talkTypeName = "隊伍"; break;
				case 4 : talkTypeName = "血盟"; break;
				default : talkTypeName = "不明"; break;
			}
			
			System.out.printf ("[%s]%s: %s", talkTypeName, pc.name, talkText);
			/*
			for (byte b : talkText.getBytes () ) {
				System.out.printf ("0x%02X ", b) ;
			}
			*/
			System.out.println();
		}
	}
}

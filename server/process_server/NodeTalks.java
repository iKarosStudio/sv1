package vidar.server.process_server;

import vidar.server.packet.*;

public class NodeTalks {
	PacketBuilder packet = new PacketBuilder () ;
	String name;
	int uuid;
	int opcode;
	int talkType;
	String text;
	
	
	public NodeTalks (int _uuid, String _name, String _text, int _opcode, int _talkType) {
		name = _name;
		uuid = _uuid;
		opcode = _opcode;
		talkType = _talkType;
		text = _text;
	}
	
	public void withName () {
		packet.writeByte (opcode);
		packet.writeByte (talkType);
		packet.writeDoubleWord (uuid);
		packet.writeString (name + ":" + text);
	}
	
	public void withoutName () {
		packet.writeByte (opcode);
		packet.writeByte (talkType);
		packet.writeDoubleWord (uuid);
		packet.writeString (text);
	}
	
	
	public byte[] getRaw () {
		return packet.getPacket () ;
	}
}

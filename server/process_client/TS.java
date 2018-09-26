package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;

public class TS
{
	public TS (SessionHandler Handle, byte[] Data) {
		PacketReader reader = new PacketReader (Data) ;
		
		//int unknown = reader.readByte () ;
		reader.readByte (); //pseudo read
		
		String Content = reader.readString () ;
		String Subject = reader.readString () ;
		
		System.out.printf ("TS標題:%s\nTS內容:%s\n", Subject, Content) ;
	}
}

package vidar.server.packet;

public class PacketReader
{
	private int offset = 1;
	private byte[] raw;
	
	public PacketReader (byte[] data) {
		raw = data;
	}
	
	public byte[] readRaw () {
		byte[] result = new byte[raw.length - offset] ;
		try {
			System.arraycopy (raw, offset, result, 0, raw.length - offset);
			offset = raw.length;
		} catch (Exception e) {e.printStackTrace () ; }
		
		return result;
	}

	public byte readByte () {
		byte b = raw[offset];
		offset++;
		return b;
	}
	
	public int readWord () {
		int w = ((raw[offset+1] << 8) & 0xFF00) | (raw[offset] & 0xFF) ;
		offset += 2;
		return w;
	}
	
	public int readDoubleWord () {
		int dw = ((raw[offset+3] & 0xFF) << 24) | ((raw[offset+2] & 0xFF) << 16) | ((raw[offset+1] & 0xFF) << 8) | (raw[offset] & 0xFF) ;
		offset += 4;
		return dw;
	}
	
	public String readString () {
		String parse = null;
		try{
			parse = new String (raw, offset, raw.length - offset, "BIG5");
			parse = parse.substring (0, parse.indexOf ('\0') ) ;
			offset += parse.getBytes ("BIG5").length + 1;
			
			return parse;
		} catch (Exception e) {
			e.printStackTrace () ;
			return null;
		}
	}
	
	/*
	public String ReadTextString () {
		String Parse = null;
		try{
			Parse = new String (Raw, offset, Raw.length - offset, "BIG5");
			Parse = Parse.substring (0, Parse.indexOf ("\0\0") ) ;
			offset += Parse.getBytes ("MS950").length + 1;
			
			return Parse;
		} catch (Exception e) {
			e.printStackTrace () ;
			return null;
		}
	}
	*/
}

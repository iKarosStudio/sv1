package vidar.server;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.logging.*;

import vidar.server.packet.*;
import vidar.game.model.*;

public class SessionHandler extends Thread implements Runnable
{
	Logger Log = Logger.getLogger (SessionHandler.class.getSimpleName () ) ;
	private Socket sock;

	public Account account;
	
	private InputStream inStream;
	private OutputStream outStream;
	
	private PacketHandler packetHandle;
	private PacketCodec packetCodec;
	
	private static final byte[] INIT_PACKET = {
			(byte) 0xB1, (byte) 0x3C, (byte) 0x2C, (byte) 0x28, (byte) 0xF6,
			(byte) 0x65, (byte) 0x1D, (byte) 0xDD, (byte) 0x56, (byte) 0xE3, 
			(byte) 0xEF
	} ;
		
	public void firstPacket () {
		byte[] initPacket = new byte[18];
		
		initPacket[0] = (byte) (INIT_PACKET.length + 7) ;
		initPacket[1] = (byte) 0;
		initPacket[2] = (byte) 0x20;
		initPacket[3] = (byte) 0xEC;
		initPacket[4] = (byte) 0x90;
		initPacket[5] = (byte) 0xC6;
		initPacket[6] = (byte) 0x5C;
		System.arraycopy (INIT_PACKET, 0, initPacket, 7, INIT_PACKET.length);
		
		try {
			outStream.write (initPacket) ;
			outStream.flush () ;
			
		} catch (Exception e) {
			e.printStackTrace ();
			
		}
	}
	
	/*
	 * Recieve client side RAW data packet, return a Decoded packet
	 */
	public byte[] receivePacket () throws IOException {
		try {
			int sizeHi = inStream.read () ;
			int sizeLo = inStream.read () ;
			int size = ((sizeLo << 8) | sizeHi) - 2;
			
			byte[] data = new byte[size];	
			
			inStream.read (data) ;
			packetCodec.decode (data, size) ;
			packetCodec.updateDecodeKey (data) ;
			
			return data;
		} catch (IOException e) {
			throw e;
		}
	}
	
	public synchronized void sendPacket (byte[] Data)  {
		byte[] raw = null;
		try {
			raw = packetCodec.encode (Data) ;
			//Codec.UpdateEncodeKey (Data) ;
			/*
			System.out.printf ("[OUT:0x%08x, 0x%08x]:", Codec.EncodeKeyL[0], Codec.EncodeKeyL[1]) ;
			for (byte b : Data) {
				System.out.printf ("0x%02X ", b) ;
			}
			System.out.print ("\n") ;
			*/
			outStream.write (raw) ;
			outStream.flush () ;
			
		} catch (Exception e) {	
			try {
				outStream.close () ;
				System.out.printf ("Send Packet Exception! Close ") ;
				System.out.print (outStream) ;
				System.out.printf (" Output flow\n") ;
				
			} catch (Exception p) {
				p.printStackTrace ();
			}
			//e.printStackTrace () ;
		}
	}
	
	/*
	 * 使用者Session主要工作迴圈
	 */
	public void run () {
		firstPacket ();
		packetCodec.initKey () ;
		
		while (true) {
			try {
				byte[] recvData = receivePacket (); //Get decoded data
				
				/* 處理客戶端封包  */
				packetHandle.process (recvData) ;
				
			} catch (SocketException s) {
				/* 連線中斷 */
				break;
				
			} catch (Exception e) {
				e.printStackTrace () ;
				break;
			}
		} //while ture
		
		/* 斷線後該做的事  */
		if (account != null) {
			try {
				if (!account.activePc.isExit) { //若不是客戶端主動離線
					account.activePc.offline () ;
				}
				
				account.activePc = null;
				
				account.updateLastLogin ();
				sock.close () ;
				
				System.out.printf ("[Disconnect]IP:%s [Host:%s]\n", 
						sock.getInetAddress().getHostAddress ().toString (),
						sock.getInetAddress ().getHostName ());
			} catch (Exception e) {
				e.printStackTrace () ;
			}
			account = null;
		}
	}
	
	public SessionHandler () {
		//
	}
	
	public SessionHandler (Socket _sock) {
		try {
			sock = _sock;
			
			inStream = sock.getInputStream () ;
			outStream = new BufferedOutputStream(sock.getOutputStream () ) ;
			
			packetCodec = new PacketCodec () ;
			packetHandle = new PacketHandler (this);
			//Db = HikariCP.getInstance () ;
			
		} catch (Exception e) {
			e.printStackTrace () ;
		}
	}
	
	public String getIP () {
		return sock.getInetAddress ().getHostAddress ();
	}
	
	public String getHostName () {
		return sock.getInetAddress ().getHostName ();
	}
	
	public int getPort () {
		return sock.getPort ();
	}
	
	public PcInstance getPc () {
		PcInstance Pc = null;
		if (account != null) {
			if (account.activePc != null) {
				Pc = account.activePc;
			}
		}
		
		return Pc;
	}
	
	public PacketCodec getCodec () {
		return packetCodec;
	}

	public OutputStream getOutputStream () {
		return outStream;
	}
	
	public InputStream getInputStream () {
		return inStream;
	}
	
	public boolean isClosed () {
		return sock.isClosed () ;
	}
	
	public void disconnect () {
		try {
			sock.close ();
			
		} catch (Exception e) {
			e.printStackTrace () ;
		}
	}
}

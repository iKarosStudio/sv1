package vidar.server.process_client;

import java.sql.*;

import vidar.config.*;
import vidar.server.*;
import vidar.server.database.*;
import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.server.process_server.*;
import vidar.game.*;

public class AccountOperation
{
	public static final int LOGIN_OK = 0x00;
	public static final int CHARACTER_NAME_EXISTS  = 0x06;
	public static final int ACCOUNT_ALREADY_EXISTS = 0x07;
	public static final int ACCOUNT_PASSWORD_ERROR = 0x08;
	public static final int ACCOUNT_IN_USE         = 0x16;
	
	private String userName;
	private String userPassword;
	private String ip;
	private String hostName;
	
	public void login (SessionHandler handle, byte[] data) {
		PacketBuilder resultPacket = new PacketBuilder () ;
		PacketReader packetReader = new PacketReader (data) ;
		
		userName = packetReader.readString ().toLowerCase () ;
		userPassword = packetReader.readString () ;
		
		ip = handle.getIP () ;
		hostName = handle.getHostName () ;
		int port = handle.getPort () ;
		
		Account account = new Account (handle, userName, userPassword) ;
		int loginResult = account.load ();
		
		/* 回報登入結果 */
		resultPacket.reset ();
		resultPacket.writeByte (ServerOpcodes.LOGIN_RESULT) ;
		resultPacket.writeByte (loginResult); //LOGIN RESULT CODE
		resultPacket.writeDoubleWord (0x00000000);
		resultPacket.writeDoubleWord (0x00000000);
		resultPacket.writeDoubleWord (0x00000000);		
		handle.sendPacket (resultPacket.getPacket ());
		
		if (loginResult == LOGIN_OK) {
			System.out.printf ("[LOGIN]") ;
			handle.account = account;
			
			if (Vidar.getInstance ().onlinePlayers < Configurations.MAX_PLAYER) {
				/* 登入公告訊息 */
				new LoginAnnounce (handle);
				handle.account.updateLastLogin ();
			} else {
				new LoginAnnounce (handle, "線上使用人數已滿");
				handle.disconnect ();//bye
			}
			
		} else if (loginResult == ACCOUNT_ALREADY_EXISTS) {
			/* 帳號不存在, 建立新帳號 */
			System.out.printf ("[CREATE ACCOUNT]") ;
			createNewAccount (handle) ;
			
		} else if (loginResult == ACCOUNT_IN_USE) {
			/* 重複登入 */
			System.out.printf ("[ALREADY LOGIN]") ;
			
		} else {
			/* 密碼錯誤 */
			System.out.printf ("[PW ERROR]") ;
			handle.account = null;
		}
		
		System.out.printf ("Account:%s Password:%s From %s:%d(Host:%s)\n", userName, userPassword, ip, port, hostName) ;
	}
	
	public void createNewAccount (SessionHandler handle) {
		DatabaseCmds.createAccount (userName, userPassword, ip, hostName) ;
	}
	
	public void delete (SessionHandler Handle, byte[] Data) {
		/*
		 * not implement yet
		 */
	}
	
	public int getCharacterAmount (SessionHandler handle, byte[] data) {
		int characterAmount = 0;
		Account account = handle.account;
		
		characterAmount = DatabaseCmds.getCharacterAmount (account.userName) ;
		
		return characterAmount;
	}
	
	/* CommonClick service */
	public void getCharacterData (SessionHandler handle, byte[] data) {
		Account account = handle.account;
		
		int characterAmount = 0;
		
		ResultSet rs = null;
		try {	
			/* 回報帳號腳色數量 */
			characterAmount = getCharacterAmount (handle, data) ;
			
			PacketBuilder packet = new PacketBuilder () ;
			packet.writeByte (ServerOpcodes.CHAR_AMOUNT) ;
			packet.writeByte (characterAmount) ; //character amount
			packet.writeDoubleWord (0x00000000) ;
			packet.writeDoubleWord (0x00000000) ;
			handle.sendPacket (packet.getPacket () ) ;

			if (characterAmount > 0) {
				rs = DatabaseCmds.getAccountCharacters (account.userName) ;
				
				/* 回報帳號腳色資料 */
				while (rs.next () ) {
					packet = new PacketBuilder () ;
					packet.writeByte (ServerOpcodes.CHAR_LIST) ;
					packet.writeString (rs.getString ("char_name") ) ;
					packet.writeString (rs.getString ("Clanname") ) ;
					
					/* Type - 0:Royal 1:Knight 2:Elf 3:Mage 4:Darkelf */
					packet.writeByte (rs.getInt ("Type") ) ;
					
					/* Sex - 0:male, 1:female */
					packet.writeByte (rs.getInt("Sex"));
					packet.writeWord (rs.getInt ("Lawful")); //lawful
					packet.writeWord (rs.getInt ("CurHP")); //hp
					packet.writeWord (rs.getInt ("CurMP")); //mp
					packet.writeByte (rs.getByte ("Ac")); //ac
					packet.writeByte (rs.getByte ("level")); //level
					packet.writeByte (rs.getByte ("Str")); //str
					packet.writeByte (rs.getByte ("Dex")); //dex
					packet.writeByte (rs.getByte ("Con")); //con
					packet.writeByte (rs.getByte ("Wis")); //wis
					packet.writeByte (rs.getByte ("Cha")); //cha
					packet.writeByte (rs.getByte ("Intel")); //int
					packet.writeByte (0x00) ; //END	
					handle.sendPacket (packet.getPacket ());
				}
			} else {
				//do nothing
			}
		} catch (Exception e) {
			e.printStackTrace () ;
			
		} finally {
			DatabaseUtil.close (rs) ;
			
		}
	}
	
}

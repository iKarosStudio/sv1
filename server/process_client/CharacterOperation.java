package vidar.server.process_client;

import java.sql.*;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;
import vidar.server.database.*;
import vidar.server.utility.*;
import vidar.game.*;
import vidar.game.model.*;
import vidar.game.routine_task.*;

public class CharacterOperation {	
	public void login (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data);
		String charName = packetReader.readString ();
		
		/* 載入角色 */
		PcInstance pc = new PcInstance (handle) ;
		if (pc.load (charName)) {
			/* 開始回報角色登入資料 */
			handle.account.activePc = pc;
			
			new Unknown1 (handle);
			new Unknown2 (handle);
			//new RequestStart (Handle) ;  //active spell
			/*
			 * 載入角色記憶座標
			 */
			
			pc.loadItemBag ();
			pc.loadSkills ();
			pc.skillBuffs.loadBuffs ();
					
			/* fix
			byte[] config = new SendClientConfig (Handle).getRaw () ;
			if (config.length > 0) {
				Handle.SendPacket (config) ;
			}
			*/
			
			handle.sendPacket (new GameTime().getRaw ());
			
			handle.sendPacket (new NodeStatus (pc).getRaw ());
			handle.sendPacket (new MapId (pc.location.mapId).getRaw ());
			handle.sendPacket (new ModelPacket (pc).getRaw ());
			handle.sendPacket (new ReportSpMr (handle).getRaw ());
			handle.sendPacket (new ReportTitle (handle).getRaw ());
			
			handle.sendPacket (new ReportWeather (Vidar.getInstance ().weather).getRaw ());
			
			//Set Emblem here

			handle.sendPacket (new NodeStatus (pc).getRaw ());
			
			/* 固定循環工作 */
			pc.routineTasks.start ();
			pc.skillBuffs.start ();
			pc.hpMonitor.start ();
			
			/* 開始經驗值監測 */
			pc.expMonitor.start ();
			
			
			/* 視距物件更新服務 */
			pc.sightUpdate = new SightUpdate (pc);
			pc.sightUpdate.start ();
			
			pc.updateCurrentMap ();
			Vidar.getInstance ().addPc (pc);
			
		} else {
			/* 沒有角色ID, 非正常登入現象 */
			System.out.printf ("不正常角色登入 %s form ip:%s\n", charName, handle.getIP ());
			
		}
	}
	
	/* 參考C_CreateChar.java */
	public void create (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data) ;
		String charName = packetReader.readString () ;
		
		/* 
		 * 0: Prince/Princess
		 * 1: Knight
		 * 2: Elf
		 * 3: Mage
		 * 4: DarkElf
		 */
		int type = packetReader.readByte () ;
		
		/* 
		 * 0:Male
		 * 1:Female
		 */
		int sex =  packetReader.readByte () ; 
		
		int str =  packetReader.readByte () ;
		int dex =  packetReader.readByte () ;
		int con =  packetReader.readByte () ;
		int wis =  packetReader.readByte () ;
		int cha =  packetReader.readByte () ;
		int intel= packetReader.readByte () ;
		
		Connection conn = HikariCP.getConnection ();
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rsAmount = null;
		ResultSet rsIdRepeat = null;
		
		try {
			//檢查帳號角色數量
			ps1 = conn.prepareStatement ("SELECT count(*) as cnt FROM characters WHERE account_name=?;") ;
			ps1.setString (1, handle.account.userName) ;
			rsAmount = ps1.executeQuery () ;
			if (rsAmount.next () ) {
				if (rsAmount.getInt ("cnt") > 4) {
					handle.sendPacket (new CharCreateResult (CharCreateResult.WRONG_AMOUNT).getRaw () ) ;
					return;
				}
			}
			
			//檢查重複ID
			ps2 = conn.prepareStatement ("SELECT account_name FROM characters WHERE char_name=?;");
			ps2.setString (1, charName);
			rsIdRepeat = ps2.executeQuery ();
			if (rsIdRepeat.next ()) {
				handle.sendPacket (new CharCreateResult (CharCreateResult.ALREADY_EXIST).getRaw ());
				return;
			}
			
			//檢查數值總和
			if ((str + dex + con + wis + cha + intel) != 75) {
				handle.sendPacket (new CharCreateResult (CharCreateResult.WRONG_AMOUNT).getRaw ());
				return;
			}
			
			//Done, Write to Database
			CharacterInitializer pcCreate = new CharacterInitializer (handle, charName, type, sex, str, dex, con, wis, cha, intel);
			pcCreate.execute ();
			System.out.printf ("Create Character:%s\t From Account:%s @ %s\n", charName, handle.account.userName, handle.getIP ());
			
		} catch (Exception e) {
			e.printStackTrace ();
			
		} finally {
			DatabaseUtil.close (rsAmount);
			DatabaseUtil.close (rsIdRepeat);
			DatabaseUtil.close (ps1);
			DatabaseUtil.close (ps2);
			DatabaseUtil.close (conn);
		}
		
	}
	
	public void delete (SessionHandler handle, byte[] data) {
		//刪除角色
	}
}



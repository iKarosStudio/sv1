package vidar.server.utility;

import java.sql.*;

import vidar.server.*;
import vidar.server.database.*;
import vidar.server.process_server.*;
import vidar.game.model.*;

public class CharacterInitializer
{
	public static final int CLASSID_PRINCE = 0;
	public static final int CLASSID_PRINCESS = 1;
	public static final int CLASSID_KNIGHT_MALE = 61;
	public static final int CLASSID_KNIGHT_FEMALE = 48;
	public static final int CLASSID_ELF_MALE = 138;
	public static final int CLASSID_ELF_FEMALE = 37;
	public static final int CLASSID_WIZARD_MALE = 734;
	public static final int CLASSID_WIZARD_FEMALE = 1186;
	public static final int CLASSID_DARK_ELF_MALE = 2786;
	public static final int CLASSID_DARK_ELF_FEMALE = 2796;
	
	private static final int[] MALE_LIST = new int[] {0, 61, 138, 734, 2786};
	private static final int[] FEMALE_LIST = new int[] {1, 48, 37, 1186, 2796};
	
	PcInstance pc = null;
	
	public CharacterInitializer (SessionHandler handle,
		String name, int type, int sex,
		int str, int dex, int con, int wis, int cha, int intel
	) {
		pc = new PcInstance ();
		pc.setHandle (handle);
		pc.uuid = UuidGenerator.next ();
		pc.name = name;
		pc.title = "";
		pc.clanName = "";
		pc.type = type;
		pc.sex = sex;
		if (pc.sex == 0) { //Male
			pc.gfx = MALE_LIST[pc.type];
		} else { //Female
			pc.gfx = FEMALE_LIST[pc.type];
		}
		pc.gfxTemp = pc.gfx;
		pc.lawful = 0;
		pc.level = 1;
		pc.exp = 0;
		pc.basicParameters.ac = 10;
		pc.satiation = 3;
		
		pc.basicParameters.str = str;
		pc.basicParameters.dex = dex;
		pc.basicParameters.con = con;
		pc.basicParameters.wis = wis;
		pc.basicParameters.cha = cha;
		pc.basicParameters.intel = intel;
		
		/* 出生地點 */
		pc.location.mapId = 0;
		pc.location.point.x = 32643;
		pc.location.point.y = 32960;
		
		if (pc.isRoyal ()) {
			pc.basicParameters.maxHp = 14;
			if (pc.basicParameters.wis > 15) {
				pc.basicParameters.maxMp = 4;
			} else if (pc.basicParameters.wis > 11) {
				pc.basicParameters.maxMp = 3;
			} else {
				pc.basicParameters.maxMp = 2;
			}
			
		} else if (pc.isKnight ()) {
			pc.basicParameters.maxHp = 16;
			if (pc.basicParameters.wis > 11) {
				pc.basicParameters.maxMp = 2;
			} else {
				pc.basicParameters.maxMp = 1;
			}
				
			
		} else if (pc.isElf ()) {
			pc.basicParameters.maxHp = 15;
			if (pc.basicParameters.wis > 15) {
				pc.basicParameters.maxMp = 6;
			} else {
				pc.basicParameters.maxMp = 4;
			}
			
		} else if (pc.isWizard ()) {
			pc.basicParameters.maxHp = 12;
			if (pc.basicParameters.wis > 15) {
				pc.basicParameters.maxMp = 8;
			} else {
				pc.basicParameters.maxMp = 6;
			}
			
		} else if (pc.isDarkelf ()) {
			pc.basicParameters.maxHp = 12;
			if (pc.basicParameters.wis > 15) {
				pc.basicParameters.maxMp = 6;
			} else if (pc.basicParameters.wis > 11) {
				pc.basicParameters.maxMp = 4;
			} else {
				pc.basicParameters.maxMp = 3;
			}
			
		}
		pc.hp = pc.basicParameters.maxHp ;
		pc.mp = pc.basicParameters.maxMp ;
	}
	
	public void execute () {
		/*
		 * 寫入資料庫
		 */
		Connection conn = HikariCP.getConnection ();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement ("INSERT INTO characters SET account_name=?, objid=?, char_name=?, level=?, Exp=?, MaxHp=?, MaxMp=?, CurHp=?, CurMp=?, Ac=?, Str=?, Con=?, Dex=?, Cha=?, Intel=?, Wis=?, Status=?, Class=?, Sex=?, Type=?, Heading=?, LocX=?, LocY=?, MapID=?, Food=?, Lawful=?, Title=?, ClanID=?, Clanname=?;") ;
			ps.setString (1, pc.getHandle ().account.userName);
			ps.setInt (2, pc.uuid);
			ps.setString (3, pc.name);
			ps.setInt (4, pc.level);
			ps.setInt (5, pc.exp);
			ps.setInt (6, pc.basicParameters.maxHp);
			ps.setInt (7, pc.basicParameters.maxMp);
			ps.setInt (8, pc.hp);
			ps.setInt (9, pc.mp);
			ps.setInt (10, pc.basicParameters.ac);
			ps.setInt (11, pc.basicParameters.str);
			ps.setInt (12, pc.basicParameters.con);
			ps.setInt (13, pc.basicParameters.dex);
			ps.setInt (14, pc.basicParameters.cha);
			ps.setInt (15, pc.basicParameters.intel);
			ps.setInt (16, pc.basicParameters.wis);
			ps.setInt (17, pc.status);
			ps.setInt (18, pc.gfx);
			ps.setInt (19, pc.sex);
			ps.setInt (20, pc.type);
			ps.setInt (21, pc.heading);
			ps.setInt (22, pc.location.point.x);
			ps.setInt (23, pc.location.point.y);
			ps.setInt (24, pc.location.mapId);
			ps.setInt (25, pc.satiation);
			ps.setInt (26, pc.lawful);
			ps.setString (27, pc.title);
			ps.setInt (28, pc.clanId);
			ps.setString (29, pc.clanName);
			
			ps.execute ();
			
			/*
			 * 給初始道具
			 */
			//
			
		} catch (Exception e) {
			e.printStackTrace ();
			
		} finally {
			DatabaseUtil.close (ps);
			DatabaseUtil.close (conn);
		}
		
		/*
		 * 更新客戶端角色顯示		
		 */
		pc.getHandle().sendPacket (new CharCreateResult (CharCreateResult.OK).getRaw ());
		pc.getHandle().sendPacket (new NewCharacterPack (pc).getRaw ());
	}
}

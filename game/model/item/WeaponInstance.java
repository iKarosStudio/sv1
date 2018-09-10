package vidar.game.model.item;

import vidar.types.*;
import vidar.server.packet.*;
import vidar.game.*;
import vidar.game.template.*;

public class WeaponInstance extends ItemInstance
{
	public int minLevel = 0; //要求使用等級
	public int maxLevel = 0; //最大使用等級
	
	public int gfx; //角色武器GFX
	
	public int safeEnchant; //安定值
	
	public int str, con, dex, wis, cha, intel;
	public int hp, mp, hpr, mpr, sp, mr;
	
	public int hitModify = 0; //命中修正
	//public int bowHitModify = 0; //弓箭命中修正
	public int dmgModify = 0; //額外攻擊點數
	public int magicDmgModify = 0; //魔法攻擊點數
	//public int DefenseWater=0, DefenseWind=0, DefenseFire=0, DefenseEarth=0;
	//public int ResistStan=0, ResistStone=0, ResistSleep=0, ResistFreeze=0;
	public boolean isDamageable; //會損壞的武器
	public boolean isTwoHanded; //雙手武器
	public boolean isHasteEffect; //加速效果
	public boolean isManaDrain; //魔力抽取效果
	
	public boolean isRoyalUsable;
	public boolean isKnightUsable;
	public boolean isElfUsable;
	public boolean isWizardUsable;
	public boolean isDarkelfUsable;

	public WeaponInstance (
		int _id,
		int _uuid,
		int _ownerUuid,
		int _durability,
		int _enchant,
		boolean _isUsing,
		boolean _isIdentified) {
		
		WeaponTemplate template = CacheData.weapon.get (_id);
		location = new Location ();
		
		id = _id;
		uuid = _uuid;
		uuidOwner = _ownerUuid;
		gfxInBag = template.gfxInBag;
		gfxOnGround = template.gfxOnGround;
		gfx = template.gfx;
		name = template.name;
		nameId = template.nameId;
		materialName = template.materialName;
		typeName = template.typeName;
		desciptId = template.descriptId;
		material = template.material;
		majorType = 1;
		minorType = template.minorType;
		useType = ItemTypeTable.TYPE_USE_WEAPON;//template.useType;
		count = 1;
		weight = template.weight;
		isIdentified = _isIdentified;
		isTradeable = template.isTradeable;
		isStackable = template.isStackable;
		durability = _durability;
		chargeCount = 0;
		bless = template.bless;
		isUsing = _isUsing;
		
		minLevel = template.minLevel;
		maxLevel = template.maxLevel;
		safeEnchant = template.safeEnchant;
		enchant = _enchant;
		dmgSmall = template.dmgSmall;
		dmgLarge = template.dmgLarge;
		str = template.str; con = template.con; dex = template.dex;
		wis = template.wis; cha = template.cha; intel = template.intel;
		hp = template.hp; hpr = template.hpr;
		mp = template.mp; mpr = template.mpr;
		sp = template.sp; mr = template.mr;
		hitModify = template.hitModify;
		dmgModify = template.dmgModify;
		magicDmgModify = template.magicDmgModify;
		isDamageable = template.isDamageable;
		isTwoHanded = template.isTwohandedWeapon ();
		isHasteEffect = template.isHasteItem;
		isManaDrain = template.isManaDrain;
		isRoyalUsable = template.isRoyalUsable;
		isKnightUsable= template.isKnightUsable;
		isWizardUsable= template.isWizardUsable;
		isElfUsable   = template.isElfUsable;
		isDarkelfUsable=template.isDarkelfUsable;
	}
	
	public String getName () {
		StringBuffer weaponViewName = new StringBuffer (name);
		
		if (isIdentified) {
			weaponViewName.insert (0, "+" + enchant + " ") ;
		} 
		if (isUsing) {
			weaponViewName.append (" ($9)") ;
		}
		
		return weaponViewName.toString ();
	}
	
	public byte[] getDetail () {
		PacketBuilder packet = new PacketBuilder () ;
		
		packet.writeByte (1) ;
		packet.writeByte (dmgSmall) ;
		packet.writeByte (dmgLarge) ;
		packet.writeByte (material) ;
		packet.writeDoubleWord (weight / 1000) ;
		
		//
		packet.writeByte (2) ; //Enchant level
		packet.writeByte (enchant) ;
		
		if (durability > 0) {
			packet.writeByte (3) ; //Durability
			packet.writeByte (durability) ;
		}
		
		if (isTwoHanded) {
			packet.writeByte (4) ;
		}
		
		if (hitModify > 0) {
			packet.writeByte (5) ; //hit modifier
			packet.writeByte (hitModify) ;
		}
		
		if (dmgModify > 0) {
			packet.writeByte (6) ; //dmg modifier
			packet.writeByte (dmgModify) ;
		}
		
		byte usableClass = 0;
		if (isRoyalUsable) usableClass   |= 0x01;
		if (isKnightUsable) usableClass  |= 0x02;
		if (isElfUsable) usableClass     |= 0x04;
		if (isWizardUsable) usableClass  |= 0x08;
		if (isDarkelfUsable) usableClass |= 0x10;
		packet.writeByte (7) ; //use class
		packet.writeByte (usableClass);
		
		if (isManaDrain || id == 126 || id == 127) {
			packet.writeByte (16) ;
		}
		
		if (str > 0) {
			packet.writeByte (8) ;
			packet.writeByte (str) ;
		}
		
		if (dex > 0) {
			packet.writeByte (9) ;
			packet.writeByte (dex) ;
		}
		
		if (con > 0) {
			packet.writeByte (10) ;
			packet.writeByte (con) ;
		}
		
		if (intel > 0) {
			packet.writeByte (11) ;
			packet.writeByte (intel) ;
		}
		
		if (wis > 0) {
			packet.writeByte (12) ;
			packet.writeByte (wis) ;
		}
		
		if (cha > 0) {
			packet.writeByte (13) ;
			packet.writeByte (cha) ;
		}
		
		if (hp > 0) {
			packet.writeByte (31) ;
			packet.writeByte (hp) ;
		}
		
		if (mp > 0) {
			packet.writeByte (32) ;
			packet.writeByte (mp) ;
		}
		
		if (mr > 0) {
			packet.writeByte (15) ;
			packet.writeWord (mr) ;
		}
		
		if (sp > 0) {
			packet.writeByte (17) ;
			packet.writeWord (sp) ;
		}
		
		if (isHasteEffect) {
			packet.writeByte (18) ;
		}
		
		return packet.getPacketNoPadding () ;
	}
}

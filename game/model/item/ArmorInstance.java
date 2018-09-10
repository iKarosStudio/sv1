package vidar.game.model.item;

import vidar.types.*;
import vidar.server.packet.*;
import vidar.game.*;
import vidar.game.template.*;

public class ArmorInstance extends ItemInstance
{	
	public int minLevel = 0; //要求使用等級
	public int maxLevel = 0; //最大使用等級
	
	public int ac; //防禦值
	
	public int safeEnchant; //安定值
	
	public int str, con, dex, wis, cha, intel;
	public int hp, mp, hpr, mpr, sp, mr;
	
	//public int hitModify = 0; //命中修正
	public int bowHitModify = 0; //弓箭命中修正
	//public int dmgModify = 0; //額外攻擊點數
	public int magicDmgModify = 0; //魔法攻擊點數
	public int defenseWater=0, defenseWind=0, defenseFire=0, defenseEarth=0;
	public int resistStan=0, resistStone=0, resistSleep=0, resistFreeze=0;
	public boolean isHasteEffect; //加速效果
	
	public int dmgReduction;
	public int weightReduction;
	
	public boolean isRoyalUsable;
	public boolean isKnightUsable;
	public boolean isElfUsable;
	public boolean isWizardUsable;
	public boolean isDarkelfUsable;
	
	public ArmorInstance (
		int _id,
		int _uuid,
		int _ownerUuid,
		int _durability,
		int _enchant,
		boolean _isUsing,
		boolean _isIdentified) {
		
		ArmorTemplate template = CacheData.armor.get (_id);
		location = new Location ();
		
		id = _id;
		uuid = _uuid;
		uuidOwner = _ownerUuid;
		gfxInBag = template.gfxInBag;
		gfxOnGround = template.gfxOnGround;
		name = template.name;
		nameId = template.nameId;
		materialName = template.materialName;
		typeName = template.typeName;
		desciptId = template.descriptId;
		material = template.material;
		majorType = 2;
		minorType = template.minorType;
		useType = ItemTypeTable.ArmorMinorType2UseType (minorType);//template.useType;
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
		ac = template.ac;
		safeEnchant = template.safeEnchant;
		enchant = _enchant;
		str = template.str; con = template.con; dex = template.dex;
		wis = template.wis; cha = template.cha; intel = template.intel;
		hp = template.hp; hpr = template.hpr;
		mp = template.mp; mpr = template.mpr;
		sp = template.sp; mr = template.mr;
		bowHitModify = template.bowHitRate;
		defenseWater = template.defenseWater;
		defenseWind  = template.defenseWind;
		defenseFire  = template.defenseFire;
		defenseEarth = template.defenseEarth;
		resistStan  = template.resistStan;
		resistStone = template.resistStone;
		resistSleep = template.resistSleep;
		resistFreeze= template.resistFreeze;
		dmgReduction= template.dmgReduction;
		weightReduction=template.weightReduction;
		isHasteEffect = template.isHasteItem;
		isRoyalUsable = template.isRoyalUsable;
		isKnightUsable= template.isKnightUsable;
		isWizardUsable= template.isWizardUsable;
		isElfUsable   = template.isElfUsable;
		isDarkelfUsable=template.isDarkelfUsable;
	}
	
	public String getName () {
		StringBuffer armorViewName = new StringBuffer (name);
		
		if (isIdentified) {
			armorViewName.insert (0, "+" + enchant + " ") ;
		} 
		if (isUsing) {
			armorViewName.append (" ($117)") ;
		}
		
		return armorViewName.toString ();
	}
	
	public byte[] getDetail () {
		PacketBuilder packet = new PacketBuilder () ;
		
		packet.writeByte (19) ;
		packet.writeByte (Math.abs (ac) ) ;
		packet.writeByte (material) ;
		packet.writeDoubleWord (weight / 1000) ;
		
		//
		packet.writeByte (2) ; //Enchant level
		packet.writeByte (enchant) ;
		
		if (durability > 0) {
			packet.writeByte (3) ; //Durability
			packet.writeByte (durability) ;
		}
		
		byte usableClass = 0;
		if (isRoyalUsable) usableClass   |= 0x01;
		if (isKnightUsable) usableClass  |= 0x02;
		if (isElfUsable) usableClass     |= 0x04;
		if (isWizardUsable) usableClass  |= 0x08;
		if (isDarkelfUsable) usableClass |= 0x10;
		packet.writeByte (7) ; //use class
		packet.writeByte (usableClass);

		
		if (bowHitModify > 0) {
			packet.writeByte (24) ;
			packet.writeByte (bowHitModify) ;
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
		
		if (defenseFire > 0) {
			packet.writeByte (27) ;
			packet.writeByte (defenseFire) ;
		}
		
		if (defenseWater > 0) {
			packet.writeByte (28) ;
			packet.writeByte (defenseWater) ;
		}
		
		if (defenseWind > 0) {
			packet.writeByte (29) ;
			packet.writeByte (defenseWind) ;
		}
		
		if (defenseEarth > 0) {
			packet.writeByte (30) ;
			packet.writeByte (defenseEarth) ;
		}
		
		return packet.getPacketNoPadding () ;
	}
}

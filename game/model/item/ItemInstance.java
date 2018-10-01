package vidar.game.model.item;

import vidar.types.*;
import vidar.server.packet.*;
import vidar.game.*;
import vidar.game.template.*;
import static vidar.game.template.ItemTypeTable.*;

public class ItemInstance
{	
	public int uuid;
	public int uuidOwner;
	public int id;
	
	public int gfxInBag;
	public int gfxOnGround;
	
	public Location location; 
	
	public String name;
	public String nameId;
	public String materialName;
	public String typeName;
	public int desciptId;
	
	public int material;
	
	public int majorType;
	public int minorType;
	public int useType;
	
	public int count; //單位數量
	public int weight; //重量 = 單位數量 * 單位重量
	public int enchant;
	
	public boolean isIdentified; //已經鑑定
	public boolean isTradeable; //可交易
	public boolean isStackable; //可堆疊物品
	
	public int durability;
	public int chargeCount;
	public int bless;
	
	public int foodValue;
	public int dmgSmall;
	public int dmgLarge;
	
	public int delayTime;
	
	public boolean isUsing;
	
	/* weapon & armor 共通 */
	public int minLevel;
	public int maxLevel;
	public int safeEnchant;
	public int str, con, dex, wis, cha, intel;
	public int hp, mp, hpr, mpr;
	public int mr, sp;
	
	public int hitModify;
	public int bowHitModify;
	public int dmgModify;
	public int magicDmgModify;
	
	public boolean isHasteEffect;
	public boolean isRoyalUsable;
	public boolean isKnightUsable;
	public boolean isElfUsable;
	public boolean isWizardUsable;
	public boolean isDarkelfUsable;
	
	/* weapon 特性 */
	public int gfx;
	public boolean isDamageable;
	public boolean isTwoHanded;
	public boolean isManaDrain;
	
	/* armor 特性 */
	public int ac;
	public int defenseWater=0, defenseWind=0, defenseFire=0, defenseEarth=0;
	public int resistStan=0, resistStone=0, resistSleep=0, resistFreeze=0;
	public int dmgReduction;
	public int weightReduction;
	
	
	//加入SkillEffectTimer for 針對道具的技能
	
	public byte[] detail;
	
	public ItemInstance () {
		location = new Location ();
	}
	
	public ItemInstance (
		int _id,
		int _uuid,
		int _ownerUuid,
		int _enchant,
		int _count,
		int _durability,
		int _chargeCount,
		boolean _isUsing,
		boolean _isIdentified) {
		
		location = new Location ();
		id = _id;
		uuid = _uuid;
		uuidOwner = _ownerUuid;
		enchant = _enchant;
		count = _count;
		durability = _durability;
		chargeCount = _chargeCount;
		isUsing = _isUsing;
		isIdentified = _isIdentified;
		
		if (CacheData.item.containsKey (_id)) {
			ItemTemplate template = CacheData.item.get (_id);
			majorType = 0;
			minorType = template.minorType;
			useType = template.useType;
			weight = template.weight * count;
			gfxInBag = template.gfxInBag;
			gfxOnGround = template.gfxOnGround;
			name = template.name;
			nameId = template.nameId;
			material = template.material;
			materialName = template.materialName;
			typeName = template.typeName;
			desciptId = template.descriptId;
			isTradeable = template.isTradeable;
			isStackable = template.isStackable;
			bless = template.bless;
			foodValue = template.foodValue;	
			dmgSmall = template.dmgSmall;
			dmgLarge = template.dmgLarge;
			delayTime = template.delayTime;
			
		} else if (CacheData.weapon.containsKey (_id)) {
			WeaponTemplate template = CacheData.weapon.get (_id);
			majorType = 1;
			minorType = template.minorType;
			useType = TYPE_USE_WEAPON;
			weight = template.weight;
			gfxInBag = template.gfxInBag;
			gfxOnGround = template.gfxOnGround;
			gfx = template.gfx;
			name = template.name;
			nameId = template.nameId;
			material = template.material;
			materialName = template.materialName;
			desciptId = template.descriptId;
			isTradeable = template.isTradeable;
			isStackable = template.isStackable;
			bless = template.bless;
			
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
			dmgSmall = template.dmgSmall;
			dmgLarge = template.dmgLarge;
			
		} else if (CacheData.armor.containsKey (_id)) {
			ArmorTemplate template = CacheData.armor.get (_id);
			majorType = 2;
			minorType = template.minorType;
			useType = ArmorMinorType2UseType (template.minorType);
			weight = template.weight;
			gfxInBag = template.gfxInBag;
			gfxOnGround = template.gfxOnGround;
			name = template.name;
			nameId = template.nameId;
			material = template.material;
			materialName = template.materialName;
			typeName = template.typeName;
			desciptId = template.descriptId;
			isTradeable = template.isTradeable;
			isStackable = template.isStackable;
			bless = template.bless;
			
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
		
		
		updateDetail ();
	}
	
	public String getName () {
		StringBuffer veiwName = new StringBuffer (name);
		if (isItem ()) {
			if (count > 1) {
				veiwName.append (" (" + count + ")");
			}
			
		} else if (isWeapon ()) {
			if (isIdentified) {
				veiwName.insert (0, "+" + enchant + " ") ;
			} 
			if (isUsing) {
				veiwName.append (" ($9)") ;
			}
			
		} else if (isArmor ()) {
			if (isIdentified) {
				veiwName.insert (0, "+" + enchant + " ") ;
			} 
			if (isUsing) {
				veiwName.append (" ($117)") ;
			}
			
		}
		
		return veiwName.toString ();
	}
	
	
	public byte[] getItemDetail () {
		PacketBuilder packet = new PacketBuilder ();
		
		switch (typeName) {
		case "light" :
			packet.writeByte (22);
			packet.writeWord (10); //light range
			break;
			
		case "food" :
			packet.writeByte (21);
			packet.writeWord (foodValue) ;
			break;
		
		case "arrow" :
		case "sting" :
			packet.writeByte (1);
			packet.writeByte (dmgSmall);
			packet.writeByte (dmgLarge);
			break; 
			
		default :
			packet.writeByte (23);
			break;
		}
		
		packet.writeByte (material);
		//packet.writeDoubleWord ((weight * count) / 1000);
		packet.writeDoubleWord (weight / 1000);
		
		return packet.getPacketNoPadding ();
	}
	
	public byte[] getWeaponDetail () {
		PacketBuilder packet = new PacketBuilder ();
		
		packet.writeByte (1) ;
		packet.writeByte (dmgSmall);
		packet.writeByte (dmgLarge);
		packet.writeByte (material);
		packet.writeDoubleWord (weight / 1000);
		
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
	
	public byte[] getArmorDetail () {
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
	
	public byte[] getDetail () {
		if (detail == null) {
			updateDetail ();
		}
		return detail;
	}
	
	public void updateDetail () {
		switch (majorType) {
		case 0:
			detail = getItemDetail ();
			break;
		case 1:
			detail = getWeaponDetail ();
			break;
		case 2:
			detail = getItemDetail ();
			break;
		default: break;
		}
	}
	
	public boolean isWeapon () {
		return majorType == 1;
	}
	
	public boolean isRemoteWeapon () {
		return (minorType == WEAPON_TYPE_BOW) || (minorType == WEAPON_TYPE_SINGLE_BOW) || (minorType == WEAPON_TYPE_GAUNTLET);
	}
	
	public boolean isArmor () {
		return majorType == 2;
	}
	
	public boolean isArrow () {
		return (majorType == 0) && (minorType == TYPE_ARROW);
	}
	
	public boolean isItem () {
		return majorType == 0;
	}
	
	public boolean isSilver () {
		return material == MATERIAL_SILVER;
	}
	
	public boolean isMithril () {
		return material == MATERIAL_MITHRIL;
	}
	
	public boolean isOriharukon () {
		return material == MATERIAL_ORIHARUKON;
	}
	
	/*
	 * 自身對p(x, y)的距離
	 */
	public int getDistance (int x, int y) {
		int dx = Math.abs (x - location.point.x);
		int dy = Math.abs (y - location.point.y);
		
		return (int) Math.sqrt (Math.pow (dx, 2) + Math.pow (dy, 2) );		
	}
}

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
	
	public boolean isUsing;
	
	//加入SkillEffectTimer
	
	public ItemInstance () {
		location = new Location ();
	}
	
	public ItemInstance (
		int _id,
		int _uuid,
		int _ownerUuid,
		int _count,
		int _durability,
		int _chargeCount,
		boolean _isUsing,
		boolean _isIdentified) {
		
		ItemTemplate template = CacheData.item.get (_id);
		location = new Location ();
		id = _id;
		uuid = _uuid;
		uuidOwner = _ownerUuid;
		count = _count;
		weight = template.weight * count;
		gfxInBag = template.gfxInBag;
		gfxOnGround = template.gfxOnGround;
		name = template.name;
		nameId = template.nameId;
		material = template.material;
		materialName = template.materialName;
		typeName = template.typeName;
		majorType = 0;
		minorType = template.minorType;
		useType = template.useType;
		desciptId = template.descriptId;
		isIdentified = _isIdentified;
		isUsing = _isUsing;
		isTradeable = template.isTradeable;
		isStackable = template.isStackable;
		durability = _durability;
		chargeCount = _chargeCount;
		bless = template.bless;
		foodValue = template.foodValue;	
		dmgSmall = template.dmgSmall;
		dmgLarge = template.dmgLarge;
	}

	public String getName () {
		StringBuffer itemViewName = new StringBuffer (name);
		
		if (count > 1) {
			itemViewName.append (" (" + count + ")");
		}
		
		return itemViewName.toString ();
	}
	
	public byte[] getDetail () {
		PacketBuilder packet = new PacketBuilder ();
		
		switch (typeName) {
		case "light" :
			packet.writeByte (22) ;
			packet.writeWord (10) ; //light range
			break;
			
		case "food" :
			packet.writeByte (21) ;
			packet.writeWord (foodValue) ;
			break;
		
		case "arrow" :
		case "sting" :
			packet.writeByte (1) ;
			packet.writeByte (dmgSmall) ;
			packet.writeByte (dmgLarge) ;
			break; 
			
		default :
			packet.writeByte (23) ;
			break;
		}
		
		packet.writeByte (material) ;
		packet.writeDoubleWord ((weight * count) / 1000);
		
		return packet.getPacketNoPadding () ;
	}
}

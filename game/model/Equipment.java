package vidar.game.model;

import java.util.*;

import vidar.server.*;
import vidar.server.database.*;
import vidar.server.process_server.*;
import vidar.game.model.item.*;

public class Equipment
{
	/* Offset -> equiement type
	 * 0  Weapon
	 * 1  Arrow
	 * 2  Helmet
	 * 3  Armor
	 * 4  T_shirt
	 * 5  Cloak
	 * 6  Boots
	 * 7  Shield
	 * 8  Belt
	 * 9  Amulet
	 * 10 Ring1
	 * 11 Ring2
	 * 12 Earring
	 */
	//private static final int WEAPON = 0;
	//private static final int ARROW = 1;
	private static final int HELMET = 2;
	private static final int ARMOR = 3;
	private static final int TSHIRT = 4;
	private static final int CLOAK = 5;
	private static final int BOOTS = 6;
	private static final int SHIELD = 7;
	private static final int BELT = 8;
	private static final int AMULET = 9;
	private static final int RING1 = 10;
	private static final int RING2 = 11;
	private static final int EARRING = 12;
	
	private SessionHandler handle;
	
	public WeaponInstance weapon;
	public ItemInstance arrow;
	public ArmorInstance helmet;
	public ArmorInstance armor;
	public ArmorInstance tshirt;
	public ArmorInstance cloak;
	public ArmorInstance boots;
	public ArmorInstance shield;
	public ArmorInstance belt;
	public ArmorInstance amulet;
	public ArmorInstance ring1;
	public ArmorInstance ring2;
	public ArmorInstance earring;
	
	public Equipment (SessionHandler handle) {
		this.handle = handle;
	}
	
	public void setWeapon (WeaponInstance _weapon) {
		//加入檢查雙手武器及盾牌判定
		
		if (weapon == null) { //空手=>WEAPON
			weapon = _weapon;
			weapon.isUsing = true;
			handle.sendPacket (new ItemUpdateName(weapon).getRaw());
			DatabaseCmds.updateItem (weapon);
		} else {
			if (weapon.uuid == _weapon.uuid) { //WEAPON->空手
				weapon.isUsing = false;
				handle.sendPacket (new ItemUpdateName(weapon).getRaw());
				DatabaseCmds.updateItem (weapon);
				weapon = null;
				
			} else { //WEAPON A-> WEAPON B
				weapon.isUsing = false;
				handle.sendPacket (new ItemUpdateName (weapon).getRaw());
				DatabaseCmds.updateItem (weapon);
				
				weapon = _weapon;
				weapon.isUsing = true;
				handle.sendPacket (new ItemUpdateName (weapon).getRaw());
				DatabaseCmds.updateItem (weapon);
			}
		}
	}
	
	/* c_itemuse.java : 3498 (UseArmor)->4185 */
	public void setArmor (ArmorInstance _armor) {
		ArmorInstance temp;
		switch (_armor.minorType + 1) {
		case HELMET: 
			temp = helmet; break;
		case ARMOR: 
			temp = armor; break;
		case TSHIRT:
			temp = tshirt; break;
		case CLOAK:
			temp = cloak; break;
		case BOOTS:
			temp = boots; break;
		case SHIELD:
			temp = shield; break;
		case BELT:
			temp = belt; break;
		case AMULET:
			temp = amulet; break;
		case RING1:
			temp = ring1; break;
		case RING2:
			temp = ring2; break;
		case EARRING:
			temp = earring; break;
		default:
			temp = null; break;
		}
		
		
		
		/*
		int TypeIndex = a.MinorType + 1;
		
		if (equipment[TypeIndex] != null) {
			if (equipment[TypeIndex].Uuid == a.Uuid) {
				//P裝備->解除裝備
				equipment[TypeIndex].IsEquipped = false;
				handle.sendPacket (new ItemUpdateName (equipment[TypeIndex]).getRaw () ) ;
				DatabaseCmds.updateItem (equipment[TypeIndex]) ;
				equipment[TypeIndex] = null;
				
				//System.out.printf ("解除裝備 %s\n", a.getName () ) ;
			} else {
				//P裝備->A裝備
				equipment[TypeIndex].IsEquipped = false;
				handle.sendPacket (new ItemUpdateName (equipment[TypeIndex]).getRaw () ) ;
				DatabaseCmds.updateItem (equipment[TypeIndex]) ;
				
				equipment[TypeIndex] = a;
				equipment[TypeIndex].IsEquipped = true;
				handle.sendPacket (new ItemUpdateName (equipment[TypeIndex]).getRaw () ) ;
				DatabaseCmds.updateItem (equipment[TypeIndex]) ;
				
				//System.out.printf ("裝備 %s\n", a.getName () ) ;
			}
		} else {
			//空裝->裝備(A)道具
			equipment[TypeIndex] = a;
			equipment[TypeIndex].IsEquipped = true;
			handle.sendPacket (new ItemUpdateName (equipment[TypeIndex]).getRaw () ) ;
			DatabaseCmds.updateItem (equipment[TypeIndex]) ;
			
			//System.out.printf ("裝備 %s\n", a.getName () ) ;
		}*/
	}
	
	public void setArrow (ItemInstance _arrow) {
		//
	}
	
	public List<ItemInstance> getList () {
		List<ItemInstance> result = new ArrayList<ItemInstance> ();
		result.add (weapon);
		result.add (arrow);
		result.add (helmet);
		result.add (armor);
		result.add (tshirt);
		result.add (cloak);
		result.add (boots);
		result.add (shield);
		result.add (belt);
		result.add (amulet);
		result.add (ring1);
		result.add (ring2);
		result.add (earring);
		
		return result;
	}
}

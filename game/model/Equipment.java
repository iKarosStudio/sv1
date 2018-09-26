package vidar.game.model;

import java.util.*;

import vidar.server.*;
import vidar.server.database.*;
import vidar.server.process_server.*;
import vidar.game.model.item.*;

import static vidar.game.template.ItemTypeTable.*;

public class Equipment
{
	private SessionHandler handle;
	
	public ItemInstance weapon;
	public ItemInstance arrow;
	public ItemInstance helmet;
	public ItemInstance armor;
	public ItemInstance tshirt;
	public ItemInstance cloak;
	public ItemInstance boots;
	public ItemInstance shield;
	public ItemInstance belt;
	public ItemInstance amulet;
	public ItemInstance ring1;
	public ItemInstance ring2;
	public ItemInstance earring;
	
	public Equipment (SessionHandler handle) {
		this.handle = handle;
	}
	
	public void setWeapon (ItemInstance _weapon) {
		//加入檢查雙手武器及盾牌判定
		
		if (weapon == null) { //空手=>WEAPON
			weapon = _weapon;
			weapon.isUsing = true;
			handle.sendPacket (new UpdateItemName(weapon).getRaw());
			DatabaseCmds.updateItem (weapon);
		} else {
			if (weapon.uuid == _weapon.uuid) { //WEAPON->空手
				weapon.isUsing = false;
				handle.sendPacket (new UpdateItemName(weapon).getRaw());
				DatabaseCmds.updateItem (weapon);
				weapon = null;
				
			} else { //WEAPON A-> WEAPON B
				weapon.isUsing = false;
				handle.sendPacket (new UpdateItemName (weapon).getRaw());
				DatabaseCmds.updateItem (weapon);
				
				weapon = _weapon;
				weapon.isUsing = true;
				handle.sendPacket (new UpdateItemName (weapon).getRaw());
				DatabaseCmds.updateItem (weapon);
			}
		}
	}
	
	/* c_itemuse.java : 3498 (UseArmor)->4185 */
	public void setEquipment (ItemInstance item) {	
		switch (item.minorType) {
		case ARMOR_TYPE_HELM:
			setHelmet (item);
			break;
			
		case ARMOR_TYPE_ARMOR:
			setArmor (item);
			break;
			
		case ARMOR_TYPE_T:
			setTshirt (item);
			break;
			
		case ARMOR_TYPE_CLOAK:
			setCloak (item);
			break;
			
		case ARMOR_TYPE_BOOTS:
			setBoots (item);
			break;
			
		case ARMOR_TYPE_SHIELD:
			setShield (item);
			break;
			
		case ARMOR_TYPE_BELT:
			setBelt (item);
			break;
			
		case ARMOR_TYPE_AMULET:
			setAmulet (item);
			break;
			
		case ARMOR_TYPE_RING:
			setRing1 (item);
			break;
			
		case ARMOR_TYPE_RING2:
			setRing2 (item);
			break;
			
		case ARMOR_TYPE_EARRING:
			setEarring (item);
			break;
			
		default:
			System.out.printf ("未知裝備minor type\n");
			break;
		}
	}
	
	public void setHelmet (ItemInstance _helmet) {
		if (helmet == null) {
			helmet = _helmet;
			helmet.isUsing = true;
			updateItem (helmet);
		} else {
			if (helmet.uuid == _helmet.uuid) {
				helmet.isUsing = false;
				updateItem (helmet);
				helmet = null;
			} else {
				helmet.isUsing = false;
				updateItem (helmet);
				helmet = _helmet;
				helmet.isUsing = true;
				updateItem (helmet);
			}
		}
	}
	
	public void setArmor (ItemInstance _armor) {
		if (armor == null) {
			armor = _armor;
			armor.isUsing = true;
			updateItem (armor);
		} else {
			if (armor.uuid == _armor.uuid) {
				armor.isUsing = false;
				updateItem (armor);
				armor = null;
			} else {
				armor.isUsing = false;
				updateItem (armor);
				armor = _armor;
				armor.isUsing = true;
				updateItem (armor);
			}
		}
	}
	
	public void setTshirt (ItemInstance _tshirt) {
		if (tshirt == null) {
			tshirt = _tshirt;
			tshirt.isUsing = true;
			updateItem (tshirt);
		} else {
			if (tshirt.uuid == _tshirt.uuid) {
				tshirt.isUsing = false;
				updateItem (tshirt);
				tshirt = null;
			} else {
				tshirt.isUsing = false;
				updateItem (tshirt);
				tshirt = _tshirt;
				tshirt.isUsing = true;
				updateItem (tshirt);
			}
		}
	}
	
	public void setCloak (ItemInstance _cloak) {
		if (cloak == null) {
			cloak = _cloak;
			cloak.isUsing = true;
			updateItem (cloak);
		} else {
			if (cloak.uuid == _cloak.uuid) {
				cloak.isUsing = false;
				updateItem (cloak);
				cloak = null;
			} else {
				cloak.isUsing = false;
				updateItem (cloak);
				cloak = _cloak;
				cloak.isUsing = true;
				updateItem (cloak);
			}
		}
	}
	
	public void setBoots (ItemInstance _boots) {
		if (boots == null) {
			boots = _boots;
			boots.isUsing = true;
			updateItem (boots);
		} else {
			if (boots.uuid == _boots.uuid) {
				boots.isUsing = false;
				updateItem (boots);
				boots = null;
			} else {
				boots.isUsing = false;
				updateItem (boots);
				boots = _boots;
				boots.isUsing = true;
				updateItem (boots);
			}
		}
	}
	
	public void setShield (ItemInstance _shield) {
		if (shield == null) {
			shield = _shield;
			shield.isUsing = true;
			updateItem (shield);
		} else {
			if (shield.uuid == _shield.uuid) {
				shield.isUsing = false;
				updateItem (shield);
				shield = null;
			} else {
				shield.isUsing = false;
				updateItem (shield);
				shield = _shield;
				shield.isUsing = true;
				updateItem (shield);
			}
		}
	}
	
	public void setBelt (ItemInstance _belt) {
		if (belt == null) {
			belt = _belt;
			belt.isUsing = true;
			updateItem (belt);
		} else {
			if (belt.uuid == _belt.uuid) {
				belt.isUsing = false;
				updateItem (belt);
				belt = null;
			} else {
				belt.isUsing = false;
				updateItem (belt);
				belt = _belt;
				belt.isUsing = true;
				updateItem (belt);
			}
		}
	}
	
	public void setAmulet (ItemInstance _amulet) {
		if (amulet == null) {
			amulet = _amulet;
			amulet.isUsing = true;
			updateItem (amulet);
		} else {
			if (amulet.uuid == _amulet.uuid) {
				amulet.isUsing = false;
				updateItem (amulet);
				amulet = null;
			} else {
				amulet.isUsing = false;
				updateItem (amulet);
				amulet = _amulet;
				amulet.isUsing = true;
				updateItem (amulet);
			}
		}
	}
	
	public void setRing1 (ItemInstance _ring1) {
		if (ring1 == null) {
			ring1 = _ring1;
			ring1.isUsing = true;
			updateItem (ring1);
		} else {
			if (ring1.uuid == _ring1.uuid) {
				ring1.isUsing = false;
				updateItem (ring1);
				ring1 = null;
			} else {
				ring1.isUsing = false;
				updateItem (ring1);
				ring1 = _ring1;
				ring1.isUsing = true;
				updateItem (ring1);
			}
		}
	}
	
	public void setRing2 (ItemInstance _ring2) {
		if (ring2 == null) {
			ring2 = _ring2;
			ring2.isUsing = true;
			updateItem (ring2);
		} else {
			if (ring2.uuid == _ring2.uuid) {
				ring2.isUsing = false;
				updateItem (ring2);
				ring2 = null;
			} else {
				ring2.isUsing = false;
				updateItem (ring2);
				ring2 = _ring2;
				ring2.isUsing = true;
				updateItem (ring2);
			}
		}
	}
	
	public void setEarring (ItemInstance _earring) {
		if (earring == null) {
			earring = _earring;
			earring.isUsing = true;
			updateItem (earring);
		} else {
			if (earring.uuid == _earring.uuid) {
				earring.isUsing = false;
				updateItem (earring);
				earring = null;
			} else {
				earring.isUsing = false;
				updateItem (earring);
				earring = _earring;
				earring.isUsing = true;
				updateItem (earring);
			}
		}
	}
	
	public void setArrow (ItemInstance _arrow) {
		//
	}
	
	public void updateItem (ItemInstance _item) {
		handle.sendPacket (new UpdateItemName(_item).getRaw());
		DatabaseCmds.updateItem (_item);
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
	
	public List<ItemInstance> getArmorList () {
		List<ItemInstance> result = new ArrayList<ItemInstance> ();
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

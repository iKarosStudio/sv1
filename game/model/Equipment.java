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
	
	//public ArmorInstance temp = null;
	
	public Equipment (SessionHandler handle) {
		this.handle = handle;
	}
	
	public void setWeapon (WeaponInstance _weapon) {
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
	public void setEquipment (ArmorInstance _armor) {		
		switch (_armor.minorType) {
		case ARMOR_TYPE_HELM:
			setHelmet (_armor);
			break;
			
		case ARMOR_TYPE_ARMOR:
			setArmor (_armor);
			break;
			
		case ARMOR_TYPE_T:
			setTshirt (_armor);
			break;
			
		case ARMOR_TYPE_CLOAK:
			setCloak (_armor);
			break;
			
		case ARMOR_TYPE_BOOTS:
			setBoots (_armor);
			break;
			
		case ARMOR_TYPE_SHIELD:
			setShield (_armor);
			break;
			
		case ARMOR_TYPE_BELT:
			setBelt (_armor);
			break;
			
		case ARMOR_TYPE_AMULET:
			setAmulet (_armor);
			break;
			
		case ARMOR_TYPE_RING:
			setRing1 (_armor);
			break;
			
		case ARMOR_TYPE_RING2:
			setRing2 (_armor);
			break;
			
		case ARMOR_TYPE_EARRING:
			setEarring (_armor);
			break;
			
		default:
			break;
		}
	}
	
	public void setHelmet (ArmorInstance _helmet) {
		if (_helmet.minorType != ARMOR_TYPE_HELM) {
			return;
		}
		
		if (helmet == null) {
			helmet = _helmet;
			updateUsingStatus (helmet, true);
		} else {
			if (helmet.uuid == _helmet.uuid) {
				updateUsingStatus (helmet, false);
				helmet = null;
			} else {
				updateUsingStatus (helmet, false);
				
				helmet = _helmet;
				updateUsingStatus (helmet, true);
			}
		}
	}
	
	public void setArmor (ArmorInstance _armor) {
		if (_armor.minorType != ARMOR_TYPE_ARMOR) {
			return;
		}
		
		if (armor == null) {
			armor = _armor;
			updateUsingStatus (armor, true);
		} else {
			if (armor.uuid == _armor.uuid) {
				updateUsingStatus (armor, false);
				armor = null;
			} else {
				updateUsingStatus (armor, false);
				
				armor = _armor;
				updateUsingStatus (armor, true);
			}
		}
	}
	
	public void setTshirt (ArmorInstance _tshirt) {
		if (_tshirt.minorType != ARMOR_TYPE_T) {
			return;
		}
		
		if (tshirt == null) {
			tshirt = _tshirt;
			updateUsingStatus (tshirt, true);
		} else {
			if (tshirt.uuid == _tshirt.uuid) {
				updateUsingStatus (tshirt, false);
				tshirt = null;
			} else {
				updateUsingStatus (tshirt, false);
				
				tshirt = _tshirt;
				updateUsingStatus (tshirt, true);
			}
		}
	}
	
	public void setCloak (ArmorInstance _cloak) {
		if (_cloak.minorType != ARMOR_TYPE_CLOAK) {
			return;
		}
		
		if (cloak == null) {
			cloak = _cloak;
			updateUsingStatus (cloak, true);
		} else {
			if (cloak.uuid == _cloak.uuid) {
				updateUsingStatus (cloak, false);
				cloak = null;
			} else {
				updateUsingStatus (cloak, false);
				
				cloak = _cloak;
				updateUsingStatus (cloak, true);
			}
		}
	}
	
	public void setBoots (ArmorInstance _boots) {
		if (_boots.minorType != ARMOR_TYPE_BOOTS) {
			return;
		}
		
		if (boots == null) {
			boots = _boots;
			updateUsingStatus (boots, true);
		} else {
			if (boots.uuid == _boots.uuid) {
				updateUsingStatus (boots, false);
				boots = null;
			} else {
				updateUsingStatus (boots, false);
				
				boots = _boots;
				updateUsingStatus (boots, true);
			}
		}
	}
	
	public void setShield (ArmorInstance _shield) {
		if (_shield.minorType != ARMOR_TYPE_SHIELD) {
			return;
		}
		
		if (shield == null) {
			shield = _shield;
			updateUsingStatus (shield, true);
		} else {
			if (shield.uuid == _shield.uuid) {
				updateUsingStatus (shield, false);
				shield = null;
			} else {
				updateUsingStatus (shield, false);
				
				shield = _shield;
				updateUsingStatus (shield, true);
			}
		}
	}
	
	public void setBelt (ArmorInstance _belt) {
		if (_belt.minorType != ARMOR_TYPE_BELT) {
			return;
		}
		
		if (belt == null) {
			belt = _belt;
			updateUsingStatus (belt, true);
		} else {
			if (belt.uuid == _belt.uuid) {
				updateUsingStatus (belt, false);
				belt = null;
			} else {
				updateUsingStatus (belt, false);
				
				belt = _belt;
				updateUsingStatus (belt, true);
			}
		}
	}
	
	public void setAmulet (ArmorInstance _amulet) {
		if (_amulet.minorType != ARMOR_TYPE_AMULET) {
			return;
		}
		
		if (amulet == null) {
			amulet = _amulet;
			updateUsingStatus (amulet, true);
		} else {
			if (amulet.uuid == _amulet.uuid) {
				updateUsingStatus (amulet, false);
				amulet = null;
			} else {
				updateUsingStatus (amulet, false);
				
				amulet = _amulet;
				updateUsingStatus (amulet, true);
			}
		}
	}
	
	public void setRing1 (ArmorInstance _ring1) {
		if (_ring1.minorType != ARMOR_TYPE_RING) {
			return;
		}
		
		if (ring1 == null) {
			ring1 = _ring1;
			updateUsingStatus (ring1, true);
		} else {
			if (ring1.uuid == _ring1.uuid) {
				updateUsingStatus (ring1, false);
				ring1 = null;
			} else {
				updateUsingStatus (ring1, false);
				
				ring1 = _ring1;
				updateUsingStatus (ring1, true);
			}
		}
	}
	
	public void setRing2 (ArmorInstance _ring2) {
		if (_ring2.minorType != ARMOR_TYPE_RING2) {
			return;
		}
		
		if (ring2 == null) {
			ring2 = _ring2;
			updateUsingStatus (ring2, true);
		} else {
			if (ring2.uuid == _ring2.uuid) {
				updateUsingStatus (ring2, false);
				ring2 = null;
			} else {
				updateUsingStatus (ring2, false);
				
				ring2 = _ring2;
				updateUsingStatus (ring2, true);
			}
		}
	}
	
	public void setEarring (ArmorInstance _earring) {
		if (_earring.minorType != ARMOR_TYPE_RING2) {
			return;
		}
		
		if (earring == null) {
			earring = _earring;
			updateUsingStatus (earring, true);
		} else {
			if (earring.uuid == _earring.uuid) {
				updateUsingStatus (earring, false);
				earring = null;
			} else {
				updateUsingStatus (earring, false);
				
				earring = _earring;
				updateUsingStatus (earring, true);
			}
		}
	}
	
	public void setArrow (ItemInstance _arrow) {
		//
	}
	
	public void updateUsingStatus (ArmorInstance _item, boolean _isUsing) {
		_item.isUsing = _isUsing;
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
	
	public List<ArmorInstance> getArmorList () {
		List<ArmorInstance> result = new ArrayList<ArmorInstance> ();
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

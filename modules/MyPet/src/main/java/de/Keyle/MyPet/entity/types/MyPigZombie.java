/*
 * This file is part of mypet
 *
 * Copyright (C) 2011-2016 Keyle
 * mypet is licensed under the GNU Lesser General Public License.
 *
 * mypet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mypet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.entity.types;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.DefaultInfo;
import de.Keyle.MyPet.api.entity.EquipmentSlot;
import de.Keyle.MyPet.api.entity.MyPetType;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.entity.MyPet;
import de.keyle.knbt.TagByte;
import de.keyle.knbt.TagCompound;
import de.keyle.knbt.TagInt;
import de.keyle.knbt.TagList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Material.ROTTEN_FLESH;

@DefaultInfo(food = {ROTTEN_FLESH})
public class MyPigZombie extends MyPet implements de.Keyle.MyPet.api.entity.types.MyPigZombie {
    protected boolean isBaby = false;
    protected Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();

    public MyPigZombie(MyPetPlayer petOwner) {
        super(petOwner);
    }

    public ItemStack[] getEquipment() {
        ItemStack[] equipment = new ItemStack[EquipmentSlot.values().length];
        for (int i = 0; i < EquipmentSlot.values().length; i++) {
            equipment[i] = getEquipment(EquipmentSlot.getSlotById(i));
        }
        return equipment;
    }

    public ItemStack getEquipment(EquipmentSlot slot) {
        return equipment.get(slot);
    }

    @Override
    public TagCompound writeExtendedInfo() {
        TagCompound info = super.writeExtendedInfo();
        info.getCompoundData().put("Baby", new TagByte(isBaby()));

        List<TagCompound> itemList = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (getEquipment(slot) != null) {
                TagCompound item = MyPetApi.getBukkitHelper().itemStackToCompund(getEquipment(slot));
                item.getCompoundData().put("Slot", new TagInt(slot.getSlotId()));
                itemList.add(item);
            }
        }
        info.getCompoundData().put("Equipment", new TagList(itemList));
        return info;
    }

    @Override
    public void readExtendedInfo(TagCompound info) {
        if (info.getCompoundData().containsKey("Baby")) {
            setBaby(info.getAs("Baby", TagByte.class).getBooleanData());
        }
        if (info.getCompoundData().containsKey("Equipment")) {
            TagList equipment = info.getAs("Equipment", TagList.class);
            for (int i = 0; i < equipment.getReadOnlyList().size(); i++) {
                TagCompound item = equipment.getTagAs(i, TagCompound.class);

                ItemStack itemStack = MyPetApi.getBukkitHelper().compundToItemStack(item);
                setEquipment(EquipmentSlot.getSlotById(item.getAs("Slot", TagInt.class).getIntData()), itemStack);
            }
        }
    }

    @Override
    public MyPetType getPetType() {
        return MyPetType.PigZombie;
    }

    public boolean isBaby() {
        return isBaby;
    }

    public void setBaby(boolean flag) {
        if (status == PetState.Here) {
            getEntity().getHandle().updateVisuals();
        }
        this.isBaby = flag;
    }

    public void setEquipment(EquipmentSlot slot, ItemStack item) {
        if (item == null) {
            equipment.remove(slot);
            getEntity().getHandle().updateVisuals();
            return;
        }
        item = item.clone();
        item.setAmount(1);
        equipment.put(slot, item);
        if (status == PetState.Here) {
            getEntity().getHandle().updateVisuals();
        }
    }

    @Override
    public void dropEquipment() {
        if (getStatus() == PetState.Here) {
            Location dropLocation = getLocation();
            for (ItemStack itemStack : equipment.values()) {
                if (itemStack != null) {
                    dropLocation.getWorld().dropItem(dropLocation, itemStack);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "MyPigZombie{owner=" + getOwner().getName() + ", name=" + ChatColor.stripColor(petName) + ", exp=" + experience.getExp() + "/" + experience.getRequiredExp() + ", lv=" + experience.getLevel() + ", status=" + status.name() + ", skilltree=" + (skillTree != null ? skillTree.getName() : "-") + ", worldgroup=" + worldGroup + "}";
    }
}
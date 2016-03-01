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
import de.Keyle.MyPet.api.entity.MyPetType;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.entity.MyPet;
import de.keyle.knbt.TagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static de.Keyle.MyPet.api.entity.LeashFlag.UserCreated;
import static org.bukkit.Material.IRON_INGOT;

@DefaultInfo(food = {IRON_INGOT}, leashFlags = {UserCreated})
public class MyIronGolem extends MyPet implements de.Keyle.MyPet.api.entity.types.MyIronGolem {
    protected ItemStack flower;

    public MyIronGolem(MyPetPlayer petOwner) {
        super(petOwner);
    }

    @Override
    public TagCompound writeExtendedInfo() {
        TagCompound info = super.writeExtendedInfo();
        if (hasFlower()) {
            info.getCompoundData().put("Flower", MyPetApi.getBukkitHelper().itemStackToCompund(getFlower()));
        }
        return info;
    }

    @Override
    public void readExtendedInfo(TagCompound info) {
        if (info.containsKeyAs("Flower", TagCompound.class)) {
            TagCompound itemTag = info.get("Flower");
            ItemStack item = MyPetApi.getBukkitHelper().compundToItemStack(itemTag);
            setFlower(item);
        }
    }

    @Override
    public MyPetType getPetType() {
        return MyPetType.IronGolem;
    }

    public ItemStack getFlower() {
        return flower;
    }

    public boolean hasFlower() {
        return flower != null;
    }

    public void setFlower(ItemStack item) {
        if (item != null && item.getType() != Material.RED_ROSE && item.getData().getData() == 0) {
            return;
        }
        this.flower = item;
        if (this.flower != null) {
            this.flower.setAmount(1);
        }
        if (status == PetState.Here) {
            getEntity().getHandle().updateVisuals();
        }
    }

    @Override
    public String toString() {
        return "MyIronGolem{owner=" + getOwner().getName() + ", name=" + ChatColor.stripColor(petName) + ", exp=" + experience.getExp() + "/" + experience.getRequiredExp() + ", lv=" + experience.getLevel() + ", status=" + status.name() + ", skilltree=" + (skillTree != null ? skillTree.getName() : "-") + ", worldgroup=" + worldGroup + "}";
    }
}
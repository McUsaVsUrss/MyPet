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

import de.Keyle.MyPet.api.entity.DefaultInfo;
import de.Keyle.MyPet.api.entity.MyPetType;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.entity.MyPet;
import de.keyle.knbt.TagByte;
import de.keyle.knbt.TagCompound;
import de.keyle.knbt.TagInt;
import org.bukkit.ChatColor;

import static org.bukkit.Material.APPLE;

@DefaultInfo(food = {APPLE})
public class MyVillager extends MyPet implements de.Keyle.MyPet.api.entity.types.MyVillager {
    protected int profession = 0;
    protected boolean isBaby = false;
    protected TagCompound originalData = null;

    public MyVillager(MyPetPlayer petOwner) {
        super(petOwner);
    }

    @Override
    public TagCompound writeExtendedInfo() {
        TagCompound info = super.writeExtendedInfo();
        info.getCompoundData().put("Profession", new TagInt(getProfession()));
        info.getCompoundData().put("Baby", new TagByte(isBaby()));
        if (originalData != null) {
            info.getCompoundData().put("OriginalData", originalData);
        }
        return info;
    }

    @Override
    public void readExtendedInfo(TagCompound info) {
        if (info.getCompoundData().containsKey("Profession")) {
            setProfession(info.getAs("Profession", TagInt.class).getIntData());
        }
        if (info.getCompoundData().containsKey("Baby")) {
            setBaby(info.getAs("Baby", TagByte.class).getBooleanData());
        }
        if (info.getCompoundData().containsKey("OriginalData")) {
            originalData = info.getAs("OriginalData", TagCompound.class);
        }
    }

    @Override
    public MyPetType getPetType() {
        return MyPetType.Villager;
    }

    public int getProfession() {
        return profession;
    }

    public void setProfession(int value) {
        if (status == PetState.Here) {
            getEntity().getHandle().updateVisuals();
        }
        this.profession = value;
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

    public void setOriginalData(TagCompound compound) {
        this.originalData = compound;
    }

    public TagCompound getOriginalData() {
        return this.originalData;
    }

    public boolean hasOriginalData() {
        return this.originalData != null;
    }

    @Override
    public String toString() {
        return "MyVillager{owner=" + getOwner().getName() + ", name=" + ChatColor.stripColor(petName) + ", exp=" + experience.getExp() + "/" + experience.getRequiredExp() + ", lv=" + experience.getLevel() + ", status=" + status.name() + ", skilltree=" + (skillTree != null ? skillTree.getName() : "-") + ", profession=" + getProfession() + ", baby=" + isBaby() + ", worldgroup=" + worldGroup + "}";
    }
}
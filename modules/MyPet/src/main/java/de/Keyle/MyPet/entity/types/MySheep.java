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
import org.bukkit.DyeColor;

import static org.bukkit.Material.WHEAT;

@DefaultInfo(food = {WHEAT})
public class MySheep extends MyPet implements de.Keyle.MyPet.api.entity.types.MySheep {
    protected DyeColor color = DyeColor.WHITE;
    protected boolean isSheared = false;
    protected boolean isBaby = false;

    public MySheep(MyPetPlayer petOwner) {
        super(petOwner);
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        if (status == PetState.Here) {
            getEntity().getHandle().updateVisuals();
        }
        this.color = color;
    }

    @Override
    public TagCompound writeExtendedInfo() {
        TagCompound info = super.writeExtendedInfo();
        info.getCompoundData().put("Color", new TagByte(getColor().getDyeData()));
        info.getCompoundData().put("Sheared", new TagByte(isSheared()));
        info.getCompoundData().put("Baby", new TagByte(isBaby()));
        return info;
    }

    @Override
    public void readExtendedInfo(TagCompound info) {
        if (info.containsKeyAs("Color", TagInt.class)) {
            setColor(DyeColor.getByDyeData((byte) info.getAs("Color", TagInt.class).getIntData()));
        } else if (info.containsKeyAs("Color", TagByte.class)) {
            setColor(DyeColor.getByDyeData(info.getAs("Color", TagByte.class).getByteData()));
        }
        if (info.getCompoundData().containsKey("Sheared")) {
            setSheared(info.getAs("Sheared", TagByte.class).getBooleanData());
        }
        if (info.getCompoundData().containsKey("Baby")) {
            setBaby(info.getAs("Baby", TagByte.class).getBooleanData());
        }
    }

    @Override
    public MyPetType getPetType() {
        return MyPetType.Sheep;
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

    public boolean isSheared() {
        return isSheared;
    }

    public void setSheared(boolean flag) {
        if (status == PetState.Here) {
            getEntity().getHandle().updateVisuals();
        }
        this.isSheared = flag;
    }

    @Override
    public String toString() {
        return "MySheep{owner=" + getOwner().getName() + ", name=" + ChatColor.stripColor(petName) + ", exp=" + experience.getExp() + "/" + experience.getRequiredExp() + ", lv=" + experience.getLevel() + ", status=" + status.name() + ", skilltree=" + (skillTree != null ? skillTree.getName() : "-") + ", worldgroup=" + worldGroup + ", color=" + getColor() + ", sheared=" + isSheared() + ", baby=" + isBaby() + "}";
    }
}
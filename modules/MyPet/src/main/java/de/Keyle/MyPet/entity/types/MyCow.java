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
import org.bukkit.ChatColor;

import static org.bukkit.Material.WHEAT;

@DefaultInfo(food = {WHEAT})
public class MyCow extends MyPet implements de.Keyle.MyPet.api.entity.types.MyCow {
    protected boolean isBaby = false;

    public MyCow(MyPetPlayer petOwner) {
        super(petOwner);
    }

    @Override
    public TagCompound writeExtendedInfo() {
        TagCompound info = super.writeExtendedInfo();
        info.getCompoundData().put("Baby", new TagByte(isBaby()));
        return info;
    }

    @Override
    public void readExtendedInfo(TagCompound info) {
        if (info.getCompoundData().containsKey("Baby")) {
            setBaby(info.getAs("Baby", TagByte.class).getBooleanData());
        }
    }

    @Override
    public MyPetType getPetType() {
        return MyPetType.Cow;
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

    @Override
    public String toString() {
        return "MyCow{owner=" + getOwner().getName() + ", name=" + ChatColor.stripColor(petName) + ", exp=" + experience.getExp() + "/" + experience.getRequiredExp() + ", lv=" + experience.getLevel() + ", status=" + status.name() + ", skilltree=" + (skillTree != null ? skillTree.getName() : "-") + ", worldgroup=" + worldGroup + ", baby=" + isBaby() + "}";
    }
}
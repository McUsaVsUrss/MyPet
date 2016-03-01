/*
 * This file is part of mypet-api
 *
 * Copyright (C) 2011-2016 Keyle
 * mypet-api is licensed under the GNU Lesser General Public License.
 *
 * mypet-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mypet-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.api.entity;

import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.skill.skilltree.SkillTree;
import de.keyle.knbt.TagCompound;

import java.util.UUID;

public interface MyPet {
    double getExp();

    void setExp(double exp);

    double getHealth();

    void setHealth(double health);

    double getHungerValue();

    void setHungerValue(double value);

    TagCompound getInfo();

    void setInfo(TagCompound info);

    void setOwner(MyPetPlayer owner);

    MyPetPlayer getOwner();

    String getPetName();

    void setPetName(String petName);

    MyPetType getPetType();

    void setPetType(MyPetType petType);

    boolean wantsToRespawn();

    void setWantsToRespawn(boolean wantsToRespawn);

    int getRespawnTime();

    void setRespawnTime(int respawnTime);

    SkillTree getSkilltree();

    boolean setSkilltree(SkillTree skillTree);

    TagCompound getSkillInfo();

    void setSkills(TagCompound skills);

    UUID getUUID();

    void setUUID(UUID uuid);

    String getWorldGroup();

    void setWorldGroup(String worldGroup);

    long getLastUsed();

    void setLastUsed(long lastUsed);
}
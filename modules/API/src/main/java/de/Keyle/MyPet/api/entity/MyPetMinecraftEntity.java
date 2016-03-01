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

import de.Keyle.MyPet.api.entity.ai.AIGoalSelector;
import de.Keyle.MyPet.api.entity.ai.navigation.AbstractNavigation;
import de.Keyle.MyPet.api.entity.ai.target.TargetPriority;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface MyPetMinecraftEntity {
    boolean isMyPet();

    ActiveMyPet getMyPet();

    AIGoalSelector getPathfinder();

    AIGoalSelector getTargetSelector();

    void setPathfinder();

    double getWalkSpeed();

    void makeSound(String sound, float volume, float pitch);

    MyPetBukkitEntity getBukkitEntity();

    MyPetPlayer getOwner();

    void updateNameTag();

    void setLocation(Location loc);

    AbstractNavigation getPetNavigation();

    void updateVisuals();

    LivingEntity getTarget();

    TargetPriority getTargetPriority();

    void setTarget(LivingEntity entity, TargetPriority priority);

    void forgetTarget();

    boolean hasTarget();

    boolean hasRider();

    void showPotionParticles(Color color);

    void hidePotionParticles();
}
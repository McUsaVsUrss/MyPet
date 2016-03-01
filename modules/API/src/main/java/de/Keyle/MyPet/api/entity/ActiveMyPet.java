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

import de.Keyle.MyPet.api.skill.MyPetExperience;
import de.Keyle.MyPet.api.skill.Skills;
import de.Keyle.MyPet.api.util.Scheduler;
import org.bukkit.Location;

public interface ActiveMyPet extends MyPet, Scheduler {
    MyPetExperience getExperience();

    enum PetState {
        Dead, Despawned, PetState, Here
    }

    enum SpawnFlags {
        Success, NoSpace, AlreadyHere, Dead, Canceled, OwnerDead, Flying, NotAllowed
    }

    void removePet();

    void removePet(boolean wantsToRespawn);

    PetState getStatus();

    void setStatus(PetState status);

    Location getLocation();

    double getMaxHealth();

    Skills getSkills();

    boolean autoAssignSkilltree();

    SpawnFlags createEntity();

    MyPetBukkitEntity getEntity();

    double getDamage();

    double getRangedDamage();

    boolean isPassiv();

    boolean hasTarget();

    void decreaseHunger(double value);
}
/*
 * This file is part of MyPet
 *
 * Copyright (C) 2011-2016 Keyle
 * MyPet is licensed under the GNU Lesser General Public License.
 *
 * MyPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.compat.v1_9_R1.skill.skills.ranged.bukkit;

import de.Keyle.MyPet.api.skill.skills.ranged.CraftMyPetProjectile;
import de.Keyle.MyPet.api.skill.skills.ranged.EntityMyPetProjectile;
import net.minecraft.server.v1_9_R1.EntitySnowball;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftSnowball;

public class CraftMyPetSnowball extends CraftSnowball implements CraftMyPetProjectile {

    public CraftMyPetSnowball(CraftServer server, EntitySnowball entity) {
        super(server, entity);
    }

    public EntityMyPetProjectile getMyPetProjectile() {
        return ((EntityMyPetProjectile) this.getHandle());
    }
}
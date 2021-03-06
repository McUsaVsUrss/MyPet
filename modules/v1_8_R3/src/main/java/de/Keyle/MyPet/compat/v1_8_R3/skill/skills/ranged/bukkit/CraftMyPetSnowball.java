/*
 * This file is part of mypet-v1_8_R3
 *
 * Copyright (C) 2011-2016 Keyle
 * mypet-v1_8_R3 is licensed under the GNU Lesser General Public License.
 *
 * mypet-v1_8_R3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mypet-v1_8_R3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.compat.v1_8_R3.skill.skills.ranged.bukkit;

import de.Keyle.MyPet.api.skill.skills.ranged.CraftMyPetProjectile;
import de.Keyle.MyPet.api.skill.skills.ranged.EntityMyPetProjectile;
import net.minecraft.server.v1_8_R3.EntitySnowball;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSnowball;
import org.bukkit.entity.LivingEntity;

public class CraftMyPetSnowball extends CraftSnowball implements CraftMyPetProjectile {

    public CraftMyPetSnowball(CraftServer server, EntitySnowball entity) {
        super(server, entity);
    }

    @Deprecated
    public LivingEntity _INVALID_getShooter() {
        return (LivingEntity) super.getShooter();
    }

    @Deprecated
    public void _INVALID_setShooter(LivingEntity shooter) {
        super.setShooter(shooter);
    }

    public EntityMyPetProjectile getMyPetProjectile() {
        return ((EntityMyPetProjectile) this.getHandle());
    }
}
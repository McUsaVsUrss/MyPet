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

package de.Keyle.MyPet.compat.v1_9_R1.entity.types;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.entity.EntitySize;
import de.Keyle.MyPet.compat.v1_9_R1.entity.EntityMyPet;
import net.minecraft.server.v1_9_R1.World;

@EntitySize(width = 0.7F, height = 0.475f)
public class EntityMySquid extends EntityMyPet {
    public EntityMySquid(World world, ActiveMyPet myPet) {
        super(world, myPet);
    }

    @Override
    protected String getDeathSound() {
        return "entity.squid.death";
    }

    @Override
    protected String getHurtSound() {
        return "entity.squid.hurt";
    }

    protected String getLivingSound() {
        return "entity.squid.ambient";
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.random.nextBoolean()) {
            MyPetApi.getBukkitHelper().playParticleEffect(myPet.getLocation().add(0, 0.7, 0), "WATER_SPLASH", 0.2F, 0.2F, 0.2F, 0.5F, 10, 20);
        }
    }
}
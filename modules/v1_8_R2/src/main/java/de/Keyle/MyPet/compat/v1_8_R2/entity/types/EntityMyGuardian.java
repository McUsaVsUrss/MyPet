/*
 * This file is part of mypet-compat
 *
 * Copyright (C) 2011-2016 Keyle
 * mypet-compat is licensed under the GNU Lesser General Public License.
 *
 * mypet-compat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mypet-compat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.compat.v1_8_R2.entity.types;

import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.entity.EntitySize;
import de.Keyle.MyPet.api.entity.types.MyGuardian;
import de.Keyle.MyPet.compat.v1_8_R2.entity.EntityMyPet;
import net.minecraft.server.v1_8_R2.World;

@EntitySize(width = 0.7F, height = 0.85F)
public class EntityMyGuardian extends EntityMyPet {
    public EntityMyGuardian(World world, ActiveMyPet myPet) {
        super(world, myPet);
    }

    @Override
    protected String getDeathSound() {
        if (getMyPet().isElder()) {
            return "mob.guardian.elder.death";
        }
        return "mob.guardian.death";
    }

    @Override
    protected String getHurtSound() {
        if (getMyPet().isElder()) {
            return "mob.guardian.elder.hit";
        }
        return "mob.guardian.hit";
    }

    protected String getLivingSound() {
        if (getMyPet().isElder()) {
            return "mob.guardian.elder.idle";
        }
        return "mob.guardian.idle";
    }

    @Override
    public void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(16, Integer.valueOf(0));
        this.datawatcher.a(17, Integer.valueOf(0));
    }

    @Override
    public void updateVisuals() {
        int old = this.datawatcher.getInt(16);
        if (getMyPet().isElder()) {
            this.datawatcher.watch(16, Integer.valueOf(old | 4));
        } else {
            this.datawatcher.watch(16, Integer.valueOf(old & ~4));
        }
    }

    public MyGuardian getMyPet() {
        return (MyGuardian) myPet;
    }
}
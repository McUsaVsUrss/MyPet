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

package de.Keyle.MyPet.compat.v1_8_R3.entity.ai.movement;

import de.Keyle.MyPet.api.entity.ai.AIGoal;
import de.Keyle.MyPet.compat.v1_8_R3.entity.EntityMyPet;
import de.Keyle.MyPet.compat.v1_8_R3.entity.types.EntityMyOcelot;
import de.Keyle.MyPet.compat.v1_8_R3.entity.types.EntityMyWolf;

public class Sit extends AIGoal {
    private EntityMyPet entityMyPet;
    private boolean sitting = false;

    public Sit(EntityMyPet entityMyPet) {
        this.entityMyPet = entityMyPet;
    }

    @Override
    public boolean shouldStart() {
        if (!(this.entityMyPet instanceof EntityMyOcelot) && !(this.entityMyPet instanceof EntityMyWolf)) {
            return false;
        } else if (this.entityMyPet.V()) { // -> isInWater()
            return false;
        } else if (!this.entityMyPet.onGround) {
            return false;
        }
        return this.sitting;
    }

    @Override
    public void start() {
        this.entityMyPet.getPetNavigation().stop();
        if (this.entityMyPet instanceof EntityMyOcelot) {
            ((EntityMyOcelot) this.entityMyPet).applySitting(true);
        }
        if (this.entityMyPet instanceof EntityMyWolf) {
            ((EntityMyWolf) this.entityMyPet).applySitting(true);
        }
        entityMyPet.setGoalTarget(null);
    }

    @Override
    public void finish() {
        if (this.entityMyPet instanceof EntityMyOcelot) {
            ((EntityMyOcelot) this.entityMyPet).applySitting(false);
        }
        if (this.entityMyPet instanceof EntityMyWolf) {
            ((EntityMyWolf) this.entityMyPet).applySitting(false);
        }
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void toogleSitting() {
        this.sitting = !this.sitting;
    }
}
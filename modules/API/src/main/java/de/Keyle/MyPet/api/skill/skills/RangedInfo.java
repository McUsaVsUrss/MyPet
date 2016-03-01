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

package de.Keyle.MyPet.api.skill.skills;

import de.Keyle.MyPet.api.skill.SkillInfo;
import de.Keyle.MyPet.api.skill.SkillName;
import de.Keyle.MyPet.api.skill.SkillProperties;
import de.Keyle.MyPet.api.skill.SkillProperties.NBTdatatypes;
import de.Keyle.MyPet.api.skill.skilltree.SkillTreeSkill;

@SkillName(value = "Ranged", translationNode = "Name.Skill.Ranged")
@SkillProperties(
        parameterNames = {"damage_double", "addset_damage", "projectile", "rateoffire", "addset_rateoffire"},
        parameterTypes = {NBTdatatypes.Double, NBTdatatypes.String, NBTdatatypes.String, NBTdatatypes.Int, NBTdatatypes.String},
        parameterDefaultValues = {"1.0", "add", "Arrow", "35", "add"})
public class RangedInfo extends SkillTreeSkill implements SkillInfo {
    protected double damage = 0;
    protected int rateOfFire = 0;

    public enum Projectiles {
        Arrow, Snowball, LargeFireball, SmallFireball, WitherSkull, Egg
    }

    protected Projectiles selectedProjectile = Projectiles.Arrow;

    public RangedInfo(boolean addedByInheritance) {
        super(addedByInheritance);
    }

    public double getDamage() {
        return damage;
    }

    public int getRateOfFire() {
        return rateOfFire;
    }

    public Projectiles getProjectile() {
        return selectedProjectile;
    }

    public SkillInfo cloneSkill() {
        RangedInfo newSkill = new RangedInfo(this.isAddedByInheritance());
        newSkill.setProperties(getProperties());
        return newSkill;
    }
}
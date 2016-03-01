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

package de.Keyle.MyPet.skill.skills;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.skill.ActiveSkill;
import de.Keyle.MyPet.api.skill.SkillInfo;
import de.Keyle.MyPet.api.skill.SkillInstance;
import de.Keyle.MyPet.api.skill.skills.ThornsInfo;
import de.Keyle.MyPet.api.util.locale.Translation;
import de.keyle.knbt.TagInt;
import de.keyle.knbt.TagString;
import org.bukkit.entity.LivingEntity;

import java.util.Random;

public class Thorns extends ThornsInfo implements SkillInstance, ActiveSkill {
    private static Random random = new Random();
    private ActiveMyPet myPet;

    public Thorns(boolean addedByInheritance) {
        super(addedByInheritance);
    }

    public void setMyPet(ActiveMyPet myPet) {
        this.myPet = myPet;
    }

    public ActiveMyPet getMyPet() {
        return myPet;
    }

    public boolean isActive() {
        return chance > 0;
    }

    public void upgrade(SkillInfo upgrade, boolean quiet) {
        if (upgrade instanceof ThornsInfo) {
            if (upgrade.getProperties().getCompoundData().containsKey("chance")) {
                if (!upgrade.getProperties().getCompoundData().containsKey("addset_chance") || upgrade.getProperties().getAs("addset_chance", TagString.class).getStringData().equals("add")) {
                    chance += upgrade.getProperties().getAs("chance", TagInt.class).getIntData();
                } else {
                    chance = upgrade.getProperties().getAs("chance", TagInt.class).getIntData();
                }
                if (!upgrade.getProperties().getCompoundData().containsKey("addset_reflection") || upgrade.getProperties().getAs("addset_reflection", TagString.class).getStringData().equals("add")) {
                    reflectedDamagePercent += upgrade.getProperties().getAs("reflection", TagInt.class).getIntData();
                } else {
                    reflectedDamagePercent = upgrade.getProperties().getAs("reflection", TagInt.class).getIntData();
                }
                reflectedDamagePercent = Math.min(reflectedDamagePercent, 100);
                chance = Math.min(chance, 100);
                if (!quiet) {
                    myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Skill.Thorns.Upgrade", myPet.getOwner().getLanguage()), myPet.getPetName(), chance, reflectedDamagePercent));
                }
            }
        }
    }

    public String getFormattedValue() {
        return chance + "% -> " + reflectedDamagePercent + "% " + Translation.getString("Name.Damage", myPet.getOwner().getLanguage());
    }

    public void reset() {
        chance = 0;
        reflectedDamagePercent = 0;
    }

    public boolean activate() {
        return random.nextDouble() < chance / 100.;
    }

    public double getReflectedDamage(double damage) {
        return damage * reflectedDamagePercent / 100.;
    }

    public void reflectDamage(LivingEntity damager, double damage) {
        damager.damage(getReflectedDamage(damage), myPet.getEntity());
        myPet.getEntity().getHandle().makeSound("damage.thorns", 0.5F, 1.0F);
        MyPetApi.getBukkitHelper().playParticleEffect(myPet.getLocation().add(0, 1, 0), "CRIT_MAGIC", 0.5F, 0.5F, 0.5F, 0.1F, 20, 20);
        MyPetApi.getBukkitHelper().playParticleEffect(myPet.getLocation().add(0, 1, 0), "CRIT", 0.5F, 0.5F, 0.5F, 0.1F, 10, 20);
    }

    @Override
    public SkillInstance cloneSkill() {
        Thorns newSkill = new Thorns(this.isAddedByInheritance());
        newSkill.setProperties(getProperties());
        return newSkill;
    }
}
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

package de.Keyle.MyPet.compat.v1_7_R4.entity.ai.target;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.entity.ai.AIGoal;
import de.Keyle.MyPet.api.entity.ai.target.TargetPriority;
import de.Keyle.MyPet.api.skill.skills.BehaviorInfo.BehaviorState;
import de.Keyle.MyPet.compat.v1_7_R4.entity.EntityMyPet;
import de.Keyle.MyPet.skill.skills.Behavior;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EntityTameableAnimal;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class OwnerHurtByTarget extends AIGoal {
    private EntityMyPet petEntity;
    private EntityLiving lastDamager;
    private ActiveMyPet myPet;
    private Behavior behaviorSkill = null;
    private EntityPlayer owner;

    public OwnerHurtByTarget(EntityMyPet entityMyPet) {
        this.petEntity = entityMyPet;
        myPet = entityMyPet.getMyPet();
        if (myPet.getSkills().hasSkill(Behavior.class)) {
            behaviorSkill = myPet.getSkills().getSkill(Behavior.class);
        }
        owner = ((CraftPlayer) petEntity.getOwner().getPlayer()).getHandle();
    }

    @Override
    public boolean shouldStart() {
        if (!petEntity.canMove()) {
            return false;
        }
        if (myPet.getDamage() <= 0 && myPet.getRangedDamage() <= 0) {
            return false;
        }
        this.lastDamager = owner.getLastDamager();

        if (this.lastDamager == null || !lastDamager.isAlive()) {
            return false;
        }
        if (lastDamager == petEntity) {
            return false;
        }
        if (lastDamager instanceof EntityPlayer) {
            if (owner == lastDamager) {
                return false;
            }

            Player targetPlayer = (Player) lastDamager.getBukkitEntity();

            if (!MyPetApi.getHookManager().canHurt(myPet.getOwner().getPlayer(), targetPlayer, true)) {
                return false;
            }
        } else if (lastDamager instanceof EntityMyPet) {
            ActiveMyPet targetMyPet = ((EntityMyPet) lastDamager).getMyPet();
            if (!MyPetApi.getHookManager().canHurt(myPet.getOwner().getPlayer(), targetMyPet.getOwner().getPlayer(), true)) {
                return false;
            }
        } else if (lastDamager instanceof EntityTameableAnimal) {
            EntityTameableAnimal tameable = (EntityTameableAnimal) lastDamager;
            if (tameable.isTamed() && tameable.getOwner() != null) {
                Player tameableOwner = (Player) tameable.getOwner().getBukkitEntity();
                if (myPet.getOwner().equals(tameableOwner)) {
                    return false;
                }
            }
        }
        if (!MyPetApi.getHookManager().canHurt(myPet.getOwner().getPlayer(), lastDamager.getBukkitEntity())) {
            return false;
        }
        if (behaviorSkill != null && behaviorSkill.isActive()) {
            if (behaviorSkill.getBehavior() == Behavior.BehaviorState.Friendly) {
                return false;
            }
            if (behaviorSkill.getBehavior() == BehaviorState.Raid) {
                if (lastDamager instanceof EntityTameableAnimal && ((EntityTameableAnimal) lastDamager).isTamed()) {
                    return false;
                }
                if (lastDamager instanceof EntityMyPet) {
                    return false;
                }
                if (lastDamager instanceof EntityPlayer) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldFinish() {
        if (!petEntity.canMove()) {
            return true;
        }
        if (!petEntity.hasTarget()) {
            return true;
        }

        EntityLiving target = ((CraftLivingEntity) this.petEntity.getTarget()).getHandle();

        if (target.world != petEntity.world) {
            return true;
        } else if (petEntity.f(target) > 400) {
            return true;
        } else if (petEntity.f(((CraftPlayer) petEntity.getOwner().getPlayer()).getHandle()) > 600) {
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        petEntity.setTarget((LivingEntity) this.lastDamager.getBukkitEntity(), TargetPriority.OwnerGetsHurt);
    }

    @Override
    public void finish() {
        petEntity.forgetTarget();
    }
}
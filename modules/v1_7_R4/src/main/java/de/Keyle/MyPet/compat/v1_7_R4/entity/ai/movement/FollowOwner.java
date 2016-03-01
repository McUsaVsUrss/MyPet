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

package de.Keyle.MyPet.compat.v1_7_R4.entity.ai.movement;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.ai.AIGoal;
import de.Keyle.MyPet.api.entity.ai.navigation.AbstractNavigation;
import de.Keyle.MyPet.compat.v1_7_R4.entity.EntityMyPet;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

public class FollowOwner extends AIGoal {
    private EntityMyPet petEntity;
    private AbstractNavigation nav;
    private int setPathTimer = 0;
    private float stopDistance;
    private double startDistance;
    private float teleportDistance;
    private Control controlPathfinderGoal;
    private EntityPlayer owner;

    public FollowOwner(EntityMyPet entityMyPet, double startDistance, float stopDistance, float teleportDistance) {
        this.petEntity = entityMyPet;
        this.nav = entityMyPet.getPetNavigation();
        this.startDistance = startDistance * startDistance;
        this.stopDistance = stopDistance * stopDistance;
        this.teleportDistance = teleportDistance * teleportDistance;
        this.owner = ((CraftPlayer) petEntity.getOwner().getPlayer()).getHandle();
    }

    @Override
    public boolean shouldStart() {
        if (controlPathfinderGoal == null) {
            if (petEntity.getPathfinder().hasGoal("Control")) {
                controlPathfinderGoal = (Control) petEntity.getPathfinder().getGoal("Control");
            }
        }
        if (!this.petEntity.canMove()) {
            return false;
        }
        if (this.petEntity.getTarget() != null && !this.petEntity.getTarget().isDead()) {
            return false;
        }
        if (this.petEntity.getOwner() == null) {
            return false;
        }
        if (this.petEntity.f(owner) < this.startDistance) {
            return false;
        }
        if (controlPathfinderGoal != null && controlPathfinderGoal.moveTo != null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldFinish() {
        if (controlPathfinderGoal.moveTo != null) {
            return true;
        } else if (this.petEntity.getOwner() == null) {
            return true;
        } else if (this.petEntity.f(owner) < this.stopDistance) {
            return true;
        } else if (!this.petEntity.canMove()) {
            return true;
        } else if (this.petEntity.getTarget() != null && !this.petEntity.getTarget().isDead()) {
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        applyWalkSpeed();
        this.setPathTimer = 0;
    }

    @Override
    public void finish() {
        nav.getParameters().removeSpeedModifier("FollowOwner");
        this.nav.stop();
    }

    @Override
    public void tick() {
        Location ownerLocation = this.petEntity.getMyPet().getOwner().getPlayer().getLocation();
        Location petLocation = this.petEntity.getMyPet().getLocation();
        if (ownerLocation.getWorld() != petLocation.getWorld()) {
            return;
        }

        this.petEntity.getControllerLook().a(owner, 10.0F, (float) this.petEntity.x());

        if (this.petEntity.canMove()) {
            if (--this.setPathTimer <= 0) {
                this.setPathTimer = 10;
                if (!this.nav.navigateTo(this.owner.getBukkitEntity())) {
                    if (owner.onGround && this.petEntity.f(owner) >= this.teleportDistance && controlPathfinderGoal.moveTo == null && !petEntity.hasTarget() && MyPetApi.getBukkitHelper().canSpawn(ownerLocation, this.petEntity)) {
                        this.petEntity.setPositionRotation(ownerLocation.getX(), ownerLocation.getY(), ownerLocation.getZ(), this.petEntity.yaw, this.petEntity.pitch);
                        this.nav.navigateTo(owner.getBukkitEntity());
                    }
                } else {
                    applyWalkSpeed();
                }
            }
        }
    }

    private void applyWalkSpeed() {
        float walkSpeed = owner.abilities.walkSpeed;
        if (owner.abilities.isFlying) {
            // make the pet faster when the player is flying
            walkSpeed += owner.abilities.flySpeed;
        } else if (owner.isSprinting()) {
            // make the pet faster when the player is sprinting
            if (owner.getAttributeMap().a(GenericAttributes.d) != null) {
                walkSpeed += owner.getAttributeMap().a(GenericAttributes.d).getValue();
            }
        } else if (owner.vehicle != null && owner.vehicle instanceof EntityLiving) {
            // adjust the speed to the pet can catch up with the vehicle the player is in
            AttributeInstance vehicleSpeedAttribute = ((EntityLiving) owner.vehicle).getAttributeMap().a(GenericAttributes.d);
            if (vehicleSpeedAttribute != null) {
                walkSpeed = (float) vehicleSpeedAttribute.getValue();
            }
        } else if (owner.hasEffect(MobEffectList.FASTER_MOVEMENT)) {
            // make the pet faster when the player is has the SPEED effect
            walkSpeed += owner.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier() * 0.2 * walkSpeed;
        }
        // make the pet a little bit faster than the player so it can catch up
        walkSpeed += 0.07f;

        nav.getParameters().addSpeedModifier("FollowOwner", walkSpeed);
    }
}
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

package de.Keyle.MyPet.compat.v1_7_R4.entity.types;

import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.entity.EntitySize;
import de.Keyle.MyPet.api.entity.types.MyEnderman;
import de.Keyle.MyPet.api.skill.skills.BehaviorInfo;
import de.Keyle.MyPet.compat.v1_7_R4.entity.EntityMyPet;
import de.Keyle.MyPet.skill.skills.Behavior;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;

@EntitySize(width = 0.6F, height = 2.55F)
public class EntityMyEnderman extends EntityMyPet {

    public EntityMyEnderman(World world, ActiveMyPet myPet) {
        super(world, myPet);
    }

    public int getBlockData() {
        return getMyPet().getBlock() != null ? getMyPet().getBlock().getData().getData() : 0;
    }

    public int getBlockID() {
        return getMyPet().getBlock() != null ? getMyPet().getBlock().getTypeId() : 0;
    }

    @Override
    protected String getDeathSound() {
        return "mob.endermen.death";
    }

    @Override
    protected String getHurtSound() {
        return "mob.endermen.hit";
    }

    @Override
    protected String getLivingSound() {
        return getMyPet().isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
    }

    public boolean handlePlayerInteraction(EntityHuman entityhuman) {
        if (super.handlePlayerInteraction(entityhuman)) {
            return true;
        }

        ItemStack itemStack = entityhuman.inventory.getItemInHand();

        if (getOwner().equals(entityhuman) && itemStack != null && canUseItem()) {
            if (itemStack.getItem() == Items.SHEARS && getMyPet().hasBlock() && getOwner().getPlayer().isSneaking()) {
                EntityItem entityitem = this.a(CraftItemStack.asNMSCopy(getMyPet().getBlock()), 1.0F);
                entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);

                makeSound("mob.sheep.shear", 1.0F, 1.0F);
                getMyPet().setBlock(null);
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemStack.damage(1, entityhuman);
                }

                return true;
            } else if (getMyPet().getBlock() == null && Util.isBetween(1, 256, Item.getId(itemStack.getItem())) && getOwner().getPlayer().isSneaking()) {
                getMyPet().setBlock(CraftItemStack.asBukkitCopy(itemStack));
                if (!entityhuman.abilities.canInstantlyBuild) {
                    if (--itemStack.count <= 0) {
                        entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                    }
                }
                return true;
            }
        }
        return false;
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(16, new Short((short) 0));  // blockID
        this.datawatcher.a(17, new Byte((byte) 0));    // blockData
        this.datawatcher.a(18, new Byte((byte) 0));    // face(angry)
    }

    @Override
    public void updateVisuals() {
        this.datawatcher.watch(16, (short) (getBlockID() & 0xFF));
        this.datawatcher.watch(17, (byte) (getBlockData() & 0xFF));
        this.datawatcher.watch(18, (byte) (getMyPet().isScreaming() ? 1 : 0));
    }

    protected void doMyPetTick() {
        super.doMyPetTick();
        Behavior skill = getMyPet().getSkills().getSkill(Behavior.class);
        if (skill != null) {
            BehaviorInfo.BehaviorState behavior = skill.getBehavior();
            if (behavior == BehaviorInfo.BehaviorState.Aggressive) {
                if (!getMyPet().isScreaming()) {
                    getMyPet().setScreaming(true);
                }
            } else {
                if (getMyPet().isScreaming()) {
                    getMyPet().setScreaming(false);
                }
            }
        }
    }

    public MyEnderman getMyPet() {
        return (MyEnderman) myPet;
    }
}
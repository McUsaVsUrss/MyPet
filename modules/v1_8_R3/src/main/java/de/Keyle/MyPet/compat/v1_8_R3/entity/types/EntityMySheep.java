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

package de.Keyle.MyPet.compat.v1_8_R3.entity.types;

import de.Keyle.MyPet.api.Configuration;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.entity.EntitySize;
import de.Keyle.MyPet.api.entity.types.MySheep;
import de.Keyle.MyPet.compat.v1_8_R3.entity.EntityMyPet;
import de.Keyle.MyPet.compat.v1_8_R3.entity.ai.movement.EatGrass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.DyeColor;

@EntitySize(width = 0.7F, height = 1.2349999f)
public class EntityMySheep extends EntityMyPet {
    public EntityMySheep(World world, ActiveMyPet myPet) {
        super(world, myPet);
    }

    @Override
    protected String getDeathSound() {
        return "mob.sheep.say";
    }

    @Override
    protected String getHurtSound() {
        return "mob.sheep.say";
    }

    protected String getLivingSound() {
        return "mob.sheep.say";
    }

    public boolean handlePlayerInteraction(EntityHuman entityhuman) {
        if (super.handlePlayerInteraction(entityhuman)) {
            return true;
        }

        ItemStack itemStack = entityhuman.inventory.getItemInHand();

        if (getOwner().equals(entityhuman) && itemStack != null && canUseItem()) {
            if (itemStack.getItem() == Items.DYE && itemStack.getData() <= 15 && itemStack.getData() != getMyPet().getColor().getDyeData() && !getMyPet().isSheared()) {
                getMyPet().setColor(DyeColor.getByDyeData((byte) itemStack.getData()));
                if (!entityhuman.abilities.canInstantlyBuild) {
                    if (--itemStack.count <= 0) {
                        entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                    }
                }
                return true;
            } else if (itemStack.getItem() == Items.SHEARS && Configuration.MyPet.Sheep.CAN_BE_SHEARED && !getMyPet().isSheared()) {
                getMyPet().setSheared(true);
                int woolDropCount = 1 + this.random.nextInt(3);

                for (int j = 0; j < woolDropCount; ++j) {
                    EntityItem entityitem = this.a(new ItemStack(Blocks.WOOL, 1, getMyPet().getColor().getDyeData()), 1.0F);

                    entityitem.motY += (double) this.random.nextFloat() * 0.05F;
                    entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                }
                makeSound("mob.sheep.shear", 1.0F, 1.0F);
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemStack.damage(1, entityhuman);
                }
                return true;
            } else if (Configuration.MyPet.Sheep.GROW_UP_ITEM.compare(itemStack) && getMyPet().isBaby() && getOwner().getPlayer().isSneaking()) {
                if (!entityhuman.abilities.canInstantlyBuild) {
                    if (--itemStack.count <= 0) {
                        entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                    }
                }
                getMyPet().setBaby(false);
                return true;
            }
        }
        return false;
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(12, new Byte((byte) 0));     // age
        this.datawatcher.a(16, new Byte((byte) 0)); // color/sheared
    }

    @Override
    public void updateVisuals() {
        if (getMyPet().isBaby()) {
            this.datawatcher.watch(12, Byte.valueOf(Byte.MIN_VALUE));
        } else {
            this.datawatcher.watch(12, new Byte((byte) 0));
        }

        byte b0 = this.datawatcher.getByte(16);
        if (getMyPet().isSheared()) {
            this.datawatcher.watch(16, (byte) (b0 | 16));
        } else {
            this.datawatcher.watch(16, (byte) (b0 & -17));
        }

        this.datawatcher.watch(16, getMyPet().getColor().getWoolData());
    }

    public void playStepSound() {
        makeSound("mob.sheep.step", 0.15F, 1.0F);
    }

    public MySheep getMyPet() {
        return (MySheep) myPet;
    }

    public void setPathfinder() {
        super.setPathfinder();
        petPathfinderSelector.addGoal("EatGrass", new EatGrass(this, 0.02));
    }
}
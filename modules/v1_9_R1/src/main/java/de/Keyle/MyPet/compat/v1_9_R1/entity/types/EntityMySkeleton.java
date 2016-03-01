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
import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.entity.ActiveMyPet.PetState;
import de.Keyle.MyPet.api.entity.EntitySize;
import de.Keyle.MyPet.api.entity.EquipmentSlot;
import de.Keyle.MyPet.api.entity.types.MySkeleton;
import de.Keyle.MyPet.compat.v1_9_R1.entity.EntityMyPet;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;

@EntitySize(width = 0.6F, height = 1.9F)
public class EntityMySkeleton extends EntityMyPet {
    private static final DataWatcherObject<Integer> typeWatcher = DataWatcher.a(EntityMySkeleton.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> watcher = DataWatcher.a(EntityMySkeleton.class, DataWatcherRegistry.h);

    public EntityMySkeleton(World world, ActiveMyPet myPet) {
        super(world, myPet);
    }

    protected String getDeathSound() {
        return "entity.skeleton.death";
    }

    protected String getHurtSound() {
        return "entity.skeleton.hurt";
    }

    protected String getLivingSound() {
        return "entity.skeleton.ambient";
    }

    public boolean handlePlayerInteraction(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemStack) {
        if (super.handlePlayerInteraction(entityhuman, enumhand, itemStack)) {
            return true;
        }

        if (getOwner().equals(entityhuman) && itemStack != null && canUseItem()) {
            if (itemStack.getItem() == Items.SHEARS && getOwner().getPlayer().isSneaking() && canEquip()) {
                boolean hadEquipment = false;
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack itemInSlot = CraftItemStack.asNMSCopy(getMyPet().getEquipment(slot));
                    if (itemInSlot != null) {
                        EntityItem entityitem = this.a(itemInSlot.cloneItemStack(), 1.0F);
                        entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                        entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                        entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                        getMyPet().setEquipment(slot, null);
                        hadEquipment = true;
                    }
                }
                if (hadEquipment) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemStack.damage(1, entityhuman);
                    }
                }
                return true;
            } else if (MyPetApi.getBukkitHelper().isEquipment(CraftItemStack.asBukkitCopy(itemStack)) && getOwner().getPlayer().isSneaking() && canEquip()) {
                EquipmentSlot slot = EquipmentSlot.getSlotById(d(itemStack).c());
                ItemStack itemInSlot = CraftItemStack.asNMSCopy(getMyPet().getEquipment(slot));
                if (itemInSlot != null && !entityhuman.abilities.canInstantlyBuild) {
                    EntityItem entityitem = this.a(itemInSlot.cloneItemStack(), 1.0F);
                    entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                    entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                }
                getMyPet().setEquipment(slot, CraftItemStack.asBukkitCopy(itemStack));
                if (!entityhuman.abilities.canInstantlyBuild) {
                    --itemStack.count;
                }
                if (itemStack.count <= 0) {
                    entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                }
                return true;
            }
        }
        return false;
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(typeWatcher, 0); // skeleton type
        this.datawatcher.register(watcher, false);
    }

    public void playPetStepSound() {
        makeSound("entity.skeleton.step", 0.15F, 1.0F);
    }

    @Override
    public void updateVisuals() {
        this.datawatcher.set(typeWatcher, getMyPet().isWither() ? 1 : 0);

        Bukkit.getScheduler().runTaskLater(MyPetApi.getPlugin(), new Runnable() {
            public void run() {
                if (getMyPet().getStatus() == PetState.Here) {
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        if (getMyPet().getEquipment(slot) != null) {
                            setPetEquipment(slot, CraftItemStack.asNMSCopy(getMyPet().getEquipment(slot)));
                        }
                    }
                }
            }
        }, 5L);
    }

    public MySkeleton getMyPet() {
        return (MySkeleton) myPet;
    }

    public void setPetEquipment(EquipmentSlot slot, ItemStack itemStack) {
        ((WorldServer) this.world).getTracker().a(this, new PacketPlayOutEntityEquipment(getId(), EnumItemSlot.values()[slot.get19Slot()], itemStack));
    }

    public ItemStack getEquipment(EnumItemSlot vanillaSlot) {
        if (Util.findClassInStackTrace(Thread.currentThread().getStackTrace(), "net.minecraft.server." + MyPetApi.getCompatUtil().getInternalVersion() + ".EntityTrackerEntry", 2)) {
            EquipmentSlot slot = EquipmentSlot.getSlotById(vanillaSlot.c());
            if (getMyPet().getEquipment(slot) != null) {
                return CraftItemStack.asNMSCopy(getMyPet().getEquipment(slot));
            }
        }
        return super.getEquipment(vanillaSlot);
    }

    public float getHeadHeight() {
        return this.datawatcher.get(typeWatcher) == 1 ? super.getHeadHeight() : 1.74F;
    }
}
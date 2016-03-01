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

package de.Keyle.MyPet.listeners;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Configuration;
import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.WorldGroup;
import de.Keyle.MyPet.api.entity.*;
import de.Keyle.MyPet.api.entity.ActiveMyPet.PetState;
import de.Keyle.MyPet.api.entity.ai.target.TargetPriority;
import de.Keyle.MyPet.api.entity.types.MyEnderman;
import de.Keyle.MyPet.api.entity.types.MyRabbit;
import de.Keyle.MyPet.api.event.ActiveSkillEvent;
import de.Keyle.MyPet.api.event.MyPetLeashEvent;
import de.Keyle.MyPet.api.player.DonateCheck;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.player.Permissions;
import de.Keyle.MyPet.api.repository.RepositoryCallback;
import de.Keyle.MyPet.api.skill.MyPetExperience;
import de.Keyle.MyPet.api.skill.experience.MonsterExperience;
import de.Keyle.MyPet.api.skill.skills.BehaviorInfo.BehaviorState;
import de.Keyle.MyPet.api.skill.skills.ranged.CraftMyPetProjectile;
import de.Keyle.MyPet.api.skill.skills.ranged.EntityMyPetProjectile;
import de.Keyle.MyPet.api.util.ConfigItem;
import de.Keyle.MyPet.api.util.hooks.EconomyHook;
import de.Keyle.MyPet.api.util.hooks.PluginHookManager;
import de.Keyle.MyPet.api.util.inventory.CustomInventory;
import de.Keyle.MyPet.api.util.locale.Translation;
import de.Keyle.MyPet.commands.CommandInfo;
import de.Keyle.MyPet.commands.CommandInfo.PetInfoDisplay;
import de.Keyle.MyPet.entity.InactiveMyPet;
import de.Keyle.MyPet.skill.skills.*;
import de.Keyle.MyPet.skill.skills.Wither;
import de.Keyle.MyPet.util.CaptureHelper;
import de.Keyle.MyPet.util.hooks.PvPChecker;
import de.keyle.fanciful.FancyMessage;
import de.keyle.fanciful.ItemTooltip;
import de.keyle.knbt.TagByte;
import de.keyle.knbt.TagCompound;
import de.keyle.knbt.TagInt;
import de.keyle.knbt.TagList;
import net.citizensnpcs.api.CitizensAPI;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

import static org.bukkit.Bukkit.getPluginManager;

public class EntityListener implements Listener {
    Random random = new Random();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onMyPetEntitySpawn(final CreatureSpawnEvent event) {
        if (event.getEntity() instanceof MyPetBukkitEntity) {
            event.setCancelled(false);
        }
        if (!Configuration.LevelSystem.Experience.FROM_MONSTER_SPAWNER_MOBS && event.getSpawnReason() == SpawnReason.SPAWNER) {
            event.getEntity().setMetadata("MonsterSpawner", new FixedMetadataValue(MyPetApi.getPlugin(), true));
        }
        if (!event.isCancelled() && event.getEntity() instanceof Zombie) {
            MyPetApi.getBukkitHelper().addZombieTargetGoal((Zombie) event.getEntity());
        }
    }

    @EventHandler
    public void onMyPetEntityPortal(EntityPortalEvent event) {
        if (event.getEntity() instanceof MyPetBukkitEntity) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMyPetEntityInteract(EntityInteractEvent event) {
        if (event.getEntity() instanceof MyPetBukkitEntity) {
            if (event.getBlock().getType() == Material.SOIL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMyPetEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof MyPetBukkitEntity) {
            MyPetBukkitEntity craftMyPet = (MyPetBukkitEntity) event.getEntity();
            ActiveMyPet myPet = craftMyPet.getMyPet();
            if (event.getDamager() instanceof Player || (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)) {
                Player damager;
                if (event.getDamager() instanceof Projectile) {
                    damager = (Player) ((Projectile) event.getDamager()).getShooter();
                } else {
                    damager = (Player) event.getDamager();
                }
                if (MyPetApi.getMyPetInfo().getLeashItem(myPet.getPetType()).compare(damager.getItemInHand())) {
                    boolean infoShown = false;
                    if (CommandInfo.canSee(PetInfoDisplay.Name.adminOnly, damager, myPet)) {
                        damager.sendMessage(ChatColor.AQUA + myPet.getPetName() + ChatColor.RESET + ":");
                        infoShown = true;
                    }
                    if (CommandInfo.canSee(PetInfoDisplay.Owner.adminOnly, damager, myPet) && myPet.getOwner().getPlayer() != damager) {
                        damager.sendMessage("   " + Translation.getString("Name.Owner", damager) + ": " + myPet.getOwner().getName());
                        infoShown = true;
                    }
                    if (CommandInfo.canSee(PetInfoDisplay.HP.adminOnly, damager, myPet)) {
                        String msg;
                        if (myPet.getHealth() > myPet.getMaxHealth() / 3 * 2) {
                            msg = "" + ChatColor.GREEN;
                        } else if (myPet.getHealth() > myPet.getMaxHealth() / 3) {
                            msg = "" + ChatColor.YELLOW;
                        } else {
                            msg = "" + ChatColor.RED;
                        }
                        msg += String.format("%1.2f", myPet.getHealth()) + ChatColor.WHITE + "/" + String.format("%1.2f", myPet.getMaxHealth());
                        damager.sendMessage("   " + Translation.getString("Name.HP", damager) + ": " + msg);
                        infoShown = true;
                    }
                    if (myPet.getStatus() == PetState.Dead && CommandInfo.canSee(PetInfoDisplay.RespawnTime.adminOnly, damager, myPet)) {
                        damager.sendMessage("   " + Translation.getString("Name.Respawntime", damager) + ": " + myPet.getRespawnTime());
                        infoShown = true;
                    }
                    if (!myPet.isPassiv() && CommandInfo.canSee(PetInfoDisplay.Damage.adminOnly, damager, myPet)) {
                        double damage = (myPet.getSkills().isSkillActive(Damage.class) ? myPet.getSkills().getSkill(Damage.class).getDamage() : 0);
                        damager.sendMessage("   " + Translation.getString("Name.Damage", damager) + ": " + String.format("%1.2f", damage));
                        infoShown = true;
                    }
                    if (myPet.getRangedDamage() > 0 && CommandInfo.canSee(PetInfoDisplay.RangedDamage.adminOnly, damager, myPet)) {
                        double damage = myPet.getRangedDamage();
                        damager.sendMessage("   " + Translation.getString("Name.RangedDamage", damager) + ": " + String.format("%1.2f", damage));
                        infoShown = true;
                    }
                    if (myPet.getSkills().hasSkill(Behavior.class) && CommandInfo.canSee(PetInfoDisplay.Behavior.adminOnly, damager, myPet)) {
                        Behavior behavior = myPet.getSkills().getSkill(Behavior.class);
                        damager.sendMessage("   Behavior: " + Translation.getString("Name." + behavior.getBehavior().name(), damager));
                        infoShown = true;
                    }
                    if (Configuration.HungerSystem.USE_HUNGER_SYSTEM && CommandInfo.canSee(PetInfoDisplay.Hunger.adminOnly, damager, myPet)) {
                        damager.sendMessage("   " + Translation.getString("Name.Hunger", damager) + ": " + Math.round(myPet.getHungerValue()));

                        FancyMessage m = new FancyMessage("   " + Translation.getString("Name.Food", damager) + ": ");
                        boolean comma = false;
                        for (ConfigItem material : MyPetApi.getMyPetInfo().getFood(myPet.getPetType())) {
                            ItemStack is = material.getItem();
                            if (is == null) {
                                continue;
                            }
                            if (comma) {
                                m.then(", ");
                            }
                            if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                                m.then(is.getItemMeta().getDisplayName());
                            } else {
                                m.then(WordUtils.capitalizeFully(MyPetApi.getBukkitHelper().getMaterialName(material.getItem().getTypeId()).replace("_", " ")));
                            }
                            m.color(ChatColor.GOLD);
                            ItemTooltip it = new ItemTooltip();
                            it.setMaterial(is.getType());
                            if (is.hasItemMeta()) {
                                if (is.getItemMeta().hasDisplayName()) {
                                    it.setTitle(is.getItemMeta().getDisplayName());
                                }
                                if (is.getItemMeta().hasLore()) {
                                    it.setLore(is.getItemMeta().getLore().toArray(new String[is.getItemMeta().getLore().size()]));
                                }
                            }
                            m.itemTooltip(it);
                            comma = true;
                        }
                        MyPetApi.getBukkitHelper().sendMessageRaw(damager, m.toJSONString());

                        infoShown = true;
                    }
                    if (CommandInfo.canSee(PetInfoDisplay.Skilltree.adminOnly, damager, myPet) && myPet.getSkilltree() != null) {
                        damager.sendMessage("   " + Translation.getString("Name.Skilltree", damager) + ": " + myPet.getSkilltree().getName());
                        infoShown = true;
                    }
                    if (CommandInfo.canSee(PetInfoDisplay.Level.adminOnly, damager, myPet)) {
                        int lvl = myPet.getExperience().getLevel();
                        damager.sendMessage("   " + Translation.getString("Name.Level", damager) + ": " + lvl);
                        infoShown = true;
                    }
                    int maxLevel = myPet.getSkilltree() != null ? myPet.getSkilltree().getMaxLevel() : Configuration.LevelSystem.Experience.LEVEL_CAP;
                    if (CommandInfo.canSee(PetInfoDisplay.Exp.adminOnly, damager, myPet) && myPet.getExperience().getLevel() < maxLevel) {
                        double exp = myPet.getExperience().getCurrentExp();
                        double reqEXP = myPet.getExperience().getRequiredExp();
                        damager.sendMessage("   " + Translation.getString("Name.Exp", damager) + ": " + String.format("%1.2f", exp) + "/" + String.format("%1.2f", reqEXP));
                        infoShown = true;
                    }
                    if (myPet.getOwner().getDonationRank() != DonateCheck.DonationRank.None) {
                        infoShown = true;
                        damager.sendMessage("   " + myPet.getOwner().getDonationRank().getDisplayText());
                    }

                    if (!infoShown) {
                        damager.sendMessage(Translation.getString("Message.No.NothingToSeeHere", myPet.getOwner().getLanguage()));
                    }

                    event.setCancelled(true);
                } else if (myPet.getOwner().equals(damager) && (!Configuration.Misc.OWNER_CAN_ATTACK_PET || !PvPChecker.canHurt(myPet.getOwner().getPlayer()))) {
                    event.setCancelled(true);
                } else if (!myPet.getOwner().equals(damager) && !PvPChecker.canHurt(damager, myPet.getOwner().getPlayer(), true)) {
                    event.setCancelled(true);
                }
            }
            if (event.getDamager() instanceof CraftMyPetProjectile) {
                EntityMyPetProjectile projectile = ((CraftMyPetProjectile) event.getDamager()).getMyPetProjectile();

                if (myPet == projectile.getShooter().getMyPet()) {
                    event.setCancelled(true);
                }
                if (!PvPChecker.canHurt(projectile.getShooter().getOwner().getPlayer(), myPet.getOwner().getPlayer(), true)) {
                    event.setCancelled(true);
                }
            }
            if (!event.isCancelled() && event.getDamager() instanceof LivingEntity) {
                LivingEntity damager = (LivingEntity) event.getDamager();
                if (damager instanceof Player) {
                    if (!PvPChecker.canHurt(myPet.getOwner().getPlayer(), (Player) damager, true)) {
                        return;
                    }
                }

                if (myPet.getSkills().isSkillActive(Thorns.class)) {
                    if (damager instanceof Creeper) {
                        return;
                    }
                    Thorns thornsSkill = myPet.getSkills().getSkill(Thorns.class);
                    if (thornsSkill.activate()) {
                        isSkillActive = true;
                        thornsSkill.reflectDamage(damager, event.getDamage());
                        isSkillActive = false;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onEntityDamageByPlayer(final EntityDamageByEntityEvent event) {
        if (!event.getEntity().isDead() && !(event.getEntity() instanceof MyPetBukkitEntity) && event.getDamager() instanceof Player) {
            if (MyPetApi.getMyPetInfo().isLeashableEntityType(event.getEntity().getType())) {
                Player damager = (Player) event.getDamager();

                if (!MyPetApi.getMyPetList().hasActiveMyPet(damager)) {
                    LivingEntity leashTarget = (LivingEntity) event.getEntity();

                    MyPetType petType = MyPetType.byEntityTypeName(leashTarget.getType().name());
                    ConfigItem leashItem = MyPetApi.getMyPetInfo().getLeashItem(petType);

                    if (!leashItem.compare(damager.getItemInHand()) || !Permissions.has(damager, "MyPet.user.leash." + petType.name())) {
                        return;
                    }
                    if (Permissions.has(damager, "MyPet.user.capturehelper") && MyPetApi.getPlayerList().isMyPetPlayer(damager) && MyPetApi.getPlayerList().getMyPetPlayer(damager).isCaptureHelperActive()) {
                        CaptureHelper.checkTamable(leashTarget, event.getDamage(), damager);
                    }
                    if (PluginHookManager.isPluginUsable("Citizens")) {
                        try {
                            if (CitizensAPI.getNPCRegistry().isNPC(leashTarget)) {
                                return;
                            }
                        } catch (Error | Exception ignored) {
                        }
                    }
                    if (!PvPChecker.canHurt(damager, leashTarget)) {
                        return;
                    }

                    boolean willBeLeashed = true;

                    flagLoop:
                    for (LeashFlag flag : MyPetApi.getMyPetInfo().getLeashFlags(petType)) {
                        switch (flag) {
                            case Adult:
                                if (leashTarget instanceof Ageable) {
                                    willBeLeashed = ((Ageable) leashTarget).isAdult();
                                } else if (leashTarget instanceof Zombie) {
                                    willBeLeashed = !((Zombie) leashTarget).isBaby();
                                }
                                break;
                            case Baby:
                                if (leashTarget instanceof Ageable) {
                                    willBeLeashed = !((Ageable) leashTarget).isAdult();
                                } else if (leashTarget instanceof Zombie) {
                                    willBeLeashed = ((Zombie) leashTarget).isBaby();
                                }
                                break;
                            case LowHp:
                                willBeLeashed = ((leashTarget.getHealth() - event.getDamage()) * 100) / leashTarget.getMaxHealth() <= 10;
                                break;
                            case UserCreated:
                                if (leashTarget instanceof IronGolem) {
                                    willBeLeashed = ((IronGolem) leashTarget).isPlayerCreated();
                                }
                                break;
                            case Wild:
                                if (leashTarget instanceof IronGolem) {
                                    willBeLeashed = !((IronGolem) leashTarget).isPlayerCreated();
                                } else if (leashTarget instanceof Tameable) {
                                    willBeLeashed = !((Tameable) leashTarget).isTamed();
                                } else if (leashTarget instanceof Horse) {
                                    willBeLeashed = !((Horse) leashTarget).isTamed();
                                }
                                break;
                            case Tamed:
                                if (leashTarget instanceof Tameable) {
                                    willBeLeashed = ((Tameable) leashTarget).isTamed() && ((Tameable) leashTarget).getOwner() == damager;
                                }
                                if (leashTarget instanceof Horse) {
                                    willBeLeashed = ((Horse) leashTarget).isTamed() && ((Horse) leashTarget).getOwner() == damager;
                                }
                                break;
                            case CanBreed:
                                if (leashTarget instanceof Ageable) {
                                    willBeLeashed = ((Ageable) leashTarget).canBreed();
                                }
                                break;
                            case Angry:
                                if (leashTarget instanceof Wolf) {
                                    willBeLeashed = ((Wolf) leashTarget).isAngry();
                                }
                                break;
                            case Impossible:
                                willBeLeashed = false;
                                break flagLoop;
                            case None:
                                willBeLeashed = true;
                                break flagLoop;
                        }
                        if (!willBeLeashed) {
                            break;
                        }
                    }

                    if (willBeLeashed) {
                        event.setCancelled(true);

                        MyPetPlayer owner;
                        if (MyPetApi.getPlayerList().isMyPetPlayer(damager)) {
                            owner = MyPetApi.getPlayerList().getMyPetPlayer(damager);
                        } else {
                            owner = MyPetApi.getPlayerList().registerMyPetPlayer(damager);
                        }


                        InactiveMyPet inactiveMyPet = new InactiveMyPet(owner);
                        inactiveMyPet.setPetType(petType);
                        inactiveMyPet.setPetName(Translation.getString("Name." + petType.name(), inactiveMyPet.getOwner().getLanguage()));

                        WorldGroup worldGroup = WorldGroup.getGroupByWorld(damager.getWorld().getName());
                        inactiveMyPet.setWorldGroup(worldGroup.getName());
                        inactiveMyPet.getOwner().setMyPetForWorldGroup(worldGroup.getName(), inactiveMyPet.getUUID());

                        /*
                        if(leashTarget.getCustomName() != null)
                        {
                            inactiveMyPet.setPetName(leashTarget.getCustomName());
                        }
                        */

                        TagCompound extendedInfo = new TagCompound();
                        if (leashTarget instanceof Ocelot) {
                            extendedInfo.getCompoundData().put("CatType", new TagInt(((Ocelot) leashTarget).getCatType().getId()));
                            extendedInfo.getCompoundData().put("Sitting", new TagByte(((Ocelot) leashTarget).isSitting()));
                        } else if (leashTarget instanceof Wolf) {
                            extendedInfo.getCompoundData().put("Sitting", new TagByte(((Wolf) leashTarget).isSitting()));
                            extendedInfo.getCompoundData().put("Tamed", new TagByte(((Wolf) leashTarget).isTamed()));
                            extendedInfo.getCompoundData().put("CollarColor", new TagByte(((Wolf) leashTarget).getCollarColor().getWoolData()));
                        } else if (leashTarget instanceof Sheep) {
                            extendedInfo.getCompoundData().put("Color", new TagInt(((Sheep) leashTarget).getColor().getDyeData()));
                            extendedInfo.getCompoundData().put("Sheared", new TagByte(((Sheep) leashTarget).isSheared()));
                        } else if (leashTarget instanceof Villager) {
                            extendedInfo.getCompoundData().put("Profession", new TagInt(((Villager) leashTarget).getProfession().getId()));

                            TagCompound villagerTag = MyPetApi.getBukkitHelper().entityToTag(leashTarget);
                            String[] allowedTags = {"Riches", "Career", "CareerLevel", "Willing", "Inventory", "Offers"};
                            Set<String> keys = new HashSet<>(villagerTag.getCompoundData().keySet());
                            for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
                                String key = iterator.next();
                                if (Arrays.binarySearch(allowedTags, key) > -1) {
                                    continue;
                                }
                                villagerTag.remove(key);
                            }
                            extendedInfo.getCompoundData().put("OriginalData", villagerTag);
                        } else if (leashTarget instanceof Pig) {
                            extendedInfo.getCompoundData().put("Saddle", new TagByte(((Pig) leashTarget).hasSaddle()));
                        } else if (leashTarget instanceof Slime) {
                            extendedInfo.getCompoundData().put("Size", new TagInt(((Slime) leashTarget).getSize()));
                        } else if (leashTarget instanceof Creeper) {
                            extendedInfo.getCompoundData().put("Powered", new TagByte(((Creeper) leashTarget).isPowered()));
                        } else if (leashTarget instanceof Horse) {
                            Horse horse = (Horse) leashTarget;

                            byte type = (byte) horse.getVariant().ordinal();
                            int style = horse.getStyle().ordinal();
                            int color = horse.getColor().ordinal();
                            int variant = color & 255 | style << 8;

                            if (horse.getInventory().getArmor() != null) {
                                TagCompound armor = MyPetApi.getBukkitHelper().itemStackToCompund(horse.getInventory().getArmor());
                                extendedInfo.getCompoundData().put("Armor", armor);
                            }
                            if (horse.getInventory().getSaddle() != null) {
                                TagCompound saddle = MyPetApi.getBukkitHelper().itemStackToCompund(horse.getInventory().getSaddle());
                                extendedInfo.getCompoundData().put("Saddle", saddle);
                            }

                            extendedInfo.getCompoundData().put("Type", new TagByte(type));
                            extendedInfo.getCompoundData().put("Variant", new TagInt(variant));
                            extendedInfo.getCompoundData().put("Chest", new TagByte(horse.isCarryingChest()));
                            extendedInfo.getCompoundData().put("Age", new TagInt(horse.getAge()));

                            if (horse.isCarryingChest()) {
                                ItemStack[] contents = horse.getInventory().getContents();
                                for (int i = 2; i < contents.length; i++) {
                                    ItemStack item = contents[i];
                                    if (item != null) {
                                        horse.getWorld().dropItem(horse.getLocation(), item);
                                    }
                                }
                            }
                        } else if (leashTarget instanceof Zombie) {
                            extendedInfo.getCompoundData().put("Baby", new TagByte(((Zombie) leashTarget).isBaby()));
                            extendedInfo.getCompoundData().put("Villager", new TagByte(((Zombie) leashTarget).isVillager()));
                        } else if (leashTarget instanceof Enderman) {
                            Enderman enderman = (Enderman) leashTarget;
                            if (enderman.getCarriedMaterial().getItemType() != Material.AIR) {
                                ItemStack block = enderman.getCarriedMaterial().toItemStack(1);
                                extendedInfo.getCompoundData().put("Block", MyPetApi.getBukkitHelper().itemStackToCompund(block));
                            }
                        } else if (leashTarget instanceof Skeleton) {
                            extendedInfo.getCompoundData().put("Wither", new TagByte(((Skeleton) leashTarget).getSkeletonType() == SkeletonType.WITHER));
                        } else if (leashTarget instanceof Guardian) {
                            extendedInfo.getCompoundData().put("Elder", new TagByte(((Guardian) leashTarget).isElder()));
                        } else if (leashTarget instanceof Rabbit) {
                            extendedInfo.getCompoundData().put("Variant", new TagByte(MyRabbit.RabbitType.getTypeByBukkitEnum(((Rabbit) leashTarget).getRabbitType()).getId()));
                        }
                        if (leashTarget instanceof Ageable) {
                            extendedInfo.getCompoundData().put("Baby", new TagByte(!((Ageable) leashTarget).isAdult()));
                        }
                        if (leashTarget.getWorld().getGameRuleValue("doMobLoot").equalsIgnoreCase("true") && Configuration.Misc.RETAIN_EQUIPMENT_ON_TAME && (leashTarget instanceof Zombie || leashTarget instanceof PigZombie || leashTarget instanceof Skeleton)) {
                            List<TagCompound> equipmentList = new ArrayList<>();
                            if (random.nextFloat() <= leashTarget.getEquipment().getChestplateDropChance()) {
                                ItemStack itemStack = leashTarget.getEquipment().getChestplate();
                                if (itemStack != null && itemStack.getType() != Material.AIR) {
                                    TagCompound item = MyPetApi.getBukkitHelper().itemStackToCompund(itemStack);
                                    item.getCompoundData().put("Slot", new TagInt(EquipmentSlot.Chestplate.getSlotId()));
                                    equipmentList.add(item);
                                }
                            }
                            if (random.nextFloat() <= leashTarget.getEquipment().getHelmetDropChance()) {
                                ItemStack itemStack = leashTarget.getEquipment().getHelmet();
                                if (itemStack != null && itemStack.getType() != Material.AIR) {
                                    TagCompound item = MyPetApi.getBukkitHelper().itemStackToCompund(itemStack);
                                    item.getCompoundData().put("Slot", new TagInt(EquipmentSlot.Helmet.getSlotId()));
                                    equipmentList.add(item);
                                }
                            }
                            if (random.nextFloat() <= leashTarget.getEquipment().getLeggingsDropChance()) {
                                ItemStack itemStack = leashTarget.getEquipment().getLeggings();
                                if (itemStack != null && itemStack.getType() != Material.AIR) {
                                    TagCompound item = MyPetApi.getBukkitHelper().itemStackToCompund(itemStack);
                                    item.getCompoundData().put("Slot", new TagInt(EquipmentSlot.Leggins.getSlotId()));
                                    equipmentList.add(item);
                                }
                            }
                            if (random.nextFloat() <= leashTarget.getEquipment().getBootsDropChance()) {
                                ItemStack itemStack = leashTarget.getEquipment().getBoots();
                                if (itemStack != null && itemStack.getType() != Material.AIR) {
                                    TagCompound item = MyPetApi.getBukkitHelper().itemStackToCompund(itemStack);
                                    item.getCompoundData().put("Slot", new TagInt(EquipmentSlot.Boots.getSlotId()));
                                    equipmentList.add(item);
                                }
                            }
                            extendedInfo.getCompoundData().put("Equipment", new TagList(equipmentList));
                        }
                        inactiveMyPet.setInfo(extendedInfo);

                        leashTarget.remove();

                        if (Configuration.Misc.CONSUME_LEASH_ITEM && damager.getGameMode() != GameMode.CREATIVE && damager.getItemInHand() != null) {
                            if (damager.getItemInHand().getAmount() > 1) {
                                damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
                            } else {
                                damager.setItemInHand(null);
                            }
                        }

                        final ActiveMyPet myPet = MyPetApi.getMyPetList().activateMyPet(inactiveMyPet);
                        if (myPet != null) {
                            MyPetApi.getPlugin().getRepository().addMyPet(inactiveMyPet, new RepositoryCallback<Boolean>() {
                                @Override
                                public void callback(Boolean value) {
                                    myPet.createEntity();

                                    getPluginManager().callEvent(new MyPetLeashEvent(myPet));

                                    myPet.getOwner().sendMessage(Translation.getString("Message.Leash.Add", myPet.getOwner().getLanguage()));

                                    if (myPet.getOwner().isCaptureHelperActive()) {
                                        myPet.getOwner().setCaptureHelperActive(false);
                                        myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Command.CaptureHelper.Mode", myPet.getOwner()), Translation.getString("Name.Disabled", myPet.getOwner())));
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMyPetEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof MyPetBukkitEntity) {
            MyPetBukkitEntity bukkitEntity = (MyPetBukkitEntity) event.getEntity();

            if (event.getCause() == DamageCause.SUFFOCATION) {
                final ActiveMyPet myPet = bukkitEntity.getMyPet();
                final MyPetPlayer myPetPlayer = myPet.getOwner();

                myPet.removePet(true);
                myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Spawn.Despawn", myPetPlayer.getLanguage()), myPet.getPetName()));

                MyPetApi.getPlugin().getServer().getScheduler().runTaskLater(MyPetApi.getPlugin(), new Runnable() {
                    public void run() {
                        if (myPetPlayer.hasMyPet()) {
                            ActiveMyPet runMyPet = myPetPlayer.getMyPet();
                            switch (runMyPet.createEntity()) {
                                case Canceled:
                                    runMyPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Spawn.Prevent", myPet.getOwner()), runMyPet.getPetName()));
                                    break;
                                case NoSpace:
                                    runMyPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Spawn.NoSpace", myPet.getOwner()), runMyPet.getPetName()));
                                    break;
                                case NotAllowed:
                                    runMyPet.getOwner().sendMessage(Translation.getString("Message.No.AllowedHere", myPet.getOwner()).replace("%petname%", myPet.getPetName()));
                                    break;
                                case Flying:
                                    runMyPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Spawn.Flying", myPet.getOwner()), myPet.getPetName()));
                                    break;
                                case Success:
                                    if (runMyPet != myPet) {
                                        runMyPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Command.Call.Success", myPet.getOwner().getLanguage()), runMyPet.getPetName()));
                                    }
                                    break;
                            }
                        }
                    }
                }, 10L);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityDamageMonitor(final EntityDamageByEntityEvent event) {
        if (!event.isCancelled() && Configuration.LevelSystem.Experience.DAMAGE_WEIGHTED_EXPERIENCE_DISTRIBUTION && event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player) && !(event.getEntity() instanceof MyPetBukkitEntity)) {
            LivingEntity damager = null;
            if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                if (projectile.getShooter() instanceof LivingEntity) {
                    damager = (LivingEntity) projectile.getShooter();
                }
            } else if (event.getDamager() instanceof LivingEntity) {
                damager = (LivingEntity) event.getDamager();
            }
            if (damager != null) {
                MyPetExperience.addDamageToEntity(damager, (LivingEntity) event.getEntity(), event.getDamage());
            }
        }
    }

    boolean isSkillActive = false;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityResult(final EntityDamageByEntityEvent event) {
        Entity damagedEntity = event.getEntity();
        // --  fix unwanted screaming of Endermen --
        if (damagedEntity instanceof MyPetBukkitEntity && ((MyPetBukkitEntity) damagedEntity).getPetType() == MyPetType.Enderman) {
            ((MyEnderman) ((MyPetBukkitEntity) damagedEntity).getMyPet()).setScreaming(true);
            ((MyEnderman) ((MyPetBukkitEntity) damagedEntity).getMyPet()).setScreaming(false);
        }

        if (damagedEntity instanceof LivingEntity) {
            Entity damager = event.getDamager();

            if (damager instanceof Projectile) {
                ProjectileSource source = ((Projectile) damager).getShooter();
                if (source instanceof Entity) {
                    damager = (Entity) source;
                }
            }

            if (damager instanceof Player) {
                Player player = (Player) damager;
                if (event.getDamage() == 0 || event.isCancelled()) {
                    return;
                } else if (damagedEntity instanceof MyPetBukkitEntity) {
                    if (MyPetApi.getMyPetInfo().getLeashItem(((MyPetBukkitEntity) damagedEntity).getPetType()).compare(player.getItemInHand())) {
                        return;
                    }
                }
                if (MyPetApi.getMyPetList().hasActiveMyPet(player)) {
                    ActiveMyPet myPet = MyPetApi.getMyPetList().getMyPet(player);
                    if (myPet.getStatus() == PetState.Here && damagedEntity != myPet.getEntity()) {
                        myPet.getEntity().setTarget((LivingEntity) damagedEntity, TargetPriority.OwnerHurts);
                    }
                }
            } else if (damager instanceof MyPetBukkitEntity) {
                ActiveMyPet myPet = ((MyPetBukkitEntity) damager).getMyPet();

                // fix influence of other plugins
                if (event.getDamager() instanceof Projectile) {
                    event.setDamage(myPet.getRangedDamage());
                } else {
                    event.setDamage(myPet.getDamage());
                }

                if (damagedEntity instanceof Player && event.isCancelled()) {
                    return;
                }

                if (!isSkillActive) {
                    //  --  Skills  --
                    boolean skillUsed = false;
                    if (myPet.getSkills().hasSkill(Poison.class)) {
                        Poison poisonSkill = myPet.getSkills().getSkill(Poison.class);
                        if (poisonSkill.activate()) {
                            ActiveSkillEvent skillEvent = new ActiveSkillEvent(myPet, poisonSkill);
                            Bukkit.getPluginManager().callEvent(skillEvent);
                            if (!skillEvent.isCancelled()) {
                                poisonSkill.poisonTarget((LivingEntity) damagedEntity);
                                skillUsed = true;
                            }
                        }
                    }
                    if (!skillUsed && myPet.getSkills().hasSkill(Wither.class)) {
                        Wither witherSkill = myPet.getSkills().getSkill(Wither.class);
                        if (witherSkill.activate()) {
                            ActiveSkillEvent skillEvent = new ActiveSkillEvent(myPet, witherSkill);
                            Bukkit.getPluginManager().callEvent(skillEvent);
                            if (!skillEvent.isCancelled()) {
                                witherSkill.witherTarget((LivingEntity) damagedEntity);
                                skillUsed = true;
                            }
                        }
                    }
                    if (!skillUsed && myPet.getSkills().hasSkill(Fire.class)) {
                        Fire fireSkill = myPet.getSkills().getSkill(Fire.class);
                        if (fireSkill.activate()) {
                            ActiveSkillEvent skillEvent = new ActiveSkillEvent(myPet, fireSkill);
                            Bukkit.getPluginManager().callEvent(skillEvent);
                            if (!skillEvent.isCancelled()) {
                                fireSkill.igniteTarget((LivingEntity) damagedEntity);
                                skillUsed = true;
                            }
                        }
                    }
                    if (!skillUsed && myPet.getSkills().hasSkill(Slow.class)) {
                        Slow slowSkill = myPet.getSkills().getSkill(Slow.class);
                        if (slowSkill.activate()) {
                            ActiveSkillEvent skillEvent = new ActiveSkillEvent(myPet, slowSkill);
                            Bukkit.getPluginManager().callEvent(skillEvent);
                            if (!skillEvent.isCancelled()) {
                                slowSkill.slowTarget((LivingEntity) damagedEntity);
                                skillUsed = true;
                            }
                        }
                    }
                    if (!skillUsed && myPet.getSkills().hasSkill(Knockback.class)) {
                        Knockback knockbackSkill = myPet.getSkills().getSkill(Knockback.class);
                        if (knockbackSkill.activate()) {
                            ActiveSkillEvent skillEvent = new ActiveSkillEvent(myPet, knockbackSkill);
                            Bukkit.getPluginManager().callEvent(skillEvent);
                            if (!skillEvent.isCancelled()) {
                                knockbackSkill.knockbackTarget((LivingEntity) damagedEntity);
                                skillUsed = true;
                            }
                        }
                    }
                    if (!skillUsed && myPet.getSkills().hasSkill(Lightning.class)) {
                        Lightning lightningSkill = myPet.getSkills().getSkill(Lightning.class);
                        if (lightningSkill.activate()) {
                            ActiveSkillEvent skillEvent = new ActiveSkillEvent(myPet, lightningSkill);
                            Bukkit.getPluginManager().callEvent(skillEvent);
                            if (!skillEvent.isCancelled()) {
                                isSkillActive = true;
                                lightningSkill.strikeLightning(damagedEntity.getLocation());
                                isSkillActive = false;
                            }
                        }
                    }
                    if (!skillUsed && myPet.getSkills().hasSkill(Stomp.class)) {
                        Stomp stompSkill = myPet.getSkills().getSkill(Stomp.class);
                        if (stompSkill.activate()) {
                            ActiveSkillEvent skillEvent = new ActiveSkillEvent(myPet, stompSkill);
                            Bukkit.getPluginManager().callEvent(skillEvent);
                            if (!skillEvent.isCancelled()) {
                                isSkillActive = true;
                                stompSkill.stomp(myPet.getLocation());
                                isSkillActive = false;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMyPetEntityDeath(final EntityDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();
        if (deadEntity instanceof MyPetBukkitEntity) {
            ActiveMyPet myPet = ((MyPetBukkitEntity) deadEntity).getMyPet();
            if (myPet == null || myPet.getHealth() > 0) // check health for death events where the pet isn't really dead (/killall)
            {
                return;
            }

            final MyPetPlayer owner = myPet.getOwner();

            if (Configuration.Misc.RELEASE_PETS_ON_DEATH && !owner.isMyPetAdmin()) {
                if (myPet.getSkills().isSkillActive(Inventory.class)) {
                    CustomInventory inv = myPet.getSkills().getSkill(Inventory.class).getInventory();
                    inv.dropContentAt(myPet.getLocation());
                }
                if (myPet instanceof MyPetEquipment) {
                    ((MyPetEquipment) myPet).dropEquipment();
                }


                myPet.removePet();
                owner.setMyPetForWorldGroup(WorldGroup.getGroupByWorld(owner.getPlayer().getWorld().getName()).getName(), null);

                myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Command.Release.Dead", owner), myPet.getPetName()));

                MyPetApi.getMyPetList().deactivateMyPet(owner, false);
                MyPetApi.getRepository().removeMyPet(myPet.getUUID(), new RepositoryCallback<Boolean>() {
                    @Override
                    public void callback(Boolean value) {
                    }
                });


                return;
            }

            myPet.setRespawnTime((Configuration.Respawn.TIME_FIXED + MyPetApi.getMyPetInfo().getCustomRespawnTimeFixed(myPet.getPetType())) + (myPet.getExperience().getLevel() * (Configuration.Respawn.TIME_FACTOR + MyPetApi.getMyPetInfo().getCustomRespawnTimeFactor(myPet.getPetType()))));
            myPet.setStatus(PetState.Dead);

            if (deadEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) deadEntity.getLastDamageCause();

                if (e.getDamager() instanceof Player) {
                    myPet.setRespawnTime((Configuration.Respawn.TIME_PLAYER_FIXED + MyPetApi.getMyPetInfo().getCustomRespawnTimeFixed(myPet.getPetType())) + (myPet.getExperience().getLevel() * (Configuration.Respawn.TIME_PLAYER_FACTOR + MyPetApi.getMyPetInfo().getCustomRespawnTimeFactor(myPet.getPetType()))));
                } else if (e.getDamager() instanceof MyPetBukkitEntity) {
                    ActiveMyPet killerMyPet = ((MyPetBukkitEntity) e.getDamager()).getMyPet();
                    if (myPet.getSkills().isSkillActive(Behavior.class) && killerMyPet.getSkills().isSkillActive(Behavior.class)) {
                        Behavior killerBehaviorSkill = killerMyPet.getSkills().getSkill(Behavior.class);
                        Behavior deadBehaviorSkill = myPet.getSkills().getSkill(Behavior.class);
                        if (deadBehaviorSkill.getBehavior() == BehaviorState.Duel && killerBehaviorSkill.getBehavior() == BehaviorState.Duel) {
                            MyPetMinecraftEntity myPetEntity = ((MyPetBukkitEntity) deadEntity).getHandle();

                            if (myPetEntity.getTarget().equals(e.getDamager())) {
                                myPet.setRespawnTime(10);
                            }
                        }
                    }
                }
            }
            event.setDroppedExp(0);

            if (Configuration.LevelSystem.Experience.LOSS_FIXED > 0 || Configuration.LevelSystem.Experience.LOSS_PERCENT > 0) {
                double lostExpirience = Configuration.LevelSystem.Experience.LOSS_FIXED;
                lostExpirience += myPet.getExperience().getRequiredExp() * Configuration.LevelSystem.Experience.LOSS_PERCENT / 100;
                if (lostExpirience > myPet.getExperience().getCurrentExp()) {
                    lostExpirience = myPet.getExperience().getCurrentExp();
                }
                if (myPet.getSkilltree() != null) {
                    int requiredLevel = myPet.getSkilltree().getRequiredLevel();
                    if (requiredLevel > 1) {
                        double minExp = myPet.getExperience().getExpByLevel(requiredLevel);
                        lostExpirience = myPet.getExp() - lostExpirience < minExp ? myPet.getExp() - minExp : lostExpirience;
                    }
                }
                if (Configuration.LevelSystem.Experience.DROP_LOST_EXP) {
                    event.setDroppedExp((int) (lostExpirience + 0.5));
                }
                myPet.getExperience().removeCurrentExp(lostExpirience);
            }
            if (myPet.getSkills().isSkillActive(Inventory.class)) {
                Inventory inventorySkill = myPet.getSkills().getSkill(Inventory.class);
                inventorySkill.closeInventory();
                if (inventorySkill.dropOnDeath() && !owner.isMyPetAdmin()) {
                    inventorySkill.getInventory().dropContentAt(myPet.getLocation());
                }
            }
            sendDeathMessage(event);
            myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Spawn.Respawn.In", owner.getPlayer()), myPet.getPetName(), myPet.getRespawnTime()));

            if (EconomyHook.canUseEconomy() && owner.hasAutoRespawnEnabled() && myPet.getRespawnTime() >= owner.getAutoRespawnMin() && Permissions.has(owner.getPlayer(), "MyPet.user.respawn")) {
                double costs = myPet.getRespawnTime() * Configuration.Respawn.COSTS_FACTOR + Configuration.Respawn.COSTS_FIXED;
                if (EconomyHook.canPay(owner, costs)) {
                    EconomyHook.pay(owner, costs);
                    myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Command.Respawn.Paid", owner.getPlayer()), myPet.getPetName(), costs + " " + EconomyHook.getEconomy().currencyNameSingular()));
                    myPet.setRespawnTime(1);
                } else {
                    myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Command.Respawn.NoMoney", owner.getPlayer()), myPet.getPetName(), costs + " " + EconomyHook.getEconomy().currencyNameSingular()));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();
        if (deadEntity instanceof MyPetBukkitEntity) {
            return;
        }
        if (Configuration.Hooks.SkillAPI.DISABLE_VANILLA_EXP) {
            return;
        }
        if (!Configuration.LevelSystem.Experience.FROM_MONSTER_SPAWNER_MOBS && event.getEntity().hasMetadata("MonsterSpawner")) {
            for (MetadataValue value : event.getEntity().getMetadata("MonsterSpawner")) {
                if (value.getOwningPlugin().getName().equals(MyPetApi.getPlugin().getName())) {
                    if (value.asBoolean()) {
                        return;
                    }
                    break;
                }
            }
        }
        if (Configuration.LevelSystem.Experience.DAMAGE_WEIGHTED_EXPERIENCE_DISTRIBUTION) {
            Map<Entity, Double> damagePercentMap = MyPetExperience.getDamageToEntityPercent(deadEntity);
            for (Entity entity : damagePercentMap.keySet()) {
                if (entity instanceof MyPetBukkitEntity) {
                    ActiveMyPet myPet = ((MyPetBukkitEntity) entity).getMyPet();
                    if (Configuration.Skilltree.PREVENT_LEVELLING_WITHOUT_SKILLTREE && myPet.getSkilltree() == null) {
                        if (!myPet.autoAssignSkilltree()) {
                            continue;
                        }
                    }
                    if (myPet.getSkilltree() == null || myPet.getSkilltree().getMaxLevel() <= 1 || myPet.getExperience().getLevel() < myPet.getSkilltree().getMaxLevel()) {
                        double randomExp = MonsterExperience.getMonsterExperience(deadEntity.getType()).getRandomExp();
                        myPet.getExperience().addExp(damagePercentMap.get(entity) * randomExp);
                    }
                } else if (entity instanceof Player) {
                    Player owner = (Player) entity;
                    if (MyPetApi.getMyPetList().hasActiveMyPet(owner)) {
                        ActiveMyPet myPet = MyPetApi.getMyPetList().getMyPet(owner);
                        if (Configuration.Skilltree.PREVENT_LEVELLING_WITHOUT_SKILLTREE && myPet.getSkilltree() == null) {
                            if (!myPet.autoAssignSkilltree()) {
                                continue;
                            }
                        }
                        if (myPet.isPassiv() || Configuration.LevelSystem.Experience.ALWAYS_GRANT_PASSIVE_XP) {
                            if (myPet.getStatus() == PetState.Here) {
                                if (myPet.getSkilltree() == null || myPet.getSkilltree().getMaxLevel() <= 1 || myPet.getExperience().getLevel() < myPet.getSkilltree().getMaxLevel()) {
                                    myPet.getExperience().addExp(deadEntity.getType(), Configuration.LevelSystem.Experience.PASSIVE_PERCENT_PER_MONSTER);
                                }
                            }
                        }
                    }
                }
            }
        } else if (deadEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) deadEntity.getLastDamageCause();

            Entity damager = edbee.getDamager();
            if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Entity) {
                damager = (Entity) ((Projectile) damager).getShooter();
            }
            if (damager instanceof MyPetBukkitEntity) {
                ActiveMyPet myPet = ((MyPetBukkitEntity) damager).getMyPet();
                if (myPet.getSkilltree() == null && Configuration.Skilltree.PREVENT_LEVELLING_WITHOUT_SKILLTREE) {
                    if (!myPet.autoAssignSkilltree()) {
                        return;
                    }
                }
                myPet.getExperience().addExp(edbee.getEntity().getType());
            } else if (damager instanceof Player) {
                Player owner = (Player) damager;
                if (MyPetApi.getMyPetList().hasActiveMyPet(owner)) {
                    ActiveMyPet myPet = MyPetApi.getMyPetList().getMyPet(owner);
                    if (Configuration.Skilltree.PREVENT_LEVELLING_WITHOUT_SKILLTREE && myPet.getSkilltree() == null) {
                        if (!myPet.autoAssignSkilltree()) {
                            return;
                        }
                    }
                    if (myPet.isPassiv() || Configuration.LevelSystem.Experience.ALWAYS_GRANT_PASSIVE_XP) {
                        if (myPet.getStatus() == PetState.Here) {
                            if (myPet.getSkilltree() == null || myPet.getSkilltree().getMaxLevel() <= 1 || myPet.getExperience().getLevel() < myPet.getSkilltree().getMaxLevel()) {
                                myPet.getExperience().addExp(deadEntity.getType(), Configuration.LevelSystem.Experience.PASSIVE_PERCENT_PER_MONSTER);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(final EntityTargetEvent event) {
        if (event.getEntity() instanceof MyPetBukkitEntity) {
            ActiveMyPet myPet = ((MyPetBukkitEntity) event.getEntity()).getMyPet();
            if (myPet.getSkills().isSkillActive(Behavior.class)) {
                Behavior behaviorSkill = myPet.getSkills().getSkill(Behavior.class);
                if (behaviorSkill.getBehavior() == Behavior.BehaviorState.Friendly) {
                    event.setCancelled(true);
                } else if (event.getTarget() instanceof Player && event.getTarget().getName().equals(myPet.getOwner().getName())) {
                    event.setCancelled(true);
                } else if (behaviorSkill.getBehavior() == Behavior.BehaviorState.Raid) {
                    if (event.getTarget() instanceof Player) {
                        event.setCancelled(true);
                    } else if (event.getTarget() instanceof Tameable && ((Tameable) event.getTarget()).isTamed()) {
                        event.setCancelled(true);
                    } else if (event.getTarget() instanceof MyPetBukkitEntity) {
                        event.setCancelled(true);
                    }
                }
            }
        } else if (event.getEntity() instanceof Tameable) {
            if (event.getTarget() instanceof MyPetBukkitEntity) {
                Tameable tameable = ((Tameable) event.getEntity());
                ActiveMyPet myPet = ((MyPetBukkitEntity) event.getTarget()).getMyPet();
                if (myPet.getOwner().equals(tameable.getOwner())) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getEntity() instanceof IronGolem) {
            if (event.getTarget() instanceof MyPetBukkitEntity) {
                if (event.getReason() == TargetReason.RANDOM_TARGET) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private void sendDeathMessage(final EntityDeathEvent event) {
        if (event.getEntity() instanceof MyPetBukkitEntity) {
            ActiveMyPet myPet = ((MyPetBukkitEntity) event.getEntity()).getMyPet();
            String killer;
            if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

                if (e.getDamager().getType() == EntityType.PLAYER) {
                    if (e.getDamager() == myPet.getOwner().getPlayer()) {
                        killer = Translation.getString("Name.You", myPet.getOwner().getLanguage());
                    } else {
                        killer = e.getDamager().getName();
                    }
                } else if (e.getDamager().getType() == EntityType.WOLF) {
                    Wolf w = (Wolf) e.getDamager();
                    killer = Translation.getString("Name.Wolf", myPet.getOwner().getLanguage());
                    if (w.isTamed()) {
                        killer += " (" + w.getOwner().getName() + ')';
                    }
                } else if (e.getDamager() instanceof MyPetBukkitEntity) {
                    MyPetBukkitEntity craftMyPet = (MyPetBukkitEntity) e.getDamager();
                    killer = craftMyPet.getMyPet().getPetName() + " (" + craftMyPet.getOwner().getName() + ')';
                } else if (e.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) e.getDamager();
                    killer = Translation.getString("Name." + Util.capitalizeName(projectile.getType().name()), myPet.getOwner().getLanguage()) + " (";
                    if (projectile.getShooter() instanceof Player) {
                        if (projectile.getShooter() == myPet.getOwner().getPlayer()) {
                            killer += Translation.getString("Name.You", myPet.getOwner().getLanguage());
                        } else {
                            killer += ((Player) projectile.getShooter()).getName();
                        }
                    } else {
                        if (MyPetApi.getMyPetInfo().isLeashableEntityType(e.getDamager().getType())) {
                            killer += Translation.getString("Name." + Util.capitalizeName(MyPetType.byEntityTypeName(e.getDamager().getType().name()).name()), myPet.getOwner().getLanguage());
                        } else if (e.getDamager().getType().getName() != null) {
                            killer += Translation.getString("Name." + Util.capitalizeName(e.getDamager().getType().getName()), myPet.getOwner().getLanguage());
                        } else {
                            killer += Translation.getString("Name.Unknow", myPet.getOwner().getLanguage());
                        }
                    }
                    killer += ")";
                } else {
                    if (MyPetApi.getMyPetInfo().isLeashableEntityType(e.getDamager().getType())) {
                        killer = Translation.getString("Name." + Util.capitalizeName(MyPetType.byEntityTypeName(e.getDamager().getType().name()).name()), myPet.getOwner().getLanguage());
                    } else {
                        if (e.getDamager().getType().getName() != null) {
                            killer = Translation.getString("Name." + Util.capitalizeName(e.getDamager().getType().getName()), myPet.getOwner().getLanguage());
                        } else {
                            killer = Translation.getString("Name.Unknow", myPet.getOwner().getLanguage());
                        }
                    }
                }
            } else {
                if (event.getEntity().getLastDamageCause() != null) {
                    killer = Translation.getString("Name." + Util.capitalizeName(event.getEntity().getLastDamageCause().getCause().name()), myPet.getOwner().getLanguage());
                } else {
                    killer = Translation.getString("Name.Unknow", myPet.getOwner().getLanguage());
                }
            }
            myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.DeathMessage", myPet.getOwner().getLanguage()), myPet.getPetName(), killer));
        }
    }
}
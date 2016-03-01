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

package de.Keyle.MyPet.util;

import com.google.common.collect.Lists;
import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Configuration;
import de.Keyle.MyPet.api.Configuration.*;
import de.Keyle.MyPet.api.entity.DefaultInfo;
import de.Keyle.MyPet.api.entity.LeashFlag;
import de.Keyle.MyPet.api.entity.MyPetType;
import de.Keyle.MyPet.api.skill.experience.MonsterExperience;
import de.Keyle.MyPet.api.util.Colorizer;
import de.Keyle.MyPet.api.util.ConfigItem;
import de.Keyle.MyPet.api.util.NameFilter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationLoader {


    public static void setDefault() {
        FileConfiguration config = MyPetApi.getPlugin().getConfig();

        config.addDefault("MyPet.Leash.Consume", Misc.CONSUME_LEASH_ITEM);
        config.addDefault("MyPet.Leash.ShowAlwaysForOwner", Misc.ALWAYS_SHOW_LEASH_FOR_OWNER);
        config.addDefault("MyPet.OwnerCanAttackPet", Misc.OWNER_CAN_ATTACK_PET);
        config.addDefault("MyPet.DisablePetVersusPlayer", Misc.DISABLE_PET_VS_PLAYER);
        config.addDefault("MyPet.RemovePetsAfterRelease", Misc.REMOVE_PETS_AFTER_RELEASE);
        config.addDefault("MyPet.FollowStartDistance", Misc.MYPET_FOLLOW_START_DISTANCE);
        config.addDefault("MyPet.ReleasePetsOnDeath", Misc.RELEASE_PETS_ON_DEATH);
        config.addDefault("MyPet.RetainEquipmentOnTame", Misc.RETAIN_EQUIPMENT_ON_TAME);
        config.addDefault("MyPet.Make-Pet-Invisible-When-Owner-Is-Invisible", Misc.INVISIBLE_LIKE_OWNER);
        config.addDefault("MyPet.Log.INFO", Log.INFO);
        config.addDefault("MyPet.Log.ERROR", Log.ERROR);
        config.addDefault("MyPet.Log.WARNING", Log.WARNING);
        config.addDefault("MyPet.Update-Check", true);

        config.addDefault("MyPet.Repository.NBT.AutoSaveTime", Repository.NBT.AUTOSAVE_TIME);
        config.addDefault("MyPet.Repository.NBT.Pet.SaveOnAdd", Repository.NBT.SAVE_ON_PET_ADD);
        config.addDefault("MyPet.Repository.NBT.Pet.SaveOnUpdate", Repository.NBT.SAVE_ON_PET_UPDATE);
        config.addDefault("MyPet.Repository.NBT.Pet.SaveOnRemove", Repository.NBT.SAVE_ON_PET_REMOVE);
        config.addDefault("MyPet.Repository.NBT.Player.SaveOnAdd", Repository.NBT.SAVE_ON_PLAYER_ADD);
        config.addDefault("MyPet.Repository.NBT.Player.SaveOnUpdate", Repository.NBT.SAVE_ON_PLAYER_UPDATE);
        config.addDefault("MyPet.Repository.NBT.Player.SaveOnRemove", Repository.NBT.SAVE_ON_PLAYER_REMOVE);
        config.addDefault("MyPet.Repository.NBT.Backup.Active", Repository.NBT.MAKE_BACKUPS);
        config.addDefault("MyPet.Repository.NBT.Backup.SaveInterval", Repository.NBT.SAVE_INTERVAL);
        config.addDefault("MyPet.Repository.NBT.Backup.DateFormat", Repository.NBT.DATE_FORMAT);

        config.addDefault("MyPet.Respawn.Time.Default.Factor", Respawn.TIME_FACTOR);
        config.addDefault("MyPet.Respawn.Time.Player.Factor", Respawn.TIME_PLAYER_FACTOR);
        config.addDefault("MyPet.Respawn.Time.Default.Fixed", Respawn.TIME_FIXED);
        config.addDefault("MyPet.Respawn.Time.Player.Fixed", Respawn.TIME_PLAYER_FIXED);
        config.addDefault("MyPet.Respawn.EconomyCost.Fixed", Respawn.COSTS_FIXED);
        config.addDefault("MyPet.Respawn.EconomyCost.Factor", Respawn.COSTS_FACTOR);

        config.addDefault("MyPet.Permissions.Enabled", Permissions.ENABLED);
        config.addDefault("MyPet.Permissions.UseExtendedPermissions", Permissions.EXTENDED);

        config.addDefault("MyPet.LevelSystem.CalculationMode", LevelSystem.CALCULATION_MODE);
        config.addDefault("MyPet.LevelSystem.Firework.Enabled", LevelSystem.FIREWORK);
        config.addDefault("MyPet.LevelSystem.Firework.Color", "#00FF00");

        config.addDefault("MyPet.HungerSystem.Active", HungerSystem.USE_HUNGER_SYSTEM);
        config.addDefault("MyPet.HungerSystem.Time", HungerSystem.HUNGER_SYSTEM_TIME);
        config.addDefault("MyPet.HungerSystem.HungerPointsPerFeed", HungerSystem.HUNGER_SYSTEM_POINTS_PER_FEED);

        config.addDefault("MyPet.Skilltree.AutomaticAssignment", Skilltree.AUTOMATIC_SKILLTREE_ASSIGNMENT);
        config.addDefault("MyPet.Skilltree.RandomAssignment", Skilltree.RANDOM_SKILLTREE_ASSIGNMENT);
        config.addDefault("MyPet.Skilltree.InheritAlreadyInheritedSkills", Skilltree.INHERIT_ALREADY_INHERITED_SKILLS);
        config.addDefault("MyPet.Skilltree.ChooseOnce", Skilltree.CHOOSE_SKILLTREE_ONLY_ONCE);
        config.addDefault("MyPet.Skilltree.PreventLevellingWithout", Skilltree.PREVENT_LEVELLING_WITHOUT_SKILLTREE);
        config.addDefault("MyPet.Skilltree.SwitchPenaltyFixed", Skilltree.SWITCH_PENALTY_FIXED);
        config.addDefault("MyPet.Skilltree.SwitchPenaltyPercent", Skilltree.SWITCH_PENALTY_PERCENT);
        config.addDefault("MyPet.Skilltree.SwitchPenaltyAdmin", Skilltree.SWITCH_PENALTY_ADMIN);

        config.addDefault("MyPet.Hooks.BungeeCord.UUID-Mode", Hooks.BUNGEE_MODE);
        config.addDefault("MyPet.Hooks.Towny", Hooks.USE_Towny);
        config.addDefault("MyPet.Hooks.Heroes", Hooks.USE_Heroes);
        config.addDefault("MyPet.Hooks.Factions", Hooks.USE_Factions);
        config.addDefault("MyPet.Hooks.WorldGuard", Hooks.USE_WorldGuard);
        config.addDefault("MyPet.Hooks.Citizens", Hooks.USE_Citizens);
        config.addDefault("MyPet.Hooks.mcMMO", Hooks.USE_McMMO);
        config.addDefault("MyPet.Hooks.Regios", Hooks.USE_Regios);
        config.addDefault("MyPet.Hooks.MobArena.PvP", Hooks.USE_MobArena);
        config.addDefault("MyPet.Hooks.MobArena.DisablePetsInArena", Hooks.DISABLE_PETS_IN_MOB_ARENA);
        config.addDefault("MyPet.Hooks.Residence", Hooks.USE_Residence);
        config.addDefault("MyPet.Hooks.AncientRPG", Hooks.USE_AncientRPG);
        config.addDefault("MyPet.Hooks.GriefPrevention", Hooks.USE_GriefPrevention);
        config.addDefault("MyPet.Hooks.PvPManager", Hooks.USE_PvPManager);
        config.addDefault("MyPet.Hooks.Minigames.DisablePetsInGames", Hooks.DISABLE_PETS_IN_MINIGAMES);
        config.addDefault("MyPet.Hooks.PvPArena.DisablePetsInArena", Hooks.DISABLE_PETS_IN_ARENA);
        config.addDefault("MyPet.Hooks.PvPArena.PvP", Hooks.USE_PvPArena);
        config.addDefault("MyPet.Hooks.SurvivalGames.PvP", Hooks.USE_SurvivalGame);
        config.addDefault("MyPet.Hooks.SurvivalGames.DisablePetsInGames", Hooks.DISABLE_PETS_IN_SURVIVAL_GAMES);
        config.addDefault("MyPet.Hooks.MyHungerGames.DisablePetsInGames", Hooks.DISABLE_PETS_IN_HUNGER_GAMES);
        config.addDefault("MyPet.Hooks.BattleArena.DisablePetsInArena", Hooks.DISABLE_PETS_IN_ARENA);
        config.addDefault("MyPet.Hooks.Vault.Economy", Hooks.USE_ECONOMY);
        config.addDefault("MyPet.Hooks.SkillAPI.GrantExp", Hooks.SkillAPI.GRANT_EXP);
        config.addDefault("MyPet.Hooks.SkillAPI.Disable-Vanilla-Exp", Hooks.SkillAPI.DISABLE_VANILLA_EXP);

        config.addDefault("MyPet.Name.Filter", Lists.newArrayList("whore", "fuck"));
        config.addDefault("MyPet.Name.MaxLength", Name.MAX_LENGTH);
        config.addDefault("MyPet.Name.OverHead.Visible", Name.OVERHEAD_NAME);
        config.addDefault("MyPet.Name.OverHead.Prefix", Name.OVERHEAD_PREFIX);
        config.addDefault("MyPet.Name.OverHead.Suffix", Name.OVERHEAD_SUFFIX);

        config.addDefault("MyPet.Exp.DamageWeightedExperienceDistribution", LevelSystem.Experience.DAMAGE_WEIGHTED_EXPERIENCE_DISTRIBUTION);
        config.addDefault("MyPet.Exp.Passive.Always-Grant-Passive-XP", LevelSystem.Experience.ALWAYS_GRANT_PASSIVE_XP);
        config.addDefault("MyPet.Exp.Passive.PercentPerMonster", LevelSystem.Experience.PASSIVE_PERCENT_PER_MONSTER);
        config.addDefault("MyPet.Exp.Loss.Percent", LevelSystem.Experience.LOSS_PERCENT);
        config.addDefault("MyPet.Exp.Loss.Fixed", LevelSystem.Experience.LOSS_FIXED);
        config.addDefault("MyPet.Exp.Loss.Drop", LevelSystem.Experience.DROP_LOST_EXP);
        config.addDefault("MyPet.Exp.Gain.MonsterSpawner", LevelSystem.Experience.FROM_MONSTER_SPAWNER_MOBS);
        config.addDefault("MyPet.Exp.LevelCap", LevelSystem.Experience.LEVEL_CAP);

        config.addDefault("MyPet.Skill.Control.Item", Material.LEASH.getId());
        config.addDefault("MyPet.Skill.Ride.Item", Material.LEASH.getId());
        config.addDefault("MyPet.Skill.Ride.HungerPerMeter", Skilltree.Skill.Ride.HUNGER_PER_METER);
        config.addDefault("MyPet.Skill.Inventory.Creative", Skilltree.Skill.Inventory.OPEN_IN_CREATIVE);
        config.addDefault("MyPet.Skill.Inventory.DropWhenOwnerDies", Skilltree.Skill.Inventory.DROP_WHEN_OWNER_DIES);
        config.addDefault("MyPet.Skill.Beacon.HungerDecreaseTime", Skilltree.Skill.Beacon.HUNGER_DECREASE_TIME);
        config.addDefault("MyPet.Skill.Beacon.Party-Support", Skilltree.Skill.Beacon.PARTY_SUPPORT);

        config.addDefault("MyPet.Info.Wiki-URL", Misc.WIKI_URL);

        for (EntityType entityType : EntityType.values()) {
            if(MonsterExperience.mobExp.containsKey(entityType.name())) {
                config.addDefault("MyPet.Exp.Active." + entityType.name() + ".Min", MonsterExperience.getMonsterExperience(entityType).getMin());
                config.addDefault("MyPet.Exp.Active." + entityType.name() + ".Max", MonsterExperience.getMonsterExperience(entityType).getMax());
            }
        }

        config.options().copyDefaults(true);
        MyPetApi.getPlugin().saveConfig();

        File petConfigFile = new File(MyPetApi.getPlugin().getDataFolder().getPath() + File.separator + "pet-config.yml");
        config = new YamlConfiguration();

        if (petConfigFile.exists()) {
            try {
                config.load(petConfigFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

        for (MyPetType petType : MyPetType.values()) {
            DefaultInfo pi = petType.getMyPetClass().getAnnotation(DefaultInfo.class);
            if (pi == null) {
                continue;
            }

            config.addDefault("MyPet.Pets." + petType.name() + ".HP", pi.hp());
            config.addDefault("MyPet.Pets." + petType.name() + ".Speed", pi.walkSpeed());
            config.addDefault("MyPet.Pets." + petType.name() + ".Food", linkFood(pi.food()));
            config.addDefault("MyPet.Pets." + petType.name() + ".LeashFlags", linkLeashFlags(pi.leashFlags()));
            config.addDefault("MyPet.Pets." + petType.name() + ".CustomRespawnTimeFactor", 0);
            config.addDefault("MyPet.Pets." + petType.name() + ".CustomRespawnTimeFixed", 0);
            config.addDefault("MyPet.Pets." + petType.name() + ".LeashItem", Material.LEASH.getId());
        }

        config.addDefault("MyPet.Pets.Chicken.CanLayEggs", Configuration.MyPet.Chicken.CAN_LAY_EGGS);
        config.addDefault("MyPet.Pets.Cow.CanGiveMilk", Configuration.MyPet.Cow.CAN_GIVE_MILK);
        config.addDefault("MyPet.Pets.Sheep.CanBeSheared", Configuration.MyPet.Sheep.CAN_BE_SHEARED);
        config.addDefault("MyPet.Pets.Sheep.CanRegrowWool", Configuration.MyPet.Sheep.CAN_REGROW_WOOL);
        config.addDefault("MyPet.Pets.IronGolem.CanThrowUp", Configuration.MyPet.IronGolem.CAN_THROW_UP);
        config.addDefault("MyPet.Pets.Snowman.FixSnowTrack", Configuration.MyPet.Snowman.FIX_SNOW_TRACK);
        config.addDefault("MyPet.Pets.Chicken.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.Cow.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.Horse.GrowUpItem", Material.BREAD.getId());
        config.addDefault("MyPet.Pets.Mooshroom.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.Mooshroom.CanGiveStew", Configuration.MyPet.Mooshroom.CAN_GIVE_SOUP);
        config.addDefault("MyPet.Pets.Ocelot.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.Pig.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.Sheep.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.Villager.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.Wolf.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.Zombie.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.PigZombie.GrowUpItem", Material.POTION.getId());
        config.addDefault("MyPet.Pets.Rabbit.GrowUpItem", Material.POTION.getId());

        config.options().copyDefaults(true);
        try {
            config.save(petConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfiguration() {
        FileConfiguration config = MyPetApi.getPlugin().getConfig();

        Misc.CONSUME_LEASH_ITEM = config.getBoolean("MyPet.Leash.Consume", false);
        Misc.ALWAYS_SHOW_LEASH_FOR_OWNER = config.getBoolean("MyPet.Leash.ShowAlwaysForOwner", false);

        Skilltree.Skill.CONTROL_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Skill.Control.Item", "" + Material.LEASH.getId()));
        Skilltree.Skill.Ride.RIDE_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Skill.Ride.Item", "" + Material.LEASH.getId()));
        Skilltree.Skill.Ride.HUNGER_PER_METER = config.getDouble("MyPet.Skill.Ride.HungerPerMeter", 0.01);
        Skilltree.Skill.Beacon.HUNGER_DECREASE_TIME = config.getInt("MyPet.Skill.Beacon.HungerDecreaseTime", 100);
        Skilltree.Skill.Beacon.PARTY_SUPPORT = config.getBoolean("MyPet.Skill.Beacon.Party-Support", true);
        Skilltree.Skill.Inventory.OPEN_IN_CREATIVE = config.getBoolean("MyPet.Skill.Inventory.Creative", true);
        Skilltree.Skill.Inventory.DROP_WHEN_OWNER_DIES = config.getBoolean("MyPet.Skill.Inventory.DropWhenOwnerDies", false);

        Skilltree.SWITCH_PENALTY_FIXED = config.getDouble("MyPet.Skilltree.SwitchPenaltyFixed", 0.0);
        Skilltree.SWITCH_PENALTY_PERCENT = config.getInt("MyPet.Skilltree.SwitchPenaltyPercent", 5);
        Skilltree.SWITCH_PENALTY_ADMIN = config.getBoolean("MyPet.Skilltree.SwitchPenaltyAdmin", false);
        Skilltree.INHERIT_ALREADY_INHERITED_SKILLS = config.getBoolean("MyPet.Skilltree.InheritAlreadyInheritedSkills", false);
        Respawn.TIME_FACTOR = config.getInt("MyPet.Respawn.Time.Default.Factor", 5);
        Respawn.TIME_PLAYER_FACTOR = config.getInt("MyPet.Respawn.Time.Player.Factor", 5);
        Respawn.TIME_FIXED = config.getInt("MyPet.Respawn.Time.Default.Fixed", 0);
        Respawn.TIME_PLAYER_FIXED = config.getInt("MyPet.Respawn.Time.Player.Fixed", 0);
        Respawn.COSTS_FACTOR = config.getDouble("MyPet.Respawn.EconomyCost.Factor", 1.0);
        Respawn.COSTS_FIXED = config.getDouble("MyPet.Respawn.EconomyCost.Fixed", 0.0);
        Skilltree.AUTOMATIC_SKILLTREE_ASSIGNMENT = config.getBoolean("MyPet.Skilltree.AutomaticAssignment", false);
        Skilltree.RANDOM_SKILLTREE_ASSIGNMENT = config.getBoolean("MyPet.Skilltree.RandomAssignment", false);
        Skilltree.CHOOSE_SKILLTREE_ONLY_ONCE = config.getBoolean("MyPet.Skilltree.ChooseOnce", true);
        Skilltree.PREVENT_LEVELLING_WITHOUT_SKILLTREE = config.getBoolean("MyPet.Skilltree.PreventLevellingWithout", true);
        Misc.OWNER_CAN_ATTACK_PET = config.getBoolean("MyPet.OwnerCanAttackPet", false);
        Misc.DISABLE_PET_VS_PLAYER = config.getBoolean("MyPet.DisablePetVersusPlayer", false);
        HungerSystem.USE_HUNGER_SYSTEM = config.getBoolean("MyPet.HungerSystem.Active", true);
        HungerSystem.HUNGER_SYSTEM_TIME = config.getInt("MyPet.HungerSystem.Time", 60);
        HungerSystem.HUNGER_SYSTEM_POINTS_PER_FEED = config.getInt("MyPet.HungerSystem.HungerPointsPerFeed", 6);
        Misc.RELEASE_PETS_ON_DEATH = config.getBoolean("MyPet.ReleasePetsOnDeath", false);
        Misc.REMOVE_PETS_AFTER_RELEASE = config.getBoolean("MyPet.RemovePetsAfterRelease", false);
        Misc.RETAIN_EQUIPMENT_ON_TAME = config.getBoolean("MyPet.RetainEquipmentOnTame", true);
        Misc.INVISIBLE_LIKE_OWNER = config.getBoolean("MyPet.Make-Pet-Invisible-When-Owner-Is-Invisible", true);
        Misc.MYPET_FOLLOW_START_DISTANCE = config.getDouble("MyPet.FollowStartDistance", 7.0D);
        LevelSystem.FIREWORK_COLOR = Integer.decode(config.getString("MyPet.LevelSystem.Firework.Color", "#00FF00"));
        LevelSystem.CALCULATION_MODE = config.getString("MyPet.LevelSystem.CalculationMode", "Default");
        LevelSystem.FIREWORK = config.getBoolean("MyPet.LevelSystem.Firework", true);

        Log.INFO = config.getBoolean("MyPet.Log.INFO", Log.INFO);
        Log.ERROR = config.getBoolean("MyPet.Log.ERROR", Log.ERROR);
        Log.WARNING = config.getBoolean("MyPet.Log.WARNING", Log.WARNING);

        NameFilter.NAME_FILTER = new ArrayList<>();
        for (Object o : config.getList("MyPet.Name.Filter", Lists.newArrayList("whore", "fuck"))) {
            NameFilter.NAME_FILTER.add(o.toString());
        }
        Name.MAX_LENGTH = config.getInt("MyPet.Name.MaxLength", 64);
        Name.OVERHEAD_NAME = config.getBoolean("MyPet.Name.OverHead.Visible", true);
        Name.OVERHEAD_PREFIX = Colorizer.setColors(config.getString("MyPet.Name.OverHead.Prefix", "<aqua>"));
        Name.OVERHEAD_SUFFIX = Colorizer.setColors(config.getString("MyPet.Name.OverHead.Suffix", ""));

        Repository.REPOSITORY_TYPE = config.getString("MyPet.Repository.Type", Repository.REPOSITORY_TYPE);
        Repository.NBT.AUTOSAVE_TIME = config.getInt("MyPet.Repository.NBT.AutoSaveTime", Repository.NBT.AUTOSAVE_TIME);
        Repository.NBT.SAVE_ON_PET_UPDATE = config.getBoolean("MyPet.Repository.NBT.Pet.SaveOnUpdate", Repository.NBT.SAVE_ON_PET_UPDATE);
        Repository.NBT.SAVE_ON_PET_REMOVE = config.getBoolean("MyPet.Repository.NBT.Pet.SaveOnRemove", Repository.NBT.SAVE_ON_PET_REMOVE);
        Repository.NBT.SAVE_ON_PET_ADD = config.getBoolean("MyPet.Repository.NBT.Pet.SaveOnAdd", Repository.NBT.SAVE_ON_PET_ADD);
        Repository.NBT.SAVE_ON_PLAYER_ADD = config.getBoolean("MyPet.Repository.NBT.Player.SaveOnAdd", Repository.NBT.SAVE_ON_PLAYER_ADD);
        Repository.NBT.SAVE_ON_PLAYER_UPDATE = config.getBoolean("MyPet.Repository.NBT.Player.SaveOnUpdate", Repository.NBT.SAVE_ON_PLAYER_UPDATE);
        Repository.NBT.SAVE_ON_PLAYER_REMOVE = config.getBoolean("MyPet.Repository.NBT.Player.SaveOnRemove", Repository.NBT.SAVE_ON_PLAYER_REMOVE);
        Repository.NBT.MAKE_BACKUPS = config.getBoolean("MyPet.Repository.NBT.Backup.Active", Repository.NBT.MAKE_BACKUPS);
        Repository.NBT.SAVE_INTERVAL = config.getInt("MyPet.Repository.NBT.Backup.SaveInterval", Repository.NBT.SAVE_INTERVAL);
        Repository.NBT.DATE_FORMAT = config.getString("MyPet.Repository.NBT.Backup.DateFormat", Repository.NBT.DATE_FORMAT);

        Misc.WIKI_URL = config.getString("MyPet.Info.Wiki-URL", Misc.WIKI_URL);

        Permissions.EXTENDED = config.getBoolean("MyPet.Permissions.UseExtendedPermissions", false);
        Permissions.ENABLED = config.getBoolean("MyPet.Permissions.Enabled", true);

        Hooks.BUNGEE_MODE = config.getString("MyPet.Hooks.BungeeCord.UUID-Mode", "online");
        Hooks.USE_ECONOMY = config.getBoolean("MyPet.Hooks.Vault.Economy", true);
        Hooks.DISABLE_PETS_IN_MINIGAMES = config.getBoolean("MyPet.Hooks.Minigames.DisablePetsInGames", true);
        Hooks.DISABLE_PETS_IN_ARENA = config.getBoolean("MyPet.Hooks.PvPArena.DisablePetsInArena", true);
        Hooks.DISABLE_PETS_IN_SURVIVAL_GAMES = config.getBoolean("MyPet.Hooks.SurvivalGames.DisablePetsInGames", true);
        Hooks.DISABLE_PETS_IN_HUNGER_GAMES = config.getBoolean("MyPet.Hooks.MyHungerGames.DisablePetsInGames", true);
        Hooks.DISABLE_PETS_IN_MOB_ARENA = config.getBoolean("MyPet.Hooks.MobArena.DisablePetsInArena", false);
        Hooks.SkillAPI.GRANT_EXP = config.getBoolean("MyPet.Hooks.SkillAPI.GrantExp", true);
        Hooks.SkillAPI.DISABLE_VANILLA_EXP = config.getBoolean("MyPet.Hooks.SkillAPI.Disable-Vanilla-Exp", false);
        Hooks.SkillAPI.EXP_PERCENT = config.getInt("MyPet.Hooks.SkillAPI.ExpPercent", 100);
        Hooks.USE_PvPArena = config.getBoolean("MyPet.Hooks.PvPArena.PvP", true);
        Hooks.USE_Towny = config.getBoolean("MyPet.Hooks.Towny", true);
        Hooks.USE_Factions = config.getBoolean("MyPet.Hooks.Factions", true);
        Hooks.USE_WorldGuard = config.getBoolean("MyPet.Hooks.WorldGuard", true);
        Hooks.USE_Citizens = config.getBoolean("MyPet.Hooks.Citizens", true);
        Hooks.USE_Heroes = config.getBoolean("MyPet.Hooks.Heroes", true);
        Hooks.USE_McMMO = config.getBoolean("MyPet.Hooks.mcMMO", true);
        Hooks.USE_MobArena = config.getBoolean("MyPet.Hooks.MobArena.PvP", true);
        Hooks.USE_SurvivalGame = config.getBoolean("MyPet.Hooks.SurvivalGames.PvP", true);
        Hooks.USE_Regios = config.getBoolean("MyPet.Hooks.Regios", true);
        Hooks.USE_Residence = config.getBoolean("MyPet.Hooks.Residence", true);
        Hooks.USE_AncientRPG = config.getBoolean("MyPet.Hooks.AncientRPG", true);
        Hooks.USE_GriefPrevention = config.getBoolean("MyPet.Hooks.GriefPrevention", true);
        Hooks.USE_PvPManager = config.getBoolean("MyPet.Hooks.PvPManager", true);

        LevelSystem.Experience.LEVEL_CAP = config.getInt("MyPet.Exp.LevelCap", LevelSystem.Experience.LEVEL_CAP);
        LevelSystem.Experience.LOSS_PERCENT = config.getInt("MyPet.Exp.Loss.Percent", 0);
        LevelSystem.Experience.LOSS_FIXED = config.getDouble("MyPet.Exp.Loss.Fixed", 0.0);
        LevelSystem.Experience.DROP_LOST_EXP = config.getBoolean("MyPet.Exp.Loss.Drop", true);
        LevelSystem.Experience.PASSIVE_PERCENT_PER_MONSTER = config.getInt("MyPet.Exp.Passive.PercentPerMonster", 25);
        LevelSystem.Experience.ALWAYS_GRANT_PASSIVE_XP = config.getBoolean("MyPet.Exp.Passive.Always-Grant-Passive-XP", true);
        LevelSystem.Experience.FROM_MONSTER_SPAWNER_MOBS = config.getBoolean("MyPet.Exp.Gain.MonsterSpawner", true);
        LevelSystem.Experience.DAMAGE_WEIGHTED_EXPERIENCE_DISTRIBUTION = config.getBoolean("MyPet.Exp.DamageWeightedExperienceDistribution", true);

        if (config.getStringList("MyPet.exp.active") != null) {
            double min;
            double max;
            for (EntityType entityType : EntityType.values()) {
                if(MonsterExperience.mobExp.containsKey(entityType.name())) {
                    max = config.getDouble("MyPet.Exp.Active." + entityType.name() + ".Max", 0.);
                    min = config.getDouble("MyPet.Exp.Active." + entityType.name() + ".Min", 0.);
                    if (min == max) {
                        MonsterExperience.getMonsterExperience(entityType).setExp(max);
                    } else {
                        MonsterExperience.getMonsterExperience(entityType).setMin(min);
                        MonsterExperience.getMonsterExperience(entityType).setMax(max);
                    }
                }
            }
        }

        File petConfigFile = new File(MyPetApi.getPlugin().getDataFolder().getPath(), "pet-config.yml");
        if (petConfigFile.exists()) {
            YamlConfiguration ymlcnf = new YamlConfiguration();
            try {
                ymlcnf.load(petConfigFile);
                config = ymlcnf;
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        Configuration.MyPet.Chicken.CAN_LAY_EGGS = config.getBoolean("MyPet.Pets.Chicken.CanLayEggs", true);
        Configuration.MyPet.Cow.CAN_GIVE_MILK = config.getBoolean("MyPet.Pets.Cow.CanGiveMilk", true);
        Configuration.MyPet.Sheep.CAN_BE_SHEARED = config.getBoolean("MyPet.Pets.Sheep.CanBeSheared", true);
        Configuration.MyPet.Sheep.CAN_REGROW_WOOL = config.getBoolean("MyPet.Pets.Sheep.CanRegrowWool", true);
        Configuration.MyPet.IronGolem.CAN_THROW_UP = config.getBoolean("MyPet.Pets.IronGolem.CanThrowUp", true);
        Configuration.MyPet.Snowman.FIX_SNOW_TRACK = config.getBoolean("MyPet.Pets.Snowman.FixSnowTrack", true);
        Configuration.MyPet.Chicken.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Chicken.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.Cow.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Cow.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.Horse.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Horse.GrowUpItem", "" + Material.BREAD.getId()));
        Configuration.MyPet.Mooshroom.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Mooshroom.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.Mooshroom.CAN_GIVE_SOUP = config.getBoolean("MyPet.Pets.Mooshroom.CanGiveStew", false);
        Configuration.MyPet.Ocelot.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Ocelot.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.Pig.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Pig.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.Sheep.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Sheep.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.Villager.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Villager.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.Wolf.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Wolf.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.Zombie.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Zombie.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.PigZombie.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.PigZombie.GrowUpItem", "" + Material.POTION.getId()));
        Configuration.MyPet.Rabbit.GROW_UP_ITEM = ConfigItem.createConfigItem(config.getString("MyPet.Pets.Rabbit.GrowUpItem", "" + Material.POTION.getId()));

        for (MyPetType petType : MyPetType.values()) {
            DefaultInfo pi = petType.getMyPetClass().getAnnotation(DefaultInfo.class);

            MyPetApi.getMyPetInfo().setStartHP(petType, config.getDouble("MyPet.Pets." + petType.name() + ".HP", pi.hp()));
            MyPetApi.getMyPetInfo().setSpeed(petType, config.getDouble("MyPet.Pets." + petType.name() + ".Speed", pi.walkSpeed()));
            if (config.get("MyPet.Pets." + petType.name() + ".Food") instanceof ArrayList) {
                List<String> foodList = config.getStringList("MyPet.Pets." + petType.name() + ".Food");
                for (String foodString : foodList) {
                    ConfigItem ci = ConfigItem.createConfigItem(foodString);
                    if (ci.getItem().getType() != Material.AIR) {
                        MyPetApi.getMyPetInfo().setFood(petType, ci);
                    }
                }
            } else {
                seperateFood(petType, config.getString("MyPet.Pets." + petType.name() + ".Food", "0"));
            }
            seperateLeashFlags(petType, config.getString("MyPet.Pets." + petType + ".LeashFlags", linkLeashFlags(pi.leashFlags())));
            MyPetApi.getMyPetInfo().setCustomRespawnTimeFactor(petType, config.getInt("MyPet.Pets." + petType.name() + ".CustomRespawnTimeFactor", 0));
            MyPetApi.getMyPetInfo().setCustomRespawnTimeFixed(petType, config.getInt("MyPet.Pets." + petType.name() + ".CustomRespawnTimeFixed", 0));
            MyPetApi.getMyPetInfo().setLeashItem(petType, ConfigItem.createConfigItem(config.getString("MyPet.Pets." + petType.name() + ".LeashItem", "" + Material.LEASH.getId())));
        }
    }

    public static List<Integer> linkFood(Material[] foodTypes) {
        List<Integer> foodList = new ArrayList<>();
        for (Material foodType : foodTypes) {
            foodList.add(foodType.getId());
        }
        return foodList;
    }

    public static void seperateFood(MyPetType type, String foodString) {
        foodString = foodString.trim();
        while (true) {
            if (foodString.endsWith("\\;")) {
                foodString = foodString.substring(0, foodString.length() - 2);
                continue;
            }
            if (foodString.endsWith(";")) {
                foodString = foodString.substring(0, foodString.length() - 1);
                continue;
            }
            break;
        }
        if (foodString.contains(";")) {
            for (String foodIDString : foodString.split("(?<!\\\\);")) {
                MyPetApi.getMyPetInfo().setFood(type, ConfigItem.createConfigItem(foodIDString.replace("\\;", ";")));
            }
        } else {
            MyPetApi.getMyPetInfo().setFood(type, ConfigItem.createConfigItem(foodString));
        }
    }

    public static String linkLeashFlags(LeashFlag[] leashFlags) {
        String linkedLeashFlags = "";
        for (LeashFlag leashFlag : leashFlags) {
            if (!linkedLeashFlags.equalsIgnoreCase("")) {
                linkedLeashFlags += ",";
            }
            linkedLeashFlags += leashFlag.name();
        }
        return linkedLeashFlags;
    }

    public static void seperateLeashFlags(MyPetType type, String leashFlagString) {
        leashFlagString = leashFlagString.replaceAll("\\s", "");
        if (leashFlagString.contains(",")) {
            for (String leashFlagSplit : leashFlagString.split(",")) {
                if (LeashFlag.getLeashFlagByName(leashFlagSplit) != null) {
                    MyPetApi.getMyPetInfo().setLeashFlags(type, LeashFlag.getLeashFlagByName(leashFlagSplit));
                } else {
                    MyPetApi.getLogger().info(ChatColor.RED + leashFlagString + " is not a valid LeashFlag!");
                }
            }
        } else {
            if (LeashFlag.getLeashFlagByName(leashFlagString) != null) {
                MyPetApi.getMyPetInfo().setLeashFlags(type, LeashFlag.getLeashFlagByName(leashFlagString));
            } else {
                MyPetApi.getLogger().info(ChatColor.RED + leashFlagString + " is not a valid LeashFlag!");
            }
        }
    }
}

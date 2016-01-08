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

package de.Keyle.MyPet.repository.types;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.Keyle.MyPet.MyPetPlugin;
import de.Keyle.MyPet.api.repository.Repository;
import de.Keyle.MyPet.api.util.IScheduler;
import de.Keyle.MyPet.entity.types.InactiveMyPet;
import de.Keyle.MyPet.entity.types.MyPet;
import de.Keyle.MyPet.entity.types.MyPetType;
import de.Keyle.MyPet.repository.MyPetList;
import de.Keyle.MyPet.repository.PlayerList;
import de.Keyle.MyPet.repository.RepositoryCallback;
import de.Keyle.MyPet.util.Backup;
import de.Keyle.MyPet.util.BukkitUtil;
import de.Keyle.MyPet.util.MyPetVersion;
import de.Keyle.MyPet.util.configuration.ConfigurationNBT;
import de.Keyle.MyPet.util.logger.DebugLogger;
import de.Keyle.MyPet.util.logger.MyPetLogger;
import de.Keyle.MyPet.util.player.MyPetPlayer;
import de.Keyle.MyPet.util.player.UUIDFetcher;
import de.keyle.knbt.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NbtRepository implements Repository, IScheduler {
    public static final ArrayListMultimap<MyPetPlayer, InactiveMyPet> myPets = ArrayListMultimap.create();
    protected final static Map<UUID, MyPetPlayer> players = Maps.newHashMap();

    private File NBTPetFile;
    private int autoSaveTimer = 0;
    private Backup backupManager;

    public static boolean SAVE_ON_PET_ADD = true;
    public static boolean SAVE_ON_PET_REMOVE = true;
    public static boolean SAVE_ON_PET_UPDATE = true;
    public static boolean SAVE_ON_PLAYER_ADD = true;
    public static boolean SAVE_ON_PLAYER_REMOVE = true;
    public static boolean SAVE_ON_PLAYER_UPDATE = true;
    public static int AUTOSAVE_TIME = 60;

    @Override
    public void disable() {
        saveData(false);
        myPets.clear();
    }

    @Override
    public void save() {
        saveData(true);
    }

    @Override
    public void init() {
        NBTPetFile = new File(MyPetPlugin.getPlugin().getDataFolder().getPath() + File.separator + "My.Pets");

        if (Backup.MAKE_BACKUPS) {
            new File(MyPetPlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "backups" + File.separator).mkdirs();
            backupManager = new Backup(NBTPetFile, new File(MyPetPlugin.getPlugin().getDataFolder().getPath() + File.separator + "backups" + File.separator));
        }


        loadData(NBTPetFile);
    }

    @Override
    public void countMyPets(final RepositoryCallback<Integer> callback) {
        callback.setValue(myPets.values().size());
        callback.run();
    }

    @Override
    public void countMyPets(MyPetType type, final RepositoryCallback<Integer> callback) {
        int counter = 0;
        for (InactiveMyPet inactiveMyPet : myPets.values()) {
            if (inactiveMyPet.getPetType() == type) {
                counter++;
            }
        }
        callback.setValue(counter);
        callback.run();
    }

    public void saveData(boolean async) {
        autoSaveTimer = AUTOSAVE_TIME;
        final ConfigurationNBT nbtConfiguration = new ConfigurationNBT(NBTPetFile);

        nbtConfiguration.getNBTCompound().getCompoundData().put("Version", new TagString(MyPetVersion.getVersion()));
        nbtConfiguration.getNBTCompound().getCompoundData().put("Build", new TagInt(Integer.parseInt(MyPetVersion.getBuild())));
        nbtConfiguration.getNBTCompound().getCompoundData().put("OnlineMode", new TagByte(BukkitUtil.isInOnlineMode()));
        nbtConfiguration.getNBTCompound().getCompoundData().put("Pets", savePets());
        nbtConfiguration.getNBTCompound().getCompoundData().put("Players", savePlayers());
        nbtConfiguration.getNBTCompound().getCompoundData().put("PluginStorage", MyPetPlugin.getPlugin().getPluginStorage().save());
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(MyPetPlugin.getPlugin(), new Runnable() {
                public void run() {
                    nbtConfiguration.save();
                }
            });
        } else {
            nbtConfiguration.save();
        }
    }

    private void loadData(File f) {
        ConfigurationNBT nbtConfiguration = new ConfigurationNBT(f);
        if (!nbtConfiguration.load()) {
            return;
        }

        if (nbtConfiguration.getNBTCompound().containsKeyAs("CleanShutdown", TagByte.class)) {
            DebugLogger.info("Clean shutdown: " + nbtConfiguration.getNBTCompound().getAs("CleanShutdown", TagByte.class).getBooleanData());
        }

        DebugLogger.info("Loading plugin storage ------------------" + nbtConfiguration.getNBTCompound().containsKeyAs("PluginStorage", TagCompound.class));
        if (nbtConfiguration.getNBTCompound().containsKeyAs("PluginStorage", TagCompound.class)) {
            TagCompound storageTag = nbtConfiguration.getNBTCompound().getAs("PluginStorage", TagCompound.class);
            for (String plugin : storageTag.getCompoundData().keySet()) {
                DebugLogger.info("  " + plugin);
            }
            DebugLogger.info(" Storage for " + storageTag.getCompoundData().keySet().size() + " MyPet-plugin(s) loaded");

        }
        DebugLogger.info("-----------------------------------------");

        DebugLogger.info("Loading players -------------------------");
        if (nbtConfiguration.getNBTCompound().containsKeyAs("Players", TagList.class)) {
            DebugLogger.info(loadPlayers(nbtConfiguration.getNBTCompound().getAs("Players", TagList.class)) + " PetPlayer(s) loaded");
        }
        DebugLogger.info("-----------------------------------------");

        DebugLogger.info("Loading Pets: -----------------------------");
        int petCount = loadPets(nbtConfiguration.getNBTCompound().getAs("Pets", TagList.class));
        MyPetLogger.write("" + ChatColor.YELLOW + petCount + ChatColor.RESET + " pet(s) loaded");
        DebugLogger.info("-----------------------------------------");
    }

    public Backup getBackupManager() {
        return backupManager;
    }

    @Override
    public void schedule() {
        if (AUTOSAVE_TIME > 0 && autoSaveTimer-- <= 0) {
            saveData(true);
            autoSaveTimer = AUTOSAVE_TIME;
        }
    }

    // Pets ------------------------------------------------------------------------------------------------------------

    @Override
    public List<InactiveMyPet> getAllMyPets(Map<UUID, MyPetPlayer> owners) {
        return new ArrayList<>(myPets.values());
    }

    @Override
    public void hasMyPets(final MyPetPlayer myPetPlayer, final RepositoryCallback<Boolean> callback) {
        if (callback != null) {
            callback.setValue(myPets.containsKey(myPetPlayer));
            callback.run();
        }
    }

    @Override
    public void getMyPets(final MyPetPlayer owner, final RepositoryCallback<List<InactiveMyPet>> callback) {
        if (callback != null) {
            callback.setValue(myPets.get(owner));
            callback.run();
        }
    }

    @Override
    public void getMyPet(final UUID uuid, final RepositoryCallback<InactiveMyPet> callback) {
        if (callback != null) {
            for (InactiveMyPet pet : myPets.values()) {
                if (uuid.equals(pet.getUUID())) {
                    callback.setValue(pet);
                    callback.run();
                    return;
                }
            }
        }
    }

    @Override
    public void removeMyPet(final UUID uuid, final RepositoryCallback<Boolean> callback) {
        for (InactiveMyPet pet : myPets.values()) {
            if (uuid.equals(pet.getUUID())) {
                myPets.remove(pet.getOwner(), pet);
                if (SAVE_ON_PET_REMOVE) {
                    saveData(true);
                }
                if (callback != null) {
                    callback.setValue(true);
                    callback.run();
                }
                return;
            }
        }
    }

    @Override
    public void removeMyPet(final InactiveMyPet inactiveMyPet, final RepositoryCallback<Boolean> callback) {
        boolean result = myPets.remove(inactiveMyPet.getOwner(), inactiveMyPet);
        if (SAVE_ON_PET_REMOVE) {
            saveData(true);
        }
        if (callback != null) {
            callback.setValue(result);
            callback.run();
        }
    }

    @Override
    public void addMyPet(final InactiveMyPet inactiveMyPet, final RepositoryCallback<Boolean> callback) {
        if (!myPets.containsEntry(inactiveMyPet.getOwner(), inactiveMyPet)) {
            myPets.put(inactiveMyPet.getOwner(), inactiveMyPet);
            if (SAVE_ON_PET_ADD) {
                saveData(true);
            }
            if (callback != null) {
                callback.setValue(true);
                callback.run();
            }
            return;
        }
        if (callback != null) {
            callback.setValue(false);
            callback.run();
        }
    }

    @Override
    public void updateMyPet(final MyPet myPet, final RepositoryCallback<Boolean> callback) {
        List<InactiveMyPet> pets = myPets.get(myPet.getOwner());
        for (InactiveMyPet pet : pets) {
            if (myPet.getUUID().equals(pet.getUUID())) {
                myPets.put(myPet.getOwner(), MyPetList.getInactiveMyPetFromMyPet(myPet));
                myPets.remove(myPet.getOwner(), pet);

                if (SAVE_ON_PET_UPDATE) {
                    saveData(true);
                }

                if (callback != null) {
                    callback.setValue(true);
                    callback.run();
                }
                return;
            }
        }
        if (callback != null) {
            callback.setValue(false);
            callback.run();
        }
    }

    private int loadPets(TagList petList) {
        int petCount = 0;
        boolean oldPets = false;
        for (int i = 0; i < petList.getReadOnlyList().size(); i++) {
            TagCompound myPetNBT = petList.getTagAs(i, TagCompound.class);
            MyPetPlayer petPlayer;
            if (myPetNBT.containsKeyAs("Internal-Owner-UUID", TagString.class)) {
                UUID ownerUUID = UUID.fromString(myPetNBT.getAs("Internal-Owner-UUID", TagString.class).getStringData());
                petPlayer = players.get(ownerUUID);
            } else {
                oldPets = true;
                continue;
            }
            if (petPlayer == null) {
                MyPetLogger.write("Owner for a pet (" + myPetNBT.getAs("Name", TagString.class) + " not found, pet loading skipped.");
                continue;
            }
            InactiveMyPet inactiveMyPet = new InactiveMyPet(petPlayer);
            inactiveMyPet.load(myPetNBT);

            myPets.put(inactiveMyPet.getOwner(), inactiveMyPet);

            DebugLogger.info("   " + inactiveMyPet.toString());

            petCount++;
        }
        if (oldPets) {
            MyPetLogger.write("Old MyPets can not be loaded! Please use a previous version to upgrade first.");
        }
        return petCount;
    }

    private TagList savePets() {
        List<TagCompound> petList = new ArrayList<>();

        for (MyPet myPet : MyPetList.getAllActiveMyPets()) {
            List<InactiveMyPet> pets = myPets.get(myPet.getOwner());
            for (InactiveMyPet pet : pets) {
                if (myPet.getUUID().equals(pet.getUUID())) {
                    myPets.put(myPet.getOwner(), MyPetList.getInactiveMyPetFromMyPet(myPet));
                    myPets.remove(myPet.getOwner(), pet);
                    break;
                }
            }
        }
        for (InactiveMyPet inactiveMyPet : myPets.values()) {
            try {
                TagCompound petNBT = inactiveMyPet.save();
                petList.add(petNBT);
            } catch (Exception e) {
                DebugLogger.printThrowable(e);
            }
        }
        return new TagList(petList);
    }


    // Players ---------------------------------------------------------------------------------------------------------

    @Override
    public List<MyPetPlayer> getAllMyPetPlayers() {
        return new ArrayList<>(players.values());
    }

    @Override
    public void isMyPetPlayer(final Player player, final RepositoryCallback<Boolean> callback) {
        if (callback != null) {
            for (MyPetPlayer p : players.values()) {
                if (p.getPlayerUUID().equals(player.getUniqueId())) {
                    callback.setValue(true);
                    callback.run();
                    return;
                }
            }
            callback.setValue(false);
            callback.run();
        }
    }

    public void getMyPetPlayer(final UUID uuid, final RepositoryCallback<MyPetPlayer> callback) {
        if (callback != null) {
            callback.setValue(players.get(uuid));
            callback.run();
        }
    }

    @Override
    public void getMyPetPlayer(final Player player, final RepositoryCallback<MyPetPlayer> callback) {
        if (callback != null) {
            for (MyPetPlayer p : players.values()) {
                if (p.getPlayerUUID().equals(player.getUniqueId())) {
                    callback.setValue(p);
                    callback.run();
                    return;
                }
            }
        }
    }

    @Override
    public void updatePlayer(final MyPetPlayer player, final RepositoryCallback<Boolean> callback) {
        // we work with live data so no update required
        if (SAVE_ON_PLAYER_UPDATE) {
            saveData(true);
        }
    }

    @Override
    public void addMyPetPlayer(final MyPetPlayer player, final RepositoryCallback<Boolean> callback) {
        if (players.containsKey(player.getInternalUUID())) {
            players.put(player.getInternalUUID(), player);
            if (SAVE_ON_PLAYER_ADD) {
                saveData(true);
            }
            if (callback != null) {
                callback.setValue(true);
                callback.run();
            }
            return;
        }
        if (callback != null) {
            callback.setValue(false);
            callback.run();
        }
    }

    @Override
    public void removeMyPetPlayer(final MyPetPlayer player, final RepositoryCallback<Boolean> callback) {
        boolean result = players.remove(player.getInternalUUID()) != null;

        if (SAVE_ON_PLAYER_REMOVE) {
            saveData(true);
        }

        if (callback != null) {
            callback.setValue(result);
            callback.run();
        }
    }

    private TagList savePlayers() {
        List<TagCompound> playerList = Lists.newArrayList();
        for (MyPetPlayer myPetPlayer : players.values()) {
            if (myPets.get(myPetPlayer).size() > 0 || myPetPlayer.hasCustomData()) {
                try {
                    playerList.add(myPetPlayer.save());
                } catch (Exception e) {
                    DebugLogger.printThrowable(e);
                }
            }
        }
        return new TagList(playerList);
    }

    private int loadPlayers(TagList playerList) {
        int playerCount = 0;
        if (BukkitUtil.isInOnlineMode()) {
            List<String> unknownPlayers = new ArrayList<>();
            for (int i = 0; i < playerList.getReadOnlyList().size(); i++) {
                TagCompound playerTag = playerList.getTagAs(i, TagCompound.class);
                if (playerTag.containsKeyAs("Name", TagString.class)) {
                    if (playerTag.containsKeyAs("UUID", TagCompound.class)) {
                        TagCompound uuidTag = playerTag.getAs("UUID", TagCompound.class);
                        if (!uuidTag.containsKeyAs("Mojang-UUID", TagString.class)) {
                            String playerName = playerTag.getAs("Name", TagString.class).getStringData();
                            unknownPlayers.add(playerName);
                        }
                    } else if (!playerTag.getCompoundData().containsKey("Mojang-UUID")) {
                        String playerName = playerTag.getAs("Name", TagString.class).getStringData();
                        unknownPlayers.add(playerName);
                    }
                }
            }
            UUIDFetcher.call(unknownPlayers);
        }

        for (int i = 0; i < playerList.getReadOnlyList().size(); i++) {
            TagCompound playerTag = playerList.getTagAs(i, TagCompound.class);
            MyPetPlayer player = PlayerList.createMyPetPlayer(playerTag);
            if (player != null) {
                players.put(player.getInternalUUID(), player);
                playerCount++;
            }
        }
        return playerCount;
    }
}
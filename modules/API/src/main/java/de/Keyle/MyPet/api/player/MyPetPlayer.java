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

package de.Keyle.MyPet.api.player;

import com.google.common.collect.BiMap;
import de.Keyle.MyPet.api.WorldGroup;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.util.NBTStorage;
import de.Keyle.MyPet.api.util.Scheduler;
import de.keyle.knbt.TagBase;
import de.keyle.knbt.TagCompound;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface MyPetPlayer extends Scheduler, NBTStorage {
    String getName();

    boolean hasCustomData();

    // Custom Data -----------------------------------------------------------------

    void setAutoRespawnEnabled(boolean flag);

    boolean hasAutoRespawnEnabled();

    void setAutoRespawnMin(int value);

    int getAutoRespawnMin();

    float getPetLivingSoundVolume();

    void setPetLivingSoundVolume(float volume);

    boolean isHealthBarActive();

    void setHealthBarActive(boolean showHealthBar);

    boolean isCaptureHelperActive();

    void setCaptureHelperActive(boolean captureHelperMode);

    void setMyPetForWorldGroup(String worldGroup, UUID myPetUUID);

    UUID getMyPetForWorldGroup(String worldGroup);

    UUID getMyPetForWorldGroup(WorldGroup worldGroup);

    BiMap<String, UUID> getMyPetsForWorldGroups();

    String getWorldGroupForMyPet(UUID petUUID);

    boolean hasMyPetInWorldGroup(String worldGroup);

    boolean hasMyPetInWorldGroup(WorldGroup worldGroup);

    void setExtendedInfo(TagCompound compound);

    void addExtendedInfo(String key, TagBase tag);

    TagBase getExtendedInfo(String key);

    TagCompound getExtendedInfo();

    // -----------------------------------------------------------------------------

    boolean isOnline();

    UUID getPlayerUUID();

    UUID getInternalUUID();

    UUID getOfflineUUID();

    UUID getMojangUUID();

    String getLanguage();

    boolean isMyPetAdmin();

    boolean hasMyPet();

    ActiveMyPet getMyPet();

    Player getPlayer();

    void sendMessage(String message);

    //donate-delete-start
    DonateCheck.DonationRank getDonationRank();

    void checkForDonation();
    //donate-delete-end
}
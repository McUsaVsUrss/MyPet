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

package de.Keyle.MyPet.util.hooks;

import com.sucy.skill.api.event.PlayerExperienceGainEvent;
import com.sucy.skill.api.event.PlayerExperienceLostEvent;
import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Configuration;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.util.hooks.PluginHookManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkillApi implements Listener {


    private static boolean active = false;

    public static void findPlugin() {
        if (PluginHookManager.isPluginUsable("SkillAPI")) {
            active = true;
            Bukkit.getPluginManager().registerEvents(new SkillApi(), MyPetApi.getPlugin());
        }
    }

    @EventHandler
    public void on(PlayerExperienceGainEvent event) {
        if (Configuration.Hooks.SkillAPI.GRANT_EXP) {
            Player player = event.getPlayerData().getPlayer();
            if (MyPetApi.getPlayerList().isMyPetPlayer(player)) {
                MyPetPlayer petPlayer = MyPetApi.getPlayerList().getMyPetPlayer(player);
                if (petPlayer.hasMyPet()) {
                    ActiveMyPet myPet = petPlayer.getMyPet();
                    if (Configuration.Skilltree.PREVENT_LEVELLING_WITHOUT_SKILLTREE && myPet.getSkilltree() == null) {
                        if (!myPet.autoAssignSkilltree()) {
                            return;
                        }
                    }
                    myPet.getExperience().addExp(event.getExp() * Configuration.Hooks.SkillAPI.EXP_PERCENT / 100);
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerExperienceLostEvent event) {
        if (Configuration.Hooks.SkillAPI.GRANT_EXP) {
            Player player = event.getPlayerData().getPlayer();
            if (MyPetApi.getPlayerList().isMyPetPlayer(player)) {
                MyPetPlayer petPlayer = MyPetApi.getPlayerList().getMyPetPlayer(player);
                if (petPlayer.hasMyPet()) {
                    ActiveMyPet myPet = petPlayer.getMyPet();
                    if (Configuration.Skilltree.PREVENT_LEVELLING_WITHOUT_SKILLTREE && myPet.getSkilltree() == null) {
                        if (!myPet.autoAssignSkilltree()) {
                            return;
                        }
                    }
                    //TODO -> removeExp
                    myPet.getExperience().removeCurrentExp(event.getExp() * Configuration.Hooks.SkillAPI.EXP_PERCENT / 100);
                }
            }
        }
    }

    public static boolean isActive() {
        return active;
    }
}
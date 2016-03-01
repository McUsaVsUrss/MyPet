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

import com.ancientshores.AncientRPG.API.ApiManager;
import com.ancientshores.AncientRPG.Party.AncientRPGParty;
import com.gmail.nossr50.api.PartyAPI;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import de.Keyle.MyPet.api.util.hooks.PluginHookManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyManager {

    public static boolean isInParty(Player player) {
        return getPartyMembers(player) != null;
    }

    public static List<Player> getPartyMembers(Player player) {

        if (PluginHookManager.isPluginUsable("Heroes")) {
            try {
                Heroes heroes = PluginHookManager.getPluginInstance(Heroes.class);
                Hero heroPlayer = heroes.getCharacterManager().getHero(player);
                if (heroPlayer.getParty() != null) {
                    List<Player> members = new ArrayList<>();
                    for (Hero hero : heroPlayer.getParty().getMembers()) {
                        if (hero.getPlayer().isOnline()) {
                            members.add(hero.getPlayer());
                        }
                    }
                    return members;
                }
            } catch (Exception ignored) {
            }
        }
        if (PluginHookManager.isPluginUsable("mcMMO")) {
            try {
                if (PartyAPI.inParty(player)) {
                    List<Player> members = new ArrayList<>();
                    String partyName = PartyAPI.getPartyName(player);
                    for (Player member : PartyAPI.getOnlineMembers(partyName)) {
                        members.add(member);
                    }
                    return members;
                }
            } catch (Exception ignored) {
            }
        }
        if (PluginHookManager.isPluginUsable("AncientRPG")) {
            try {
                ApiManager api = ApiManager.getApiManager();
                AncientRPGParty party = api.getPlayerParty(player);
                if (party != null) {
                    List<Player> members = new ArrayList<>();
                    for (UUID memberUUID : party.getMembers()) {
                        Player member = Bukkit.getPlayer(memberUUID);
                        if (member.isOnline()) {
                            members.add(member);
                        }
                    }
                    return members;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
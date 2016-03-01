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

package de.Keyle.MyPet.commands.admin;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.WorldGroup;
import de.Keyle.MyPet.api.commands.CommandOptionTabCompleter;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.repository.RepositoryCallback;
import de.Keyle.MyPet.api.util.locale.Translation;
import de.Keyle.MyPet.commands.CommandAdmin;
import de.Keyle.MyPet.entity.InactiveMyPet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandOptionClone implements CommandOptionTabCompleter {
    @Override
    public boolean onCommandOption(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String lang = MyPetApi.getBukkitHelper().getCommandSenderLanguage(sender);
        Player oldOwner = Bukkit.getPlayer(args[0]);
        if (oldOwner == null || !oldOwner.isOnline()) {
            sender.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] " + Translation.getString("Message.No.PlayerOnline", lang));
            return true;
        }
        final Player newOwner = Bukkit.getPlayer(args[1]);
        if (newOwner == null || !newOwner.isOnline()) {
            sender.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] " + Translation.getString("Message.No.PlayerOnline", lang));
            return true;
        }

        if (!MyPetApi.getPlayerList().isMyPetPlayer(oldOwner)) {
            sender.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] " + Util.formatText(Translation.getString("Message.No.UserHavePet", lang), oldOwner.getName()));
            return true;
        }

        MyPetPlayer oldPetOwner = MyPetApi.getPlayerList().getMyPetPlayer(oldOwner);

        if (!oldPetOwner.hasMyPet()) {
            sender.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] " + Util.formatText(Translation.getString("Message.No.UserHavePet", lang), oldOwner.getName()));
            return true;
        }

        final MyPetPlayer newPetOwner;
        if (MyPetApi.getPlayerList().isMyPetPlayer(newOwner)) {
            newPetOwner = MyPetApi.getPlayerList().getMyPetPlayer(newOwner);
        } else {
            newPetOwner = MyPetApi.getPlayerList().registerMyPetPlayer(newOwner);
        }

        if (newPetOwner.hasMyPet()) {
            sender.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] " + newOwner.getName() + " has already an active MyPet!");
            return true;
        }

        ActiveMyPet oldPet = oldPetOwner.getMyPet();
        final InactiveMyPet newPet = new InactiveMyPet(newPetOwner);
        newPet.setPetName(oldPet.getPetName());
        newPet.setExp(oldPet.getExperience().getExp());
        newPet.setHealth(oldPet.getHealth());
        newPet.setHungerValue(oldPet.getHungerValue());
        newPet.setRespawnTime(oldPet.getRespawnTime());
        newPet.setInfo(oldPet.getInfo());
        newPet.setPetType(oldPet.getPetType());
        newPet.setSkilltree(oldPet.getSkilltree());
        newPet.setWorldGroup(oldPet.getWorldGroup());
        newPet.setSkills(oldPet.getSkillInfo());

        MyPetApi.getRepository().addMyPet(newPet, new RepositoryCallback<Boolean>() {
            @Override
            public void callback(Boolean value) {
                ActiveMyPet myPet = MyPetApi.getMyPetList().activateMyPet(newPet);

                if (myPet != null) {
                    WorldGroup worldGroup = WorldGroup.getGroupByWorld(newPet.getOwner().getPlayer().getWorld().getName());
                    newPet.setWorldGroup(worldGroup.getName());
                    newPet.getOwner().setMyPetForWorldGroup(worldGroup.getName(), newPet.getUUID());
                    MyPetApi.getRepository().updateMyPetPlayer(newPetOwner, null);

                    newOwner.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] MyPet owned by " + newPetOwner.getName() + " successfully cloned!");
                }
            }
        });


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        if (strings.length == 2) {
            return null;
        }
        if (strings.length == 3) {
            return null;
        }
        return CommandAdmin.EMPTY_LIST;
    }
}
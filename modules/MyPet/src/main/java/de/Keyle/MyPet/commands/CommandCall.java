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

package de.Keyle.MyPet.commands;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.util.locale.Translation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandCall implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player petOwner = (Player) sender;
            if (MyPetApi.getMyPetList().hasActiveMyPet(petOwner)) {
                ActiveMyPet myPet = MyPetApi.getMyPetList().getMyPet(petOwner);

                myPet.removePet(true);

                switch (myPet.createEntity()) {
                    case Success:
                        sender.sendMessage(Util.formatText(Translation.getString("Message.Command.Call.Success", petOwner), myPet.getPetName()));
                        break;
                    case Canceled:
                        sender.sendMessage(Util.formatText(Translation.getString("Message.Spawn.Prevent", petOwner), myPet.getPetName()));
                        break;
                    case NoSpace:
                        sender.sendMessage(Util.formatText(Translation.getString("Message.Spawn.NoSpace", petOwner), myPet.getPetName()));
                        break;
                    case NotAllowed:
                        sender.sendMessage(Translation.getString("Message.No.AllowedHere", petOwner).replace("%petname%", myPet.getPetName()));
                        break;
                    case Dead:
                        sender.sendMessage(Util.formatText(Translation.getString("Message.Call.Dead", petOwner), myPet.getPetName(), myPet.getRespawnTime()));
                        break;
                    case Flying:
                        sender.sendMessage(Util.formatText(Translation.getString("Message.Spawn.Flying", petOwner), myPet.getPetName()));
                        break;
                }
                return true;
            } else {
                sender.sendMessage(Translation.getString("Message.No.HasPet", petOwner));
            }
            return true;
        }
        sender.sendMessage("You can't use this command from server console!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        return CommandAdmin.EMPTY_LIST;
    }
}
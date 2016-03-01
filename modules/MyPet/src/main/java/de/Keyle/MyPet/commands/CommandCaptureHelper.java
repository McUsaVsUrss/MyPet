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

package de.Keyle.MyPet.commands;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.player.Permissions;
import de.Keyle.MyPet.api.util.locale.Translation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandCaptureHelper implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;


            if (Permissions.has(player, "MyPet.user.command.capturehelper")) {
                MyPetPlayer myPetPlayer;
                if (MyPetApi.getPlayerList().isMyPetPlayer(player)) {
                    myPetPlayer = MyPetApi.getPlayerList().getMyPetPlayer(player);

                    if (myPetPlayer.hasMyPet()) {
                        player.sendMessage(Translation.getString("Message.Command.CaptureHelper.HasPet", player));
                        return true;
                    }
                } else {
                    myPetPlayer = MyPetApi.getPlayerList().registerMyPetPlayer(player);
                }

                myPetPlayer.setCaptureHelperActive(!myPetPlayer.isCaptureHelperActive());
                String mode = myPetPlayer.isCaptureHelperActive() ? Translation.getString("Name.Enabled", player) : Translation.getString("Name.Disabled", player);
                player.sendMessage(Util.formatText(Translation.getString("Message.Command.CaptureHelper.Mode", player), mode));
                return true;
            }
            player.sendMessage(Translation.getString("Message.No.Allowed", player));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        return CommandAdmin.EMPTY_LIST;
    }
}
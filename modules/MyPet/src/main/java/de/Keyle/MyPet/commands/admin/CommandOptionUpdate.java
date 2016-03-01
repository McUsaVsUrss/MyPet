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

import de.Keyle.MyPet.api.commands.CommandOption;
import de.Keyle.MyPet.util.UpdateCheck;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandOptionUpdate implements CommandOption {
    @Override
    public boolean onCommandOption(CommandSender sender, String[] args) {
        String message = UpdateCheck.checkForUpdate("MyPet");
        if (message != null) {
            sender.sendMessage("A new version is available: " + ChatColor.GOLD + message);
        } else {
            sender.sendMessage("Your version of MyPet is up to date.");
        }
        return true;
    }
}
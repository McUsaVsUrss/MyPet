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

package de.Keyle.MyPet.commands.options;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.commands.CommandOptionTabCompleter;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.util.locale.Translation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandOptionPetLivingSound implements CommandOptionTabCompleter {
    private List<String> presetVolumes = new ArrayList<>();

    public CommandOptionPetLivingSound() {
        presetVolumes.add("100");
        presetVolumes.add("75");
        presetVolumes.add("50");
        presetVolumes.add("25");
        presetVolumes.add("0");
    }

    @Override
    public boolean onCommandOption(CommandSender sender, String[] args) {
        if (sender instanceof Player && MyPetApi.getPlayerList().isMyPetPlayer((Player) sender)) {
            if (args.length > 0 && Util.isInt(args[0])) {
                float volume = Math.min(Math.max(Integer.parseInt(args[0]), 0f), 100f) / 100f;

                MyPetPlayer myPetPlayer = MyPetApi.getPlayerList().getMyPetPlayer((Player) sender);
                myPetPlayer.setPetLivingSoundVolume(volume);

                sender.sendMessage(Translation.getString("Message.Command.Success", sender));
                return true;
            }
        }
        sender.sendMessage(Translation.getString("Message.Command.Fail", sender));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return presetVolumes;
    }
}
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

package de.Keyle.MyPet.commands.admin;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Util;
import de.Keyle.MyPet.api.WorldGroup;
import de.Keyle.MyPet.api.commands.CommandOptionTabCompleter;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.repository.RepositoryCallback;
import de.Keyle.MyPet.api.util.locale.Translation;
import de.Keyle.MyPet.commands.CommandAdmin;
import de.keyle.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CommandOptionSwitch implements CommandOptionTabCompleter {
    @Override
    public boolean onCommandOption(final CommandSender sender, String[] parameter) {
        boolean show = true;
        MyPetPlayer o = null;
        UUID petUUID = null;
        final String lang = MyPetApi.getBukkitHelper().getCommandSenderLanguage(sender);

        if (parameter.length == 0) {
            if (sender instanceof Player) {
                Player petOwner = (Player) sender;
                if (MyPetApi.getPlayerList().isMyPetPlayer(petOwner)) {
                    o = MyPetApi.getPlayerList().getMyPetPlayer(petOwner);
                }
            } else {
                sender.sendMessage("You can't use this command from server console!");
                return true;
            }
        } else if (parameter.length == 1) {
            Player player = Bukkit.getPlayer(parameter[0]);
            if (player == null || !player.isOnline()) {
                sender.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] " + Translation.getString("Message.No.PlayerOnline", lang));
                return true;
            }
            if (MyPetApi.getPlayerList().isMyPetPlayer(player)) {
                o = MyPetApi.getPlayerList().getMyPetPlayer(player);
            }
            if (o == null) {
                sender.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] " + Util.formatText(Translation.getString("Message.No.UserHavePet", lang), o.getName()));
            }
        } else if (parameter.length == 2) {
            show = false;
            try {
                o = MyPetApi.getPlayerList().getMyPetPlayer(UUID.fromString(parameter[0]));
                petUUID = UUID.fromString(parameter[1]);
            } catch (IllegalArgumentException ignored) {
            }
        }

        final MyPetPlayer owner = o;

        if (show && owner != null) {
            MyPetApi.getRepository().getMyPets(owner, new RepositoryCallback<List<MyPet>>() {
                @Override
                public void callback(List<MyPet> value) {
                    sender.sendMessage("Select the MyPet you want the player to switch to:");
                    boolean doComma = false;
                    FancyMessage message = new FancyMessage("");
                    for (MyPet mypet : value) {

                        if (doComma) {
                            message.then(", ");
                        }
                        message.then(mypet.getPetName())
                                .color(ChatColor.AQUA)
                                .command("/petadmin switch " + owner.getInternalUUID() + " " + mypet.getUUID())
                                .itemTooltip(Util.myPetToItemTooltip(mypet, lang));
                        if (!doComma) {
                            doComma = true;
                        }
                    }
                    MyPetApi.getBukkitHelper().sendMessageRaw((Player) sender, message.toJSONString());
                }
            });

        } else if (!show && owner != null && petUUID != null) {
            MyPetApi.getRepository().getMyPet(petUUID, new RepositoryCallback<MyPet>() {
                @Override
                public void callback(MyPet newPet) {
                    if (newPet != null) {
                        if (owner.hasMyPet()) {
                            MyPetApi.getMyPetList().deactivateMyPet(owner, true);
                        }

                        ActiveMyPet myPet = MyPetApi.getMyPetList().activateMyPet(newPet);
                        sender.sendMessage(Translation.getString("Message.Command.Success", sender));
                        if (myPet != null) {

                            WorldGroup worldGroup = WorldGroup.getGroupByWorld(owner.getPlayer().getWorld().getName());
                            newPet.setWorldGroup(worldGroup.getName());
                            newPet.getOwner().setMyPetForWorldGroup(worldGroup.getName(), newPet.getUUID());

                            myPet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.MultiWorld.NowActivePet", owner), myPet.getPetName()));
                            switch (myPet.createEntity()) {
                                case Success:
                                    sender.sendMessage(Util.formatText(Translation.getString("Message.Command.Call.Success", owner), myPet.getPetName()));
                                    break;
                                case Canceled:
                                    sender.sendMessage(Util.formatText(Translation.getString("Message.Spawn.Prevent", owner), myPet.getPetName()));
                                    break;
                                case NoSpace:
                                    sender.sendMessage(Util.formatText(Translation.getString("Message.Spawn.NoSpace", owner), myPet.getPetName()));
                                    break;
                                case NotAllowed:
                                    sender.sendMessage(Translation.getString("Message.No.AllowedHere", owner).replace("%petname%", myPet.getPetName()));
                                    break;
                                case Dead:
                                    sender.sendMessage(Util.formatText(Translation.getString("Message.Call.Dead", owner), myPet.getPetName(), myPet.getRespawnTime()));
                                    break;
                                case Flying:
                                    sender.sendMessage(Util.formatText(Translation.getString("Message.Spawn.Flying", owner), myPet.getPetName()));
                                    break;
                            }
                        }
                    }
                }
            });

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        if (strings.length == 2) {
            return null;
        } else {
            return CommandAdmin.EMPTY_LIST;
        }
    }
}
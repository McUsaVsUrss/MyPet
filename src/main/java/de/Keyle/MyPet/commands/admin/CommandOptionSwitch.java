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

import de.Keyle.MyPet.api.commands.CommandOptionTabCompleter;
import de.Keyle.MyPet.commands.CommandAdmin;
import de.Keyle.MyPet.entity.types.InactiveMyPet;
import de.Keyle.MyPet.entity.types.MyPet;
import de.Keyle.MyPet.repository.MyPetList;
import de.Keyle.MyPet.repository.PlayerList;
import de.Keyle.MyPet.repository.RepositoryCallback;
import de.Keyle.MyPet.util.BukkitUtil;
import de.Keyle.MyPet.util.Util;
import de.Keyle.MyPet.util.WorldGroup;
import de.Keyle.MyPet.util.locale.Locales;
import de.Keyle.MyPet.util.player.MyPetPlayer;
import de.keyle.fanciful.FancyMessage;
import de.keyle.fanciful.ItemTooltip;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RESET;

public class CommandOptionSwitch implements CommandOptionTabCompleter {
    @Override
    public boolean onCommandOption(final CommandSender sender, String[] parameter) {
        boolean show = true;
        MyPetPlayer o = null;
        UUID petUUID = null;
        final String lang = BukkitUtil.getCommandSenderLanguage(sender);

        if (parameter.length == 0) {
            if (sender instanceof Player) {
                Player petOwner = (Player) sender;
                if (PlayerList.isMyPetPlayer(petOwner)) {
                    o = PlayerList.getMyPetPlayer(petOwner);
                }
            } else {
                sender.sendMessage("You can't use this command from server console!");
                return true;
            }
        } else if (parameter.length == 1) {
            Player player = Bukkit.getPlayer(parameter[0]);
            if (player == null || !player.isOnline()) {
                sender.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] " + Locales.getString("Message.No.PlayerOnline", lang));
                return true;
            }
            if (PlayerList.isMyPetPlayer(player)) {
                o = PlayerList.getMyPetPlayer(player);
            }
            if (o == null) {
                sender.sendMessage("[" + ChatColor.AQUA + "MyPet" + ChatColor.RESET + "] " + Util.formatText(Locales.getString("Message.No.UserHavePet", lang), o.getName()));
            }
        } else if (parameter.length == 2) {
            show = false;
            try {
                o = PlayerList.getMyPetPlayer(UUID.fromString(parameter[0]));
                petUUID = UUID.fromString(parameter[1]);
            } catch (IllegalArgumentException ignored) {
            }
        }

        final MyPetPlayer owner = o;

        if (show && owner != null) {
            owner.getInactiveMyPets(new RepositoryCallback<List<InactiveMyPet>>() {
                @Override
                public void callback(List<InactiveMyPet> value) {
                    sender.sendMessage("Select the MyPet you want the player to switch to:");
                    boolean doComma = false;
                    FancyMessage message = new FancyMessage("");
                    for (InactiveMyPet mypet : value) {
                        List<String> lore = new ArrayList<>();
                        lore.add(RESET + Locales.getString("Name.Hunger", lang) + ": " + GOLD + mypet.getHungerValue());
                        if (mypet.getRespawnTime() > 0) {
                            lore.add(RESET + Locales.getString("Name.Respawntime", lang) + ": " + GOLD + mypet.getRespawnTime() + "sec");
                        } else {
                            lore.add(RESET + Locales.getString("Name.HP", lang) + ": " + GOLD + String.format("%1.2f", mypet.getHealth()));
                        }
                        lore.add(RESET + Locales.getString("Name.Exp", lang) + ": " + GOLD + String.format("%1.2f", mypet.getExp()));
                        lore.add(RESET + Locales.getString("Name.Type", lang) + ": " + GOLD + mypet.getPetType().getTypeName());
                        lore.add(RESET + Locales.getString("Name.Skilltree", lang) + ": " + GOLD + (mypet.getSkillTree() != null ? mypet.getSkillTree().getDisplayName() : "-"));

                        if (doComma) {
                            message.then(", ");
                        }
                        message.then(mypet.getPetName())
                                .color(ChatColor.AQUA)
                                .command("/petadmin switch " + owner.getInternalUUID() + " " + mypet.getUUID())
                                .itemTooltip(new ItemTooltip().setMaterial(Material.MONSTER_EGG).addLore(lore).setTitle(mypet.getPetName()));
                        if (!doComma) {
                            doComma = true;
                        }
                    }
                    BukkitUtil.sendMessageRaw((Player) sender, message.toJSONString());
                }
            });

        } else if (!show && owner != null && petUUID != null) {
            owner.getInactiveMyPet(petUUID, new RepositoryCallback<InactiveMyPet>() {
                @Override
                public void callback(InactiveMyPet newPet) {
                    if (newPet != null) {
                        if (owner.hasMyPet()) {
                            MyPetList.deactivateMyPet(owner);
                        }

                        MyPet myPet = MyPetList.activateMyPet(newPet);
                        sender.sendMessage(Locales.getString("Message.Command.Success", sender));
                        if (myPet != null) {

                            WorldGroup worldGroup = WorldGroup.getGroupByWorld(owner.getPlayer().getWorld().getName());
                            newPet.setWorldGroup(worldGroup.getName());
                            newPet.getOwner().setMyPetForWorldGroup(worldGroup.getName(), newPet.getUUID());

                            myPet.sendMessageToOwner(Util.formatText(Locales.getString("Message.MultiWorld.NowActivePet", owner), myPet.getPetName()));
                            switch (myPet.createPet()) {
                                case Success:
                                    sender.sendMessage(Util.formatText(Locales.getString("Message.Command.Call.Success", owner), myPet.getPetName()));
                                    break;
                                case Canceled:
                                    sender.sendMessage(Util.formatText(Locales.getString("Message.Spawn.Prevent", owner), myPet.getPetName()));
                                    break;
                                case NoSpace:
                                    sender.sendMessage(Util.formatText(Locales.getString("Message.Spawn.NoSpace", owner), myPet.getPetName()));
                                    break;
                                case NotAllowed:
                                    sender.sendMessage(Locales.getString("Message.No.AllowedHere", owner).replace("%petname%", myPet.getPetName()));
                                    break;
                                case Dead:
                                    sender.sendMessage(Util.formatText(Locales.getString("Message.Call.Dead", owner), myPet.getPetName(), myPet.getRespawnTime()));
                                    break;
                                case Flying:
                                    sender.sendMessage(Util.formatText(Locales.getString("Message.Spawn.Flying", owner), myPet.getPetName()));
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
            return CommandAdmin.emptyList;
        }
    }
}
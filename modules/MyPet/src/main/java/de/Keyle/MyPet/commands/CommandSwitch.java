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
import de.Keyle.MyPet.api.WorldGroup;
import de.Keyle.MyPet.api.entity.ActiveMyPet;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.player.Permissions;
import de.Keyle.MyPet.api.repository.RepositoryCallback;
import de.Keyle.MyPet.api.util.locale.Translation;
import de.Keyle.MyPet.util.selectionmenu.MyPetSelectionGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSwitch implements CommandExecutor, TabCompleter {
    private List<String> storeList = new ArrayList<>();

    public CommandSwitch() {
        storeList.add("store");
    }

    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't use this command from server console!");
            return true;
        }
        Player player = (Player) sender;
        if (!Permissions.has(player, "MyPet.user.command.switch")) {
            player.sendMessage(Translation.getString("Message.No.Allowed", player));
            return true;
        }

        if (MyPetApi.getPlayerList().isMyPetPlayer(player)) {
            final MyPetPlayer owner = MyPetApi.getPlayerList().getMyPetPlayer(player);

            if (args.length > 0 && args[0].equalsIgnoreCase("store")) {
                if (owner.isOnline() && owner.hasMyPet()) {
                    MyPetApi.getRepository().getMyPets(owner, new RepositoryCallback<List<MyPet>>() {
                        @Override
                        public void callback(List<MyPet> pets) {
                            ActiveMyPet myPet = owner.getMyPet();
                            String worldGroup = myPet.getWorldGroup();

                            int inactivePetCount = getInactivePetCount(pets, worldGroup) - 1; // -1 for active pet
                            int maxPetCount = getMaxPetCount(owner.getPlayer());

                            if (inactivePetCount >= maxPetCount) {
                                sender.sendMessage(Util.formatText(Translation.getString("Message.Command.Switch.Limit", owner), maxPetCount));
                                return;
                            }
                            if (MyPetApi.getMyPetList().deactivateMyPet(owner, true)) {
                                owner.setMyPetForWorldGroup(worldGroup, null);
                                sender.sendMessage(Util.formatText(Translation.getString("Message.Command.Switch.Success", owner), myPet.getPetName()));
                            }
                        }
                    });
                } else {
                    player.sendMessage(Translation.getString("Message.Command.Switch.NoPet", player));
                }
                return true;
            }


            MyPetApi.getRepository().getMyPets(owner, new RepositoryCallback<List<MyPet>>() {
                @Override
                public void callback(List<MyPet> pets) {
                    if (pets.size() == 0) {
                        owner.sendMessage(Translation.getString("Message.No.HasPet", owner));
                        return;
                    }
                    if (owner.isOnline()) {
                        String worldGroup = WorldGroup.getGroupByWorld(owner.getPlayer().getWorld().getName()).getName();
                        int inactivePetCount = getInactivePetCount(pets, worldGroup);
                        int maxPetCount = getMaxPetCount(owner.getPlayer());

                        if (owner.hasMyPet()) {
                            inactivePetCount--;
                            if (!Permissions.has(owner, "MyPet.user.command.switch.bypass")) {
                                if (inactivePetCount >= maxPetCount) {
                                    sender.sendMessage(Util.formatText(Translation.getString("Message.Command.Switch.Limit", owner), maxPetCount));
                                    return;
                                }
                            }
                        }

                        String stats = "(" + inactivePetCount + "/" + maxPetCount + ")";

                        final MyPetSelectionGui gui = new MyPetSelectionGui(owner, stats + " " + Translation.getString("Message.SelectMyPet", owner));
                        gui.open(pets, new RepositoryCallback<MyPet>() {
                            @Override
                            public void callback(MyPet myPet) {
                                ActiveMyPet activePet = MyPetApi.getMyPetList().activateMyPet(myPet);
                                if (activePet != null && owner.isOnline()) {
                                    Player player = owner.getPlayer();
                                    activePet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Npc.ChosenPet", owner), activePet.getPetName()));
                                    WorldGroup wg = WorldGroup.getGroupByWorld(player.getWorld().getName());
                                    owner.setMyPetForWorldGroup(wg.getName(), activePet.getUUID());

                                    switch (activePet.createEntity()) {
                                        case Canceled:
                                            activePet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Spawn.Prevent", owner), activePet.getPetName()));
                                            break;
                                        case NoSpace:
                                            activePet.getOwner().sendMessage(Util.formatText(Translation.getString("Message.Spawn.NoSpace", owner), activePet.getPetName()));
                                            break;
                                        case NotAllowed:
                                            activePet.getOwner().sendMessage(Translation.getString("Message.No.AllowedHere", owner).replace("%petname%", activePet.getPetName()));
                                            break;
                                        case Dead:
                                            activePet.getOwner().sendMessage(Translation.getString("Message.Spawn.Respawn.In", owner).replace("%petname%", activePet.getPetName()).replace("%time%", "" + activePet.getRespawnTime()));
                                            break;
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } else {
            sender.sendMessage(Translation.getString("Message.No.HasPet", player));
        }
        return true;
    }

    private int getMaxPetCount(Player p) {
        int maxPetCount = 0;
        if (p.isOp()) {
            maxPetCount = 54;
        } else {
            for (int i = 54; i > 0; i--) {
                if (Permissions.has(p, "MyPet.user.command.switch.limit." + i)) {
                    maxPetCount = i;
                    break;
                }
            }
        }
        return maxPetCount;
    }

    private int getInactivePetCount(List<MyPet> pets, String worldGroup) {
        int inactivePetCount = 0;

        for (MyPet pet : pets) {
            if (!pet.getWorldGroup().equals(worldGroup)) {
                continue;
            }
            inactivePetCount++;
        }

        return inactivePetCount;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (MyPetApi.getMyPetList().hasActiveMyPet((Player) commandSender)) {
            return storeList;
        }
        return CommandAdmin.EMPTY_LIST;
    }
}
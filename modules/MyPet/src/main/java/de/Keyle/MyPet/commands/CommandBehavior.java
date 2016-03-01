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
import de.Keyle.MyPet.api.entity.ActiveMyPet.PetState;
import de.Keyle.MyPet.api.player.Permissions;
import de.Keyle.MyPet.api.skill.skills.BehaviorInfo.BehaviorState;
import de.Keyle.MyPet.api.util.locale.Translation;
import de.Keyle.MyPet.skill.skills.Behavior;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandBehavior implements CommandExecutor, TabCompleter {
    private static List<String> behaviorList = new ArrayList<>();

    static {
        behaviorList.add("normal");
        behaviorList.add("friendly");
        behaviorList.add("aggressive");
        behaviorList.add("raid");
        behaviorList.add("farm");
        behaviorList.add("duel");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player petOwner = (Player) sender;
            if (MyPetApi.getMyPetList().hasActiveMyPet(petOwner)) {
                ActiveMyPet myPet = MyPetApi.getMyPetList().getMyPet(petOwner);

                if (myPet.getStatus() == PetState.Despawned) {
                    sender.sendMessage(Util.formatText(Translation.getString("Message.Call.First", petOwner), myPet.getPetName()));
                    return true;
                }
                if (myPet.getStatus() == PetState.Dead) {
                    sender.sendMessage(Util.formatText(Translation.getString("Message.No.CanUse", petOwner), myPet.getPetName()));
                    return true;
                } else if (myPet.getSkills().hasSkill(Behavior.class)) {
                    Behavior behaviorSkill = myPet.getSkills().getSkill(Behavior.class);
                    if (args.length == 1) {
                        if ((args[0].equalsIgnoreCase("friendly") || args[0].equalsIgnoreCase("friend"))) {
                            if (!Permissions.hasExtended(petOwner, "MyPet.user.extended.Behavior.Friendly") || !behaviorSkill.isModeUsable(BehaviorState.Friendly)) {
                                myPet.getOwner().sendMessage(Translation.getString("Message.No.Allowed", petOwner));
                                return true;
                            }
                            behaviorSkill.activateBehavior(Behavior.BehaviorState.Friendly);
                        } else if ((args[0].equalsIgnoreCase("aggressive") || args[0].equalsIgnoreCase("Aggro"))) {
                            if (!Permissions.hasExtended(petOwner, "MyPet.user.extended.Behavior.aggressive") || !behaviorSkill.isModeUsable(BehaviorState.Aggressive)) {
                                myPet.getOwner().sendMessage(Translation.getString("Message.No.Allowed", petOwner));
                                return true;
                            }
                            behaviorSkill.activateBehavior(Behavior.BehaviorState.Aggressive);
                        } else if (args[0].equalsIgnoreCase("farm")) {
                            if (!Permissions.hasExtended(petOwner, "MyPet.user.extended.Behavior.Farm") || !behaviorSkill.isModeUsable(BehaviorState.Farm)) {
                                myPet.getOwner().sendMessage(Translation.getString("Message.No.Allowed", petOwner));
                                return true;
                            }
                            behaviorSkill.activateBehavior(BehaviorState.Farm);
                        } else if (args[0].equalsIgnoreCase("raid")) {
                            if (!Permissions.hasExtended(petOwner, "MyPet.user.extended.Behavior.Raid") || !behaviorSkill.isModeUsable(BehaviorState.Raid)) {
                                myPet.getOwner().sendMessage(Translation.getString("Message.No.Allowed", petOwner));
                                return true;
                            }
                            behaviorSkill.activateBehavior(Behavior.BehaviorState.Raid);
                        } else if (args[0].equalsIgnoreCase("duel")) {
                            if (!Permissions.hasExtended(petOwner, "MyPet.user.extended.Behavior.Duel") || !behaviorSkill.isModeUsable(BehaviorState.Duel)) {
                                myPet.getOwner().sendMessage(Translation.getString("Message.No.Allowed", petOwner));
                                return true;
                            }
                            behaviorSkill.activateBehavior(Behavior.BehaviorState.Duel);
                        } else if (args[0].equalsIgnoreCase("normal")) {
                            behaviorSkill.activateBehavior(Behavior.BehaviorState.Normal);
                        } else {
                            behaviorSkill.activate();
                            return false;
                        }
                    } else {
                        behaviorSkill.activate();
                    }
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
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            return behaviorList;
        }
        return CommandAdmin.EMPTY_LIST;
    }
}
/*
 * This file is part of mypet-api
 *
 * Copyright (C) 2011-2016 Keyle
 * mypet-api is licensed under the GNU Lesser General Public License.
 *
 * mypet-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mypet-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.api.skill.skilltreeloader;


import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPetType;
import de.Keyle.MyPet.api.skill.SkillInfo;
import de.Keyle.MyPet.api.skill.SkillsInfo;
import de.Keyle.MyPet.api.skill.skilltree.SkillTree;
import de.Keyle.MyPet.api.skill.skilltree.SkillTreeLevel;
import de.Keyle.MyPet.api.skill.skilltree.SkillTreeMobType;
import de.Keyle.MyPet.api.util.configuration.ConfigurationNBT;
import de.keyle.knbt.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SkillTreeLoaderNBT extends SkillTreeLoader {
    public static SkillTreeLoaderNBT getSkilltreeLoader() {
        return new SkillTreeLoaderNBT();
    }

    private SkillTreeLoaderNBT() {
    }

    public void loadSkillTrees(String configPath, String[] mobtypes) {
        ConfigurationNBT skilltreeConfig;
        File skillFile;

        for (String mobType : mobtypes) {
            skillFile = new File(configPath + File.separator + mobType.toLowerCase() + ".st");

            SkillTreeMobType skillTreeMobType;
            if (mobType.equalsIgnoreCase("default")) {
                skillTreeMobType = SkillTreeMobType.DEFAULT;
            } else {
                skillTreeMobType = SkillTreeMobType.getMobTypeByName(mobType);
            }

            if (!skillFile.exists()) {
                continue;
            }

            skilltreeConfig = new ConfigurationNBT(skillFile);
            if (skilltreeConfig.load()) {
                try {
                    loadSkillTree(skilltreeConfig, skillTreeMobType);
                } catch (Exception e) {
                    MyPetApi.getLogger().warning("Error while loading skilltrees from: " + mobType.toLowerCase() + ".st");
                    e.printStackTrace();
                }
            }
            skillTreeMobType.cleanupPlaces();
        }
    }

    protected void loadSkillTree(ConfigurationNBT nbtConfiguration, SkillTreeMobType skillTreeMobType) {
        TagList skilltreeList = nbtConfiguration.getNBTCompound().getAs("Skilltrees", TagList.class);
        for (int i_skilltree = 0; i_skilltree < skilltreeList.size(); i_skilltree++) {
            TagCompound skilltreeCompound = skilltreeList.getTagAs(i_skilltree, TagCompound.class);
            SkillTree skillTree = new SkillTree(skilltreeCompound.getAs("Name", TagString.class).getStringData());

            int place = skilltreeCompound.getAs("Place", TagInt.class).getIntData();

            if (skilltreeCompound.getCompoundData().containsKey("Inherits")) {
                skillTree.setInheritance(skilltreeCompound.getAs("Inherits", TagString.class).getStringData());
            }
            if (skilltreeCompound.getCompoundData().containsKey("Permission")) {
                skillTree.setPermission(skilltreeCompound.getAs("Permission", TagString.class).getStringData());
            }
            if (skilltreeCompound.getCompoundData().containsKey("Display")) {
                skillTree.setDisplayName(skilltreeCompound.getAs("Display", TagString.class).getStringData());
            }
            if (skilltreeCompound.getCompoundData().containsKey("MaxLevel")) {
                skillTree.setMaxLevel(skilltreeCompound.getAs("MaxLevel", TagInt.class).getIntData());
            }
            if (skilltreeCompound.getCompoundData().containsKey("RequiredLevel")) {
                skillTree.setRequiredLevel(skilltreeCompound.getAs("RequiredLevel", TagInt.class).getIntData());
            }
            if (skilltreeCompound.getCompoundData().containsKey("IconItem")) {
                skillTree.setIconItem(skilltreeCompound.getAs("IconItem", TagCompound.class));
            }
            if (skilltreeCompound.getCompoundData().containsKey("Description")) {
                TagList descriptionTagList = skilltreeCompound.getAs("Description", TagList.class);
                for (int i = 0; i < descriptionTagList.size(); i++) {
                    skillTree.addDescriptionLine(descriptionTagList.getTagAs(i, TagString.class).getStringData());
                }
            }

            if (skilltreeCompound.getCompoundData().containsKey("Level")) {
                TagList levelList = skilltreeCompound.getAs("Level", TagList.class);
                for (int i_level = 0; i_level < levelList.size(); i_level++) {
                    TagCompound levelCompound = levelList.getTag(i_level);
                    int thisLevel;
                    if (levelCompound.containsKeyAs("Level", TagInt.class)) {
                        thisLevel = levelCompound.getAs("Level", TagInt.class).getIntData();
                    } else {
                        thisLevel = levelCompound.getAs("Level", TagShort.class).getShortData();
                    }

                    SkillTreeLevel newLevel = skillTree.addLevel(thisLevel);
                    if (levelCompound.getCompoundData().containsKey("Message")) {
                        String message = levelCompound.getAs("Message", TagString.class).getStringData();
                        newLevel.setLevelupMessage(message);
                    }

                    TagList skillList = levelCompound.get("Skills");
                    for (int i_skill = 0; i_skill < skillList.size(); i_skill++) {
                        TagCompound skillCompound = skillList.getTag(i_skill);
                        String skillName = skillCompound.getAs("Name", TagString.class).getStringData();
                        if (SkillsInfo.getSkillInfoClass(skillName) != null) {
                            TagCompound skillPropertyCompound = skillCompound.get("Properties");
                            SkillInfo skill = SkillsInfo.getNewSkillInfoInstance(skillName);
                            if (skill != null) {
                                skill.setProperties(skillPropertyCompound);
                                skill.setDefaultProperties();
                                skillTree.addSkillToLevel(thisLevel, skill);
                            }
                        }
                    }
                }
            }
            skillTreeMobType.addSkillTree(skillTree, place);
        }
    }

    public List<String> saveSkillTrees(String configPath, String[] mobtypes) {
        ConfigurationNBT nbtConfig;
        File skillFile;
        List<String> savedPetTypes = new ArrayList<>();

        for (String petType : mobtypes) {
            skillFile = new File(configPath + File.separator + petType.toLowerCase() + ".st");
            nbtConfig = new ConfigurationNBT(skillFile);
            if (saveSkillTree(nbtConfig, petType)) {
                savedPetTypes.add(petType);
            }
        }
        return savedPetTypes;
    }

    protected boolean saveSkillTree(ConfigurationNBT nbtConfiguration, String petTypeName) {
        boolean saveMobType = false;

        SkillTreeMobType mobType;
        if (petTypeName.equalsIgnoreCase("default")) {
            mobType = SkillTreeMobType.DEFAULT;
        } else if (SkillTreeMobType.hasMobType(MyPetType.byName(petTypeName))) {
            mobType = SkillTreeMobType.getMobTypeByName(petTypeName);
        } else {
            return false;
        }
        if (mobType.getSkillTreeNames().size() != 0) {
            mobType.cleanupPlaces();

            List<TagCompound> skilltreeList = new ArrayList<>();
            for (SkillTree skillTree : mobType.getSkillTrees()) {
                TagCompound skilltreeCompound = new TagCompound();
                skilltreeCompound.getCompoundData().put("Name", new TagString(skillTree.getName()));
                skilltreeCompound.getCompoundData().put("Place", new TagInt(mobType.getSkillTreePlace(skillTree)));
                if (skillTree.hasInheritance()) {
                    skilltreeCompound.getCompoundData().put("Inherits", new TagString(skillTree.getInheritance()));
                }
                if (skillTree.hasCustomPermissions()) {
                    skilltreeCompound.getCompoundData().put("Permission", new TagString(skillTree.getPermission()));
                }
                if (skillTree.hasDisplayName()) {
                    skilltreeCompound.getCompoundData().put("Display", new TagString(skillTree.getDisplayName()));
                }
                if (skillTree.getMaxLevel() > 0) {
                    skilltreeCompound.getCompoundData().put("MaxLevel", new TagInt(skillTree.getMaxLevel()));
                }
                if (skillTree.getRequiredLevel() > 1) {
                    skilltreeCompound.getCompoundData().put("RequiredLevel", new TagInt(skillTree.getRequiredLevel()));
                }
                if (skillTree.getDescription().size() > 0) {
                    List<TagString> descriptionTagList = new ArrayList<>();
                    for (String line : skillTree.getDescription()) {
                        descriptionTagList.add(new TagString(line));
                    }
                    skilltreeCompound.getCompoundData().put("Description", new TagList(descriptionTagList));
                }
                skilltreeCompound.getCompoundData().put("IconItem", skillTree.getIconItem());

                List<TagCompound> levelList = new ArrayList<>();
                for (SkillTreeLevel level : skillTree.getLevelList()) {
                    TagCompound levelCompound = new TagCompound();
                    levelCompound.getCompoundData().put("Level", new TagInt(level.getLevel()));
                    if (level.hasLevelupMessage()) {
                        levelCompound.getCompoundData().put("Message", new TagString(level.getLevelupMessage()));
                    }

                    List<TagCompound> skillList = new ArrayList<>();
                    for (SkillInfo skill : skillTree.getLevel(level.getLevel()).getSkills()) {
                        if (!skill.isAddedByInheritance()) {
                            TagCompound skillCompound = new TagCompound();
                            skillCompound.getCompoundData().put("Name", new TagString(skill.getName()));
                            skillCompound.getCompoundData().put("Properties", skill.getProperties());

                            skillList.add(skillCompound);
                        }
                    }
                    levelCompound.getCompoundData().put("Skills", new TagList(skillList));
                    levelList.add(levelCompound);
                }
                skilltreeCompound.getCompoundData().put("Level", new TagList(levelList));
                skilltreeList.add(skilltreeCompound);
            }
            nbtConfiguration.getNBTCompound().getCompoundData().put("Skilltrees", new TagList(skilltreeList));

            if (mobType.getSkillTreeNames().size() > 0) {
                nbtConfiguration.save();
                saveMobType = true;
            }
        }
        return saveMobType;
    }
}
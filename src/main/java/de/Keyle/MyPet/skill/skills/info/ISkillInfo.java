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

package de.Keyle.MyPet.skill.skills.info;

import de.Keyle.MyPet.gui.skilltreecreator.skills.SkillPropertiesPanel;
import de.keyle.knbt.TagCompound;

public interface ISkillInfo {
    public SkillPropertiesPanel getGuiPanel();

    public void setProperties(TagCompound propertiesCompound);

    public TagCompound getProperties();

    public void setDefaultProperties();

    public boolean isAddedByInheritance();

    public void setIsInherited(boolean flag);

    public ISkillInfo cloneSkill();

    public String getName();

    public String getName(String locale);
}
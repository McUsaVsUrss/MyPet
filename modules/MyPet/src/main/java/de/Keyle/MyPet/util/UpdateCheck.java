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

package de.Keyle.MyPet.util;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.MyPetVersion;
import de.Keyle.MyPet.api.Util;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UpdateCheck {
    public static String checkForUpdate(String plugin) {
        try {
            String parameter = "";
            parameter += "plugin=" + plugin;
            parameter += "&package=" + MyPetApi.getCompatUtil().getInternalVersion();
            parameter += "&build=" + MyPetVersion.getBuild();

            // no data will be saved on the server
            String content = Util.readUrlContent("http://update.mypet.keyle.de/check.php?" + parameter);
            JSONParser parser = new JSONParser();
            JSONObject result = (JSONObject) parser.parse(content);

            if (result.containsKey("latest")) {
                return result.get("latest").toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
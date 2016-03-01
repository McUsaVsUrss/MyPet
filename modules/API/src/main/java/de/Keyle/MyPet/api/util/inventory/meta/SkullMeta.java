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

package de.Keyle.MyPet.api.util.inventory.meta;

import com.google.common.io.BaseEncoding;
import de.keyle.knbt.TagCompound;
import de.keyle.knbt.TagList;
import de.keyle.knbt.TagString;
import org.json.simple.JSONObject;

public class SkullMeta implements IconMeta {
    String name = null;
    String texture = null;

    /**
     * Gets the owner of the skull.
     *
     * @return the owner if the skull
     */
    public String getOwner() {
        return name;
    }

    /**
     * Checks to see if the skull has an owner.
     *
     * @return true if the skull has an owner
     */
    public boolean hasOwner() {
        return name != null;
    }

    /**
     * Sets the owner of the skull.
     * <p/>
     * Plugins should check that hasOwner() returns true before calling this
     * plugin.
     *
     * @param name the new owner of the skull
     * @return true if the owner was successfully set
     */
    public boolean setOwner(String name) {
        if (name != null && name.length() > 16) {
            return false;
        } else {
            this.name = name;
            return true;
        }
    }

    /**
     * Gets the texture URL of the skull.
     *
     * @return the texture URL if the skull
     */
    public String getTexture() {
        return texture;
    }

    /**
     * Checks to see if the skull has a texture URL.
     *
     * @return true if the skull has a texture URL
     */
    public boolean hasTexture() {
        return texture != null;
    }

    /**
     * Sets the texture URL of the skull.
     * <p/>
     * Plugins should check that hasTexture() returns true before calling this
     * plugin.
     *
     * @param url the URL to the texture of the skull
     * @return true if the URL was successfully set
     */
    public boolean setTexture(String url) {
        if (hasOwner()) {
            this.texture = url;
            return true;
        }
        return false;
    }

    public void applyTo(TagCompound tag) {
        if (hasOwner()) {
            TagCompound ownerTag = new TagCompound();


            ownerTag.put("Name", new TagString(getOwner()));

            if (hasTexture()) {
                TagCompound propertiesTag = new TagCompound();
                TagList textureList = new TagList();
                TagCompound textureTag = new TagCompound();
                JSONObject jsonObject = new JSONObject();
                JSONObject texturesObject = new JSONObject();
                JSONObject skinObject = new JSONObject();
                jsonObject.put("textures", texturesObject);
                texturesObject.put("SKIN", skinObject);
                skinObject.put("url", getTexture());
                String base64 = BaseEncoding.base64Url().encode(jsonObject.toJSONString().getBytes());
                textureTag.put("Value", new TagString(base64));
                textureList.addTag(textureTag);
                propertiesTag.put("textures", textureList);
                ownerTag.put("Properties", propertiesTag);
            }

            tag.put("SkullOwner", ownerTag);
        }
    }
}

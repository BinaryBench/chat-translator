package me.binarybench.chattranslator.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Bench on 5/20/2016.
 */
public class LanguageUtil {

    public static boolean isSteveSkin(Player player)
    {
        String profile = new String(Base64.decodeBase64(((CraftPlayer) player).getHandle().getProfile().getProperties().get("textures").iterator().next().getValue()));
        JsonObject jsonObject = new JsonParser().parse(profile).getAsJsonObject();
        boolean steve = true;
        try
        {
            steve = !jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").getAsJsonObject("metadata").get("model").getAsString().equals("slim");
        }
        catch (NullPointerException ignored)
        {

        }
        return steve;
    }

}

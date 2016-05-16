package me.binarybench.chattranslator.storage;

import me.binarybench.chattranslator.api.LangStorage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Bench on 5/16/2016.
 */
public class YamlLangStorage implements LangStorage {
    private File saveFile;
    private YamlConfiguration yamlConfiguration;

    public YamlLangStorage(File saveFile)
    {
        this.saveFile = saveFile;
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(saveFile);
    }

    public String getLang(UUID playersUUID) {
        return yamlConfiguration.getString(playersUUID.toString());
    }

    public void setLang(UUID playersUUID, String lang)
    {
        yamlConfiguration.set(playersUUID.toString(), lang);
        try {
            yamlConfiguration.save(saveFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}

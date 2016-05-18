package me.binarybench.chattranslator;

import me.binarybench.chattranslator.api.Lang;
import me.binarybench.chattranslator.api.LangStorage;
import me.binarybench.chattranslator.api.TranslateModule;
import me.binarybench.chattranslator.commands.LangCommand;
import me.binarybench.chattranslator.listeners.ChatListener;
import me.binarybench.chattranslator.message.TranslateMessage;
import me.binarybench.chattranslator.storage.DudLangStorage;
import me.binarybench.chattranslator.storage.MySqlStorage;
import me.binarybench.chattranslator.storage.YamlLangStorage;
import me.binarybench.chattranslator.translator.GoogleAppsTranslator;
import me.binarybench.chattranslator.translator.TranslatorManager;
import me.binarybench.chattranslator.translator.UnsupportedLanguageException;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bench on 5/13/2016.
 */
public class ChatTranslator extends JavaPlugin implements Listener {

    private volatile TranslateModule translateModule;

    @Override
    public void onEnable() {


        //Config
        getConfig().options().copyDefaults(true);
        saveConfig();

        String stringDefaultLang = getConfig().getString("default-language");
        Lang defaultLang;
        if ((defaultLang = Lang.getLang(stringDefaultLang)) == null)
            defaultLang = Lang.ENGLISH;

        //Storage
        LangStorage langStorage = new MySqlStorage();


        /*File yamlFile = new File(getDataFolder() + File.separator + "languages.yml");

        try
        {
            if (yamlFile.getParentFile().mkdirs() && yamlFile.createNewFile())
                System.out.printf("Language file not found, creating: %s\n", yamlFile.getName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.langStorage = new YamlLangStorage(yamlFile);
        */

        translateModule = new TranslateModule(this, new GoogleAppsTranslator(), langStorage, defaultLang);


        //Commands
        getCommand("language").setExecutor(new LangCommand(translateModule));


        //Listeners
        Bukkit.getPluginManager().registerEvents(new ChatListener(translateModule), this);

    }

    public TranslateModule getTranslateModule()
    {
        return translateModule;
    }
}

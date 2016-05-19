package me.binarybench.chattranslator;

import me.binarybench.chattranslator.api.Lang;
import me.binarybench.chattranslator.api.LangStorage;
import me.binarybench.chattranslator.api.TranslateModule;
import me.binarybench.chattranslator.commands.LangCommand;
import me.binarybench.chattranslator.listeners.ChatListener;
import me.binarybench.chattranslator.storage.MySqlStorage;
import me.binarybench.chattranslator.storage.YamlLangStorage;
import me.binarybench.chattranslator.translator.GoogleAppsTranslator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Bench on 5/13/2016.
 */
public class ChatTranslator extends JavaPlugin implements Listener {

    private volatile TranslateModule translateModule;


    // Config
    public static final String DEFAULT_LANG = "DefaultLanguage";

    public static final String CONFIG_ENABLED = "enabled";

    // YAML Config
    public static final String YAML_SECTION = "YAML";
    public static final String YAML_ENABLED = YAML_SECTION + "." + CONFIG_ENABLED;
    public static final String YAML_FILE_NAME = YAML_SECTION + ".fileName";

    // MySQL Config
    public static final String MYSQL_SECTION = "MySQL";
    public static final String MYSQL_ENABLED = MYSQL_SECTION + "." + CONFIG_ENABLED;
    public static final String MYSQL_URL = MYSQL_SECTION + ".url";
    public static final String MYSQL_USER = MYSQL_SECTION + ".user";
    public static final String MYSQL_PASS = MYSQL_SECTION + ".password";



    @Override
    public void onEnable()
    {


        //Config
        //getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();

        String stringDefaultLang = getConfig().getString(DEFAULT_LANG);

        Lang defaultLang;
        if ((defaultLang = Lang.getLang(stringDefaultLang)) == null)
            defaultLang = Lang.ENGLISH;

        //YAML - config
        boolean yamlEnabled = (Boolean) getConfig().get(YAML_ENABLED);
        String yamlFileName = (String) getConfig().get(YAML_FILE_NAME);

        //MySQL - config
        boolean mySQLEnabled = (Boolean) getConfig().get(MYSQL_ENABLED);
        String mySQLurl = (String) getConfig().get(MYSQL_URL);
        String mySQLUser = (String) getConfig().get(MYSQL_USER);
        String mySQLPass = (String) getConfig().get(MYSQL_PASS);


        //Storage
        LangStorage langStorage = null;

        if (mySQLEnabled)
            langStorage = new MySqlStorage(mySQLurl, mySQLUser, mySQLPass);
        else if (yamlEnabled)
            langStorage = new YamlLangStorage(new File(getDataFolder() + File.separator + yamlFileName));


        translateModule = new TranslateModule(this, new GoogleAppsTranslator(), langStorage, defaultLang);


        //Commands
        getCommand("language").setExecutor(new LangCommand(translateModule));


        //Listeners
        Bukkit.getPluginManager().registerEvents(new ChatListener(translateModule), this);

    }


    public static void printConfigurationSection(ConfigurationSection con)
    {

        for (String key : con.getKeys(true))
        {
            System.out.printf("%s: %s", key, con.get(key).toString());
        }


    }


    public TranslateModule getTranslateModule()
    {
        return translateModule;
    }
}

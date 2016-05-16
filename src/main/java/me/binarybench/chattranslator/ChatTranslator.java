package me.binarybench.chattranslator;

import me.binarybench.chattranslator.api.Lang;
import me.binarybench.chattranslator.api.LangStorage;
import me.binarybench.chattranslator.commands.LangCommand;
import me.binarybench.chattranslator.listeners.ChatListener;
import me.binarybench.chattranslator.message.TranslateMessage;
import me.binarybench.chattranslator.storage.DudLangStorage;
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
    //Static
    public static final String PRE = "<t>";
    public static final String POST = "<\\t>";
    public static final Pattern MATCH_VARIABLE =  Pattern.compile("(.*?)" + Pattern.quote(PRE) + "(.*?)" + Pattern.quote(POST));

    private ConcurrentHashMap<Player, Lang> langs;
    private volatile Lang defaultLang = Lang.ENGLISH;

    private TranslatorManager translator;
    private ExecutorService threadPool;

    private LangStorage langStorage;

    @Override
    public void onEnable() {
        //Init var
        this.threadPool = Executors.newCachedThreadPool();
        this.translator = new TranslatorManager(getThreadPool(), new GoogleAppsTranslator());
        langs = new ConcurrentHashMap<Player, Lang>();

        //Config
        getCommand("language").setExecutor(new LangCommand(this));
        getConfig().options().copyDefaults(true);
        saveConfig();

        String stringDefaultLang = getConfig().getString("default-language", defaultLang.getId());
        this.defaultLang = Lang.getLang(stringDefaultLang);

        //Storage
        File yamlFile = new File(getDataFolder() + File.separator + "languages.yml");
        yamlFile.getParentFile().mkdirs();
        if (!yamlFile.exists()) {
            try
            {
                yamlFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        this.langStorage = new YamlLangStorage(yamlFile);

        //Listeners
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        loadLang(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        langs.remove(event.getPlayer());
    }


    public Lang getLang(Player player)
    {
        Lang lang;
        return (lang = langs.get(player)) == null ? defaultLang : lang;
    }

    public boolean setLang(final Player player, final Lang lang)
    {
        if (lang == null)
            return false;

        if (lang.equals(Lang.DETECT_LANG))
            return false;

        getThreadPool().execute(new Runnable() {
            public void run() {
                getLangStorage().setLang(player.getUniqueId(), lang.getId());
            }
        });

        langs.put(player, lang);
        return true;
    }

    public void loadLang(final Player player)
    {
        final UUID playersUUID = player.getUniqueId();
        final Plugin plugin = this;

        getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    String stringLang = getLangStorage().getLang(playersUUID);

                    if (stringLang == null)
                        return;

                    final Lang lang = Lang.getLang(stringLang);

                    if (lang == null)
                        throw new UnsupportedLanguageException(stringLang);

                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        public void run() {
                            langs.put(player, lang);
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        });

    }







    public static String t(String translatedText)
    {
        return PRE + translatedText + POST;
    }

    public void broadcastMessage(String sourceLang, String message, Collection<? extends Player> players)
    {
        // example message:  "This is not translated. <t>This is translated<\t>"

        Matcher matcher = MATCH_VARIABLE.matcher(message);

        TranslateMessage translateMessage = new TranslateMessage(sourceLang, this, players);

        while (matcher.find())
        {
            String nonTranslated = matcher.group(1);
            String translated = matcher.group(2);

            if (!nonTranslated.equals(""))
            {
                translateMessage.addText(nonTranslated);
            }


            if (!translated.equals(""))
            {
                translateMessage.addTranslated(translated);
            }

        }
        //StringBuffer end = new StringBuffer();

        //matcher.appendTail(end);
        //if (!end.toString().equals(""))
            //translateMessage.addText(end.toString());
        translateMessage.send();
    }

    public TranslatorManager getTranslator() {
        return translator;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public LangStorage getLangStorage() {
        return langStorage;
    }
}

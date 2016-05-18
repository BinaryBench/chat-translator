package me.binarybench.chattranslator.api;

import me.binarybench.chattranslator.commands.LangCommand;
import me.binarybench.chattranslator.listeners.ChatListener;
import me.binarybench.chattranslator.message.TranslateMessage;
import me.binarybench.chattranslator.storage.MySqlStorage;
import me.binarybench.chattranslator.translator.GoogleAppsTranslator;
import me.binarybench.chattranslator.translator.TranslatorManager;
import me.binarybench.chattranslator.translator.UnsupportedLanguageException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import sun.print.PSPrinterJob;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bench on 5/18/2016.
 */
public class TranslateModule implements Listener {
    //Static
    public static final String PRE = "<t>";
    public static final String POST = "<\\t>";
    public static final Pattern MATCH_VARIABLE =  Pattern.compile("(.*?)" + Pattern.quote(PRE) + "(.*?)" + Pattern.quote(POST));

    private Plugin plugin;

    private ConcurrentHashMap<Player, Lang> langs;
    private volatile Lang defaultLang;

    private ExecutorService threadPool;

    private TranslatorManager translator;
    private volatile LangStorage langStorage;

    public TranslateModule(Plugin plugin, Translator translator, LangStorage langStorage, Lang defaultLang)
    {
        this(plugin, Executors.newCachedThreadPool(), translator, langStorage, defaultLang);
    }

    public TranslateModule(Plugin plugin, ExecutorService threadPool, Translator translator, LangStorage langStorage, Lang defaultLang)
    {
        this(plugin, threadPool, new TranslatorManager(threadPool, translator), langStorage, defaultLang);
    }

    public TranslateModule(Plugin plugin, ExecutorService threadPool, TranslatorManager translatorManager, LangStorage langStorage, Lang defaultLang)
    {
        //Init vars
        this.langs = new ConcurrentHashMap<Player, Lang>();
        this.plugin = plugin;
        this.threadPool = threadPool;
        this.translator = translatorManager;
        this.defaultLang = defaultLang;
        this.langStorage = langStorage;
        Bukkit.getPluginManager().registerEvents(this, plugin);


        //Load langs for players already online
        for (Player player : Bukkit.getOnlinePlayers())
            this.loadLang(player);
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

    public Plugin getPlugin()
    {
        return plugin;
    }
}

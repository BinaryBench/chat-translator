package me.binarybench.chattranslator;

import me.binarybench.chattranslator.api.Lang;
import me.binarybench.chattranslator.commands.LangCommand;
import me.binarybench.chattranslator.listeners.ChatListener;
import me.binarybench.chattranslator.message.TranslateMessage;
import me.binarybench.chattranslator.translator.GoogleAppsTranslator;
import me.binarybench.chattranslator.translator.TranslatorManager;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bench on 5/13/2016.
 */
public class ChatTranslator extends JavaPlugin {

    public static final String PRE = "<t>";
    public static final String POST = "<\\t>";
    public static final Pattern MATCH_VARIABLE =  Pattern.compile("(.*?)" + Pattern.quote(PRE) + "(.*?)" + Pattern.quote(POST));

    private ConcurrentHashMap<Player, Lang> langs;
    private volatile Lang defaultLang = Lang.ENGLISH;

    private TranslatorManager translator;
    private ExecutorService threadPool;

    @Override
    public void onEnable() {
        //Init var
        this.threadPool = Executors.newCachedThreadPool();
        this.translator = new TranslatorManager(getThreadPool(), new GoogleAppsTranslator());
        langs = new ConcurrentHashMap<Player, Lang>();

        //Config
        getCommand("language").setExecutor(new LangCommand(this));
        getConfig().options().copyDefaults(true);

        String stringDefaultLang = getConfig().getString("default-language", defaultLang.getId());
        this.defaultLang = Lang.getLang(stringDefaultLang);


        //Listeners
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);

    }

    public Lang getLang(Player player)
    {
        Lang lang;
        return (lang = langs.get(player)) == null ? defaultLang : lang;
    }

    public boolean setLang(Player player, Lang lang)
    {
        if (lang == null)
            return false;

        if (lang.equals(Lang.DETECT_LANG))
            return false;
        langs.put(player, lang);
        return true;
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
}

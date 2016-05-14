package me.binarybench.chattranslator;

import me.binarybench.chattranslator.api.Lang;
import me.binarybench.chattranslator.commands.LangCommand;
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
public class ChatTranslator extends JavaPlugin implements Listener {

    public static final String PRE = "<t>";
    public static final String POST = "<\\t>";
    public static final Pattern MATCH_VARIABLE =  Pattern.compile("(.*?)" + Pattern.quote(PRE) + "(.*?)" + Pattern.quote(POST));

    private ConcurrentHashMap<Player, Lang> langs;
    private volatile Lang defaultLang = Lang.ENGLISH;

    private TranslatorManager translator;
    private ExecutorService threadPool;

    @Override
    public void onEnable() {
        this.threadPool = Executors.newCachedThreadPool();

        this.translator = new TranslatorManager(getThreadPool(), new GoogleAppsTranslator());

        langs = new ConcurrentHashMap<Player, Lang>();

        getCommand("language").setExecutor(new LangCommand(this));

        getConfig().options().copyDefaults(true);

        String stringDefaultLang = getConfig().getString("default-language", defaultLang.getId());
        this.defaultLang = Lang.getLang(stringDefaultLang);

        Bukkit.getPluginManager().registerEvents(this, this);
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event)
    {
        event.setCancelled(true);

        broadcastMessage(getLang(event.getPlayer()).getId(), formatChat(event.getFormat(), event.getPlayer().getName(), PRE + event.getMessage() + POST), event.getRecipients());

    }

    public static String formatChat(String format, String name, String message)
    {

        return String.format(format, name, message);
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

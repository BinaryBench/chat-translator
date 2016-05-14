package me.binarybench.chattranslator.message;

import javafx.scene.control.Cell;
import me.binarybench.chattranslator.ChatTranslator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by Bench on 5/13/2016.
 */
public class TranslateMessage {


    private ChatTranslator plugin;
    private String sourceLang;

    private Collection<? extends Player> players;

    private List<Text> texts;


    public TranslateMessage(String sourceLang, ChatTranslator plugin, Collection<? extends Player> players)
    {
        this.sourceLang = sourceLang;
        this.plugin = plugin;
        this.players = players;
        this.texts = new ArrayList<Text>();
    }

    public TranslateMessage addText(String text)
    {
        texts.add(new Text(sourceLang, text, false));
        return this;
    }

    public TranslateMessage addTranslated(String text)
    {
        texts.add(new Text(sourceLang, text, true));
        return this;
    }


    public void send()
    {
        getPlugin().getThreadPool().execute(new Runnable() {
            public void run() {
                Map<String, Set<Player>> langs = new HashMap<String, Set<Player>>();

                for (Player player : players) {
                    String lang = getPlugin().getLang(player).getId();
                    if (!langs.containsKey(lang))
                        langs.put(lang, new HashSet<Player>());
                    langs.get(lang).add(player);
                }

                //TODO Optimize this a bit

                for (String lang : langs.keySet())
                {
                    for (Text text : texts)
                    {
                        text.translate(lang, getPlugin().getTranslator());
                    }
                }

                for (Map.Entry<String, Set<Player>> entry : langs.entrySet())
                {
                    StringBuilder builder = new StringBuilder();

                    try {
                        for (Text text : texts)
                        {
                            builder.append(text.getText(entry.getKey()));
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        System.err.println("An error occurred while translating the message to: " + entry.getKey() + "!");
                        builder = new StringBuilder(ChatColor.RED + "An error occurred while translating the message");
                    }

                    for (Player player : entry.getValue())
                        player.sendMessage(builder.toString());
                }
            }
        });

    }


    public String getSourceLang()
    {
        return sourceLang;
    }

    public ChatTranslator getPlugin()
    {
        return plugin;
    }
}
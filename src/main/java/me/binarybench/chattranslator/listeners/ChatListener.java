package me.binarybench.chattranslator.listeners;

import me.binarybench.chattranslator.ChatTranslator;
import me.binarybench.chattranslator.api.TranslateModule;
import me.binarybench.chattranslator.message.TranslateMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Bench on 5/14/2016.
 */
public class ChatListener implements Listener {

    private TranslateModule plugin;

    public ChatListener(TranslateModule plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event)
    {
        event.setCancelled(true);

        plugin.broadcastMessage(
                plugin.getLang(event.getPlayer()).getId(),
                formatChat(
                        event.getFormat(), event.getPlayer().getName(),
                        TranslateModule.t(event.getMessage())),
                event.getRecipients());

    }

    public static String formatChat(String format, String name, String message)
    {
        return String.format(format, name, message);
    }

}

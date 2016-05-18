package me.binarybench.chattranslator.commands;

import me.binarybench.chattranslator.ChatTranslator;
import me.binarybench.chattranslator.api.Lang;
import me.binarybench.chattranslator.api.TranslateModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Bench on 5/13/2016.
 */
public class LangCommand implements CommandExecutor {

    private TranslateModule plugin;

    public LangCommand(TranslateModule plugin) {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args)
    {

        if (!(sender instanceof Player))
        {
            sender.sendMessage("This command is only for Players!");
            return true;
        }
        Player player = (Player) sender;

        if (args.length <= 0)
        {


            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Lang lang : Lang.values())
            {
                if (lang.equals(Lang.DETECT_LANG))
                    continue;
                if (first)
                    first = false;
                else
                    sb.append(", ");
                sb.append(lang.getDisplayName());
            }

            player.sendMessage(sb.toString());

            return true;
        }

        if (args.length >= 1)
        {
            Lang lang = Lang.getLang(args[0]);

            if (lang != null && getPlugin().setLang(player, lang))
            {
                player.sendMessage("Language set to: " + lang.getDisplayName());
            }
            else
            {
                player.sendMessage("Unsupported Lang: " + args[0]);
            }
            return true;
        }

        return false;
    }


    public TranslateModule getPlugin() {
        return plugin;
    }
}

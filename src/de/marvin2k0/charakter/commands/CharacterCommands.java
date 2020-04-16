package de.marvin2k0.charakter.commands;

import de.marvin2k0.charakter.Charakter;
import de.marvin2k0.charakter.CharakterPlugin;
import de.marvin2k0.charakter.listeners.CharakterListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CharacterCommands implements CommandExecutor
{
    private CharakterPlugin cp = CharakterPlugin.getCp();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cNur fuer Spieler!");

            return true;
        }

        Player player = (Player) sender;

        if (!(args.length <= 3) && args.length != 0)
        {
            player.sendMessage("help");

            return true;
        }

        if (args[0].equalsIgnoreCase("create"))
        {
            if (!player.hasPermission("character.create"))
            {
                player.sendMessage("§cDazu bist du nicht berechtigt!");

                return true;
            }

            Charakter charakter = cp.getCharakter(player);
            CharakterListener cl = new CharakterListener();

            CharakterPlugin.getCp().openCharakterInv(player);

            if (!cl.getErstellung().contains(player))
                cl.getErstellung().add(player);

            return true;
        }

        return true;
    }
}

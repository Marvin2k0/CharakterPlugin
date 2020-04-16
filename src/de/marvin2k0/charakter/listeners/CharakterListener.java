package de.marvin2k0.charakter.listeners;

import de.marvin2k0.charakter.Charakter;
import de.marvin2k0.charakter.CharakterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CharakterListener implements Listener
{
    private ArrayList<Player> vorname = new ArrayList<>();
    private ArrayList<Player> nachname = new ArrayList<>();
    private ArrayList<Player> alter = new ArrayList<>();
    private ArrayList<Player> erstellung = new ArrayList<>();

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();

        if (inventory != null && inventory.getName().equals("§lDaten") && item != null)
        {
            String name = item.getItemMeta().getDisplayName();

            if (name.equals("§7§oVorname"))
            {
                player.sendMessage("§aBitte gebe deinen fiktiven §9Vornamen §aein!");
                vorname.add(player);

                player.closeInventory();

                return;
            }
            else if (name.equals("§7§oNachname"))
            {
                player.sendMessage("§aBitte gebe deinen fiktiven §9Nachnamen §aein!");
                nachname.add(player);

                player.closeInventory();

                return;
            }
            else if (name.equals("§7§oAlter"))
            {
                player.sendMessage("§aBitte gebe dein fiktives §9Alter §aein!");
                alter.add(player);

                player.closeInventory();

                return;
            }
            else if (name.equals("§aBestätigen"))
            {
                erstellung.remove(player);

            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event)
    {
        if (!CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).isValid() && event.getInventory().getName().equals("§lDaten") && (!vorname.contains(event.getPlayer()) && !nachname.contains(event.getPlayer()) && !alter.contains(event.getPlayer())))
        {
            Bukkit.getScheduler().runTaskLater(CharakterPlugin.getCp(), new Runnable()
            {
                @Override
                public void run()
                {
                    CharakterPlugin.getCp().openCharakterInv((Player) event.getPlayer());
                }
            }, 5);
        }

        if (event.getInventory().getName().equals("§lDaten") && CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).isValid())
        {
            if (!CharakterPlugin.getCp().exists(CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).getName()))
            {
                erstellung.remove(event.getPlayer());
                ((Player) event.getPlayer()).setDisplayName(CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).getName());
                ((Player) event.getPlayer()).setPlayerListName(CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).getName());
                ((Player) event.getPlayer()).sendMessage("§aWillkommen, §9" + CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).getName());

                List<String> names = CharakterPlugin.getCp().getConfig().getStringList("names");
                names.add(CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).getName());
                CharakterPlugin.getCp().getConfig().set("names", names);

                CharakterPlugin.getCp().saveConfig();
            }
            else
            {
                File file = new File(CharakterPlugin.getCp().getDataFolder().getPath() + "/chars/" + event.getPlayer().getUniqueId() + ".yml");
                file.delete();

                event.getPlayer().sendMessage("§cBitte wähle einen anderen Namen! Der Charakter existiert bereits!");

                Bukkit.getScheduler().runTaskLater(CharakterPlugin.getCp(), new Runnable()
                {
                    @Override
                    public void run()
                    {
                        CharakterPlugin.getCp().openCharakterInv((Player) event.getPlayer());
                    }
                }, 5);

            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        if (!CharakterPlugin.getCp().getCharakter(event.getPlayer()).isValid())
            event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        event.setJoinMessage("");

        Charakter charakter = CharakterPlugin.getCp().getCharakter(player);

        if (!charakter.isValid())
        {
            Bukkit.getScheduler().runTaskLater(CharakterPlugin.getCp(), new Runnable()
            {
                @Override
                public void run()
                {
                    CharakterPlugin.getCp().openCharakterInv(player);

                    if (!erstellung.contains(player))
                        erstellung.add(player);
                }
            }, 5);

            return;
        }

        if (charakter.getAlter() != 0 && !charakter.getVorname().isEmpty() && !charakter.getNachname().isEmpty())
        {
            player.sendMessage("Hallo, " + charakter.getName());
            player.setDisplayName(charakter.getName());
            player.setPlayerListName(charakter.getName());

            return;
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();

        if (vorname.contains(player))
        {
            CharakterPlugin.getCp().getCharakter(player).setVorname(event.getMessage());
            vorname.remove(player);

            event.setCancelled(true);

            CharakterPlugin.getCp().openCharakterInv(player);
        }
        else if (nachname.contains(player))
        {
            CharakterPlugin.getCp().getCharakter(player).setNachname(event.getMessage());
            nachname.remove(player);

            event.setCancelled(true);

            CharakterPlugin.getCp().openCharakterInv(player);
        }
        else if (alter.contains(player))
        {
            try
            {
                CharakterPlugin.getCp().getCharakter(player).setAlter(Integer.valueOf(event.getMessage()));
            }
            catch(Exception e)
            {
                player.sendMessage("§cNur Zahlen eingeben!");
                alter.add(player);
            }

            alter.remove(player);
            CharakterPlugin.getCp().openCharakterInv(player);
            event.setCancelled(true);
        }
    }
}

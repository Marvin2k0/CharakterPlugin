package de.marvin2k0.charakter.listeners;

import de.marvin2k0.charakter.Charakter;
import de.marvin2k0.charakter.CharakterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

            if (!erstellung.contains(player) && item.getItemMeta().getDisplayName() != "§cAbbrechen")
                erstellung.add(player);

            if (!CharakterPlugin.getCp().characters.containsKey(player))
            {
                CharakterPlugin.getCp().characters.put(player, CharakterPlugin.getCp().getCharakter(player));
                System.out.println("eingefügt ");
            }

            if (name.contains("§7§oVorname§f"))
            {
                player.sendMessage("§aBitte gebe deinen fiktiven §9Vornamen §aein!");
                vorname.add(player);

                player.closeInventory();

                return;
            }
            else if (name.startsWith("§7§oNachname§f"))
            {
                player.sendMessage("§aBitte gebe deinen fiktiven §9Nachnamen §aein!");
                nachname.add(player);

                player.closeInventory();

                return;
            }
            else if (name.startsWith("§7§oAlter§f"))
            {
                player.sendMessage("§aBitte gebe dein fiktives §9Alter §aein!");
                alter.add(player);

                player.closeInventory();

                return;
            }
            else if (name.equals("§aBestätigen"))
            {
                erstellung.remove(player);

                Charakter charakter = CharakterPlugin.getCp().characters.get(player);
                charakter.saveConfig();

                System.out.println(charakter.toString());

                CharakterPlugin.getCp().characters.remove(player);

                player.sendMessage("§aDu heißt nun: §9" + CharakterPlugin.getCp().getCharakter(player).getName());
                player.closeInventory();
            }
            else if (name == "§cAbbrechen")
            {
                System.out.println(player.getName() + " beendet inv");

                if (erstellung.contains(player))
                {
                    erstellung.remove(player);
                }

                CharakterPlugin.getCp().characters.remove(player);

                if (vorname.contains(player))
                    vorname.remove(player);

                if (nachname.contains(player))
                    nachname.remove(player);

                if (alter.contains(player))
                    alter.remove(player);

                player.setDisplayName(player.getDisplayName());
                player.setPlayerListName(player.getDisplayName());

                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event)
    {
        if (erstellung.contains(event.getPlayer()) && !CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).isValid() && event.getInventory().getName().equals("§lDaten") && (!vorname.contains(event.getPlayer()) && !nachname.contains(event.getPlayer()) && !alter.contains(event.getPlayer())))
        {
            //CharakterPlugin.getCp().openCharakterInv((Player) event.getPlayer());

            System.out.println("0");
            Bukkit.getScheduler().runTaskLater(CharakterPlugin.getCp(), new Runnable()
            {
                @Override
                public void run()
                {
                    CharakterPlugin.getCp().openCharakterInv((Player) event.getPlayer());
                }
            }, 5);
        }

        if (!vorname.contains(event.getPlayer()) && !alter.contains(event.getPlayer()) && !nachname.contains(event.getPlayer()) && event.getInventory().getName().equals("§lDaten") && CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).isValid())
        {
            System.out.println("1");

            if (!CharakterPlugin.getCp().exists(CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).getName()))
            {
                CharakterPlugin.getCp().characters.remove((Player) event.getPlayer());
                System.out.println("2");
                erstellung.remove(event.getPlayer());
                ((Player) event.getPlayer()).setDisplayName(CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).getName());
                ((Player) event.getPlayer()).setPlayerListName(CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).getName());

                List<String> names = CharakterPlugin.getCp().getConfig().getStringList("names");
                names.add(CharakterPlugin.getCp().getCharakter((Player) event.getPlayer()).getName() + "_" + event.getPlayer().getUniqueId().toString());
                CharakterPlugin.getCp().getConfig().set("names", names);

                CharakterPlugin.getCp().saveConfig();
            }
            else
            {
                String uuid = event.getPlayer().getUniqueId().toString();

                for (String str : CharakterPlugin.getCp().getConfig().getStringList("names"))
                {
                    if (uuid.equals(str.split("_")[1]))
                        return;
                }

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
        if (vorname.contains(event.getPlayer()) || nachname.contains(event.getPlayer()) || alter.contains(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        event.setJoinMessage("");

        Charakter charakter = CharakterPlugin.getCp().getCharakter(player);

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
            //CharakterPlugin.getCp().getCharakter(player).setVorname(event.getMessage());
            Charakter charakter = CharakterPlugin.getCp().characters.get(player);
            CharakterPlugin.getCp().characters.remove(player);
            charakter.setVorname(event.getMessage());
            CharakterPlugin.getCp().characters.put(player, charakter);
            vorname.remove(player);

            event.setCancelled(true);

            CharakterPlugin.getCp().openCharakterInv(player);
        }
        else if (nachname.contains(player))
        {
            //CharakterPlugin.getCp().getCharakter(player).setNachname(event.getMessage());
            Charakter charakter = CharakterPlugin.getCp().characters.get(player);
            CharakterPlugin.getCp().characters.remove(player);
            charakter.setNachname(event.getMessage());
            CharakterPlugin.getCp().characters.put(player, charakter);
            nachname.remove(player);

            event.setCancelled(true);

            CharakterPlugin.getCp().openCharakterInv(player);
        }
        else if (alter.contains(player))
        {
            try
            {
                //CharakterPlugin.getCp().getCharakter(player).setAlter(Integer.valueOf(event.getMessage()));
                Charakter charakter = CharakterPlugin.getCp().characters.get(player);
                CharakterPlugin.getCp().characters.remove(player);
                charakter.setAlter(Integer.valueOf(event.getMessage()));
                CharakterPlugin.getCp().characters.put(player, charakter);

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

    public ArrayList<Player> getErstellung()
    {
        return this.erstellung;
    }
}

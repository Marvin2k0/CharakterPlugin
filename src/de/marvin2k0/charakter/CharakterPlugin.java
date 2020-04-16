package de.marvin2k0.charakter;

import de.marvin2k0.charakter.listeners.CharakterListener;
import de.marvin2k0.guiapi.GuiApi;
import de.marvin2k0.guiapi.GuiInventory;
import de.marvin2k0.guiapi.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class CharakterPlugin extends JavaPlugin
{
    private static CharakterPlugin cp;

    @Override
    public void onEnable()
    {
        GuiApi.setUp(this);
        cp = this;

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getCommand("charakter").setExecutor(this);
        this.getServer().getPluginManager().registerEvents(new CharakterListener(), this);
    }

    public static CharakterPlugin getCp()
    {
        return cp;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cNur fuer Spieler!");

            return true;
        }

        Player player = (Player) sender;




        return true;
    }

    public Charakter getCharakter(Player player)
    {
        File file = new File(this.getDataFolder().getPath() + "/chars/" + player.getUniqueId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (file.exists())
        {
            Charakter charakter = new Charakter(player.getUniqueId(), config.getString("vorname"),
                config.getString("nachname"),
                config.getInt("alter"),
                config.getString("herkunft"));

            return charakter;
        }

        Charakter charakter = new Charakter(player.getUniqueId(), "", "", 0, "");

        return charakter;
    }

    public void openCharakterInv(Player player)
    {
        Inventory content = Bukkit.createInventory(null, 3 * 9, "§lDaten");

        for (int i = 0; i < content.getSize(); i++)
        {
            ItemStack placeholder = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
            ItemMeta placeHolderMeta = placeholder.getItemMeta();
            placeHolderMeta.setDisplayName(" ");
            placeholder.setItemMeta(placeHolderMeta);

            content.setItem(i, placeholder);
        }

        GuiItem guiItemVorname = new GuiItem(GuiItem.GuiItemAction.CLOSE, Material.PAPER, 1, (short) 0);
        ItemMeta itemMetaVorname = guiItemVorname.getItemMeta();
        itemMetaVorname.setDisplayName((getCharakter(player).getVorname().isEmpty() ? "§7§oVorname" : "§9" + getCharakter(player).getVorname() + "-0-CLOSE"));
        guiItemVorname.setItemMeta(itemMetaVorname);
        content.setItem(11, guiItemVorname);

        GuiItem guiItemNachname = new GuiItem(GuiItem.GuiItemAction.CLOSE, Material.PAPER, 1, (short) 0);
        ItemMeta itemMetaNachname = guiItemNachname.getItemMeta();
        itemMetaNachname.setDisplayName((getCharakter(player).getNachname().isEmpty() ? "§7§oNachname" : "§9" + getCharakter(player).getNachname() + "-0-CLOSE"));
        guiItemNachname.setItemMeta(itemMetaNachname);
        content.setItem(12, guiItemNachname);

        GuiItem guiItemAlter = new GuiItem(GuiItem.GuiItemAction.CLOSE, Material.PAPER, 1, (short) 0);
        ItemMeta itemMetaAlter = guiItemAlter.getItemMeta();
        itemMetaAlter.setDisplayName((getCharakter(player).getAlter() == 0 ? "§7§oAlter" : "§9" + getCharakter(player).getAlter() + "-0-CLOSE"));
        guiItemAlter.setItemMeta(itemMetaAlter);
        content.setItem(13, guiItemAlter);

        Charakter c = getCharakter(player);

        if (!c.getVorname().isEmpty() && !c.getNachname().isEmpty() && c.getAlter() != 0)
        {
            GuiItem guiItemBestätigen = new GuiItem(GuiItem.GuiItemAction.CLOSE, Material.EMERALD_BLOCK, 1, (short) 0);
            ItemMeta itemMetaBestätigen = guiItemBestätigen.getItemMeta();
            itemMetaBestätigen.setDisplayName("§aBestätigen-0-CLOSE");
            guiItemBestätigen.setItemMeta(itemMetaBestätigen);
            content.setItem(26, guiItemBestätigen);
        }

        GuiInventory gui = GuiInventory.createInventory(content);

        if (player.getOpenInventory() == null || !player.getOpenInventory().getTitle().equals("§lDaten"))
        {
            player.openInventory(gui.getClearInventory());
            System.out.println("opened inventory for " + player.getName());
        }
    }
}

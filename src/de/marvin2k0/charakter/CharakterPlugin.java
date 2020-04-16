package de.marvin2k0.charakter;

import de.marvin2k0.charakter.commands.CharacterCommands;
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
import java.util.HashMap;

public class CharakterPlugin extends JavaPlugin
{
    private static CharakterPlugin cp;
    private CharakterListener cl = new CharakterListener();
    public HashMap<Player, Charakter> characters = new HashMap<>();

    @Override
    public void onEnable()
    {
        GuiApi.setUp(this);
        cp = this;

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getCommand("charakter").setExecutor(new CharacterCommands());
        this.getServer().getPluginManager().registerEvents(new CharakterListener(), this);
    }

    public static CharakterPlugin getCp()
    {
        return cp;
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

    public boolean exists(String name)
    {
        for (String str : this.getConfig().getStringList("names"))
        {
            if (name.equals(str.split("_")[0]))
                return true;
        }
        return false;
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

        Charakter charakter;

        if (characters.get(player) == null)
        {
            charakter = getCharakter(player);
            System.out.println("istnull ");
        }
        else
        {
            charakter = characters.get(player);
            System.out.println("ist nicht null");
        }

        GuiItem guiItemVorname = new GuiItem(GuiItem.GuiItemAction.CLOSE, !charakter.getVorname().isEmpty() ? Material.EMPTY_MAP : Material.PAPER, 1, (short) 0);
        ItemMeta itemMetaVorname = guiItemVorname.getItemMeta();
        itemMetaVorname.setDisplayName((charakter.getVorname().isEmpty() ? "§7§oVorname§f" : "§7§oVorname§f §9" + charakter.getVorname() + "-0-CLOSE"));
        guiItemVorname.setItemMeta(itemMetaVorname);
        content.setItem(12, guiItemVorname);

        GuiItem guiItemNachname = new GuiItem(GuiItem.GuiItemAction.CLOSE, !charakter.getNachname().isEmpty() ? Material.EMPTY_MAP : Material.PAPER, 1, (short) 0);
        ItemMeta itemMetaNachname = guiItemNachname.getItemMeta();
        itemMetaNachname.setDisplayName((charakter.getNachname().isEmpty() ? "§7§oNachname§f" : "§7§oNachname§f §9" + charakter.getNachname() + "-0-CLOSE"));
        guiItemNachname.setItemMeta(itemMetaNachname);
        content.setItem(13, guiItemNachname);

        GuiItem guiItemAlter = new GuiItem(GuiItem.GuiItemAction.CLOSE, charakter.getAlter() != 0 ? Material.EMPTY_MAP : Material.PAPER, 1, (short) 0);
        ItemMeta itemMetaAlter = guiItemAlter.getItemMeta();
        itemMetaAlter.setDisplayName((charakter.getAlter() == 0 ? "§7§oAlter§f" : "§7§oAlter§f §9" + charakter.getAlter() + "-0-CLOSE"));
        guiItemAlter.setItemMeta(itemMetaAlter);
        content.setItem(14, guiItemAlter);

        GuiItem guiItemCancel = new GuiItem(GuiItem.GuiItemAction.CLOSE, Material.REDSTONE_BLOCK, 1, (short) 1);
        ItemMeta guiItemCancelMeta = guiItemCancel.getItemMeta();
        guiItemCancelMeta.setDisplayName("§cAbbrechen");
        guiItemCancel.setItemMeta(guiItemCancelMeta);
        content.setItem(18, guiItemCancel);

        Charakter c = charakter;

        if (!c.getVorname().isEmpty() && !c.getNachname().isEmpty() && c.getAlter() != 0)
        {
            GuiItem guiItemBestätigen = new GuiItem(GuiItem.GuiItemAction.CLOSE, Material.EMERALD_BLOCK, 1, (short) 0);
            ItemMeta itemMetaBestätigen = guiItemBestätigen.getItemMeta();
            itemMetaBestätigen.setDisplayName("§aBestätigen-0-CLOSE");
            guiItemBestätigen.setItemMeta(itemMetaBestätigen);
            content.setItem(26, guiItemBestätigen);
        }
        else
        {
            GuiItem guiItemBestätigen = new GuiItem(GuiItem.GuiItemAction.CLOSE, Material.STONE, 1, (short) 0);
            ItemMeta itemMetaBestätigen = guiItemBestätigen.getItemMeta();
            itemMetaBestätigen.setDisplayName("§7§oBestätigen");
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

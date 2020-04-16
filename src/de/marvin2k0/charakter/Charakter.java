package de.marvin2k0.charakter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Charakter
{
    private File file;
    private FileConfiguration config;

    private UUID player;

    private String name;
    private String vorname;
    private String nachname;
    private int alter;
    private String herkunft;

    public Charakter(UUID player, String vorname, String nachname, int alter, String herkunft)
    {
        this.file = new File(CharakterPlugin.getCp().getDataFolder().getPath() + "/chars/" + player + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        this.vorname = vorname;
        this.nachname = nachname;
        this.alter = alter;
        this.herkunft = herkunft;
        this.name = vorname + " " + nachname;

        setVorname(vorname);
        setNachname(nachname);
        setAlter(alter);
    }

    private void setName(String name)
    {
        if (!vorname.isEmpty() && !nachname.isEmpty())
        {
            this.config.set("name", vorname + " " + nachname);
            this.name = this.config.getString("name");

            saveConfig();
        }
    }

    public String getName()
    {
        this.name = this.config.getString("name");

        return this.name;
    }

    public void setVorname(String vorname)
    {
        vorname = vorname.replace("§", "");

        this.vorname = vorname;
        this.config.set("vorname", vorname);
        saveConfig();

        if (!vorname.isEmpty() && !nachname.isEmpty())
        {
            this.config.set("name", vorname + " " + nachname);
            this.name = this.config.getString("name");

            saveConfig();
        }
    }

    public String getVorname()
    {
        this.vorname = this.config.getString("vorname");

        return this.vorname;
    }

    public void setNachname(String nachname)
    {
        nachname = nachname.replace("§", "");

        this.nachname = nachname;
        this.config.set("nachname", nachname);
        saveConfig();

        if (!vorname.isEmpty() && !nachname.isEmpty())
        {
            this.config.set("name", vorname + " " + nachname);
            this.name = this.config.getString("name");

            saveConfig();
        }
    }

    public String getNachname()
    {
        this.nachname = this.config.getString("nachname");

        return this.nachname;
    }

    public void setAlter(int alter)
    {
        this.alter = alter;
        this.config.set("alter", alter);
        saveConfig();
    }

    public int getAlter()
    {
        this.alter = this.config.getInt("alter");

        return this.alter;
    }

    public void setHerkunft(String herkunft)
    {
        herkunft = herkunft.replace("§", "");

        this.herkunft = herkunft;
        this.config.set("herkunft", herkunft);
        saveConfig();
    }

    public String getHerkunft()
    {
        this.herkunft = this.config.getString("herkunft");

        return this.herkunft;
    }

    public boolean isValid()
    {
        return !getVorname().isEmpty() && getAlter() != 0 && !getNachname().isEmpty() && getVorname() != null && getNachname() != null;
    }

    private void saveConfig()
    {
        try
        {
            config.save(file);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

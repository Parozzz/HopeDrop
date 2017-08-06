/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import me.parozzz.hopedrop.drop.DropHandler;
import me.parozzz.hopedrop.reflection.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Stefania
 */
public class HopeDrop extends JavaPlugin
{
    public static HopeDrop instance;
    @Override
    public void onEnable()
    {
        instance=this;
        initializeStaticClass();
        
        this.getDataFolder().mkdir();
        
        try 
        {
            load(false);
        } 
        catch (IOException | InvalidConfigurationException ex) 
        {
            Logger.getLogger(HopeDrop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void onDisable()
    {
        unregisterAll();
    }
    
    public void load(final boolean reload) throws UnsupportedEncodingException, IOException, FileNotFoundException, InvalidConfigurationException
    {
        if(reload)
        {
            unregisterAll();
        }
        
        FileConfiguration mob=Utils.fileStartup(this, new File(this.getDataFolder(), "mob.yml"));
        FileConfiguration block=Utils.fileStartup(this, new File(this.getDataFolder(), "block.yml"));
        
        DropHandler dropHandler=new DropHandler(mob, block);
        initializeListeners(dropHandler);
        
        FileConfiguration c=Utils.fileStartup(this, new File(this.getDataFolder(), "config.yml"));
        initializeCommand("hopedrop", new MainCommand(c));
        Configs.init(c);
    }
    
    private void initializeListeners(Listener... listeners)
    {
        Stream.of(listeners).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }
    
    private void initializeCommand(final String cmd, final CommandExecutor ex)
    {
        this.getCommand(cmd).setExecutor(ex);
    }
    
    private void initializeStaticClass()
    {
        Utils.init(this);
        ReflectionUtils.initialize();
        
        if(Dependency.setupEconomy())
        {
            Bukkit.getLogger().info("Hooked into Vault");
        }
    }
    
    private void unregisterAll()
    {
        Bukkit.getScheduler().cancelAllTasks();
        HandlerList.unregisterAll(this); 
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.parozzz.hopedrop.utilities.Utils;
import me.parozzz.hopedrop.utilities.reflection.ItemNBT;
import me.parozzz.hopedrop.utilities.reflection.NBT;
import me.parozzz.hopedrop.utilities.reflection.NBT.AttributeModifier;
import me.parozzz.hopedrop.utilities.reflection.NBT.AttributeSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Paros
 */
public class MainCommand implements CommandExecutor
{
    private static enum MessageEnum
    {
        RELOAD(true), ONRELOAD(false), RELOADFAILED(false);
        
        private final boolean isHelp;
        private MessageEnum(final boolean isHelp)
        {
            this.isHelp=isHelp;
        }
        
        public boolean isHelp()
        {
            return isHelp;
        }
    }
    
    private final EnumMap<MessageEnum, String> messages;
    private final String[] help;
    public MainCommand(final FileConfiguration c)
    {
        messages=new EnumMap(MessageEnum.class);
        
        ConfigurationSection cPath=c.getConfigurationSection("Message.Command");
        messages.putAll(cPath.getKeys(false).stream()
                .collect(Collectors.toMap(str -> MessageEnum.valueOf(str.toUpperCase()), str -> Utils.color(cPath.getString(str)))));
        
        help=messages.entrySet().stream().filter(entry -> entry.getKey().isHelp()).map(entry -> entry.getValue()).toArray(String[]::new);
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] val) 
    {
        if(cs.hasPermission("hopedrop.admin"))
        {
            if(val.length==0)
            {
                cs.sendMessage(help);
            }
            else if(val[0].equalsIgnoreCase("reload"))
            {
                try 
                {
                    HopeDrop.instance.load(true);
                    cs.sendMessage(messages.get(MessageEnum.ONRELOAD));
                } 
                catch (IOException | InvalidConfigurationException ex) 
                {
                    Logger.getLogger(MainCommand.class.getName()).log(Level.SEVERE, null, ex);
                    cs.sendMessage(messages.get(MessageEnum.RELOADFAILED));
                }
            }
            else if(val[0].equalsIgnoreCase("test"))
            {
                ItemStack item=new ItemStack(Material.DIAMOND_CHESTPLATE);
                try 
                {
                    ItemNBT nbt=new ItemNBT(item);
                    AttributeModifier modifier=new AttributeModifier();
                    modifier.armor(AttributeSlot.CHEST, 4D);
                    modifier.maxHealth(AttributeSlot.CHEST, 3);
                    modifier.armorToughness(AttributeSlot.CHEST, 10);
                    modifier.attackDamage(AttributeSlot.CHEST, -3);
                    modifier.attackSpeed(AttributeSlot.CHEST, -24);
                    modifier.luck(AttributeSlot.CHEST, 10);
                    modifier.knockbackResistance(AttributeSlot.CHEST, 0.5);
                    modifier.apply(nbt);
                    item=nbt.buildItem();
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) 
                {
                    Logger.getLogger(MainCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
                ((Player)cs).getInventory().addItem(item);
            }
            else if(val[0].equalsIgnoreCase("test2"))
            {
                ItemStack item=new ItemStack(Material.DIAMOND_BOOTS);
                try 
                {
                    ItemNBT nbt=new ItemNBT(item);
                    AttributeModifier modifier=new AttributeModifier();
                    modifier.armor(AttributeSlot.FEET, 4D);
                    modifier.maxHealth(AttributeSlot.FEET, 3);
                    modifier.armorToughness(AttributeSlot.FEET, 10);
                    modifier.attackDamage(AttributeSlot.FEET, 3);
                    modifier.attackSpeed(AttributeSlot.FEET, 24);
                    modifier.luck(AttributeSlot.FEET, 10);
                    modifier.knockbackResistance(AttributeSlot.FEET, 0.5);
                    modifier.apply(nbt);
                    item=nbt.buildItem();
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) 
                {
                    Logger.getLogger(MainCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
                ((Player)cs).getInventory().addItem(item);
            }
            else if(val[0].equalsIgnoreCase("test3"))
            {
                Bukkit.getLogger().info(new StringBuilder("LUCK ").append(((Player)cs).getAttribute(Attribute.GENERIC_LUCK).getValue()).toString());
                Bukkit.getLogger().info(new StringBuilder("DAMAGE ").append(((Player)cs).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()).toString());
                Bukkit.getLogger().info(new StringBuilder("SPEED ").append(((Player)cs).getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue()).toString());
                Bukkit.getLogger().info(new StringBuilder("ARMOR ").append(((Player)cs).getAttribute(Attribute.GENERIC_ARMOR).getValue()).toString());
                Bukkit.getLogger().info(new StringBuilder("ARMOR THOUGHNESS ").append(((Player)cs).getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue()).toString());
                Bukkit.getLogger().info(new StringBuilder("KNOCKBACK ").append(((Player)cs).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue()).toString());
            }
        }
        return true;
    }
    
}

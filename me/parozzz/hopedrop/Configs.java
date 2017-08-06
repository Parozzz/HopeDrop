/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop;

import java.util.EnumMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import me.parozzz.hopedrop.reflection.ActionBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Paros
 */
public class Configs 
{
    public static enum MessageEnum
    {
        ONMONEYRECEIVE;
    }
    
    private static EnumMap<MessageEnum, String> messages;
    private static BiConsumer<Player, Double> moneyMessage;
    protected static void init(final FileConfiguration c)
    {
        messages=new EnumMap(MessageEnum.class);        
        ConfigurationSection nPath=c.getConfigurationSection("Message.Normal");
        messages.putAll(nPath.getKeys(false).stream()
                .collect(Collectors.toMap(str -> MessageEnum.valueOf(str.toUpperCase()), str -> Utils.color(nPath.getString(str)))));
        
        String msg=messages.get(MessageEnum.ONMONEYRECEIVE);
        moneyMessage= c.getBoolean("actionbarMoney")? 
                (p, money) -> ActionBar.send(p, msg.replace("%money%", money.toString())):
                (p, money) -> p.sendMessage(msg.replace("%money%", money.toString()));
    }
    
    public static String getMessage(final MessageEnum me)
    {
        return messages.get(me);
    }
    
    public static void sendMoneyMessage(final Player p, final double money)
    {
        moneyMessage.accept(p, money);
    }
    
}

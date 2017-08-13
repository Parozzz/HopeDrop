/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.parozzz.hopedrop.utilities.Utils.ColorEnum;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.chance.ChanceManager.ChanceModifierType;
import me.parozzz.hopedrop.condition.BlockCondition;
import me.parozzz.hopedrop.condition.BlockCondition.BlockConditionType;
import me.parozzz.hopedrop.condition.ConditionType;
import me.parozzz.hopedrop.condition.GenericCondition;
import me.parozzz.hopedrop.condition.GenericCondition.GenericConditionType;
import me.parozzz.hopedrop.condition.MobCondition;
import me.parozzz.hopedrop.condition.MobCondition.KillReason;
import me.parozzz.hopedrop.condition.MobCondition.MobConditionType;
import me.parozzz.hopedrop.condition.PlayerCondition;
import me.parozzz.hopedrop.condition.PlayerCondition.PlayerConditionType;
import me.parozzz.hopedrop.condition.ToolCondition;
import me.parozzz.hopedrop.condition.ToolCondition.ToolConditionType;
import me.parozzz.hopedrop.drop.ConditionManager;
import me.parozzz.hopedrop.drop.ConditionManager.ConditionManagerType;
import me.parozzz.hopedrop.drop.RewardManager;
import me.parozzz.hopedrop.drop.RewardManager.RewardType;
import me.parozzz.hopedrop.drop.block.BlockConditionManager;
import me.parozzz.hopedrop.drop.item.ItemManager;
import me.parozzz.hopedrop.drop.item.ItemManager.AmountModifierType;
import me.parozzz.hopedrop.drop.mob.MobConditionManager;
import me.parozzz.hopedrop.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Paros
 */
public class Parser 
{
    
    public static RewardManager parseRewardManager(final List<String> list)
    {
        RewardManager manager=new RewardManager();
        
        list.stream().map(str -> str.replace("_", " ")).map(str -> str.split(":")).forEach(array -> 
        {
            RewardType type=RewardType.valueOf(array[0].toUpperCase());
            String value=array[1];

            Parser.addReward(manager, type, value);
        });
        
        return manager;
    }
    
    private static void addReward(final RewardManager manager, final RewardType type, final String value)
    {
        switch(type)
        {
            case ACTIONBAR:
                manager.addActionBarReward(Utils.color(value));
                break;
            case MESSAGE:
                manager.addMessageReward(Utils.color(value));
                break;
            case CONSOLECOMMAND:
                manager.addCommandReward(value, true);
                break;
            case PLAYERCOMMAND:
                manager.addCommandReward(value, false);
                break;
            case MONEY:
                manager.addMoneyReward(Double.valueOf(value));
                break;
            case FIREWORK:
                manager.addFireworkReward(Stream.of(value.split(";")).map(String::toUpperCase).map(ColorEnum::valueOf).map(ColorEnum::getBukkitColor).toArray(Color[]::new));
                break;
            case PLAYERSOUND:
                Parser.parseSound(manager, value, true);
                break;
            case WORLDSOUND:
                Parser.parseSound(manager, value, false);
                break;
        }
    }
    
    private static void parseSound(final RewardManager manager, final String value, final boolean playerSound)
    {
        String[] array=value.split(";");
        
        Sound sound=Sound.valueOf(array[0].toUpperCase().replace(" ", "_"));
        float volume=Float.valueOf(array[1]);
        float pitch=Float.valueOf(array[2]);
        
        manager.addSoundReward(sound, volume, pitch, playerSound);
    }
    
    public static ItemManager parseItemManager(final ConfigurationSection path)
    {
        ItemManager manager=new ItemManager(Utils.getItemByPath(path));
        
        if(path.contains("amount"))
        {
            String values=path.getString("amount");
            
            int min=Integer.valueOf(values.substring(0, values.indexOf("-")));
            int max=Integer.valueOf(values.substring(values.indexOf("-")+1));
            
            manager.setMinAndMax(min, max);
            
            path.getStringList("amountModifiers").stream().map(str -> str.split(":"))
                    .collect(Collectors.toMap(array -> array[1], array -> AmountModifierType.valueOf(array[0].toUpperCase())))
                    .forEach((str, type) ->  Parser.parseAmountModifier(manager, type, str));
        }
        
        return manager;
    }
    
    private static void parseAmountModifier(final ItemManager manager, final AmountModifierType type, final String value)
    {
        String mod=value.substring(0, value.indexOf(";"));
        
        String[] array=value.substring(value.indexOf(";")+1).split("-");
        int min=Integer.valueOf(array[0]);
        int max=Integer.valueOf(array[1]);
        
        switch(type)
        {
            case ENCHANT:
                Optional.ofNullable(Enchantment.getByName(mod.toUpperCase())).map(ench -> 
                {
                    manager.addEnchantModifier(ench, min, max);
                    return ench;
                }).orElseThrow(() -> new NullPointerException(mod+" is not a valid enchantment name"));
                break;
            case POTION:
                Optional.ofNullable(PotionEffectType.getByName(mod.toUpperCase())).map(pet -> 
                {
                    manager.addPotionModifier(pet, min, max);
                    return pet;
                }).orElseThrow(() -> new NullPointerException(mod+" is not a valid potion effect"));
                break;
        }   
    }
    
    public static ChanceManager parseChance(final ConfigurationSection path)
    {
        ChanceManager manager;
        
        if(path.contains("chance"))
        {
            manager=new ChanceManager(path.getDouble("chance"));
            path.getStringList("chanceModifiers").stream().map(str -> str.split(":")).forEach(array -> 
            {
                String modifier=array[0];
                String value=array[1];

                Parser.parseChanceModifier(manager, ChanceModifierType.valueOf(modifier.toUpperCase()), value);
            });
        }
        else
        {
            manager=new ChanceManager(100D);
        }
        
        return manager;
    }
    
    private static void parseChanceModifier(final ChanceManager manager, final ChanceModifierType type, final String value)
    {
        switch(type)
        {
            case ENCHANT:
                String enchant=value.substring(0, value.indexOf(";"));
                
                Optional.ofNullable(Enchantment.getByName(enchant.toUpperCase())).map(ench -> 
                {
                    double enchAdder=Double.valueOf(value.substring(value.indexOf(";")+1));
                    manager.addPlayerToolEnchantmentModifier(ench, enchAdder);
                    
                    return ench;
                }).orElseThrow(() -> new NullPointerException(enchant+" is not a valid enchantment"));
                break;
            case LEVEL:
                manager.addPlayerLevelModifier(Double.valueOf(value));
                break;
            case POTION:
                String potion=value.substring(0, value.indexOf(";"));
                
                Optional.ofNullable(PotionEffectType.getByName(potion.toUpperCase())).map(pet -> 
                {
                    double potionAdder=Double.valueOf(value.substring(value.indexOf(";")+1));
                    manager.addPlayerPotionModifier(pet, potionAdder);

                    return pet;
                }).orElseThrow(() -> new NullPointerException(potion+" is not a valid potion"));
                break;
        }
    }
    
    public static ConditionManager parseCondition(final ConditionManagerType type, final List<String> list)
    {
        ConditionManager manager;
        
        if(type==ConditionManagerType.BLOCK)
        {
            manager=new BlockConditionManager();
        }
        else
        {
            manager=new MobConditionManager();
        }
        
        list.stream().map(str -> str.split("->")).collect(Collectors.toMap(array -> array[1].split(":"), array -> ConditionType.valueOf(array[0].toUpperCase())))
                .forEach((array, ct) -> 
                {
                    String condition=array[0];
                    String value=array[1];
                    
                    switch(ct)
                    {
                        case GENERIC:
                            Parser.addGenericCondition(manager.getGenericCondition(), GenericConditionType.valueOf(condition.toUpperCase()), value);
                            break;
                        case MOB:
                            if(type==ConditionManagerType.MOB)
                            {
                                Parser.addMobCondition(((MobConditionManager)manager).getMobCondition(), MobConditionType.valueOf(condition.toUpperCase()), value);
                            }
                            break;
                        case PLAYER:
                            Parser.addPlayerCondition(manager.getPlayerCondition(), PlayerConditionType.valueOf(condition.toUpperCase()), value);
                            break;
                        case TOOL:
                            Parser.addToolCondition(manager.getToolCondition(), ToolConditionType.valueOf(condition.toUpperCase()), value);
                            break;
                        case BLOCK:
                            Parser.addBlockCondition(((BlockConditionManager)manager).getBlockCondition(), BlockConditionType.valueOf(condition.toUpperCase()), value);
                            break;
                    }
                });
        return manager;
    }
    
    private static void addGenericCondition(final GenericCondition cond, final GenericConditionType type, final String value)
    {
        switch(type)
        {
            case BIOME:
                cond.addBiomeCheck(Biome.valueOf(value.toUpperCase()));
                break;
            case WORLD:
                Optional.ofNullable(Bukkit.getServer().getWorld(value)).map(w -> 
                {
                    cond.addWorldCheck(w);
                    return w;
                }).orElseThrow(() -> new NullPointerException(value+" is not a valid world name"));
                break;
            case YLEVEL:
                int min=Integer.valueOf(value.substring(0, value.indexOf("-")));
                int max=Integer.valueOf(value.substring(value.indexOf("-")+1));
                
                cond.addYCheck(min, max);
                break;
        }
    }
    
    private static void addMobCondition(final MobCondition cond, final MobConditionType type, final String value)
    {
        switch(type)
        {
            case BABY:
                cond.addAgeCheck(Boolean.valueOf(value.toUpperCase()));
                break;
            case EQUIPMENT:
                String[] array = value.split(";");
                
                EquipmentSlot slot=EquipmentSlot.valueOf(array[0].toUpperCase());
                Material m=Material.valueOf(array[1].toUpperCase());
                
                cond.addEquipmentCheck(slot, m);
                break;
            case KILLREASON:
                cond.addKillReasonCheck(KillReason.valueOf(value.toUpperCase()));
                break;
            case NAME:
                cond.addNameCheck(ChatColor.translateAlternateColorCodes('&', value));
                break;
            case ONFIRE:
                cond.addOnFireCheck(Boolean.valueOf(value.toUpperCase()));
                break;
        }
    }
    
    private static void addPlayerCondition(final PlayerCondition cond, final PlayerConditionType type, final String value)
    {
        switch(type)
        {
            case GAMEMODE:
                cond.addGameModeCheck(GameMode.valueOf(value.toUpperCase()));
                break;
            case HEALTH:
                cond.addHealthCheck(Double.valueOf(value));
                break;
            case HUNGER:
                cond.addHungerCheck(Integer.valueOf(value));
                break;
            case LEVEL:
                cond.addLevelCheck(Integer.valueOf(value));
                break;
            case ONFIRE:
                cond.addOnFireCheck(Boolean.valueOf(value.toUpperCase()));
                break;
            case PERMISSION:
                cond.addPermissionCheck(value);
                break;
        }
    }
    
    private static void addBlockCondition(final BlockCondition cond, final BlockConditionType type, final String value)
    {
        switch(type)
        {
            case DATA:
                cond.addDataCheck(Short.valueOf(value));
                break;
        }
    }
    
    private static void addToolCondition(final ToolCondition cond, final ToolConditionType type, final String value)
    {
        switch(type)
        {
            case ENCHANT:
                String[] array=value.split(";");
                
                String enchant=array[0];
                String level=array[1];
                
                Optional.ofNullable(Enchantment.getByName(enchant.toUpperCase())).map(ench -> 
                {
                    if(level.contains("-"))
                    {
                        int min = Integer.valueOf(level.substring(0, level.indexOf("-")));
                        int max = Integer.valueOf(level.substring(level.indexOf("-")+1));

                        cond.addEnchantmentCheck(ench, min, max);
                    }
                    else
                    {
                        cond.addEnchantmentCheck(ench, Integer.valueOf(level));
                    }
                    return ench;
                }).orElseThrow(() -> new NullPointerException(enchant+" is not a valid enchanment"));
                break;
            case TYPE:
                cond.addMaterialCheck(Material.valueOf(value.toUpperCase()));
                break;
        }
    }
}

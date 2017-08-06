/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.chance.ChanceManager.ChanceModifierType;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
        
        list.stream().map(str -> str.replace("_", " ")).map(str -> str.split(":"))
                .forEach(array -> 
                {
                    RewardType type=RewardType.valueOf(array[0].toUpperCase());
                    String str=array[1];
                    
                    switch(type)
                    {
                        case ACTIONBAR:
                            manager.addActionBarReward(Utils.color(str));
                            break;
                        case MESSAGE:
                            manager.addMessageReward(Utils.color(str));
                            break;
                        case CONSOLECOMMAND:
                            manager.addCommandReward(str, true);
                            break;
                        case PLAYERCOMMAND:
                            manager.addCommandReward(str, false);
                            break;
                        case MONEY:
                            manager.addMoneyReward(Double.valueOf(str));
                            break;
                    }
                });
        
        return manager;
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
                manager.addEnchantModifier(Enchantment.getByName(mod), min, max);
                break;
            case POTION:
                manager.addPotionModifier(PotionEffectType.getByName(mod), min, max);
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
                Enchantment ench=Enchantment.getByName(value.substring(0, value.indexOf(";")).toUpperCase());
                double enchAdder=Double.valueOf(value.substring(value.indexOf(";")+1));
                
                manager.addPlayerToolEnchantmentModifier(ench, enchAdder);
                break;
            case LEVEL:
                manager.addPlayerLevelModifier(Double.valueOf(value));
                break;
            case POTION:
                PotionEffectType pet=PotionEffectType.getByName(value.substring(0, value.indexOf(";")));
                double potionAdder=Double.valueOf(value.substring(value.indexOf(";")+1));
                
                manager.addPlayerPotionModifier(pet, potionAdder);
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
                Optional.ofNullable(Bukkit.getServer().getWorld(value)).ifPresent(w -> cond.addWorldCheck(w));
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
    
    private static void addToolCondition(final ToolCondition cond, final ToolConditionType type, final String value)
    {
        switch(type)
        {
            case ENCHANT:
                String[] array=value.split(";");
                
                Enchantment ench=Enchantment.getByName(array[0].toUpperCase());
                String level=array[1];
                
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
                break;
            case TYPE:
                cond.addMaterialCheck(Material.valueOf(value.toUpperCase()));
                break;
        }
    }
}

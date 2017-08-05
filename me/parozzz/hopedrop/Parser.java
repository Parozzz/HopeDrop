/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.chance.ChanceModifier;
import me.parozzz.hopedrop.chance.ChanceModifierType;
import me.parozzz.hopedrop.chance.PlayerLevelModifier;
import me.parozzz.hopedrop.chance.PlayerPotionModifier;
import me.parozzz.hopedrop.chance.ToolEnchantmentModifier;
import me.parozzz.hopedrop.condition.ConditionType;
import me.parozzz.hopedrop.condition.generic.CheckBiome;
import me.parozzz.hopedrop.condition.generic.CheckWorld;
import me.parozzz.hopedrop.condition.generic.CheckYLevel;
import me.parozzz.hopedrop.condition.generic.GenericCondition;
import me.parozzz.hopedrop.condition.generic.GenericConditionType;
import me.parozzz.hopedrop.condition.mob.CheckBaby;
import me.parozzz.hopedrop.condition.mob.CheckEquipment;
import me.parozzz.hopedrop.condition.mob.CheckKillReason;
import me.parozzz.hopedrop.condition.mob.CheckKillReason.KillReason;
import me.parozzz.hopedrop.condition.mob.CheckMobOnFire;
import me.parozzz.hopedrop.condition.mob.CheckName;
import me.parozzz.hopedrop.condition.mob.MobCondition;
import me.parozzz.hopedrop.condition.mob.MobConditionType;
import me.parozzz.hopedrop.condition.player.CheckGamemode;
import me.parozzz.hopedrop.condition.player.CheckHealth;
import me.parozzz.hopedrop.condition.player.CheckHunger;
import me.parozzz.hopedrop.condition.player.CheckLevel;
import me.parozzz.hopedrop.condition.player.CheckPermission;
import me.parozzz.hopedrop.condition.player.CheckPlayerOnFire;
import me.parozzz.hopedrop.condition.player.PlayerCondition;
import me.parozzz.hopedrop.condition.player.PlayerConditionType;
import me.parozzz.hopedrop.condition.tool.CheckEnchantment;
import me.parozzz.hopedrop.condition.tool.CheckMaterial;
import me.parozzz.hopedrop.condition.tool.ToolCondition;
import me.parozzz.hopedrop.condition.tool.ToolConditionType;
import me.parozzz.hopedrop.drop.ConditionManager;
import me.parozzz.hopedrop.drop.ConditionManagerType;
import me.parozzz.hopedrop.drop.block.BlockConditionManager;
import me.parozzz.hopedrop.drop.item.AmountEnchantModifier;
import me.parozzz.hopedrop.drop.item.AmountModifier;
import me.parozzz.hopedrop.drop.item.AmountModifierType;
import me.parozzz.hopedrop.drop.item.AmountPotionModifier;
import me.parozzz.hopedrop.drop.item.ItemManager;
import me.parozzz.hopedrop.drop.mob.MobConditionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Paros
 */
public class Parser 
{
    
    public static ItemManager parseItemManager(final ConfigurationSection path)
    {
        ItemManager manager=new ItemManager(Utils.getItemByPath(path));
        
        if(path.contains("amount"))
        {
            String values=path.getString("amount");
            
            int min=Integer.valueOf(values.substring(0, values.indexOf("-")));
            int max=Integer.valueOf(values.substring(values.indexOf("-")+1));
            
            manager.setMaxAndMin(min, max);
            
            path.getStringList("amountModifiers").stream().map(str -> str.split(":"))
                    .collect(Collectors.toMap(array -> array[1], array -> AmountModifierType.valueOf(array[0].toUpperCase())))
                    .forEach((str, type) ->  manager.addAmountModifier(Parser.parseAmountModifier(type, str)));
        }
        
        return manager;
    }
    
    public static AmountModifier parseAmountModifier(final AmountModifierType type, final String value)
    {
        String mod=value.substring(0, value.indexOf(";"));
        
        String[] array=value.substring(value.indexOf(";")+1).split("-");
        int min=Integer.valueOf(array[0]);
        int max=Integer.valueOf(array[1]);
        
        switch(type)
        {
            case ENCHANT:
                return new AmountEnchantModifier(Enchantment.getByName(mod), min, max);
            case POTION:
                return new AmountPotionModifier(PotionEffectType.getByName(mod), min, max);
            default:
                return null;
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

                manager.addChanceModifier(Parser.parseChanceModifier(ChanceModifierType.valueOf(modifier.toUpperCase()), value));
            });
        }
        else
        {
            manager=new ChanceManager(100D);
        }
        
        return manager;
    }
    
    public static ChanceModifier parseChanceModifier(final ChanceModifierType type, final String value)
    {
        switch(type)
        {
            case ENCHANT:
                Enchantment ench=Enchantment.getByName(value.substring(0, value.indexOf(";")).toUpperCase());
                double enchAdder=Double.valueOf(value.substring(value.indexOf(";")+1));
                
                return new ToolEnchantmentModifier(ench, enchAdder);
            case LEVEL:
                return new PlayerLevelModifier(Double.valueOf(value));
            case POTION:
                PotionEffectType pet=PotionEffectType.getByName(value.substring(0, value.indexOf(";")));
                double potionAdder=Double.valueOf(value.substring(value.indexOf(";")+1));
                
                return new PlayerPotionModifier(pet, potionAdder);
            default:
                return null;
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
                            manager.addGenericCondition(Parser.getGenericCondition(GenericConditionType.valueOf(condition.toUpperCase()), value));
                            break;
                        case MOB:
                            if(type==ConditionManagerType.MOB)
                            {
                                ((MobConditionManager)manager).addMobCondition(Parser.getMobCondition(MobConditionType.valueOf(condition.toUpperCase()), value));
                            }
                            break;
                        case PLAYER:
                            manager.addPlayerCondition(Parser.getPlayerCondition(PlayerConditionType.valueOf(condition.toUpperCase()), value));
                            break;
                        case TOOL:
                            manager.addToolCondition(Parser.getToolCondition(ToolConditionType.valueOf(condition.toUpperCase()), value));
                            break;
                    }
                });
        return manager;
    }
    
    public static GenericCondition getGenericCondition(final GenericConditionType type, final String value)
    {
        switch(type)
        {
            case BIOME:
                return new CheckBiome(Biome.valueOf(value.toUpperCase()));
            case WORLD:
                return Optional.ofNullable(Bukkit.getServer().getWorld(value)).map(w -> new CheckWorld(w)).orElseGet(() -> null);
            case YLEVEL:
                int min=Integer.valueOf(value.substring(0, value.indexOf("-")));
                int max=Integer.valueOf(value.substring(value.indexOf("-")+1));
                
                return new CheckYLevel(min, max);
            default:
                return null;
        }
    }
    
    public static MobCondition getMobCondition(final MobConditionType type, final String value)
    {
        switch(type)
        {
            case BABY:
                return new CheckBaby(Boolean.valueOf(value.toUpperCase()));
            case EQUIPMENT:
                String[] array = value.split(";");
                
                EquipmentSlot slot=EquipmentSlot.valueOf(array[0].toUpperCase());
                Material m=Material.valueOf(array[1].toUpperCase());
                
                return new CheckEquipment(slot, m);
            case KILLREASON:
                return new CheckKillReason(KillReason.valueOf(value.toUpperCase()));
            case NAME:
                return new CheckName(ChatColor.translateAlternateColorCodes('&', value));
            case ONFIRE:
                return new CheckMobOnFire(Boolean.valueOf(value.toUpperCase()));
            default:
                return null;
        }
    }
    
    public static PlayerCondition getPlayerCondition(final PlayerConditionType type, final String value)
    {
        switch(type)
        {
            case GAMEMODE:
                return new CheckGamemode(GameMode.valueOf(value.toUpperCase()));
            case HEALTH:
                return new CheckHealth(Double.valueOf(value));
            case HUNGER:
                return new CheckHunger(Integer.valueOf(value));
            case LEVEL:
                return new CheckLevel(Integer.valueOf(value));
            case ONFIRE:
                return new CheckPlayerOnFire(Boolean.valueOf(value.toUpperCase()));
            case PERMISSION:
                return new CheckPermission(value);
            default:
                return null;
        }
    }
    
    public static ToolCondition getToolCondition(final ToolConditionType type, final String value)
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
                    
                    return new CheckEnchantment(ench, min, max);
                }
                else
                {
                    return new CheckEnchantment(ench, Integer.valueOf(level));
                }
            case TYPE:
                return new CheckMaterial(Material.valueOf(value.toUpperCase()));
            default:
                return null;
        }
    }
}

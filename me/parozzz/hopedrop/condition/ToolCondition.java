/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public class ToolCondition 
{
    public enum ToolConditionType 
    {
        ENCHANT, TYPE;
    }
    
    
    private Predicate<ItemStack> condition;
    public ToolCondition()
    {
        condition=tool -> true;
    }
    
    public void addEnchantmentCheck(final Enchantment ench, final int level)
    {
        condition=condition.and(tool -> tool.getEnchantmentLevel(ench)==level);
    }
    
    public void addEnchantmentCheck(final Enchantment ench, final int minLevel, final int maxLevel)
    {
        condition=condition.and(tool -> 
        {
           int enchLevel=tool.getEnchantmentLevel(ench);
           return enchLevel>=minLevel && enchLevel<=maxLevel;
        });
    }
    
    public void addMaterialCheck(final Material type, final boolean equals)
    {
        condition = equals ? condition.and(tool -> tool.getType() == type) : condition.and(tool -> tool.getType() != type);
    }
    
    public boolean checkAll(final ItemStack tool)
    {
        return condition.test(tool);
    }
}

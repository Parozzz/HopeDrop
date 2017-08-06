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
    
    
    private final Set<Predicate<ItemStack>> conditions;
    public ToolCondition()
    {
        conditions=new HashSet<>();
    }
    
    public void addEnchantmentCheck(final Enchantment ench, final int level)
    {
        conditions.add(item -> item.getEnchantmentLevel(ench)==level);
    }
    
    public void addEnchantmentCheck(final Enchantment ench, final int minLevel, final int maxLevel)
    {
        conditions.add(item -> 
        {
           int enchLevel=item.getEnchantmentLevel(ench);
           return enchLevel>=minLevel && enchLevel<=maxLevel;
        });
    }
    
    public void addMaterialCheck(final Material type)
    {
        conditions.add(item -> item.getType()==type);
    }
    
    public boolean checkAll(final ItemStack tool)
    {
        return conditions.stream().allMatch(pr -> pr.test(tool));
    }
}

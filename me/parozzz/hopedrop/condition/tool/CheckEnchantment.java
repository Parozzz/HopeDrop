/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition.tool;

import java.util.function.Predicate;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public class CheckEnchantment implements ToolCondition
{
    private final Enchantment ench;
    private final Predicate<Integer> check;
    public CheckEnchantment(final Enchantment ench, final int level)
    {
        this.ench=ench;
        
        check= lv -> lv.equals(level);
    }
    
    public CheckEnchantment(final Enchantment ench, final int minLevel, final int maxLevel)
    {
        this.ench=ench;
        
        check= lv -> lv>=minLevel && lv<=maxLevel;
    }
    
    @Override
    public boolean check(ItemStack item) 
    {
        return check.test(item.getEnchantmentLevel(ench));
    }
    
}

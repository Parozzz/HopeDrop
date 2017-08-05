/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop;

import me.parozzz.hopedrop.drop.item.ItemManager;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.drop.item.NumberManager;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public abstract class Drop 
{
    private final ItemManager item;
    private final ConditionManager conditionManager;
    private final ChanceManager chanceManager;
    public Drop(final ChanceManager chance, final ConditionManager cond, final ItemManager item)
    {
        conditionManager=cond;
        chanceManager=chance;
        this.item=item;
    }
        
    public ConditionManager getConditionManager()
    {
        return conditionManager;
    }
    
    public ChanceManager getChanceManager()
    {
        return chanceManager;
    }
    
    public ItemManager getItemManager()
    {
        return item;
    }
}

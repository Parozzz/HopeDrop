/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.block;

import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.drop.Drop;
import me.parozzz.hopedrop.drop.RewardManager;
import me.parozzz.hopedrop.drop.item.ItemManager;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public class BlockDrop extends Drop
{
    
    public BlockDrop(final ChanceManager chance, final BlockConditionManager cond, final ItemManager item, final RewardManager reward) 
    {
        super(chance, cond, item, reward);
    }
    
    @Override
    public BlockConditionManager getConditionManager()
    {
        return (BlockConditionManager)super.getConditionManager();
    }
    
}

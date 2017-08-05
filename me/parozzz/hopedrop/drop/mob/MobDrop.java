/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.mob;

import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.drop.Drop;
import me.parozzz.hopedrop.drop.item.ItemManager;
import me.parozzz.hopedrop.drop.item.NumberManager;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stefania
 */
public class MobDrop extends Drop
{
    
    public MobDrop(final ChanceManager chance, final MobConditionManager cond, final ItemManager item) 
    {
        super(chance, cond, item);
    }
    
    @Override
    public MobConditionManager getConditionManager()
    {
        return (MobConditionManager)super.getConditionManager();
    }
    
}

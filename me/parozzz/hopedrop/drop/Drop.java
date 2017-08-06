/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop;

import me.parozzz.hopedrop.drop.item.ItemManager;
import me.parozzz.hopedrop.chance.ChanceManager;

/**
 *
 * @author Stefania
 */
public abstract class Drop 
{
    private final ItemManager itemManager;
    private final ConditionManager conditionManager;
    private final ChanceManager chanceManager;
    private final RewardManager rewardManager;
    public Drop(final ChanceManager chance, final ConditionManager cond, final ItemManager item, final RewardManager reward)
    {
        conditionManager=cond;
        chanceManager=chance;
        itemManager=item;
        rewardManager=reward;
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
        return itemManager;
    }
    
    public RewardManager getRewardManager()
    {
        return rewardManager;
    }
}

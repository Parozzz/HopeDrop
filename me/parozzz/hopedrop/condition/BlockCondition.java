/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.condition;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.block.Block;

/**
 *
 * @author Paros
 */
public class BlockCondition 
{
    public enum BlockConditionType
    {
        DATA;
    }
    
    private Predicate<Block> condition;
    public BlockCondition()
    {
        condition= b -> true;
    }
    
    public void addDataCheck(final short data)
    {
        condition=condition.and(b -> b.getState().getData().toItemStack().getDurability()==data);
    }
    
    public boolean checkAll(final Block b)
    {
        return condition.test(b);
    }
}

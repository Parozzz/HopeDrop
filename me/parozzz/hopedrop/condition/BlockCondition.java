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
    
    private final Set<Predicate<Block>> conditions;
    public BlockCondition()
    {
        conditions=new HashSet<>();
    }
    
    public void addDataCheck(final short data)
    {
        conditions.add(b -> b.getState().getData().toItemStack().getDurability()==data);
    }
    
    public boolean checkAll(final Block b)
    {
        return conditions.stream().allMatch(prd -> prd.test(b));
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.item;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 *
 * @author Paros
 */
public class NumberManager 
{
    private int min;
    private int max;
    public NumberManager(final int min, final int max)
    {
        this.min=min;
        this.max=max;
    }
    
    public void setMin(final int min)
    {
        this.min=min;
    }
    
    public void setMax(final int max)
    {
        this.max=max;
    }
    
    
    public void addToMin(final int adder)
    {
        min+=adder;
    }
    
    public void addToMax(final int adder)
    {
        max+=adder;
    }
    
    public int getMin()
    {
        return min;
    }
    
    public int getMax()
    {
        return max;
    }
    
    public NumberManager getAddedClone(final int adder)
    {
        return new NumberManager(min + adder, max + adder);
    }
    
    public NumberManager getAddedClone(final Collection<NumberManager> managers)
    {
        NumberManager ret=new NumberManager(min, max);
        managers.forEach(manager -> 
        {
            ret.addToMax(manager.getMax());
            ret.addToMin(manager.getMin());
        });
        return ret;
    }
    
    public NumberManager getMultipliedClone(final int multiplier)
    {
        return new NumberManager(min * multiplier, max * multiplier);
    }
    
    public int generateBetween()
    {
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }
    
    public static NumberManager getEmptyManager()
    {
        return new NumberManager(0 , 0);
    }
    
}

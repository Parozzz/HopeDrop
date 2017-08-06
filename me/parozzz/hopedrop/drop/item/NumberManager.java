/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop.item;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.bukkit.Bukkit;

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
    
    private int minAdder=0;
    public void setMinAdder(final int adder)
    {
        minAdder=adder;
    }
    
    public void addToMinAdder(final int add)
    {
        minAdder+=add;
    }
    
    private int maxAdder=0;
    public void setMaxAdder(final int adder)
    {
        maxAdder=adder;
    }
    
    public void addToMaxAdder(final int add)
    {
        maxAdder+=add;
    }
    
    public int generateBetween()
    {
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }
    
    public int generateBetweenWithAdders()
    {
        int ret=ThreadLocalRandom.current().nextInt(min + minAdder, max + maxAdder + 1);
        minAdder=0;
        maxAdder=0;
        return ret;
    }
    
    public static NumberManager getEmptyManager()
    {
        return new NumberManager(0 , 0);
    }
    
}

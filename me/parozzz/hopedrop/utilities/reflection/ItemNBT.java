/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.utilities.reflection;

import me.parozzz.hopedrop.utilities.reflection.NBTTag.NBTCompound;
import me.parozzz.hopedrop.utilities.reflection.NBTTag.NBTType;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import me.parozzz.hopedrop.utilities.reflection.NBTTag.Tags;

/**
 *
 * @author Paros
 */
public class ItemNBT 
{
    public static boolean hasKey(final ItemStack item, final String key)
    {
        try 
        {
            Object nmsItemStack=NBT.asNMSCopy(item);
            return NBT.hasItemTag(nmsItemStack) && (boolean)NBTTag.compoundHasKey.invoke(NBT.getItemTag(nmsItemStack), key);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) 
        {
            Logger.getLogger(ItemNBT.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static <T> T getKey(final ItemStack item, final String key, final Class<T> clazz)
    {
        try 
        {
            Object obj=NBT.getItemTag(NBT.asNMSCopy(item));
            return obj!=null? (T)NBTTag.compoundGetters.get(clazz).invoke(obj, key) : null;
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) 
        {
            Logger.getLogger(ItemNBT.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private final NBTCompound compound;
    private final Object nmsItemStack;
    
    public ItemNBT(final Material type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
    {
        nmsItemStack=NBT.asNMSCopy(new ItemStack(type));
        compound=new NBTCompound();
    }
    
    public ItemNBT(final ItemStack item) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
    {
        nmsItemStack=NBT.asNMSCopy(item);
        compound=NBT.hasItemTag(nmsItemStack)?new NBTCompound(NBT.getItemTag(nmsItemStack)):new NBTCompound();
    }

    /*
    private NBTList nbtList;
    public ItemNBT newList() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        nbtList=new NBTList();
        return this;
    }
    */
    /*
    /**
     * You need to apply applyList() after this method to be effective
     * and initialize a new list using initializeList()
     * @param value
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     *//*
    public ItemNBT addValueToNewList(final Object value) 
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
    {
        return Optional.of(new NBTBase(value)).flatMap(base -> 
        {
            try { nbtList.addTag(base); } 
            catch (Exception ex) { ex.printStackTrace(); }
            return Optional.of(this);
        }).orElseGet(() -> this);
    }
    */
    /*
    /**
     * You need to apply applyList() after this method to be effective
     * and initialize a new list using initializeList()
     * @param values
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     *//*
    public ItemNBT addCoumpoundToNewList(final Map<String,Object> values) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        NBTCompound nbt=new NBTCompound();
        values.forEach((key,value) -> 
        { 
            try { nbt.addValue(key, value); }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) { ex.printStackTrace(); }
        });
        nbtList.addTag(nbt);
        return this;
    }*/
    /*
    public ItemNBT addList(final String key, final NBTList list) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        compound.addTag(key, list);
        return this;
    }
    */
    /*
    private NBTCompound newCompound;
    public ItemNBT newCompound() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        newCompound=new NBTCompound();
        return this;
    }
    
    public ItemNBT addValueToNewCompound(final String key, final Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        newCompound.addValue(key, value);
        return this;
    }
    *//*
    public ItemNBT setCoumpound(final String key, final NBTCompound nbt) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        compound.addTag(coumpoundName, newCompound);
        return this;
    }
    */
    
    public ItemNBT addTag(final String key, final Tags tag) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        compound.addTag(key, tag);
        return this;
    }
    
    public ItemNBT addValue(final String key, final Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
    {
        compound.addValue(key, value);
        return this;
    }
    
    public boolean hasKey(final String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return compound.hasKey(key);
    }
    
    public boolean hasKeyOfType(final String key, final NBTType t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return compound.hasKeyOfType(key, t);
    }
    
    public <T> T getValue(final String key, final Class<T> type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return compound.getKey(key, type);
    }
    
    public NBTType getTypeByKey(final String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return compound.getTypeByKey(key);
    }
    
    public Set<String> getNBTKeys() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return compound.keySet();
    }
    
    public ItemStack buildItem() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        NBT.setItemTag(nmsItemStack, compound.getNBTObject());
        return (ItemStack)NBT.asBukkitCopy(nmsItemStack);
    }
    
    @Override
    public String toString() 
    {
        return compound.toString(); 
    }
}

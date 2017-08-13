/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.utilities.reflection;

import me.parozzz.hopedrop.utilities.Utils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import me.parozzz.hopedrop.utilities.reflection.NBTTag.NBTCompound;
import me.parozzz.hopedrop.utilities.reflection.NBTTag.NBTList;
import org.bukkit.attribute.Attribute;

/**
 *
 * @author Paros
 */
public class NBT 
{
    
    public static enum AdventureTag
    {
        CANPLACEON("CanPlaceOn"),
        CANDESTROY("CanDestroy");
        
        private final String value;
        private AdventureTag(final String str) 
        {
            value=str; 
        }
        
        public String getValue() 
        {
            return value; 
        }
    }
    
    private static Method asNMSCopy;
    public static Object asNMSCopy(final ItemStack item) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
    {
        return asNMSCopy.invoke(null, item); 
    }
    
    private static Method asBukkitCopy;
    public static Object asBukkitCopy(final Object nmsItemStack)throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return asBukkitCopy.invoke(null, nmsItemStack); 
    }
 
    private static Method getItemTag;
    public static Object getItemTag(final Object nmsItemStack)throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
    {
        return getItemTag.invoke(nmsItemStack); 
    }
    
    private static Method setItemTag;
    public static Object setItemTag(final Object nmsItemStack, final Object compound) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
    { 
        return setItemTag.invoke(nmsItemStack, compound); 
    }
    
    private static Method hasItemTag;
    public static boolean hasItemTag(final Object nmsItemStack)throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
    {
        return (boolean)hasItemTag.invoke(nmsItemStack); 
    }
    
    protected static void init() throws NoSuchMethodException, ClassNotFoundException
    {
        NBTTag.init();
        
        Class<?> nmsItemStack=ReflectionUtils.getNMSClass("ItemStack");
        getItemTag=ReflectionUtils.getMethod(nmsItemStack, "getTag", new Class[0]);
        setItemTag=ReflectionUtils.getMethod(nmsItemStack, "setTag", NBTTag.compoundClass);
        hasItemTag=ReflectionUtils.getMethod(nmsItemStack, "hasTag", new Class[0]);

        Class<?> craftItemStack=ReflectionUtils.getCraftbukkitClass("inventory.CraftItemStack");
        asNMSCopy=ReflectionUtils.getMethod(craftItemStack, "asNMSCopy", ItemStack.class);
        asBukkitCopy=ReflectionUtils.getMethod(craftItemStack, "asBukkitCopy", nmsItemStack);
    }
    
    public static ItemStack setSpawnedType(final ItemStack egg, final EntityType et) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
    {
        NBTCompound id=new NBTCompound();
        id.addValue("id", et.name());
        return new ItemNBT(egg).addTag("EntityTag", id).buildItem();
    }
    
    public static ItemStack setAdventureFlag(final ItemStack item, final AdventureTag tag ,final Material... where) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException 
    {
        NBTList list=new NBTList();
        for(String str:Stream.of(where)
                .map(m -> m.name().toLowerCase())
                .map(str -> (Utils.bukkitVersion("1.11","1.12")?"minecraft:":"")+str)
                .toArray(String[]::new)) 
        {
            list.addBase(str);
        }
        return new ItemNBT(item).addTag(tag.getValue(), list).buildItem();
    }
    
    public static enum AttributeSlot
    {
        MAINHAND, OFFHAND, FEET, LEGS, CHEST, HEAD;
    }
    
    public static class AttributeModifier
    {
        private final UUID u;
        private final NBTList attributeList;
        public AttributeModifier() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            u=UUID.randomUUID();
            attributeList=new NBTList();
        }
        
        public void attackSpeed(final AttributeSlot slot, final double modifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            NBTCompound speed=new NBTCompound();
            speed.addValue("AttributeName", "generic.attackSpeed");
            speed.addValue("Name", "generic.attackSpeed");
            speed.addValue("Amount", modifier);
            speed.addValue("Operation", 0);
            speed.addValue("UUIDLeast", u.getLeastSignificantBits());
            speed.addValue("UUIDMost", u.getMostSignificantBits());
            speed.addValue("Slot", slot.name().toLowerCase());
            attributeList.addTag(speed);
        }
        
        public void attackDamage(final AttributeSlot slot, final double modifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            NBTCompound damage=new NBTCompound();
            damage.addValue("AttributeName", "generic.attackDamage");
            damage.addValue("Name", "generic.attackDamage");
            damage.addValue("Amount", modifier);
            damage.addValue("Operation", 0);
            damage.addValue("UUIDLeast", u.getLeastSignificantBits());
            damage.addValue("UUIDMost", u.getMostSignificantBits());
            damage.addValue("Slot", slot.name().toLowerCase());
            attributeList.addTag(damage);
        }
        
        public void movementSpeed(final AttributeSlot slot, final double modifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            NBTCompound speed=new NBTCompound();
            speed.addValue("AttributeName", "generic.movementSpeed");
            speed.addValue("Name", "generic.movementSpeed");
            speed.addValue("Amount", modifier);
            speed.addValue("Operation", 0);
            speed.addValue("UUIDLeast", u.getLeastSignificantBits());
            speed.addValue("UUIDMost", u.getMostSignificantBits());
            speed.addValue("Slot", slot.name().toLowerCase());
            attributeList.addTag(speed);
        }
        
        public void maxHealth(final AttributeSlot slot, final double modifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            NBTCompound health=new NBTCompound();
            health.addValue("AttributeName", "generic.maxHealth");
            health.addValue("Name", "generic.maxHealth");
            health.addValue("Amount", modifier);
            health.addValue("Operation", 0);
            health.addValue("UUIDLeast", u.getLeastSignificantBits());
            health.addValue("UUIDMost", u.getMostSignificantBits());
            health.addValue("Slot", slot.name().toLowerCase());
            attributeList.addTag(health);
        }
        
        public void luck(final AttributeSlot slot, final double modifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            NBTCompound luck=new NBTCompound();
            luck.addValue("AttributeName", "generic.luck");
            luck.addValue("Name", "generic.luck");
            luck.addValue("Amount", modifier);
            luck.addValue("Operation", 0);
            luck.addValue("UUIDLeast", u.getLeastSignificantBits());
            luck.addValue("UUIDMost", u.getMostSignificantBits());
            luck.addValue("Slot", slot.name().toLowerCase());
            attributeList.addTag(luck);
        }
        
        public void armor(final AttributeSlot slot, final double modifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            NBTCompound armor=new NBTCompound();
            armor.addValue("AttributeName", "generic.armor");
            armor.addValue("Name", "generic.armor");
            armor.addValue("Amount", modifier);
            armor.addValue("Operation", 0);
            armor.addValue("UUIDLeast", u.getLeastSignificantBits());
            armor.addValue("UUIDMost", u.getMostSignificantBits());
            armor.addValue("Slot", slot.name().toLowerCase());
            attributeList.addTag(armor);
        }
        
        public void armorToughness(final AttributeSlot slot, final double modifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            NBTCompound toughness=new NBTCompound();
            toughness.addValue("AttributeName", "generic.armorToughness");
            toughness.addValue("Name", "generic.armorToughness");
            toughness.addValue("Amount", modifier);
            toughness.addValue("Operation", 0);
            toughness.addValue("UUIDLeast", u.getLeastSignificantBits());
            toughness.addValue("UUIDMost", u.getMostSignificantBits());
            toughness.addValue("Slot", slot.name().toLowerCase());
            attributeList.addTag(toughness);
        }
        
        public void knockbackResistance(final AttributeSlot slot, final double modifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            if(modifier<=1D)
            {                
                NBTCompound knockback=new NBTCompound();
                knockback.addValue("AttributeName", "generic.knockbackResistance");
                knockback.addValue("Name", "generic.knockbackResistance");
                knockback.addValue("Amount", modifier);
                knockback.addValue("Operation", 0);
                knockback.addValue("UUIDLeast", u.getLeastSignificantBits());
                knockback.addValue("UUIDMost", u.getMostSignificantBits());
                knockback.addValue("Slot", slot.name().toLowerCase());
                attributeList.addTag(knockback);
            }
        }
        
        public void apply(final ItemNBT item) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            item.addTag("AttributeModifiers", attributeList);
        }
    }
}

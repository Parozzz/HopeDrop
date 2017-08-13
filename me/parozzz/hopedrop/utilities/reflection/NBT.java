/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.utilities.reflection;

import me.parozzz.hopedrop.utilities.Utils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import me.parozzz.hopedrop.utilities.reflection.NBTTag.NBTCompound;
import me.parozzz.hopedrop.utilities.reflection.NBTTag.NBTList;

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
    
    public static enum ItemAttribute
    {
        MAX_HEALTH("generic.maxHealth"),
        KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
        MOVEMENT_SPEED("generic.movementSpeed"),
        ATTACK_DAMAGE("generic.attackDamage"),
        ATTACK_SPEED("generic.attackSpeed"),
        ARMOR("generic.armor"),
        ARMOR_TOUGHTNESS("generic.armorToughness"),
        LUCK("generic.luck");
        
        private final String name;
        private ItemAttribute(final String name)
        {
            this.name=name;
        }
        
        public String getName()
        {
            return name;
        }
    }
    
    public static class ItemAttributeModifier
    {
        private final UUID randomUUID;
        private final NBTList attributeList;
        public ItemAttributeModifier() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            attributeList=new NBTList();
            randomUUID=UUID.randomUUID();
        }
        
        public void addModifier(final AttributeSlot slot, final ItemAttribute attribute,final double value) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            NBTCompound modifierCompound=new NBTCompound();
            modifierCompound.addValue("AttributeName", attribute.getName());
            modifierCompound.addValue("Name", attribute.getName());
            modifierCompound.addValue("Amount", value);
            modifierCompound.addValue("Operation", 0);
            modifierCompound.addValue("UUIDLeast", randomUUID.getLeastSignificantBits());
            modifierCompound.addValue("UUIDMost", randomUUID.getMostSignificantBits());
            modifierCompound.addValue("Slot", slot.name().toLowerCase());
            attributeList.addTag(modifierCompound);
        }
        
        public void apply(final ItemNBT item) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            item.addTag("AttributeModifiers", attributeList);
        }
    }
}
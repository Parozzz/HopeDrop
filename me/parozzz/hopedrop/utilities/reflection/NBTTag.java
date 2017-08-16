/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.utilities.reflection;

import me.parozzz.hopedrop.utilities.Utils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
/**
 *
 * @author Paros
 */
public class NBTTag 
{
    
    public static enum NBTType
    {
        BYTE((byte)1, byte.class),
        SHORT((byte)2, short.class),
        INT((byte)3, int.class),
        LONG((byte)4, long.class),
        FLOAT((byte)5, float.class),
        DOUBLE((byte)6, double.class),
        BYTEARRAY((byte)7, byte[].class),
        STRING((byte)8, String.class),
        LIST((byte)9, ReflectionUtils.getNMSClass("NBTTagList")),
        COMPOUND((byte)10, ReflectionUtils.getNMSClass("NBTTagCompound")),
        INTARRAY((byte)11, int[].class),
        LONGARRAY((byte)12, long[].class);
        
        private final Class<?> type;
        private final byte id;
        private NBTType(final byte id,final Class<?> type) 
        { 
            this.id=id;
            this.type=type;
        }
        public byte getId() { return id; }
        public Class<?> getType() { return type; }
        
        public static NBTType getById(final byte id)
        {
            return Stream.of(NBTType.values()).filter(nbt -> nbt.getId()==id).findFirst().orElseGet(() -> null);
        }
    }
    
    /*
    ==================
    #### NBT BASE ####
    ==================
    */
    protected static Class<?> baseClass;
    protected final static Map<Class<?>, Constructor<?>> baseConstructors=new HashMap<>();
    
    /*
    ==================
    #### COMPOUND ####
    ==================
    */
    protected static Class<?> compoundClass;
    protected static Constructor<?> compound;
    
    /*
    ==========================
    #### COMPOUND SETTERS ####
    ==========================
    */
    protected static Method compoundSetNBT;
    protected static Method compoundKeySet;
    protected static Method compoundGetTypeByKey;
    
    protected static final Map<Class<?>,Method> compoundSetters=new HashMap<>();
    
    /*
    ==========================
    #### COMPOUND GETTERS ####
    ==========================
    */    
    protected static Method compoundHasKey;
    protected static Method compoundHasKeyOfType;
    protected static Method compoundRemoveKey; //compoundRemoveKey.invoke(Object compound, String key); 
    
    protected static final Map<Class<?>,Method> compoundGetters=new HashMap<>();
    /*
    ==============
    #### LIST ####
    ==============
    */
    protected static Class<?> listClass;
    
    protected static Constructor<?> list;
    protected static Method listAddTo;
    protected static Method listGetType;
    
    protected static void init() throws NoSuchMethodException
    {  
        baseClass=ReflectionUtils.getNMSClass("NBTBase");
        listClass=ReflectionUtils.getNMSClass("NBTTagList");
        
        list=listClass.getConstructor(new Class[0]);
        listAddTo=ReflectionUtils.getMethod(listClass, "add", baseClass);
        listGetType=ReflectionUtils.getMethod(listClass, Utils.bukkitVersion("1.8")? "f" : "g"); 
        
        compoundClass=ReflectionUtils.getNMSClass("NBTTagCompound");

        compound=compoundClass.getConstructor(new Class[0]);
        compoundSetNBT=ReflectionUtils.getMethod(compoundClass, "set", String.class, baseClass); 
        compoundKeySet=ReflectionUtils.getMethod(compoundClass, "c");
        compoundGetTypeByKey=ReflectionUtils.getMethod(compoundClass, Utils.bukkitVersion("1.8") ? "b" : "d", String.class); 
        compoundHasKey=ReflectionUtils.getMethod(compoundClass, "hasKey", String.class); //Checker
        compoundHasKeyOfType=ReflectionUtils.getMethod(compoundClass, "hasKeyOfType", String.class, int.class);
        compoundRemoveKey=ReflectionUtils.getMethod(compoundClass, "remove", String.class); //Removed
        
        compoundGetters.put(String.class, ReflectionUtils.getMethod(compoundClass, "getString", String.class));
        compoundSetters.put(String.class, ReflectionUtils.getMethod(compoundClass, "setString", String.class, String.class));
        baseConstructors.put(String.class, ReflectionUtils.getNMSClass("NBTTagString").getConstructor(String.class));
        Stream.of(Double.class, Float.class, Short.class, Integer.class, Long.class, Boolean.class,
                double.class, float.class, short.class, int.class, long.class, boolean.class).forEach(clazz -> 
        {
            String objectName=clazz==Integer.class?"Int":clazz.getSimpleName().substring(0, 1).toUpperCase() + clazz.getSimpleName().substring(1);
            compoundGetters.put(clazz, ReflectionUtils.getMethod(compoundClass, "get"+objectName, String.class));

            try 
            {
                Class<?> primitive=ReflectionUtils.objectToPrimitive.get(clazz);
                if(primitive==null)
                {
                    primitive=clazz;
                }
                
                compoundSetters.put(clazz, ReflectionUtils.getMethod(compoundClass, "set"+objectName, String.class, primitive));
                if(primitive!=boolean.class)
                {
                    baseConstructors.put(clazz, ReflectionUtils.getNMSClass("NBTTag"+objectName).getConstructor(primitive));
                }
            } 
            catch (NoSuchMethodException | SecurityException ex) 
            {
                Logger.getLogger(NBTTag.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    
    public static class NBTBase implements Tags
    {
        private final Object nbtBase;
        public NBTBase(final Object value) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException 
        { 
            nbtBase=baseConstructors.get(value.getClass()).newInstance(value);
        }
        
        @Override
        public Object getNBTObject() 
        {
            return nbtBase; 
        }
        
        @Override
        public String toString()
        {
            return nbtBase.toString(); 
        }
    }
    
    public static class NBTList implements Tags
    {
        private final Object nbtList;
        public NBTList() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            nbtList=list.newInstance();
        }
        
        public NBTList(final Object obj) 
        {
            nbtList=obj; 
        }
        
        @Override
        public Object getNBTObject()
        {
            return nbtList; 
        }
        
        public void addBase(final Object value) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            this.addTag(new NBTBase(value));
        }
        
        public void addTag(final Tags nbt) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            listAddTo.invoke(nbtList, nbt.getNBTObject()); 
        }
        
        public NBTType getListType() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            return NBTType.getById((byte)listGetType.invoke(nbtList));
        }
        
        @Override
        public String toString()
        {
            return nbtList.toString(); 
        }
    }
    
    public static class NBTCompound implements Tags
    {
        private final Object nbtCompound;
        public NBTCompound() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            nbtCompound=compound.newInstance();
        }
        
        public NBTCompound(final Object compound) 
        {
            this.nbtCompound=compound; 
        }
        
        @Override
        public Object getNBTObject() 
        {
            return nbtCompound; 
        }
        
        public void addValue(final String key, final Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            compoundSetters.get(value.getClass()).invoke(nbtCompound, key, value);
        }
        
        public void addTag(final String key, final Tags nbt) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            compoundSetNBT.invoke(nbtCompound, key, nbt.getNBTObject()); 
        }
        
        public boolean hasKey(final String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
        {
            return (boolean)compoundHasKey.invoke(nbtCompound, key);
        }
        
        public NBTType getTypeByKey(final String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            return NBTType.getById((byte)compoundGetTypeByKey.invoke(nbtCompound, key));
        }
        
        public boolean hasKeyOfType(final String key, final NBTType type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
        {
            return (boolean)compoundHasKeyOfType.invoke(nbtCompound, key, type.getId());
        }
        
        public <T> T getKey(final String key, final Class<T> type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            return (T)compoundGetters.get(type).invoke(nbtCompound, key);//(T)NBTTag.compoundGet(this, key, type);
        }
        
        public Set<String> keySet() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
        {
            return (Set<String>)compoundKeySet.invoke(nbtCompound);
        }
        
        @Override
        public String toString()
        {
            return nbtCompound.toString(); 
        }
    }
    
    public interface Tags
    {
        Object getNBTObject();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.utilities.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
/**
 *
 * @author Paros
 */
public class ReflectionUtils 
{
    
    public final static Map<Class<?> , Class<?>> objectToPrimitive=new HashMap<>();
    public static String version;
    public static void init()
    {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        version = name.substring(name.lastIndexOf('.') + 1) + ".";
        
        objectToPrimitive.put(Boolean.class, boolean.class);
        objectToPrimitive.put(Long.class, long.class);
        objectToPrimitive.put(Double.class, double.class);
        objectToPrimitive.put(Short.class, short.class);
        objectToPrimitive.put(Float.class, float.class);
        objectToPrimitive.put(Integer.class, int.class);
        
        try 
        {
            ActionBar.init();
            NBT.init();
            Packets.init();
            Particle.init();
        } 
        catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException ex) 
        {
            ex.printStackTrace(); 
        }
    }
    
    public static Class<?> getNMSClass(final String className)
    {
        try 
        {
            return Class.forName(new StringBuilder("net.minecraft.server.").append(version).append(className).toString());
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static Class<?> getCraftbukkitClass(final String path)
    {
        try 
        { 
            return Class.forName(new StringBuilder("org.bukkit.craftbukkit.").append(version).append(path).toString());
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    
    public static Field getField(Class<?> clazz, String name)
    {
        try 
        {
            return Optional.ofNullable(clazz.getDeclaredField(name))
                    .map(field -> 
                    {
                        field.setAccessible(true);
                        return field;
                    })
                    .orElseThrow(() -> new NullPointerException("Field "+name+" does not exist in class "+clazz.getName()));
        } 
        catch (NoSuchFieldException | SecurityException ex) 
        {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static Method getMethod(final Class<?> clazz, final String name, final Class<?>... args) 
    {
        return Arrays.stream(clazz.getMethods())
                .filter(mt -> mt.getName().equals(name))
                .filter(mt -> args.length == 0 || classListEqual(args, mt.getParameterTypes()))
                .findFirst().map(method -> 
                { 
                    method.setAccessible(true);
                    return method;
                }).orElseThrow(() -> new NullPointerException("Method "+name+" does not exist in class "+clazz.getName()));
    }
    
    private static boolean classListEqual(Class<?>[] l1, Class<?>[] l2) 
    {
        //if (l1.length != l2.length) { return false; }
        return l1.length == l2.length && IntStream.of(l1.length-1).allMatch(i -> l1[i] == l2[i]);
        //for (int i = 0; i < l1.length; i++) { if (l1[i] != l2[i]) { return false; } }
        //return true;
    }
}

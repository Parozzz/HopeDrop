/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.utilities.reflection;

import static me.parozzz.hopedrop.utilities.reflection.ReflectionUtils.version;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;
/**
 *
 * @author Paros
 */
public class Packets 
{
    private static Method handle;
    private static Method getWorld;
    private static Field playerConnection;
    private static Method serialize;
    private static Class<?> nmsChatSerializer;
    
    protected static void init() throws ClassNotFoundException, NoSuchFieldException
    {
        if (version.contains("1_8")) 
        {
            if (version.contains("R1")) 
            {
                nmsChatSerializer = ReflectionUtils.getNMSClass("ChatSerializer");
            } 
            else if (version.contains("R2") || version.contains("R3")) 
            {
                nmsChatSerializer = ReflectionUtils.getNMSClass("IChatBaseComponent$ChatSerializer");
            }
        } 
        else 
        {
            nmsChatSerializer = ReflectionUtils.getNMSClass("IChatBaseComponent$ChatSerializer");
        }

        serialize=ReflectionUtils.getMethod(nmsChatSerializer, "a", String.class);

        Class<?> CraftPlayer=ReflectionUtils.getCraftbukkitClass("entity.CraftPlayer");
        handle=ReflectionUtils.getMethod(CraftPlayer,"getHandle",new Class[0]);

        Class<?> EntityPlayer=ReflectionUtils.getNMSClass("EntityPlayer");
        playerConnection=ReflectionUtils.getField(EntityPlayer, "playerConnection");
        getWorld=ReflectionUtils.getMethod(EntityPlayer, "getWorld", new Class[0]);
    }
    
    public static Object getHandle(final Player p) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return handle.invoke(p);
    }
    
    public static Object getWorld(final Object handle) 
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return getWorld.invoke(handle);
    }
    
    public static Object playerConnection(final Object handle) throws IllegalArgumentException, IllegalAccessException
    {
        return playerConnection.get(handle);
    }
    
    public static Object getStringSerialized(final String str) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return serialize.invoke(nmsChatSerializer,"{\"text\":\""+str+"\"}");
    }
    
    public static void sendPacket(final Player p, final Object packet) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        sendPacket(playerConnection(getHandle(p)),packet);
    }
            
    public static void sendPacket(final Object playerConnection, final Object packet) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        ReflectionUtils.getMethod(playerConnection.getClass(), "sendPacket").invoke(playerConnection, packet);
    }
}

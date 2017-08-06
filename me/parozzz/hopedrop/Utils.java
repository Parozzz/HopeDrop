/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop;

import me.parozzz.hopedrop.reflection.NBT;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.material.SpawnEgg;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Paros
 */
public final class Utils {
    /*
        INVENTORY TESTING
        pl.getLogger().info("=====================");
        pl.getLogger().info("InvType: "+(e.getInventory().getType()!=null?e.getInventory().getType().toString():""));
        pl.getLogger().info("CurrentItem: "+(e.getCurrentItem()!=null?e.getCurrentItem().toString():""));
        pl.getLogger().info("CursorItem: "+(e.getCursor()!=null?e.getCursor().toString():""));
        pl.getLogger().info("SlotItem: "+(e.getInventory().getItem(e.getSlot())!=null?e.getInventory().getItem(e.getSlot()).toString():""));
        pl.getLogger().info("RawSlotItem: "+(e.getInventory().getItem(e.getRawSlot())!=null?e.getInventory().getItem(e.getRawSlot()).toString():""));
        pl.getLogger().info("HotbarSlotItem: "+(e.getInventory().getItem(e.getHotbarButton())!=null?e.getInventory().getItem(e.getHotbarButton()).toString():""));
        pl.getLogger().info("PlayerCursorItem: "+(e.getWhoClicked().getItemOnCursor()!=null?e.getWhoClicked().getItemOnCursor().toString():""));
        pl.getLogger().info("Action: "+(e.getAction()!=null?e.getAction().toString():""));
        pl.getLogger().info("SlotType: "+(e.getSlotType()!=null?e.getSlotType().toString():""));
        pl.getLogger().info("Click: "+(e.getClick()!=null?e.getClick().toString():""));
        pl.getLogger().info("Slot: "+Integer.toString(e.getSlot()));
        pl.getLogger().info("RawSlot: "+Integer.toString(e.getRawSlot()));
        pl.getLogger().info("HotbarSlot: "+Integer.toString(e.getHotbarButton()));
        pl.getLogger().info("=====================");
    */
    
    public static enum ColorEnum
    {
        AQUA(Color.AQUA),BLACK(Color.BLACK),FUCHSIA(Color.FUCHSIA),
        GRAY(Color.GRAY),GREEN(Color.GREEN),LIME(Color.LIME),
        MAROON(Color.MAROON),NAVY(Color.NAVY),OLIVE(Color.OLIVE),
        ORANGE(Color.ORANGE),PURPLE(Color.PURPLE),RED(Color.RED),
        BLUE(Color.BLUE),SILVER(Color.SILVER),TEAL(Color.TEAL),
        WHITE(Color.WHITE),YELLOW(Color.YELLOW);
        
        private final Color color;
        private ColorEnum(Color color)
        {
            this.color=color;
        }
        
        public Color getBukkitColor()
        {
            return color;
        }
    }
    
    public static enum GiveCommandEnum
    {
        PLAYEROFFLINE,WRONGITEM,ITEMGIVEN,FULLINVENTORY;
    }
    
    private static final String FIREWORK_DATA="Firework.NoDamage";
    
    private static Function<EntityEquipment,ItemStack> getHand;
    private static Function<LivingEntity,Double> getMaxHealth;
    private static BiConsumer<LivingEntity,Double> setMaxHealth;
    private static Predicate<ItemStack> checkUnbreakable;
    public static void init(final JavaPlugin instance)
    {
        if(Utils.bukkitVersion("1.8"))
        {
            getHand = p -> p.getItemInHand();
            getMaxHealth = ent -> ent.getMaxHealth();
            setMaxHealth = (ent, health) -> ent.setMaxHealth(health);
            checkUnbreakable = item -> false;
        }
        else
        {
            getHand = equip -> equip.getItemInMainHand();
            getMaxHealth= ent -> ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            setMaxHealth = (ent, health) -> ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            
            if(Utils.bukkitVersion("1.9", "1.10"))
            {
                checkUnbreakable = item -> item.getItemMeta().spigot().isUnbreakable();
            }
            else
            {
                checkUnbreakable = item -> item.getItemMeta().isUnbreakable();
                Bukkit.getServer().getPluginManager().registerEvents(new Listener()
                {
                    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
                    private void onFireworkDamage(final EntityDamageByEntityEvent e)
                    {
                        e.setCancelled(e.getDamager().getType()==EntityType.FIREWORK && e.getDamager().hasMetadata(FIREWORK_DATA));
                    }
                }, instance);
            }
        }
    }
    
    public static ItemStack getMainHand(final EntityEquipment equip)
    {
        return getHand.apply(equip);
    }
    
    public static double getMaxHealth(final LivingEntity ent)
    {
        return getMaxHealth.apply(ent);
    }
    
    public static void setMaxHealth(final LivingEntity ent, final double health)
    {
        setMaxHealth.accept(ent, health);
    }
    
    public static GiveCommandEnum giveCommand(final Player p,ItemStack item,final String amount)
    {
        if(p==null){ return GiveCommandEnum.PLAYEROFFLINE; }
        
        if(item==null){ return GiveCommandEnum.WRONGITEM; }
        else { item=item.clone(); }
        
        if(amount!=null && amount.chars().noneMatch(i -> Character.isLetter(i))){ item.setAmount(Integer.parseInt(amount)); }
        
        if(giveAndDropItem(p.getInventory(),item,p.getLocation())){ return GiveCommandEnum.ITEMGIVEN; }
        else{ return GiveCommandEnum.FULLINVENTORY; }
    }
    
    public static String booleanToEz(final boolean bln)
    {
        if(bln)
        {
            return ChatColor.GREEN+"OK";
        }
        else
        {
            return ChatColor.RED+"NO";
        }
    }
    
    public static boolean giveAndDropItem(final Inventory i, final ItemStack item, final Location l)
    {
        Map<Integer,ItemStack> notFit=i.addItem(item.clone());
        if(!notFit.values().isEmpty())
        {
            notFit.values().stream().forEach((temp) -> l.getWorld().dropItem(l, temp));
            return false;
        }
        return true;
    }
    
    public static boolean addOnlyItem(final Inventory i, final ItemStack item)
    {
        return i.addItem(item.clone()).values().isEmpty();
    }
    
    public static int emptySlot(final Inventory i)
    {
        ItemStack[] storage = i.getContents();
        if(!Bukkit.getVersion().contains("1.8"))
        { 
            storage=i.getStorageContents(); 
        }
        
        Long amount=Arrays.stream(storage).filter((temp) -> temp==null).count();
        return amount.intValue();
    }
    
    public static boolean or(final Object o, final Object... array)
    {
        return Arrays.stream(array).anyMatch(ob -> ob.equals(o));
    }
    
    public static boolean and(final Object o, final Object... array)
    {
        return Arrays.stream(array).allMatch(ob -> ob.equals(o));
    }
    
    public static boolean isNumber(final String str) 
    {
        return str.chars().allMatch(c -> Character.isDigit((char)c)); 
    }
    
    public static String color(final String s)
    {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    
    public static String stripColor(final String s)
    {
        return ChatColor.stripColor(s);
    }
    
    public static List<String> colorList(final List<String> list)
    {
        return list.stream().map(Utils::color).collect(Collectors.toList());
    }
    
    public static List<String> split(String s, final String ch)
    {
        List<String> ret=new ArrayList<>();
        for(Integer j=0;;j++)
        {
            ret.add(j,s.contains(ch)?s.substring(0,s.indexOf(ch)):s);
            if(!s.contains(ch))
            {
                return ret; 
            }
            s=s.substring(s.indexOf(ch)+1);
        }
    }
    
    public static FileConfiguration fileStartup(final JavaPlugin pl,final File file) throws FileNotFoundException, UnsupportedEncodingException, IOException, InvalidConfigurationException
    {
        if(!file.exists()) { pl.saveResource(file.getPath().replace("plugins"+File.separator+pl.getName()+File.separator, ""), true); }
        FileConfiguration c=new YamlConfiguration();
        c.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        return c;
    }
    
    public static void setName(final Entity ent, final String name)
    {
        ent.setCustomName(name);
        ent.setCustomNameVisible(true);
    }
    
    public static ArmorStand spawnHologram( final Location l, final String str)
    {
        ArmorStand as=(ArmorStand)l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        as.setCustomName(str);
        as.setCustomNameVisible(true);
        as.setVisible(false);
        as.setGravity(false);
        as.setMarker(true);
        as.setRemoveWhenFarAway(false);
        as.setBasePlate(false);
        as.setSilent(true);
        if(!Utils.bukkitVersion("1.8")) 
        {
            as.setInvulnerable(true); 
        }
        return as;
    }
    
    public static Item spawnFloatingItem( final Location l, final String str, final Material type)
    {
        if(type==null) 
        { 
            return null; 
        }
        
        Item item=l.getWorld().dropItem(l, new ItemStack(type));
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setVelocity(new Vector(0,0,0));
        item.setCustomName(str);
        item.setCustomNameVisible(true);
        item.setGravity(false);
        item.setSilent(true);
        if(!Utils.bukkitVersion("1.8")) 
        { 
            item.setInvulnerable(true); 
        }
        return item;
    }
    
    public static void sendTitle(final Player p,final String title, final String subtitle) 
    {
        Utils.sendTitle(p, title, subtitle, 10, 60, 10); 
    }
    
    public static void sendTitle(final Player p,final String title, final String subtitle, final int fadein, final int stay, final int fadeout)
    {
        if(Utils.bukkitVersion("1.8","1.9","1.10")) 
        { 
            p.sendTitle(title, subtitle); 
        }
        else{ p.sendTitle(title, subtitle, fadein, stay, fadeout); }
    }

    public static String chunkToString(final Chunk c)
    { 
        return new StringBuilder().append(c.getX()).append(c.getZ()).toString(); 
    }

    public static ItemStack getItemByPath(final ConfigurationSection path)
    { 
        return getItemByPath(null,path);
    }
    
    public static ItemStack getItemByPath(final Material id, final ConfigurationSection path)
    {
        ItemStack item=null;
        try
        {
            if(Utils.bukkitVersion("1.8") && path.getString("id","").equalsIgnoreCase("SPLASH_POTION")) 
            {
                item=new Potion(PotionType.valueOf(path.getString("type").toUpperCase())).splash().toItemStack(1);
            }
            else 
            {
                item=new ItemStack(id!=null?id:Material.valueOf(path.getString("id").toUpperCase())); 
            }

            ItemMeta meta=item.getItemMeta();
            switch(item.getType())
            {
                case POTION:
                case SPLASH_POTION:
                case LINGERING_POTION:
                case TIPPED_ARROW:
                    if(path.contains("color") && bukkitVersion("1.11","1.12"))
                    { 
                        ((PotionMeta)meta).setColor(ColorEnum.valueOf(path.getString("color").toUpperCase()).getBukkitColor()); 
                    }

                    for(Iterator<String[]> it=path.getStringList("PotionEffect").stream().map(str -> str.split(":")).iterator();it.hasNext();)
                    {
                        String[] array=it.next();
                        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.getByName(array[0]),Integer.parseInt(array[1]),Integer.parseInt(array[2])), true);
                    }
                    break;
                case MONSTER_EGG:
                    if(bukkitVersion("1.8")) 
                    {
                        item=new SpawnEgg(EntityType.valueOf(path.getString("data","PIG").toUpperCase())).toItemStack(1); 
                    }
                    else if(bukkitVersion("1.9","1.10")) 
                    { 
                        meta=NBT.setSpawnedType(item, EntityType.valueOf(path.getString("data","PIG").toUpperCase())).getItemMeta(); 
                    }
                    else 
                    {
                        ((SpawnEggMeta)meta).setSpawnedType(EntityType.valueOf(path.getString("data","PIG").toUpperCase()));   
                    }
                    break;
                default:
                    item.setDurability((short)path.getInt("data",0));
                    break;
            }
            meta.setDisplayName(color(path.getString("name",new String())));
            meta.setLore(colorList(path.getStringList("lore")));
            for(String str:path.getStringList("flag")) 
            { 
                meta.addItemFlags(ItemFlag.valueOf(str)); 
            }

            try
            {
                for(Map.Entry<Enchantment,Integer> entry:path.getStringList("enchant").stream()
                        .map(str -> str.split("#"))
                        .collect(Collectors.toMap(array -> Enchantment.getByName(array[0].toUpperCase()), array -> Integer.parseInt(array[1])))
                        .entrySet()) { meta.addEnchant(entry.getKey(), entry.getValue(), true); }
            }
            catch(NullPointerException ex) 
            {
                Bukkit.getLogger().severe("Wrong enchantment name!"); 
            }

            if(Utils.bukkitVersion("1.11","1.12")) 
            { 
                meta.setUnbreakable(path.getBoolean("unbreakable")); 
            }
            else if(Utils.bukkitVersion("1.9","1.10")) 
            {
                meta.spigot().setUnbreakable(path.getBoolean("unbreakable")); 
            }

            item.setItemMeta(meta);
        }
        catch(Exception ex) 
        {
            ex.printStackTrace();
            Bukkit.getLogger().severe("Something went wrong during an item creation");
        }
        return item;
    }
    
    public static ItemStack parseItemVariable(final ItemStack item,final String s,final String replace)
    {
        ItemMeta meta=item.getItemMeta();
        meta.setDisplayName(Utils.color(meta.getDisplayName().replace(s, replace)));
        meta.setLore(Utils.colorList(meta.getLore().stream().map(lore -> lore.replace(s, replace)).collect(Collectors.toList())));
        item.setItemMeta(meta);
        return item;
    }
    
    public static Boolean isUnbreakable(final ItemStack item)
    {
        return checkUnbreakable.test(item);
    }
    
    public static Boolean bukkitVersion(final String... version)
    { 
        return Arrays.stream(version).anyMatch(s -> Bukkit.getVersion().contains(s)); 
    }
    
    public static class FireworkBuilder
    {
        private final JavaPlugin instance;
        private final Builder builder;
        public FireworkBuilder(final JavaPlugin instance)
        {
            this.instance=instance;
            builder=FireworkEffect.builder();
        }
        
        public FireworkBuilder addColor(final Color... colors)
        {
            builder.withColor(colors);
            return this;
        }
        
        private FireworkEffect effect;
        public FireworkBuilder build()
        {
            effect=builder.build();
            return this;
        }
        
        public void spawn(final Location l)
        {
            Firework fw=(Firework)l.getWorld().spawnEntity(l, EntityType.FIREWORK);

            FireworkMeta meta=fw.getFireworkMeta();
            meta.addEffect(effect);
            fw.setFireworkMeta(meta);

            fw.setMetadata(Utils.FIREWORK_DATA, new FixedMetadataValue(instance,"Damage"));

            new BukkitRunnable()
            {
                @Override
                public void run() 
                {
                    fw.detonate();
                }
            }.runTaskLater(instance, 5);
        }
    }
}

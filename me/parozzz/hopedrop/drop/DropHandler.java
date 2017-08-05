/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.hopedrop.drop;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import me.parozzz.hopedrop.Parser;
import me.parozzz.hopedrop.Utils;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.drop.block.BlockConditionManager;
import me.parozzz.hopedrop.drop.block.BlockDrop;
import me.parozzz.hopedrop.drop.item.ItemManager;
import me.parozzz.hopedrop.drop.item.NumberManager;
import me.parozzz.hopedrop.drop.mob.MobConditionManager;
import me.parozzz.hopedrop.drop.mob.MobDrop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 *
 * @author Stefania
 */
public class DropHandler implements Listener
{
    private final EnumMap<EntityType, MobDropOptions> mobs;
    private final EnumMap<Material, BlockDropOptions> blocks;
    
    public DropHandler(final FileConfiguration mob, final FileConfiguration block)
    {
        mobs=new EnumMap(EntityType.class);
        blocks=new EnumMap(Material.class);
        
        mob.getKeys(false).stream()
                .collect(Collectors.toMap(str -> mob.getConfigurationSection(str), str -> EntityType.valueOf(str.toUpperCase())))
                .forEach((path, et) -> 
                {
                    MobDropOptions options=new MobDropOptions();
                    path.getKeys(false).forEach(str -> 
                    {
                        if(str.equalsIgnoreCase("drop"))
                        {
                            options.setDropDefault(path.getBoolean("drop"));
                        }
                        else if(str.equalsIgnoreCase("exp"))
                        {
                            String values=path.getString("exp");
                            
                            int min=Integer.valueOf(values.substring(0, values.indexOf("-")));
                            int max=Integer.valueOf(values.substring(values.indexOf("-")+1));
                            
                            options.setExpValues(min, max);
                        }
                        else
                        {
                            ConfigurationSection dropPath=path.getConfigurationSection(str);
                            
                            ChanceManager chance=Parser.parseChance(dropPath);
                            MobConditionManager cond=(MobConditionManager)Parser.parseCondition(ConditionManagerType.MOB, dropPath.getStringList("condition"));
                            ItemManager item=Parser.parseItemManager(dropPath.getConfigurationSection("Item"));
                            
                            options.addDrop(new MobDrop(chance, cond, item));
                        }
                    });
                    
                    mobs.put(et, options);
                });
        
        block.getKeys(false).stream()
                .collect(Collectors.toMap(str -> block.getConfigurationSection(str), str -> Material.valueOf(str.toUpperCase())))
                .forEach((path, type) -> 
                {
                    BlockDropOptions options=new BlockDropOptions();
                    path.getKeys(false).forEach(str -> 
                    {
                        if(str.equalsIgnoreCase("drop"))
                        {
                            options.setDropDefault(path.getBoolean("drop"));
                        }
                        else if(str.equalsIgnoreCase("exp"))
                        {
                            String values=path.getString("exp");
                            
                            int min=Integer.valueOf(values.substring(0, values.indexOf("-")));
                            int max=Integer.valueOf(values.substring(values.indexOf("-")+1));
                            
                            options.setExpValues(min, max);
                        }
                        else
                        {
                            ConfigurationSection dropPath=path.getConfigurationSection(str);
                            
                            ChanceManager chance=Parser.parseChance(dropPath);
                            BlockConditionManager cond=(BlockConditionManager)Parser.parseCondition(ConditionManagerType.BLOCK, dropPath.getStringList("condition"));
                            ItemManager item=Parser.parseItemManager(dropPath.getConfigurationSection("Item"));
                            
                            options.addDrop(new BlockDrop(chance, cond, item));
                        }
                    });

                    blocks.put(type, options); 
                });
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    private void onMobDeath(final EntityDeathEvent e)
    {
        Optional.ofNullable(mobs.get(e.getEntityType())).ifPresent(options -> 
        {
            if(options.hasExpModified())
            {
                e.setDroppedExp(options.getRandomExp());
            }

            if(!options.getDropDefault())
            {
                e.getDrops().clear();
            }

            Player killer=e.getEntity().getKiller();
            
            options.getDrops().stream()
                    .filter(bd -> 
                    { 
                        MobConditionManager manager=bd.getConditionManager();
                        return killer==null?
                                manager.checkMob(e.getEntity()) && manager.checkGeneric(e.getEntity().getLocation()):
                                manager.checkAll(killer, Utils.getHand(killer), e.getEntity().getLocation(), e.getEntity());
                    })
                    .filter(bd -> killer==null?bd.getChanceManager().random():bd.getChanceManager().random(killer))
                    .forEach(bd -> 
                    {
                        ItemManager item=bd.getItemManager();

                        Item entity=item.hasModifiersDrop()&&killer!=null?
                                item.modifiersDrop(e.getEntity().getLocation(), killer):
                                item.simpleDrop(e.getEntity().getLocation());
                    });
        });
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    private void onBlockBreak(final BlockBreakEvent e)
    {
        Optional.ofNullable(blocks.get(e.getBlock().getType())).ifPresent(options -> 
        {
            if(options.hasExpModified())
            {
                e.setExpToDrop(options.getRandomExp());
            }
            
            if(Utils.bukkitVersion("1.12", "1.12.1"))
            {
                e.setDropItems(options.getDropDefault());
            }
            
            options.getDrops().stream()
                    .filter(bd -> bd.getConditionManager().checkAll(e.getPlayer(), Utils.getHand(e.getPlayer()), e.getBlock().getLocation()))
                    .filter(bd -> bd.getChanceManager().random(e.getPlayer()))
                    .forEach(bd -> 
                    {
                        ItemManager item=bd.getItemManager();
                        
                        Item entity=item.hasModifiersDrop()?item.modifiersDrop(e.getBlock().getLocation(), e.getPlayer()):item.simpleDrop(e.getBlock().getLocation());
                    });
        });
    }
    
    private class MobDropOptions extends DropOptions
    {
        private final Set<MobDrop> drops;
        public MobDropOptions()
        {
            drops=new HashSet<>();
        }
        
        public void addDrop(final MobDrop drop)
        {
            drops.add(drop);
        }
        
        public Set<MobDrop> getDrops()
        {
            return drops;
        }
    }
    
    private class BlockDropOptions extends DropOptions
    {
        private final Set<BlockDrop> drops;
        public BlockDropOptions()
        {
            drops=new HashSet<>();
        }
        
        public void addDrop(final BlockDrop drop)
        {
            drops.add(drop);
        }
        
        public Set<BlockDrop> getDrops()
        {
            return drops;
        }
    }
    
    private abstract class DropOptions
    {
        private boolean dropDefault=true;
        public void setDropDefault(final boolean bln)
        {
            dropDefault=bln;
        }

        public boolean getDropDefault()
        {
            return dropDefault;
        }

        private NumberManager expManager;
        public void setExpValues(final int min, final int max)
        {
            expManager=new NumberManager(min, max);
        }

        public boolean hasExpModified()
        {
            return expManager!=null;
        }

        public int getRandomExp()
        {
            return expManager.generateBetween();
        }
    }
}

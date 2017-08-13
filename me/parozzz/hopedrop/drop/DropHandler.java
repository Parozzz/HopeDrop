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
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.parozzz.hopedrop.Configs;
import me.parozzz.hopedrop.Dependency;
import me.parozzz.hopedrop.Parser;
import me.parozzz.hopedrop.utilities.Utils;
import me.parozzz.hopedrop.utilities.Utils.CreatureType;
import me.parozzz.hopedrop.chance.ChanceManager;
import me.parozzz.hopedrop.drop.ConditionManager.ConditionManagerType;
import me.parozzz.hopedrop.drop.block.BlockConditionManager;
import me.parozzz.hopedrop.drop.block.BlockDrop;
import me.parozzz.hopedrop.drop.item.ItemManager;
import me.parozzz.hopedrop.drop.item.NumberManager;
import me.parozzz.hopedrop.drop.mob.MobConditionManager;
import me.parozzz.hopedrop.drop.mob.MobDrop;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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
    private final EnumMap<CreatureType, MobDropOptions> mobs;
    private final EnumMap<Material, BlockDropOptions> blocks;
    
    public DropHandler(final FileConfiguration mob, final FileConfiguration block)
    {
        mobs=new EnumMap(CreatureType.class);
        blocks=new EnumMap(Material.class);
        
        mob.getKeys(false).stream()
                .collect(Collectors.toMap(str -> mob.getConfigurationSection(str), str -> CreatureType.valueOf(str.toUpperCase())))
                .forEach((path, ct) -> 
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
                        else if(str.equalsIgnoreCase("money"))
                        {
                            String values=path.getString("money");
                            
                            if(values.contains("-"))
                            {
                                String[] array=values.split("-");
                                
                                double min=Double.valueOf(array[0]);
                                double max=Double.valueOf(array[1]);
                                
                                options.setRandomMoneyDrop(min, max);
                            }
                            else
                            {
                                options.setMoneyDrop(Double.valueOf(values));
                            }
                        }
                        else
                        {
                            ConfigurationSection dropPath=path.getConfigurationSection(str);
                            
                            ChanceManager chance=Parser.parseChance(dropPath);
                            MobConditionManager cond=(MobConditionManager)Parser.parseCondition(ConditionManagerType.MOB, dropPath.getStringList("condition"));
                            ItemManager item=Parser.parseItemManager(dropPath.getConfigurationSection("Item"));
                            RewardManager reward=Parser.parseRewardManager(dropPath.getStringList("reward"));
                            
                            options.addDrop(new MobDrop(chance, cond, item, reward));
                        }
                    });
                    
                    mobs.put(ct, options);
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
                        else if(str.equalsIgnoreCase("money"))
                        {
                            String values=path.getString("money");
                            
                            if(values.contains("-"))
                            {
                                String[] array=values.split("-");
                                
                                double min=Double.valueOf(array[0]);
                                double max=Double.valueOf(array[1]);
                                
                                options.setRandomMoneyDrop(min, max);
                            }
                            else
                            {
                                options.setMoneyDrop(Double.valueOf(values));
                            }
                        }
                        else
                        {
                            ConfigurationSection dropPath=path.getConfigurationSection(str);
                            
                            ChanceManager chance=Parser.parseChance(dropPath);
                            BlockConditionManager cond=(BlockConditionManager)Parser.parseCondition(ConditionManagerType.BLOCK, dropPath.getStringList("condition"));
                            ItemManager item=Parser.parseItemManager(dropPath.getConfigurationSection("Item"));
                            RewardManager reward=Parser.parseRewardManager(dropPath.getStringList("reward"));
                            
                            options.addDrop(new BlockDrop(chance, cond, item, reward));
                        }
                    });

                    blocks.put(type, options); 
                });
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    private void onMobDeath(final EntityDeathEvent e)
    {
        Optional.ofNullable(mobs.get(CreatureType.getByLivingEntity(e.getEntity()))).ifPresent(options -> 
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
            
            if(options.hasMoneyDrop() && killer!=null)
            {
                options.addMoney(killer);
            }
            
            options.getDrops().stream()
                    .filter(bd -> 
                    { 
                        MobConditionManager manager=bd.getConditionManager();
                        return killer==null?
                                manager.getMobCondition().checkAll(e.getEntity()) && manager.getGenericCondition().checkAll(e.getEntity().getLocation()):
                                manager.checkAll(e.getEntity().getLocation(), killer, Utils.getMainHand(killer.getEquipment()), e.getEntity());
                    })
                    .filter(bd -> killer==null?bd.getChanceManager().random():bd.getChanceManager().random(killer))
                    .forEach(bd -> 
                    {
                        bd.getRewardManager().executeAll(e.getEntity().getLocation());
                        ItemManager item=bd.getItemManager();
                        if(killer!=null)
                        {
                            bd.getRewardManager().executeAll(killer);
                            if(item.hasModifiersDrop())
                            {
                                item.modifiersDrop(e.getEntity().getLocation(), killer);
                            }
                        }
                        else
                        {
                            item.simpleDrop(e.getEntity().getLocation());
                        }
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
            
            if(options.hasMoneyDrop())
            {
                options.addMoney(e.getPlayer());
            }
            
            if(Utils.bukkitVersion("1.12", "1.12.1"))
            {
                e.setDropItems(options.getDropDefault());
            }
            
            options.getDrops().stream()
                    .filter(bd -> bd.getConditionManager().checkAll(e.getBlock().getLocation(), e.getPlayer(), Utils.getMainHand(e.getPlayer().getEquipment()), e.getBlock()))
                    .filter(bd -> bd.getChanceManager().random(e.getPlayer()))
                    .forEach(bd -> 
                    {
                        bd.getRewardManager().executeAll(e.getBlock().getLocation());
                        bd.getRewardManager().executeAll(e.getPlayer());
                        
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
        
        private Consumer<Player> moneyGiver;
        public void setMoneyDrop(final double money)
        {
            if(Dependency.isEconomyEnabled())
            {
                moneyGiver = p -> 
                {
                    if(Dependency.eco.depositPlayer(p, money).transactionSuccess())
                    {
                        Configs.sendMoneyMessage(p, money);
                    }
                };
            }
        }
        
        public void setRandomMoneyDrop(final double min, final double max)
        {
            if(Dependency.isEconomyEnabled())
            {
                moneyGiver = p -> 
                {
                    double money=ThreadLocalRandom.current().nextDouble(min, max);
                    if(Dependency.eco.depositPlayer(p, money).transactionSuccess())
                    {
                        Configs.sendMoneyMessage(p, money);
                    }
                };
            }
        }
        
        public boolean hasMoneyDrop()
        {
            return moneyGiver!=null;
        }
        
        public void addMoney(final Player p)
        {
            moneyGiver.accept(p);
        }
    }
}

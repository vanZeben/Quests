package me.blackvein.quests;

import java.io.File;
import java.util.Map.Entry;
import java.util.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Quester{

    String name;
    Quest currentQuest;
    Stage currentStage;
    int questPoints = 0;
    Quests plugin;
    boolean isTalking = false;

    LinkedList<Quest> completedQuests = new LinkedList<Quest>();

    Map<Quest, Long> completedTimes = new HashMap<Quest, Long>();

    Map<Material, Integer> blocksDamaged = new EnumMap<Material, Integer>(Material.class);

    Map<Material, Integer> blocksBroken = new EnumMap<Material, Integer>(Material.class);

    Map<Material, Integer> blocksPlaced = new EnumMap<Material, Integer>(Material.class);

    Map<Material, Integer> itemsCollected = new EnumMap<Material, Integer>(Material.class);

    Map<Material, Integer> blocksUsed = new EnumMap<Material, Integer>(Material.class);

    Map<Material, Integer> blocksCut = new EnumMap<Material, Integer>(Material.class);

    Map<Integer, Integer> potionsBrewed = new HashMap<Integer, Integer>();

    int fishCaught = 0;
    int playersKilled = 0;

    Map<String, Long> playerKillTimes = new HashMap<String, Long>();

    Map<Map<Enchantment, Material>, Integer> itemsEnchanted = new HashMap<Map<Enchantment, Material>, Integer>();

    LinkedList<EntityType> mobsKilled = new LinkedList<EntityType>();
    LinkedList<Integer> mobNumKilled = new LinkedList<Integer>();
    LinkedList<Location> locationsToKillWithin = new LinkedList<Location>();
    LinkedList<Integer> radiiToKillWithin = new LinkedList<Integer>();

    Map<NPC, Boolean> citizensInteracted = new HashMap<NPC, Boolean>();

    LinkedList<Location> locationsReached = new LinkedList<Location>();
    LinkedList<Boolean> hasReached = new LinkedList<Boolean>();
    LinkedList<Integer> radiiToReachWithin = new LinkedList<Integer>();

    Map<EntityType, Integer> mobsTamed = new EnumMap<EntityType, Integer>(EntityType.class);

    Map<DyeColor, Integer> sheepSheared = new EnumMap<DyeColor, Integer>(DyeColor.class);

    Map<Material, Integer> itemsCrafted = new EnumMap<Material, Integer>(Material.class);

    public Quester(Quests newPlugin){

        plugin = newPlugin;

    }

    public LinkedList<String> getObjectives(){

        LinkedList<String> unfinishedObjectives = new LinkedList<String>();
        LinkedList<String> finishedObjectives = new LinkedList<String>();
        LinkedList<String> objectives = new LinkedList<String>();

        for(Entry e : currentStage.blocksToDamage.entrySet()){

            for(Entry e2 : blocksDamaged.entrySet()){

                if(((Material) e2.getKey()).equals((Material) e.getKey())){

                    if(((Integer) e2.getValue()) < ((Integer) e.getValue())){

                        unfinishedObjectives.add(ChatColor.GREEN + "Damage " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + (Integer)e2.getValue() + "/" + ((Integer)e.getValue()));

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Damage " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + ((Integer)e2.getValue()) + "/" + ((Integer)e.getValue()));

                    }

                }

            }

        }

        for(Entry e : currentStage.blocksToBreak.entrySet()){

            for(Entry e2 : blocksBroken.entrySet()){

                if(((Material) e2.getKey()).equals((Material) e.getKey())){

                    if(((Integer) e2.getValue()) < ((Integer) e.getValue())){

                        unfinishedObjectives.add(ChatColor.GREEN + "Break " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + ((Integer)e2.getValue()) + "/" + ((Integer)e.getValue()));

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Break " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + ((Integer)e2.getValue()) + "/" + ((Integer)e.getValue()));

                    }

                }

            }

        }

        for(Entry e : currentStage.blocksToPlace.entrySet()){

            for(Entry e2 : blocksPlaced.entrySet()){

                if(((Material) e2.getKey()).equals((Material) e.getKey())){

                    if(((Integer) e2.getValue()) < ((Integer) e.getValue())){

                        unfinishedObjectives.add(ChatColor.GREEN + "Place " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + ((Integer)e2.getValue()) + "/" + ((Integer)e.getValue()));

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Place " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + ((Integer)e2.getValue()) + "/" + ((Integer)e.getValue()));

                    }

                }

            }

        }

        for(Entry e : currentStage.itemsToCollect.entrySet()){

            Map<Material, Integer> map = (Map<Material, Integer>) e.getKey();

            for(Entry e2 : map.entrySet()){

                for(Entry e3 : itemsCollected.entrySet()){

                    if(((Material) e3.getKey()).equals((Material) e2.getKey())){

                        if(((Integer) e3.getValue()) < ((Integer) e2.getValue())){

                            unfinishedObjectives.add(ChatColor.GREEN + "Collect " + Quester.prettyItemString(((Material)e3.getKey()).getId()) + ": " + ((Integer)e3.getValue()) + "/" + ((Integer)e2.getValue()));

                        }else {

                            finishedObjectives.add(ChatColor.GRAY + "Collect " + Quester.prettyItemString(((Material)e3.getKey()).getId()) + ": " + ((Integer)e3.getValue()) + "/" + ((Integer)e2.getValue()));

                        }

                    }

                }

            }

        }

        for(Entry e : currentStage.blocksToUse.entrySet()){

            for(Entry e2 : blocksUsed.entrySet()){

                if(((Material) e2.getKey()).equals((Material) e.getKey())){

                    if(((Integer) e2.getValue()) < ((Integer) e.getValue())){

                        unfinishedObjectives.add(ChatColor.GREEN + "Use " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + ((Integer)e2.getValue()) + "/" + ((Integer)e.getValue()));

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Use " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + ((Integer)e2.getValue()) + "/" + ((Integer)e.getValue()));

                    }

                }

            }

        }

        for(Entry e : currentStage.blocksToCut.entrySet()){

            for(Entry e2 : blocksCut.entrySet()){

                if(((Material) e2.getKey()).equals((Material) e.getKey())){

                    if(((Integer) e2.getValue()) < ((Integer) e.getValue())){

                        unfinishedObjectives.add(ChatColor.GREEN + "Cut " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + ((Integer)e2.getValue()) + "/" + ((Integer)e.getValue()));

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Cut " + Quester.prettyItemString(((Material)e2.getKey()).getId()) + ": " + ((Integer)e2.getValue()) + "/" + ((Integer)e.getValue()));

                    }

                }

            }

        }

        if(currentStage.fishToCatch != null){

            if(fishCaught < currentStage.fishToCatch){

                unfinishedObjectives.add(ChatColor.GREEN + "Catch Fish: " + fishCaught + "/" + currentStage.fishToCatch);

            }else {

                finishedObjectives.add(ChatColor.GRAY + "Catch Fish: " + fishCaught + "/" + currentStage.fishToCatch);

            }

        }

        Map set;
        Map set2;
        Set<Enchantment> enchantSet;
        Set<Enchantment> enchantSet2;
        Collection<Material> matSet;
        Enchantment enchantment = null;
        Enchantment enchantment2 = null;
        Material mat = null;
        int num1;
        int num2;

        for(Entry e : currentStage.itemsToEnchant.entrySet()){

            for(Entry e2 : itemsEnchanted.entrySet()){

                set = (Map<Enchantment, Material>) e2.getKey();
                set2 = (Map<Enchantment, Material>) e.getKey();
                enchantSet = (Set<Enchantment>) set.keySet();
                enchantSet2 = (Set<Enchantment>) set2.keySet();
                for(Object o : enchantSet.toArray()){

                    enchantment = (Enchantment) o;

                }
                for(Object o : enchantSet2.toArray()){

                    enchantment2 = (Enchantment) o;

                }
                num1 = (Integer)e2.getValue();
                num2 = (Integer)e.getValue();

                matSet =  (Collection<Material>) set.values();

                for(Object o : matSet.toArray()){

                    mat = (Material) o;

                }

                if(enchantment2 == enchantment){

                    if(num1 < num2){

                        unfinishedObjectives.add(ChatColor.GREEN + "Enchant " + Quester.prettyItemString(mat.getId()) + " with " + Quester.prettyEnchantmentString(enchantment) + ": " + num1 + "/" + num2);

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Enchant " + Quester.prettyItemString(mat.getId()) + " with " + Quester.prettyEnchantmentString(enchantment) + ": " + num1 + "/" + num2);

                    }

                }

            }

        }

        for(EntityType e : currentStage.mobsToKill){

            for(EntityType e2 : mobsKilled){

                if(e == e2){

                    if(mobNumKilled.get(mobsKilled.indexOf(e2)) < currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(e))){

                        if(currentStage.locationsToKillWithin.isEmpty())
                            unfinishedObjectives.add(ChatColor.GREEN + "Kill " + Quester.prettyMobString(e) + ": " + (mobNumKilled.get(mobsKilled.indexOf(e2))) + "/" + (currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(e))));
                        else{
                            unfinishedObjectives.add(ChatColor.GREEN + "Kill " + Quester.prettyMobString(e) + " at " + currentStage.areaNames.get(currentStage.mobsToKill.indexOf(e)) + ": " + (mobNumKilled.get(mobsKilled.indexOf(e2))) + "/" + (currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(e))));
                        }
                    }else {

                        if(currentStage.locationsToKillWithin.isEmpty())
                            finishedObjectives.add(ChatColor.GRAY + "Kill " + Quester.prettyMobString(e) + ": " + (mobNumKilled.get(mobsKilled.indexOf(e2))) + "/" + (currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(e))));
                        else
                            finishedObjectives.add(ChatColor.GRAY + "Kill " + Quester.prettyMobString(e) + " at " + currentStage.areaNames.get(currentStage.mobsToKill.indexOf(e)) + ": " + (mobNumKilled.get(mobsKilled.indexOf(e2))) + "/" + (currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(e))));

                    }

                }

            }

        }

        if(currentStage.playersToKill != null){

            if(playersKilled < currentStage.playersToKill){

                unfinishedObjectives.add(ChatColor.GREEN + "Kill a Player: " + playersKilled + "/" + currentStage.playersToKill);

            }else {

                finishedObjectives.add(ChatColor.GRAY + "Kill a Player: " + playersKilled + "/" + currentStage.playersToKill);

            }

        }

        for(NPC n : currentStage.citizensToInteract){

            for(Entry e : citizensInteracted.entrySet()){

                if(((NPC) e.getKey()).equals(n)){

                    if(((Boolean) e.getValue()) == false){

                        unfinishedObjectives.add(ChatColor.GREEN + "Talk to " + n.getName());

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Talk to " + n.getName());

                    }

                }

            }

        }

        for(Entry e : currentStage.mobsToTame.entrySet()){

            for(Entry e2: mobsTamed.entrySet()){

                if(e.getKey().equals(e2.getKey())){

                    if((Integer) e2.getValue() < (Integer) e.getValue()){

                        unfinishedObjectives.add(ChatColor.GREEN + "Tame " + getCapitalized(((EntityType) e.getKey()).getName()) + ": " + ((Integer) e2.getValue()) + "/" + ((Integer) e.getValue()));

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Tame " + getCapitalized(((EntityType) e.getKey()).getName()) + ": " + ((Integer) e2.getValue()) + "/" + ((Integer) e.getValue()));

                    }

                }

            }

        }

        for(Entry e : currentStage.sheepToShear.entrySet()){

            for(Entry e2: sheepSheared.entrySet()){

                if(e.getKey().equals(e2.getKey())){

                    if((Integer) e2.getValue() < (Integer) e.getValue()){

                        unfinishedObjectives.add(ChatColor.GREEN + "Shear " + ((DyeColor) e.getKey()).name().toString().toLowerCase() + " sheep: " + ((Integer) e2.getValue()) + "/" + ((Integer) e.getValue()));

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Shear " + ((DyeColor) e.getKey()).name().toString().toLowerCase() + " sheep: " + ((Integer) e2.getValue()) + "/" + ((Integer) e.getValue()));

                    }

                }

            }

        }

        for(Entry e : currentStage.itemsToCraft.entrySet()){

            for(Entry e2: itemsCrafted.entrySet()){

                if(e.getKey().equals(e2.getKey())){

                    if((Integer) e2.getValue() < (Integer) e.getValue()){

                        unfinishedObjectives.add(ChatColor.GREEN + "Craft " + prettyItemString(((Material) e.getKey()).getId()) + ": " + ((Integer) e2.getValue()) + "/" + ((Integer) e.getValue()));

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Craft " + prettyItemString(((Material) e.getKey()).getId()) + ": " + ((Integer) e2.getValue()) + "/" + ((Integer) e.getValue()));

                    }

                }

            }

        }

        for(Location l : currentStage.locationsToReach){

            for(Location l2 : locationsReached){

                if(l.equals(l2)){

                    if(hasReached.get(locationsReached.indexOf(l2)) == false){

                        unfinishedObjectives.add(ChatColor.GREEN + "Go to " + currentStage.locationNames.get(currentStage.locationsToReach.indexOf(l)));

                    }else {

                        finishedObjectives.add(ChatColor.GRAY + "Go to " + currentStage.locationNames.get(currentStage.locationsToReach.indexOf(l)));

                    }

                }

            }

        }

        objectives.addAll(unfinishedObjectives);
        objectives.addAll(finishedObjectives);

        return objectives;

    }

    public boolean hasObjective(String s){

        if(currentStage == null)
            return false;

        if(s.equalsIgnoreCase("damageBlock")){

            if(currentStage.blocksToDamage.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("breakBlock")){

            if(currentStage.blocksToBreak.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("placeBlock")){

            if(currentStage.blocksToPlace.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("collectItem")){

            if(currentStage.itemsToCollect.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("useBlock")){

            if(currentStage.blocksToUse.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("cutBlock")){

            if(currentStage.blocksToCut.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("catchFish")){

            if(currentStage.fishToCatch != null)
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("enchantItem")){

            if(currentStage.itemsToEnchant.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("killMob")){

            if(currentStage.mobsToKill.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("killPlayer")){

            if(currentStage.playersToKill != null)
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("talkToNPC")){

            if(currentStage.citizensToInteract.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("tameMob")){

            if(currentStage.mobsToTame.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("shearSheep")){

            if(currentStage.sheepToShear.isEmpty())
                return false;
            else
                return true;

        }else if(s.equalsIgnoreCase("craftItem")){

            if(currentStage.itemsToCraft.isEmpty())
                return false;
            else
                return true;

        }else {

            if(currentStage.locationsToReach.isEmpty())
                return false;
            else
                return true;

        }

    }

    public void damageBlock(Material m){

        if(blocksDamaged.containsKey(m)){

            if(blocksDamaged.get(m) < currentStage.blocksToDamage.get(m)){
                int i = blocksDamaged.get(m);
                blocksDamaged.put(m, (i + 1));

                if(blocksDamaged.get(m).equals(currentStage.blocksToDamage.get(m)))
                    finishObjective("damageBlock", m, null, null, null, null, null, null, null, 0);

            }

        }

    }

    public void breakBlock(Material m){

        if(blocksBroken.containsKey(m)){

            if(blocksBroken.get(m) < currentStage.blocksToBreak.get(m)){
                int i = blocksBroken.get(m);
                blocksBroken.put(m, (i + 1));

                if(blocksBroken.get(m).equals(currentStage.blocksToBreak.get(m))){
                    finishObjective("breakBlock", m, null, null, null, null, null, null, null, 0);
                }
            }

        }

    }

    public void placeBlock(Material m){

        if(blocksPlaced.containsKey(m)){

            if(blocksPlaced.get(m) < currentStage.blocksToPlace.get(m)){
                int i = blocksPlaced.get(m);
                blocksPlaced.put(m, (i + 1));

                if(blocksPlaced.get(m).equals(currentStage.blocksToPlace.get(m)))
                    finishObjective("placeBlock", m, null, null, null, null, null, null, null, 0);
            }

        }

    }

    public void collectItem(ItemStack i){

        if(itemsCollected.containsKey(i.getType())){

            for(Map m : currentStage.itemsToCollect.keySet()){

                if(m.containsKey(i.getType())){

                    if(itemsCollected.get(i.getType()) < (Integer)m.get(i.getType())){
                        int integer = itemsCollected.get(i.getType());
                        if((i.getAmount() + integer) >= (Integer)m.get(i.getType()))
                            itemsCollected.put(i.getType(), (Integer)m.get(i.getType()));
                        else
                            itemsCollected.put(i.getType(), (integer + i.getAmount()));

                        if(itemsCollected.get(i.getType()).equals((Integer)m.get(i.getType())))
                            finishObjective("collectItem", null, i, null, null, null, null, null, null, 0);

                    }

                    break;

                }

            }

        }

    }

    public void useBlock(Material m){

        if(blocksUsed.containsKey(m)){

            if(blocksUsed.get(m) < currentStage.blocksToUse.get(m)){
                int i = blocksUsed.get(m);
                blocksUsed.put(m, (i + 1));

                if(blocksUsed.get(m).equals(currentStage.blocksToUse.get(m)))
                    finishObjective("useBlock", m, null, null, null, null, null, null, null, 0);

            }

        }

    }

    public void cutBlock(Material m){

        if(blocksCut.containsKey(m)){

            if(blocksCut.get(m) < currentStage.blocksToCut.get(m)){
                int i = blocksCut.get(m);
                blocksCut.put(m, (i + 1));

                if(blocksCut.get(m).equals(currentStage.blocksToCut.get(m)))
                    finishObjective("cutBlock", m, null, null, null, null, null, null, null, 0);

            }

        }

    }

    public void catchFish(){

        if(fishCaught < currentStage.fishToCatch){
            fishCaught++;

            if(((Integer) fishCaught).equals(currentStage.fishToCatch))
                    finishObjective("catchFish", null, null, null, null, null, null, null, null, 0);

        }

    }

    public void enchantItem(Enchantment e, Material m){

        for(Entry entry : itemsEnchanted.entrySet()){

            if(((Map)entry.getKey()).containsKey(e) && ((Map)entry.getKey()).containsValue(m)){

                for(Entry entry2 : currentStage.itemsToEnchant.entrySet()){

                    if(((Map)entry2.getKey()).containsKey(e) && ((Map)entry2.getKey()).containsValue(m)){

                        if((Integer)entry.getValue() < (Integer)entry2.getValue()){

                            Integer num = (Integer)entry.getValue() + 1;
                            itemsEnchanted.put(((Map)entry.getKey()),(num));

                            if((num).equals((Integer)entry2.getValue()))
                                finishObjective("enchantItem", m, null, e, null, null, null, null, null, 0);

                        }
                        break;

                    }

                }

                break;

            }

        }

    }

    public void killMob(Location l, EntityType e){

        if(mobsKilled.contains(e)){

                if(locationsToKillWithin.isEmpty() == false){

                    int index = mobsKilled.indexOf(e);
                    Location locationToKillWithin = locationsToKillWithin.get(index);
                    double radius = radiiToKillWithin.get(index);
                    int numKilled = mobNumKilled.get(index);
                    if(l.getX() < (locationToKillWithin.getX() + radius) && l.getX() > (locationToKillWithin.getX() - radius)){

                        if(l.getZ() < (locationToKillWithin.getZ() + radius) && l.getZ() > (locationToKillWithin.getZ() - radius)){

                            if(l.getY() < (locationToKillWithin.getY() + radius) && l.getY() > (locationToKillWithin.getY() - radius)){

                                if(numKilled < currentStage.mobNumToKill.get(index)){

                                    Integer numKilledInteger = numKilled + 1;

                                    mobNumKilled.set(index, numKilledInteger);

                                    if((numKilledInteger).equals(currentStage.mobNumToKill.get(index)))
                                        finishObjective("killMob", null, null, null, e, null, null, null, null, 0);

                                }

                            }

                        }

                    }

                }else {

                    if(mobNumKilled.get(mobsKilled.indexOf(e)) < currentStage.mobNumToKill.get(mobsKilled.indexOf(e))){

                                    mobNumKilled.set(mobsKilled.indexOf(e), mobNumKilled.get(mobsKilled.indexOf(e)) + 1);

                                    if((mobNumKilled.get(mobsKilled.indexOf(e))).equals(currentStage.mobNumToKill.get(mobsKilled.indexOf(e))))
                                        finishObjective("killMob", null, null, null, e, null, null, null, null, 0);

                    }

                }

        }

    }

    public void killPlayer(String player){

        if(playerKillTimes.containsKey(player)){

            long killTime = playerKillTimes.get(player);
            long comparator = plugin.killDelay*1000;
            long currentTime = System.currentTimeMillis();

            if((currentTime - killTime) < comparator){

                long minutes = (comparator - (currentTime - killTime))/60000;
                plugin.getServer().getPlayer(name).sendMessage(ChatColor.RED + "[Quests] Kill did not count. You must wait " + ChatColor.DARK_PURPLE + minutes + " minutes " + ChatColor.RED + " before you can kill " + ChatColor.DARK_PURPLE + player + " again.");
                return;

            }

        }

        playerKillTimes.put(player, System.currentTimeMillis());

        if(playersKilled < currentStage.playersToKill){
            playersKilled++;

            if(((Integer)playersKilled).equals(currentStage.playersToKill))
                    finishObjective("killPlayer", null, null, null, null, null, null, null, null, 0);

        }

    }

    public void interactWithNPC(NPC n){

        if(citizensInteracted.containsKey(n)){

            if(citizensInteracted.get(n) == false){
                citizensInteracted.put(n, true);
                finishObjective("talkToNPC", null, null, null, null, null, n, null, null, 0);
            }

        }

    }

    public void reachLocation(Location l){

                for(Location location : locationsReached){

                    int index = locationsReached.indexOf(location);
                    Location locationToReach = currentStage.locationsToReach.get(index);
                    double radius = radiiToReachWithin.get(index);
                    if(l.getX() < (locationToReach.getX() + radius) && l.getX() > (locationToReach.getX() - radius)){

                        if(l.getZ() < (locationToReach.getZ() + radius) && l.getZ() > (locationToReach.getZ() - radius)){

                            if(l.getY() < (locationToReach.getY() + radius) && l.getY() > (locationToReach.getY() - radius)){

                                if(hasReached.get(index) == false){

                                    hasReached.set(index, true);
                                    finishObjective("reachLocation", null, null, null, null, null, null, location, null, 0);

                                }

                            }

                        }

                    }

                }

    }

    public void tameMob(EntityType entity){

        if(mobsTamed.containsKey(entity)){

            mobsTamed.put(entity, (mobsTamed.get(entity) + 1));

            if(mobsTamed.get(entity).equals(currentStage.mobsToTame.get(entity)))
                finishObjective("tameMob", null, null, null, entity, null, null, null, null, 0);

        }

    }

    public void shearSheep(DyeColor color){

        if(sheepSheared.containsKey(color)){

            sheepSheared.put(color, (sheepSheared.get(color) + 1));

            if(sheepSheared.get(color).equals(currentStage.sheepToShear.get(color)))
                finishObjective("shearSheep", null, null, null, null, null, null, null, color, 0);

        }

    }

    public void craftItem(ItemStack i){

        if(itemsCrafted.containsKey(i.getType())){

            itemsCrafted.put(i.getType(), (itemsCrafted.get(i.getType()) + i.getAmount()));

            if(itemsCrafted.get(i.getType()) >= currentStage.itemsToCraft.get(i.getType()))
                finishObjective("craftItem", i.getType(), null, null, null, null, null, null, null, 0);

        }

    }

    public void finishObjective(String objective, Material material, ItemStack itemstack, Enchantment enchantment, EntityType mob, String player, NPC npc, Location location, DyeColor color, int dv){

        Player p = plugin.getServer().getPlayerExact(name);

        if(objective.equalsIgnoreCase("damageBlock")){

            String message = ChatColor.GREEN + "(Completed) Damage " + prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToDamage.get(material) + "/" + currentStage.blocksToDamage.get(material);
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("breakBlock")){

            String message = ChatColor.GREEN + "(Completed) Break " + prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToBreak.get(material) + "/" + currentStage.blocksToBreak.get(material);
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("placeBlock")){

            String message = ChatColor.GREEN + "(Completed) Place " + prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToPlace.get(material) + "/" + currentStage.blocksToPlace.get(material);
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("collectItem")){

            String message = ChatColor.GREEN + "(Completed) Collect " + prettyItemString(itemstack.getTypeId());
            for(Map<Material, Integer> map : currentStage.itemsToCollect.keySet()){

                if(map.containsKey(itemstack.getType())){

                    message = message + " " + map.get(itemstack.getType()) + "/" + map.get(itemstack.getType());
                    break;

                }

            }

            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("useBlock")){

            String message = ChatColor.GREEN + "(Completed) Use " + prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToUse.get(material) + "/" + currentStage.blocksToUse.get(material);
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("cutBlock")){

            String message = ChatColor.GREEN + "(Completed) Cut " + prettyItemString(material.getId());
            message = message + " " + currentStage.blocksToCut.get(material) + "/" + currentStage.blocksToCut.get(material);
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("catchFish")){

            String message = ChatColor.GREEN + "(Completed) Catch Fish ";
            message = message + " " + currentStage.fishToCatch + "/" + currentStage.fishToCatch;
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("enchantItem")){

            String message = ChatColor.GREEN + "(Completed) Enchant " + prettyItemString(material.getId()) + " with " + Quester.prettyEnchantmentString(enchantment);
            for(Map<Enchantment, Material> map : currentStage.itemsToEnchant.keySet()){

                if(map.containsKey(enchantment)){

                    message = message + " " + currentStage.itemsToEnchant.get(map) + "/" + currentStage.itemsToEnchant.get(map);
                    break;

                }

            }

            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("killMob")){

            String message = ChatColor.GREEN + "(Completed) Kill " + mob.getName();
            message = message + " " + currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(mob)) + "/" + currentStage.mobNumToKill.get(currentStage.mobsToKill.indexOf(mob));
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("killPlayer")){

            String message = ChatColor.GREEN + "(Completed) Kill a Player";
            message = message + " " + currentStage.playersToKill + "/" + currentStage.playersToKill;
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("talkToNPC")){

            String message = ChatColor.GREEN + "(Completed) Talk to " + npc.getName();
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("tameMob")){

            String message = ChatColor.GREEN + "(Completed) Tame " + getCapitalized(mob.getName());
            message = message + " " + currentStage.mobsToTame.get(mob) + "/" + currentStage.mobsToTame.get(mob);
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("shearSheep")){

            String message = ChatColor.GREEN + "(Completed) Shear " + color.name().toString().toLowerCase() + " sheep";
            message = message + " " + currentStage.sheepToShear.get(color) + "/" + currentStage.sheepToShear.get(color);
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else if(objective.equalsIgnoreCase("craftItem")){

            String message = ChatColor.GREEN + "(Completed) Craft " + prettyItemString(material.getId());
            message = message + " " + currentStage.itemsToCraft.get(material) + "/" + currentStage.itemsToCraft.get(material);
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }else {

            String message = ChatColor.GREEN + "(Completed) Go to " + currentStage.locationNames.get(currentStage.locationsToReach.indexOf(location));
            p.sendMessage(message);
            if (testComplete())
                currentQuest.nextStage(this);

        }

    }

    public boolean testComplete(){

        for(String s : getObjectives()){

            if(s.contains(ChatColor.GREEN.toString()))
                return false;

        }
        return true;

    }

    public void addEmpties(){

        if(currentStage.blocksToDamage.isEmpty() == false)
            for(Material m : currentStage.blocksToDamage.keySet()){

                blocksDamaged.put(m, 0);

            }

        if(currentStage.blocksToBreak.isEmpty() == false)
            for(Material m : currentStage.blocksToBreak.keySet()){

                blocksBroken.put(m, 0);

            }

        if(currentStage.blocksToPlace.isEmpty() == false)
            for(Material m : currentStage.blocksToPlace.keySet()){

                blocksPlaced.put(m, 0);

            }

        if(currentStage.itemsToCollect.isEmpty() == false)
            for(Entry e : currentStage.itemsToCollect.entrySet()){

                for(Object o : ((Map)e.getKey()).keySet()){

                    itemsCollected.put((Material) o, 0);

                }

            }

        if(currentStage.blocksToUse.isEmpty() == false)
            for(Material m : currentStage.blocksToUse.keySet()){

                blocksUsed.put(m, 0);

            }

        if(currentStage.blocksToCut.isEmpty() == false)
            for(Material m : currentStage.blocksToCut.keySet()){

                blocksCut.put(m, 0);

            }

        fishCaught = 0;

        if(currentStage.itemsToEnchant.isEmpty() == false)
            for(Entry e : currentStage.itemsToEnchant.entrySet()){

                Map<Enchantment, Material> map = (Map<Enchantment, Material>) e.getKey();
                itemsEnchanted.put(map, 0);

            }

        if(currentStage.mobsToKill.isEmpty() == false)
            for(EntityType e : currentStage.mobsToKill){

                mobsKilled.add(e);
                mobNumKilled.add(0);
                if(currentStage.locationsToKillWithin.isEmpty() == false)
                    locationsToKillWithin.add(currentStage.locationsToKillWithin.get(mobsKilled.indexOf(e)));
                if(currentStage.radiiToKillWithin.isEmpty() == false)
                    radiiToKillWithin.add(currentStage.radiiToKillWithin.get(mobsKilled.indexOf(e)));

            }

        playersKilled = 0;

        if(currentStage.citizensToInteract.isEmpty() == false)
            for(NPC n : currentStage.citizensToInteract){

                citizensInteracted.put(n, false);

            }

        if(currentStage.blocksToCut.isEmpty() == false)
            for(Material m : currentStage.blocksToCut.keySet()){

                blocksCut.put(m, 0);

            }

        if(currentStage.locationsToReach.isEmpty() == false)
            for(Location l : currentStage.locationsToReach){

                locationsReached.add(l);
                hasReached.add(false);
                radiiToReachWithin.add(currentStage.radiiToReachWithin.get(locationsReached.indexOf(l)));

            }

        if(currentStage.mobsToTame.isEmpty() == false)
            for(EntityType e : currentStage.mobsToTame.keySet()){

                mobsTamed.put(e, 0);

            }

        if(currentStage.sheepToShear.isEmpty() == false)
            for(DyeColor d : currentStage.sheepToShear.keySet()){

                sheepSheared.put(d, 0);

            }

        if(currentStage.itemsToCraft.isEmpty() == false)
            for(Material m : currentStage.itemsToCraft.keySet()){

                itemsCrafted.put(m, 0);

            }

    }

    public void reset(){

        blocksDamaged.clear();
        blocksBroken.clear();
        blocksPlaced.clear();
        itemsCollected.clear();
        blocksUsed.clear();
        blocksCut.clear();
        fishCaught = 0;
        itemsEnchanted.clear();
        mobsKilled.clear();
        mobNumKilled.clear();
        locationsToKillWithin.clear();
        radiiToKillWithin.clear();
        playersKilled = 0;
        citizensInteracted.clear();
        locationsReached.clear();
        hasReached.clear();
        radiiToReachWithin.clear();
        mobsTamed.clear();
        sheepSheared.clear();
        itemsCrafted.clear();

    }

    public static String getCapitalized(String target) {
        String firstLetter = target.substring(0,1);
        String remainder = target.substring(1);
        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

        return capitalized;
    }

    public static String prettyItemString(int itemID) {
        String baseString = Material.getMaterial(itemID).toString();
        String[] substrings = baseString.split("_");
        String prettyString = "";
        int size = 1;

        for (String s : substrings) {
            prettyString = prettyString.concat(Quester.getCapitalized(s));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        return prettyString;
    }

    public static String prettyPotionString(PotionType type){

        if(type.equals(PotionType.FIRE_RESISTANCE))
            return "Fire Resistance";
        else if(type.equals(PotionType.INSTANT_DAMAGE))
            return "Harming";
        else if(type.equals(PotionType.INSTANT_HEAL))
            return "Healing";
        else if(type.equals(PotionType.POISON))
            return "Poison";
        else if(type.equals(PotionType.REGEN))
            return "Regeneration";
        else if(type.equals(PotionType.SLOWNESS))
            return "Slowness";
        else if(type.equals(PotionType.SPEED))
            return "Swiftness";
        else if(type.equals(PotionType.STRENGTH))
            return "Strength";
        else if(type.equals(PotionType.WATER))
            return "Water";
        else
            return "Weakness";

    }

    public static String fullPotionString(short dv){

        Potion potion = Potion.fromDamage(dv);
        String potionName = "";
        boolean isPrimary = false;

        try{

            potionName = "Potion of " + Quester.prettyPotionString(potion.getType());

        }catch (NullPointerException e){ // Potion is primary

            isPrimary = true;

            if(dv == 0)
                potionName = "Water Bottle";
            else if(dv == 16)
                potionName = "Awkward Potion";
            else if(dv == 32)
                potionName = "Thick Potion";
            else if(dv == 64)
                potionName = "Mundane Potion (Extended)";
            else if(dv == 8192)
                potionName = "Mundane Potion";

        }

        if(isPrimary == false){

            if(potion.hasExtendedDuration())
                potionName = potionName + " (Extended)";
            else if(potion.getLevel() == 2)
                potionName = potionName + " II";

            if(potion.isSplash())
                potionName = "Splash " + potionName;

        }

        return potionName;

    }

    public static String prettyMobString(EntityType type) {

        String baseString = type.getName();
        String[] substrings = baseString.split("_");
        String prettyString = "";
        int size = 1;

        for (String s : substrings) {
            prettyString = prettyString.concat(Quester.getCapitalized(s));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        if(type.equals((EntityType.OCELOT)))
            prettyString = "Ocelot";

        return prettyString;
    }

    public static String prettyEnchantmentString(Enchantment e){

        String prettyString = "";

        if(e.equals(Enchantment.ARROW_DAMAGE))
            prettyString = "Power";
        else if (e.equals(Enchantment.ARROW_FIRE))
            prettyString = "Flame";
        else if (e.equals(Enchantment.ARROW_INFINITE))
            prettyString = "Infinity";
        else if (e.equals(Enchantment.ARROW_KNOCKBACK))
            prettyString = "Punch";
        else if (e.equals(Enchantment.DAMAGE_ALL))
            prettyString = "Sharpness";
        else if (e.equals(Enchantment.DAMAGE_ARTHROPODS))
            prettyString = "Bane of Arthropods";
        else if (e.equals(Enchantment.DAMAGE_UNDEAD))
            prettyString = "Smite";
        else if (e.equals(Enchantment.DIG_SPEED))
            prettyString = "Efficiency";
        else if (e.equals(Enchantment.DURABILITY))
            prettyString = "Unbreaking";
        else if (e.equals(Enchantment.FIRE_ASPECT))
            prettyString = "Fire Aspect";
        else if (e.equals(Enchantment.KNOCKBACK))
            prettyString = "Knockback";
        else if (e.equals(Enchantment.LOOT_BONUS_BLOCKS))
            prettyString = "Fortune";
        else if (e.equals(Enchantment.LOOT_BONUS_MOBS))
            prettyString = "Looting";
        else if (e.equals(Enchantment.OXYGEN))
            prettyString = "Respiration";
        else if (e.equals(Enchantment.PROTECTION_ENVIRONMENTAL))
            prettyString = "Protection";
        else if (e.equals(Enchantment.PROTECTION_EXPLOSIONS))
            prettyString = "Blast Protection";
        else if (e.equals(Enchantment.PROTECTION_FALL))
            prettyString = "Feather Falling";
        else if (e.equals(Enchantment.PROTECTION_FIRE))
            prettyString = "Fire Protection";
        else if (e.equals(Enchantment.PROTECTION_PROJECTILE))
            prettyString = "Projectile Protection";
        else if (e.equals(Enchantment.SILK_TOUCH))
            prettyString = "Silk Touch";
        else if (e.equals(Enchantment.WATER_WORKER))
            prettyString = "Aqua Affinity";

        return prettyString;

    }

    public static String prettyColorString(DyeColor color){

        if(color.equals(DyeColor.BLACK))
            return "Black";
        else if(color.equals(DyeColor.BLUE))
            return "Blue";
        else if(color.equals(DyeColor.BROWN))
            return "Brown";
        else if(color.equals(DyeColor.CYAN))
            return "Cyan";
        else if(color.equals(DyeColor.GRAY))
            return "Gray";
        else if(color.equals(DyeColor.GREEN))
            return "Green";
        else if(color.equals(DyeColor.LIGHT_BLUE))
            return "LightBlue";
        else if(color.equals(DyeColor.LIME))
            return "Lime";
        else if(color.equals(DyeColor.MAGENTA))
            return "Magenta";
        else if(color.equals(DyeColor.ORANGE))
            return "Orange";
        else if(color.equals(DyeColor.PINK))
            return "Pink";
        else if(color.equals(DyeColor.PURPLE))
            return "Purple";
        else if(color.equals(DyeColor.RED))
            return "Red";
        else if(color.equals(DyeColor.SILVER))
            return "Silver";
        else if(color.equals(DyeColor.WHITE))
            return "White";
        else
            return "Yellow";

    }

    public void saveData(){

        FileConfiguration data = getBaseData();
        try{
            data.save(new File(plugin.getDataFolder(), "data/" + name + ".yml"));
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public long getDifference(Quest q){

        long currentTime = System.currentTimeMillis();
        long lastTime;
        if(completedTimes.containsKey(q) == false){
            lastTime = System.currentTimeMillis();
            completedTimes.put(q, System.currentTimeMillis());
        }else{
            lastTime = completedTimes.get(q);
        }
        long comparator = q.redoDelay;
        long difference = (comparator - (currentTime - lastTime));

        return difference;


    }

    public FileConfiguration getBaseData(){

        FileConfiguration data = new YamlConfiguration();

            if(completedTimes.isEmpty() == false){

                List<String> questTimeNames = new LinkedList<String>();
                List<Long> questTimes = new LinkedList<Long>();

                for(Quest q : completedTimes.keySet()){

                    questTimeNames.add(q.name);
                    questTimes.add(completedTimes.get(q));

                }

                data.set("completedRedoableQuests", questTimeNames);
                data.set("completedQuestTimes", questTimes);

            }

            if(currentQuest != null){

                data.set("currentQuest", currentQuest.name);
                data.set("currentStage", currentQuest.stages.indexOf(currentStage));
                data.set("quest-points", questPoints);

                if(blocksDamaged.isEmpty() == false){

                    LinkedList<Integer> blockIds = new LinkedList<Integer>();
                    LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                    for(Material m : blocksDamaged.keySet()){
                        blockIds.add(m.getId());
                        blockAmounts.add(blocksDamaged.get(m));
                    }

                    data.set("blocks-damaged-ids", blockIds);
                    data.set("blocks-damaged-amounts", blockAmounts);

                }

                if(blocksBroken.isEmpty() == false){

                    LinkedList<Integer> blockIds = new LinkedList<Integer>();
                    LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                    for(Material m : blocksBroken.keySet()){
                        blockIds.add(m.getId());
                        blockAmounts.add(blocksBroken.get(m));
                    }

                    data.set("blocks-broken-ids", blockIds);
                    data.set("blocks-broken-amounts", blockAmounts);

                }

                if(blocksPlaced.isEmpty() == false){

                    LinkedList<Integer> blockIds = new LinkedList<Integer>();
                    LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                    for(Material m : blocksPlaced.keySet()){
                        blockIds.add(m.getId());
                        blockAmounts.add(blocksPlaced.get(m));
                    }

                    data.set("blocks-placed-ids", blockIds);
                    data.set("blocks-placed-amounts", blockAmounts);

                }

                if(itemsCollected.isEmpty() == false){

                    LinkedList<Integer> itemIds = new LinkedList<Integer>();
                    LinkedList<Integer> itemAmounts = new LinkedList<Integer>();

                    for(Material m : itemsCollected.keySet()){
                        itemIds.add(m.getId());
                        itemAmounts.add(itemsCollected.get(m));
                    }

                    data.set("items-collected-ids", itemIds);
                    data.set("items-collected-amounts", itemAmounts);

                }

                if(blocksUsed.isEmpty() == false){

                    LinkedList<Integer> blockIds = new LinkedList<Integer>();
                    LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                    for(Material m : blocksUsed.keySet()){
                        blockIds.add(m.getId());
                        blockAmounts.add(blocksUsed.get(m));
                    }

                    data.set("blocks-used-ids", blockIds);
                    data.set("blocks-used-amounts", blockAmounts);

                }

                if(blocksCut.isEmpty() == false){

                    LinkedList<Integer> blockIds = new LinkedList<Integer>();
                    LinkedList<Integer> blockAmounts = new LinkedList<Integer>();

                    for(Material m : blocksCut.keySet()){
                        blockIds.add(m.getId());
                        blockAmounts.add(blocksCut.get(m));
                    }

                    data.set("blocks-cut-ids", blockIds);
                    data.set("blocks-cut-amounts", blockAmounts);

                }

                if(currentStage.fishToCatch != null)
                    data.set("fish-caught", fishCaught);

                if(currentStage.playersToKill != null)
                    data.set("players-killed", playersKilled);

                if(itemsEnchanted.isEmpty() == false){

                    LinkedList<String> enchantments = new LinkedList<String>();
                    LinkedList<Integer> itemIds = new LinkedList<Integer>();
                    LinkedList<Integer> enchAmounts = new LinkedList<Integer>();

                    for(Entry e : itemsEnchanted.entrySet()){

                        Map<Enchantment, Material> enchMap = (Map<Enchantment, Material>) e.getKey();
                        enchAmounts.add(itemsEnchanted.get(enchMap));
                        for(Entry e2: enchMap.entrySet()){

                            enchantments.add(Quester.prettyEnchantmentString((Enchantment) e2.getKey()));
                            itemIds.add(((Material) e2.getValue()).getId());

                        }


                    }

                    data.set("enchantments", enchantments);
                    data.set("enchantment-item-ids", itemIds);
                    data.set("times-enchanted", enchAmounts);

                }

                if(mobsKilled.isEmpty() == false){

                    LinkedList<String> mobNames = new LinkedList<String>();
                    LinkedList<Integer> mobAmounts = new LinkedList<Integer>();
                    LinkedList<String> locations = new LinkedList<String>();
                    LinkedList<Integer> radii = new LinkedList<Integer>();

                    for(EntityType e : mobsKilled){

                        mobNames.add(Quester.prettyMobString(e));

                    }

                    for(int i : mobNumKilled){

                        mobAmounts.add(i);

                    }

                    data.set("mobs-killed", mobNames);
                    data.set("mobs-killed-amounts", mobAmounts);

                    if(locationsToKillWithin.isEmpty() == false){

                        for(Location l : locationsToKillWithin){

                            locations.add(l.getWorld().getName() + " " + l.getX() + " " + l.getY() + " " + l.getZ());

                        }

                        for(int i : radiiToKillWithin){

                            radii.add(i);

                        }

                        data.set("mob-kill-locations", locations);
                        data.set("mob-kill-location-radii", radii);

                    }

                }

                if(citizensInteracted.isEmpty() == false){

                    LinkedList<Integer> npcIds = new LinkedList<Integer>();
                    LinkedList<Boolean> hasTalked = new LinkedList<Boolean>();

                    for(NPC n : citizensInteracted.keySet()){

                        npcIds.add(n.getId());
                        hasTalked.add(citizensInteracted.get(n));

                    }

                    data.set("citizen-ids-to-talk-to", npcIds);
                    data.set("has-talked-to", hasTalked);

                }

                if(locationsReached.isEmpty() == false){

                    LinkedList<String> locations = new LinkedList<String>();
                    LinkedList<Boolean> has = new LinkedList<Boolean>();
                    LinkedList<Integer> radii = new LinkedList<Integer>();

                    for(Location l : locationsReached){

                        locations.add(l.getWorld().getName() + " " + l.getX() + " " + l.getY() + " " + l.getZ());

                    }

                    for(boolean b : hasReached)
                        has.add(b);

                    for(int i : radiiToReachWithin)
                        radii.add(i);

                    data.set("locations-to-reach", locations);
                    data.set("has-reached-location", has);
                    data.set("radii-to-reach-within", radii);

                }

                if(potionsBrewed.isEmpty() == false){

                    LinkedList<Integer> potionIds = new LinkedList<Integer>();
                    LinkedList<Integer> potionAmounts = new LinkedList<Integer>();

                    for(Entry entry : potionsBrewed.entrySet()){

                        potionIds.add((Integer)entry.getKey());
                        potionAmounts.add((Integer)entry.getValue());

                    }

                    data.set("potions-brewed-ids", potionIds);
                    data.set("potions-brewed-amounts", potionAmounts);

                }

                if(mobsTamed.isEmpty() == false){

                    LinkedList<String> mobNames = new LinkedList<String>();
                    LinkedList<Integer> mobAmounts = new LinkedList<Integer>();

                    for(EntityType e : mobsTamed.keySet()){

                        mobNames.add(Quester.prettyMobString(e));
                        mobAmounts.add(mobsTamed.get(e));

                    }

                    data.set("mobs-to-tame", mobNames);
                    data.set("mob-tame-amounts", mobAmounts);

                }

                if(sheepSheared.isEmpty() == false){

                    LinkedList<String> colors = new LinkedList<String>();
                    LinkedList<Integer> shearAmounts = new LinkedList<Integer>();

                    for(DyeColor d : sheepSheared.keySet()){

                        colors.add(Quester.prettyColorString(d));
                        shearAmounts.add(sheepSheared.get(d));

                    }

                    data.set("sheep-to-shear", colors);
                    data.set("sheep-sheared", shearAmounts);

                }

                if(itemsCrafted.isEmpty() == false){

                    LinkedList<Integer> itemIds = new LinkedList<Integer>();
                    LinkedList<Integer> itemAmounts = new LinkedList<Integer>();

                    for(Material m : itemsCrafted.keySet()){

                        itemIds.add(m.getId());
                        itemAmounts.add(itemsCrafted.get(m));

                    }

                    data.set("item-ids-to-craft", itemIds);
                    data.set("items-crafted", itemAmounts);

                }


            }else{

                data.set("currentQuest", "none");
                data.set("currentStage", "none");
                data.set("quest-points", questPoints);

            }

            if(completedQuests.isEmpty()){

                    data.set("completed-Quests", "none");

            }else {

                String[] completed = new String[completedQuests.size()];
                for(Quest q : completedQuests){

                    completed[completedQuests.indexOf(q)] = q.name;

                }
                data.set("completed-Quests", completed);

            }

            return data;

    }

    public boolean loadData(){

        FileConfiguration data = new YamlConfiguration();
        try{
            data.load(new File(plugin.getDataFolder(), "data/" + name + ".yml"));
        }catch (Exception e){
            return false;
        }

        if(data.contains("completedRedoableQuests")){

            for(String s : data.getStringList("completedRedoableQuests")){

                for(Object o : data.getList("completedQuestTimes")){

                    for(Quest q : plugin.quests){

                        if(q.name.equalsIgnoreCase(s)){
                            completedTimes.put(q, (Long) o);
                            break;
                        }

                    }

                }

            }

        }

        questPoints = data.getInt("quest-points");

        if(data.isList("completed-Quests")){

            for(String s : data.getStringList("completed-Quests")){

                for(Quest q : plugin.quests){

                    if(q.name.equalsIgnoreCase(s)){
                        completedQuests.add(q);
                        break;
                    }

                }

            }

        }else{
            completedQuests.clear();
        }

        if(data.getString("currentQuest").equalsIgnoreCase("none") == false){

            Quest quest = null;
            Stage stage = null;

            for(Quest q : plugin.quests){

                if(q.name.equalsIgnoreCase(data.getString("currentQuest"))){
                    quest = q;
                    break;
                }

            }

            if(quest == null)
                return true;

            int stageIndex = data.getInt("currentStage");

            for(Stage s : quest.stages){

                if(quest.stages.indexOf(s) == (stageIndex)){
                    stage = s;
                    break;
                }

            }

            currentQuest = quest;
            currentStage = stage;

            addEmpties();

            if(data.contains("blocks-damaged-ids")){

                List<Integer> ids = data.getIntegerList("blocks-damaged-ids");
                List<Integer> amounts = data.getIntegerList("blocks-damaged-amounts");

                for(int i : ids){

                    blocksDamaged.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if(data.contains("blocks-broken-ids")){

                List<Integer> ids = data.getIntegerList("blocks-broken-ids");
                List<Integer> amounts = data.getIntegerList("blocks-broken-amounts");

                for(int i : ids){

                    blocksBroken.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if(data.contains("blocks-placed-ids")){

                List<Integer> ids = data.getIntegerList("blocks-placed-ids");
                List<Integer> amounts = data.getIntegerList("blocks-placed-amounts");

                for(int i : ids){

                    blocksPlaced.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if(data.contains("items-collected-ids")){

                List<Integer> ids = data.getIntegerList("items-collected-ids");
                List<Integer> amounts = data.getIntegerList("items-collected-amounts");

                for(int i : ids){

                    itemsCollected.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if(data.contains("blocks-used-ids")){

                List<Integer> ids = data.getIntegerList("blocks-used-ids");
                List<Integer> amounts = data.getIntegerList("blocks-used-amounts");

                for(int i : ids){

                    blocksUsed.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if(data.contains("blocks-cut-ids")){

                List<Integer> ids = data.getIntegerList("blocks-cut-ids");
                List<Integer> amounts = data.getIntegerList("blocks-cut-amounts");

                for(int i : ids){

                    blocksCut.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

            if(data.contains("fish-caught"))
                fishCaught = data.getInt("fish-caught");

            if(data.contains("players-killed")){

                playersKilled = data.getInt("players-killed");

                List<String> playerNames = data.getStringList("player-killed-names");
                List<Long> killTimes = data.getLongList("kill-times");

                for(String s : playerNames){

                    playerKillTimes.put(s, killTimes.get(playerNames.indexOf(s)));

                }

            }

            if(data.contains("enchantments")){

                LinkedList<Enchantment> enchantments = new LinkedList<Enchantment>();
                LinkedList<Material> materials = new LinkedList<Material>();
                LinkedList<Integer> amounts = new LinkedList<Integer>();

                List<String> enchantNames = data.getStringList("enchantments");
                List<Integer> ids = data.getIntegerList("enchantment-item-ids");

                for(String s : enchantNames){

                    if(s.equalsIgnoreCase("Power")){

                            enchantments.add(Enchantment.ARROW_DAMAGE);

                        }else if(s.equalsIgnoreCase("Flame")){

                            enchantments.add(Enchantment.ARROW_FIRE);

                        }else if(s.equalsIgnoreCase("Infinity")){

                            enchantments.add(Enchantment.ARROW_INFINITE);

                        }else if(s.equalsIgnoreCase("Punch")){

                            enchantments.add(Enchantment.ARROW_KNOCKBACK);

                        }else if(s.equalsIgnoreCase("Sharpness")){

                            enchantments.add(Enchantment.DAMAGE_ALL);

                        }else if(s.equalsIgnoreCase("BaneOfArthropods")){

                            enchantments.add(Enchantment.DAMAGE_ARTHROPODS);

                        }else if(s.equalsIgnoreCase("Smite")){

                            enchantments.add(Enchantment.DAMAGE_UNDEAD);

                        }else if(s.equalsIgnoreCase("Efficiency")){

                            enchantments.add(Enchantment.DIG_SPEED);

                        }else if(s.equalsIgnoreCase("Unbreaking")){

                            enchantments.add(Enchantment.DURABILITY);

                        }else if(s.equalsIgnoreCase("FireAspect")){

                            enchantments.add(Enchantment.FIRE_ASPECT);

                        }else if(s.equalsIgnoreCase("Knockback")){

                            enchantments.add(Enchantment.KNOCKBACK);

                        }else if(s.equalsIgnoreCase("Fortune")){

                            enchantments.add(Enchantment.LOOT_BONUS_BLOCKS);

                        }else if(s.equalsIgnoreCase("Looting")){

                            enchantments.add(Enchantment.LOOT_BONUS_MOBS);

                        }else if(s.equalsIgnoreCase("Respiration")){

                            enchantments.add(Enchantment.OXYGEN);

                        }else if(s.equalsIgnoreCase("Protection")){

                            enchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);

                        }else if(s.equalsIgnoreCase("BlastProtection")){

                            enchantments.add(Enchantment.PROTECTION_EXPLOSIONS);

                        }else if(s.equalsIgnoreCase("FeatherFalling")){

                            enchantments.add(Enchantment.PROTECTION_FALL);

                        }else if(s.equalsIgnoreCase("FireProtection")){

                            enchantments.add(Enchantment.PROTECTION_FIRE);

                        }else if(s.equalsIgnoreCase("ProjectileProtection")){

                            enchantments.add(Enchantment.PROTECTION_PROJECTILE);

                        }else if(s.equalsIgnoreCase("SilkTouch")){

                            enchantments.add(Enchantment.SILK_TOUCH);

                        }else if(s.equalsIgnoreCase("AquaAffinity")){

                            enchantments.add(Enchantment.WATER_WORKER);

                        }

                        materials.add(Material.getMaterial(ids.get(enchantNames.indexOf(s))));
                        amounts.add(enchantNames.indexOf(s));

                }

                for(Enchantment e : enchantments){

                    Map<Enchantment, Material> map = new HashMap<Enchantment, Material>();
                    map.put(e, materials.get(enchantments.indexOf(e)));

                    itemsEnchanted.put(map, amounts.get(enchantments.indexOf(e)));

                }

            }

            if(data.contains("mobs-killed")){

                LinkedList<EntityType> mobs = new LinkedList<EntityType>();
                List<Integer> amounts = data.getIntegerList("mobs-killed-amounts");

                for(String s : data.getStringList("mobs-killed")){

                        if(s.equalsIgnoreCase("Blaze")){

                            mobs.add(EntityType.BLAZE);

                        }else if(s.equalsIgnoreCase("CaveSpider")){

                            mobs.add(EntityType.CAVE_SPIDER);

                        }else if(s.equalsIgnoreCase("Chicken")){

                            mobs.add(EntityType.CHICKEN);

                        }else if(s.equalsIgnoreCase("Cow")){

                            mobs.add(EntityType.COW);

                        }else if(s.equalsIgnoreCase("Creeper")){

                            mobs.add(EntityType.CREEPER);

                        }else if(s.equalsIgnoreCase("Enderman")){

                            mobs.add(EntityType.ENDERMAN);

                        }else if(s.equalsIgnoreCase("EnderDragon")){

                            mobs.add(EntityType.ENDER_DRAGON);

                        }else if(s.equalsIgnoreCase("Ghast")){

                            mobs.add(EntityType.GHAST);

                        }else if(s.equalsIgnoreCase("Giant")){

                            mobs.add(EntityType.GIANT);

                        }else if(s.equalsIgnoreCase("IronGolem")){

                            mobs.add(EntityType.IRON_GOLEM);

                        }else if(s.equalsIgnoreCase("MagmaCube")){

                            mobs.add(EntityType.MAGMA_CUBE);

                        }else if(s.equalsIgnoreCase("MushroomCow")){

                            mobs.add(EntityType.MUSHROOM_COW);

                        }else if(s.equalsIgnoreCase("Ocelot")){

                            mobs.add(EntityType.OCELOT);

                        }else if(s.equalsIgnoreCase("Pig")){

                            mobs.add(EntityType.PIG);

                        }else if(s.equalsIgnoreCase("PigZombie")){

                            mobs.add(EntityType.PIG_ZOMBIE);

                        }else if(s.equalsIgnoreCase("Sheep")){

                            mobs.add(EntityType.SHEEP);

                        }else if(s.equalsIgnoreCase("Silverfish")){

                            mobs.add(EntityType.SILVERFISH);

                        }else if(s.equalsIgnoreCase("Skeleton")){

                            mobs.add(EntityType.SKELETON);

                        }else if(s.equalsIgnoreCase("Slime")){

                            mobs.add(EntityType.SLIME);

                        }else if(s.equalsIgnoreCase("Snowman")){

                            mobs.add(EntityType.SNOWMAN);

                        }else if(s.equalsIgnoreCase("Spider")){

                            mobs.add(EntityType.SPIDER);

                        }else if(s.equalsIgnoreCase("Squid")){

                            mobs.add(EntityType.SQUID);

                        }else if(s.equalsIgnoreCase("Villager")){

                            mobs.add(EntityType.VILLAGER);

                        }else if(s.equalsIgnoreCase("Wolf")){

                            mobs.add(EntityType.WOLF);

                        }else if(s.equalsIgnoreCase("Zombie")){

                            mobs.add(EntityType.ZOMBIE);

                        }

                        mobsKilled.clear();
                        mobNumKilled.clear();

                        for(EntityType e : mobs){

                            mobsKilled.add(e);
                            mobNumKilled.add(amounts.get(mobs.indexOf(e)));

                        }

                        if(data.contains("mob-kill-locations")){

                            LinkedList<Location> locations = new LinkedList<Location>();
                            List<Integer> radii = data.getIntegerList("mob-kill-location-radii");

                            for(String loc : data.getStringList("mob-kill-locations")){

                                String[] info = loc.split(" ");
                                double x = Double.parseDouble(info[1]);
                                double y = Double.parseDouble(info[2]);
                                double z = Double.parseDouble(info[3]);
                                Location finalLocation = new Location(plugin.getServer().getWorld(info[0]), x, y, z);
                                locations.add(finalLocation);

                            }

                            locationsToKillWithin = locations;
                            radiiToKillWithin.clear();
                            for(int i : radii)
                                radiiToKillWithin.add(i);

                        }

                }

            }

            if(data.contains("citizen-ids-to-talk-to")){

                List<Integer> ids = data.getIntegerList("citizen-ids-to-talk-to");
                List<Boolean> has = data.getBooleanList("has-talked-to");

                for(int i : ids){

                    citizensInteracted.put(plugin.citizens.getNPCRegistry().getById(i), has.get(ids.indexOf(i)));

                }

            }

            if(data.contains("locations-to-reach")){

                LinkedList<Location> locations = new LinkedList<Location>();
                List<Boolean> has = data.getBooleanList("has-reached-location");
                List<Integer> radii = data.getIntegerList("radii-to-reach-within");

                for(String loc : data.getStringList("locations-to-reach")){

                    String[] info = loc.split(" ");
                    double x = Double.parseDouble(info[1]);
                    double y = Double.parseDouble(info[2]);
                    double z = Double.parseDouble(info[3]);
                    Location finalLocation = new Location(plugin.getServer().getWorld(info[0]), x, y, z);
                    locations.add(finalLocation);

                }

                locationsReached = locations;
                hasReached.clear();
                radiiToReachWithin.clear();

                for(boolean b : has)
                    hasReached.add(b);

                for(int i : radii)
                    radiiToReachWithin.add(i);

            }

            if(data.contains("potions-brewed-ids")){

                List<Integer> ids = data.getIntegerList("potions-brewed-ids");
                List<Integer> amounts = data.getIntegerList("potions-brewed-amounts");

                for(int i : ids){

                    potionsBrewed.put(i, amounts.get(ids.indexOf(i)));

                }

            }

            if(data.contains("mobs-to-tame")){

                List<String> mobs = data.getStringList("mobs-to-tame");
                List<Integer> amounts = data.getIntegerList("mob-tame-amounts");

                for(String mob : mobs){

                    if(mob.equalsIgnoreCase("Wolf")){

                        mobsTamed.put(EntityType.WOLF, amounts.get(mobs.indexOf(mob)));

                    }else {

                        mobsTamed.put(EntityType.OCELOT, amounts.get(mobs.indexOf(mob)));

                    }

                }

            }

            if(data.contains("sheep-to-shear")){

                List<String> colors = data.getStringList("sheep-to-shear");
                List<Integer> amounts = data.getIntegerList("sheep-sheared");

                for(String color : colors){

                        if(color.equalsIgnoreCase("Black")){

                            sheepSheared.put(DyeColor.BLACK, amounts.get(colors.indexOf(color)));

                        }else if(color.equalsIgnoreCase("Blue")){

                            sheepSheared.put(DyeColor.BLUE, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Brown")){

                            sheepSheared.put(DyeColor.BROWN, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Cyan")){

                            sheepSheared.put(DyeColor.CYAN, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Gray")){

                            sheepSheared.put(DyeColor.GRAY, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Green")){

                            sheepSheared.put(DyeColor.GREEN, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("LightBlue")){

                            sheepSheared.put(DyeColor.LIGHT_BLUE, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Lime")){

                            sheepSheared.put(DyeColor.LIME, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Magenta")){

                            sheepSheared.put(DyeColor.MAGENTA, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Orange")){

                            sheepSheared.put(DyeColor.ORANGE, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Pink")){

                            sheepSheared.put(DyeColor.PINK, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Purple")){

                            sheepSheared.put(DyeColor.PURPLE, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Red")){

                            sheepSheared.put(DyeColor.RED, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Silver")){

                            sheepSheared.put(DyeColor.SILVER, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("White")){

                            sheepSheared.put(DyeColor.WHITE, amounts.get(colors.indexOf(color)));

                        }if(color.equalsIgnoreCase("Yellow")){

                            sheepSheared.put(DyeColor.YELLOW, amounts.get(colors.indexOf(color)));

                        }

                }

            }

            if(data.contains("item-ids-to-craft")){

                List<Integer> ids = data.getIntegerList("item-ids-to-craft");
                List<Integer> amounts = data.getIntegerList("items-crafted");

                for(int i : ids){

                    itemsCrafted.put(Material.getMaterial(i), amounts.get(ids.indexOf(i)));

                }

            }

        }

        return true;

    }

    public void checkQuest(){

        if(currentQuest != null){

            boolean exists = false;

            for(Quest q : plugin.quests){

                if(q.name.equalsIgnoreCase(currentQuest.name)){

                    exists = true;
                    if(q.equals(currentQuest) == false){

                        currentStage = null;
                        reset();
                        if(plugin.getServer().getPlayer(name) != null)
                            plugin.getServer().getPlayer(name).sendMessage(ChatColor.GOLD + "[Quests] " + ChatColor.RED + "Your active Quest " + ChatColor.DARK_PURPLE + currentQuest.name + ChatColor.RED + " has been modified. You have quit the Quest.");
                        currentQuest = null;

                    }

                    break;

                }

            }

            if(exists == false){

                currentStage = null;
                reset();
                if(plugin.getServer().getPlayer(name) != null)
                    plugin.getServer().getPlayer(name).sendMessage(ChatColor.GOLD + "[Quests] " + ChatColor.RED + "Your active Quest " + ChatColor.DARK_PURPLE + currentQuest.name + ChatColor.RED + " no longer exists. You have quit the Quest.");
                currentQuest = null;

            }

        }

    }

}

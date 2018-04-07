package eadsproject.temp;
import java.util.*;

public class Location {
    private int cluster;
    private int stack;
    private int tier;
    private static List<Integer> totCluster = new ArrayList<>();
    private static List<Integer> totStack = new ArrayList<>();
    private static List<Integer> totTier = new ArrayList<>();
    
    public Location(int cluster, int stack, int tier){
        this.cluster = cluster;
        this.stack = stack;
        this.tier = tier;
        if(!totCluster.contains(cluster)){
            totCluster.add(cluster);
        }
        if(!totStack.contains(stack)){
            totStack.add(stack);
        }
        if(!totTier.contains(tier)){
            totTier.add(tier);
        }
    }
    
    public static int getTotalCluster(){
        return totCluster.size();
    }
    
    public static  int getTotalStack(){
        return totStack.size();
    }
    
    public static int getTotalTier(){
        return totTier.size();
    }
    
    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public int getStack() {
        return stack;
    }

    public void setStack(int stack) {
        this.stack = stack;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }
}

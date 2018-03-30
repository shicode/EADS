package eadsproject.temp;

public class Location {
    private int cluster;
    private int stack;
    private int tier;
    
    public Location(int cluster, int stack, int tier){
        this.cluster = cluster;
        this.stack = stack;
        this.tier = tier;
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject.temp;

import java.util.*;
import sun.misc.CRC16;

/**
 *
 * @author user
 */
public class Cluster {

    private int clusterID;
    private ArrayList<Activity> waitingList;
    private HashMap<String, ArrayList<Resource>> resourceMap;
    private ArrayList<Container> containerPendingList;

    public Cluster(int clusterID) {
        this.clusterID = clusterID;
        this.resourceMap = new HashMap<>();
        resourceMap.put("C", new ArrayList<>());
        resourceMap.put("T", new ArrayList<>());
        this.waitingList = new ArrayList<>();
        this.containerPendingList = new ArrayList<>();
    }

    public void setWaitingList(Activity waiting) {
        waitingList.add(waiting);
    }

    public void setResourceList(Resource resource) {
        
        if(resource.getResourceType().equals("C")){
            ArrayList<Resource> craneList = resourceMap.get("C");
            craneList.add(resource);
            resourceMap.put("C",craneList);
        }else{
            ArrayList<Resource> truckList = resourceMap.get("T");
            truckList.add(resource);
            resourceMap.put("T",truckList);
        }
    }

    public void setContainerPendingList(Container container) {
        containerPendingList.add(container);
    }

    public int getClusterID() {
        return clusterID;
    }

    public ArrayList<Activity> getWaitingList() {
        return waitingList;
    }

    public HashMap<String, ArrayList<Resource>> getResourceList() {
        return resourceMap;
    }

    public ArrayList<Container> getContainerPendingList() {
        return containerPendingList;
    }
    
    public int getCurrentNumberOfTrucks(){
        return resourceMap.get("T").size();
    }
    
    public int getCurrentNumberOfCranes(){
       return resourceMap.get("C").size();
    }
    
    /*public getAvailableCrane(){
        
    }*/

    //Input parameters: HashMap of cluster number (key) and 2D Array of ContainerAllocation (value),
    //                  2D array (cluster*stack) of integers which stores the value of the highest tiered containers
    public static ArrayList<Container> topTierContainers(int clstID, HashMap<Integer, ContainerAllocation[][]> grids, int[][] maxTierRef) {
        ArrayList<Container> topTierContainers = new ArrayList<>();
        //Iterate through all clusters

        ContainerAllocation[][] currentClusterGrid = grids.get(clstID);
        for (int j = 0; j < Location.getTotalStack(); j++) {
            // looping all the tier that is filled up 
            int currentMaxTier = maxTierRef[clstID][j];
            if (currentMaxTier != 0) {
                ContainerAllocation ca = currentClusterGrid[j][currentMaxTier-1];
                if (ca != null && ca.getContainer() != null) {
                    topTierContainers.add(ca.getContainer());
                }
            }

        }

        return topTierContainers;
    }

    //Input parameters: HashMap of cluster number (key) and 2D Array of ContainerAllocation (value),
    //                  2D array (cluster*stack) of integers which stores the value of the highest tiered containers
    public static ArrayList<Container> containersByTier(int clstID, HashMap<Integer, ContainerAllocation[][]> grids, int tierNumber) {
        ArrayList<Container> containersInTier = new ArrayList<>();
        ContainerAllocation[][] currentClusterGrid = grids.get(clstID);
        // looping all the columns stacks (0-200)
        for (int j = 0; j < Location.getTotalStack(); j++) {
            ContainerAllocation ca = currentClusterGrid[j][tierNumber - 1];
            if (ca != null && ca.getContainer() != null) {
                containersInTier.add(ca.getContainer());
            }

        }
        return containersInTier;
    }

    //Input parameters: HashMap of cluster number (key) and 2D Array of ContainerAllocation (value),
    //                  HashMap of colours (key) and int Array of assigned cluster, total no. of containers,
    //                  no. of containers currently in cluster (value), 
    //                  2D array (cluster*stack) of integers which stores the value of the highest tiered containers
    public static ArrayList<Location> checkGridAvailability(String colour, int clstID, HashMap<Integer, ContainerAllocation[][]> grids, HashMap<String, int[]> clusterAllocations,
            int[][] maxTierRef) {
        if(colour!=null){
            clstID = clusterAllocations.get(colour)[0];
        }
        String currentColour = "";
        String previousColour = "";
        
        // if topmost container is at tier-4, will check tier 1 to 4 has the same color for a stacking position
        boolean allContainerSameColour = true;

        ArrayList<Location> readyToPlaceGridList = new ArrayList<>();
        
        ContainerAllocation[][] currentClusterGrid = grids.get(clstID);
        for (int j = 0; j < Location.getTotalStack(); j++) {
                
            //Add cell to available grid list if there are no containers (Initial stack positions have 0 stack position with tier length=0)
            if (maxTierRef[clstID][j] == 0) { // empty stack
                Location l = new Location(clstID, j, 1);
                readyToPlaceGridList.add(new Location(clstID, j, 1));
            } else {
                //Iterate through all ContainerAllocations in specific stacking position
                //for(int k=0;k<maxTierRef[clstID][j];k++){
                int maxTier = maxTierRef[clstID][j];
                ContainerAllocation ca = currentClusterGrid[j][maxTier - 1];

                //Check max tier for current cluster and stack (Do not consider ContainerAllocations with 5 containers by checking null values)
                if (ca != null && ca.getContainer() != null) {

                    int currentCluster = ca.getContainer().getLocation().getCluster();
                    int currentStack = ca.getContainer().getLocation().getStack();
                    int currentTier = ca.getContainer().getLocation().getTier();
                    currentColour = ca.getContainer().getColour();
                    int[] clusterList = clusterAllocations.get(currentColour);

                    //currentColour = ca.getContainer().getColour();
                    for (int l = maxTier; l > 0; l--) {
                        Container chkContainer = currentClusterGrid[j][l - 1].getContainer();
                        String chkColour = chkContainer.getColour();
                        if (!currentColour.equals(chkColour)) {
                            allContainerSameColour = false;
                            break;
                        }
                    }

                    if (allContainerSameColour && clusterList[0] == currentCluster) {
                        readyToPlaceGridList.add(new Location(clstID, j, maxTier + 1));
                    }
                }
            }
        }
        return readyToPlaceGridList;
    }
}

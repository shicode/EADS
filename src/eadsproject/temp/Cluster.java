/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject.temp;

import java.util.*;

/**
 *
 * @author user
 */
public class Cluster {

    private static int clusterID;
    private static ArrayList<Activity> waitingList = new ArrayList<>();
    private static ArrayList<Resource> resourceList = new ArrayList<>();
    private static ArrayList<Container> containerList = new ArrayList<>();

    public Cluster(int clusterID, ArrayList<Activity> waitingList, ArrayList<Resource> resourceList, ArrayList<Container> containerList) {
        this.clusterID = clusterID;
        this.waitingList = waitingList;
        this.resourceList = resourceList;
        this.containerList = containerList;
    }

    //Input parameters: HashMap of cluster number (key) and 2D Array of ContainerAllocation (value),
    //                  2D array (cluster*stack) of integers which stores the value of the highest tiered containers
    public static HashMap<Integer, ArrayList<Container>> topTierContainers(int clstID, HashMap<Integer, ContainerAllocation[][]> grids, int[][] maxTierRef) {
        HashMap<Integer, ArrayList<Container>> returnMap = new HashMap<>();
        ArrayList<Container> topTierContainers = new ArrayList<>();
        //Iterate through all clusters

        ContainerAllocation[][] currentClusterGrid = grids.get(clstID);
        // System.out.println(currentClusterGrid[1][3].getContainer());
        // looping all the columns stacks (0-200)
        for (int j = 0; j < Location.getTotalStack(); j++) {
            // looping all the tier that is filled up 
            for (int k = 0; k < currentClusterGrid[j].length; k++) {
                ContainerAllocation ca = currentClusterGrid[j][k];
                if (ca != null && ca.getContainer() != null) {
                    if (returnMap.size() == 0) {
                        ArrayList<Container> newList = new ArrayList<>();
                        newList.add(ca.getContainer());
                        returnMap.put(k, newList);
                    } else {
                        if (returnMap.containsKey(k)) {
                            ArrayList<Container> tempList = returnMap.get(k);
                            tempList.add(ca.getContainer());
                            returnMap.put(k, tempList);
                        } else {
                            ArrayList<Container> newList = new ArrayList<>();
                            newList.add(ca.getContainer());
                            returnMap.put(k, newList);
                        }
                    }
                }
            }
        }
//        System.out.println("Cluster " + clstID);
//        Iterator it = returnMap.entrySet().iterator();
//        while(it.hasNext()){
//            Map.Entry pair = (Map.Entry) it.next();
//            ArrayList<Container> list = (ArrayList<Container>)pair.getValue();
//            int Tier = (Integer)pair.getKey() + 1;
//          
//        }
        return returnMap;
    }
    
    

    //Input parameters: HashMap of cluster number (key) and 2D Array of ContainerAllocation (value),
    //                  HashMap of colours (key) and int Array of assigned cluster, total no. of containers,
    //                  no. of containers currently in cluster (value), 
    //                  2D array (cluster*stack) of integers which stores the value of the highest tiered containers
    public static HashMap<Integer, ArrayList<Location>> checkGridAvailability(int clstID, HashMap<Integer, ContainerAllocation[][]> grids, HashMap<String, int[]> clusterAllocations,
            int[][] maxTierRef) {
        HashMap<Integer, ArrayList<Location>> returnMap = new HashMap<>();

        String currentColour = "";
        String previousColour = "";
        
        // if topmost container is at tier-4, will check tier 1 to 4 has the same color for a stacking position
        boolean allContainerSameColour = true;

        ContainerAllocation[][] currentClusterGrid = grids.get(clstID);
        for (int j = 0; j < Location.getTotalStack(); j++) {
            ArrayList<Location> readyToPlaceGridList;

            if (returnMap.containsKey(clstID)) {
                readyToPlaceGridList = returnMap.get(clstID);
            } else {
                readyToPlaceGridList = new ArrayList<Location>();
                returnMap.put(clstID, readyToPlaceGridList);
            }
            //Add cell to available grid list if there are no containers (Initial stack positions have 0 stack position with tier length=0)
            if (maxTierRef[clstID][j] == 0) { // empty stack
                Location l = new Location(clstID, j, 1);
                readyToPlaceGridList.add(new Location(clstID, j, 1));
                returnMap.put(clstID, readyToPlaceGridList);
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
                returnMap.put(clstID, readyToPlaceGridList);
            }
        }
        //}
        //}
        Iterator it = returnMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Location> list = (ArrayList<Location>) pair.getValue();
            System.out.print("Location - ");
            for (Location loct : list) {
                System.out.print("[" + loct.getStack() + " , " + loct.getTier() + "],");
            }
            System.out.println("");
        }
        return returnMap;
    }
}

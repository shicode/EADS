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
    
    private ArrayList<Activity> waitingList = new ArrayList<>();
    private ArrayList<Resource> resourceList = new ArrayList<>();
    private ArrayList<Container> containerList = new ArrayList<>();
    
    public Cluster(ArrayList<Activity> waitingList, ArrayList<Resource> resourceList, ArrayList<Container> containerList){
        this.waitingList = waitingList;
        this.resourceList = resourceList;
        this.containerList = containerList;
    }
    
    //Input parameters: HashMap of cluster number (key) and 2D Array of ContainerAllocation (value),
    //                  2D array (cluster*stack) of integers which stores the value of the highest tiered containers
    public ArrayList<Container> topTierContainers(HashMap<Integer, ContainerAllocation[][]> grids, int[][] maxTierRef){
        ArrayList<Container> topTierContainers = new ArrayList<>();
        //Iterate through all clusters
        for(int i=0;i<=10;i++){
            ContainerAllocation[][] currentClusterGrid = grids.get(i);
            Container c = currentClusterGrid.getContainer();
            int currentContainerCluster = c.getLocation().getCluster();
            int currentContainerStack = c.getLocation().getStack();
            int currentContainerTier = c.getLocation().getTier();
            int lookupMaxTier = maxTierRef[currentContainerCluster][currentContainerStack];
            //Add containers into list if current container is at the "highest tier" (based on maxTierRef)
            if(currentContainerTier==lookupMaxTier){
                topTierContainers.add(c);
            }
        }
        return topTierContainers;
    }
    
    //Input parameters: HashMap of cluster number (key) and 2D Array of ContainerAllocation (value),
    //                  HashMap of colours (key) and int Array of assigned cluster, total no. of containers,
    //                  no. of containers currently in cluster (value)
    public ArrayList<Location> checkGridAvailability(HashMap<Integer, ContainerAllocation[][]> grids, HashMap<String, int[]> clusterAllocations){
        ArrayList<Location> availableGridList = new ArrayList<>();
        boolean allContainerSameColour = true;
        String currentColour = "";
        String previousColour = "";
        for(int i=0;i<=10;i++){
            ContainerAllocation[][] currentClusterGrid = grids.get(i);
            for(int j=0;j<=200;j++){
                //Add cell to available grid list if there are no containers
                if(currentClusterGrid[j].length==0){
                    availableGridList.add(new Location(i, j));
                //Do not consider stack positions currently with 5 containers    
                }else if(currentClusterGrid[j].length<5){
                    //Iterate through all containers in specific stacking position
                    for(int k=0;k<currentClusterGrid[j].length;k++){
                        if(allContainerSameColour){
                            allContainerSameColour = true;
                            Container c = currentClusterGrid[j].getContainer();
                            if(c!=null){
                                int currentContainerCluster = c.getLocation().getCluster();
                                currentColour = c.getColour();
                                int[] clusterList = clusterAllocations.get(currentColour);
                                //check whether current container is in the assigned cluster
                                if(clusterList[0]==currentContainerCluster){
                                    //check if all containers in the specific stacking position has the same colour
                                    if(k==0){
                                        previousColour = currentColour;
                                    }else{
                                        if(!currentColour.equals(previousColour)){
                                            allContainerSameColour = false;
                                        }
                                    }
                                }
                            }
                        }
                    } if(allContainerSameColour){
                        availableGridList.add(new Location(i, j));
                    }
                }
            }
        }
        return availableGridList;
    }
    
    
}

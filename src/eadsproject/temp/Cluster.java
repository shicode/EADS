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
    public static ArrayList<Container> topTierContainers(HashMap<Integer, ContainerAllocation[][]> grids, int[][] maxTierRef){
        ArrayList<Container> topTierContainers = new ArrayList<>();
        //Iterate through all clusters
        for(int i=0;i<=10;i++){
            ContainerAllocation[][] currentClusterGrid = grids.get(i);
            for(int j=0;j<=200;j++){
                for(int k=0;k<currentClusterGrid[j].length;k++){
                    ContainerAllocation ca = currentClusterGrid[j][k];
                    if(ca!=null){
                        Container c = ca.getContainer();
                        int currentContainerTier = c.getLocation().getTier();
                        int maxTier = maxTierRef[i][j];
                        if(maxTier==currentContainerTier){
                            topTierContainers.add(c);
                                                   System.out.println(c.getContainerId());
                        }  
                    }
                }
            }
        }
        return topTierContainers;
    }
    
    //Input parameters: HashMap of cluster number (key) and 2D Array of ContainerAllocation (value),
    //                  HashMap of colours (key) and int Array of assigned cluster, total no. of containers,
    //                  no. of containers currently in cluster (value), 
    //                  2D array (cluster*stack) of integers which stores the value of the highest tiered containers
    public static ArrayList<Location> checkGridAvailability(HashMap<Integer, ContainerAllocation[][]> grids, HashMap<String, int[]> clusterAllocations,
            int[][] maxTierRef){
        ArrayList<Location> availableGridList = new ArrayList<>();
        boolean allContainerSameColour = true;
        String currentColour = "";
        String previousColour = "";
        for(int i=0;i<=10;i++){
            ContainerAllocation[][] currentClusterGrid = grids.get(i);
            for(int j=0;j<=200;j++){
                //Add cell to available grid list if there are no containers (Initial stack positions have 0 stack position with tier length=0)
                if(currentClusterGrid[j].length==0){
                    Location l = new Location(i, j, 0);
                    //System.out.println("Cluster: " +l.getCluster() + ", Stack: " + l.getStack() + ", Tier: " + l.getTier());
                    availableGridList.add(new Location(i, j, 0));
                }
                //Iterate through all ContainerAllocations in specific stacking position
                for(int k=0;k<currentClusterGrid[j].length;k++){
                    ContainerAllocation ca = currentClusterGrid[j][k];
                    //Check max tier for current cluster and stack (Do not consider ContainerAllocations with 5 containers by checking null values)
                    if(ca!=null){
                        int currentCluster = ca.getContainer().getLocation().getCluster();
                        int currentStack = ca.getContainer().getLocation().getStack();
                        int currentTier = ca.getContainer().getLocation().getTier();
                        if(currentTier!=5){
                            
                            
                            //if(allContainerSameColour){
                                //allContainerSameColour = true;
                                //Check all container colour in the current cluster and stack are the same)
                                Container c = ca.getContainer();
                                int currentContainerCluster = c.getLocation().getCluster();
                                //currentColour = c.getColour();
                                //int[] clusterList = clusterAllocations.get(currentColour);
                                int currentClusterStackMaxTier = maxTierRef[currentCluster][currentStack];
                                //For all cluster and stack position with only 1 container, add location (cluster, stack, tier) to list IF
                                //container colour matches with assigned cluster
                                if(currentClusterStackMaxTier==1){
                                    currentColour = c.getColour();
                                    int[] clusterList = clusterAllocations.get(currentColour);
                                    if(clusterList[0]==currentContainerCluster){
                                        //availableGridList.add(new Location(i, j, 1));
                                        //System.out.println("Cluster: " + i + ", Stack: " + j + ", Tier: " + 1);
                                    }
                                }else{
                                    int actualTier = k+1;
                                    System.out.println("Cluster: " + i + ", Stack: " + j + ", Tier: " + actualTier);
                                    for(int l=0;l<currentClusterStackMaxTier;l++){
                                        if(l==0){
                                            currentColour = c.getColour(); 
                                            previousColour = currentColour;
                                        }else{
                                            currentColour = c.getColour();
                                            if(!previousColour.equals(currentColour)){
                                                allContainerSameColour = false;
                                            }
                                        }
                                        System.out.println("L: " + l + ", Current Colour: " + currentColour + ", Previous Colour: " + previousColour);
                                        /*
                                        currentColour = c.getColour();
                                        int actualTier = k+1;
                                        availableGridList.add(new Location(i, j, actualTier));
                                        System.out.println("Cluster: " + i + ", Stack: " + j + ", Tier: " + actualTier);
                                        */
                                    }
                                }
                                
                                //check whether current container is in the assigned cluster
                                /*
                                if(clusterList[0]==currentContainerCluster){
                                    System.out.println("Cluster:" + currentCluster + ", Stack:" + currentStack);
                                    //System.out.println("Assigned Cluster:" + clusterList[0] + ", Current Container"
                                    //    + " Cluster:" + currentContainerCluster);
                                    
                                    //check if all containers in the specific stacking position has the same colour
                                    
                                    if(k==0){
                                        previousColour = currentColour;
                                    }else{
                                        if(!currentColour.equals(previousColour)){
                                            allContainerSameColour = false;
                                        }
                                    }
                                }*/
                            //}
                    
                        }
                    }
                    
                } /*if(allContainerSameColour){
                    availableGridList.add(new Location(i, j, currentClusterGrid[j].length));
                }*/
                
            }
        }
        return availableGridList;
    }
    
    
}

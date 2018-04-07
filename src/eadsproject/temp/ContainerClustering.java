/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject.temp;

import eadsproject.temp.Container;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author user
 */
public class ContainerClustering {

    private static HashMap<Integer, HashMap<String, ArrayList<Container>>> ctnMapByCluster = new HashMap<>();
    private static HashMap<String, ArrayList<Container>> ctnMapByColor = new HashMap<>();
    private static ArrayList<Container> ctnList = new ArrayList<>();
    private static ArrayList<Location> locationList = new ArrayList<>();
    private static HashMap<Integer, HashMap<String, Double>> clstByDiffIndx = new HashMap<>();
    private static HashMap<String, Integer> maxTierRef = new HashMap<>();
    private static int[][] topTierLookupArray = new int[11][201];
    private static int[] cranesActualByCluster = new int[11];
    private static int[] trucksActualByCluster = new int[11];
    
    public static void main(String[] args) {

        readDataset();
        resourceAllocation();

        HashMap<Integer, HashMap<String, Double>> test = assignDiffIndxtoClst(ctnMapByCluster);
        Iterator it = test.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            HashMap<String, Double> value = (HashMap<String, Double>) pair.getValue();
            Iterator it2 = value.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry pair2 = (Map.Entry) it2.next();
                System.out.println(pair.getKey() + " - " + pair2.getKey() + " - " + pair2.getValue());
            }
        }
    }

    public static HashMap<Integer, HashMap<String, Double>> assignDiffIndxtoClst(HashMap<Integer, HashMap<String, ArrayList<Container>>> rawData) {
        HashMap<Integer, HashMap<String, Double>> diffIndxbyCluster = new HashMap<>();
        Iterator it1 = rawData.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry) it1.next();
            Integer cluster = (Integer) pair.getKey();
            HashMap<String, ArrayList<Container>> ctnListbyColor = (HashMap) pair.getValue();
            Iterator it2 = ctnListbyColor.entrySet().iterator();
            HashMap<String, Double> diffIndxbyColor = new HashMap<>();
            while (it2.hasNext()) {
                Map.Entry pair2 = (Map.Entry) it2.next();
                String color = (String) pair2.getKey();
                ArrayList<Container> ctnList = (ArrayList<Container>) pair2.getValue();
                double diffIndx = 0.0;
                for (Container ctn : ctnList) {
                    int i = ctn.getLocation().getCluster();
                    int j = ctn.getLocation().getStack();
                    int k = ctn.getLocation().getTier();
                    diffIndx += maxTierRef.get(i + "-" + j) - k + 1;
                }
                diffIndxbyColor.put(color, diffIndx);
            }

            diffIndxbyCluster.put(cluster, diffIndxbyColor);
        }
        return diffIndxbyCluster;
    }

    public static HashMap<Integer, HashMap<String, ArrayList<Container>>> readDataset() {
        String csvFile = "src\\Data\\PSA test data.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            String headerLine = br.readLine();
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] container = line.split(cvsSplitBy);
                colourCount(container[1]);

                int containerID = Integer.parseInt(container[0]);
                String colour = container[1];
                int cluster = Integer.parseInt(container[2]);
                int stack = Integer.parseInt(container[3]);
                int tier = Integer.parseInt(container[4]);

                Container ctn = new Container(containerID, colour, cluster, stack, tier);
                Location location = new Location(cluster, stack, tier);

                //Add to max tier ref HashMap
                String key = cluster + "-" + stack;
                if (!maxTierRef.containsKey(key)) {
                    maxTierRef.put(key, tier);
                } else {
                    int currentMaxTier = maxTierRef.get(key);
                    if (tier > currentMaxTier) {
                        maxTierRef.put(key, tier);
                    }
                }

                locationList.add(location);

                if (!ctnMapByCluster.containsKey(cluster)) {
                    ctnMapByColor = new HashMap<>();
                    ctnList = new ArrayList<>();
                    ctnList.add(ctn);
                    ctnMapByColor.put(colour, ctnList);
                    ctnMapByCluster.put(cluster, ctnMapByColor);
                } else {
                    HashMap<String, ArrayList<Container>> currentCtnMapByColour = ctnMapByCluster.get(cluster);
                    if (!currentCtnMapByColour.containsKey(colour)) {
                        ctnList = new ArrayList<>();
                    } else {
                        ctnList = currentCtnMapByColour.get(colour);
                    }
                    ctnList.add(ctn);
                    currentCtnMapByColour.put(colour, ctnList);
                    ctnMapByCluster.put(cluster, currentCtnMapByColour);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return ctnMapByCluster;
        }
    }

    public static void colourCount(String colour) {
        if (colour.equals("blue")) {
            Container.incrementBlueCount();
        }

        if (colour.equals("green")) {
            Container.incrementGreenCount();
        }

        if (colour.equals("indigo")) {
            Container.incrementIndigoCount();
        }

        if (colour.equals("orange")) {
            Container.incrementOrangeCount();
        }

        if (colour.equals("red")) {
            Container.incrementRedCount();
        }

        if (colour.equals("yellow")) {
            Container.incrementYellowCount();
        }
    }

    public static HashMap<String, Integer> requiredNumberOfCluster() {
        HashMap<String, Integer> hashmap = new HashMap<>();
        double blueNum = Container.getBlueCount();
        int blueNumCluster = (int) Math.ceil(blueNum / 1000);
        hashmap.put("blue", blueNumCluster);
        double greenNum = Container.getGreenCount();
        int greenNumCluster = (int) Math.ceil(greenNum / 1000);
        hashmap.put("green", greenNumCluster);
        double indigoNum = Container.getIndigoCount();
        int indigoNumCluster = (int) Math.ceil(indigoNum / 1000);
        hashmap.put("indigo", indigoNumCluster);
        double orangeNum = Container.getOrangeCount();
        int orangeNumCluster = (int) Math.ceil(orangeNum / 1000);
        hashmap.put("orange", orangeNumCluster);
        double redNum = Container.getRedCount();
        int redNumCluster = (int) Math.ceil(redNum / 1000);
        hashmap.put("red", redNumCluster);
        double yellowNum = Container.getYellowCount();
        int yellowNumCluster = (int) Math.ceil(yellowNum / 1000);
        hashmap.put("yellow", yellowNumCluster);
        return hashmap;
    }

    public static void resourceAllocation() {
        /*
        //Iterate through the String array and we get the colours for each cluster and the number of containers.
        //Cluster --> colour --> number
                           //--> difficulty index
        
        //Come up with the activity list. Take that container and get all the colours not equal to this colour, 
        //Have an activity list of cluster, colour, and list of containers by id that are meant to be there
        
        Iterate through the container map, by cluster number and then by colour.
        Find out which cluster each colour needs to be in by comparing it to the string array
        For that colour, we make a 2D array for all the containers in each cluster ie one cluster for each 2D array.
        The 2D is by stacking position and by tier. The element of the 2D array contains the Container object as well as the the destination cluster number.
        
        Across cluster - Truck and 2 Crane
        Within cluster - Crane
        
        Go through the 
        
        DO the ratio to scale down the number of trucks and cranes to the number of available trucks and cranes
        
        2D array with highest k of the list
        
        
         */
        //Initialize the cluster assignments array

        HashMap<String, int[]> clusterAllocations = new HashMap();
        int[] clusterList = new int[]{2, 669, 0};
        clusterAllocations.put("yellow", clusterList);

        clusterList = new int[]{9, 662, 0};
        clusterAllocations.put("blue", clusterList);

        clusterList = new int[]{8, 656, 0};
        clusterAllocations.put("indigo", clusterList);

        clusterList = new int[]{5, 667, 0};
        clusterAllocations.put("green", clusterList);

        clusterList = new int[]{7, 675, 0};
        clusterAllocations.put("orange", clusterList);

        clusterList = new int[]{6, 671, 0};
        clusterAllocations.put("red", clusterList);

        System.out.println(clusterAllocations);

        int numClusters = ctnMapByCluster.size();
        HashMap<Integer, ContainerAllocation[][]> allocationGrids = new HashMap<>();
        for (int i = 0; i < numClusters; i++) {
            ContainerAllocation[][] grid = new ContainerAllocation[201][5];
            allocationGrids.put(i, grid);
        }

        //Iterate through the countainer by cluster map
        Iterator it = ctnMapByCluster.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            HashMap<String, ArrayList<Container>> value = (HashMap<String, ArrayList<Container>>) pair.getValue();
            Iterator it2 = value.entrySet().iterator();
            //HashMap<Integer, HashMap<String, ArrayList<Container>>>
            int currentClusterNumber = (int) pair.getKey();
            ContainerAllocation[][] currentGrid = allocationGrids.get(currentClusterNumber);
            while (it2.hasNext()) {
                Map.Entry pair2 = (Map.Entry) it2.next();
                String currentColour = (String) pair2.getKey();
                ArrayList<Container> currentContainerList = (ArrayList<Container>) pair2.getValue();
                int destinationCluster = clusterAllocations.get(currentColour)[0];
                int numberContainers = clusterAllocations.get(currentColour)[1];
                int allocatedContainers = clusterAllocations.get(currentColour)[2];

                for (Container c : currentContainerList) {
                    if (allocatedContainers < numberContainers) {
                        ContainerAllocation ca = new ContainerAllocation(c, destinationCluster, 1, 2);
                        currentGrid[c.getLocation().getStack()][c.getLocation().getTier()-1] = ca;
                        allocatedContainers++;
                        clusterList = clusterAllocations.get(currentColour);
                        clusterList[2] = allocatedContainers;
                        clusterAllocations.put(currentColour, clusterList);
                    }
                }
                //System.out.println(pair.getKey() + " - " + pair2.getKey() + " - " + pair2.getValue());
            }
            allocationGrids.put(currentClusterNumber, currentGrid);

        }
        
        //Find the top tier number of each stacking position in each cluster and store them in an ixj array
        int[] cranesNeededByCluster = new int[11];
        int[] trucksNeededByCluster = new int[11];
        
        
        it = allocationGrids.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ContainerAllocation[][] currentGrid = (ContainerAllocation[][]) pair.getValue();
            int currentClusterNumber = (int) pair.getKey();
            int totalTrucksNeeded = 0;
            int totalCranesNeeded = 0;
            
            
            for(int j=0; j<201; j++){
                int highestTier = 0;
                int k=1;
                while(k<=5 && currentGrid[j][k-1]!=null){
                    highestTier = k;
                    k++;
                }
                topTierLookupArray[currentClusterNumber][j] = highestTier;
                if(highestTier>0){
                    totalTrucksNeeded+= currentGrid[j][highestTier-1].getNumberOfTrucks();
                    totalCranesNeeded+= currentGrid[j][highestTier-1].getNumberOfCranes();
                }
                
                
            }
            cranesNeededByCluster[currentClusterNumber] = totalCranesNeeded;
            trucksNeededByCluster[currentClusterNumber] = totalTrucksNeeded;
        }
        
        //Calculate and allocate trucks and cranes to each cluster
        int totalCranesNeeded = Arrays.stream(cranesNeededByCluster).sum();
        int totalTrucksNeeded = Arrays.stream(trucksNeededByCluster).sum();
        int trucksAvailable = 100;
        int cranesAvailable = 20;
        
        //Calculate trucks needed
        for(int i=0; i<trucksNeededByCluster.length; i++){
            int z = trucksNeededByCluster[i];
            float ratio = (float) ((z*1.0)/totalTrucksNeeded);
            float numNeeded = ratio*trucksAvailable;
            int numNeededByCluster = (int)numNeeded;
            trucksActualByCluster[i] = numNeededByCluster;
        }
        
        int actualTrucksNeeded = Arrays.stream(trucksActualByCluster).sum();
        if(actualTrucksNeeded < trucksAvailable){
            int difference = trucksAvailable - actualTrucksNeeded;
            Random r = new Random();
            while(difference>0){
                int randomCluster = r.nextInt(11);
                trucksActualByCluster[randomCluster]++;
                difference--;
            }
        }
        
        //Calculate cranes needed
        for(int i=0; i<cranesNeededByCluster.length; i++){
            int z = cranesNeededByCluster[i];
            float ratio = (float) ((z*1.0)/totalCranesNeeded);
            float numNeeded = ratio*cranesAvailable;
            int numNeededByCluster = (int)numNeeded;
            cranesActualByCluster[i] = numNeededByCluster;
        }
        actualTrucksNeeded = Arrays.stream(trucksActualByCluster).sum();
        
        int actualCranesNeeded = Arrays.stream(cranesActualByCluster).sum();
        if(actualCranesNeeded < cranesAvailable){
            int difference = cranesAvailable - actualCranesNeeded;
            Random r = new Random();
            while(difference>0){
                int randomCluster = r.nextInt(11);
                cranesActualByCluster[randomCluster]++;
                difference--;
            }
        }
        actualCranesNeeded = Arrays.stream(cranesActualByCluster).sum();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject.temp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Arrays;


/**
 *
 * @author user
 */
public class ContainerClustering {
    
    private static HashMap<Integer, HashMap<String, ArrayList<Container>>> ctnMapByCluster = new HashMap<>();
    private static HashMap<String, ArrayList<Container>> ctnMapByColor = new HashMap<>();
    private static HashMap<String, HashMap<Integer, int[]>> ctnByClusterTier = new HashMap<>();
    private static ArrayList<Container> ctnList = new ArrayList<>();
    private static ArrayList<Location> locationList = new ArrayList<>();
    private static HashMap<Integer, HashMap<String, Double>> clstByDiffIndx = new HashMap<>();
    private static HashMap<String, Integer> maxTierRef = new HashMap<>();
    private static int[][] topTierLookupArray = new int[11][201];
    private static int[] cranesActualByCluster = new int[11];
    private static int[] trucksActualByCluster = new int[11];
    
    public static void main(String[] args){
        
        // Read input data row by row
        readDataset();
        
        // Getting the difficulty index of each color in respective cluster
        HashMap<Integer, HashMap<String,Double>> clstColorDiffIndx = assignDiffIndxtoClst(ctnMapByCluster);
        
        // Tranfomring the above Hashmap to get the difficulty index of each cluster in respective color
        HashMap<String, HashMap<Integer,Double>> colorClstDiffIndx = assignDiffIndxtoColor(clstColorDiffIndx);
        
        // Priority Sequence of designation clsuter for respective color
        HashMap<String, Integer[]> prefClstByColor = generateDestinationClstSeq(colorClstDiffIndx);
        
        // Finalized cluster allocation to respective color
        HashMap<String, int[]> clusterAllocations = allocateClst(prefClstByColor);
        
        // Allocate resources to cluster
        resourceAllocation(clusterAllocations);
    }
    public static HashMap<Integer, HashMap<String, ArrayList<Container>>> readDataset() {
        // Input Data Source
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

            
                //Add to max tier ref HashMap for dynamic scaling
                String key = cluster + "-" + stack;
                if (!maxTierRef.containsKey(key)) {
                    maxTierRef.put(key, tier);
                } else {
                    int currentMaxTier = maxTierRef.get(key);
                    if (tier > currentMaxTier) {
                        maxTierRef.put(key, tier);
                    }
                }
                
                //Add Container Count for every tier of each cluster
                if(ctnByClusterTier.containsKey(colour)){
                    HashMap<Integer,int[]> cntTier = ctnByClusterTier.get(colour);
                    if(cntTier.containsKey(cluster)){
                        int[] intArray = cntTier.get(cluster);
                        intArray[tier-1]++;
                        cntTier.put(cluster, intArray);       
                    }else{
                        int[] intArray = new int[5];
                        intArray[tier-1] = 1;
                        cntTier.put(cluster, intArray);
                    }
                }else{
                    HashMap<Integer,int[]> newCntTier = new HashMap();
                    int[] intArray = new int[5];
                    intArray[tier-1] = 1;
                    newCntTier.put(cluster, intArray);
                    ctnByClusterTier.put(colour,newCntTier);
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
    public static HashMap<Integer, HashMap<String,Double>> assignDiffIndxtoClst(HashMap<Integer, HashMap<String,ArrayList<Container>>> rawData) {
        HashMap<Integer, HashMap<String,Double>> diffIndxbyCluster = new HashMap<>();
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
    public static HashMap<String, HashMap<Integer, Double>> assignDiffIndxtoColor(HashMap<Integer, HashMap<String,Double>> clstColorDiffIndx){
        
        Set<Integer> clusters = clstColorDiffIndx.keySet();
        Integer[] clusterArray = clusters.toArray(new Integer[clusters.size()]);
        HashMap<String, HashMap<Integer, Double>> returnMap = new HashMap<>();
        int chkVariable = 0;
        for(Integer i: clusterArray){
            HashMap<String,Double> colorDiffIndx = clstColorDiffIndx.get(i);
            Set<String> colors = colorDiffIndx.keySet();
            String[] colorArray = colors.toArray(new String[colors.size()]);
            
            if (chkVariable == 0){
                for(String str: colorArray){
                        HashMap<Integer, Double> colorHash = new HashMap<>();
                        colorHash.put(i, colorDiffIndx.get(str));
                        returnMap.put(str, colorHash);
                }
            }else{
                for (String str: colorArray){
                    HashMap<Integer, Double> colorHash = returnMap.get(str);
                    colorHash.put(i, colorDiffIndx.get(str));
                    returnMap.put(str, colorHash);
                }
            }
            chkVariable++;
        }
        return returnMap;
    }
    public static HashMap<String, Integer[]> generateDestinationClstSeq(HashMap<String, HashMap<Integer,Double>> colorClstDiffIndx){
        HashMap<String, Integer[]> prefClstByColor = new HashMap<>();
        Iterator it = colorClstDiffIndx.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry color= (Map.Entry)it.next();
            
            // get the Cluster - DiffIndx for every color 
            HashMap<Integer,Double> clustDiffIndx = (HashMap<Integer, Double>) color.getValue();
            // Sorting the hashmax in descending order (most difficult on the top)
            HashMap<Integer,Double> sortedValue = sortByValues(clustDiffIndx);
          
            // Transform the keySet to soreted Integer Array
            Set<Integer> prefClstList = sortedValue.keySet();
            Integer[] prefClstArray = prefClstList.toArray(new Integer[prefClstList.size()]);
            
            // generating preferred sequence
            prefClstByColor.put((String)color.getKey(),prefClstArray);
        }
        return prefClstByColor;
    }
    public static HashMap<String, int[]> allocateClst(HashMap<String, Integer[]> prefClstSeq){
        
        HashMap<String, int[]> clusterAllocations = new HashMap<>();
        Iterator it = prefClstSeq.entrySet().iterator();
        
        // List of color for tie-breaking (having largest diff Indx for same cluster)
        List<String> tieList = new ArrayList<>();
        List<Integer> assignedClst = new ArrayList<>(); 
        
        int untieClst = 0;
        
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            String color = (String) pair.getKey();
            
            //System.out.println(pair.getKey() + " - " + Arrays.toString((Integer[])pair.getValue()));
            
            Integer[] prefClsts = (Integer[]) pair.getValue();
            int topClst = prefClsts[0];
            Set<String> colors = prefClstSeq.keySet();
            String[] colorsArr = colors.toArray(new String[colors.size()]);
            boolean tie = false;
            for(int i= 0; i < colorsArr.length; i++){
                if(!color.equals(colorsArr[i])){
                    int chkTie = prefClstSeq.get(colorsArr[i])[0];
                    topClst = prefClsts[0];
                    if (chkTie == topClst){
                        tieList.add(color);
                        tie = true;
                        for (int j = 1; j < Location.getTotalCluster(); j++){
                            topClst = prefClsts[j];
                            chkTie = prefClstSeq.get(colorsArr[i])[j];
                            if (chkTie != topClst){
                                untieClst = j;
                                break;
                            } 
                        }
                        
                    }
                }
            }
            if (!tie) {
                clusterAllocations.put(color, new int[]{topClst,Container.getContainerCnt(color),0});
                assignedClst.add(topClst);
            }
        }
        //System.out.println(tieList.toString());
        
        for(int i =0; i < tieList.size(); i++){
            for(int k = untieClst; k < Location.getTotalCluster(); k++){
                int cluster = prefClstSeq.get(tieList.get(i))[k];
                if(!assignedClst.contains(cluster))  {
                    String color = tieList.get(i);
                    clusterAllocations.put(color, new int[]{cluster,Container.getContainerCnt(color),0});
                    assignedClst.add(cluster);
                    break;
                }
            }
        }
//        Iterator it1 = clusterAllocations.entrySet().iterator();
//        while(it1.hasNext()){
//            Map.Entry pair = (Map.Entry)it1.next();
//            System.out.println(pair.getKey() + " - " + Arrays.toString((int[])pair.getValue()));
//        }
        return clusterAllocations;
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
    public static void resourceAllocation(HashMap<String, int[]> clusterAllocations) {
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

        
        //System.out.println(clusterAllocations);

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
                        int[] clusterList = clusterAllocations.get(currentColour);
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
    private static HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return ((Comparable) ((Map.Entry) (o2)).getValue())
                  .compareTo(((Map.Entry) (o1)).getValue());
            }
       });
       // Here I am copying the sorted list in HashMap
       // using LinkedHashMap to preserve the insertion order
       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
  }
}
 
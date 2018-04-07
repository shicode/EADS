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
public class ContainerClustering{
    
    private static HashMap<Integer, HashMap<String, ArrayList<Container>>> ctnMapByCluster = new HashMap<>();
    private static HashMap<String, ArrayList<Container>> ctnMapByColor = new HashMap<>();
    
    private static HashMap<String, HashMap<Integer, int[]>> ctnByClusterTier = new HashMap<>();
    private static ArrayList<Container> ctnList = new ArrayList<>();
    private static ArrayList<Location> locationList = new ArrayList<>();
    private static HashMap<Integer, HashMap<String,Double>> clstByDiffIndx = new HashMap<>();
    private static ArrayList<HashMap<String,Double[]>> test = new ArrayList<>();
    
    // Reference list to set dynamic scaling while calculatin DiffIndex
    private static HashMap<String,Integer> maxTierRef = new HashMap<>();
    
    public static void main(String[] args){
        
        // Read input data row by row
        readDataset();
        
        // Getting the difficulty index of each color in respective cluster
        HashMap<Integer, HashMap<String,Double>> clstColorDiffIndx = assignDiffIndxtoClst(ctnMapByCluster);
        ArrayList<HashMap<String, String[]>> arrList = getDiffIndxofClstByColor(clstColorDiffIndx);
        HashMap<String, HashMap<Integer,Double>> test = test(clstColorDiffIndx);
        HashMap<String, Integer[]> prefClstByColor = new HashMap<>();
        HashMap<String, int[]> clusterAllocations = new HashMap<>();
        
//        for (HashMap<String, String[]> haha: arrList ){
//            Iterator it = haha.entrySet().iterator();
//            while(it.hasNext()){
//                Map.Entry pair = (Map.Entry) it.next();
//                String[] db = (String[]) pair.getValue();
//                for(int i = 0; i < db.length; i++){
//                    System.out.println( pair.getKey() + ":" + db[i]);
//                }     
//            }
//         }
//    
//        Iterator it = ctnByClusterTier.entrySet().iterator();
//        while(it.hasNext()){
//            Map.Entry pair = (Map.Entry) it.next();
//            System.out.println(pair.getKey());
//            HashMap<Integer, int[]> value = (HashMap<Integer, int[]>) pair.getValue();
//            Iterator it2 = value.entrySet().iterator();
//            while(it2.hasNext()){
//                Map.Entry pair2 = (Map.Entry) it2.next();
//                System.out.print("Cluster - " + pair2.getKey() + ":[");
//                int[] intArray = (int[]) pair2.getValue();
//                for(int i=1; i <= intArray.length; i++){
//                    System.out.print("Tier " + i + ": " + intArray[i-1]);
//                    if(i < intArray.length) System.out.print(",");
//                }
//                System.out.print("]");
//                System.out.println();
//            }
//        }

        
        Iterator it = test.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry color= (Map.Entry)it.next();
            
            HashMap<Integer,Double> clustDiffIndx = (HashMap<Integer, Double>) color.getValue();
            HashMap<Integer,Double> sortedValue = sortByValues(clustDiffIndx);
          
            Set<Integer> prefClstList = sortedValue.keySet();
            Integer[] prefClstArray = prefClstList.toArray(new Integer[prefClstList.size()]);
            prefClstByColor.put((String)color.getKey(),prefClstArray);
        }
        
        Iterator it2 = prefClstByColor.entrySet().iterator();
        List<String> tieList = new ArrayList<>();
        while (it2.hasNext()){
            Map.Entry pair = (Map.Entry)it2.next();
            String color = (String) pair.getKey();
            System.out.println(pair.getKey() + " - " + Arrays.toString((Integer[])pair.getValue()));
            Integer[] prefClsts = (Integer[]) pair.getValue();
            int topClst = prefClsts[0];
            Set<String> colors = prefClstByColor.keySet();
            String[] colorsArr = colors.toArray(new String[colors.size()]);
            boolean tie = false;
            for(int i= 0; i < colorsArr.length; i++){
                if(!color.equals(colorsArr[i])){
                    int chkTie = prefClstByColor.get(colorsArr[i])[0];
                    if (chkTie == topClst){
                        tieList.add(color);
                        tie = true;
                    }
                }
            }
            if (!tie) clusterAllocations.put(color, new int[]{topClst,Container.getContainerCnt(color),0});
        }
        
        System.out.println(tieList.size());
        Iterator it3 = clusterAllocations.entrySet().iterator();
        while (it3.hasNext()){
            Map.Entry pair = (Map.Entry)it3.next();
            System.out.println(pair.getKey() + " - " + Arrays.toString((int[])pair.getValue()));
        }
        
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
    public static ArrayList<HashMap<String, String[]>> getDiffIndxofClstByColor(HashMap<Integer, HashMap<String,Double>> clstColorDiffIndx){
        
        Set<Integer> clusters = clstColorDiffIndx.keySet();
        Integer[] clusterArray = clusters.toArray(new Integer[clusters.size()]);
        ArrayList<HashMap<String, String[]>> arrList = new ArrayList<>();
        int chkVariable = 0;
        for(Integer i: clusterArray){
            HashMap<String,Double> colorDiffIndx = clstColorDiffIndx.get(i);
            Set<String> colors = colorDiffIndx.keySet();
            String[] colorArray = colors.toArray(new String[colors.size()]);
            int colorIndex = 0;
            double maxDiffIndx = 0.0;
            if (chkVariable == 0){
                for(String str: colorArray){
                        HashMap<String, String[]> colorHash = new HashMap<>();
                        String[] colorDouble = new String[clusters.size()];
                        colorDouble[i] = i + "_" + colorDiffIndx.get(str);
                        colorHash.put(str, colorDouble);
                        arrList.add(colorHash);
                }
            }else{
                for(int k=0; k < colors.size(); k++){
                    HashMap<String, String[]> tst = arrList.get(k);
                    for (String str: colorArray){
                       if(tst.containsKey(str)){
                           tst.get(str)[i] = i + "_" + colorDiffIndx.get(str);
                       }
                     }
                }
                colorIndex++;
            }
            chkVariable++;
        }
        return arrList;
    }
    public static HashMap<String, HashMap<Integer, Double>> test(HashMap<Integer, HashMap<String,Double>> clstColorDiffIndx){
        
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
    public static HashMap<Integer, HashMap<String,Double>> assignDiffIndxtoClst(HashMap<Integer, HashMap<String,ArrayList<Container>>> rawData)
    {
        HashMap<Integer, HashMap<String,Double>> diffIndxbyCluster = new HashMap<>();
        Iterator it1 = rawData.entrySet().iterator(); 
        while(it1.hasNext()){
            Map.Entry pair = (Map.Entry)it1.next();
            Integer cluster = (Integer) pair.getKey();
            HashMap<String,ArrayList<Container>> ctnListbyColor = (HashMap) pair.getValue();
            Iterator it2 = ctnListbyColor.entrySet().iterator();
            HashMap<String,Double> diffIndxbyColor = new HashMap<>();
            while(it2.hasNext()){
                Map.Entry pair2 = (Map.Entry) it2.next();
                String color = (String) pair2.getKey();
                ArrayList<Container> ctnList = (ArrayList<Container>) pair2.getValue();
                double diffIndx = 0.0;
                for(Container ctn: ctnList){
                    int i = ctn.getLocation().getCluster();
                    int j = ctn.getLocation().getStack();
                    int k = ctn.getLocation().getTier();
                    diffIndx += maxTierRef.get(i + "-" + j) - k + 1;
                }
                diffIndxbyColor.put(color, diffIndx);
            }
            diffIndxbyCluster.put(cluster,diffIndxbyColor);
        }
        return diffIndxbyCluster;
    }
    public static HashMap<Integer, HashMap<String, ArrayList<Container>>> readDataset() {
        String csvFile = "src\\Data\\PSA test data.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        //int test = 0;
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
                Location location = new Location(cluster,stack, tier);
               
                
            
                //Add to max tier ref HashMap
                String key = cluster+"-"+stack;
                if(!maxTierRef.containsKey(key)){
                    maxTierRef.put(key,tier);
                }else{
                    int currentMaxTier = maxTierRef.get(key);
                    if(tier > currentMaxTier){
                        maxTierRef.put(key,tier);
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
                }
                else{
                    HashMap<String, ArrayList<Container>> currentCtnMapByColour = ctnMapByCluster.get(cluster);
                    if(!currentCtnMapByColour.containsKey(colour)){
                        ctnList = new ArrayList<>();
                    }else{
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
    public static void colourCount(String colour){
        if(colour.equals("blue")){
            Container.incrementBlueCount();
        }
        
        if(colour.equals("green")){
            Container.incrementGreenCount();
        }
        
        if(colour.equals("indigo")){
            Container.incrementIndigoCount();
        }
        
        if(colour.equals("orange")){
            Container.incrementOrangeCount();
        }
        
        if(colour.equals("red")){
            Container.incrementRedCount();
        }
        
        if(colour.equals("yellow")){
            Container.incrementYellowCount();
        }
    }
    public static HashMap<String, Integer> requiredNumberOfCluster(){
        HashMap<String, Integer> hashmap = new HashMap<>();
        double blueNum = Container.getBlueCount();
        int blueNumCluster = (int)Math.ceil(blueNum/1000);
        hashmap.put("blue", blueNumCluster);
        double greenNum = Container.getGreenCount();
        int greenNumCluster = (int)Math.ceil(greenNum/1000);
        hashmap.put("green", greenNumCluster);
        double indigoNum = Container.getIndigoCount();
        int indigoNumCluster = (int)Math.ceil(indigoNum/1000);
        hashmap.put("indigo", indigoNumCluster);
        double orangeNum = Container.getOrangeCount();
        int orangeNumCluster = (int)Math.ceil(orangeNum/1000);
        hashmap.put("orange", orangeNumCluster);
        double redNum = Container.getRedCount();
        int redNumCluster = (int)Math.ceil(redNum/1000);
        hashmap.put("red", redNumCluster);
        double yellowNum = Container.getYellowCount();
        int yellowNumCluster = (int)Math.ceil(yellowNum/1000);
        hashmap.put("yellow", yellowNumCluster);
        return hashmap;
    }
}

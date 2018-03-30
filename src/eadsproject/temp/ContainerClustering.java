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
public class ContainerClustering{
    
    private static HashMap<Integer, HashMap<String, ArrayList<Container>>> ctnMapByCluster = new HashMap<>();
    private static HashMap<String, ArrayList<Container>> ctnMapByColor = new HashMap<>();
    private static ArrayList<Container> ctnList = new ArrayList<>();
    private static ArrayList<Location> locationList = new ArrayList<>();
    private static HashMap<Integer, HashMap<String,Double>> clstByDiffIndx = new HashMap<>();
    private static HashMap<String,Integer> maxTierRef = new HashMap<>();
    
    public static void main(String[] args){
        int x=1;
        readDataset();
        HashMap<Integer, HashMap<String,Double>> test = assignDiffIndxtoClst(ctnMapByCluster);
        Iterator it = test.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            HashMap<String,Double> value = (HashMap<String,Double>) pair.getValue();
            Iterator it2 = value.entrySet().iterator();
            while (it2.hasNext()){
                Map.Entry pair2 = (Map.Entry)it2.next();
                System.out.println(pair.getKey() + " - " + pair2.getKey() + " - " + pair2.getValue());
            }
        }
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

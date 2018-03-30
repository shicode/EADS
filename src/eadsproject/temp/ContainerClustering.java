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

    public static void main(String[] args) {
        HashMap<Integer, HashMap<String, ArrayList<Container>>> ctnMapByCluster = readDataset();
        HashMap<String, Integer> hashmap = requiredNumberOfCluster();
        Iterator it = hashmap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
        }
    }

    public static HashMap<Integer, HashMap<String, ArrayList<Container>>> readDataset() {
        String csvFile = "C:/Users/Shraddha/Documents/NetBeansProjects/EADSProject/src/Data/PSA test data.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        HashMap<Integer, HashMap<String, ArrayList<Container>>> ctnMapByCluster = new HashMap<>();
        HashMap<String, ArrayList<Container>> ctnMapByColor = new HashMap<>();
        ArrayList<Container> ctnList = new ArrayList<>();
        ArrayList<Location> locationList = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(csvFile));
            String headerLine = br.readLine();
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] container = line.split(cvsSplitBy);
                /*System.out.println("Container=" + container[0] + " , Colour=" + container[1] + " , Cluster=" + container[2] + 
                        ", Stacking Position=" + container[3] + ", Tier=" + container[4] + "");*/
                colourCount(container[1]);

                int containerID = Integer.parseInt(container[0]);
                String colour = container[1];
                int cluster = Integer.parseInt(container[2]);
                int stack = Integer.parseInt(container[3]);
                int tier = Integer.parseInt(container[4]);
                Container ctn = new Container(containerID, colour, cluster, stack, tier);
                Location location = new Location(cluster,stack, tier);
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
            /*System.out.println("Blue Total=" + Container.getBlueCount() + ", Green Total=" + Container.getGreenCount() + ", Indigo Total="
                + Container.getIndigoCount() + ", Orange Total=" + Container.getOrangeCount() + ", Red Total=" + Container.getRedCount() + 
                ", Yellow Total=" + Container.getYellowCount());
             */

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
}

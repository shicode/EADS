/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject.temp;

/**
 *
 * @author user
 */
public class Container {
    private int containerID;
    private String colour;
    private Location location; 
    
    private static int blueCount=0;
    private static int greenCount=0;
    private static int indigoCount=0;
    private static int orangeCount=0;
    private static int redCount=0;
    private static int yellowCount=0;
        
    public Container(int containerID, String colour, int cluster, int stackingPosition, int tier){
        this.containerID = containerID;
        this.colour = colour;
        location = new Location(cluster, stackingPosition, tier);
//        switch(colour){
//            case "blue": blueCount++;
//            case "green": greenCount++;
//            case "indigo": indigoCount++;
//            case "orange": orangeCount++;
//            case "red": redCount++;
//            default: yellowCount++;
//        }
    }
    
    public Container(int containerID, Location location){
        this.containerID = containerID;
        this.location = location;
    }
    
    public static void incrementBlueCount(){
        blueCount++;
    }
   
    public static void incrementGreenCount(){
        greenCount++;
    }
   
    public static void incrementIndigoCount(){
        indigoCount++;
    }
    
    public static void incrementOrangeCount(){
        orangeCount++;
    }
    
    public static void incrementRedCount(){
        redCount++;
    }
    
    public static void incrementYellowCount(){
        yellowCount++;
    }
    
    public static int getContainerCnt(String color){
        switch(color){
            case "blue": return blueCount;
            case "green": return greenCount;
            case "indigo": return indigoCount;
            case "orange": return orangeCount;
            case "red": return redCount;
            default: return yellowCount;
        }
    }
    public static int getBlueCount(){
        return blueCount;
    }
    
    public static int getGreenCount(){
        return greenCount;
    }
    
    public static int getIndigoCount(){
        return indigoCount;
    }
    
    public static int getOrangeCount(){
        return orangeCount;
    }
    
    public static int getRedCount(){
        return redCount;
    }
    
    public static int getYellowCount(){
        return yellowCount;
    }
    
    public String getColour(){
        return colour;
    }
    
    public Location getLocation(){
        return location;
    }
}


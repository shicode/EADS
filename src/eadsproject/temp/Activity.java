/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject.temp;

import java.util.Date;

/**
 *
 * @author user
 */
public class Activity {
    private String activityType;
    private double startTime;
    private double endTime;
    private Container container;
    private Location destination;
    
    public Activity(String activityType, double startTime, double endTime, Container container, Location destination){
        this.activityType = activityType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.container = container;
        this.destination = destination;
    }
    
    public String getActivityType(){
        return activityType;
    }
    
    public double getStartTime(){
        return startTime;
    }
    
    public double getEndTime(){
        return endTime;
    }
    
    public Container getContainer(){
        return container;
    }
    
    public Location getDestination(){
        return destination;
    }
}

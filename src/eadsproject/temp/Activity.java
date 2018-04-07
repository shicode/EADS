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
    private Date startTime;
    private Date endTime;
    private int containerID;
    private Location destination;
    
    public Activity(String activityType, Date startTime, Date endTime, int containerID, Location destination){
        this.activityType = activityType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.containerID = containerID;
        this.destination = destination;
    }
    
    public String getActivityType(){
        return activityType;
    }
    
    public Date getStartTime(){
        return startTime;
    }
    
    public Date getEndTime(){
        return endTime;
    }
    
    public int getContainerID(){
        return containerID;
    }
    
    public Location getDestination(){
        return destination;
    }
}

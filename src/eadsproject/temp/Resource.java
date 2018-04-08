/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject.temp;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Resource {
    private String resourceID;
    private String resourceType;
    private boolean isFree;
    private ArrayList<Activity> activityList;
    
    public Resource(String resourceID, boolean isFree){
        this.resourceID = resourceID;
        this.isFree = isFree;
        this.activityList = new ArrayList<>();
        this.resourceType = resourceID.substring(0,1);
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setActivityList(Activity activity) {
        this.activityList.add(activity);
    }

    public boolean isFree() {
        return isFree;
    }

    public ArrayList<Activity> getActivityList() {
        return activityList;
    }
    
    public double getAvailableStartTime(){
        return activityList.get(activityList.size()-1).getEndTime();
    }
    
    public String getResourceID(){
        return resourceID;
    }
    
    public boolean getAvailabilityStatus(){
        return isFree;
    }
    
    public void setAvailabilityStatus(boolean isFree){
        this.isFree = isFree;
    }
}

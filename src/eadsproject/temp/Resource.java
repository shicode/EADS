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
public class Resource {
    private int resourceID;
    private boolean availabilityStatus;
    
    public Resource(int resourceID, boolean availabilityStatus){
        this.resourceID = resourceID;
        this.availabilityStatus = availabilityStatus;
    }
    
    public int getResourceID(){
        return resourceID;
    }
    
    public boolean getAvailabilityStatus(){
        return availabilityStatus;
    }
    
    public void setAvailabilityStatus(boolean availabilityStatus){
        this.availabilityStatus = availabilityStatus;
    }
}

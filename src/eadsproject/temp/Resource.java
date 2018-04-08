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
    private String resourceID;
    private boolean isFree;
    
    public Resource(String resourceID, boolean isFree){
        this.resourceID = resourceID;
        this.isFree = isFree;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject.temp;

/**
 *
 * @author Shraddha
 */
public class ContainerAllocation {
    private Container container;
    private int destinationClusterId;
    private int numberOfTrucks;
    private int numberOfCranes;

    public ContainerAllocation(Container container, int destinationClustedId, int numberOfTrucks, int numberOfCranes)
    {
        this.container = container;
        this.destinationClusterId = destinationClustedId;
        this.numberOfTrucks = numberOfTrucks;
        this.numberOfCranes = numberOfCranes;
    }
    
    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public int getDestinationClusterId() {
        return destinationClusterId;
    }

    public void setDestinationClusterId(int destinationClusterId) {
        this.destinationClusterId = destinationClusterId;
    }

    public int getNumberOfTrucks() {
        return numberOfTrucks;
    }

    public void setNumberOfTrucks(int numberOfTrucks) {
        this.numberOfTrucks = numberOfTrucks;
    }

    public int getNumberOfCranes() {
        return numberOfCranes;
    }

    public void setNumberOfCranes(int numberOfCranes) {
        this.numberOfCranes = numberOfCranes;
    }
    
    
}

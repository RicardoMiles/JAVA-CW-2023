package edu.uob.GameEntities;

public class PathPair  {
    private String startLocation;
    private String endLocation;

    public PathPair(String startLocation, String endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public String getStartLocationFromPath() {
        return startLocation;
    }

    public String getEndLocationFromPath() {
        return endLocation;
    }

}

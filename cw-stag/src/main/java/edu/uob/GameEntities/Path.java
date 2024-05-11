package edu.uob.GameEntities;

import edu.uob.GameEntity;

//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;

public class Path extends GameEntity {
    private String name;
    private String description;

    private Location startLocation;
    private Location endLocation;
    public Path(String name, String description) {
        super(name, description);
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

}

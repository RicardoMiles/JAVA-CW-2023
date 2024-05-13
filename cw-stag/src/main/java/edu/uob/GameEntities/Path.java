package edu.uob.GameEntities;

//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;


public class Path {
    private String fromLocation;
    private String toLocation;

    // 构造函数
    public Path(String from, String to) {
        this.fromLocation = from;
        this.toLocation = to;
    }

    // Getter 和 Setter
    public String getFrom() {
        return fromLocation;
    }

    public void setFrom(String from) {
        this.fromLocation = from;
    }

    public String getTo() {
        return toLocation;
    }

    public void setTo(String to) {
        this.toLocation = to;
    }
}

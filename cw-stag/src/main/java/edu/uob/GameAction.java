package edu.uob;


import java.util.ArrayList;

public class GameAction {
    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> consumed = new ArrayList<>();
    private ArrayList<String> produced = new ArrayList<>();
    private String narration;

    public void addAttributes(String type, String attributeName) {
        switch (type) {
            case "subjects":
                subjects.add(attributeName);
                break;
            case "consumed":
                consumed.add(attributeName);
                break;
            case "produced":
                produced.add(attributeName);
                break;
            default:
                throw new RuntimeException("unrecognized action attribute.\n");
        }
    }

    public void addNarration(String narration){
        this.narration = narration;
    }

    public String getNarration(){
        return narration;
    }

    public ArrayList<String> getConsumed() {
        return consumed;
    }

    public ArrayList<String> getProduced() {
        return produced;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }
}


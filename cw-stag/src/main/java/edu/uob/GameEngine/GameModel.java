package edu.uob.GameEngine;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.File;

public class GameModel {
    private String startingLocation;
    private String currentPlayer;

    public GameModel(File entitiesFileName, File actionsFileName) throws IOException {
        loadEntitiesFile(entitiesFileName.getAbsolutePath());
        loadActionsFile(actionsFileName.getAbsolutePath());
    }

}

package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Parser {
    private String[] specialCharacters = {"(",")",",",";"};
    ArrayList<String> tokens = new ArrayList<String>();

    public List<String> parseCommand(String query) throws ParserException{
        List<String> tokens = new ArrayList<>();
        query = query.trim();
        String [] fragments = query.split("'");

        for (int i = 0; i < fragments.length; i++){
            if(i %2 !=0){
                tokens.add("'" + fragments[i] + "'");
            }else{
                String[] nextBatchOfTokens = tokenise(fragments[i]);
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }

        if (!tokens.get(0).equalsIgnoreCase("INSERT") && !tokens.get(0).equalsIgnoreCase("SELECT") /* 其他关键词 */) {
            throw new ParserException("Unsupported command: " + tokens.get(0));
        }

        return tokens;
    }

    private String[] tokenise(String input){
        for (String specialCharacter : specialCharacters) {
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        input = input.trim();
        return input.split("\\s+");
    }


}


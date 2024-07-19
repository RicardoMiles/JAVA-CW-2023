package edu.uob.GameEngine;

import java.util.ArrayList;
import java.util.List;


public class CommandNormalizer {
    private String matchedCommand;
    private String playerName;

    public CommandNormalizer(String incomingCommand) {
        // 步骤 1：处理第一个“:”之前的内容
        this.playerName = "";
        int colonIndex = incomingCommand.indexOf(':');
        if (colonIndex != -1) {
            this.playerName = incomingCommand.substring(0, colonIndex).replaceAll("\\s+", "");
        }

        // 步骤 2：处理第一个“:”之后的内容
        String remainingCommand = "";
        if (colonIndex != -1 && colonIndex + 1 < incomingCommand.length()) {
            remainingCommand = incomingCommand.substring(colonIndex + 1).trim();
        }

        // 使用空格分割字符串，并存储到列表中
        List<String> commandParts = new ArrayList<>();
        String[] parts = remainingCommand.split("\\s+");
        for (String part : parts) {
            commandParts.add(part);
        }

        // 输出测试
        System.out.println("Player Name: " + playerName);
        System.out.println("Command Parts: " + commandParts);

        this.matchedCommand = findMatchedCommand(commandParts);
    }

    public String findMatchedCommand(List<String> commandParts) {
        // 预定义的字符串列表
        List<String> predefinedCommands = List.of("get", "drop", "goto", "look", "inventory", "inv");

        // 比对命令部分
        String matchedCommand = null;
        int matchCount = 0;

        for (String commandPart : commandParts) {
            for (String predefinedCommand : predefinedCommands) {
                if (commandPart.equals(predefinedCommand)) {
                    matchCount++;
                    if (matchCount == 1) {
                        matchedCommand = predefinedCommand;
                    } else {
                        matchedCommand = "Conflicted unsupported multiple command";
                        break;
                    }
                }
            }
            if (matchCount > 1) {
                matchedCommand = "Conflicted unsupported multiple command";
                break;
            }
        }

        if (matchCount == 0) {
            matchedCommand = "No matched command";
        }

        // 输出匹配结果
        System.out.println("Matched Command: " + matchedCommand);

        return matchedCommand;
    }

    public String outputPlayerName() {
        return playerName;
    }

    public String outputMatchedCommand() {
        return matchedCommand;
    }

}

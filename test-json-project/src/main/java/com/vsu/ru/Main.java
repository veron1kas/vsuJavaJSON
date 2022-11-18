package com.vsu.ru;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.vsu.ru.console.Args;
import lombok.SneakyThrows;

import java.util.List;

public class Main {
    private final static PlayerService playerService = new PlayerServiceImpl();

    @SneakyThrows
    public static void main(String[] args) {
        Args consoleArgs = new Args();
        JCommander jc = new JCommander();
        jc.addObject(consoleArgs);
        try {
            jc.parse(args);
        }catch (ParameterException parameterException){
            jc.usage();
            return;
        }
        if(consoleArgs.getHelp()){
            jc.usage();
            return;
        }
        switch (consoleArgs.getCrudOperations()) {
            case GET -> {
                if (consoleArgs.getId() == null) {
                    List<Player> allPlayers = playerService.readPlayers();
                    playerService.writeToConsole(allPlayers);
                } else {
                    Player playerById = playerService.findById(consoleArgs.getId());
                    playerService.writeToConsole(playerById);
                }
            }
            case SAVE -> {
                if (consoleArgs.getInputFile() == null) {
                    System.err.println("input file is required if save crud operation");
                } else {
                    List<Player> playerList = playerService.readPlayersFromFile(consoleArgs.getInputFile());
                    playerService.createPlayers(playerList);
                }
            }
            case DELETE -> {
                if (consoleArgs.getId() == null) {
                    playerService.deleteAll();
                } else {
                    playerService.deleteById(consoleArgs.getId());
                }
            }
        }
    }
}
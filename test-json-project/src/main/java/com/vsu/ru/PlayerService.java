package com.vsu.ru;

import com.vsu.ru.Player;

import java.io.IOException;
import java.util.List;

public interface PlayerService {
    List<Player> readPlayersFromFile(String pathToFile) throws IOException;
    void writeToConsole(List<Player> players);
    void writeToConsole(Player player);
    String getAsString(List<Player> players);
    String getAsString(Player players);
    void writeToFile(String fileName, Player player);
    void writeToFile(String fileName, List<Player> players);
    void createPlayers(List<Player> players);
    void createPlayer(Player player);
    List<Player> readPlayers();
    List<Player> convertAllFromString(String stringPlayers);
    Player convertOneFromString(String stringPlayers);
    void deleteAll();
    void deleteById(Long id);
    Player findById(Long id);
}

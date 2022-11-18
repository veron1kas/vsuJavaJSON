package com.vsu.ru;

import com.vsu.ru.Player;

import java.io.IOException;
import java.util.List;

public interface PlayerService {
    List<Player> readPlayersFromFile(String pathToFile) throws IOException;
    void writeToConsole(List<Player> players);
    void writeToConsole(Player player);
    void createPlayers(List<Player> players);
    List<Player> readPlayers();
    void deleteAll();
    void deleteById(Long id);
    Player findById(Long id);
}

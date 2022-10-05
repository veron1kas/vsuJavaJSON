package com.vsu.ru.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vsu.ru.model.Player;

import java.io.IOException;
import java.util.List;

public interface PlayerService {
    List<Player> readPlayersFromFile(String pathToFile) throws IOException;
    void createPlayers(List<Player> players);
    List<Player> readPlayers();
    void deleteAll();
    void deleteById(Long id);
    Player findById(Long id);
}

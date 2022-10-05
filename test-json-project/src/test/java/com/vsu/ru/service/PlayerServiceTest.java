package com.vsu.ru.service;

import com.vsu.ru.model.Player;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerServiceTest{
    private final PlayerService playerService = new PlayerServiceImpl();



    @Test
    public void readTest() throws IOException {
        List<Player> players = playerService.readPlayersFromFile("players.json");
        assertEquals(10000, players.size());
    }

    @Test
    public void readAndSaveTest() throws IOException {
        List<Player> players = playerService.readPlayersFromFile("players.json");
        playerService.createPlayers(players);
        List<Player> playersFromDb = playerService.readPlayers();
        assertEquals(playersFromDb.size(), players.size());
        playerService.deleteAll();
        List<Player> playersAfterDeleteInDb = playerService.readPlayers();
        assertEquals(0, playersAfterDeleteInDb.size());
    }
}

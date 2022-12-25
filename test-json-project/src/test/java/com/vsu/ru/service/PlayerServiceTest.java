package com.vsu.ru.service;

import com.vsu.ru.Player;
import com.vsu.ru.PlayerService;
import com.vsu.ru.PlayerServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerServiceTest{
    private final PlayerService playerService = new PlayerServiceImpl();



    @Test
    public void readTest() throws IOException {
        List<Player> players = playerService.readPlayersFromFile("players.json");
        Player player = players.get(0);
        System.out.println(player.getPlayerId());
        assertEquals(10000, players.size());
    }

    @Test //это будет долго по скольку в файле 10000 игроков, и нужно их все сохранить в базе, потом достать оттуда, а потом еще и удалить.
    // НО зато этот тест "грубо говоря окончательны", он скажет , что все работает так, как надо
    //А, вообще, я думаю это можно оптимизировать
    public void readAndSaveTest() throws IOException {
        List<Player> players = playerService.readPlayersFromFile("players.json");
        playerService.createPlayers(players);
        List<Player> playersFromDb = playerService.readPlayers();
        assertEquals(playersFromDb.size(), players.size());
        playerService.deleteAll();
        List<Player> playersAfterDeleteInDb = playerService.readPlayers();
        assertEquals(0, playersAfterDeleteInDb.size());
    }

    @Test //это будет долго по скольку в файле 10000 игроков, и нужно их все сохранить в базе, потом достать оттуда, а потом еще и удалить.
    // НО зато этот тест "грубо говоря окончательны", он скажет , что все работает так, как надо
    //А, вообще, я думаю это можно оптимизировать
    public void readAndSaveTestFirstTen() throws IOException {
        List<Player> players = playerService.readPlayersFromFile("first_ten.json");
        playerService.createPlayers(players);
        List<Player> playersFromDb = playerService.readPlayers();
        assertEquals(playersFromDb.size(), players.size());
    }


    @SneakyThrows
    @Test
    public void writeToFileTest(){
        List<Player> players = playerService.readPlayersFromFile("players.json");
        List<Player> firstTen = players.subList(0, 10);
        playerService.writeToFile("first_ten.json", firstTen);
    }



}

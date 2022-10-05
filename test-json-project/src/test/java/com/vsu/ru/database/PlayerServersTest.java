package com.vsu.ru.database;

import com.vsu.ru.model.Currency;
import com.vsu.ru.model.Item;
import com.vsu.ru.model.Player;
import com.vsu.ru.model.Progress;
import com.vsu.ru.service.PlayerServiceImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlayerServersTest extends ServersAbstractTest<Player, Long>{
    private final DataBaseServers<Player,Long> playersServers = new PlayersServers();
    private final PlayerServiceImpl playerService = new PlayerServiceImpl();

    @Override
    protected DataBaseServers<Player, Long> getServers() {
        return playersServers;
    }


    @Test
    public void createAndReadTest() throws IOException {
        //остальное тестить нет особого смысла так как только здесь будет каскадное удаление и тд
        //так как чтение уже работает, можем прочитать из файла
        List<Player> players = playerService.readPlayersFromFile("players.json");
        //возьмем первого игрока
        Player player = players.get(0);
        //сохраним его
        playersServers.saveOrUpdate(player);
        //проверим теперь что все каскадно сохранилось, для этого прочитаем его
        Player read = playersServers.read(player.getId());
        assertNotNull(read);
        assertEquals(read.getNickname(), player.getNickname());
        assertEquals(read.getCurrencies().size(), player.getCurrencies().size());
        assertEquals(read.getProgresses().size(), player.getProgresses().size());
        assertEquals(read.getItems().size(), player.getItems().size());
    }
}

package com.vsu.ru;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class PlayerServiceImpl implements PlayerService{
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PlayersServers playersServers = new PlayersServers();
    {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
    }

    @Override
    public List<Player> readPlayersFromFile(String fileName) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName);
        return objectMapper.readValue(resourceAsStream, new TypeReference<List<Player>>() {});
    }

    @SneakyThrows
    @Override
    public void writeToConsole(List<Player> players) {
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(players));
    }

    @SneakyThrows
    @Override
    public void writeToConsole(Player player) {
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(player));
    }

    @SneakyThrows
    @Override
    public String getAsString(List<Player> players) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(players);
    }

    @SneakyThrows
    @Override
    public String getAsString(Player players) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(players);
    }

    @SneakyThrows
    @Override
    public void writeToFile(String fileName, Player player) {
        File f = new File(fileName);
        f.createNewFile();
        try(OutputStream outputStream =
                    new FileOutputStream(f)){
            PrintStream ps = new PrintStream(outputStream);
            ps.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(player));
        }
    }

    @SneakyThrows
    @Override
    public void writeToFile(String fileName, List<Player> players) {
        File f = new File(fileName);
        f.createNewFile();
        try(OutputStream outputStream =
                    new FileOutputStream(f)){
            PrintStream ps = new PrintStream(outputStream);
            ps.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(players));
        }
    }

    @Override
    public void createPlayers(List<Player> players) {
        playersServers.saveOrUpdateAll(players);
    }

    @Override
    public void createPlayer(Player player) {
        playersServers.saveOrUpdate(player);
    }

    @Override
    public List<Player> readPlayers() {
        return playersServers.readAll();
    }

    @SneakyThrows
    @Override
    public List<Player> convertAllFromString(String stringPlayers) {
        return Arrays.stream(objectMapper.readValue(stringPlayers, Player[].class)).toList();
    }

    @SneakyThrows
    @Override
    public Player convertOneFromString(String stringPlayers) {
        return objectMapper.readValue(stringPlayers, Player.class);
    }

    @Override
    public void deleteAll() {
        playersServers.deleteAll();
    }

    @Override
    public void deleteById(Long id) {
        playersServers.delete(id);
    }

    @Override
    public Player findById(Long id) {
        return playersServers.read(id);
    }
}

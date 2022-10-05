package com.vsu.ru;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayersServers extends DataBaseAbstractServers<Player, Long>{
    private final ProgressServers progressServers;
    private final CurrencyServers currencyServers;
    private final ItemsServers itemsServers;

    public PlayersServers(){
        progressServers = new ProgressServers();
        currencyServers = new CurrencyServers();
        itemsServers = new ItemsServers();
    }


    private static final String INSERT_PLAYERS_SQL = "INSERT INTO players(playerId, nickname) VALUES (?, ?)";

    private static final String READ_PLAYERS_SQL = "SELECT pl.playerid, pl.nickname, " +
            "pr.id as pr_id, pr.score as pr_score, pr.maxscore as pr_maxscore, pr.resourceid as pr_resource_id, " +
            "i.id as i_id, i.count as i_count, i.level as i_level, i.resourceid as i_resource_id, " +
            "c.id as c_id, c.resourceid as c_resource_id, c.count as c_count, c.name as c_name " +
            "FROM players pl " + // вычитываем все данные из всех таблиц наджойнив их на игрока
            "join progresses pr on pl.playerid = pr.playerid " +
            "join player_item_map pim on pl.playerid = pim.playerid " +
            "join items i on pim.itemid = i.id " +
            "join player_currency_map pcm on pl.playerid = pcm.playerid " +
            "join currencies c on c.id = pcm.currencyid";

    private static final String READ_PLAYER_BY_ID_SQL = "SELECT pl.playerid, pl.nickname, " +
            "pr.id as pr_id, pr.score as pr_score, pr.maxscore as pr_maxscore, pr.resourceid as pr_resource_id, " +
            "i.id as i_id, i.count as i_count, i.level as i_level, i.resourceid as i_resource_id, " +
            "c.id as c_id, c.resourceid as c_resource_id, c.count as c_count, c.name as c_name " +
            "FROM players pl " + // вычитываем все данные из всех таблиц наджойнив их на игрока
            "right join progresses pr on pl.playerid = pr.playerid " +
            "right join player_item_map pim on pl.playerid = pim.playerid " +
            "right join items i on pim.itemid = i.id " +
            "right join player_currency_map pcm on pl.playerid = pcm.playerid " +
            "right join currencies c on c.id = pcm.currencyid " +
            "where pl.playerid = ?";

    private static final String UPDATE_PLAYERS_SQL = "UPDATE players SET nickname = ? where playerid = ?;";
    private static final String DELETE_PLAYER_SQL = "DELETE FROM players WHERE playerId = ?;";
    private static final String DELETE_PLAYERS_SQL = "DELETE FROM players;";
    private static final String EXISTS_PLAYER_BY_ID_SQL = "SELECT EXISTS(SELECT * FROM players where playerid = ?);";
    private static final String DELETE_FROM_PLAYER_ITEM_MAP_SQL = "DELETE FROM player_item_map where playerId = ? and itemid = ?;";
    private static final String DELETE_FROM_PLAYER_CURRENCY_MAP_SQL = "DELETE FROM player_currency_map where playerid = ? and currencyid = ?;";
    private static final String DELETE_ALL_FROM_PLAYER_ITEM_MAP_BY_PLAYER_ID_SQL = "DELETE FROM player_item_map where playerId = ?;";
    private static final String DELETE_ALL_FROM_PLAYER_CURRENCY_MAP_BY_PLAYER_ID_SQL = "DELETE FROM player_currency_map where playerid = ?;";
    private static final String DELETE_ALL_FROM_PLAYER_ITEM_MAP_SQL = "DELETE FROM player_item_map;";
    private static final String DELETE_ALL_FROM_PLAYER_CURRENCY_MAP_SQL = "DELETE FROM player_currency_map;";
    private static final String INSERT_IN_PLAYER_ITEM_MAP_SQL = "INSERT INTO player_item_map(playerid, itemid) values(?, ?);";
    private static final String INSERT_IN_PLAYER_CURRENCY_MAP_SQL = "INSERT INTO player_currency_map(playerid, currencyid) values(?, ?);";
    private static final String EXISTS_IN_PLAYER_CURRENCY_MAP_SQL = "SELECT EXISTS(SELECT * FROM player_currency_map where currencyid = ? and playerid = ?);";
    private static final String EXISTS_IN_PLAYER_ITEM_MAP_SQL = "SELECT EXISTS(SELECT * FROM player_item_map where itemid = ? and playerid = ?);";

    @Override
    protected void update(Connection connection, Player newPlayer) throws SQLException {
        //если здесь, значит игрок существует, нужно проверить, что в нем ничего не изменилось,
        // а если изменилось то изменить каскадно
        //достанем старого игрока
        Player oldPlayer = readAndConvert(connection, newPlayer.getId());
        Map<Long, Currency> oldPlayerCurrencies = oldPlayer.getCurrencies();
        Map<Long, Item> oldPlayerItems = oldPlayer.getItems();
        Map<Long, Progress> oldPlayerProgresses = oldPlayer.getProgresses().stream().collect(Collectors.toMap(Progress::getId, Function.identity()));
        //проверяем существующие
        for(var s : newPlayer.getItems().entrySet()){
            if(oldPlayerItems.containsKey(s.getKey())){
                itemsServers.update(connection, s.getValue());
                oldPlayerItems.remove(s.getKey());
            }else{
                itemsServers.insert(connection, s.getValue());
            }
        }
        //удаляем у этого игрока оставшиеся
        for(var s : oldPlayerItems.entrySet()){
            deleteFromPlayerItemMap(connection, newPlayer.getPlayerId(), s.getKey());
            //тут наверное стоит проверить что есть другие игроки которые мапятся на эту сущность
        }
        //и так же со всеми
        for(var s : newPlayer.getCurrencies().entrySet()){
            if(oldPlayerCurrencies.containsKey(s.getKey())){
                currencyServers.update(connection, s.getValue());
                oldPlayerCurrencies.remove(s.getKey());
            }else{
               currencyServers.insert(connection, s.getValue());
            }
        }
        for(var s : oldPlayerCurrencies.entrySet()){
            deleteFromPlayerCurrencyMap(connection, newPlayer.getPlayerId(), s.getKey());
        }
        for(var s : newPlayer.getProgresses()){
            if(oldPlayerCurrencies.containsKey(s.getId())){
                progressServers.update(connection, s);
                oldPlayerProgresses.remove(s.getId());
            }else{
                progressServers.insert(connection, s);
            }
        }
        for(var s : oldPlayerProgresses.entrySet()){
            progressServers.delete(s.getKey());
        }

        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PLAYERS_SQL);
        preparedStatement.setString(1, newPlayer.getNickname());
        preparedStatement.setLong(2, newPlayer.getPlayerId());
        preparedStatement.executeUpdate();
    }

    @Override
    protected void insert(Connection connection, Player newPlayer) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PLAYERS_SQL);
        preparedStatement.setLong(1, newPlayer.getPlayerId());
        preparedStatement.setString(2, newPlayer.getNickname());
        preparedStatement.executeUpdate();
        newPlayer.getProgresses().forEach(progressServers::saveOrUpdate);
        for(var item : newPlayer.getItems().values()) {
            itemsServers.saveOrUpdate(item);
            if (!existsInPlayerItemMap(connection, newPlayer.getPlayerId(), item.getId())) {
                insertIntoPlayerItemMap(connection, newPlayer.getPlayerId(), item.getId());
            }
        }

        for(var currency : newPlayer.getCurrencies().values()){
            currencyServers.saveOrUpdate(currency);
            if(!existsInPlayerCurrencyMap(connection, newPlayer.getPlayerId(), currency.getId())){
                insertIntoPlayerCurrencyMap(connection, newPlayer.getPlayerId(), currency.getId());
            }
        }

    }

    @Override
    protected void delete(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PLAYER_SQL);
        preparedStatement.setLong(1, id);
        deleteAllFromPlayerCurrencyMapByPlayerId(connection, id);
        deleteAllFromPlayerItemMapByPlayerId(connection, id);
        progressServers.deleteAllByPlayerId(connection, id);
        preparedStatement.executeUpdate();
    }

    @Override
    protected void delete(Connection connection) throws SQLException {
        progressServers.deleteAll();
        deleteAllFromPlayerCurrencyMap(connection);
        deleteAllFromPlayerItemMap(connection);
        currencyServers.deleteAll();
        itemsServers.deleteAll();
        connection.prepareStatement(DELETE_PLAYERS_SQL).executeUpdate();
    }

    @Override
    protected List<Player> readAllAndConvert(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(READ_PLAYERS_SQL);
        preparedStatement.execute();
        return convertToPlayers(preparedStatement.getResultSet());
    }

    @Override
    protected Player readAndConvert(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(READ_PLAYER_BY_ID_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        List<Player> players = convertToPlayers(preparedStatement.getResultSet());
        return players.stream().findAny().orElse(null);
    }

    @Override
    protected boolean isExists(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_PLAYER_BY_ID_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        preparedStatement.getResultSet().next();
        return preparedStatement.getResultSet().getBoolean(1);
    }


    private List<Player> convertToPlayers(ResultSet resultSet) throws SQLException {
        Map<Long, Player> playerMap = new HashMap<>();
        Map<Long, Currency> currencyMap = new HashMap<>();
        Map<Long, Item> itemMap = new HashMap<>();
        Map<Long, Progress> progressMap = new HashMap<>();

        while(resultSet.next()){
            Long playerId = resultSet.getLong("playerId");
            String playerName = resultSet.getString("nickname");
            Long progressId = resultSet.getLong("pr_id");
            Integer progressScore = resultSet.getInt("pr_score");
            Integer progressMaxScore = resultSet.getInt("pr_maxscore");
            Long progressResourceId = resultSet.getLong("pr_resource_id");
            Long itemId = resultSet.getLong("i_id");
            Integer itemCount = resultSet.getInt("i_count");
            Integer itemLevel = resultSet.getInt("i_level");
            Long itemResourceId = resultSet.getLong("i_resource_id");
            Long currencyId = resultSet.getLong("c_id");
            Long currencyResourceId = resultSet.getLong("c_resource_id");
            Integer currencyCount = resultSet.getInt("c_count");
            String currencyName = resultSet.getString("c_name");
            //теперь распределяем
            if(playerId != 0) {
                if (!playerMap.containsKey(playerId)) {
                    playerMap.put(playerId, Player.builder()
                            .playerId(playerId)
                            .items(new HashMap<>())
                            .currencies(new HashMap<>())
                            .progresses(new ArrayList<>())
                            .nickname(playerName).build());
                }
            }else{
                //странный кейс
                continue;
            }
            if(progressId != 0) {
                if (!progressMap.containsKey(progressId)) {
                    Progress progress = Progress.builder()
                            .playerId(playerId)
                            .id(progressId)
                            .maxScore(progressMaxScore)
                            .score(progressScore)
                            .resourceId(progressResourceId)
                            .build();
                    playerMap.get(playerId).getProgresses().add(progress);
                    progressMap.put(progressId, progress);
                }
            }
            if(itemId != 0) {
                if (!itemMap.containsKey(itemId)) {
                    Item item = Item.builder()
                            .count(itemCount)
                            .id(itemId)
                            .resourceId(itemResourceId)
                            .level(itemLevel).build();
                    playerMap.get(playerId).getItems().put(itemId, item);
                    itemMap.put(itemId, item);
                }
            }
            if (currencyId != 0) {
                if(!currencyMap.containsKey(currencyId)){
                    Currency currency = Currency.builder()
                            .count(currencyCount)
                            .name(currencyName)
                            .id(currencyId)
                            .resourceId(currencyResourceId)
                            .build();
                    playerMap.get(playerId).getCurrencies().put(currencyId, currency);
                    currencyMap.put(currencyId, currency);
                }
            }
        }
        return playerMap.values().stream().toList();
    }

    private void deleteFromPlayerItemMap(Connection connection, Long playerId, Long ItemId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FROM_PLAYER_ITEM_MAP_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.setLong(2, ItemId);
        preparedStatement.executeUpdate();
    }

    private void deleteFromPlayerCurrencyMap(Connection connection, Long playerId, Long currencyId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FROM_PLAYER_CURRENCY_MAP_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.setLong(2, currencyId);
        preparedStatement.executeUpdate();
    }


    private void deleteAllFromPlayerCurrencyMapByPlayerId(Connection connection, Long playerId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_FROM_PLAYER_CURRENCY_MAP_BY_PLAYER_ID_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.executeUpdate();
    }

    private void deleteAllFromPlayerItemMapByPlayerId(Connection connection, Long playerId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_FROM_PLAYER_ITEM_MAP_BY_PLAYER_ID_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.executeUpdate();
    }

    private void deleteAllFromPlayerItemMap(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_FROM_PLAYER_ITEM_MAP_SQL);
        preparedStatement.executeUpdate();
    }

    private void deleteAllFromPlayerCurrencyMap(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_FROM_PLAYER_CURRENCY_MAP_SQL);
        preparedStatement.executeUpdate();
    }

    private boolean existsInPlayerCurrencyMap(Connection connection, Long playerId, Long currencyId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_IN_PLAYER_CURRENCY_MAP_SQL);
        preparedStatement.setLong(1, currencyId);
        preparedStatement.setLong(2, playerId);
        preparedStatement.execute();
        preparedStatement.getResultSet().next();
        return preparedStatement.getResultSet().getBoolean(1);
    }
    private boolean existsInPlayerItemMap(Connection connection, Long playerId, Long itemId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_IN_PLAYER_ITEM_MAP_SQL);
        preparedStatement.setLong(1, itemId);
        preparedStatement.setLong(2, playerId);
        preparedStatement.execute();
        preparedStatement.getResultSet().next();
        return preparedStatement.getResultSet().getBoolean(1);
    }

    private void insertIntoPlayerCurrencyMap(Connection connection, Long playerId, Long currencyId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_IN_PLAYER_CURRENCY_MAP_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.setLong(2, currencyId);
        preparedStatement.executeUpdate();
    }
    private void insertIntoPlayerItemMap(Connection connection, Long playerId, Long itemId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_IN_PLAYER_ITEM_MAP_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.setLong(2, itemId);
        preparedStatement.executeUpdate();
    }


}

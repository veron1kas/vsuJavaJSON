package com.vsu.ru;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgressServers extends DataBaseAbstractServers<Progress, Long>{

    private static final String INSERT_PROGRESS_SQL = "INSERT INTO progresses" +
            "  (id, playerid, resourceid, score, maxscore) VALUES " +
            " (?, ?, ?, ?, ?);";
    private static final String READ_PROGRESSES_SQL = "SELECT * FROM progresses;";
    private static final String READ_PROGRESS_BY_ID_SQL = "SELECT * FROM progresses where id = ?;";
    private static final String UPDATE_PROGRESS_SQL = "UPDATE progresses SET (playerid, resourceid, score, maxscore) = (?, ?, ?, ?) where id = ?;";
    private static final String DELETE_PROGRESS_SQL = "DELETE FROM progresses WHERE id = ?;";
    private static final String DELETE_PROGRESSES_SQL = "DELETE FROM progresses;";
    private static final String DELETE_PROGRESSES_BY_PLAYER_ID_SQL = "DELETE FROM progresses where playerid = ?;";
    private static final String EXISTS_BY_ID_SQL = "SELECT EXISTS(SELECT * FROM progresses where id = ?);";

    @Override
    protected void update(Connection connection, Progress item) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PROGRESS_SQL);
        preparedStatement.setLong(1, item.getPlayerId());
        preparedStatement.setLong(2, item.getResourceId());
        preparedStatement.setInt(3, item.getScore());
        preparedStatement.setInt(4, item.getMaxScore());
        preparedStatement.setLong(5, item.getId());
        preparedStatement.executeUpdate();
    }

    @Override
    protected void insert(Connection connection, Progress item) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROGRESS_SQL);
        if(item.getMaxScore() < item.getScore()){
            item.setMaxScore(item.getScore()); //это странно - такого быть не должно
        }
        preparedStatement.setLong(1, item.getId());
        preparedStatement.setLong(2, item.getPlayerId());
        preparedStatement.setLong(3, item.getResourceId());
        preparedStatement.setInt(4, item.getScore());
        preparedStatement.setInt(5, item.getMaxScore());
        preparedStatement.executeUpdate();
    }

    @Override
    protected void delete(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PROGRESS_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    protected void delete(Connection connection) throws SQLException {
        connection.prepareStatement(DELETE_PROGRESSES_SQL).executeUpdate();
    }

    @Override
    protected List<Progress> readAllAndConvert(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(READ_PROGRESSES_SQL);
        preparedStatement.execute();
        return convertToItems(preparedStatement.getResultSet());
    }

    @Override
    protected Progress readAndConvert(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(READ_PROGRESS_BY_ID_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        return convertToItems(preparedStatement.getResultSet()).stream().findAny().orElse(null);
    }

    private List<Progress> convertToItems(ResultSet resultSet) throws SQLException {
        List<Progress> result = new ArrayList<>();
        while(resultSet.next()){
            Long resourceId = resultSet.getLong("resourceid");
            Long id = resultSet.getLong("id");
            Long playerId = resultSet.getLong("playerid");
            int score = resultSet.getInt("score");
            int maxScore = resultSet.getInt("maxscore");
            Progress item = Progress.builder()
                    .id(id)
                    .resourceId(resourceId)
                    .playerId(playerId)
                    .score(score)
                    .maxScore(maxScore)
                    .build();
            result.add(item);
        }
        return result;
    }

    @Override
    protected boolean isExists(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_BY_ID_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        preparedStatement.getResultSet().next();
        return preparedStatement.getResultSet().getBoolean(1);
    }

    protected void deleteAllByPlayerId(Connection connection, Long playerId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PROGRESSES_BY_PLAYER_ID_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.executeUpdate();
    }
}

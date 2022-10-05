package com.vsu.ru;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyServers extends DataBaseAbstractServers<Currency, Long>{
    private static final String INSERT_CURRENCY_SQL = "INSERT INTO currencies" +
            "  (id, resourceid, name, count) VALUES " +
            " (?, ?, ?, ?);";
    private static final String READ_CURRENCIES_SQL = "SELECT * FROM currencies;";
    private static final String READ_CURRENCY_BY_ID_SQL = "SELECT * FROM currencies where id = ?;";
    private static final String UPDATE_CURRENCY_SQL = "UPDATE currencies SET (resourceid, name, count) = (?, ?, ?) where id = ?;";
    private static final String DELETE_CURRENCY_SQL = "DELETE FROM currencies WHERE id = ?;";
    private static final String DELETE_CURRENCIES_SQL = "DELETE FROM currencies;";
    private static final String EXISTS_BY_ID_SQL = "SELECT EXISTS(SELECT * FROM currencies where id = ?);";

    @Override
    protected void update(Connection connection, Currency item) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CURRENCY_SQL);
        preparedStatement.setLong(1, item.getResourceId());
        preparedStatement.setString(2, item.getName());
        preparedStatement.setInt(3, item.getCount());
        preparedStatement.setLong(4, item.getId());
        preparedStatement.executeUpdate();
    }

    @Override
    protected void insert(Connection connection, Currency item) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CURRENCY_SQL);
        preparedStatement.setLong(1, item.getId());
        preparedStatement.setLong(2, item.getResourceId());
        preparedStatement.setString(3, item.getName());
        preparedStatement.setInt(4, item.getCount());
        preparedStatement.executeUpdate();
    }

    @Override
    protected void delete(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CURRENCY_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    protected void delete(Connection connection) throws SQLException {
        connection.prepareStatement(DELETE_CURRENCIES_SQL).executeUpdate();
    }

    @Override
    protected List<Currency> readAllAndConvert(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(READ_CURRENCIES_SQL);
        preparedStatement.execute();
        return convertToItems(preparedStatement.getResultSet());
    }

    @Override
    protected Currency readAndConvert(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(READ_CURRENCY_BY_ID_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        return convertToItems(preparedStatement.getResultSet()).stream().findAny().orElse(null);
    }

    private List<Currency> convertToItems(ResultSet resultSet) throws SQLException {
        List<Currency> result = new ArrayList<>();
        if(resultSet != null) {
            while (resultSet.next()) {
                Long resourceId = resultSet.getLong("resourceid");
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                int count = resultSet.getInt("count");
                Currency item = Currency.builder()
                        .id(id)
                        .resourceId(resourceId)
                        .count(count)
                        .name(name)
                        .build();
                result.add(item);
            }
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
}

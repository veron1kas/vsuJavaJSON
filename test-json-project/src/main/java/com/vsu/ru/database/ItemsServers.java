package com.vsu.ru.database;

import com.vsu.ru.model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemsServers extends DataBaseAbstractServers<Item, Long>{

    private static final String INSERT_ITEMS_SQL = "INSERT INTO items" +
            "  (id, count, level, resourceid) VALUES " +
            " (?, ?, ?, ?);";
    private static final String READ_ITEMS_SQL = "SELECT * FROM items;";
    private static final String READ_ITEM_BY_ID_SQL = "SELECT * FROM items where id = ?;";
    private static final String UPDATE_ITEM_SQL = "UPDATE items SET (count, level, resourceid) = (?, ?, ?) where id = ?;";
    private static final String DELETE_ITEM_SQL = "DELETE FROM items WHERE id = ?;";
    private static final String DELETE_ITEMS_SQL = "DELETE FROM items;";
    private static final String EXISTS_BY_ID_SQL = "SELECT EXISTS(SELECT * FROM items where id = ?);";

    @Override
    protected void update(Connection connection, Item item) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ITEM_SQL);
        preparedStatement.setInt(1, item.getCount());
        preparedStatement.setInt(2, item.getLevel());
        preparedStatement.setLong(3, item.getResourceId());
        preparedStatement.setLong(4, item.getId());
        preparedStatement.executeUpdate();
    }

    @Override
    protected void insert(Connection connection, Item item) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ITEMS_SQL);
        preparedStatement.setLong(1, item.getId());
        preparedStatement.setInt(2, item.getCount());
        preparedStatement.setInt(3, item.getLevel());
        preparedStatement.setLong(4, item.getResourceId());
        preparedStatement.executeUpdate();
    }

    @Override
    protected void delete(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ITEM_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    protected List<Item> readAllAndConvert(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(READ_ITEMS_SQL);
        preparedStatement.execute();
        return convertToItems(preparedStatement.getResultSet());
    }

    @Override
    protected Item readAndConvert(Connection connection, Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(READ_ITEM_BY_ID_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        List<Item> items = convertToItems(preparedStatement.getResultSet());
        return items.stream().findAny().orElse(null);
    }

    private List<Item> convertToItems(ResultSet resultSet) throws SQLException {
        List<Item> result = new ArrayList<>();
        while(resultSet.next()){
            Long resourceId = resultSet.getLong("resourceid");
            Long id = resultSet.getLong("id");
            int count = resultSet.getInt("count");
            int level = resultSet.getInt("level");
            Item item = Item.builder()
                    .id(id)
                    .resourceId(resourceId)
                    .count(count)
                    .level(level)
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

    @Override
    protected void delete(Connection connection) throws SQLException {
        connection.prepareStatement(DELETE_ITEMS_SQL).executeUpdate();
    }
}

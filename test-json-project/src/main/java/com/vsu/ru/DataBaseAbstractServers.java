package com.vsu.ru;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public abstract class DataBaseAbstractServers<T extends DataBaseItem<K>, K> implements DataBaseServers<T, K>{
    private static final String url = "jdbc:postgresql://localhost:5432/game";
    private static final String user = "postgres";
    private static final String password = "456852";


    @Override
    public void delete(K id) {
        try (Connection connection = DriverManager.getConnection(url, user, password)){
            // Step 2:Create a statement using connection object
            delete(connection, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = DriverManager.getConnection(url, user, password)){
            // Step 2:Create a statement using connection object
            delete(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveOrUpdate(T item) {
        try (Connection connection = DriverManager.getConnection(url, user, password)){
             // Step 2:Create a statement using connection object
            if(item.getId() != null && isExists(connection, item.getId())){
                update(connection, item);
            }else{
                insert(connection, item);
            }
            // Step 3: Execute the query or update query
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T read(K id) {
        try (Connection connection = DriverManager.getConnection(url, user, password)){
             // Step 2:Create a statement using connection object
             return readAndConvert(connection, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> readAll() {
         try (Connection connection = DriverManager.getConnection(url, user, password)){
            // Step 2:Create a statement using connection object
            return readAllAndConvert(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        };
         return null;
    }

    @Override
    public void saveOrUpdateAll(Iterable<T> iterable) {
        iterable.forEach(this::saveOrUpdate);
    }

    @Override
    public boolean isExist(K id) {
        try (Connection connection = DriverManager.getConnection(url, user, password)){
            // Step 2:Create a statement using connection object
            return isExists(connection, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected abstract void update(Connection connection, T item) throws SQLException;
    protected abstract void insert(Connection connection, T item) throws SQLException;
    protected abstract void delete(Connection connection, K id) throws SQLException;
    protected abstract void delete(Connection connection) throws SQLException;
    protected abstract List<T> readAllAndConvert(Connection connection) throws SQLException;
    protected abstract T readAndConvert(Connection connection, K id) throws SQLException;
    protected abstract boolean isExists(Connection connection, K id) throws SQLException;
}

package com.vsu.ru.database;

import com.vsu.ru.CurrencyServers;
import com.vsu.ru.DataBaseServers;
import com.vsu.ru.Currency;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CurrencyServersTest extends ServersAbstractTest<Currency, Long>{
    private final DataBaseServers<Currency, Long> currencyServers = new CurrencyServers();

    @Override
    protected DataBaseServers<Currency, Long> getServers() {
        return currencyServers;
    }

    @Test
    public void createAndReadTest(){
        Currency currency = Currency.builder()
                .name("testName")
                .resourceId(123L)
                .count(2)
                .id(55L)
                .build();
        currencyServers.saveOrUpdate(currency);
        //теперь достанем из базы и проверим что он есть
        Currency read = currencyServers.read(currency.getId());
        assertNotNull(read);
        assertEquals(currency.getName(), read.getName());
    }

    @Test
    public void creteFewAndReadFewTest(){
        Currency one = Currency.builder()
                .name("testOne")
                .resourceId(123L)
                .count(2)
                .id(55L)
                .build();
        Currency two = Currency.builder()
                .name("testTwo")
                .resourceId(124L)
                .count(2)
                .id(56L)
                .build();
        List<Currency> currencyList = List.of(one, two);
        currencyServers.saveOrUpdateAll(currencyList);
        List<Currency> allInDataBase = currencyServers.readAll();
        assertEquals(currencyList.size(), allInDataBase.size());
    }

    @Test
    public void deleteAndReadTest(){
        Currency one = Currency.builder()
                .name("testOne")
                .resourceId(123L)
                .count(2)
                .id(55L)
                .build();
        Currency two = Currency.builder()
                .name("testTwo")
                .resourceId(124L)
                .count(2)
                .id(56L)
                .build();
        List<Currency> currencyList = List.of(one, two);
        currencyServers.saveOrUpdateAll(currencyList);
        // сейчас их в базе два, удалим первый
        currencyServers.delete(one.getId());
        //ожидаем что в базе остался один
        List<Currency> currencies = currencyServers.readAll();
        assertEquals(1, currencies.size());
    }

}

package com.vsu.ru.database;

import com.vsu.ru.model.DataBaseItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class ServersAbstractTest<T extends DataBaseItem<K>, K> {


    @BeforeEach
    @AfterEach
    public void clean(){
        //как раз тестируем удаление
        getServers().deleteAll();
    }

    protected abstract DataBaseServers<T, K> getServers();

}

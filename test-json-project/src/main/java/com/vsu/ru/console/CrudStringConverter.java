package com.vsu.ru.console;

import com.beust.jcommander.IStringConverter;

public class CrudStringConverter implements IStringConverter<CrudOperations> {
    @Override
    public CrudOperations convert(String value) {
        return CrudOperations.valueOf(value);
    }
}

package com.vsu.ru.console;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class Args {

    @Parameter(names = {"--crud-operation", "-co"}, description = "Crud type of operation", required = true, converter = CrudStringConverter.class)
    private CrudOperations crudOperations;

    @Parameter(names = {"--input-file", "-if"}, description = "Path of the input file")
    private String inputFile;

    @Parameter(names = {"-id"}, description = "Id of the player")
    private Long id;

    @Parameter(names = "--help", help = true)
    private Boolean help = true;
}

package com.vsu.ru;

import com.vsu.ru.service.PlayerService;
import com.vsu.ru.service.PlayerServiceImpl;

public class Main {
    public static void main(String[] args) {
        PlayerService playerService = new PlayerServiceImpl();
    }
}
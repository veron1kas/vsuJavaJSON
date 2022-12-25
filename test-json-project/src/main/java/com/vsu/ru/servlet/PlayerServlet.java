package com.vsu.ru.servlet;

import com.vsu.ru.Player;
import com.vsu.ru.PlayerService;
import com.vsu.ru.PlayerServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/player/*")
public class PlayerServlet extends HttpServlet {
    private static final PlayerService playerService = new PlayerServiceImpl();

    public PlayerServlet() {
        /*try {
            List<Player> players = playerService.readPlayersFromFile("first_ten.json");
            System.out.println(players);
            playerService.createPlayers(players);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestURI = req.getRequestURI();
        String[] split = requestURI.split("/");
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if(split.length == 2){
            out.println(playerService.getAsString(playerService.readPlayers()));
        }else if(split.length == 3){
            Long id = Long.valueOf(split[2]);
            out.println(playerService.getAsString(playerService.findById(id)));
        }else{
            resp.sendError(400);
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String stringPlayers = readDataFromRequest(req);
        List<Player> players = playerService.convertAllFromString(stringPlayers);
        playerService.createPlayers(players);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String stringPlayer = readDataFromRequest(req);
        String requestURI = req.getRequestURI();
        String[] split = requestURI.split("/");
        if(split.length != 3){
            resp.sendError(400);
        }else{
            Long id = Long.valueOf(split[2]);
            Player player = playerService.convertOneFromString(stringPlayer);
            player.setPlayerId(id);
            playerService.createPlayer(player);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestURI = req.getRequestURI();
        String[] split = requestURI.split("/");
        if(split.length != 3){
            resp.sendError(400);
        }else{
            Long id = Long.valueOf(split[2]);
            playerService.deleteById(id);
        }
    }

    private String readDataFromRequest(HttpServletRequest req){
        StringBuilder jb = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        return jb.toString();
    }
}

package com.juego16bits.juego16bits.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.juego16bits.juego16bits.dto.DecisionRequest;
import com.juego16bits.juego16bits.dto.GameState;
import com.juego16bits.juego16bits.dto.StartGameRequest;
import com.juego16bits.juego16bits.dto.UseItemRequest;
import com.juego16bits.juego16bits.service.GameService;


@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Estado actual del juego (útil para refrescar UI)
     */
    @GetMapping("/")
    public GameState getState() {
        return gameService.startGame();
    }

    @PostMapping("/start")
    public GameState start(@RequestBody StartGameRequest req) {
        return gameService.startNewGame(req.name());
    }

    @PostMapping("/decision")
    public GameState takeDecision(@RequestBody DecisionRequest request) {
        return gameService.takeDecision(request);
    }

    @PostMapping("/use-item")
    public GameState useItem(@RequestBody UseItemRequest request) {
        return gameService.useItem(request.itemId());
    }

    @PostMapping("/reset")
    public GameState reset() {
        return gameService.resetGame();
    }

    /**
     * Guardar partida (Hibernate)
     */
    @PostMapping("/save")
    public String saveGame() {
        gameService.saveGame();
        return "OK";
    }

    @PostMapping("/load")
    public GameState load() {
        return gameService.loadGame();
    }
}

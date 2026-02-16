package com.juego16bits.juego16bits.dto;

import java.util.List;

import com.juego16bits.juego16bits.model.Decision;
import com.juego16bits.juego16bits.model.Location;
import com.juego16bits.juego16bits.model.Player;

public record GameState(
        Player player,
        Location location,
        List<Decision> decisions,
        String message,
        boolean gameOver
) {}

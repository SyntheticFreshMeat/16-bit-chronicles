package com.juego16bits.juego16bits.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juego16bits.juego16bits.entity.SavedGame;

public interface SavedGameRepository extends JpaRepository<SavedGame, Long> {

    Optional<SavedGame> findTopByPlayerNameOrderByIdDesc(String playerName);
}

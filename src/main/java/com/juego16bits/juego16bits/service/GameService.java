package com.juego16bits.juego16bits.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.juego16bits.juego16bits.config.GameConstants;
import com.juego16bits.juego16bits.dto.DecisionRequest;
import com.juego16bits.juego16bits.dto.GameState;
import com.juego16bits.juego16bits.entity.SavedGame;
import com.juego16bits.juego16bits.model.Decision;
import com.juego16bits.juego16bits.model.Location;
import com.juego16bits.juego16bits.model.Player;
import com.juego16bits.juego16bits.repository.SavedGameRepository;

@Service
public class GameService {

    private final SavedGameRepository savedGameRepository;

    // ya no es final para poder resetear y para /start con nombre
    private Player player = new Player("Josep");

    private Location currentLocation = new Location(
            GameConstants.LOCATION_FOREST,
            GameConstants.FOREST_DESCRIPTION,
            "Lirien.png");

    private List<Decision> currentDecisions = new ArrayList<>();

    public GameService(SavedGameRepository savedGameRepository) {
        this.savedGameRepository = savedGameRepository;
        currentDecisions = getDecisionsForLocation(currentLocation.getName());
    }

    /**
     * NEW GAME: permite iniciar partida con nombre (para mostrar Hibernate “bien”)
     */
    public GameState startNewGame(String name) {
        String safeName = (name == null || name.isBlank()) ? "Hero" : name.trim();

        player = new Player(safeName);

        currentLocation = new Location(
                GameConstants.LOCATION_FOREST,
                GameConstants.FOREST_DESCRIPTION,
                getImageForLocation(GameConstants.LOCATION_FOREST));

        currentDecisions = getDecisionsForLocation(currentLocation.getName());

        return new GameState(
                player,
                currentLocation,
                currentDecisions,
                "El aire es denso... El Bosque de Lirien te observa en silencio.",
                player.isDead());
    }

    public GameState loadGame() {
        String name = player.getName();

        return savedGameRepository.findTopByPlayerNameOrderByIdDesc(name)
                .map(save -> {
                    player = new Player(save.getPlayerName());

                    // restaurar stats
                    player.setLevel(save.getLevel());
                    player.setHealth(save.getHealth());
                    player.setExperience(save.getExperience());

                    // restaurar location
                    currentLocation = new Location(
                            save.getLocationName(),
                            getDescriptionForLocation(save.getLocationName()),
                            getImageForLocation(save.getLocationName()));

                    currentDecisions = getDecisionsForLocation(currentLocation.getName());

                    return new GameState(
                            player,
                            currentLocation,
                            currentDecisions,
                            "Partida cargada ✅",
                            player.isDead());
                })
                .orElseGet(() -> new GameState(
                        player,
                        currentLocation,
                        currentDecisions,
                        "No hay partida guardada para " + name,
                        player.isDead()));
    }

    /**
     * Guardar partida (Hibernate)
     */
    public void saveGame() {
        SavedGame save = new SavedGame(
                player.getName(),
                player.getLevel(),
                player.getHealth(),
                player.getExperience(),
                currentLocation.getName());
        savedGameRepository.save(save);
    }

    /**
     * Estado actual (tu controller lo usa como GET /)
     */
    public GameState startGame() {
        return new GameState(
                player,
                currentLocation,
                currentDecisions,
                "El aire es denso... El Bosque de Lirien te observa en silencio.",
                player.isDead());
    }

    /**
     * Reset de partida (para botón RETRY / Restart)
     */
    public GameState resetGame() {
        player = new Player(player.getName());

        currentLocation = new Location(
                GameConstants.LOCATION_FOREST,
                GameConstants.FOREST_DESCRIPTION,
                getImageForLocation(GameConstants.LOCATION_FOREST));

        currentDecisions = getDecisionsForLocation(currentLocation.getName());
        return startGame();
    }

    public GameState takeDecision(DecisionRequest request) {
        String chosenText = request.text();

        Decision chosen = currentDecisions.stream()
                .filter(d -> d.getText().equals(chosenText))
                .findFirst()
                .orElse(null);

        if (chosen == null) {
            String message = GameConstants.DECISION_NOT_EXISTS + " Algo ha ido mal...";
            return new GameState(player, currentLocation, currentDecisions, message, player.isDead());
        }

        String originLocationName = currentLocation.getName();

        // 1) Efectos base
        applyHealthDelta(chosen.getHealthChange());
        player.gainExperience(chosen.getExperienceChange());

        // 2) Evento aleatorio
        EventOutcome outcome = rollRandomEvent(originLocationName, chosen.getText());
        if (outcome.extraHealth != 0)
            applyHealthDelta(outcome.extraHealth);
        if (outcome.extraExp != 0)
            player.gainExperience(outcome.extraExp);

        // GAME OVER CHECK (tras aplicar todo)
        if (player.isDead()) {
            return new GameState(
                    player,
                    currentLocation,
                    List.of(),
                    GameConstants.GAME_OVER + " Has caído en tu aventura. Pulsa RETRY para intentarlo de nuevo.",
                    true);
        }

        // 3) Mensaje base + evento
        String baseMessage = getMessageForDecision(chosen);
        String message = (outcome.message != null && !outcome.message.isBlank())
                ? baseMessage + " " + outcome.message
                : baseMessage;

        // 4) Cambiar localización si aplica
        if (!chosen.getNextLocation().equals(currentLocation.getName())) {
            currentLocation = new Location(
                    chosen.getNextLocation(),
                    getDescriptionForLocation(chosen.getNextLocation()),
                    getImageForLocation(chosen.getNextLocation()));
        }

        // 5) Nuevas decisiones
        currentDecisions = getDecisionsForLocation(currentLocation.getName());

        return new GameState(player, currentLocation, currentDecisions, message, player.isDead());
    }

    public GameState useItem(String itemId) {
        if (player.isDead()) {
            return new GameState(player, currentLocation, List.of(), GameConstants.DO_NOT_USE_ITEM, true);
        }

        if (itemId == null || itemId.isBlank()) {
            return new GameState(player, currentLocation, currentDecisions, GameConstants.NO_ITEM_SELECTED,
                    player.isDead());
        }

        switch (itemId) {
            case "herbs" -> {
                boolean removed = player.removeFromBackpack("herbs", 1);
                if (!removed) {
                    return new GameState(player, currentLocation, currentDecisions, GameConstants.NO_HERBS,
                            player.isDead());
                }

                applyHealthDelta(+20);

                return new GameState(player, currentLocation, currentDecisions, GameConstants.USING_HERBS,
                        player.isDead());
            }
            default -> {
                return new GameState(player, currentLocation, currentDecisions, GameConstants.NOT_USING_ITEM,
                        player.isDead());
            }
        }
    }

    private void applyHealthDelta(int delta) {
        // delta positivo = curar, delta negativo = daño
        player.takeDamage(-delta);
    }

    private List<Decision> getDecisionsForLocation(String locationName) {
        List<Decision> decisions = new ArrayList<>();

        switch (locationName) {
            case GameConstants.LOCATION_FOREST -> {
                decisions.add(new Decision(GameConstants.FOREST_EXPLORATION, GameConstants.LOCATION_FOREST, -10, 15));
                decisions.add(new Decision(GameConstants.FOREST_REST, GameConstants.LOCATION_FOREST, +10, 0));
                decisions.add(new Decision(GameConstants.FOREST_CONTINUE_ROAD, GameConstants.LOCATION_AETHAR, 0, 0));
                decisions.add(
                        new Decision(GameConstants.FOREST_TO_THE_RUINS, GameConstants.LOCATION_ANCIENT_RUINS, -5, 25));
            }
            case GameConstants.LOCATION_AETHAR -> {
                decisions.add(new Decision(GameConstants.CITY_TALK_MERCHANT, GameConstants.LOCATION_AETHAR, 0, 10));
                decisions.add(new Decision(GameConstants.CITY_TO_THE_TAVERN, GameConstants.LOCATION_AETHAR, -5, 20));
                decisions.add(new Decision(GameConstants.CITY_REST, GameConstants.LOCATION_AETHAR, +15, 0));
                decisions.add(new Decision(GameConstants.CITY_TO_THE_FOREST, GameConstants.LOCATION_FOREST, 0, 0));
            }
            case GameConstants.LOCATION_ANCIENT_RUINS -> {
                decisions.add(new Decision(GameConstants.RUINS_CHEST, GameConstants.LOCATION_ANCIENT_RUINS, -10, 40));
                decisions.add(
                        new Decision(GameConstants.RUINS_EXPLORE_WALLS, GameConstants.LOCATION_ANCIENT_RUINS, 0, 30));
                decisions
                        .add(new Decision(GameConstants.RUINS_HEAR_ECHO, GameConstants.LOCATION_ANCIENT_RUINS, -5, 15));
                decisions.add(new Decision(GameConstants.BACK_TO_THE_FOREST, GameConstants.LOCATION_FOREST, 0, 0));
            }
            default ->
                decisions.add(new Decision(GameConstants.BACK_TO_THE_FOREST, GameConstants.LOCATION_FOREST, 0, 0));
        }

        return decisions;
    }

    private String getDescriptionForLocation(String locationName) {
        return switch (locationName) {
            case GameConstants.LOCATION_FOREST -> GameConstants.FOREST_DESCRIPTION;
            case GameConstants.LOCATION_AETHAR -> GameConstants.CITY_DESCRIPTION;
            case GameConstants.LOCATION_ANCIENT_RUINS -> GameConstants.RUINS_DESCRIPTION;
            default -> "Un lugar desconocido que no debería existir.";
        };
    }

    /**
     * Mapeo estable: locationName -> nombre exacto de PNG en /static/images
     */
    private String getImageForLocation(String locationName) {
        return switch (locationName) {
            case GameConstants.LOCATION_FOREST -> "Lirien.png";
            case GameConstants.LOCATION_AETHAR -> "Ciudad_Aethar.png";
            case GameConstants.LOCATION_ANCIENT_RUINS -> "ruinas_antiguas.png";
            default -> "Lirien.png";
        };
    }

    private String getMessageForDecision(Decision chosen) {
        String decisionText = chosen.getText();

        if (decisionText.equals(GameConstants.FOREST_EXPLORATION))
            return GameConstants.INTO_THE_SHADOWS;
        if (decisionText.equals(GameConstants.FOREST_REST))
            return GameConstants.REGAIN_YOUR_STRENGTH;
        if (decisionText.equals(GameConstants.FOREST_CONTINUE_ROAD))
            return GameConstants.PATH_TO_AETHAR;
        if (decisionText.equals(GameConstants.FOREST_TO_THE_RUINS))
            return GameConstants.TRAIL_OF_ANCIENT_RUINS;

        if (decisionText.equals(GameConstants.CITY_TALK_MERCHANT))
            return GameConstants.MERCHANT_SECRETS;
        if (decisionText.equals(GameConstants.CITY_TO_THE_TAVERN))
            return GameConstants.TAVERN_RUMOR;
        if (decisionText.equals(GameConstants.CITY_REST))
            return GameConstants.SOFT_BED;
        if (decisionText.equals(GameConstants.CITY_TO_THE_FOREST))
            return GameConstants.CITY_BEHIND;

        if (decisionText.equals(GameConstants.RUINS_CHEST))
            return GameConstants.INSIDE_CHEST;
        if (decisionText.equals(GameConstants.RUINS_EXPLORE_WALLS))
            return GameConstants.SYMBOLS_IN_THE_WALL;
        if (decisionText.equals(GameConstants.RUINS_HEAR_ECHO))
            return GameConstants.ECHO_RESPONDS;
        if (decisionText.equals(GameConstants.BACK_TO_THE_FOREST))
            return GameConstants.TURN_ARROUND_FOREST;

        return "Algo sucede, pero no sabes explicarlo.";
    }

    private static class EventOutcome {
        int extraHealth;
        int extraExp;
        String message;

        EventOutcome(int extraHealth, int extraExp, String message) {
            this.extraHealth = extraHealth;
            this.extraExp = extraExp;
            this.message = message;
        }
    }

    private EventOutcome rollRandomEvent(String locationName, String decisionText) {
        int roll = ThreadLocalRandom.current().nextInt(100);

        if (locationName.equals(GameConstants.LOCATION_FOREST)
                && decisionText.equals(GameConstants.FOREST_EXPLORATION)) {
            if (roll < 40) {
                boolean added = player.addToBackpack("herbs", "Hierbas curativas", 1);
                String lootMsg = added
                        ? " Obtienes: Hierbas curativas (+1)."
                        : " Tu mochila está llena. Dejas las hierbas atrás.";
                return new EventOutcome(+10, 0, "Encuentras hierbas curativas. Te sientes mejor."
                        + lootMsg + " " + GameConstants.HEALTH_UP);

            } else if (roll < 80) {
                return new EventOutcome(-15, +10,
                        "Una manada de lobos te embosca. Logras ahuyentarlos, pero sales herido. "
                                + GameConstants.HEALTH_DOWN + " " + GameConstants.EXP_UP);
            } else {
                return new EventOutcome(0, +30,
                        "Descubres un símbolo antiguo grabado en piedra. Aprendes algo inquietante. "
                                + GameConstants.EXP_UP);
            }
        }

        if (locationName.equals(GameConstants.LOCATION_FOREST)
                && decisionText.equals(GameConstants.FOREST_TO_THE_RUINS)) {
            if (roll < 35) {
                return new EventOutcome(-10, 0,
                        "El suelo cede bajo tus pies. Caes sobre raíces afiladas, pero sigues adelante. "
                                + GameConstants.HEALTH_DOWN);
            } else if (roll < 75) {
                return new EventOutcome(0, 35,
                        "Una luz azul te guía entre la niebla. Llegas a las ruinas con una extraña certeza. "
                                + GameConstants.EXP_UP);
            } else {
                return new EventOutcome(+10, +25,
                        "Encuentras una fuente oculta. El agua sabe a metal... pero te fortalece. "
                                + GameConstants.HEALTH_UP + " " + GameConstants.EXP_UP);
            }
        }

        if (locationName.equals(GameConstants.LOCATION_ANCIENT_RUINS)
                && decisionText.equals(GameConstants.RUINS_CHEST)) {
            if (roll < 35) {
                return new EventOutcome(-20, 0,
                        "¡Trampa! Una aguja te pincha. Maldición... " + GameConstants.HEALTH_DOWN);

            } else if (roll < 75) {
                boolean added = player.addToBackpack("ancient_relic", "Reliquia antigua", 1);
                String lootMsg = added
                        ? " Obtienes: Reliquia antigua."
                        : " Tu mochila está llena. No puedes llevarte la reliquia.";
                return new EventOutcome(0, +50, "Dentro hay un artefacto antiguo. Tu mente se llena de conocimiento."
                        + lootMsg + " " + GameConstants.EXP_UP);

            } else {
                return new EventOutcome(-10, +60, "¡El cofre era un monstruo! Lo derrotas por poco. "
                        + GameConstants.HEALTH_DOWN + " " + GameConstants.EXP_UP);
            }
        }

        if (locationName.equals(GameConstants.LOCATION_ANCIENT_RUINS)
                && decisionText.equals(GameConstants.RUINS_EXPLORE_WALLS)) {
            if (roll < 40) {
                return new EventOutcome(-15, +20, "Las runas arden en tu mente. Aprendes... y pagas el precio. "
                        + GameConstants.HEALTH_DOWN + " " + GameConstants.EXP_UP);
            } else if (roll < 80) {
                return new EventOutcome(0, +45,
                        "Descifras un fragmento del idioma antiguo. Sientes que has subido un peldaño en el conocimiento. "
                                + GameConstants.EXP_UP);
            } else {
                return new EventOutcome(-5, +60,
                        "Una presencia te responde desde la piedra. Sales temblando... pero con un secreto grabado en el alma. "
                                + GameConstants.HEALTH_DOWN + " " + GameConstants.EXP_UP);
            }
        }

        if (locationName.equals(GameConstants.LOCATION_AETHAR)
                && decisionText.equals(GameConstants.CITY_TO_THE_TAVERN)) {
            if (roll < 40) {
                boolean added = player.addToBackpack("torn_map", "Mapa rasgado", 1);
                String lootMsg = added
                        ? " Obtienes: Mapa rasgado."
                        : " Tu mochila está llena. Memorizas lo que puedes y lo dejas.";
                return new EventOutcome(0, +25, "Escuchas un rumor útil sobre las ruinas. Te da ventaja."
                        + lootMsg + " " + GameConstants.EXP_UP);

            } else if (roll < 75) {
                return new EventOutcome(-10, 0,
                        "Un bruto te empuja en la multitud. Sales golpeado. " + GameConstants.HEALTH_DOWN);
            } else {
                return new EventOutcome(-15, +30, "Se monta una pelea. Aprendes a defenderte... aunque te duele todo. "
                        + GameConstants.HEALTH_DOWN + " " + GameConstants.EXP_UP);
            }
        }

        if (locationName.equals(GameConstants.LOCATION_AETHAR)
                && decisionText.equals(GameConstants.CITY_TALK_MERCHANT)) {
            if (roll < 35) {
                return new EventOutcome(-10, 0,
                        "Te la juega con un 'amuleto protector'. Te sientes estafado y un poco mareado. "
                                + GameConstants.HEALTH_DOWN);
            } else if (roll < 75) {
                return new EventOutcome(0, +30,
                        "Te da un mapa incompleto de las ruinas. No es gratis, pero es útil. " + GameConstants.EXP_UP);
            } else {
                return new EventOutcome(+10, +20,
                        "El mercader baja la voz: 'No deberías ir...'. Te entrega un talismán y te desea suerte. "
                                + GameConstants.HEALTH_UP + " " + GameConstants.EXP_UP);
            }
        }

        return new EventOutcome(0, 0, "");
    }
}

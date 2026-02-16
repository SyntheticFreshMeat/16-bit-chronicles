package com.juego16bits.juego16bits.config;

public final class GameConstants {
    
    private GameConstants() {
        // evitar instanciación!
    }

    // LOCATIONS
    public static final String LOCATION_FOREST = "Bosque de Lirien";
    public static final String LOCATION_AETHAR = "Ciudad de Aethar";
    public static final String LOCATION_ANCIENT_RUINS = "Ruinas antiguas";

    // DECISIONS

        // FOREST
    public static final String FOREST_EXPLORATION = "Explorar una zona oscura del bosque";
    public static final String FOREST_REST = "Descansar junto a un árbol antiguo";
    public static final String FOREST_CONTINUE_ROAD = "Seguir el camino hasta la ciudad";
    public static final String FOREST_TO_THE_RUINS = "Internarse hacia unas ruinas cubiertas de musgo";
    
        // AETHAR
    public static final String CITY_TALK_MERCHANT = "Hablar con un mercader sospechoso";
    public static final String CITY_TO_THE_TAVERN = "Entrar en una taberna ruidosa";
    public static final String CITY_REST = "Descansar en una posada";
    


        // RUINS
    public static final String RUINS_CHEST = "Abrir un cofre oxidado";
    public static final String RUINS_EXPLORE_WALLS = "Examinar inscripciones en la pared";
    public static final String RUINS_HEAR_ECHO = "Escuchar el eco en los pasillos derruidos";

        // GO TO
    public static final String BACK_TO_THE_FOREST = "Volver al bosque";
    public static final String CITY_TO_THE_FOREST = "Salir de la ciudad hacia el bosque";


    // DESCRIPTIONS
    public static final String FOREST_DESCRIPTION = "Un bosque antiguo donde los árboles susurran historias olvidadas";
    public static final String CITY_DESCRIPTION = "Una ciudad de piedra y humo, llena de rumores y miradas esquivas";
    public static final String RUINS_DESCRIPTION = "Paredes cubiertas de musgo, símbolos extraños... y una presencia que no ves";
    public static final String DECISION_NOT_EXISTS = "Esa decisión no existe.";
    

    // DECISIONTEXT
    public static final String INTO_THE_SHADOWS = "Te adentras entre sombras. Algo te roza... y sientes un escalofrío.";
    public static final String REGAIN_YOUR_STRENGTH = "Respiras hondo. La corteza vibra levemente. Recuperas fuerzas.";
    public static final String PATH_TO_AETHAR = "El sendero se abre. A lo lejos, se alzan las murallas de Aethar.";
    public static final String TRAIL_OF_ANCIENT_RUINS = "Sigues un rastro de piedras antiguas. Las ruinas te llaman por tu nombre.";
    public static final String MERCHANT_SECRETS = "El mercader sonríe demasiado. Te cuenta algo útil... pero cobra con secretos.";
    public static final String TAVERN_RUMOR = "Risas, vasos, música. Entre el ruido, escuchas un rumor interesante.";
    public static final String SOFT_BED = "Una cama blanda. Tus heridas cierran. Por un momento, todo está en calma.";
    public static final String CITY_BEHIND = "Dejas atrás la piedra y el humo. El bosque vuelve a tragarse el camino.";
    public static final String INSIDE_CHEST = "La cerradura cede con un crujido. Dentro hay algo valioso... y algo afilado.";
    public static final String SYMBOLS_IN_THE_WALL = "Los símbolos brillan un instante. Sientes que entiendes algo que no deberías.";
    public static final String ECHO_RESPONDS = "El eco responde tarde... como si alguien más estuviera respirando contigo.";
    public static final String TURN_ARROUND_FOREST = "Das media vuelta. La luz del bosque parece más segura que estas piedras.";


    // HEALTH EVENTS
        // FOREST
    public static final String HEALTH_UP = "Recuperas salud.";
    public static final String HEALTH_DOWN = "Pierdes salud.";
    public static final String EXP_UP = "Ganas experiencia.";



    // ITEMS
    public static final String ANCIENT_RELIC = "Relíquia antigua";
    public static final String MAP = "Mapa rasgado";
    public static final String HERBS = "Hierbas curativas";
    public static final String BAG = "Mochila";
    public static final String DO_NOT_USE_ITEM = "No puedes usar objetos. Estás fuera de combate. ";
    public static final String NO_ITEM_SELECTED = "No has seleccionado ningún objeto.";
    public static final String NO_HERBS = "No tienes hierbas curativas. ";
    public static final String USING_HERBS = "Usas hierbas curativas. Recuperas 20 puntos de vida. ";
    public static final String NOT_USING_ITEM = "Ese objeto no se puede usar. ";

    // MESSAGES
    public static final String GAME_OVER = "Game Over";
}
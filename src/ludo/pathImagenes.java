package ludo;

import java.util.HashMap;
import java.util.Map;

public class pathImagenes {

    public enum Color {amarillo, rojo, verde, azul}

    public enum Tema {plano, oscuro, pastel}

    public enum Tablero {tablero, tableroespecial}

    public static String CARPETA = "images";   //<-----------------imagen
    public static String DIR = "\\";
    public static String EXTENSION_FILE = ".png";
    public static String PATH_DADO = CARPETA + DIR + "dice\\reddice\\";


    private static Map<Tema, String> pathTema = new HashMap<>();
    private static Map<Color, String> tokenPath = new HashMap<>();
    private static Map<Tablero, String> pathTablero = new HashMap<>();

    public pathImagenes() {
        for (Tema t : Tema.values()) {
            pathTema.put(t, CARPETA + DIR + t.name() + DIR);
        }
    }

    public static void setPathFicha(Tema t) {
        for (Color c : Color.values()) {
            tokenPath.put(c, pathTema.get(t) + c.name() + EXTENSION_FILE);
        }
    }

    public static String getPathFicha(Color c) {
        return tokenPath.get(c);
    }

    public static void setPathTablero(Tema t) {
        for (Tablero b : Tablero.values()) {
            pathTablero.put(b, pathTema.get(t) + b.name() + EXTENSION_FILE);
        }
    }

    public static String getPathTablero(Tablero t) {
        return pathTablero.get(t);
    }
}

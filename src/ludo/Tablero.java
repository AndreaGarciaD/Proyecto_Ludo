package ludo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import ludo.pathImagenes.Tema;

public class Tablero {
    public static List<Integer> CASILLAS_GLOBO = Arrays.asList(8, 21, 34, 47);
    public static List<Integer> CASILLAS_ESTRELLA = Arrays.asList(5, 11, 18, 24, 31, 37, 44, 50);

    private boolean especial;
    private Map<Tema, BufferedImage> imgNormal = new HashMap<>();
    private Map<Tema, BufferedImage> imgEspecial = new HashMap<>();

    public Tablero(boolean especial) {
        this.especial = especial;
        for (Tema t : Tema.values()) {
            pathImagenes.setPathTablero(t);
            try {
                imgNormal.put(t, ImageIO.read(new File(pathImagenes.getPathTablero(pathImagenes.Tablero.tablero))));
                imgEspecial.put(t, ImageIO.read(new File(pathImagenes.getPathTablero(pathImagenes.Tablero.tableroespecial))));
            } catch (IOException ex) {
                System.out.println("Imagen de tablero no encontrada");
            }
        }
    }

    public BufferedImage getImg(Tema tema) {
        return imgNormal.get(tema);
    }

    public BufferedImage getImgEspecial(Tema tema) {
        return imgEspecial.get(tema);
    }

    public boolean getEspecial() {
        return this.especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public void setSpecial(String special) {
        this.especial = special.equalsIgnoreCase("Especial");
    }
}

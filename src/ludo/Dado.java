package ludo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;

import static javax.swing.JOptionPane.showInputDialog;
import static ludo.pathImagenes.PATH_DADO;
import static ludo.LudoGUI.TAMANO_CASILLA;

public class Dado {

    public static int TAMANO_DADO = (int) (1.5 * TAMANO_CASILLA);
    private final Random random = new Random();
    private Scanner scanner;
    private int resultado = 3;
    private int indexJugador; //jugador con el dado
    private boolean esSeis = false;
    private boolean esTres = false;
    private boolean debug = false;
    private int[] coordinates = new int[2];
    private int contadorTick;
    private int posicion;
    private int vel;

    private BufferedImage dado[] = new BufferedImage[6];
    private BufferedImage animacionDado[] = new BufferedImage[25];
    private BufferedImage imagenDado;

    public Dado() {
        try {
            for (int i = 0; i < dado.length; i++)
                dado[i] = ImageIO.read(new File(PATH_DADO + "result" + (i + 1) + ".png"));
            for (int i = 0; i < animacionDado.length; i++)
                animacionDado[i] = ImageIO.read(new File(PATH_DADO + "animateddice" + (i + 1) + ".png"));
        } catch (IOException ex) {
            System.out.println("Imagen de dado no encontrada");
        }
        imagenDado = dado[resultado - 1];
        this.coordinates[0] = (TAMANO_CASILLA * 15 - TAMANO_DADO) / 2;
        this.coordinates[1] = (TAMANO_CASILLA * 15 - TAMANO_DADO) / 2;
    }
    public void tirarDado(int indexJugador) {
        this.indexJugador = indexJugador;
        this.resultado = this.tirar();
        this.esSeis = this.resultado == 1;
        this.esTres = this.resultado == 3;

        resetCoordenadas();
        this.contadorTick = 0;
        this.vel = 1;
    }


    public int tirar() {
        if (!debug)
            this.resultado = random.nextInt(6) + 1;
        else {
            scanner = new Scanner(showInputDialog("Ingresa el valor del dado (1-6):"));
            try {
                int res = scanner.nextInt() % 7;
                this.resultado = res != 0 ? res : 6;
            } catch (NoSuchElementException ne) {
                this.resultado = 6;
            }
        }
        return this.resultado;
    }

    public void resetCoordenadas() {
        posicion = 0;
        if (this.indexJugador < 2) {
            this.coordinates[0] = TAMANO_CASILLA * 15 - TAMANO_DADO;
        } else {
            this.coordinates[0] = 0;
        }
        if (this.indexJugador % 3 == 0) {
            this.coordinates[1] = 0;
        } else {
            this.coordinates[1] = TAMANO_CASILLA * 15 - TAMANO_DADO;
        }
    }
    public void setCoordenadas(int pos) {
        if (this.indexJugador < 2) {
            this.coordinates[0] = TAMANO_CASILLA * 15 - TAMANO_DADO - pos;
        } else {
            this.coordinates[0] = pos;
        }
        if (this.indexJugador % 3 == 0) {
            this.coordinates[1] = pos;
        } else {
            this.coordinates[1] = TAMANO_CASILLA * 15 - TAMANO_DADO - pos;
        }
    }

    public void animarDado() {
        posicion += vel * contadorTick;
        if (posicion < (TAMANO_CASILLA * 15 - TAMANO_DADO) / 2) {
            if (indexJugador % 3 == 0)
                imagenDado = animacionDado[contadorTick % animacionDado.length];
            else
                imagenDado = animacionDado[animacionDado.length - 1 - (contadorTick % animacionDado.length)];
            contadorTick++;
        } else {
            imagenDado = dado[resultado - 1];
            posicion = (TAMANO_CASILLA * 15 - TAMANO_DADO) / 2;
        }
        setCoordenadas(posicion);
    }

    public boolean getIsSix() {
        return this.esSeis;
    }

    public boolean getTres(){
        return this.esTres;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getCoordenadas(int i) {
        return this.coordinates[i];
    }

    public BufferedImage getImagenDado() {
        return this.imagenDado;
    }
    public int getResultado() {
        return resultado;
    }
}


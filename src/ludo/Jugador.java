package ludo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import static ludo.Tablero.CASILLAS_GLOBO;
import static ludo.Tablero.CASILLAS_ESTRELLA;
import ludo.pathImagenes.Color;
import ludo.pathImagenes.Tema;

import static ludo.LudoGUI.IGNORAR;
import static ludo.LudoGUI.TAMANO_CASILLA;

public class Jugador {

    public static final int FUERA_DE_TABLERO = 60; //posicion de ficha fuera del tablero
    public static int DISTANCIA_DE_INICIO = 13;
    public static int META = 5;

    private int meta;  //cuantas fichas han alcanzado la meta
    private int indexJugadores;  //index del jugador cuando el array de jugadores es creado
    private ArrayList<Integer> fichasEnTablero; //posicion actual de cada ficha en el tablero
    private Color color;
    private Ficha[] fichas; //guarda la posicion de cada ficha en el tablero
    private boolean activo;
    private boolean turn;
    private Map<pathImagenes.Tema, BufferedImage> imagenFicha = new HashMap<>();

    public Jugador(Color color) {
        valoresIniciales();
        this.color = color;
        for (pathImagenes.Tema t : Tema.values()) {
            pathImagenes.setPathFicha(t);
            try {
                imagenFicha.put(t, ImageIO.read(new File(pathImagenes.getPathFicha(color))));
            } catch (IOException ex) {
                System.out.println("No se encontro imagen de ficha");
            }
        }
    }

    private void valoresIniciales() {
        this.meta = 0;
        this.turn = false;
        this.activo = false;
        this.fichasEnTablero = new ArrayList<>();
        this.fichas = new Ficha[4];
        for (int i = 0; i < 4; i++) {
            this.fichas[i] = new Ficha(i);
        }
        this.indexJugadores = 0;
    }

    public void reset() {
        this.fichasEnTablero.clear();
        for (int i = 0; i < 4; i++) {
            this.fichas[i] = new Ficha(i);
            this.setPosicionXY(i);
        }
    }
    private void checkMeta(int indexFicha) {
        if (this.getFicha(indexFicha).getEnRectaFinal() && this.getFicha(indexFicha).getPosicion() == META) {
            this.meta++;
            this.getFichasEnTablero().remove(this.fichasEnTablero.indexOf(indexFicha));
            this.fichas[indexFicha].setFueraDeCasa(false);
        }
    }

    public void posicionInicial(int indexFicha) {
        this.fichas[indexFicha].setPosicion(this.indexJugadores * DISTANCIA_DE_INICIO);
        this.fichas[indexFicha].setFueraDeCasa(true);
        this.fichasEnTablero.add(indexFicha);
    }

    public void fueraDelTablero(int indexFicha) {
        if (!this.fichas[indexFicha].getEnRectaFinal()) {
            this.fichas[indexFicha].enviarACasa();
            this.fichasEnTablero.remove(this.fichasEnTablero.indexOf(indexFicha));
            this.setPosicionXY(indexFicha);
        }
    }

    private void iniciarRectaFinal(int pos, int indexFicha) {
        this.fichas[indexFicha].setPosicion(pos % 51);
        this.fichas[indexFicha].setEnRectaFinal(true);
        this.setPosicionXY(indexFicha);
        this.checkMeta(indexFicha);
    }

    public void checkCasillaEspecial(int indexFicha) {
        this.fichas[indexFicha].setEstaSeguro(false);
        if (!this.fichas[indexFicha].getEnRectaFinal() && this.fichas[indexFicha].getPosicion() != FUERA_DE_TABLERO) {
            int tokenPosition = this.fichas[indexFicha].getPosicion();
            if (CASILLAS_ESTRELLA.contains(tokenPosition)) {
                do {
                    this.fichas[indexFicha].moverFicha(1);
                }
                while (!CASILLAS_ESTRELLA.contains(this.fichas[indexFicha].getPosicion()));
            } else {
                if (CASILLAS_GLOBO.contains(tokenPosition))
                    this.turn = true;
                else {
                    if (tokenPosition == DISTANCIA_DE_INICIO * this.indexJugadores)
                        this.fichas[indexFicha].setEstaSeguro(true);
                }
            }
            this.setPosicionXY(indexFicha);
        }
    }

    public void setPosicionXY(int indexFicha) {
        Ficha ficha = this.getFicha(indexFicha);
        switch (ficha.getPosicion()) {
            case FUERA_DE_TABLERO: {
                ficha.setCoordenadasX(coordenadasFueraDeTablero(this.indexJugadores, indexFicha)[0]);
                ficha.setCoordenadasY(coordenadasFueraDeTablero(this.indexJugadores, indexFicha)[1]);
                break;
            }
            default: {
                if (!ficha.getEnRectaFinal()) {
                    ficha.setCoordenadasX(coordenadasDeTablero(ficha.getPosicion()));
                    ficha.setCoordenadasY(coordenadasDeTablero((ficha.getPosicion() + 39) % 52));
                } else {
                    ficha.setCoordenadasX(coordenadasRectaFinal(ficha.getPosicion(), this.indexJugadores, indexFicha)[0]);
                    ficha.setCoordenadasY(coordenadasRectaFinal(ficha.getPosicion(), this.indexJugadores, indexFicha)[1]);
                }
                break;
            }
        }
    }

    private int[] getPosicionXY(Ficha ficha) {
        int[] coordenadas = new int[2];
        switch (ficha.getPosicion()) {
            case FUERA_DE_TABLERO: {
                coordenadas = coordenadasFueraDeTablero(this.indexJugadores, ficha.getIndex());
                break;
            }
            default: {
                if (!ficha.getEnRectaFinal()) {
                    coordenadas[0] = coordenadasDeTablero(ficha.getPosicion());
                    coordenadas[1] = coordenadasDeTablero((ficha.getPosicion() + 39) % 52);
                } else {
                    coordenadas = coordenadasRectaFinal(ficha.getPosicion(), this.indexJugadores, ficha.getPosicion());
                }
                break;
            }
        }
        return coordenadas;
    }

    private static int[] coordenadasFueraDeTablero(int indexJugador, int indexTablero) {
        int coordenadas[] = new int[2];
        coordenadas[0] = TAMANO_CASILLA / 2 * (21 + 4 * (indexTablero / 2) - 18 * (indexJugador / 2));
        coordenadas[1] = TAMANO_CASILLA / 2 * (3 + 4 * (indexTablero % 2) + 18 * ((indexJugador % 3) > 0 ? 1 : 0));
        return coordenadas;
    }

    private static int coordenadasDeTablero(int pos) {
        int coordenada;
        int a = pos % 26;
        int b = 7 - Math.abs(a - 11);
        int c = pos > 24 & pos != 51 ? 2 : 0;
        coordenada = (pos != 24 & pos != 50) ? c + (((a > 4) & (a < 18)) ? (b - (b / 7)) : 0) : 1;
        coordenada = pos > 23 ? TAMANO_CASILLA * (8 - coordenada) : TAMANO_CASILLA * (8 + coordenada);
        return coordenada;
    }

    private static int[] coordenadasRectaFinal(int pos, int indexJugador, int indexFicha) {
        int coordenadas[] = new int[2];
        if (pos != META) {
            coordenadas[indexJugador % 2] = TAMANO_CASILLA * 7;
            if (indexJugador % 3 == 0) {
                coordenadas[(indexJugador + 1) % 2] = TAMANO_CASILLA * (pos + 1);
            } else {
                coordenadas[(indexJugador + 1) % 2] = TAMANO_CASILLA * (13 - pos);
            }
        } else {
            if (indexJugador % 2 == 0) {
                coordenadas[0] = TAMANO_CASILLA * 6 + TAMANO_CASILLA / 2 * (1 + indexFicha);
                coordenadas[1] = TAMANO_CASILLA * 6 + TAMANO_CASILLA * 2 * (indexJugador / 2);
            } else {
                coordenadas[0] = TAMANO_CASILLA * 6 + TAMANO_CASILLA * 2 * (1 - (indexJugador / 2));
                coordenadas[1] = TAMANO_CASILLA * 6 + TAMANO_CASILLA / 2 * (1 + indexFicha);
            }
        }
        return coordenadas;
    }

    public int getFichaPorCoordenadas(int[] clickXY) {
        int index = IGNORAR;
        int coordenadaX;
        int coordenadaY;
        for (Ficha ficha : this.fichas) {
            coordenadaX = getPosicionXY(ficha)[0];
            coordenadaY = getPosicionXY(ficha)[1];
            if (clickXY[0] - coordenadaX >= 0 && clickXY[0] - coordenadaX <= TAMANO_CASILLA) {
                if (clickXY[1] - coordenadaY >= 0 && clickXY[1] - coordenadaY <= TAMANO_CASILLA) {
                    System.out.println("coordX: " + coordenadaX);
                    System.out.println("coordY: " + coordenadaY);
                    index = ficha.getIndex();
                    return index;
                }
            }
        }
        return index;
    }

    public void moverFichaSeleccionada(int indexFicha, int resultadoDado) {
        int posicionFicha = this.fichas[indexFicha].getPosicion();
        switch (posicionFicha) {
            case FUERA_DE_TABLERO: {
                this.posicionInicial(indexFicha);
                this.setTurno(false);
                break;
            }
            default: {
                if ((52 + posicionFicha - this.getIndexJugadores() * 13) % 52 + resultadoDado >= 51) {
                    this.iniciarRectaFinal((52 + posicionFicha - this.getIndexJugadores() * 13) % 52 + resultadoDado, indexFicha);
                } else {
                    this.getFicha(indexFicha).moverFicha(resultadoDado);
                    this.checkMeta(indexFicha);
                }
            }
        }
        this.setPosicionXY(indexFicha);
    }

    public BufferedImage getImagenFicha(pathImagenes.Tema tema) {
        return this.imagenFicha.get(tema);
    }

    public int getMeta() {
        return this.meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public int getIndexJugadores() {
        return this.indexJugadores;
    }

    public void setjugadorIndex(int index) {
        this.indexJugadores = index;
    }

    public boolean getTurno() {
        return this.turn;
    }

    public void setTurno(boolean turno) {
        this.turn = turno;
    }

    public boolean getActivo() {
        return this.activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public ArrayList<Integer> getFichasEnTablero() {
        return this.fichasEnTablero;
    }

    public void setFichasEnTablero(ArrayList<Integer> fichasEnTablero) {
        this.fichasEnTablero = fichasEnTablero;
    }

    public Ficha getFicha(int i) {
        return this.fichas[i];
    }

    public Ficha[] getFichas() {
        return this.fichas;
    }

    public String getColor() {
        return this.color.name();
    }

}

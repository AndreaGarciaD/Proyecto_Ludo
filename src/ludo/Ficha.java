
package ludo;

import static ludo.LudoGUI.TAMANO_CASILLA;
import static ludo.Jugador.META;
import static ludo.Jugador.FUERA_DE_TABLERO;

public class Ficha {

    private int index;
    private int posicion;
    private int coordenadasX;
    private int coordenadasY;
    private boolean fueraDeCasa;
    private boolean enRectaFinal;
    private boolean estaSeguro;

    public Ficha(int i) {
        this.index = i;
        this.posicion = FUERA_DE_TABLERO;
        this.fueraDeCasa = false;
        this.enRectaFinal = false;
        this.coordenadasX = TAMANO_CASILLA * 15;
        this.coordenadasY = TAMANO_CASILLA * 15;
    }

    public int getIndex() {
        return this.index;
    }

    public int getPosicion() {
        return this.posicion;
    }

    public void setPosicion(int pos) {
        this.posicion = pos;
    }

    public int getCoordenadasX() {
        return this.coordenadasX;
    }

    public int getCoordenadasY() {
        return this.coordenadasY;
    }

    public void setCoordenadasX(int x) {
        this.coordenadasX = x;
    }

    public void setCoordenadasY(int y) {
        this.coordenadasY = y;
    }

    public boolean getEstaSeguro() {
        return this.estaSeguro;
    }

    public void setEstaSeguro(boolean estaSeguro) {
        this.estaSeguro = estaSeguro;
    }

    public boolean getFueraDeCasa() {
        return this.fueraDeCasa;
    }

    public void setFueraDeCasa(boolean fueraDeCasa) {
        this.fueraDeCasa = fueraDeCasa;
    }

    public boolean getEnRectaFinal() {
        return this.enRectaFinal;
    }

    public void setEnRectaFinal(boolean ft) {
        this.enRectaFinal = ft;
    }

    public void enviarACasa() {
        this.posicion = FUERA_DE_TABLERO;
        this.fueraDeCasa = false;
        this.enRectaFinal = false;
    }

    public void moverFicha(int resultadoDado) {
        if (!this.enRectaFinal) {
            this.posicion = (this.posicion + resultadoDado) % 52;
        } else {
            int x = this.posicion + resultadoDado;
            this.posicion = x > META ? META - (x - META) : x;
            //this.position = Math.abs((int) ((x % 10) / 5) * 5 - x % 5);
        }
    }
}
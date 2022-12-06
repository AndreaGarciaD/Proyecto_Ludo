package ludo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ludo.pathImagenes.*;

import static ludo.Jugador.META;
import static ludo.Jugador.FUERA_DE_TABLERO;

public class EstadoJuego {
    public static Tema TEMA_DEFAULT = Tema.plano;
    private Tablero tablero;
    private Dado dado;
    private Jugador[] jugadores;
    private Tema tema;
    private int jugadorActual;
    private int turno; //conteo de turnos desde el inicio de la partida
    private boolean tirandoDado;
    private boolean jugando;
    private boolean debug;
    private ArrayList<Integer> jugadoresX;
    private ArrayList<Integer> fichasX;
    private ArrayList<Integer> ganadores; //indexes de jugadores activos
    private String resultadosDeJuego;

    public EstadoJuego(Tema tema, List<pathImagenes.Color> coloresJugadores, boolean tableroEspecial) {
        this.resultadosDeJuego = "";
        valoresIniciales();
        this.tema = tema;
        this.tablero = new Tablero(tableroEspecial);
        crearSetJugadores(coloresJugadores);
    }

    private void valoresIniciales() {
        this.dado = new Dado();
        this.jugando = true;
        this.jugadorActual = 0;
        this.turno = 0;
        this.jugadoresX = new ArrayList<>();
        this.jugadores = new Jugador[4];
        this.tirandoDado = true;
        this.fichasX = new ArrayList<>();
        this.ganadores = new ArrayList<>();
        this.resultadosDeJuego = "";
        this.debug = false;
    }

    public Jugador getJugador(String color) {
        for (Jugador jugador : this.jugadores)
            if (jugador.getColor().equalsIgnoreCase(color))
                return jugador;
        return null;
    }

    public void addJugadoresX(int indexJugador) {
        this.jugadoresX.add(indexJugador);
        Collections.sort(this.jugadoresX);
    }

    public void desactivarJugadoresX(int indexJugador) {
        this.jugadoresX.remove(this.jugadoresX.indexOf(indexJugador));
    }


    public void setTema(String theme) {
        for (Tema t : Tema.values())
            if (theme.equalsIgnoreCase(t.name()))
                this.tema = t;
    }

    private void crearSetJugadores(List<pathImagenes.Color> colores) {
        int i = 0;
        for (pathImagenes.Color c : pathImagenes.Color.values()) {
            this.jugadores[i] = new Jugador(c);
            this.jugadores[i].setjugadorIndex(i);
            for (int j = 0; j < 4; j++) {
                this.jugadores[i].setPosicionXY(j);
            }
            if (colores.contains(c)) {
                this.jugadores[i].setActivo(true);
                this.jugadoresX.add(i);
            }
            i++;
        }
    }

    public void reiniciar() {
        for (Jugador j : jugadores)
            if (j.getActivo())
                j.reset();
        turno = 0;
        jugadorActual = 0;
        tirandoDado = true;
    }

    public void addJugador(String color) {
        if (!this.getJugador(color).getActivo()) {
            this.getJugador(color).reset();
            this.getJugador(color).setActivo(true);
            addJugadoresX(this.getJugador(color).getIndexJugadores());
            turno = this.jugadoresX.indexOf(jugadorActual);
        }
    }

    public void removerJugador(String color) {
        this.getJugador(color).setActivo(false);
        desactivarJugadoresX(this.getJugador(color).getIndexJugadores());
        turno = this.jugadoresX.indexOf(jugadorActual);
    }

    private void checkOtrasFichas(int indexJugador, int indexFicha) {
        int posicionFicha = this.jugadores[indexJugador].getFicha(indexFicha).getPosicion();
        for (int i = 0; i < this.jugadoresX.size(); i++) {
            if (!this.jugadores[indexJugador].getColor().equals(jugadores[jugadoresX.get(i)].getColor())) {
                for (int j = 0; j < 4; j++) {
                    if (jugadores[jugadoresX.get(i)].getFicha(j).getPosicion() == posicionFicha && jugadores[jugadoresX.get(i)].getFicha(j).getPosicion() != FUERA_DE_TABLERO && !jugadores[jugadoresX.get(i)].getFicha(j).getEstaSeguro()) {
                        jugadores[jugadoresX.get(i)].fueraDelTablero(j);
                    }
                }
            }
        }
    }

    public void buscarFichasDisponibles() {
        this.jugadorActual = this.jugadoresX.get(this.turno % this.jugadoresX.size());
        this.dado.tirarDado(this.jugadorActual);
        this.fichasX.clear();

        if (this.dado.getIsSix() || this.dado.getTres()) {
            this.jugadores[jugadorActual].setTurno(true);// bandera para tirar el dado otra vez si la ficha es movida
            for (Ficha ficha : this.jugadores[jugadorActual].getFichas()) {
                if (!(ficha.getEnRectaFinal() && ficha.getPosicion() == META)) {
                    this.fichasX.add(ficha.getIndex());
                }
            }
        } else {
            this.jugadores[jugadorActual].setTurno(false);
            for (int index : this.jugadores[jugadorActual].getFichasEnTablero()) {
                this.fichasX.add(index);
            }
        }
    }

    public void checkMovimientoOPasar() {
        if (this.fichasX.size() > 0) {
            this.tirandoDado = false;
        } else {  //si no hay fichas disponibles para mover se pasa el turno
            this.turno++;
        }
        this.jugadorActual = this.jugadoresX.get(this.turno % this.jugadoresX.size());
    }

    public void seleccionarYMover(int indexFicha) {
        Ficha thisFicha = this.jugadores[jugadorActual].getFichas()[indexFicha];
        if (!(thisFicha.getEnRectaFinal() && !thisFicha.getFueraDeCasa())) {
            this.jugadores[jugadorActual].moverFichaSeleccionada(indexFicha, this.dado.getResultado());
            if (!thisFicha.getEnRectaFinal()) {
                this.checkOtrasFichas(this.jugadores[jugadorActual].getIndexJugadores(), indexFicha);
                if (this.tablero.getEspecial()) {
                    this.jugadores[jugadorActual].checkCasillaEspecial(indexFicha);
                }
            }
            if (this.jugadores[jugadorActual].getMeta() == 4) {
                this.addGanadores(this.jugadores[jugadorActual].getIndexJugadores());
                this.desactivarJugadoresX(this.jugadores[jugadorActual].getIndexJugadores());
                if (this.getJugadoresX().isEmpty()) {
                    this.jugando = false;
                    this.resultadosDeJuego = "\nResultados:\n\n";
                    for (int i = 0; i < this.getGanadores().size(); i++) {
                        this.resultadosDeJuego += (i + 1) + " lugar - " + this.getJugadores()[this.getGanadores().get(i)].getColor() + " jugador\n";
                    }
                }
            }
            if (!this.jugadores[jugadorActual].getTurno()) {
                this.turno++;
            }
            this.tirandoDado = true;
            if (jugando)
                this.jugadorActual = this.jugadoresX.get(this.turno % this.jugadoresX.size());
        }
    }


    public boolean getTirandoDado() {
        return this.tirandoDado;
    }

    public boolean getJugando() {
        return this.jugando;
    }

    public void setJugando(boolean jugando) {
        this.jugando = jugando;
    }

    public int getJugadorActual() {
        return this.jugadorActual;
    }

    public String getResultadosDeJuego() {
        return this.resultadosDeJuego;
    }

    public Tablero getTablero() {
        return this.tablero;
    }

    public Dado getDado() {
        return this.dado;
    }
    public Jugador[] getJugadores() {
        return this.jugadores;
    }

    public Jugador getJugador(int i) {
        return this.jugadores[i];
    }

    public ArrayList<Integer> getGanadores() {
        return this.ganadores;
    }

    public void addGanadores(int playerIndex) {
        this.ganadores.add(playerIndex);
    }

    public Tema getTema() {
        return this.tema;
    }

    public ArrayList<Integer> getJugadoresX() {
        return this.jugadoresX;
    }

}

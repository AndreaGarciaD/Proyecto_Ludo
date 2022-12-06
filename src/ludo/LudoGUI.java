package ludo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import static javax.swing.JOptionPane.showMessageDialog;
import static ludo.Dado.TAMANO_DADO;
import static ludo.pathImagenes.*;

public class LudoGUI extends JPanel implements ActionListener, MouseListener, KeyListener {
    public static int IGNORAR = 10;
    public static int TAMANO_CASILLA = 40;

    private List<String> mJuego = Arrays.asList("Reiniciar", "Exit");
    private List<String> mJugadores = Arrays.asList("Amarillo", "Rojo", "Verde", "Azul");
    private List<String> mTema = Arrays.asList("Plano", "Oscuro", "Pastel");
    private List<String> mTablero = Arrays.asList("Normal", "Especial");
    private List<String> mConfiguracionJugadores = Arrays.asList("Añadir", "Off");

    BufferedImage highlighter;
    Timer animacion = new Timer(40, this);

    JMenuBar menuBar;
    EstadoJuego thisJuego;
    private JDialog frame; // <---------

    int tamanoCasilla = TAMANO_CASILLA;
    int tamanoFrame = tamanoCasilla * 15;
    int diceSize = (int) (tamanoCasilla * 1.5);

    public LudoGUI(EstadoJuego game) {
        thisJuego = game;
        addMenu();
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(tamanoFrame, tamanoFrame));
        try {
            highlighter = ImageIO.read(new File(CARPETA + DIR + "highlight.png"));
        } catch (IOException e) {
        }
    }

    public JMenuBar getMenu() {
        return this.menuBar;
    }

    private void render(Graphics2D g2) {
        g2.drawImage(thisJuego.getTablero().getImg(thisJuego.getTema()), 0, 0, tamanoFrame, tamanoFrame, null);
        if (thisJuego.getTablero().getEspecial()) {
            g2.drawImage(thisJuego.getTablero().getImgEspecial(thisJuego.getTema()), 0, 0, tamanoFrame, tamanoFrame, null);
        }
        Jugador[] jugadores = thisJuego.getJugadores();
        for (Jugador jugador : jugadores) {
            if (jugador.getActivo()) {
                for (Ficha ficha : jugador.getFichas()) {
                    g2.drawImage(jugador.getImagenFicha(thisJuego.getTema()), ficha.getCoordenadasX(), ficha.getCoordenadasY(), tamanoCasilla, tamanoCasilla, null);
                    if (!thisJuego.getTirandoDado()) {
                        if (!animacion.isRunning()) {
                            if (thisJuego.getJugadorActual() == jugador.getIndexJugadores()) {
                                if (thisJuego.getDado().getIsSix() || thisJuego.getDado().getTres() & !ficha.getEnRectaFinal()) {   //////
                                    g2.drawImage(highlighter, ficha.getCoordenadasX(), ficha.getCoordenadasY(), tamanoCasilla, tamanoCasilla, null);
                                } else {
                                    if (jugador.getFichasEnTablero().contains(ficha.getIndex())) {
                                        g2.drawImage(highlighter, ficha.getCoordenadasX(), ficha.getCoordenadasY(), tamanoCasilla, tamanoCasilla, null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        g2.drawImage(thisJuego.getDado().getImagenDado(), thisJuego.getDado().getCoordenadas(0), thisJuego.getDado().getCoordenadas(1), TAMANO_DADO, TAMANO_DADO, null);
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        render(g2);
    }

    private void addMenu() {
        JMenuBar menu = new JMenuBar();
        menu.setPreferredSize(new Dimension(15 * TAMANO_CASILLA, 25));
        JMenu gameMenu = crearSubMenu("Juego", mJuego, true);
        JMenu settingsMenu = new JMenu("Configuraciones");
        JMenu playerMenu = new JMenu("Jugadores");
        JMenu themeMenu = crearSubMenu("Tema", mTema, true);
        JMenu boardMenu = crearSubMenu("Tablero", mTablero, true);
        for (String player : mJugadores) {
            playerMenu.add(crearSubMenu(player, mConfiguracionJugadores, false));
        }
        menu.add(gameMenu);
        menu.add(settingsMenu);
        settingsMenu.add(playerMenu);
        settingsMenu.add(themeMenu);
        settingsMenu.add(boardMenu);
        this.menuBar = menu;
    }

    private JMenu crearSubMenu(String label, List<String> listaMenu, boolean nombreItem) {
        JMenu menu = new JMenu(label);
        for (String opcionn : listaMenu) {
            JMenuItem item = new JMenuItem(opcionn);
            item.setName(nombreItem ? opcionn : label);
            item.addActionListener(this);
            menu.add(item);
        }
        return menu;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!thisJuego.getJugando()) {
            showMessageDialog(frame, thisJuego.getResultadosDeJuego() + "\n", "Game Over", JOptionPane.PLAIN_MESSAGE);
            cerrarGUI();
        } else {
            if (thisJuego.getTirandoDado()) {
                thisJuego.buscarFichasDisponibles();
                animacion.start();
            } else {
                int[] clickXY = new int[2];
                clickXY[0] = e.getX();
                clickXY[1] = e.getY();
                int fichaSeleccionada = thisJuego.getJugador(thisJuego.getJugadorActual()).getFichaPorCoordenadas(clickXY);
                if (thisJuego.getDado().getIsSix() || thisJuego.getDado().getTres() && fichaSeleccionada != IGNORAR) {
                    thisJuego.seleccionarYMover(fichaSeleccionada);
                } else {
                    if (thisJuego.getJugador(thisJuego.getJugadorActual()).getFichasEnTablero().contains(fichaSeleccionada)) {
                        thisJuego.seleccionarYMover(fichaSeleccionada);
                    }
                }
            }
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.isControlDown()) {
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_D: {
                    boolean debugMode = thisJuego.getDado().getDebug();
                    System.out.printf("Modo debug %s\n", debugMode ? "off" : "on");
                    thisJuego.getDado().setDebug(!debugMode);
                    break;
                }
            }
            repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object event = ae.getSource();
        switch (event.getClass().getSimpleName()) {
            case ("Timer"): {
                playAnimacion();
                bloquearJugadorActual();
                break;
            }
            case ("JMenuItem"): {
                menuEvent((JMenuItem) event);
                break;
            }
        }
        repaint();
    }

    public void playAnimacion() {
        thisJuego.getDado().animarDado();
        if (thisJuego.getDado().getCoordenadas(0) == (TAMANO_CASILLA * 15 - TAMANO_DADO) / 2) {
            animacion.stop();
            thisJuego.checkMovimientoOPasar();
        }
    }
    private void bloquearJugadorActual() {
        JMenu menu = (JMenu) menuBar.getMenu(1).getPopupMenu().getComponent(0);
        for (int i = 0; i < 4; i++) {
            if (thisJuego.getJugador(thisJuego.getJugadorActual()).getColor().equalsIgnoreCase(menu.getItem(i).getText())) {
                menu.getItem(i).setEnabled(false);
            } else {
                menu.getItem(i).setEnabled(true);
            }
        }
    }
    public void menuEvent(JMenuItem item) {
        if (mJuego.contains(item.getName()))
            switch (item.getName()) {
                case "Reiniciar": {
                    reiniciartGUI();
                    break;
                }
                case "Exit": {
                    cerrarGUI();
                    break;
                }
            }
        else {
            if (mTema.contains(item.getText())) {
                thisJuego.setTema(item.getText());
            } else {
                if (mTablero.contains(item.getText())) {
                    thisJuego.getTablero().setSpecial(item.getText());
                    System.out.println("Item cambiado a: " + item.getText());
                } else {
                    if (mConfiguracionJugadores.contains(item.getText())) {
                        switch (item.getText()) {
                            case "Añadir": {
                                thisJuego.addJugador(item.getName());
                                break;
                            }
                            case "Off": {
                                thisJuego.removerJugador(item.getName());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void reiniciartGUI() {
        animacion.stop();
        thisJuego.reiniciar();
    }

    public void cerrarGUI() {
        animacion.stop();
        //setFocusable(false);
        try {
            frame.dispose();
        } catch (NullPointerException e) {
        }
    }

    public void dibujarGUI(EstadoJuego game) {
        frame = new JDialog();
        frame.setTitle("LUDO");
        frame.setModal(true);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        game.setJugando(true);
        LudoGUI ludoPanel = new LudoGUI(game);
        frame.setJMenuBar(ludoPanel.getMenu());
        frame.add(ludoPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}

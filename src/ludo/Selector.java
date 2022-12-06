package ludo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import static ludo.EstadoJuego.TEMA_DEFAULT;
import static ludo.Ventana.jugadorID;
import ludo.pathImagenes.Color;
import ludo.pathImagenes.Tema;

public class Selector extends JDialog implements MouseListener {
    private String PATH_IMAGEN = "images\\";
    private String PATH_DIR = "\\";
    private String IMAGEN_DEFAULT = "azul";
    private String IMAGEN_NO_SELECCIONADA = "gray";
    private String FORMATO_IMAGEN = ".png";
    private String FUENTE_ETIQUETA = "Serif";
    private int TAMANO_FUENTE = 14;

    private int ANCHO_BOTON = 40;
    private int ALTO_BOTON = 30;

    private List<String> OPCIONES_JUGADOR = Arrays.asList("Amarillo", "Rojo", "Verde", "Azul");
    private List<String> OPCIONES_TEMA = Arrays.asList("Plano", "Oscuro", "Pastel");
    private List<String> OPCIONES_TABLERO = Arrays.asList("Regular", "Especial");
    private List<String> OPCIONES_DADO = Arrays.asList("Regular", "Especial");
    private Map<String, List<String>> listaOpciones = new HashMap<>();

    private JPanel ventanaSeleccion;
    private JPanel opciones;
    private JLabel texto;
    private JButton ok;
    private BufferedImage seleccionado;
    private BufferedImage noSeleccionado;
    private ArrayList<JRadioButton> botones = new ArrayList<>();
    private ArrayList<String> opcionesSeleccionadas = new ArrayList<>();

    public Selector(String type) {
        iniciarComponentes(type, null);
        this.setSize(500, 350);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public Selector(String type, String theme) {
        iniciarComponentes(type, theme);
        this.setSize(500, 350);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public Selector(String type, String theme, String currentPlayer) {
        iniciarComponentes(type, theme);
        this.setSize(500, 350);
        this.setLocationRelativeTo(null);
        for (JRadioButton b : this.botones)
            if (b.getText().equalsIgnoreCase(currentPlayer)) {
                b.setSelected(true);
                b.setEnabled(false);
            }
        this.setVisible(true);
    }

    private void iniciarComponentes(String tipo, String tema) {
        this.setTitle("Juagdor " + jugadorID);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setModal(true);

        ventanaSeleccion = new JPanel();
        texto = new JLabel();
        opciones = new JPanel();


        listaOpciones.put("jugador", OPCIONES_JUGADOR);
        listaOpciones.put("tema", OPCIONES_TEMA);
        listaOpciones.put("tablero", OPCIONES_TABLERO);
        listaOpciones.put("dice", OPCIONES_DADO);

        ok = new JButton();

        this.add(ventanaSeleccion);
        ventanaSeleccion.setLayout(new BoxLayout(ventanaSeleccion, BoxLayout.Y_AXIS));

        this.setPreferredSize(new Dimension(400, 350));
        ventanaSeleccion.add(Box.createVerticalGlue());

        texto.setFont(new Font(FUENTE_ETIQUETA, 1, TAMANO_FUENTE + 4));
        texto.setText("Selecciona " + tipo + ":");
        ventanaSeleccion.add(texto);
        texto.setAlignmentX(Component.CENTER_ALIGNMENT);
        ventanaSeleccion.add(Box.createVerticalGlue());

        ventanaSeleccion.add(opciones);
        addOpciones(opciones, tipo, tema);

        ventanaSeleccion.add(Box.createVerticalGlue());
        ok.setText("OK");
        ventanaSeleccion.add(ok);
        ok.setSize(ANCHO_BOTON, ALTO_BOTON);
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        ventanaSeleccion.add(Box.createVerticalGlue());

        ok.addMouseListener(this);

        //pack();
    }

    private String getPath(String tipo, String opcion, String tema, boolean seleccionado) {
        String path = "";
        switch (tipo) {
            case "tema": {
                if (seleccionado)
                    path = PATH_IMAGEN + opcion + PATH_DIR + IMAGEN_DEFAULT + FORMATO_IMAGEN;
                else {
                    if (opcion.equals("pastel"))
                        path = PATH_IMAGEN + opcion + PATH_DIR + IMAGEN_DEFAULT + IMAGEN_NO_SELECCIONADA + FORMATO_IMAGEN;
                    else
                        path = PATH_IMAGEN + opcion + PATH_DIR + IMAGEN_NO_SELECCIONADA + FORMATO_IMAGEN;
                }
                break;
            }
            case "jugador": {
                if (seleccionado)
                    path = PATH_IMAGEN + tema + PATH_DIR + opcion + FORMATO_IMAGEN;
                else if (tema.equals("pastel"))
                    path = PATH_IMAGEN + tema + PATH_DIR + opcion + IMAGEN_NO_SELECCIONADA + FORMATO_IMAGEN;
                else
                    path = PATH_IMAGEN + tema + PATH_DIR + IMAGEN_NO_SELECCIONADA + FORMATO_IMAGEN;

                break;
            }
            default: {
                if (seleccionado)
                    path = PATH_IMAGEN + tema + PATH_DIR + opcion + FORMATO_IMAGEN;
                else
                    path = PATH_IMAGEN + tema + PATH_DIR + opcion + IMAGEN_NO_SELECCIONADA + FORMATO_IMAGEN;
                break;
            }
        }
        return path;
    }

    private void addOpciones(JPanel opciones, String tipo, String tema) {

        opciones.setPreferredSize(new Dimension(200, 120));
        opciones.setAlignmentX(Component.CENTER_ALIGNMENT);
        opciones.setLayout(new BoxLayout(opciones, BoxLayout.Y_AXIS));
        ButtonGroup b = new ButtonGroup();

        for (String opcion : listaOpciones.get(tipo)) {
            JRadioButton rButton = new JRadioButton();
            rButton.setFont(new Font(FUENTE_ETIQUETA, 1, TAMANO_FUENTE));
            rButton.setName(opcion);
            rButton.setText(rButton.getName());

            //System.out.println(getPath(tipo, opcion.toLowerCase(), tema, true));
            //System.out.println(getPath(tipo, opcion.toLowerCase(), tema, false));

            try {
                seleccionado = ImageIO.read(new File(getPath(tipo, opcion.toLowerCase(), tema, true)));
                noSeleccionado = ImageIO.read(new File(getPath(tipo, opcion.toLowerCase(), tema, false)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            rButton.setIcon(new ImageIcon(noSeleccionado));
            rButton.setSelectedIcon(new ImageIcon(seleccionado));
            rButton.setIconTextGap(15);
            rButton.setRolloverEnabled(false);
            Box.createVerticalGlue();
            opciones.add(rButton);
            if (!tipo.equals("jugador")) {
                b.add(rButton);
            }
            botones.add(rButton);
            opciones.add(Box.createVerticalGlue());
        }
        if (b.getButtonCount() > 0)
            botones.get(0).setSelected(true);
    }

    public void mouseClicked(MouseEvent evt) {
        for (JRadioButton boton : botones) {
            if (boton.isSelected()) {
                opcionesSeleccionadas.add(boton.getName().toLowerCase());
                colorNoDisponible(boton);
//                for (int i = 0; i < OPCIONES_JUGADOR.size(); i++) {   //<------------------------------
//                    if(boton.getName().equalsIgnoreCase(OPCIONES_JUGADOR.get(i))){
//                        OPCIONES_JUGADOR.set(i, "Ocupado");
//                        break;
//                    }
//                }
//                System.out.println(OPCIONES_JUGADOR);
            }
        }
        this.setVisible(false);
    }

    public void colorNoDisponible(JRadioButton boton){
        for(String color : OPCIONES_JUGADOR){
            if(boton.getName().equalsIgnoreCase(color)){
                OPCIONES_JUGADOR.set(OPCIONES_JUGADOR.indexOf(color), "Ocupado");
                break;
            }
        }
    }

    public ArrayList<Color> jugadoresSeleccionados() {
        ArrayList<Color> pColors = new ArrayList<>();
        for (String opcion : opcionesSeleccionadas)
            for (Color color : Color.values())
                if (opcion.equals(color.name()))
                    pColors.add(color);
        return pColors;
    }

    public boolean tableroSeleccionado() {
        try {
            if (opcionesSeleccionadas.get(0).equals("especial")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            System.exit(0);
        }
        return false;
    }

    public Tema temaSeleccionado() {
        Tema tema = TEMA_DEFAULT;
        for (Tema t : Tema.values())
            try {
                if (opcionesSeleccionadas.get(0).equals(t.name())) {
                    return t;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Juego terminado");
                System.exit(0);
                break;
            }

        return tema;
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
}
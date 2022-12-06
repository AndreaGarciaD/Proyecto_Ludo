package ludo;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Ventana extends JFrame {

    JPanel panelMenu = new JPanel();
    private JButton btnJugar = new JButton("Jugar");
    private JButton btn1 = new JButton("Multijugador");

    public Ventana() {
        this.setTitle("LUDO");

        this.getContentPane().setBackground(Color.magenta);
        this.setResizable(false);
        this.setSize(new Dimension(500, 350));
        //this.setLayout(new BorderLayout());

        this.setLocationRelativeTo(null);
        cargarElementos();
        this.setVisible(true);
    }

    private void cargarElementos() {
        panelMenu.setSize(this.getSize());
        panelMenu.setLayout(null);
        panelMenu.setBackground(new Color(54, 146, 227));


        int x = 120;
        int y = 180;

        btnJugar.setBounds(x, y, 250, 30);
        panelMenu.add(btnJugar);
        y += 40;
        btn1.setBounds(x, y, 250, 30);
        panelMenu.add(btn1);

        btnJugar.addActionListener(e -> {
            this.dispose();
            iniciar();

        });

        btn1.addActionListener(e -> {
            this.dispose();
            iniciarMultijugador();

        });

        this.add(panelMenu);
    }

    private static EstadoJuego juego;
    private static Selector ventanaSeleccion;
    LudoGUI ludoGUI = new LudoGUI(juego);

    private void iniciar() {

        new pathImagenes();

        ventanaSeleccion = new Selector("tema");
        pathImagenes.Tema tema = ventanaSeleccion.temaSeleccionado();

        ventanaSeleccion = new Selector("jugador", tema.name());
        List<pathImagenes.Color> coloresJugadores = ventanaSeleccion.jugadoresSeleccionados();

        ventanaSeleccion = new Selector("tablero", tema.name());
        boolean especial = ventanaSeleccion.tableroSeleccionado();

        juego = new EstadoJuego(tema, coloresJugadores, especial);

        ludoGUI.dibujarGUI(juego);
        //System.exit(0);
    }

    private void iniciarMultijugador() {

        conectarAServidor();

        new pathImagenes();

        ventanaSeleccion = new Selector("tema");
        pathImagenes.Tema tema = ventanaSeleccion.temaSeleccionado();

        ventanaSeleccion = new Selector("jugador", tema.name());
        List<pathImagenes.Color> plColors = ventanaSeleccion.jugadoresSeleccionados();

        ventanaSeleccion = new Selector("tablero", tema.name());
        boolean especial = ventanaSeleccion.tableroSeleccionado();

        juego = new EstadoJuego(tema, plColors, especial);


        ludoGUI.dibujarGUI(juego);
        //System.exit(0);
    }

    //------------------------

    private Socket socket;
    public static int jugadorID;

    public void conectarAServidor() {
        try {
            socket = new Socket("localhost", 1234);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            jugadorID = in.readInt();
            if (jugadorID == 1) {
                System.out.println("Esperando a mas jugadores...");
            }
            System.out.println("Conectado al servidor");
        } catch (IOException e) {
            System.out.println("Error al conectar al servidor");
            e.printStackTrace();

        }
    }

}

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private ServerSocket serverSocket;
    private int numJugadores;
    private int maxJugadores;

    public Servidor() {
        numJugadores = 0;
        maxJugadores = 4;
        try {
            serverSocket = new ServerSocket(1234);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptConnections() {

        try {
            System.out.println("Esperando jugadores...");

            while (numJugadores < maxJugadores) {
                Socket s = serverSocket.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                numJugadores++;
                out.writeInt(numJugadores);
                System.out.println("Player " + numJugadores + " connected!");
            }
            System.out.println("Servidor lleno!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor server = new Servidor();
        server.acceptConnections();
    }
}

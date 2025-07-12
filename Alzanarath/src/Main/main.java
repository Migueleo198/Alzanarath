package Main;

import javax.swing.*;

public class main {
    // Change these to your server IP and port
    private static final String SERVER_IP = "213.165.69.145";
    private static final int SERVER_PORT = 55555;

    public static void main(String[] args) {
        startClientGame(SERVER_IP, SERVER_PORT);
    }

    private static void startClientGame(String host, int port) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Alzanarath Game");

        GamePanel gp = new GamePanel();

        // Always connect as client
        gp.connectToServer(host, port);

        window.add(gp);
        window.pack();

        gp.startGameThread();
        gp.setupGame();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}

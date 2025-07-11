package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class main {
    public static void main(String[] args) {
        // Create the main menu frame
        JFrame menuFrame = new JFrame("Alzanarath Game");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setResizable(false);
        menuFrame.setSize(400, 300);
        menuFrame.setLayout(new GridLayout(3, 1));

        // Create buttons
        JButton singlePlayerBtn = new JButton("Single Player");
        JButton hostGameBtn = new JButton("Host Multiplayer Game");
        JButton joinGameBtn = new JButton("Join Multiplayer Game");

        // Add action listeners
        singlePlayerBtn.addActionListener(e -> {
            startGame(false, null);
            menuFrame.dispose();
        });

        hostGameBtn.addActionListener(e -> {
            String portStr = JOptionPane.showInputDialog("Enter port number (default: 55555):");
            int port = portStr != null && !portStr.isEmpty() ? Integer.parseInt(portStr) : 55555;
            startGame(true, null, port);
            menuFrame.dispose();
        });

        joinGameBtn.addActionListener(e -> {
            String host = JOptionPane.showInputDialog("Enter host IP:");
            if (host != null && !host.isEmpty()) {
                String portStr = JOptionPane.showInputDialog("Enter port number (default: 55555):");
                int port = portStr != null && !portStr.isEmpty() ? Integer.parseInt(portStr) : 55555;
                startGame(false, host, port);
                menuFrame.dispose();
            }
        });

        // Add buttons to frame
        menuFrame.add(singlePlayerBtn);
        menuFrame.add(hostGameBtn);
        menuFrame.add(joinGameBtn);

        // Center and show menu
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }

    private static void startGame(boolean isHost, String host, int port) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Alzanarath Game");

        GamePanel gp = new GamePanel();
        
        // Initialize multiplayer if needed
        if (isHost) {
            gp.startServer(port);
        } else if (host != null) {
            gp.connectToServer(host, port);
        }

        window.add(gp);
        window.pack();

        gp.startGameThread();
        gp.setupGame();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    // Overload for single player
    private static void startGame(boolean isHost, String host) {
        startGame(isHost, host, 55555);
    }
}
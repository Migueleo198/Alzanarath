// MultiplayerMenu.java
package Main;

import javax.swing.*;

import Network.NetworkManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiplayerMenu extends JFrame {
    private GamePanel gamePanel;
    
    public MultiplayerMenu(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setTitle("Multiplayer Options");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        
        JButton hostButton = new JButton("Host Game");
        JButton joinButton = new JButton("Join Game");
        JButton singlePlayerButton = new JButton("Single Player");
        
        hostButton.addActionListener(e -> {
            gamePanel.startServer(NetworkManager.DEFAULT_PORT);
            gamePanel.startGameThread();
            dispose();
        });
        
        joinButton.addActionListener(e -> {
            String host = JOptionPane.showInputDialog("Enter host IP:");
            if (host != null && !host.isEmpty()) {
                gamePanel.connectToServer(host, NetworkManager.DEFAULT_PORT);
                gamePanel.startGameThread();
                dispose();
            }
        });
        
        singlePlayerButton.addActionListener(e -> {
            gamePanel.startGameThread();
            dispose();
        });
        
        add(hostButton);
        add(joinButton);
        add(singlePlayerButton);
        
        setVisible(true);
    }
}
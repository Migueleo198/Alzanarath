package Network;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import Main.GamePanel;
import entity.Player;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GamePanel gamePanel;
    private boolean running = true;
    
    private String playerId;
    private Player localPlayer;
    private Map<String, Player> remotePlayers = new HashMap<>();
    private int frameCount = 0;

    public Client(GamePanel gamePanel, String host, int port) throws IOException {
        this.gamePanel = gamePanel;
        this.localPlayer = gamePanel.player;
        this.playerId = "Player_" + System.currentTimeMillis();
        
        try {
            // Establish connection with timeout
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 5000);
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Send CONNECT message with full player data
            String connectMsg = String.format("CONNECT:%s,%d,%d,%s,1",
                playerId,
                localPlayer.worldX,
                localPlayer.worldY,
                localPlayer.direction);
            sendMessage(connectMsg);
            
            // Start message processing thread
            new Thread(this::receiveMessages).start();
            System.out.println("Connected to server successfully");
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            stop();
            throw e;
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                handleMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Disconnected from server");
        } finally {
            stop();
        }
    }

    private void handleMessage(String message) {
        System.out.println("Received: " + message);
        
        try {
            String[] parts = message.split(":", 2);
            if (parts.length < 2) {
                System.err.println("Invalid message format: " + message);
                return;
            }

            String type = parts[0];
            String data = parts[1];
            
            switch (type) {
                case "WELCOME":
                    handleWelcome(data);
                    break;
                case "PLAYER_INIT":
                    handlePlayerInit(data);
                    break;
                case "PLAYER_JOIN":
                    handlePlayerJoin(data);
                    break;
                case "PLAYER_DATA":
                    handlePlayerUpdate(data);
                    break;
                case "PLAYER_LEFT":
                    handlePlayerLeft(data);
                    break;
                case "ERROR":
                    System.err.println("Server error: " + data);
                    break;
                default:
                    System.err.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + message);
            e.printStackTrace();
        }
    }

    private void handleWelcome(String data) {
        // Server acknowledges connection
        System.out.println("Server welcome: " + data);
    }

 // Modify the handlePlayerInit method:
    private void handlePlayerInit(String data) {
        String[] initData = data.split(",");
        if (initData.length < 5) {
            System.err.println("Invalid PLAYER_INIT format: " + data);
            return;
        }
        
        String playerId = initData[0];
        // Skip our own player data
        if (playerId.equals(this.playerId)) return;
        
        gamePanel.addRemotePlayer(
            playerId,
            Integer.parseInt(initData[1]),
            Integer.parseInt(initData[2]),
            initData[3],
            Integer.parseInt(initData[4])
        );
        
        System.out.println("Initialized player: " + playerId);
    }

   

    private void handlePlayerJoin(String data) {
        try {
            String[] joinData = data.split(",");
            if (joinData.length < 5) {
                System.err.println("Invalid PLAYER_JOIN format: " + data);
                return;
            }
            
            String playerId = joinData[0];
            int x = Integer.parseInt(joinData[1]);
            int y = Integer.parseInt(joinData[2]);
            String direction = joinData[3];
            int spriteNum = Integer.parseInt(joinData[4]);
            
            // Validate position values
            if (x < 0 || y < 0) {
                System.err.println("Invalid position in PLAYER_JOIN: " + data);
                return;
            }
            
            gamePanel.addRemotePlayer(playerId, x, y, direction, spriteNum);
            
        } catch (NumberFormatException e) {
            System.err.println("Error parsing PLAYER_JOIN data: " + data);
            e.printStackTrace();
        }
    }

    // Modify the handlePlayerUpdate method:
    private void handlePlayerUpdate(String data) {
        String[] updateData = data.split(",");
        if (updateData.length < 5) {
            System.err.println("Invalid PLAYER_DATA format: " + data);
            return;
        }
        
        String playerId = updateData[0];
        // Skip our own updates
        if (playerId.equals(this.playerId)) return;
        
        // Create player if not exists
        if (!gamePanel.remotePlayers.containsKey(playerId)) {
            gamePanel.addRemotePlayer(
                playerId,
                Integer.parseInt(updateData[1]),
                Integer.parseInt(updateData[2]),
                updateData[3],
                Integer.parseInt(updateData[4])
            );
        } else {
            gamePanel.updateRemotePlayer(
                playerId,
                Integer.parseInt(updateData[1]),
                Integer.parseInt(updateData[2]),
                updateData[3],
                Integer.parseInt(updateData[4])
            );
        }
    }

    private void handlePlayerLeft(String data) {
        gamePanel.removeRemotePlayer(data);
    }

    public void sendPlayerUpdate(Player player) {
        if (out == null || out.checkError()) return;
        
        String data = String.format("PLAYER_UPDATE:%s,%d,%d,%s,%d",
            playerId,
            player.worldX,
            player.worldY,
            player.direction,
            player.spriteNum);
        
        out.println(data);
        System.out.println("Sent update: " + data);
    }

    public void sendMessage(String message) {
        try {
            if (out != null && !out.checkError()) {
                out.println(message);
                out.flush();
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                sendMessage("PLAYER_LEFT:" + playerId);
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }

    public String getPlayerId() {
        return playerId;
    }

    public Map<String, Player> getRemotePlayers() {
        return remotePlayers;
    }
}
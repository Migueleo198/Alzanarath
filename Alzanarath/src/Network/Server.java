package Network;

import java.io.*;
import java.net.*;
import java.util.*;

import Main.GamePanel;
import entity.Player;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private GamePanel gamePanel;
    private boolean running = true;
    
    public Server(GamePanel gamePanel, int port) throws IOException {
        this.gamePanel = gamePanel;
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        new Thread(this::acceptConnections).start();
    }
    
    private void acceptConnections() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start();
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        }
    }
    
 // In Server.java add these instance variables:
    private Map<String, Player> playerStates = new HashMap<>();

    // Add this method to update player states:
    public void updatePlayerState(String playerId, int x, int y, String direction, int spriteNum) {
        Player player = playerStates.get(playerId);
        if (player == null) {
            player = new Player(gamePanel);
            player.setPlayerId(playerId);
            playerStates.put(playerId, player);
        }
        player.worldX = x;
        player.worldY = y;
        player.direction = direction;
        player.spriteNum = spriteNum;
    }
    
    
    
    public void broadcastPlayerUpdate(Player player) {
        String state = String.format("PLAYER_DATA:%s,%d,%d,%s,%d",
            player.getPlayerId(),
            player.worldX,
            player.worldY,
            player.direction,
            player.spriteNum);
        
        // Send to ALL clients including the sender if needed
        broadcast(state);
        
        // Or if you want to exclude the sender:
        // broadcastExcept(state, player.getPlayerId());
    }
    
    private void broadcastExcept(String message, String excludeId) {
        synchronized(clients) {
            for (ClientHandler client : clients) {
                if (!client.getClientId().equals(excludeId)) {
                    client.sendMessage(message);
                }
            }
        }
    }
    
    public void broadcast(String message) {
        synchronized(clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }
    
    public void removeClient(ClientHandler handler) {
        synchronized(clients) {
            clients.remove(handler);
            broadcast("PLAYER_LEFT:" + handler.getClientId());
        }
    }
    
    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    class ClientHandler implements Runnable {
        private Socket socket;
        private Server server;
        private PrintWriter out;
        private BufferedReader in;
        private String clientId;
        
        public ClientHandler(Socket socket, Server server) {
            this.socket = socket;
            this.server = server;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public String getClientId() {
            return clientId;
        }
        
        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    handleNetworkMessage(inputLine);
                }
            } catch (IOException e) {
                System.err.println("Client disconnected: " + clientId);
            } finally {
                server.removeClient(this);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void handleNetworkMessage(String message) {
            System.out.println("Server received: " + message);
            
            try {
                String[] parts = message.split(":", 2);
                if (parts.length < 2) {
                    System.err.println("Invalid message format: " + message);
                    return;
                }
                
                String type = parts[0];
                String data = parts[1];
                
                switch (type) {
                    case "CONNECT":
                        handleConnect(data);
                        break;
                    case "PLAYER_UPDATE":
                        handlePlayerUpdate(data);
                        break;
                    default:
                        System.err.println("Unknown message type: " + type);
                }
            } catch (Exception e) {
                System.err.println("Error processing message: " + message);
                e.printStackTrace();
            }
        }
        
     // Modify the handleConnect method:
        private void handleConnect(String data) {
            try {
                String[] connectData = data.split(",");
                if (connectData.length < 4) {
                    sendMessage("ERROR:Invalid connect format");
                    throw new IOException("Invalid CONNECT format: " + data);
                }
                
                this.clientId = connectData[0];
                int x = Integer.parseInt(connectData[1]);
                int y = Integer.parseInt(connectData[2]);
                String direction = connectData[3];
                
                // Add player to game
                server.gamePanel.addRemotePlayer(clientId, x, y, direction, 1);
                
                // Send welcome with ALL current players (including server player)
                sendMessage(String.format("WELCOME:%s,%d,%d,%s,%d", 
                    server.gamePanel.getPlayerId(),
                    server.gamePanel.player.worldX,
                    server.gamePanel.player.worldY,
                    server.gamePanel.player.direction,
                    server.gamePanel.player.spriteNum));
                
                // Send existing players (including other clients)
                sendExistingPlayers();
                
                // Notify ALL clients about new player (including the new one)
                server.broadcast(String.format("PLAYER_JOIN:%s,%d,%d,%s,%d",
                    clientId, x, y, direction, 1));
                    
                System.out.println("Client connected: " + clientId);
            } catch (Exception e) {
                System.err.println("Connect failed: " + e.getMessage());
            }
        }

        // Enhance sendExistingPlayers:
        private void sendExistingPlayers() {
            // Send server player first
            Player serverPlayer = server.gamePanel.player;
            String serverData = String.format("PLAYER_INIT:%s,%d,%d,%s,%d",
                server.gamePanel.getPlayerId(),
                serverPlayer.worldX,
                serverPlayer.worldY,
                serverPlayer.direction,
                serverPlayer.spriteNum);
            sendMessage(serverData);
            
            // Send all other connected players
            synchronized(server.gamePanel.remotePlayers) {
                for (Map.Entry<String, Player> entry : server.gamePanel.remotePlayers.entrySet()) {
                    if (!entry.getKey().equals(clientId)) {
                        Player p = entry.getValue();
                        String playerData = String.format("PLAYER_INIT:%s,%d,%d,%s,%d",
                            entry.getKey(),
                            p.worldX,
                            p.worldY,
                            p.direction,
                            p.spriteNum);
                        sendMessage(playerData);
                    }
                }
            }
        }
        
     // Modify the handlePlayerUpdate method in ClientHandler:
     // In Server.java's handlePlayerUpdate:
        private void handlePlayerUpdate(String data) {
            String[] updateData = data.split(",");
            if (updateData.length < 5) return;
            
            String playerId = updateData[0];
            // Update server's view
            server.gamePanel.updateRemotePlayer(playerId, 
                Integer.parseInt(updateData[1]),
                Integer.parseInt(updateData[2]),
                updateData[3],
                Integer.parseInt(updateData[4]));
            
            // Broadcast to all OTHER clients
            server.broadcastExcept("PLAYER_DATA:" + data, playerId);
        }
        
        public void sendMessage(String message) {
            try {
                if (out != null && !out.checkError()) {
                    out.println(message);
                    out.flush();
                    System.out.println("Sent to " + clientId + ": " + message);
                }
            } catch (Exception e) {
                System.err.println("Error sending message to " + clientId + ": " + e.getMessage());
            }
        }
    }
}
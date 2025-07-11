package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import javax.swing.JPanel;
import entity.Entity;
import entity.Player;
import tile.TileManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import Network.Server;
import Network.Client;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    private final int tileSize = 48;
    private final int maxScreenCol = 16;
    private final int maxScreenRow = 12;
    private final int screenWidth = tileSize * maxScreenCol;
    private final int screenHeight = tileSize * maxScreenRow;
    
    // WORLD SETTINGS
    public final int maxWorldCol = 32;
    public final int maxWorldRow = 32;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;
    
    // GAME THREAD
    private Thread gameThread;
    private int FPS = 60;
    
    // GAME COMPONENTS
    public KeyHandler keyH = new KeyHandler();
    public Player player = new Player(this);
    public TileManager tileM = new TileManager(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public ArrayList<Entity> drawEntities = new ArrayList<>();
    public Sound sound = new Sound();
    
    // NETWORK COMPONENTS
    private Server server;
    private Client client;
    public Map<String, Player> remotePlayers = new HashMap<>();
    private boolean isHost = false;
    private String playerId;
    private int frameCount = 0;
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        // Initialize player with unique ID
        this.playerId = "Player_" + System.currentTimeMillis();
        player.setPlayerId(playerId);
        player.setLocalPlayer(true);
        drawEntities.add(player); // Add local player to draw list
        
        if (isHost) {
            player.worldX = 10 * tileSize;  // Host spawn position
            player.worldY = 10 * tileSize;
        } else {
            player.worldX = 20 * tileSize;  // Client spawn position 
            player.worldY = 20 * tileSize;
        }
    }
    
 
    
    public void addRemotePlayer(String playerId, int x, int y) {
        if (!remotePlayers.containsKey(playerId) && !playerId.equals(this.playerId)) {
            Player newPlayer = new Player(this);
            newPlayer.setPlayerId(playerId);
            newPlayer.setLocalPlayer(false);
            newPlayer.worldX = x;
            newPlayer.worldY = y;
            remotePlayers.put(playerId, newPlayer);
            drawEntities.add(newPlayer);
            System.out.println("Spawned remote player at: " + x + "," + y);
        }
    }

    
    
    // NETWORK METHODS
    public void startServer(int port) {
        try {
            server = new Server(this, port);
            isHost = true;
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
    
 // Modify the connectToServer method:
    public void connectToServer(String host, int port) {
        try {
            client = new Client(this, host, port);
            // Force an immediate position update
            client.sendPlayerUpdate(player);
            System.out.println("Connected to server and sent initial position");
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }
    
 // Modify the addRemotePlayer method:
    public void addRemotePlayer(String playerId, int x, int y, String direction, int spriteNum) {
        // Don't add our own player as a remote player
        if (playerId.equals(this.playerId)) return;
        
        if (!remotePlayers.containsKey(playerId)) {
            Player newPlayer = new Player(this);
            newPlayer.setPlayerId(playerId);
            newPlayer.setLocalPlayer(false);
            newPlayer.worldX = x;
            newPlayer.worldY = y;
            newPlayer.direction = direction;
            newPlayer.spriteNum = spriteNum;
            remotePlayers.put(playerId, newPlayer);
            System.out.println("Added remote player: " + playerId);
        }
    }
   
    
    public void removeRemotePlayer(String playerId) {
        Player p = remotePlayers.remove(playerId);
        if (p != null) {
            drawEntities.remove(p);
            System.out.println("Player left: " + playerId);
        }
    }
    
    // GAME LOOP METHODS
    public void setupGame() {
        playMusic(0);
    }
    
    public void startGameThread() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }
    
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
            
            // Prevent CPU overuse
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
   // In GamePanel class
    public void updateRemotePlayer(String playerId, int x, int y, String direction, int spriteNum) {
        Player p = remotePlayers.get(playerId);
        if (p != null) {
            // Add interpolation for smooth movement
            p.worldX += (x - p.worldX) * 0.2f;
            p.worldY += (y - p.worldY) * 0.2f;
            p.direction = direction;
            p.spriteNum = spriteNum;
        }
    }

    public void update() {
        // Update local player first
        player.update();
        
        // Send network updates
        if (frameCount % 3 == 0) {  // Control update rate
            if (isHost) {
                // Broadcast server player's state to all clients
                server.broadcastPlayerUpdate(player);
            } else if (client != null) {
                // Send client player's state to server
                client.sendPlayerUpdate(player);
            }
        }
        frameCount++;
        
        // Update remote players
        for (Player p : remotePlayers.values()) {
            p.update();
        }
    }
    
 // Add these helper methods:
    public int worldXToScreenX(int worldX) {
        return worldX - player.worldX + player.screenX;
    }

    public int worldYToScreenY(int worldY) {
        return worldY - player.worldY + player.screenY;
    }

    // Update the drawing code:
   
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        // Draw local player last (on top)
        player.draw(g2);
        // Draw world tiles first
        tileM.drawLayered(g2);
        
        // Draw all remote players (excluding local player)
        for (Player p : remotePlayers.values()) {
            // Skip drawing if this is our local player
            if (p.getPlayerId().equals(playerId)) continue;
            
            int screenX = worldXToScreenX(p.worldX);
            int screenY = worldYToScreenY(p.worldY);
            
            if (isOnScreen(p.worldX, p.worldY)) {
                g2.drawImage(p.getCurrentImage(), screenX, screenY, tileSize, tileSize, null);
            }
        }
        
       
    }
    
    private boolean isOnScreen(int worldX, int worldY) {
        int screenX = worldX - player.worldX + player.screenX;
        int screenY = worldY - player.worldY + player.screenY;
        return screenX + tileSize > 0 && screenX < screenWidth &&
               screenY + tileSize > 0 && screenY < screenHeight;
    }
    
    // SOUND METHODS
    public void playMusic(int i) {
        sound.setFile(i);
        sound.play();
        sound.loop();
    }
    
    public void stopMusic() {
        sound.stop();
    }
    
    public void playSE(int i) {
        sound.setFile(i);
        sound.play();
    }
    
    // CLEANUP METHOD
    public void stopGame() {
        if (server != null) server.stop();
        if (client != null) client.stop();
        gameThread = null;
    }
    
    // GETTERS
    public int getTileSize() { return tileSize; }
    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
    public int getMaxScreenCol() { return maxScreenCol; }
    public int getMaxScreenRow() { return maxScreenRow; }
    public boolean isHost() { return isHost; }
    public String getPlayerId() { return playerId; }
}
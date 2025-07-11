package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entity.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
	
	//SCREEN SETTINGS
	
	private final int tileSize = 48;
	
	private final int maxScreenCol = 16;
	

	private final int maxScreenRow = 12;
	
	private final int screenWidth = tileSize * maxScreenCol; //768p
	private final int screenHeight = tileSize * maxScreenRow; // 576p
	
	
	private Thread gameThread;
	
	public KeyHandler keyH = new KeyHandler();
	
	//SET player
	
	public Player player = new Player(this);
	
	
	//TILE MANAGER
	
	TileManager tileM = new TileManager(this);
	
	
	//WORLD SETTINGS
	public final int maxWorldCol = 64;
	public final int maxWorldRow = 60;
	
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	
	
	//COLLISIONS
	
	public CollisionChecker cChecker = new CollisionChecker(this); 
	
	
	private int FPS = 60;
	
	//SOUND AND MUSIC
	Sound sound = new Sound();
	
	public GamePanel () {
		this.setPreferredSize(new Dimension(screenWidth,screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
		
		
	}
	
	public void setupGame() {
		playMusic(0);
	}
	
	public void startGameThread() {
		
		gameThread = new Thread(this);
		
		gameThread.start();

	}
	
	public void update() {
		player.update();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		tileM.drawBackground(g2);
		player.draw(g2);
		tileM.drawForeground(g2);
		
		
		g2.dispose();
	}

	@Override
	public void run() {
	    final double drawInterval = 1000000000.0 / FPS; // 60 FPS
	    double delta = 0;
	    long lastTime = System.nanoTime();
	    long timer = 0;
	    int frameCount = 0;

	    while (gameThread != null) {
	        long currentTime = System.nanoTime();
	        delta += (currentTime - lastTime) / drawInterval;
	        timer += (currentTime - lastTime);
	        lastTime = currentTime;

	        if (delta >= 1) {
	            update();
	            repaint();
	            delta--;
	            frameCount++;
	        }

	      
	        if (timer >= 1000000000) {
	            // System.out.println("FPS: " + frameCount);
	            frameCount = 0;
	            timer = 0;
	        }

	        try {
	            Thread.sleep(1); // Prevent 100% CPU usage
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
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


	public int getTileSize() {
		return tileSize;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}
	
	public int getMaxScreenCol() {
		return maxScreenCol;
	}

	public int getMaxScreenRow() {
		return maxScreenRow;
	}
}

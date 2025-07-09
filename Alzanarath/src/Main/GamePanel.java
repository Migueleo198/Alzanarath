package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entity.Player;

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
	
	Player player = new Player(this);
	
	
	private int FPS = 60;
	
	public GamePanel () {
		this.setPreferredSize(new Dimension(screenWidth,screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
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
		
		player.draw(g2);
		
		g2.dispose();
	}

	@Override
	public void run() {
		
		double drawInterval  = 1000000000/FPS; // UPDATES EVERY 0.016666 Seconds (60 FPS Tick)
		double nextDrawTime = System.nanoTime() + drawInterval;
		
		
		
		while(gameThread!=null) {
			
			long currentTime = System.nanoTime();
			
			
			
			update();
			
			repaint();
			
			
			
			try {
				
				double remainingTime = nextDrawTime - currentTime;
				remainingTime = remainingTime / 1000000;
				
				if(remainingTime < 0) {
					remainingTime = 0;
				}
				
				Thread.sleep((long)remainingTime);
				
				nextDrawTime += drawInterval;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
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
}

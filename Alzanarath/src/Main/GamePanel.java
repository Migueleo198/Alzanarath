package Main;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public class GamePanel extends JPanel{
	
	//SCREEN SETTINGS
	
	final int tileSize = 48;
	
	final int maxScreenCol = 16;
	final int maxScreenRow = 12;
	
	final int screenWidth = tileSize * maxScreenCol; //768p
	final int screenHeight = tileSize * maxScreenRow; // 576p
	
	public GamePanel () {
		this.setPreferredSize(new Dimension(screenWidth,screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
	}
}

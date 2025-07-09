package entity;

import Main.GamePanel;

public class Player extends Entity{
	
	
	
	private GamePanel gp;
	
	
	public Player(GamePanel gp) {
		this.gp = gp;
		
		setDefaultValues();
	}
	
	public void setDefaultValues() {
		this.x = 100;
		this.y = 100;
		this.speed = 4;
	}
	
	public void update() {
		
		
		if(gp.keyH.upPressed == true) {
			y -= speed;
		}
		
		else if(gp.keyH.downPressed == true) {
			y += speed;
		}
		
		else if(gp.keyH.rightPressed == true) {
			x += speed;
		}
		
		else if(gp.keyH.leftPressed == true) {
			x -= speed;
		}
	}
	
	public void draw() {
		
	}

	public int getPlayerSpeed() {
		return speed;
	}

	public void setPlayerSpeed(int playerSpeed) {
		this.speed = playerSpeed;
	}

	public int getPlayerY() {
		return y;
	}

	public void setPlayerY(int y) {
		this.y = y;
	}

	public int getPlayerX() {
		return x;
	}

	public void setPlayerX(int x) {
		this.x = x;
	}
}

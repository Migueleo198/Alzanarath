package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class Player extends Entity{
	
	
	
	private GamePanel gp;
	
	
	public Player(GamePanel gp) {
		this.gp = gp;
		
		setDefaultValues();
		getPlayerImage();
	}
	
	public void setDefaultValues() {
		this.x = 100;
		this.y = 100;
		this.speed = 4;
		this.direction = "down";
	}
	
	public void update() {
		
		if(gp.keyH.upPressed == true || gp.keyH.downPressed == true 
				|| gp.keyH.rightPressed == true || gp.keyH.leftPressed == true) {
			
			
			if(gp.keyH.upPressed == true) {
				direction = "up";
				y -= speed;
			}
			
			else if(gp.keyH.downPressed == true) {
				direction = "down";
				y += speed;
			}
			
			else if(gp.keyH.rightPressed == true) {
				direction = "right";
				x += speed;
			}
			
			else if(gp.keyH.leftPressed == true) {
				direction = "left";
				x -= speed;
			}
			
			spriteCounter++;
			
			if(spriteCounter >  12) {
				if(spriteNum == 1) {
					spriteNum = 2;
				}
				else if (spriteNum == 2) {
					spriteNum = 1;
				}
				
				
				spriteCounter = 0;
			}
			
			
		}
		
		
	}
	
	public void draw(Graphics2D g2) {
		
		
		BufferedImage image = null;
		
		switch(direction) {
		case "up": 
			if(spriteNum==1) {
			image = up1;
			}
			if(spriteNum == 2) {
			image = up2;
			}
			break;
		case "down":
			if(spriteNum==1) {
			image = down1;
			}
			if(spriteNum == 2) {
			image = down2;
			}
			break;
		case "left":
			if(spriteNum==1) {
			image = left1;
			}
			if(spriteNum == 2) {
			image = left2;
			}
			break;
		case "right":
			if(spriteNum==1) {
			image = right1;
			}
			if(spriteNum == 2) {
			image = right2;
			}
			break;
		}
		
		g2.drawImage(image, x, y, gp.getTileSize(),gp.getTileSize(),null);
	}
	
	public void getPlayerImage() {
		try {
			
			up1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(up).png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(up2).png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(down).png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(down2).png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(right).png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(right2).png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(left).png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/Player/SpritesJava(left2).png"));
			
		}catch(IOException e) {
			e.printStackTrace();
		}
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

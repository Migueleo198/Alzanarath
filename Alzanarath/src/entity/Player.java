package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class Player extends Entity{
	
	
	
	private GamePanel gp;
	
	public final int screenX;
	public final int screenY;
	
	
	public Player(GamePanel gp) {
		this.gp = gp;
		
		screenX = gp.getScreenWidth()/2 - (gp.getTileSize()/2);
		screenY = gp.getScreenHeight()/2 - (gp.getTileSize()/2);
		
		solidAreaDefaultX = 8;
		solidAreaDefaultY = 16;
		
		solidArea = new Rectangle();
		solidArea.x = 8;
		solidArea.y = 16;
		solidArea.width = 32;
		solidArea.height = 32;
		
		setDefaultValues();
		getPlayerImage();
	}
	
	public void setDefaultValues() {
		this.worldX = gp.getTileSize() * 23;
		this.worldY = gp.getTileSize() * 21;
		this.speed = 4;
		this.direction = "down";
	}
	
	public void update() {
		
		if(gp.keyH.upPressed == true || gp.keyH.downPressed == true 
				|| gp.keyH.rightPressed == true || gp.keyH.leftPressed == true) {
			
			
			if(gp.keyH.upPressed == true) {
				direction = "up";
				
			}
			
			else if(gp.keyH.downPressed == true) {
				direction = "down";
				
			}
			
			else if(gp.keyH.rightPressed == true) {
				direction = "right";
				
			}
			
			else if(gp.keyH.leftPressed == true) {
				direction = "left";
				
			}
			
			
			//CHECK TILE COLLISION
			collisionOn = false;
			gp.cChecker.checkTile(this);
			
			
			//IF COLLISION IS FALSE, PLAYER CAN MOVE
			
			if(collisionOn == false) {
				
				switch(direction) {
				case "up":
					worldY -= speed;
					break;
				case "down":
					worldY += speed;
					break;
				case "right":
					worldX += speed;
					break;
				case "left":
					worldX -= speed;
					break;
				}
				
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
		
		g2.drawImage(image, screenX, screenY, gp.getTileSize(),gp.getTileSize(),null);
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
		return worldX;
	}

	public void setPlayerY(int y) {
		this.worldY = y;
	}

	public int getPlayerX() {
		return worldX;
	}

	public void setPlayerX(int x) {
		this.worldY = x;
	}
}

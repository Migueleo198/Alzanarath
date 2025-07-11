package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class Player extends Entity{
	private boolean isHiddenBehindObject = false;

	private boolean applyVerticalOffset = false;
	private String lastDirection = "down"; // initialize to default

	
	//CHECK LAST POSITION
	public boolean isDownLast;

	

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
	    boolean moved = false;

	    if (gp.keyH.upPressed) {
	        direction = "up";
	        moved = true;
	    } else if (gp.keyH.downPressed) {
	        direction = "down";
	        moved = true;
	    } else if (gp.keyH.rightPressed) {
	        direction = "right";
	        moved = true;
	    } else if (gp.keyH.leftPressed) {
	        direction = "left";
	        moved = true;
	    }

	    if (moved) {
	        lastDirection = direction;

	        collisionOn = false;
	        gp.cChecker.checkTile(this);

	        if (!collisionOn) {
	            switch(direction) {
	                case "up" -> worldY -= speed;
	                case "down" -> worldY += speed;
	                case "left" -> worldX -= speed;
	                case "right" -> worldX += speed;
	            }
	        }

	        spriteCounter++;
	        if (spriteCounter > 12) {
	            spriteNum = (spriteNum == 1) ? 2 : 1;
	            spriteCounter = 0;
	        }
	    }
	}

	
	

	public void updateVerticalOffset() {
	    // Example logic:
	    if ("down".equals(direction)) {
	        applyVerticalOffset = true;
	    } else if (("left".equals(direction) || "right".equals(direction)) && applyVerticalOffset) {
	        // keep offset as true if previously applied
	    } else {
	        applyVerticalOffset = false;
	    }
	}

	
	

	private int verticalOffset = 0;  // move this to instance variable

	public void draw(Graphics2D g2) {
	    BufferedImage image = null;

	    switch (direction) {
	        case "up" -> image = (spriteNum == 1) ? up1 : up2;
	        case "down" -> image = (spriteNum == 1) ? down1 : down2;
	        case "left" -> image = (spriteNum == 1) ? left1 : left2;
	        case "right" -> image = (spriteNum == 1) ? right1 : right2;
	    }

	    boolean behindObject = gp.cChecker.isPlayerBehindObject(direction);

	    int targetOffset = 0;
	    if (behindObject && "down".equals(direction)) {
	        targetOffset = 20;
	    }

	    // Smoothly move verticalOffset toward targetOffset
	    if (verticalOffset < targetOffset) {
	        verticalOffset += 2.5;  // speed of transition, tweak as needed
	        if (verticalOffset > targetOffset) verticalOffset = targetOffset;
	    } else if (verticalOffset > targetOffset) {
	        verticalOffset -= 2;
	        if (verticalOffset < targetOffset) verticalOffset = targetOffset;
	    }

	    g2.drawImage(image, screenX, screenY + verticalOffset, gp.getTileSize(), gp.getTileSize(), null);
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
	    return worldY;  // <-- was worldX
	}

	public void setPlayerY(int y) {
	    this.worldY = y;
	}

	public int getPlayerX() {
	    return worldX;  // <-- was worldX but your setter below sets worldY by mistake
	}

	public void setPlayerX(int x) {
	    this.worldX = x;  // <-- Fix this. It was setting worldY mistakenly.
	}

}

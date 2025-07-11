package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import Main.GamePanel;

public class Player extends Entity {
    private boolean isHiddenBehindObject = false;
    private boolean applyVerticalOffset = false;
    private String lastDirection = "down";
    public boolean isDownLast;
    
    private GamePanel gp;
    public final int screenX;
    public final int screenY;
    
    // Network multiplayer properties
    private String playerId;
    private boolean isLocalPlayer;
    private int verticalOffset = 0;
    
    // Animation sprites
    private BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

    public Player(GamePanel gp) {
        this.gp = gp;
        
        // Center player on screen
        screenX = gp.getScreenWidth()/2 - (gp.getTileSize()/2);
        screenY = gp.getScreenHeight()/2 - (gp.getTileSize()/2);
        
        // Set collision box
        solidAreaDefaultX = 8;
        solidAreaDefaultY = 16;
        solidArea = new Rectangle(8, 16, 32, 32);
        
        setDefaultValues();
        getPlayerImage();
    }

    // Network multiplayer methods
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public boolean isLocalPlayer() {
        return isLocalPlayer;
    }

    public void setLocalPlayer(boolean localPlayer) {
        isLocalPlayer = localPlayer;
    }

    public void setDefaultValues() {
        this.worldX = gp.getTileSize() * 23;
        this.worldY = gp.getTileSize() * 21;
        this.speed = 4;
        this.direction = "down";
    }

    public void update() {
        if (isLocalPlayer) {
            // Only process input for local player
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
        
        // Update vertical offset for hiding behind objects
        updateVerticalOffset();
    }

    private void updateVerticalOffset() {
        if ("down".equals(direction)) {
            applyVerticalOffset = true;
        } else if (("left".equals(direction) || "right".equals(direction)) && applyVerticalOffset) {
            // keep offset if previously applied
        } else {
            applyVerticalOffset = false;
        }

        // Smooth offset transition
        int targetOffset = (applyVerticalOffset && gp.cChecker.isPlayerBehindObject(direction)) ? 20 : 0;
        
        if (verticalOffset < targetOffset) {
            verticalOffset += 2;
            if (verticalOffset > targetOffset) verticalOffset = targetOffset;
        } else if (verticalOffset > targetOffset) {
            verticalOffset -= 2;
            if (verticalOffset < targetOffset) verticalOffset = targetOffset;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = getCurrentImage();
        int drawY = screenY + verticalOffset;
        
        // For remote players, calculate screen position based on world coordinates
        if (!isLocalPlayer) {
            drawY = gp.worldYToScreenY(worldY) + verticalOffset;
        }
        
        g2.drawImage(image, screenX, drawY, gp.getTileSize(), gp.getTileSize(), null);
    }

    public BufferedImage getCurrentImage() {
        return switch (direction) {
            case "up" -> (spriteNum == 1) ? up1 : up2;
            case "down" -> (spriteNum == 1) ? down1 : down2;
            case "left" -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default -> down1;
        };
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
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // Getters and setters
    public int getPlayerSpeed() {
        return speed;
    }

    public void setPlayerSpeed(int speed) {
        this.speed = speed;
    }

    public int getPlayerY() {
        return worldY;
    }

    public void setPlayerY(int y) {
        this.worldY = y;
    }

    public int getPlayerX() {
        return worldX;
    }

    public void setPlayerX(int x) {
        this.worldX = x;
    }
}
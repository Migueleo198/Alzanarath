package Main;

import entity.Entity;
import java.awt.Rectangle;

public class CollisionChecker {
    private GamePanel gp;
    private final int numLayers;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
        this.numLayers = gp.tileM.mapTileNum.length;
    }

    private void updateSolidArea(Entity entity) {
        entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
        entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;
    }

    private void resetSolidArea(Entity entity) {
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
    }

    public void checkTile(Entity entity) {
        entity.collisionOn = false;
        updateSolidArea(entity);
        
        
        

        // Predict future solid area based on movement
        Rectangle futureArea = new Rectangle(entity.solidArea);
        
        

        switch (entity.direction) {
            case "up" -> futureArea.y -= entity.speed;
            case "down" -> futureArea.y += entity.speed ;
            case "left" -> futureArea.x -= entity.speed;
            case "right" -> futureArea.x += entity.speed;
        }
        
        

        int tileSize = gp.getTileSize();
        int leftCol   = futureArea.x / tileSize;
        int rightCol  = (futureArea.x + futureArea.width) / tileSize;
        int topRow    = futureArea.y / tileSize;
        int bottomRow = (futureArea.y + futureArea.height) / tileSize;

        for (int layer = 0; layer < numLayers; layer++) {
            int[][] map = gp.tileM.mapTileNum[layer];

            // Clamp indices
            leftCol = clamp(leftCol, 0, map.length - 1);
            rightCol = clamp(rightCol, 0, map.length - 1);
            topRow = clamp(topRow, 0, map[0].length - 1);
            bottomRow = clamp(bottomRow, 0, map[0].length - 1);

            int tileNum1, tileNum2;

            switch (entity.direction) {
            case "up":
                tileNum1 = map[leftCol][topRow];
                tileNum2 = map[rightCol][topRow];
                break;
            case "down":
                tileNum1 = map[leftCol][bottomRow];
                tileNum2 = map[rightCol][bottomRow];
                break;
            case "left":
                tileNum1 = map[leftCol][topRow];
                tileNum2 = map[leftCol][bottomRow];
                break;
            case "right":
                tileNum1 = map[rightCol][topRow];
                tileNum2 = map[rightCol][bottomRow];
                break;
            default:
                continue; // This is now valid
        }


            if (isCollisionTile(tileNum1) || isCollisionTile(tileNum2)) {
                entity.collisionOn = true;
                break;
            }
        }

        resetSolidArea(entity);
    }

    private boolean isCollisionTile(int tileNum) {
        return tileNum >= 0 && tileNum < gp.tileM.tiles.length &&
               gp.tileM.tiles[tileNum] != null &&
               gp.tileM.tiles[tileNum].isCollision();
    }
    
    public boolean isPlayerBehindObject(String direction) {
        int tileSize = gp.getTileSize();

        int solidLeft = gp.player.worldX + gp.player.solidArea.x;
        int solidTop = gp.player.worldY + gp.player.solidArea.y;

        int playerTileX = (solidLeft + gp.player.solidArea.width / 2) / tileSize;
        int playerTileY = (solidTop + gp.player.solidArea.height / 2) / tileSize;

        int checkTileX = playerTileX;
        int checkTileY = playerTileY;

        switch (direction) {
            case "up" -> checkTileY = playerTileY - 1;
            case "down" -> checkTileY = playerTileY + 1;
            case "left" -> checkTileX = playerTileX - 1;
            case "right" -> checkTileX = playerTileX + 1;
        }

        // Returns true if near obstacle tiles, false otherwise
        return gp.tileM.isNearCollisionTile();
    }










    public boolean checkPlayer(Entity entity) {
        boolean contactPlayer = false;
        if (gp.player == null) return false;

        updateSolidArea(entity);
        updateSolidArea(gp.player);

        Rectangle futureArea = new Rectangle(entity.solidArea);

        switch (entity.direction) {
            case "up" -> futureArea.y -= entity.speed;
            case "down" -> futureArea.y += entity.speed;
            case "left" -> futureArea.x -= entity.speed;
            case "right" -> futureArea.x += entity.speed;
        }

        if (futureArea.intersects(gp.player.solidArea)) {
            entity.collisionOn = true;
            contactPlayer = true;
        }

        resetSolidArea(entity);
        resetSolidArea(gp.player);

        return contactPlayer;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}


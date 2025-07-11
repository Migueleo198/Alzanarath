package Main;

import entity.Entity;

public class CollisionChecker {
    private GamePanel gp;
    private final int numLayers;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
        this.numLayers = gp.tileM.mapTileNum.length;
    }

    /**
     * Position solidArea relative to world coordinates using default offsets.
     */
    private void updateSolidArea(Entity entity) {
        entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
        entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;
    }

    /**
     * Reset solidArea to its default offsets.
     */
    private void resetSolidArea(Entity entity) {
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
    }

    /**
     * Check collisions between an entity and map tiles across all layers.
     */
    public void checkTile(Entity entity) {
        entity.collisionOn = false;
        updateSolidArea(entity);

        // Predict entity bounds after movement
        int futureLeftX   = entity.solidArea.x + (entity.direction.equals("left")  ? -entity.speed : entity.direction.equals("right") ? entity.speed : 0);
        int futureRightX  = futureLeftX + entity.solidArea.width;
        int futureTopY    = entity.solidArea.y + (entity.direction.equals("up")    ? -entity.speed : entity.direction.equals("down")  ? entity.speed : 0);
        int futureBottomY = futureTopY + entity.solidArea.height;

        // Convert to tile indices
        int leftCol   = futureLeftX   / gp.getTileSize();
        int rightCol  = futureRightX  / gp.getTileSize();
        int topRow    = futureTopY    / gp.getTileSize();
        int bottomRow = futureBottomY / gp.getTileSize();

        // For each layer, check the relevant tiles
        for (int layer = 0; layer < numLayers; layer++) {
            int[][] layerMap = gp.tileM.mapTileNum[layer];

            switch (entity.direction) {
                case "up": {
                    int tileNum1 = layerMap[leftCol][topRow];
                    int tileNum2 = layerMap[rightCol][topRow];
                    if (isCollisionTile(tileNum1) || isCollisionTile(tileNum2)) {
                        entity.collisionOn = true;
                    }
                    break;
                }
                case "down": {
                    int tileNum1 = layerMap[leftCol][bottomRow];
                    int tileNum2 = layerMap[rightCol][bottomRow];
                    if (isCollisionTile(tileNum1) || isCollisionTile(tileNum2)) {
                        entity.collisionOn = true;
                    }
                    break;
                }
                case "left": {
                    int tileNum1 = layerMap[leftCol][topRow];
                    int tileNum2 = layerMap[leftCol][bottomRow];
                    if (isCollisionTile(tileNum1) || isCollisionTile(tileNum2)) {
                        entity.collisionOn = true;
                    }
                    break;
                }
                case "right": {
                    int tileNum1 = layerMap[rightCol][topRow];
                    int tileNum2 = layerMap[rightCol][bottomRow];
                    if (isCollisionTile(tileNum1) || isCollisionTile(tileNum2)) {
                        entity.collisionOn = true;
                    }
                    break;
                }
            }
            if (entity.collisionOn) break;
        }

        resetSolidArea(entity);
    }

    /**
     * Utility to guard against invalid indices and check tile collision flag.
     */
    private boolean isCollisionTile(int tileNum) {
        if (tileNum < 0 || tileNum >= gp.tileM.tile.length) return false;
        return gp.tileM.tile[tileNum] != null && gp.tileM.tile[tileNum].isCollision();
    }

    /**
     * Check whether this entity would collide with the player on movement.
     * Resets the solidAreas after checking.
     */
    public boolean checkPlayer(Entity entity) {
        boolean contactPlayer = false;
        if (gp.player == null) return false;

        updateSolidArea(entity);
        updateSolidArea(gp.player);

        // Move entity's solidArea to future position
        switch (entity.direction) {
            case "up":    entity.solidArea.y -= entity.speed; break;
            case "down":  entity.solidArea.y += entity.speed; break;
            case "left":  entity.solidArea.x -= entity.speed; break;
            case "right": entity.solidArea.x += entity.speed; break;
        }

        if (entity.solidArea.intersects(gp.player.solidArea)) {
            entity.collisionOn = true;
            contactPlayer = true;
        }

        resetSolidArea(entity);
        resetSolidArea(gp.player);
        return contactPlayer;
    }
}

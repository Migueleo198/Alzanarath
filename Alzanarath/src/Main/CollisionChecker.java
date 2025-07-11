package Main;

import entity.Entity;

public class CollisionChecker {
    private GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    private void updateSolidArea(Entity entity) {
        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;
    }

    private void resetSolidArea(Entity entity) {
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
    }

    public void checkTile(Entity entity) {
   
    	// collision for world
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // collision for each tile
        int entityLeftCol = entityLeftWorldX / gp.getTileSize();
        int entityRightCol = entityRightWorldX / gp.getTileSize();
        int entityTopRow = entityTopWorldY / gp.getTileSize();
        int entityBottomRow = entityBottomWorldY / gp.getTileSize();

        int tileNum1, tileNum2;
        int overlap = (entityBottomWorldY + entity.speed) - (entityBottomRow * gp.getTileSize());

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gp.getTileSize();
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision() && overlap > 55) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.getTileSize();
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                overlap = (entityBottomWorldY + entity.speed) - (entityBottomRow * gp.getTileSize());

                if ((gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision()) && overlap > 24) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.getTileSize();
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                overlap = (entityBottomWorldY + entity.speed) - (entityLeftCol * gp.getTileSize());
                                
                if (gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision() && overlap > 24) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gp.getTileSize();
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                overlap = (entityBottomWorldY + entity.speed) - (entityRightCol * gp.getTileSize());
                
                if (gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision() && overlap > 24) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    public boolean checkPlayer(Entity entity) {
        boolean contactPlayer = false;
        if (gp.player != null) {
            updateSolidArea(entity);
            updateSolidArea(gp.player);

            switch (entity.direction) {
                case "up": entity.solidArea.y -= entity.speed; break;
                case "down": entity.solidArea.y += entity.speed; break;
                case "left": entity.solidArea.x -= entity.speed; break;
                case "right": entity.solidArea.x += entity.speed; break;
            }

            if (entity.solidArea.intersects(gp.player.solidArea)) {
                entity.collisionOn = true;
                contactPlayer = true;
            }

            resetSolidArea(entity);
            resetSolidArea(gp.player);
        }
        return contactPlayer;
    }
}

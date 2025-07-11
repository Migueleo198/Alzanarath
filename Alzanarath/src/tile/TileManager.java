package tile;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import Main.GamePanel;

public class TileManager {
    private GamePanel gp;
    public Tile[] tiles;
    // [layer][col][row]
    public int[][][] mapTileNum;
    private final int numLayers = 3;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        loadTileConfig("/tiles/tileset.csv");      // loads and initializes tile[]
        mapTileNum = new int[numLayers][gp.maxWorldCol][gp.maxWorldRow];
        loadAllLayers("/Maps/map_output.txt");
    }

    /**
     * Reads tileset.csv and builds the tile[] array dynamically.
     * CSV columns: index,imagePath,isCollision,isPlayerAbove
     * Blank paths result in no image but collision flags still respected.
     * Images are loaded as BufferedImage and scaled to tile size.
     */
    private void loadTileConfig(String configPath) {
        try (InputStream is = getClass().getResourceAsStream(configPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int maxIndex = -1;
            List<CSVEntry> entries = new ArrayList<>();

            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                int idx = Integer.parseInt(parts[0].trim());
                String path = parts[1].trim();
                boolean coll = Boolean.parseBoolean(parts[2].trim());
                boolean above = Boolean.parseBoolean(parts[3].trim());
                entries.add(new CSVEntry(idx, path, coll, above));
                if (idx > maxIndex) maxIndex = idx;
            }

            tiles = new Tile[maxIndex + 1];

            for (CSVEntry e : entries) {
                Tile t = new Tile();
                if (!e.path.isEmpty()) {
                    try (InputStream imgStream = getClass().getResourceAsStream(e.path)) {
                        BufferedImage raw = ImageIO.read(imgStream);
                        if (raw != null) {
                            BufferedImage scaled = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2 = scaled.createGraphics();
                            g2.drawImage(raw, 0, 0, gp.getTileSize(), gp.getTileSize(), null);
                            g2.dispose();
                            t.image = scaled;
                        }
                    }
                }
                t.setCollision(e.coll);
                t.setPlayerAbove(e.above);
                tiles[e.index] = t;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static class CSVEntry {
        int index;
        String path;
        boolean coll;
        boolean above;
        CSVEntry(int i, String p, boolean c, boolean a) {
            index = i; path = p; coll = c; above = a;
        }
    }

    /**
     * Loads all layers from a single file with sections marked by "# Layer N" headers.
     */
    public void loadAllLayers(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            int currentLayer = -1;
            int row = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    // just skip blank separators
                    continue;
                }

                if (line.startsWith("#")) {
                    // new layer header
                    // format: "# Layer N"
                    String[] parts = line.split("\\s+");
                    currentLayer = Integer.parseInt(parts[2]);
                    row = 0;
                    continue;
                }

                // only attempt to parse data once we've seen a header
                if (currentLayer < 0 || currentLayer >= numLayers) {
                    continue;
                }

                String[] nums = line.split("\\s+");
                for (int col = 0; col < nums.length && col < gp.maxWorldCol; col++) {
                    mapTileNum[currentLayer][col][row] = Integer.parseInt(nums[col]);
                }
                row++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if the given tile or its immediate neighbors contain any obstacle/collision tiles.
     * Returns true if any collision tiles are found nearby, otherwise false.
     */
    public boolean isNearCollisionTile() {
        int tileSize = gp.getTileSize();
        int margin = tileSize / 2;

        Rectangle playerRect = new Rectangle(
            gp.player.worldX + gp.player.solidArea.x - margin,
            gp.player.worldY + gp.player.solidArea.y - margin,
            gp.player.solidArea.width + margin * 2,
            gp.player.solidArea.height + margin * 2
        );

        int leftTile = Math.max(0, playerRect.x / tileSize);
        int rightTile = Math.min(gp.maxWorldCol - 1, (playerRect.x + playerRect.width) / tileSize);
        int topTile = Math.max(0, playerRect.y / tileSize);
        int bottomTile = Math.min(gp.maxWorldRow - 1, (playerRect.y + playerRect.height) / tileSize);

        for (int col = leftTile; col <= rightTile; col++) {
            for (int row = topTile; row <= bottomTile; row++) {
                for (int layer = 0; layer < numLayers; layer++) {
                    int tileNum = mapTileNum[layer][col][row];
                    if (tileNum < 0 || tileNum >= tiles.length) continue;

                    Tile tile = tiles[tileNum];
                    if (tile != null && tile.isCollision()) {
                        Rectangle tileRect = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);
                        if (playerRect.intersects(tileRect)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }








    public void drawLayered(Graphics2D g2) {
        int tileSize = gp.getTileSize();
        int playerRow = gp.player.worldY / tileSize;

        // Draw layer 0 fully (always below player)
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                int tNum = mapTileNum[0][col][row];
                if (tNum < 0 || tNum >= tiles.length) continue;
                Tile t = tiles[tNum];
                if (t == null) continue;

                int worldX = col * tileSize;
                int worldY = row * tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                if (isOnScreen(worldX, worldY)) {
                    g2.drawImage(t.image, screenX, screenY, tileSize, tileSize, null);
                }
            }
        }

        // Now draw layers 1 and above with player layered by Y position
        for (int row = 0; row < gp.maxWorldRow; row++) {
            // First draw tiles from layers 1+ that are above the player vertically (worldY < player.worldY)
            for (int layer = 1; layer < numLayers; layer++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    int tNum = mapTileNum[layer][col][row];
                    if (tNum < 0 || tNum >= tiles.length) continue;
                    Tile t = tiles[tNum];
                    if (t == null) continue;

                    int worldX = col * tileSize;
                    int worldY = row * tileSize;
                    int screenX = worldX - gp.player.worldX + gp.player.screenX;
                    int screenY = worldY - gp.player.worldY + gp.player.screenY;

                    if (isOnScreen(worldX, worldY) && worldY < gp.player.worldY) {
                        g2.drawImage(t.image, screenX, screenY, tileSize, tileSize, null);
                    }
                }
            }

            // Draw player on their current row
            if (row == playerRow) {
                gp.player.draw(g2);
            }

            // Now draw tiles from layers 1+ that are below or equal to the player vertically (worldY >= player.worldY)
            for (int layer = 1; layer < numLayers; layer++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    int tNum = mapTileNum[layer][col][row];
                    if (tNum < 0 || tNum >= tiles.length) continue;
                    Tile t = tiles[tNum];
                    if (t == null) continue;

                    int worldX = col * tileSize;
                    int worldY = row * tileSize;
                    int screenX = worldX - gp.player.worldX + gp.player.screenX;
                    int screenY = worldY - gp.player.worldY + gp.player.screenY;

                    if (isOnScreen(worldX, worldY) && worldY >= gp.player.worldY) {
                        g2.drawImage(t.image, screenX, screenY, tileSize, tileSize, null);
                    }
                }
            }
        }
    }

    private boolean isOnScreen(int worldX, int worldY) {
        return worldX + gp.getTileSize() > gp.player.worldX - gp.player.screenX &&
               worldX - gp.getTileSize() < gp.player.worldX + gp.player.screenX &&
               worldY + gp.getTileSize() > gp.player.worldY - gp.player.screenY &&
               worldY - gp.getTileSize() < gp.player.worldY + gp.player.screenY;
    }


    private void draw(Graphics2D g2, boolean drawAbove) {
        for (int layer = 0; layer < numLayers; layer++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    int tNum = mapTileNum[layer][col][row];
                    if (tNum < 0 || tNum >= tiles.length) continue;
                    Tile t = tiles[tNum];
                    if (t == null || t.isPlayerAbove() != drawAbove) continue;

                    int worldX = col * gp.getTileSize();
                    int worldY = row * gp.getTileSize();
                    int screenX = worldX - gp.player.worldX + gp.player.screenX;
                    int screenY = worldY - gp.player.worldY + gp.player.screenY;

                    // cull off-screen
                    if (worldX + gp.getTileSize() > gp.player.worldX - gp.player.screenX &&
                        worldX - gp.getTileSize() < gp.player.worldX + gp.player.screenX &&
                        worldY + gp.getTileSize() > gp.player.worldY - gp.player.screenY &&
                        worldY - gp.getTileSize() < gp.player.worldY + gp.player.screenY) {

                        g2.drawImage(t.image, screenX, screenY,
                                     gp.getTileSize(), gp.getTileSize(), null);
                    }
                }
            }
        }
    }
}

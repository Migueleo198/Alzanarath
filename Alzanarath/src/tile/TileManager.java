package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class TileManager {
	
	GamePanel gp;
	public Tile tile[];
	public int mapTileNum[][];
	
	
	public TileManager(GamePanel gp){
		this.gp=gp;
		tile = new Tile[10];
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
		
		getTileImage();
		loadMap();
		
	}
	
	public void getTileImage() {
	    try (InputStream is = getClass().getResourceAsStream("/tiles/tileset.csv");
	         BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

	        String line;

	        while ((line = br.readLine()) != null) {
	            line = line.trim();
	            if (line.isEmpty() || line.startsWith("index")) continue;

	            String[] parts = line.split(",");
	            
	            int index = Integer.parseInt(parts[0].trim());
	            String pathImage = parts[1].trim();
	            boolean isCollision = Boolean.parseBoolean(parts[2].trim().toLowerCase());
	            boolean isPlayerAbove = Boolean.parseBoolean(parts[3].trim().toLowerCase());

	            tile[index] = new Tile();
	            tile[index].image = ImageIO.read(getClass().getResourceAsStream(pathImage));
	            tile[index].setCollision(isCollision);
	            tile[index].setPlayerAbove(isPlayerAbove);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	
	public void loadMap() {
		try {
			InputStream is = getClass().getResourceAsStream("/Maps/map_with_houses_and_trees.txt");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			
			while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
				String line = br.readLine();
				
				while(col < gp.maxWorldCol) {
					String numbers[] = line.split(" ");
					
					int num = Integer.parseInt(numbers[col]);
					
					mapTileNum[col][row] = num;
					col++;
					
					
				}
				
				if(col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}
			br.close();
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void drawBackground(Graphics2D g2) {
	    draw(g2, false); // Draw all tiles BELOW player
	}

	public void drawForeground(Graphics2D g2) {
	    draw(g2, true); // Draw all tiles ABOVE player
	}
	
	public void draw(Graphics2D g2, boolean drawAbove) {
		
		int worldCol = 0;
		int worldRow = 0;
		
		while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
			
			int tileNum = mapTileNum[worldCol][worldRow];
			
			int worldX = worldCol * gp.getTileSize();
			int worldY = worldRow * gp.getTileSize();
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;
			
			if(worldX + gp.getTileSize() > gp.player.worldX - gp.player.screenX &&
			   worldX - gp.getTileSize() < gp.player.worldX + gp.player.screenX &&
			   worldY + gp.getTileSize() > gp.player.worldY - gp.player.screenY &&
			   worldY - gp.getTileSize() < gp.player.worldY + gp.player.screenY) {
				
	            if (tile[tileNum].isPlayerAbove() == drawAbove) {
	                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
	            }				
			}
				
			
			worldCol++;
			
			
			if(worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow+=1;
			}
			
		}
		
	}
}

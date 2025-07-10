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
		
		try {
			
			tile[0] = new Tile();
			tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/5Grass.png"));
			
			
			tile[1] = new Tile();
			tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/7Tree.png"));
			tile[1].setCollision(true);
			
			tile[2] = new Tile();
			tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0wallTile.png"));
			tile[2].setCollision(true);
			
			tile[3] = new Tile();
			tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/8WoodenFloor.png"));
			
			
		}catch(IOException e){
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
	
	public void draw(Graphics2D g2) {
		
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
				
				g2.drawImage(tile[tileNum].image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
				
			}
				
			
			worldCol++;
			
			
			if(worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow+=1;
			}
			
		}
		
	}
}

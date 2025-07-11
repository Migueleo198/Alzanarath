package tile;

import java.awt.image.BufferedImage;

public class Tile {
	public BufferedImage image;
	private boolean collision = false;
	private boolean playerAbove = false;
	public boolean isCollision() {
		return collision;
	}
	public void setCollision(boolean collision) {
		this.collision = collision;
	}
	public boolean isPlayerAbove() {
		return playerAbove;
	}
	public void setPlayerAbove(boolean playerAbove) {
		this.playerAbove = playerAbove;
	}
}

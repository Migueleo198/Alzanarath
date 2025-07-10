package Main;

import javax.swing.JFrame;

public class main {
	
	public static void main(String[] args){
		
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		window.setTitle("Alzanarath Game");
		
		GamePanel gp = new GamePanel();
		
		window.add(gp);
		window.pack();
		
		gp.startGameThread();
		gp.setupGame();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}

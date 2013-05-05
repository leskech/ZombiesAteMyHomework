import java.awt.BorderLayout;

import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;


public class Game {
	
	public static void main(String[] args){
		
	    //GLCapabilities capabilities = createGLCapabilities();
	    MenuScreen m = new MenuScreen(800, 500);
		
		//Hierarchical h = new Hierarchical();
		
		//statistics
		int killed = 0; //how many zombies have been killed so far
		
		Player thePlayer = new Player(50,50); //character is created
		
		//main game loop
		while(thePlayer.isAlive()){
			
		}
	}
	
	public void menuScreen(){
		
	}
}

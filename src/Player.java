
public class Player {
	
	int x; //location on map
	int y; //location on map
	
	boolean life; //if player is alive
	Weapon w; //current equipped weapon
	
	public Player(int x, int y){ //default player constructor
		life = true;
		this.x = x;
		this.y = y;
		w = new Weapon();
	}
	
	public boolean isAlive(){
		return life;
	}

}

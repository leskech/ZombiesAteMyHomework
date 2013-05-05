
public class PowerUp {
	public enum Power {
		INVINCIBLE, AMMO;
	}
	
	Power p;
	
	int x; //location on map
	int y; //location on map
	
	public PowerUp(int x, int y){
		this.x = x;
		this.y = y;
	}

}

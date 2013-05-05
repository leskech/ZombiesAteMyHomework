
public class Weapon {
	public enum Current {
		HANDGUN, SHOTGUN, BOMB;
	}
	
	Current w;
	int rate; //speed at which weapon can be fired
	int ammo; //remaining ammo
	
	//constructs default weapon configuration
	public Weapon (){
		w = Current.HANDGUN;
		update();
	}
	
	//fire the weapon
	public int fire(){
		if(ammo>0){
			ammo--;
			return 1;
		}
		
		return 0;
	}
	
	//updates the graphic and details of weapon
	//uses different functions based on what is now equipped
	public void update(){
		if(w == Current.HANDGUN){
			ammo = 100;
		}
		if(w == Current.SHOTGUN){
			ammo = 20;
		}
		if(w == Current.BOMB){
			ammo = 1;
		}
	}
	
	//called to switch which weapon this is
	public void change(String s){
		if(s=="HANDGUN"){
			w = Current.HANDGUN;
		}
		if(s=="SHOTGUN"){
			w = Current.SHOTGUN;
		}
		if(s=="BOMB"){
			w = Current.BOMB;
		}
		update();
	}
}

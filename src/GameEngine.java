//a lot of the setup code comes from online examples of how to set up lwjgl a certain way from the open source website. 
//Besides that stuff, the code is written by Mike Stevens and Kevin Rizza
//Includes outside libraries included in the utility package, 
//namely Eulercamera and Model, which make the manipulation of model matrices and the loading of .obj files more friendly


import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColorMaterial;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLightModel;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Vector;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

//import actions.ActionDemo;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

import test.TestUtils;
import utility.BufferTools;
import utility.Camera;
import utility.EulerCamera;
import utility.Model;
import utility.OBJLoader;

/**
 * Top down shooter, of zombies.
 *
 * @author Mike Stevens & Kevin Rizza
 */
public class GameEngine {

	
	
    private static final String WINDOW_TITLE = "ZombiesAteMyHomework";
    private static final int[] WINDOW_DIMENSIONS = {800, 600};
    long lastFrame;//time at last frame        
	float x, z, d, wx, wz;//position of player	
	int fps;//frames per second
	long lastFPS;//fps at last frame
	private static int playerDisplayList;
	private static int bunnyDisplayList;
	private static int birdyDisplayList;

    private static String playerPath="obj/malefromabove.obj";
    private static String bunnyPath="obj/bunny1.obj";
    private static String birdyPath="obj/birdy.obj";
    private static Camera playerCam;
   // private static Camera enemyCam;
    private static Vector<Zombie> enemies;
    private static boolean playerDead;
    
    private static Vector<Weapon> weapons;
    private static Powerup powerup;
    
    private final int totalEnemy=100;
    private static int killed=0;
    private static int lastkilled;
    
    private static Music game;
    private static Sound gun;
    private static Sound zombieDie;
    
    private Zombie zombo;
    
    private int powerups;
    private static int waves=20;
    
    
    static double angle;
	private float[] distance=new float[50];
	private float dist=0;
	private final double PI = 3.14159265358979323846;
	private static int tick, powerupTick;
	private static boolean canShoot;
	
	private static boolean enablePowerup;
	private static boolean powerUpActive;
	
	
	
	
    private void render() {    	
    	 glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);         
         glLoadIdentity();              
         moveDude();//move player according to input        
         GL11.glScalef(.1f, .1f, .1f);//scale dude    
         renderDude();//render dude to screen      
         genPowerup();//see if a powerup procs
         updateWeapons();
         updateEnemies();
         checkCollisions();         
         generateEnemies();
    }

    
    private void generateEnemies(){
    	 if(killed > lastkilled){
        	 for(int i=killed-lastkilled; i>0;i--)enemyGenerator(1, waves);        	 
        	 lastkilled=killed;       	 
        	 if(killed%50 < 5)waves+=3;        	 
         }   	
    }    
    private void checkCollisions(){
    	for(Zombie z:enemies){//loop through enemies and weapons to check for collisions   
       	 if(collided(z.z,playerCam) && !playerDead && !z.isDead){playerDead=true;}//if collision with enemy, player dead        	 
       	 
	       	 for(Weapon w:weapons){//if zombie collision with weapon, both die
	       		 if(collided(w.w,z.z) && !z.isDead && !w.hitTarget){
	       			 zombieDie.play();
	       			 killed++;
	       			 w.hitTarget=true;
	       			 z.isDead=true;
	       			 powerupChance(z);		 
	       		 }       		 
	       	 }        	 
        }   	
    }   
    private void renderPowerup(){    	
    	GL11.glColor3f(1f,0.5f,0f);    	
    	glCallList(birdyDisplayList); 
    	
    }
    private void updateWeapons(){
    	for(Weapon w:weapons){//render and move each shot
       	 if(w.w.tick > 0 && !w.hitTarget){
	        	 glLoadIdentity();       	 
	        	 moveWeapon(w);
	        	 GL11.glScalef(.01f, .01f, .01f);
	             renderWeapon();  
       	 }
        }   	
    }
    
    private void updateEnemies(){
    	for(Zombie z:enemies){//render and move each enemy
       	 if(!z.isDead){
	        	 glLoadIdentity();       	 
	        	 moveEnemy(z.z);
	        	 GL11.glScalef(.1f, .1f, .1f);
	             renderEnemy(); 
            }      	 
        }   	
    }    
    private void renderDude(){    
    	GL11.glColor3f(0.5f,0.5f,1.0f);    	
    	glCallList(playerDisplayList);  
   }    
    private void moveDude(){
    	playerCam.setPosition(x,playerCam.y(),z);
        playerCam.applyTranslations();
    }    
    private void renderWeapon(){
    	GL11.glColor3f(1f, 0f, 0.0f); 
    	glCallList(bunnyDisplayList);      	
    }
    
    private void moveWeapon(Weapon w){   	
    	if(w.wxright && w.w.tick > 0)w.w.setPosition(w.w.x()-1, w.w.y(), w.w.z());
    	if(w.wxleft&& w.w.tick > 0)w.w.setPosition(w.w.x()+1, w.w.y(), w.w.z());    	
    	if(w.wzup&& w.w.tick > 0)w.w.setPosition(w.w.x(), w.w.y(), w.w.z()+1);
    	if(w.wzdown&& w.w.tick > 0)w.w.setPosition(w.w.x(), w.w.y(), w.w.z()-1);    		
    	w.w.applyTranslations();   	
    }    
    
 private void renderEnemy(){    	     	
    	GL11.glColor3f(0.0f, 1.2f, 0.0f);       	
    	glCallList(playerDisplayList);     	
    }    
 
 private void enemyGenerator(int enemyct, float offscreen){	//generate 4 enemies for every 1 killed, eventually overrun
	 for(int i=0;i<enemyct;i++){
		 enemies.add(new Zombie((EulerCamera) setUpCameraEnemy((float)(-20f*Math.random()*1-offscreen), (float)(-20f*Math.random()*1-offscreen)), false));
		 enemies.add(new Zombie((EulerCamera)setUpCameraEnemy((float)(-20f*Math.random()*1-offscreen), (float)(20f*Math.random()*1+offscreen)), false));
		 enemies.add(new Zombie((EulerCamera)setUpCameraEnemy((float)(20f*Math.random()*1+offscreen), (float)(-20f*Math.random()*1-offscreen)), false));
		 enemies.add(new Zombie((EulerCamera)setUpCameraEnemy((float)(20f*Math.random()*1+offscreen), (float)(20f*Math.random()*1+offscreen)), false));
	 } 
 }
 
    private void moveEnemy(Camera cam){//move in the direction of the player, placeholder for future AI
    	float playerx=playerCam.x();
    	float playerz=playerCam.z();
    	    	
    	float enemyx=cam.x();
    	float enemyz=cam.z();
    	
    	if(playerx > enemyx)enemyx+=.02f;
    	else enemyx-=.02f;
    	
    	if(playerz > enemyz)enemyz+=.02f;
    	else enemyz-=.02f;   	
    	
    	cam.setPosition(enemyx, cam.y(), enemyz);   	
    	
    	cam.applyTranslations();
    }
    
    
    private void shotTimerTick(){
    	if(tick <=0 )canShoot=true;
    	else{  tick--;}   	
    }
    private void weaponTick(Weapon w){
    	if(w.w.tick <=0){    		
    		w.wzup=false;
            w.wzdown=false;
            w.wxleft=false;
            w.wxright=false;   		
    		w.w.tick=0;
    	}else{    		
    		w.w.tick--;    		
    	}
    }

    private void powerupChance(Zombie z){
    	double chance;
    	chance=Math.random()*10;
    	
    	if(chance>=8){enablePowerup=true;
    	zombo=z;    	
    	}
    	else enablePowerup=false;
    }
    
   private void genPowerup(){
	    if(enablePowerup && !powerUpActive && zombo!=null){  		   	
		   	 glLoadIdentity();
		   	 powerup=new Powerup(makePowerupCam(zombo.z.x(), zombo.z.z()));   	 
		   	 powerup.p.applyTranslations(); 
		   	 GL11.glScalef(.1f, .1f, .1f);
		   	 renderPowerup();		 	
		   	 enablePowerup=false;
		   	 powerUpActive=true;
	    }
	    if(powerUpActive){
	    	 glLoadIdentity();
	    	 
	    	powerup.p.applyTranslations(); 
	    	GL11.glScalef(.1f, .1f, .1f);
	      	 renderPowerup(); 	
	    }if(powerUpActive==true &&   powerup != null && collided(powerup.p, playerCam)){
	    	if(powerups<40)powerups+=10;
	    	powerUpActive=false;
	    	enablePowerup=false;    	
	    }
    }
    
    
    private void input(int delta) {
    	  x=playerCam.x();
          z=playerCam.z();
          
          
         for(Weapon w:weapons)       	 
        	 weaponTick(w);
         shotTimerTick();
          
          
          
          if (Keyboard.isKeyDown(Keyboard.KEY_UP) && canShoot){
        	  Weapon wep = new Weapon((EulerCamera) makeWeaponCam());       
        	  wep.hitTarget=false;
        	  wep.wzup=true; 
        	  wep.w.tick=70;
        	  weapons.add(wep);
        	  tick=50-powerups;
        	  canShoot= false;
        	  gun.play();}
          if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)&& canShoot){
        	  Weapon wep = new Weapon((EulerCamera) makeWeaponCam());
        	  wep.hitTarget=false;
        	  wep.wxleft=true;         	 
        	  wep.w.tick=70;
        	  weapons.add(wep);
        	  tick=50-powerups;
        	  canShoot= false;
        	  gun.play();}
          if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)&& canShoot){
        	  Weapon wep = new Weapon((EulerCamera) makeWeaponCam());wep.hitTarget=false;
        	  wep.wxright=true;         	 
        	  wep.w.tick=70;
        	  weapons.add(wep);
        	  tick=50-powerups;
        	  canShoot= false;
        	  gun.play();}
          if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)&& canShoot){
        	  Weapon wep = new Weapon((EulerCamera) makeWeaponCam());wep.hitTarget=false;
        	  wep.wzdown=true;         	 
        	  wep.w.tick=70;
        	  weapons.add(wep);
        	  tick=50-powerups;
        	  canShoot= false;
        	  gun.play();}
          
          
       
    	if (Keyboard.isKeyDown(Keyboard.KEY_A))x += 0.01f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_D))x -= 0.01f * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))z += 0.01f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_S))z -= 0.01f * delta;
	
		
		if(Keyboard.isKeyDown(Keyboard.KEY_1))System.out.println("Camera Angle:"+playerCam.pitch() +" "+playerCam.yaw() + " "+playerCam.roll());
		if(Keyboard.isKeyDown(Keyboard.KEY_2))System.out.println("Camera Position:"+playerCam.x()	+" "+playerCam.y() + " "+playerCam.z());	
		if(Keyboard.isKeyDown(Keyboard.KEY_3))System.out.println("Player Position:"+playerCam.x()	+" "+playerCam.y() + " "+playerCam.z());	
		
		angle=getAngleBetween(x,z,Mouse.getX(), Mouse.getY());
		
		//if(Mouse.isButtonDown(0))d++;
		//if(Mouse.isButtonDown(1))d--;	
		// playerCam.processMouse(1, 80, -80);
	    //    enemyCam.processKeyboard(getTimeElapsed()+1, 50, 0, 50);
	     
			//if (Mouse.isButtonDown(0)) {
	      //      Mouse.setGrabbed(true);
	       // } else if (Mouse.isButtonDown(1)) {
	        //    Mouse.setGrabbed(false);
	       // }
	     
    }

    private  void cleanUp(boolean asCrash) {
    	glDeleteLists(playerDisplayList, 1);
        Display.destroy();
        System.exit(asCrash ? 1 : 0);
    }

    
    private  void setUpCameraPlayer() {
        playerCam = new EulerCamera.Builder().setAspectRatio((float) Display.getWidth() / Display.getHeight())
                .setRotation(-1.12f, 0.16f, 0f).setPosition(-1.38f, 1.36f, 7.95f).setFieldOfView(60).build();
        playerCam.applyOptimalStates();
        playerCam.applyPerspectiveMatrix();
        playerCam.setPosition(0.f, 30f, 0.f);
        playerCam.setRotation(100f,0.16057983f, 0);  //80.0 0.16057983 0.0: pitch, yaw, roll:we want to be looking down
      
       // playerCam.applyPerspectiveMatrix();
       // playerCam.setFieldOfView(120f);
    }
    
    
    private  Camera setUpCameraEnemy(float f, float g) {
        EulerCamera enemyCam = new EulerCamera.Builder().setAspectRatio((float) Display.getWidth() / Display.getHeight())
                .setRotation(-1.12f, 0.16f, 0f).setPosition(-1.38f, 1.36f, 7.95f).setFieldOfView(60).build();
        enemyCam.applyOptimalStates();
        enemyCam.applyPerspectiveMatrix();
        enemyCam.setPosition(f, 30f, g);
        enemyCam.setRotation(100f,0.16057983f, 0);  //80.0 0.16057983 0.0: pitch, yaw, roll:we want to be looking down
        return enemyCam;
       
    }
    
    
    
    private  Camera makePowerupCam(float f, float g) {
        EulerCamera enemyCam = new EulerCamera.Builder().setAspectRatio((float) Display.getWidth() / Display.getHeight())
                .setRotation(-1.12f, 0.16f, 0f).setPosition(-1.38f, 1.36f, 7.95f).setFieldOfView(60).build();
        enemyCam.applyOptimalStates();
        enemyCam.applyPerspectiveMatrix();
        enemyCam.setPosition(f, 30f, g);
        enemyCam.setRotation(100f,0.16057983f, 0);  //80.0 0.16057983 0.0: pitch, yaw, roll:we want to be looking down
        return enemyCam;       
    }
    
    
    
    
    
    private  Camera makeWeaponCam() {
    	Camera weaponCam = new EulerCamera.Builder().setAspectRatio((float) Display.getWidth() / Display.getHeight())
                .setRotation(-1.12f, 0.16f, 0f).setPosition(-1.38f, 1.36f, 7.95f).setFieldOfView(60).build();
        weaponCam.applyOptimalStates();
        weaponCam.applyPerspectiveMatrix();
        weaponCam.setPosition(playerCam.x(), 30f, playerCam.z());
        weaponCam.setRotation(100f,0.16057983f, 0);  //80.0 0.16057983 0.0: pitch, yaw, roll:we want to be looking down
        return weaponCam;
       // playerCam.applyPerspectiveMatrix();
       // playerCam.setFieldOfView(120f);
    }
    
    
    
    private void setUpMatrices() {
    	// Setup projection matrix
    			
    }

    
    private  void setUpStates() {    
    	angle=0; tick=0; powerupTick=50;  canShoot=true; playerDead=false;enablePowerup=false;powerUpActive=false;zombo=null;powerups=0;powerup=null;killed=0;
    	waves=20;
    	
    	weapons=new Vector<Weapon>();
    	
    	enemies=new Vector<Zombie>();
       
    }

    private void update() {
    	updateFPS(); // update FPS Counter
        Display.update();
        Display.sync(60);    
        
    }

    private void enterGameLoop(GameEngine engine) throws LWJGLException, IOException {
    	
    	
		getTimeElapsed(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer
		
        while (!Display.isCloseRequested()) {
        	if(!playerDead){
        	int delta = getTimeElapsed();           	
        	render();
            input(delta);
            update();
    	}else{
    		game.stop();
            initGUI(engine);
            setUpStates();
            setUpDisplayListPlayer();
            setUpDisplayListBunny();
            //engine.setUpLighting();
            setUpMatrices();
            setUpCameraPlayer();
            game.loop();
            enemyGenerator(5, waves);
    	}
    	}
    }

    
    private  void setUpDisplayListPlayer() {
       playerDisplayList = glGenLists(1);
       
        glNewList(playerDisplayList, GL_COMPILE);
        {
            Model m = null;
            try {
                m = OBJLoader.loadModel(new File(playerPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Display.destroy();
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                Display.destroy();
                System.exit(1);
            }
            glBegin(GL_TRIANGLES);
            for (Model.Face face : m.getFaces()) {
                Vector3f n1 = m.getNormals().get(face.getNormalIndices()[0] - 1);
                glNormal3f(n1.x, n1.y, n1.z);
                Vector3f v1 = m.getVertices().get(face.getVertexIndices()[0] - 1);
                glVertex3f(v1.x, v1.y, v1.z);
                Vector3f n2 = m.getNormals().get(face.getNormalIndices()[1] - 1);
                glNormal3f(n2.x, n2.y, n2.z);
                Vector3f v2 = m.getVertices().get(face.getVertexIndices()[1] - 1);
                glVertex3f(v2.x, v2.y, v2.z);
                Vector3f n3 = m.getNormals().get(face.getNormalIndices()[2] - 1);
                glNormal3f(n3.x, n3.y, n3.z);
                Vector3f v3 = m.getVertices().get(face.getVertexIndices()[2] - 1);
                glVertex3f(v3.x, v3.y, v3.z);
            }
            glEnd();
        }
        glEndList();
    }
    
    
    private  void setUpDisplayListBunny() {//heavily uses code from OBJLoader
    	bunnyDisplayList = glGenLists(1);
        
         glNewList(bunnyDisplayList, GL_COMPILE);
         {
             Model m = null;
             try {
                 m = OBJLoader.loadModel(new File(bunnyPath));
             } catch (FileNotFoundException e) {
                 e.printStackTrace();
                 Display.destroy();
                 System.exit(1);
             } catch (IOException e) {
                 e.printStackTrace();
                 Display.destroy();
                 System.exit(1);
             }
             glBegin(GL_TRIANGLES);
             for (Model.Face face : m.getFaces()) {
                 Vector3f n1 = m.getNormals().get(face.getNormalIndices()[0] - 1);
                 glNormal3f(n1.x, n1.y, n1.z);
                 Vector3f v1 = m.getVertices().get(face.getVertexIndices()[0] - 1);
                 glVertex3f(v1.x, v1.y, v1.z);
                 Vector3f n2 = m.getNormals().get(face.getNormalIndices()[1] - 1);
                 glNormal3f(n2.x, n2.y, n2.z);
                 Vector3f v2 = m.getVertices().get(face.getVertexIndices()[1] - 1);
                 glVertex3f(v2.x, v2.y, v2.z);
                 Vector3f n3 = m.getNormals().get(face.getNormalIndices()[2] - 1);
                 glNormal3f(n3.x, n3.y, n3.z);
                 Vector3f v3 = m.getVertices().get(face.getVertexIndices()[2] - 1);
                 glVertex3f(v3.x, v3.y, v3.z);
             }
             glEnd();
         }
         glEndList();
     }
    
    private  void setUpDisplayListBirdy() {
    	birdyDisplayList = glGenLists(1);        
         glNewList(birdyDisplayList, GL_COMPILE);
         {
             Model m = null;
             try {
                 m = OBJLoader.loadModel(new File(birdyPath));
             } catch (FileNotFoundException e) {
                 e.printStackTrace();
                 Display.destroy();
                 System.exit(1);
             } catch (IOException e) {
                 e.printStackTrace();
                 Display.destroy();
                 System.exit(1);
             }
             glBegin(GL_TRIANGLES);
             for (Model.Face face : m.getFaces()) {
                 Vector3f n1 = m.getNormals().get(face.getNormalIndices()[0] - 1);
                 glNormal3f(n1.x, n1.y, n1.z);
                 Vector3f v1 = m.getVertices().get(face.getVertexIndices()[0] - 1);
                 glVertex3f(v1.x, v1.y, v1.z);
                 Vector3f n2 = m.getNormals().get(face.getNormalIndices()[1] - 1);
                 glNormal3f(n2.x, n2.y, n2.z);
                 Vector3f v2 = m.getVertices().get(face.getVertexIndices()[1] - 1);
                 glVertex3f(v2.x, v2.y, v2.z);
                 Vector3f n3 = m.getNormals().get(face.getNormalIndices()[2] - 1);
                 glNormal3f(n3.x, n3.y, n3.z);
                 Vector3f v3 = m.getVertices().get(face.getVertexIndices()[2] - 1);
                 glVertex3f(v3.x, v3.y, v3.z);
             }
             glEnd();
         }
         glEndList();
     }
    
    
    private boolean collided(Camera w, Camera e){
    	float weaponX=w.x();
    	float weaponZ=w.z();
    	
    	float enemyX=e.x();
    	float enemyZ=e.z();
    	   	
    	if(weaponX - enemyX > 1  || weaponX-enemyX <-1 )return false;
    	if(weaponZ - enemyZ > 1 || weaponZ-enemyZ <-1 )return false;
    	
    	return true;
    	
    }
    private  void setUpDisplay() {
    	try {        
            Display.setDisplayMode(new DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
            Display.setVSyncEnabled(true);
            Display.setTitle(WINDOW_TITLE);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            cleanUp(true);
        }
    }
    
    
    public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}    
	public void initGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
    
    public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
    
    public int getTimeElapsed() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
	    return delta;
	}
    
	private float degreesToRadians(float degrees) {
		return degrees * (float)(PI / 180d);
	}
    public double getAngleBetween(double x1, double z1, double x2, double z2){
		return(Math.atan2(z1-z2, x1-x2)*57.2957795);
	}
	private float coTangent(float angle) {
		return (float)(1f / Math.tan(angle));
	}
	
	
	
	 private  void setUpLighting() {
	        glShadeModel(GL_SMOOTH);
	        glEnable(GL_DEPTH_TEST);
	        glEnable(GL_LIGHTING);
	        glEnable(GL_LIGHT0);
	        glLightModel(GL_LIGHT_MODEL_AMBIENT, BufferTools.asFlippedFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
	        glLight(GL_LIGHT0, GL_POSITION, BufferTools.asFlippedFloatBuffer(new float[]{0, 0, 0, 1}));
	        glEnable(GL_CULL_FACE);
	        glCullFace(GL_BACK);
	        glEnable(GL_COLOR_MATERIAL);
	        glColorMaterial(GL_FRONT, GL_DIFFUSE);
	 }
	
	public static void initGUI(GameEngine engine) throws LWJGLException, IOException{
		String XML;
		
		if(!playerDead){
			XML = "/test/menu.xml";
		}else{
			XML = "/test/menu.xml";
		}
		
    	LWJGLRenderer renderer = new LWJGLRenderer();
        Menu demo = new Menu(engine);
        GUI gui = new GUI(demo, renderer);
        
        demo.requestKeyboardFocus();

        ThemeManager theme = ThemeManager.createThemeManager(
                Menu.class.getResource(XML), renderer);
        gui.applyTheme(theme);
        while(!demo.start) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            gui.update();
            Display.update();
            TestUtils.reduceInputLag();
        }

        gui.destroy();
        theme.destroy();
		
    }
	
	public void initSound() throws SlickException{
		gun = new Sound("sound/sub_machine_gun_single_shot.wav");
		zombieDie = new Sound("sound/zombie_groan_002.wav");
		game = new Music("sound/528136_Redux.wav");
		game.loop();
	}
	
	public String getEnemiesKilled(){
		return "PREVIOUS SCORE: " + killed;
	}    

    public static void main(String[] args) throws LWJGLException, IOException, SlickException {
    	GameEngine engine = new GameEngine();    	
        engine.setUpDisplay();
        initGUI(engine);
        engine.setUpStates();
        engine.setUpDisplayListPlayer();
        engine.setUpDisplayListBunny();
        engine.setUpDisplayListBirdy();
       // engine.setUpLighting();
        engine.setUpMatrices();
        engine.setUpCameraPlayer();
        engine.initSound();
        engine.enemyGenerator(5, waves);
        engine.enterGameLoop(engine);
        engine.cleanUp(false);
    }
    
}
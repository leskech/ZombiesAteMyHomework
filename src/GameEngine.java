
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glNormal3f;
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
    private static String playerPath="obj/malefromabove.obj";
    private static Camera playerCam;
   // private static Camera enemyCam;
    private static Vector<Camera> enemyCams;
    
    
    private static Vector<Camera> weaponCams;
    
    private boolean wxleft, wzup, wxright, wzdown;
    
    
    static double angle;
	private float[] distance=new float[50];
	private float dist=0;
	private final double PI = 3.14159265358979323846;

	private static int tick;
	

	
    private void render() {
    	
    	 glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
         
         
         glLoadIdentity();
              
         moveDude();
        
         GL11.glScalef(.1f, .1f, .1f);//scale dude
         
        
         renderDude();
         
         
         for(Camera w:weaponCams){
        	 glLoadIdentity();       	 
        	 moveWeapon((EulerCamera)w);
        	 GL11.glScalef(.1f, .1f, .1f);//scale dude
             renderWeapon();       	 
         }
         for(Camera w:enemyCams){
        	 glLoadIdentity();       	 
        	 moveEnemies((EulerCamera)w);
        	 GL11.glScalef(.1f, .1f, .1f);//scale dude
             renderEnemies();       	 
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
    	glCallList(playerDisplayList);      	
    }
    
    private void moveWeapon(EulerCamera cam){
    	
    	if(wxright && cam.tick > 0)cam.setPosition(cam.x()-1, cam.y(), cam.z());
    	if(wxleft&& cam.tick > 0)cam.setPosition(cam.x()+1, cam.y(), cam.z());    	
    	if(wzup&& cam.tick > 0)cam.setPosition(cam.x(), cam.y(), cam.z()+1);
    	if(wzdown&& cam.tick > 0)cam.setPosition(cam.x(), cam.y(), cam.z()-1);
    		
    	
    	
    	
    	if((!wxright && !wxleft && !wzup && !wzdown))cam.setPosition(99f,99f,99f);
    	
    	
    	
    	cam.applyTranslations();
    	
    	
    	
    	
    	
    }
    
    
 private void renderEnemies(){    	     	
    	GL11.glColor3f(0.0f, 1.2f, 0.0f);       	
    	glCallList(playerDisplayList);     	
    }    
 
 private void enemyGenerator(){
	 
	 
	 
	 enemyCams.add(setUpCameraEnemy(200f, 300f));
	 enemyCams.add(setUpCameraEnemy(40f, 33f));
	 enemyCams.add(setUpCameraEnemy(-50f, 30f));
	 enemyCams.add(setUpCameraEnemy(20f, -30f));
	 
 }
 
    private void moveEnemies(Camera cam){   
    	
    	float playerx=playerCam.x();
    	float playerz=playerCam.z();
    	
    	
    	float enemyx=cam.x();
    	float enemyz=cam.z();
    	
    	if(playerx > enemyx)enemyx+=.05f;
    	else enemyx-=.051f;
    	
    	if(playerz > enemyz)enemyz+=.05f;
    	else enemyz-=.05f;
    	
    	
    	cam.setPosition(enemyx, cam.y(), enemyz);
    	
    	
    	cam.applyTranslations();
    }
    
    
    
    private void weaponTick(EulerCamera cam){
    	if(cam.tick <=0){    		
    		wzup=false;
            wzdown=false;
            wxleft=false;
            wxright=false;   		
    		cam.tick=0;
    	}else{
    		
    		cam.tick--;
    		
    	}
    	
    	
    }
    
   
    
    private static void logic() {
        // Add logic code here
    }

    private void input(int delta) {
    	  x=playerCam.x();
          z=playerCam.z();
          
          
         for(Camera w:weaponCams)weaponTick((EulerCamera)w);
          
          
          
          
          if (Keyboard.isKeyDown(Keyboard.KEY_UP) ){
        	  wzup=true;
        	  EulerCamera w;
        	  w= (EulerCamera) makeWeaponCam();
        	  w.tick=40;
        	  weaponCams.add(w);}
          if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
        	  wxleft=true; 
        	  EulerCamera w;
        	  w= (EulerCamera) makeWeaponCam();
        	  w.tick=40;
        	  weaponCams.add(w);}
          if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
        	  wxright=true;
        	  EulerCamera w;
        	  w= (EulerCamera) makeWeaponCam();
        	  w.tick=40;
        	  weaponCams.add(w);}
          if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
        	  wzdown=true;
        	  EulerCamera w;
        	  w= (EulerCamera) makeWeaponCam();
        	  w.tick=40;
        	  weaponCams.add(w);}
          
          
       
    	if (Keyboard.isKeyDown(Keyboard.KEY_A))x += 0.05f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_D))x -= 0.05f * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))z += 0.05f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_S))z -= 0.05f * delta;
	
		
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

    
    private static void setUpStates() {    
    	angle=0;
    	tick=0;
    	weaponCams=new Vector<Camera>();
    	enemyCams=new Vector<Camera>();
        //        glEnable(GL_DEPTH_TEST);
        //        glEnable(GL_LIGHTING);
        //        glEnable(GL_BLEND);
        //        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void update() {
    	updateFPS(); // update FPS Counter
        Display.update();
        Display.sync(60);    
        
    }

    private void enterGameLoop() {
    	
    	//initGL(); // init OpenGL
		getTimeElapsed(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer
		
        while (!Display.isCloseRequested()) {
        	int delta = getTimeElapsed();    	
        	logic();
        	render();
            input(delta);
            update();
          
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
    

    public static void main(String[] args) {
    	GameEngine engine = new GameEngine();
    	
    	
    	
        engine.setUpDisplay();
        engine.setUpDisplayListPlayer();
        engine.setUpStates();
        engine.setUpMatrices();
        engine.setUpCameraPlayer();
        
        engine.enemyGenerator();
        engine.enterGameLoop();
        engine.cleanUp(false);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
   
    
    
    
    
    
    
    
    
    
    
}
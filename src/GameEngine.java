
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
	float x, z, d;//position of player	
	int fps;//frames per second
	long lastFPS;//fps at last frame
	private static int playerDisplayList;
    private static String playerPath="obj/malefromabove.obj";
    private static Camera camera;
    static double angle;
	private float[] distance=new float[50];
	private float dist=0;
	private final double PI = 3.14159265358979323846;

	
	
	// Moving variables
		private int projectionMatrixLocation = 0;
		private int viewMatrixLocation = 0;
		private int modelMatrixLocation = 0;
		private Matrix4f projectionMatrix = null;
		private Matrix4f viewMatrix = null;
		private Matrix4f modelMatrix = null;
		private Vector3f modelPos = null;
		private Vector3f modelAngle = null;
		private Vector3f modelScale = null;
		private Vector3f cameraPos = null;
		private FloatBuffer matrix44Buffer = null;
	
	
	
	
    private void render() {
    	
    	 glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
         GL11.glColor3f(0.5f,0.5f,1.0f);
         GL11.glShadeModel(GL11.GL_SMOOTH);
         glLoadIdentity();
      
         
         camera.setPosition(x,camera.y(),z);
         camera.applyTranslations();
       //  glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
         
         
         GL11.glScalef(.2f, .2f, .2f);
         GL11.glRotated(angle, 0, 1, 0);
         renderDude();
         moveEnemies();
         renderEnemies();
         
    }

    
    private void renderDude(){    
    	
    	
    	
    	glCallList(playerDisplayList);  
    	
    }
    
    
    
    
    private void initializeEnemies(){
    	
    	for(int f=0;f<distance.length;f++){    		
    		distance[f]=600;   		
    	}
    	
    }
    
    
    
    
   
    
    private static void logic() {
        // Add logic code here
    }

    private void input(int delta) {
    	  x=camera.x();
          z=camera.z();
    	
       
    	if (Keyboard.isKeyDown(Keyboard.KEY_A))x += 0.05f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_D))x -= 0.05f * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))z += 0.05f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_S))z -= 0.05f * delta;
	
		
		if(Keyboard.isKeyDown(Keyboard.KEY_1))System.out.println("Camera Angle:"+camera.pitch() +" "+camera.yaw() + " "+camera.roll());
		if(Keyboard.isKeyDown(Keyboard.KEY_2))System.out.println("Camera Position:"+camera.x()	+" "+camera.y() + " "+camera.z());	
		if(Keyboard.isKeyDown(Keyboard.KEY_3))System.out.println("Player Position:"+camera.x()	+" "+camera.y() + " "+camera.z());	
		
		angle=getAngleBetween(x,z,Mouse.getX(), Mouse.getY());
		
		//if(Mouse.isButtonDown(0))d++;
		//if(Mouse.isButtonDown(1))d--;	
		// camera.processMouse(1, 80, -80);
	       // camera.processKeyboard(getTimeElapsed()+1, 50, 50, 50);
	     
			if (Mouse.isButtonDown(0)) {
	            Mouse.setGrabbed(true);
	        } else if (Mouse.isButtonDown(1)) {
	            Mouse.setGrabbed(false);
	        }
	     
    }

    private  void cleanUp(boolean asCrash) {
    	glDeleteLists(playerDisplayList, 1);
        Display.destroy();
        System.exit(asCrash ? 1 : 0);
    }

    
    private  void setUpCamera() {
        camera = new EulerCamera.Builder().setAspectRatio((float) Display.getWidth() / Display.getHeight())
                .setRotation(-1.12f, 0.16f, 0f).setPosition(-1.38f, 1.36f, 7.95f).setFieldOfView(60).build();
        camera.applyOptimalStates();
        camera.applyPerspectiveMatrix();
        camera.setPosition(0.f, 30f, 0.f);
        camera.setRotation(100f,0.16057983f, 0);  //80.0 0.16057983 0.0: pitch, yaw, roll:we want to be looking down
      
       // camera.applyPerspectiveMatrix();
       // camera.setFieldOfView(120f);
    }
    
    
    private void setUpMatrices() {
    	// Setup projection matrix
    			projectionMatrix = new Matrix4f();
    			float fieldOfView = 60f;
    			float aspectRatio = (float)WINDOW_DIMENSIONS[0] / (float)WINDOW_DIMENSIONS[1];
    			float near_plane = 0.1f;
    			float far_plane = 100f;
    			
    			float y_scale = this.coTangent(this.degreesToRadians(fieldOfView / 2f));
    			float x_scale = y_scale / aspectRatio;
    			float frustum_length = far_plane - near_plane;
    			
    			projectionMatrix.m00 = x_scale;
    			projectionMatrix.m11 = y_scale;
    			projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
    			projectionMatrix.m23 = -1;
    			projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
    			
    			// Setup view matrix
    			viewMatrix = new Matrix4f();
    			
    			// Setup model matrix
    			modelMatrix = new Matrix4f();
    			
    			// Create a FloatBuffer with the proper size to store our matrices later
    			matrix44Buffer = BufferUtils.createFloatBuffer(16);
    }

    private static void setUpStates() {    
    	angle=0;
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
        engine.setUpCamera();
        engine.enterGameLoop();
        engine.cleanUp(false);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void renderEnemies(){
	 	
    	tickDistance(); 
    	
    	
    	GL11.glColor3f(0.0f, 1.2f, 0.0f);    	
    	//for(int f=0;f<distance.length;f++){  
    		
    	GL11.glTranslatef(dist, 0, dist);   	
    	glCallList(playerDisplayList);    
    
    	
    }
    
    
    
    private void tickDistance(){
    	//for(float f:distance)   	
    	if(dist<=0)dist=(float)Math.random()*10*100;  
    	else dist--;   	
    }
    
    private void moveEnemies(){
    	GL11.glTranslatef(d, 0, d);
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
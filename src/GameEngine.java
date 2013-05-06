
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 * Top down shooter, of zombies.
 *
 * @author Mike Stevens & Kevin Rizza
 */
public class GameEngine {

    private static final String WINDOW_TITLE = "ZombiesAteMyHomework";
    private static final int[] WINDOW_DIMENSIONS = {800, 600};
    long lastFrame;//time at last frame        
	float x = 400, y = 300;//position of player	
	int fps;//frames per second
	long lastFPS;//fps at last frame
    
    
    private void render() {
    	// Clear The Screen And The Depth Buffer
    			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

    			// R,G,B,A Set The Color To Blue One Time Only
    			GL11.glColor3f(0.5f, 0.5f, 1.0f);

    			// draw player
    			GL11.glPushMatrix();
    				GL11.glTranslatef(x, y, 0);
    				GL11.glTranslatef(-x, -y, 0);    				
    				GL11.glBegin(GL11.GL_QUADS);
    					GL11.glVertex2f(x - 50, y - 50);
    					GL11.glVertex2f(x + 50, y - 50);
    					GL11.glVertex2f(x + 50, y + 50);
    					GL11.glVertex2f(x - 50, y + 50);
    				GL11.glEnd();
    			GL11.glPopMatrix();  
    }

    private static void logic() {
        // Add logic code here
    }

    private void input(int delta) {
       
    	if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) x -= 0.35f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) x += 0.35f * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) y += 0.35f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) y -= 0.35f * delta;
		
		if (x < 0) x = 0;
		if (x > 800) x = 800;
		if (y < 0) y = 0;
		if (y > 600) y = 600;    	
    }

    private static void cleanUp(boolean asCrash) {
        // Add cleaning code here
        Display.destroy();
        System.exit(asCrash ? 1 : 0);
    }

    private static void setUpMatrices() {
        // Add code for the initialization of the projection matrix here
    }

    private static void setUpStates() {    	
    
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
    	
    	initGL(); // init OpenGL
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

    private static void setUpDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
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
    
    
    
    
    

    public static void main(String[] args) {
        setUpDisplay();
        setUpStates();
        setUpMatrices();
        GameEngine engine=new GameEngine();
        engine.enterGameLoop();
        cleanUp(false);
    }
}